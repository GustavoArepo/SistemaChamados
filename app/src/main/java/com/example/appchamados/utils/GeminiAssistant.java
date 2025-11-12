package com.example.appchamados.utils;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GeminiAssistant {
    private static final String TAG = "GeminiAssistant";

    // ✅ SUA API KEY DO GEMINI
    private static final String API_KEY = "AIzaSyCpwVguJY4HKTPxzpwsd6cU01ca6AjDE5Y";

    // ✅ ENDPOINT DO GEMINI 2.0 FLASH
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    private Context context;

    public GeminiAssistant(Context context) {
        this.context = context;
    }

    // ✅ INTERFACE DE CALLBACK
    public interface AICallback {
        void onSuccess(String resposta);
        void onError(String erro);
    }

    // ✅ VERIFICAR SE ESTÁ DISPONÍVEL
    public boolean estaDisponivel() {
        return API_KEY != null && !API_KEY.isEmpty() && !API_KEY.equals("COLE_A_KEY_AQUI");
    }

    // ✅ OBTER NOME DO MODELO
    public String getModeloAtual() {
        return "Gemini 2.0 Flash";
    }

    // ✅ MÉTODO PRINCIPAL PARA FAZER PERGUNTAS
    public void perguntar(String pergunta, AICallback callback) {
        new Thread(() -> {
            try {
                Log.d(TAG, "Enviando pergunta para Gemini: " + pergunta);

                // ✅ MONTAR JSON DA REQUISIÇÃO
                JSONObject requestBody = new JSONObject();
                JSONArray contents = new JSONArray();
                JSONObject content = new JSONObject();
                JSONArray parts = new JSONArray();
                JSONObject part = new JSONObject();

                part.put("text", pergunta);
                parts.put(part);
                content.put("parts", parts);
                contents.put(content);
                requestBody.put("contents", contents);

                // ✅ CONFIGURAR REQUISIÇÃO HTTP
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("X-Goog-Api-Key", API_KEY);
                connection.setDoOutput(true);
                connection.setConnectTimeout(30000);
                connection.setReadTimeout(30000);

                // ✅ ENVIAR DADOS
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                // ✅ LER RESPOSTA
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Código de resposta: " + responseCode);

                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // ✅ PARSEAR RESPOSTA JSON
                    String resposta = parseGeminiResponse(response.toString());

                    if (resposta != null && !resposta.isEmpty()) {
                        Log.d(TAG, "✅ Resposta do Gemini recebida");
                        callback.onSuccess(resposta);
                    } else {
                        callback.onError("Resposta vazia do Gemini");
                    }
                } else {
                    // ✅ LER ERRO
                    BufferedReader errorReader = new BufferedReader(
                            new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    errorReader.close();

                    String errorMsg = "Erro HTTP " + responseCode + ": " + errorResponse.toString();
                    Log.e(TAG, errorMsg);
                    callback.onError(errorMsg);
                }

                connection.disconnect();

            } catch (Exception e) {
                String errorMsg = "Erro ao comunicar com Gemini: " + e.getMessage();
                Log.e(TAG, errorMsg, e);
                callback.onError(errorMsg);
            }
        }).start();
    }

    // ✅ PARSEAR RESPOSTA DO GEMINI
    private String parseGeminiResponse(String jsonResponse) {
        try {
            JSONObject root = new JSONObject(jsonResponse);

            if (root.has("candidates")) {
                JSONArray candidates = root.getJSONArray("candidates");

                if (candidates.length() > 0) {
                    JSONObject candidate = candidates.getJSONObject(0);
                    JSONObject content = candidate.getJSONObject("content");
                    JSONArray parts = content.getJSONArray("parts");

                    if (parts.length() > 0) {
                        JSONObject part = parts.getJSONObject(0);
                        return part.getString("text");
                    }
                }
            }

            Log.e(TAG, "Formato de resposta inesperado");
            return "Erro ao processar resposta do Gemini";

        } catch (Exception e) {
            Log.e(TAG, "Erro ao parsear resposta: " + e.getMessage());
            return "Erro ao interpretar resposta: " + e.getMessage();
        }
    }

    // ✅ DIAGNÓSTICO PARA DEBUG
    public void diagnostico() {
        Log.d(TAG, "=== DIAGNÓSTICO GEMINI ===");
        Log.d(TAG, "API Key configurada: " + (API_KEY != null && !API_KEY.isEmpty()));
        Log.d(TAG, "Disponível: " + estaDisponivel());
        Log.d(TAG, "Modelo: " + getModeloAtual());
    }
}