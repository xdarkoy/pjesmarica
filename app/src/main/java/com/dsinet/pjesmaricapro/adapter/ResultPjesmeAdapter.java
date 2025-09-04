package com.dsinet.pjesmaricapro.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dsinet.pjesmaricapro.R;
import com.dsinet.pjesmaricapro.db.DbMain;
import com.dsinet.pjesmaricapro.model.Pjesme;
import com.dsinet.pjesmaricapro.ostalo.globals;

import java.util.ArrayList;

public class ResultPjesmeAdapter extends ArrayAdapter<Pjesme> {

    public interface AdapterCallback {
        void onAdapterCallback();
    }

    private final AdapterCallback mAdapterCallback;
    private final globals g;
    private final DbMain db;
    private final LayoutInflater inflater;
    private final int rowBgColor;

    public ResultPjesmeAdapter(Context context, ArrayList<Pjesme> pjesme, AdapterCallback callback) {
        super(context, R.layout.fragment_result_pjesme_list, pjesme);
        this.mAdapterCallback = callback;
        this.g = new globals(context);
        this.db = new DbMain(context);
        this.inflater = LayoutInflater.from(context);

        // Hintergrundfarbe aus Settings vorberechnen
        int bg;
        try {
            bg = Color.parseColor(g.BojaAplikacije());
        } catch (IllegalArgumentException e) {
            bg = Color.WHITE;
        }
        this.rowBgColor = bg;
    }

    private static class ViewHolder {
        TextView my_izvodjac;
        TextView my_naslov;
        ImageButton btn;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Pjesme pjesme = getItem(position);
        if (pjesme == null) {
            // Fallback: leere Zeile, falls Daten unerwartet null sind
            View empty = convertView != null ? convertView
                    : inflater.inflate(R.layout.fragment_result_pjesme_list, parent, false);
            empty.setBackgroundColor(rowBgColor);
            return empty;
        }

        ViewHolder vh;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_result_pjesme_list, parent, false);
            vh = new ViewHolder();
            vh.my_izvodjac = convertView.findViewById(R.id.my_pjesma);
            vh.my_naslov   = convertView.findViewById(R.id.my_naslov);
            vh.btn         = convertView.findViewById(R.id.cdBrisanje);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        // Zeilenhintergrund
        convertView.setBackgroundColor(rowBgColor);

        // Inhalte
        vh.my_izvodjac.setTag(String.valueOf(pjesme.getID()));
        vh.my_izvodjac.setText(pjesme.getAutor() != null ? pjesme.getAutor() : "");
        vh.my_naslov.setText(pjesme.getNaslov() != null ? pjesme.getNaslov() : "");

        // Favorit-Status (Server liefert "0" oder "1")
        final boolean isFavorit = !"0".equals(pjesme.getFavorit());
        vh.btn.setVisibility(isFavorit ? View.INVISIBLE : View.VISIBLE);

        // Klick: Favorit anlegen (ohne Lizenz-/Limit-Prüfung)
        vh.btn.setOnClickListener(v -> {
            if (!isFavorit) {
                try {
                    db.insertFavorit(Integer.parseInt(pjesme.getID()));
                    v.setVisibility(View.INVISIBLE);
                    if (mAdapterCallback != null) {
                        mAdapterCallback.onAdapterCallback();
                    }
                } catch (NumberFormatException ignore) {
                    // falls ID unerwartet kein Integer ist – hier bewusst still
                }
            }
        });

        return convertView;
    }
}
