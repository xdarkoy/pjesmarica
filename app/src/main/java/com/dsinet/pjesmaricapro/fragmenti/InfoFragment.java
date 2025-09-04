package com.dsinet.pjesmaricapro.fragmenti;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dsinet.pjesmaricapro.BuildConfig;
import com.dsinet.pjesmaricapro.R;
import com.dsinet.pjesmaricapro.ostalo.globals;

public class InfoFragment extends Fragment {

    private globals g;

    public InfoFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        g = new globals(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Hintergrundfarbe mit Fallback
        int bg;
        try {
            bg = Color.parseColor(g.BojaAplikacije());
        } catch (IllegalArgumentException e) {
            bg = Color.WHITE;
        }
        view.setBackgroundColor(bg);

        // Version anzeigen
        TextView verzija = view.findViewById(R.id.txVerzija);
        verzija.setText(BuildConfig.VERSION_NAME);

        // Anzahl Lieder anzeigen
        TextView counter = view.findViewById(R.id.txBrojPjesama);
        counter.setText(g.BrojZapisa());

        // Datenschutzerklärung-Link
        TextView status = view.findViewById(R.id.txStatus);
        if (status != null) {
            final String url = "https://docs.google.com/document/d/1GMQ3-y-FqOnVvhe7yGEkz9xSgFcdGe62wcH5tYma4ng/view?usp=sharing";

            // Text setzen (Kroatisch)
            status.setText("Politika privatnosti");

            // Unterstreichen wie ein Link
            status.setPaintFlags(status.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            // Klickbar machen
            status.setClickable(true);
            status.setOnClickListener(v -> openUrl(url));
        }
    }

    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), "Nije moguće otvoriti link.", Toast.LENGTH_SHORT).show();
        }
    }
}
