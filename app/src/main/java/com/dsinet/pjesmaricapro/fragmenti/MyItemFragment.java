package com.dsinet.pjesmaricapro.fragmenti;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.dsinet.pjesmaricapro.R;
import com.dsinet.pjesmaricapro.adapter.MyAdapter;
import com.dsinet.pjesmaricapro.db.DbMain;
import com.dsinet.pjesmaricapro.model.My;
import com.dsinet.pjesmaricapro.ostalo.globals;

import java.util.ArrayList;

public class MyItemFragment extends Fragment implements MyAdapter.AdapterCallback {

    private MyAdapter adapter;
    private ListView lv;

    private globals g;
    private DbMain db;

    public MyItemFragment() { }

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
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        int bg;
        try { bg = Color.parseColor(g.BojaAplikacije()); } catch (IllegalArgumentException e) { bg = Color.WHITE; }
        view.setBackgroundColor(bg);

        lv = view.findViewById(R.id.lvPregled);

        adapter = new MyAdapter(requireContext(), new ArrayList<>(), this);
        lv.setAdapter(adapter);
        reloadData();

        ImageButton dodaj = view.findViewById(R.id.cdDodaj);
        dodaj.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_my_to_edit)
        );

        lv.setOnItemClickListener((AdapterView<?> parent, View v1, int position, long id) -> {
            My item = (My) parent.getItemAtPosition(position);
            if (item == null) return;

            Bundle b = new Bundle();
            b.putString("isMy", "1");
            b.putString("ID", item.getID());
            Navigation.findNavController(view).navigate(R.id.action_mojepjesme_to_detail, b);
        });
    }

    private void reloadData() {
        var data = db.getMojePjesme();
        adapter.clear();
        if (data != null) adapter.addAll(data);
        adapter.notifyDataSetChanged();
    }

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
