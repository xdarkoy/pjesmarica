package com.dsinet.pjesmaricapro.fragmenti;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.dsinet.pjesmaricapro.R;
import com.dsinet.pjesmaricapro.adapter.ResultPjesmeAdapter;
import com.dsinet.pjesmaricapro.db.DbMain;
import com.dsinet.pjesmaricapro.model.Pjesme;
import com.dsinet.pjesmaricapro.ostalo.globals;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResultPjesmeFragment extends Fragment implements ResultPjesmeAdapter.AdapterCallback {

    private ResultPjesmeAdapter adapter;
    private ListView lv;

    @Nullable private String pojam;
    private int autorId = -1;

    private DbMain db;
    private globals g;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    public ResultPjesmeFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        g  = new globals(requireContext());
        db = new DbMain(requireContext());

        // Args sicher lesen
        Bundle args = requireArguments();
        if (args.containsKey("ID")) {
            try {
                autorId = Integer.parseInt(args.getString("ID"));
            } catch (NumberFormatException ignored) {
                autorId = -1;
            }
        }
        if (args.containsKey("pojam")) {
            pojam = args.getString("pojam");
        }

        // (Optional) Dein Waiting-Dialog – nur anzeigen, wenn du willst
        // new Waiting().Cekaj(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result_pjesme, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Hintergrundfarbe mit Fallback
        int bg;
        try {
            bg = Color.parseColor(g.BojaAplikacije());
        } catch (IllegalArgumentException e) {
            bg = Color.WHITE;
        }
        view.setBackgroundColor(bg);

        lv = view.findViewById(R.id.lvPregled);

        // Leere Liste setzen; echte Daten dann laden
        adapter = new ResultPjesmeAdapter(requireContext(), new ArrayList<>(), this);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
            Pjesme item = (Pjesme) parent.getItemAtPosition(position);
            if (item == null) return;

            Bundle b = new Bundle();
            b.putString("ID", item.getID());
            Navigation.findNavController(view).navigate(R.id.action_result_to_detail, b);
        });

        // Daten laden (optional im Hintergrund – hier einfacher Executor)
        loadData();
    }

    private void loadData() {
        io.execute(() -> {
            final ArrayList<Pjesme> data = db.getPjesme(autorId < 0 ? 0 : autorId, pojam);
            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                adapter.clear();
                if (data != null) adapter.addAll(data);
                adapter.notifyDataSetChanged();
                // new Waiting().Zatvori(); // falls dein Waiting-Dialog ein Close hat
            });
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // ActionBar null-sicher
        AppCompatActivity act = (AppCompatActivity) requireActivity();
        if (act.getSupportActionBar() != null) {
            act.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public void onAdapterCallback() {
        // Wird z. B. nach Favorit-Insert aufgerufen – Liste neu laden, wenn nötig:
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Kein Leak: ListView-Referenz freigeben
        lv = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        io.shutdownNow();
    }
}
