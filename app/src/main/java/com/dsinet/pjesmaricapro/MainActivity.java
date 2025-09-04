package com.dsinet.pjesmaricapro;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.dsinet.pjesmaricapro.db.SqlLiteDbHelper;
import com.dsinet.pjesmaricapro.ostalo.globals;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY_MS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- Sicherstellen: vorbefüllte DB kopieren (falls noch nicht vorhanden) ---
        // Das fängt den Emulator-Fall ab, in dem sonst eine leere DB ohne Tabelle angelegt würde.
        try {
            SqlLiteDbHelper helper = new SqlLiteDbHelper(getApplicationContext());
            // openDataBase kopiert bei Bedarf aus assets (ist idempotent)
            helper.openDataBase();
            helper.close();
        } catch (Exception e) {
            // Loggen wäre hier hilfreich; wir wollen aber nicht beim Start crashen.
            // Falls das fehlschlägt, fahren wir trotzdem fort und verwenden Defaults.
        }

        // ActionBar nur verstecken, wenn vorhanden (je nach Theme kann sie null sein)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_main);

        ConstraintLayout cl = findViewById(R.id.splashLt);

        // Hintergrundfarbe aus Settings – defensiv: Default verwenden, falls DB/Setting fehlt
        String colorString = null;
        try {
            colorString = new globals(this).BojaAplikacije();
        } catch (Exception ignored) {
            // falls globals intern noch Fehler wirft, benutzen wir Default
        }

        if (colorString == null || colorString.isEmpty()) {
            colorString = "#FFFFFF"; // Default-Farbe (weiß)
        }

        try {
            cl.setBackgroundColor(Color.parseColor(colorString));
        } catch (IllegalArgumentException e) {
            // Falls die Farbe ungültig ist -> Fallback weiß
            cl.setBackgroundColor(Color.WHITE);
        }

        TextView txVersion = findViewById(R.id.txVersion);
        txVersion.setText(BuildConfig.VERSION_NAME);

        // Delay: weiter zum CentralActivity
        cl.postDelayed(() -> {
            startActivity(new Intent(MainActivity.this, CentralActivity.class));
            finish();
        }, SPLASH_DELAY_MS);
    }
}
