package com.dsinet.pjesmaricapro.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.dsinet.pjesmaricapro.R;
import com.dsinet.pjesmaricapro.db.DbMain;
import com.dsinet.pjesmaricapro.model.Favoriti;
import com.dsinet.pjesmaricapro.ostalo.globals;

import java.util.ArrayList;

public class FavoritiAdapter extends ArrayAdapter<Favoriti> {

    private final AdapterCallback mAdapterCallback;
    private final globals g;

    public interface AdapterCallback {
        void onAdapterCallback();
    }

    public FavoritiAdapter(Context context, ArrayList<Favoriti> favoriti, AdapterCallback callback) {
        super(context, R.layout.fragment_favoriti_list, favoriti);
        this.mAdapterCallback = callback;
        this.g = new globals(context);
    }

    private static class ViewHolder {
        TextView my_izvodjac;
        TextView my_naslov;
        ImageButton btn;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Favoriti favoriti = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.fragment_favoriti_list, parent, false);
            viewHolder.my_izvodjac = convertView.findViewById(R.id.my_pjesma);
            viewHolder.my_naslov = convertView.findViewById(R.id.my_naslov);
            viewHolder.btn = convertView.findViewById(R.id.cdBrisanje);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Hintergrundfarbe aus globalen Einstellungen
        int bgColor = android.graphics.Color.parseColor(g.BojaAplikacije());
        convertView.setBackgroundColor(bgColor);

        viewHolder.my_izvodjac.setTag(String.valueOf(favoriti.get_ID()));
        viewHolder.my_izvodjac.setText(favoriti.get_Izvodjac());
        viewHolder.my_naslov.setText(favoriti.get_Pjesma());

        LinearLayout ll = convertView.findViewById(R.id.llFragment_list);
        ll.setBackgroundColor(bgColor);

        // Löschen-Button
        viewHolder.btn.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    new DbMain(getContext()).deleteFavorit(favoriti.get_ID());
                    mAdapterCallback.onAdapterCallback();
                }
            };

            new AlertDialog.Builder(getContext())
                    .setMessage(getContext().getString(R.string.Upit))
                    .setPositiveButton(getContext().getString(R.string.Da), dialogClickListener)
                    .setNegativeButton(getContext().getString(R.string.Ne), dialogClickListener)
                    .show();
        });

        return convertView;
    }
}
