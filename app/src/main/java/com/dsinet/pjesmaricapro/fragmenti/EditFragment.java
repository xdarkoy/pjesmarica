package com.dsinet.pjesmaricapro.fragmenti;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.dsinet.pjesmaricapro.R;
import com.dsinet.pjesmaricapro.db.DbMain;
import com.dsinet.pjesmaricapro.model.My;
import com.dsinet.pjesmaricapro.ostalo.dialog;
import com.dsinet.pjesmaricapro.ostalo.filter;
import com.dsinet.pjesmaricapro.ostalo.globals;

public class EditFragment extends Fragment {

    private globals g;
    private DbMain db;

    public EditFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        g  = new globals(requireContext());
        db = new DbMain(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Hintergrundfarbe mit Fallback
        int bg;
        try { bg = Color.parseColor(g.BojaAplikacije()); }
        catch (IllegalArgumentException e) { bg = Color.WHITE; }
        view.setBackgroundColor(bg);

        // Views
        EditText edNaslov = view.findViewById(R.id.edNaslov);
        EditText edTekst  = view.findViewById(R.id.edTekst);
        ImageButton btnSave  = view.findViewById(R.id.cdSave);
        ImageButton btnClose = view.findViewById(R.id.cdClose);

        // Filter anwenden
        edNaslov.setFilters(new InputFilter[]{ new filter().filter });
        edTekst.setFilters(new InputFilter[]{ new filter().filter });

        // Argumente lesen
        String id = null;
        Bundle args = getArguments();
        if (args != null) {
            id = args.getString("ID");
        }

        // Titel + ggf. Daten laden
        AppCompatActivity act = (AppCompatActivity) requireActivity();
        if (!TextUtils.isEmpty(id)) {
            if (act.getSupportActionBar() != null) {
                act.getSupportActionBar().setTitle(getString(R.string.Izmjena));
            }
            My existing = db.getMojaPjesma(id);
            if (existing != null) {
                edNaslov.setText(existing.getNaslov());
                edTekst.setText(existing.getPjesma());
            }
        } else {
            if (act.getSupportActionBar() != null) {
                act.getSupportActionBar().setTitle(getString(R.string.Dodaj));
            }
        }

        // Schließen
        btnClose.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_edit_to_mojepjesme)
        );

        // Speichern
        final String finalId = id; // effectively final fürs Lambda
        btnSave.setOnClickListener(v -> {
            String naslov = edNaslov.getText() != null ? edNaslov.getText().toString().trim() : "";
            String tekst  = edTekst.getText()  != null ? edTekst.getText().toString().trim()  : "";

            if (naslov.length() < 3) {
                dialog.showErrorDialog(requireContext(), getString(R.string.Greska),
                        "Naslov nemože biti manji od 3 znaka!");
                return;
            }
            if (tekst.length() < 3) {
                dialog.showErrorDialog(requireContext(), getString(R.string.Greska),
                        "Tekst nemože biti manji od 3 znaka!");
                return;
            }

            My my = new My();
            my.setNaslov(naslov);
            my.setPjesma(tekst);

            if (!TextUtils.isEmpty(finalId)) {
                my.setID(finalId);
                db.updateMy(my);
            } else {
                db.insertMy(my);
            }

            Navigation.findNavController(view).navigate(R.id.action_edit_to_mojepjesme);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        AppCompatActivity act = (AppCompatActivity) requireActivity();
        if (act.getSupportActionBar() != null) {
            act.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }
}
