package com.example.appchamados.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.appchamados.activities.LoginActivity;

public class SessionManager {
    private static final String PREF_NAME = "AppChamadosSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_AUTH_TOKEN = "authToken"; // ✅ NOVA CHAVE PARA O TOKEN

    private static final String KEY_FCM_TOKEN = "fcm_token";


    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Criar sessão de login COM TOKEN
     */
    public void createLoginSession(int userId, String name, String email, String authToken) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_AUTH_TOKEN, authToken); // ✅ SALVAR TOKEN
        editor.commit();
    }

    /**
     * ✅ NOVO MÉTODO: Obter token de autenticação
     */
    public String getAuthToken() {
        return pref.getString(KEY_AUTH_TOKEN, null);
    }

    /**
     * Verificar se usuário está logado
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * ✅ SALVAR TOKEN FCM LOCALMENTE
     */
    public void saveFCMToken(String token) {
        editor.putString(KEY_FCM_TOKEN, token);
        editor.commit();
        Log.d("SESSION_MANAGER", "FCM Token salvo localmente: " + token);
    }

    /**
     * ✅ OBTER TOKEN FCM
     */
    public String getFCMToken() {
        return pref.getString(KEY_FCM_TOKEN, null);
    }

    /**
     * ✅ VERIFICAR SE TEM TOKEN FCM
     */
    public boolean hasFCMToken() {
        return getFCMToken() != null;
    }


    /**
     * Obter dados do usuário logado
     */
    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "");
    }

    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, "");
    }

    /**
     * Fazer logout
     */
    public void logoutUser() {
        editor.clear();
        editor.commit();

        // Redirecionar para LoginActivity
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Verificar sessão e redirecionar se necessário
     */
    public void checkLogin() {
        if (!isLoggedIn()) {
            logoutUser();
        }
    }
}