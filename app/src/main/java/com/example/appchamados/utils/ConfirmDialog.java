package com.example.appchamados.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ConfirmDialog {

    public interface OnConfirmListener {
        void onConfirm();
        void onCancel();
    }

    public static void show(Context context, String title, String message, OnConfirmListener listener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Sim", (dialog, which) -> {
                    if (listener != null) listener.onConfirm();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    if (listener != null) listener.onCancel();
                })
                .setCancelable(false)
                .show();
    }
}