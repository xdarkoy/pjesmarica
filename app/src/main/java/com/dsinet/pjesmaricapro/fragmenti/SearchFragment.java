package com.dsinet.pjesmaricapro.fragmenti;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.dsinet.pjesmaricapro.R;
import com.dsinet.pjesmaricapro.ostalo.dialog;
import com.dsinet.pjesmaricapro.ostalo.filter;
import com.dsinet.pjesmaricapro.ostalo.globals;

public class SearchFragment extends Fragment {

    public SearchFragment() { }

    private globals g;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        g = new globals(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle inState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle inState) {
        // Hintergrundfarbe (mit Fallback)
        try {
            view.setBackgroundColor(Color.parseColor(g.BojaAplikacije()));
        } catch (IllegalArgumentException e) {
            view.setBackgroundColor(Color.WHITE);
        }

        final Switch swPretraga = view.findViewById(R.id.swPretraga);
        final TextView txSwitchOpis = view.findViewById(R.id.txSwitchOpis);
        final EditText edSearch = view.findViewById(R.id.edSearch);
        final TextView lblBrojZapisa = view.findViewById(R.id.txCounterCaption);
        final AppCompatButton btnTrazi = view.findViewById(R.id.cmdTrazi);

        // Eingabefilter anwenden (dein eigener Filter)
        edSearch.setFilters(new InputFilter[]{ new filter().filter });

        // Zähler setzen
        lblBrojZapisa.setText(getString(R.string.lblCounter) + g.BrojZapisa());

        // Switch-Text synchron halten
        updateSwitchText(txSwitchOpis, swPretraga.isChecked());
        swPretraga.setOnCheckedChangeListener((buttonView, isChecked) ->
                updateSwitchText(txSwitchOpis, isChecked)
        );

        // Button-Enable je nach Eingabelänge (kleines UX-Upgrade)
        btnTrazi.setEnabled(false);
        edSearch.addTextChangedListener(new SimpleTextWatcher(s -> {
            btnTrazi.setEnabled(s != null && s.toString().trim().length() >= 3);
        }));

        // Suchen
        btnTrazi.setOnClickListener(v -> {
            final boolean isIzvodjac = swPretraga.isChecked();
            final String pojam = edSearch.getText() != null ? edSearch.getText().toString().trim() : "";

            if (TextUtils.isEmpty(pojam) || pojam.length() < 3) {
                dialog.showErrorDialog(requireContext(),
                        getString(R.string.Greska),
                        getString(R.string.TrazeniPojam));
                return;
            }

            final Bundle bundle = new Bundle();
            bundle.putString("pojam", pojam);

            if (isIzvodjac) {
                Navigation.findNavController(view).navigate(R.id.action_navigation_izvodjaci_result, bundle);
            } else {
                Navigation.findNavController(view).navigate(R.id.action_navigation_pjesme_result, bundle);
            }
        });
    }

    private void updateSwitchText(TextView tx, boolean isChecked) {
        tx.setText(getString(isChecked ? R.string.swOn : R.string.swOff));
    }

    // Minimaler TextWatcher ohne Boilerplate
    private static class SimpleTextWatcher implements android.text.TextWatcher {
        interface OnChanged { void run(CharSequence s); }
        private final OnChanged onChanged;
        SimpleTextWatcher(OnChanged onChanged) { this.onChanged = onChanged; }
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) { onChanged.run(s); }
        @Override public void afterTextChanged(android.text.Editable s) { }
    }
}
