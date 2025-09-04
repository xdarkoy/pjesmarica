package com.dsinet.pjesmaricapro.ostalo;

import android.app.AlertDialog;
import android.content.Context;

public class dialog {
    public static void showErrorDialog(Context cx, final String title, final String message) {
        AlertDialog aDialog = new AlertDialog.Builder(cx).setMessage(message).setTitle(title)
                .setNeutralButton("Close", (dialog, which) -> {
                }).create();
        aDialog.show();
    }
}
