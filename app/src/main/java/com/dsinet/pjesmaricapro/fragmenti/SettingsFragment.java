package com.dsinet.pjesmaricapro.fragmenti;

import static com.dsinet.pjesmaricapro.R.color.*;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.dsinet.pjesmaricapro.R;
import com.dsinet.pjesmaricapro.db.DbMain;
import com.dsinet.pjesmaricapro.ostalo.globals;

public class SettingsFragment extends Fragment {

    private globals g;
    private DbMain db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        g  = new globals(requireContext());
        db = new DbMain(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Hintergrundfarbe anwenden
        try {
            view.setBackgroundColor(Color.parseColor(g.BojaAplikacije()));
        } catch (IllegalArgumentException ex) {
            view.setBackgroundColor(ContextCompat.getColor(requireContext(), white));
        }

        // Top-Bar einfärben
        g.setTopBarColor((AppCompatActivity) requireActivity());

        // Gruppe 1 – App-Farbschema
        ImageButton ibGreenG1 = view.findViewById(R.id.ibGreenG1);
        ImageButton ibBlueG1  = view.findViewById(R.id.ibBlueG1);
        ImageButton ibRedG1   = view.findViewById(R.id.ibRedG1);

        ibGreenG1.setOnClickListener(v ->
                applySettingAndRecreate("BojaAplikacije", dsColor));

        ibBlueG1.setOnClickListener(v ->
                applySettingAndRecreate("BojaAplikacije", dsblue));

        ibRedG1.setOnClickListener(v ->
                applySettingAndRecreate("BojaAplikacije", dsred));

        // Gruppe 2 – Pjesmarnik-Farbe
        ImageButton ibWhiteG2  = view.findViewById(R.id.ibWhiteG2);
        ImageButton ibGreenG2  = view.findViewById(R.id.ibGreenG2);
        ImageButton ibOrangeG2 = view.findViewById(R.id.ibOrangeG2);

        ibWhiteG2.setOnClickListener(v -> {
            updateSetting("BojaPjesmarnika", white);
            Toast.makeText(requireContext(), R.string.pjesmarnik_white, Toast.LENGTH_SHORT).show();
        });
        ibGreenG2.setOnClickListener(v -> {
            updateSetting("BojaPjesmarnika", dsColor);
            Toast.makeText(requireContext(), R.string.pjesmarnik_green, Toast.LENGTH_SHORT).show();
        });
        ibOrangeG2.setOnClickListener(v -> {
            updateSetting("BojaPjesmarnika", dsorange);
            Toast.makeText(requireContext(), R.string.pjesmarnik_orange, Toast.LENGTH_SHORT).show();
        });

        // Gruppe 3 – Schriftfarbe
        ImageButton ibBlackG3 = view.findViewById(R.id.ibBlackG3);
        ImageButton ibRedG3   = view.findViewById(R.id.ibRedG3);
        ImageButton ibWhiteG3 = view.findViewById(R.id.ibWhiteG3);

        ibBlackG3.setOnClickListener(v -> {
            updateSetting("BojaFonta", black);
            Toast.makeText(requireContext(), R.string.font_black, Toast.LENGTH_SHORT).show();
        });
        ibRedG3.setOnClickListener(v -> {
            updateSetting("BojaFonta", dsredlight);
            Toast.makeText(requireContext(), R.string.font_red, Toast.LENGTH_SHORT).show();
        });
        ibWhiteG3.setOnClickListener(v -> {
            updateSetting("BojaFonta", white);
            Toast.makeText(requireContext(), R.string.font_white, Toast.LENGTH_SHORT).show();
        });
    }

    /** Speichert Color-Res als Hex (wie bisher) und recreatet die Activity. */
    private void applySettingAndRecreate(String key, int colorRes) {
        updateSetting(key, colorRes);
        requireActivity().recreate();
    }

    /** Speichert Color-Res als Hex (aktuelle Logik beibehalten). */
    private void updateSetting(String key, int colorRes) {
        int colorInt = ContextCompat.getColor(requireContext(), colorRes);
        db.updateSetting(key, g.hexStringColor(colorInt));
    }
}
