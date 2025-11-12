package com.example.appchamados.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchamados.R;
import com.example.appchamados.adapters.HistoricoAdapter;
import com.example.appchamados.adapters.ImagensAdapter;
import com.example.appchamados.models.Chamado;
import com.example.appchamados.models.ChamadoDetail;
import com.example.appchamados.models.ChamadoDetailResponse;
import com.example.appchamados.models.ChamadoImagem;
import com.example.appchamados.models.ImagemResponse;
import com.example.appchamados.models.ImagensResponse;
import com.example.appchamados.models.RegisterResponse;
import com.example.appchamados.network.ApiClient;
import com.example.appchamados.network.ApiService;
import com.example.appchamados.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.app.AlertDialog;
import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import android.database.Cursor;
import android.provider.OpenableColumns;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class ChamadoDetailActivity extends AppCompatActivity {

    private TextView tvTitulo, tvDescricao, tvStatus, tvDataCriacao, tvUsuario;
    private ProgressBar progressBar;
    private RecyclerView rvHistorico;
    private Button btnAlterarStatus;
    private Spinner spinnerStatus;
    private View layoutSemHistorico;

    private int chamadoId;
    private SessionManager session;
    private ChamadoDetail chamadoAtual;

    private static final String TAG = "ChamadoDetail";

    private Button btnAbrirChat;

    private RecyclerView rvImagens;
    private View layoutSemImagens;
    private Button btnAdicionarImagem;
    private View cardImagens;

    private ImagensAdapter imagensAdapter;
    private List<ChamadoImagem> listaImagens = new ArrayList<>();

    private static final int REQUEST_CODE_GALERIA = 1001;
    private static final int REQUEST_CODE_CAMERA = 1002;
    private static final int REQUEST_PERMISSOES = 1003;

    private Uri imagemSelecionadaUri;

    private void abrirChat() {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("CHAMADO_ID", chamadoId);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chamado_detail);

        session = new SessionManager(this);
        chamadoId = getIntent().getIntExtra("CHAMADO_ID", -1);

        Log.d(TAG, "onCreate - Chamado ID recebido: " + chamadoId);

        if (chamadoId == -1) {
            Log.e(TAG, "Chamado ID inválido");
            Toast.makeText(this, "Chamado não encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupStatusSpinner();
        carregarDetalhesChamado();
        setupStatusSpinner();
        setupImagensRecyclerView(); // ✅ NOVO
        carregarDetalhesChamado();
        carregarImagens(); // ✅ NOVO
    }

    private void initViews() {
        tvTitulo = findViewById(R.id.tvTitulo);
        tvDescricao = findViewById(R.id.tvDescricao);
        tvStatus = findViewById(R.id.tvStatus);
        tvDataCriacao = findViewById(R.id.tvDataCriacao);
        tvUsuario = findViewById(R.id.tvUsuario);
        progressBar = findViewById(R.id.progressBar);
        rvHistorico = findViewById(R.id.rvHistorico);
        btnAlterarStatus = findViewById(R.id.btnAlterarStatus);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        layoutSemHistorico = findViewById(R.id.layoutSemHistorico);

        // ✅ BOTÃO DO CHAT
        btnAbrirChat = findViewById(R.id.btnAbrirChat);

        rvHistorico.setLayoutManager(new LinearLayoutManager(this));

        btnAlterarStatus.setOnClickListener(v -> alterarStatus());
        btnAbrirChat.setOnClickListener(v -> abrirChat()); // ✅ CLICK LISTENER
        // ✅ NOVAS VIEWS PARA IMAGENS
        rvImagens = findViewById(R.id.rvImagens);
        layoutSemImagens = findViewById(R.id.layoutSemImagens);
        btnAdicionarImagem = findViewById(R.id.btnAdicionarImagem);
        cardImagens = findViewById(R.id.cardImagens);

        rvHistorico.setLayoutManager(new LinearLayoutManager(this));

        btnAlterarStatus.setOnClickListener(v -> alterarStatus());
        btnAbrirChat.setOnClickListener(v -> abrirChat());
        btnAdicionarImagem.setOnClickListener(v -> adicionarImagem()); // ✅ NOVO
    }

    private void setupStatusSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.status_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);
    }

    private void setupImagensRecyclerView() {
        // ✅ LAYOUT HORIZONTAL PARA IMAGENS
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvImagens.setLayoutManager(layoutManager);

        imagensAdapter = new ImagensAdapter(listaImagens, new ImagensAdapter.OnImagemClickListener() {
            @Override
            public void onImagemClick(ChamadoImagem imagem) {
                // ✅ ABRIR IMAGEM EM TELA CHEIA (implementaremos depois)
                abrirImagemFullscreen(imagem);
            }

            @Override
            public void onDeletarClick(ChamadoImagem imagem) {
                // ✅ CONFIRMAR EXCLUSÃO
                confirmarDelecaoImagem(imagem);
            }
        });

        rvImagens.setAdapter(imagensAdapter);
    }

    private void adicionarImagem() {
        mostrarDialogSelecaoImagem();
    }


    private void carregarImagens() {
        Log.d(TAG, "Carregando imagens do chamado: " + chamadoId);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ImagensResponse> call = apiService.getImagensPorChamado(chamadoId);

        call.enqueue(new Callback<ImagensResponse>() {
            @Override
            public void onResponse(Call<ImagensResponse> call, Response<ImagensResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ImagensResponse imagensResponse = response.body();

                    if (imagensResponse.isSuccess()) {
                        listaImagens = imagensResponse.getImagens();
                        imagensAdapter.atualizarLista(listaImagens);
                        verificarListaImagensVazia();
                        Log.d(TAG, "Imagens carregadas: " + listaImagens.size());
                    } else {
                        Log.e(TAG, "Erro ao carregar imagens: " + imagensResponse.getMessage());
                    }
                } else {
                    Log.e(TAG, "Erro HTTP ao carregar imagens: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ImagensResponse> call, Throwable t) {
                Log.e(TAG, "Falha ao carregar imagens: " + t.getMessage());
            }
        });
    }

    private void verificarListaImagensVazia() {
        if (listaImagens.isEmpty()) {
            rvImagens.setVisibility(View.GONE);
            layoutSemImagens.setVisibility(View.VISIBLE);
        } else {
            rvImagens.setVisibility(View.VISIBLE);
            layoutSemImagens.setVisibility(View.GONE);
        }
    }


    private void abrirImagemFullscreen(ChamadoImagem imagem) {
        // ✅ PASSAR APENAS OS DADOS NECESSÁRIOS
        Intent intent = new Intent(this, FullscreenImageActivity.class);
        intent.putExtra("IMAGEM_URL", imagem.getUrlCompleta());
        intent.putExtra("IMAGEM_NOME", imagem.getNomeArquivo());
        startActivity(intent);
    }

    private void confirmarDelecaoImagem(ChamadoImagem imagem) {
        new AlertDialog.Builder(this)
                .setTitle("Deletar Imagem")
                .setMessage("Tem certeza que deseja deletar a imagem \"" + imagem.getNomeArquivo() + "\"?")
                .setPositiveButton("Deletar", (dialog, which) -> deletarImagem(imagem))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deletarImagem(ChamadoImagem imagem) {
        Log.d(TAG, "Deletando imagem ID: " + imagem.getId());

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<RegisterResponse> call = apiService.deleteImagem(imagem.getId());

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(ChamadoDetailActivity.this, "Imagem deletada com sucesso", Toast.LENGTH_SHORT).show();
                        carregarImagens(); // Recarregar lista
                    } else {
                        Toast.makeText(ChamadoDetailActivity.this, "Erro: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChamadoDetailActivity.this, "Erro ao deletar imagem", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(ChamadoDetailActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carregarDetalhesChamado() {
        Log.d(TAG, "carregarDetalhesChamado - Iniciando... ID: " + chamadoId);
        showLoading(true);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ChamadoDetailResponse> call = apiService.getChamadoDetail(chamadoId);

        call.enqueue(new Callback<ChamadoDetailResponse>() {
            @Override
            public void onResponse(Call<ChamadoDetailResponse> call, Response<ChamadoDetailResponse> response) {
                Log.d(TAG, "onResponse - Código: " + response.code() + ", Sucesso: " + response.isSuccessful());
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ChamadoDetailResponse detailResponse = response.body();
                    Log.d(TAG, "Resposta - Success: " + detailResponse.isSuccess() + ", Message: " + detailResponse.getMessage());

                    if (detailResponse.isSuccess()) {
                        Log.d(TAG, "Chamado carregado com sucesso");
                        exibirDadosChamado(detailResponse);
                    } else {
                        Log.e(TAG, "Erro na resposta: " + detailResponse.getMessage());
                        Toast.makeText(ChamadoDetailActivity.this,
                                "Erro: " + detailResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Resposta não sucedida ou body nulo");
                    Toast.makeText(ChamadoDetailActivity.this,
                            "Erro ao carregar detalhes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChamadoDetailResponse> call, Throwable t) {
                Log.e(TAG, "onFailure - Erro: " + t.getMessage(), t);
                showLoading(false);
                Toast.makeText(ChamadoDetailActivity.this,
                        "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    } // ✅ FALTAVA ESTA CHAVE DE FECHAMENTO!

    private void exibirDadosChamado(ChamadoDetailResponse response) {
        chamadoAtual = response.getChamado();
        Log.d(TAG, "exibirDadosChamado - Chamado: " + (chamadoAtual != null ? chamadoAtual.getTitulo() : "NULL"));

        if (chamadoAtual == null) {
            Log.e(TAG, "chamadoAtual é NULL!");
            Toast.makeText(this, "Erro: Dados do chamado não disponíveis", Toast.LENGTH_SHORT).show();
            return;
        }

        // Dados básicos
        tvTitulo.setText(chamadoAtual.getTitulo());
        tvDescricao.setText(chamadoAtual.getDescricao());
        tvStatus.setText(chamadoAtual.getStatus());
        tvDataCriacao.setText("Criado em: " + formatarData(chamadoAtual.getCriadoEm()));
        tvUsuario.setText("ID Usuário: " + chamadoAtual.getUsuarioId());

        // Histórico - como não temos na API, mostramos mensagem
        mostrarEstadoSemHistorico();

        // Configurar spinner com status atual
        String statusAtual = chamadoAtual.getStatus();
        for (int i = 0; i < spinnerStatus.getCount(); i++) {
            if (spinnerStatus.getItemAtPosition(i).toString().equalsIgnoreCase(statusAtual)) {
                spinnerStatus.setSelection(i);
                break;
            }
        }
    }

    private void mostrarEstadoSemHistorico() {
        rvHistorico.setVisibility(View.GONE);
        layoutSemHistorico.setVisibility(View.VISIBLE);
    }

    private void alterarStatus() {
        Log.d(TAG, "alterarStatus - chamadoAtual: " + (chamadoAtual != null ? "NÃO NULL" : "NULL"));

        if (chamadoAtual == null) {
            Log.e(TAG, "Tentativa de alterar status com chamadoAtual NULL");
            Toast.makeText(this, "Dados do chamado não carregados", Toast.LENGTH_SHORT).show();
            return;
        }

        String novoStatus = spinnerStatus.getSelectedItem().toString();
        Log.d(TAG, "alterarStatus - Novo status: " + novoStatus);

        // Criar objeto Chamado para atualização
        Chamado chamadoAtualizado = new Chamado();
        chamadoAtualizado.setTitulo(chamadoAtual.getTitulo());
        chamadoAtualizado.setDescricao(chamadoAtual.getDescricao());
        chamadoAtualizado.setUsuarioId(chamadoAtual.getUsuarioId());
        chamadoAtualizado.setStatus(novoStatus);

        showLoading(true);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<RegisterResponse> call = apiService.atualizarChamado(chamadoId, chamadoAtualizado);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                showLoading(false);
                Log.d(TAG, "atualizarStatus - Resposta: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(ChamadoDetailActivity.this,
                                "Status atualizado!", Toast.LENGTH_SHORT).show();
                        carregarDetalhesChamado(); // Recarregar dados
                    } else {
                        Toast.makeText(ChamadoDetailActivity.this,
                                "Erro: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChamadoDetailActivity.this,
                            "Erro ao atualizar status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "atualizarStatus - Falha: " + t.getMessage());
                Toast.makeText(ChamadoDetailActivity.this,
                        "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatarData(String data) {
        try {
            return data.substring(0, 10); // Formato simples YYYY-MM-DD
        } catch (Exception e) {
            return data;
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void mostrarDialogSelecaoImagem() {
        String[] opcoes = {"Galeria", "Câmera", "Cancelar"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecionar Imagem");
        builder.setItems(opcoes, (dialog, which) -> {
            switch (which) {
                case 0: // Galeria
                    abrirGaleria();
                    break;
                case 1: // Câmera
                    abrirCamera();
                    break;
                case 2: // Cancelar
                    dialog.dismiss();
                    break;
            }
        });
        builder.show();
    }

    private void abrirGaleria() {
        // ✅ VERIFICAR PERMISSÕES
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSOES);
            return;
        }

        // ✅ ABRIR GALERIA
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_GALERIA);
    }

    private void abrirCamera() {
        // ✅ IMPLEMENTAÇÃO DA CÂMERA (OPCIONAL POR ENQUANTO)
        Toast.makeText(this, "Funcionalidade de câmera em desenvolvimento", Toast.LENGTH_SHORT).show();
    }

    // ✅ TRATAR RESULTADO DA SELEÇÃO
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_GALERIA && data != null) {
                imagemSelecionadaUri = data.getData();
                if (imagemSelecionadaUri != null) {
                    fazerUploadImagem(imagemSelecionadaUri);
                }
            }
        }
    }

    // ✅ TRATAR PERMISSÕES
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSOES) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria();
            } else {
                Toast.makeText(this, "Permissão necessária para acessar a galeria", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fazerUploadImagem(Uri imagemUri) {
        try {
            // ✅ OBTER ARQUIVO DA URI
            InputStream inputStream = getContentResolver().openInputStream(imagemUri);
            if (inputStream == null) {
                Toast.makeText(this, "Erro ao acessar a imagem", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ CRIAR ARQUIVO TEMPORÁRIO
            File file = criarArquivoTemporario(imagemUri);
            if (file == null) {
                Toast.makeText(this, "Erro ao preparar imagem", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ PREPARAR REQUEST BODY
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("arquivo", file.getName(), requestFile);

            RequestBody chamadoIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(chamadoId));

            // ✅ MOSTRAR LOADING
            showLoading(true);

            // ✅ FAZER UPLOAD
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<ImagemResponse> call = apiService.uploadImagem(chamadoIdBody, body);

            call.enqueue(new Callback<ImagemResponse>() {
                @Override
                public void onResponse(Call<ImagemResponse> call, Response<ImagemResponse> response) {
                    showLoading(false);

                    if (response.isSuccessful() && response.body() != null) {
                        ImagemResponse imagemResponse = response.body();

                        if (imagemResponse.isSuccess()) {
                            Toast.makeText(ChamadoDetailActivity.this, "Imagem enviada com sucesso!", Toast.LENGTH_SHORT).show();
                            carregarImagens(); // Recarregar lista
                        } else {
                            Toast.makeText(ChamadoDetailActivity.this, "Erro: " + imagemResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ChamadoDetailActivity.this, "Erro no upload", Toast.LENGTH_SHORT).show();
                    }

                    // ✅ LIMPAR ARQUIVO TEMPORÁRIO
                    if (file.exists()) {
                        file.delete();
                    }
                }

                @Override
                public void onFailure(Call<ImagemResponse> call, Throwable t) {
                    showLoading(false);
                    Toast.makeText(ChamadoDetailActivity.this, "Falha na conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                    // ✅ LIMPAR ARQUIVO TEMPORÁRIO
                    if (file.exists()) {
                        file.delete();
                    }
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // ✅ MÉTODO AUXILIAR PARA CRIAR ARQUIVO TEMPORÁRIO
    private File criarArquivoTemporario(Uri uri) {
        try {
            // ✅ OBTER NOME DO ARQUIVO ORIGINAL
            String nomeArquivo = obterNomeArquivo(uri);
            if (nomeArquivo == null) {
                nomeArquivo = "imagem_" + System.currentTimeMillis() + ".jpg";
            }

            // ✅ CRIAR ARQUIVO TEMPORÁRIO
            File tempFile = new File(getCacheDir(), nomeArquivo);

            // ✅ COPIAR CONTEÚDO
            InputStream inputStream = getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            return tempFile;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ✅ MÉTODO AUXILIAR PARA OBTER NOME DO ARQUIVO
    private String obterNomeArquivo(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}


