package com.example.appchamados.network;

import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectionTester {

    private static final String TAG = "CONNECTION_TESTER";

    public static void testarConexaoAPI() {
        new Thread(() -> {
            try {
                Log.d(TAG, "=== INICIANDO TESTE DE CONEXÃO COM API ===");

                URL url = new URL("http://10.0.2.2:5257/api/auth/login");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                Log.d(TAG, "URL: " + url.toString());
                Log.d(TAG, "Conectando...");

                // Dados de teste - USE AS MESMAS CREDENCIAIS DO APP
                String jsonInputString = "{\"email\": \"admin@email.com\", \"senha\": \"12345678\"}";
                Log.d(TAG, "Enviando dados: " + jsonInputString);

                // Enviar dados
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                    Log.d(TAG, "Dados enviados para API");
                }

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Código de resposta HTTP: " + responseCode);

                // Ler resposta
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    Log.d(TAG, "✅ CONEXÃO BEM-SUCEDIDA!");
                    Log.d(TAG, "Resposta completa: " + response.toString());

                    // Análise rápida da resposta
                    if (response.toString().contains("\"success\":true")) {
                        Log.d(TAG, "🎉 LOGIN TESTE FUNCIONOU!");
                    } else {
                        Log.w(TAG, "⚠️ API respondeu mas login falhou");
                    }

                } else {
                    Log.e(TAG, "❌ Erro HTTP: " + responseCode);

                    // Tentar ler mensagem de erro se houver
                    try {
                        BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                        String errorLine;
                        StringBuilder errorResponse = new StringBuilder();

                        while ((errorLine = errorReader.readLine()) != null) {
                            errorResponse.append(errorLine);
                        }
                        errorReader.close();

                        Log.e(TAG, "Mensagem de erro: " + errorResponse.toString());
                    } catch (Exception e) {
                        Log.e(TAG, "Não foi possível ler mensagem de erro: " + e.getMessage());
                    }
                }

                connection.disconnect();
                Log.d(TAG, "=== FIM DO TESTE DE CONEXÃO ===");

            } catch (Exception e) {
                Log.e(TAG, "❌ ERRO DE CONEXÃO: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    public static void testarConexaoSimples() {
        new Thread(() -> {
            try {
                Log.d(TAG, "=== TESTE SIMPLES DE CONEXÃO ===");

                URL url = new URL("http://10.0.2.2:5257");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                Log.d(TAG, "Testando URL: " + url.toString());

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Código de resposta: " + responseCode);

                if (responseCode == 200) {
                    Log.d(TAG, "✅ API ACESSÍVEL - Endpoint raiz responde!");
                } else if (responseCode == 404) {
                    Log.w(TAG, "⚠️ API ACESSÍVEL mas endpoint raiz não existe (404)");
                    Log.w(TAG, "Isso é NORMAL - significa que a API está rodando!");
                } else {
                    Log.e(TAG, "❌ API respondeu com código inesperado: " + responseCode);
                }

                connection.disconnect();
                Log.d(TAG, "=== FIM DO TESTE SIMPLES ===");

            } catch (Exception e) {
                Log.e(TAG, "❌ NÃO CONSEGUIU ACESSAR API: " + e.getMessage());
            }
        }).start();
    }

    public static void testarEndpointEspecifico(String endpoint) {
        new Thread(() -> {
            try {
                Log.d(TAG, "=== TESTE ENDPOINT ESPECÍFICO: " + endpoint + " ===");

                URL url = new URL("http://10.0.2.2:5257" + endpoint);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Endpoint: " + endpoint + " - Código: " + responseCode);

                if (responseCode == 200) {
                    Log.d(TAG, "✅ Endpoint " + endpoint + " FUNCIONANDO!");

                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    Log.d(TAG, "Resposta: " + response.toString());

                } else {
                    Log.e(TAG, "❌ Endpoint " + endpoint + " FALHOU: " + responseCode);
                }

                connection.disconnect();

            } catch (Exception e) {
                Log.e(TAG, "❌ Erro no endpoint " + endpoint + ": " + e.getMessage());
            }
        }).start();
    }
}