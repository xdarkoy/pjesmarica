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
import com.dsinet.pjesmaricapro.adapter.ResultIzvodjaciAdapter;
import com.dsinet.pjesmaricapro.db.DbMain;
import com.dsinet.pjesmaricapro.model.Izvodjaci;
import com.dsinet.pjesmaricapro.ostalo.globals;

import java.util.ArrayList;

public class ResultIzvodjaciFragment extends Fragment implements ResultIzvodjaciAdapter.AdapterCallback {

    private ResultIzvodjaciAdapter adapter;
    private ListView lv;

    private DbMain db;
    private globals g;

    public ResultIzvodjaciFragment() { }

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
        return inflater.inflate(R.layout.fragment_result_izvodjaci, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Hintergrundfarbe mit Fallback
        int bg;
        try { bg = Color.parseColor(g.BojaAplikacije()); } catch (IllegalArgumentException e) { bg = Color.WHITE; }
        view.setBackgroundColor(bg);

        lv = view.findViewById(R.id.lvPregled);

        // Suchbegriff aus Args holen
        String pojam = null;
        Bundle args = getArguments();
        if (args != null) pojam = args.getString("pojam");

        // Adapter setzen und Daten laden
        adapter = new ResultIzvodjaciAdapter(requireContext(), new ArrayList<>(), this);
        lv.setAdapter(adapter);

        var data = db.getIzvodjaci(pojam == null ? "" : pojam);
        adapter.clear();
        if (data != null) adapter.addAll(data);
        adapter.notifyDataSetChanged();

        // Klick → zu ResultPjesme
        lv.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
            Izvodjaci item = (Izvodjaci) parent.getItemAtPosition(position);
            if (item == null) return;

            Bundle b = new Bundle();
            b.putString("isIzvodjac", "1");
            b.putString("ID", item.getID());
            Navigation.findNavController(view)
                    .navigate(R.id.action_result_izvodjaci_to_result_pjesme, b);
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

    @Override
    public void onAdapterCallback() {
        // Falls der Adapter einen Refresh anfordert, hier neu laden
        String pojam = null;
        Bundle args = getArguments();
        if (args != null) pojam = args.getString("pojam");

        var data = db.getIzvodjaci(pojam == null ? "" : pojam);
        adapter.clear();
        if (data != null) adapter.addAll(data);
        adapter.notifyDataSetChanged();
    }
}
