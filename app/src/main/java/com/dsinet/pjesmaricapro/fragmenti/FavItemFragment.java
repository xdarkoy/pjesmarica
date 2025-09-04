package com.dsinet.pjesmaricapro.fragmenti;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.dsinet.pjesmaricapro.R;
import com.dsinet.pjesmaricapro.adapter.FavoritiAdapter;
import com.dsinet.pjesmaricapro.db.DbMain;
import com.dsinet.pjesmaricapro.model.Favoriti;
import com.dsinet.pjesmaricapro.ostalo.globals;

import java.util.ArrayList;

public class FavItemFragment extends Fragment implements FavoritiAdapter.AdapterCallback {

    private FavoritiAdapter adapter;
    private ListView lv;

    private globals g;
    private DbMain db;

    public FavItemFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        g  = new globals(requireContext());
        db = new DbMain(requireContext());

        // Optional: Waiting-Dialog nur zeigen, wenn wirklich nötig
        // new Waiting().Cekaj(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favoriti, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Hintergrundfarbe (mit Fallback)
        int bg;
        try { bg = Color.parseColor(g.BojaAplikacije()); }
        catch (IllegalArgumentException e) { bg = Color.WHITE; }
        view.setBackgroundColor(bg);

        LinearLayout ll = view.findViewById(R.id.llFragment);
        if (ll != null) ll.setBackgroundColor(bg);

        lv = view.findViewById(R.id.lvPregled);

        // Adapter zunächst mit leerer Liste setzen, dann Daten laden
        adapter = new FavoritiAdapter(requireContext(), new ArrayList<>(), this);
        lv.setAdapter(adapter);
        reloadData();

        lv.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
            Favoriti item = (Favoriti) parent.getItemAtPosition(position);
            if (item == null) return;

            Bundle b = new Bundle();
            b.putString("ID", item.get_PjesmaId());

            Navigation.findNavController(view)
                    .navigate(R.id.action_favoriti_to_detail, b);
        });
    }

    /** Lädt die Favoriten neu in den Adapter. */
    private void reloadData() {
        var data = db.getFavoriti();
        adapter.clear();
        if (data != null) adapter.addAll(data);
        adapter.notifyDataSetChanged();
    }

    /** Callback aus dem Adapter (z. B. nach Löschen). */
    @Override
    public void onAdapterCallback() {
        reloadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        lv = null;
    }
}
