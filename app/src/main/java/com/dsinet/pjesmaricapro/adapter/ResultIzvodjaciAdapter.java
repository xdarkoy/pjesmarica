package com.dsinet.pjesmaricapro.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dsinet.pjesmaricapro.R;
import com.dsinet.pjesmaricapro.model.Izvodjaci;
import com.dsinet.pjesmaricapro.ostalo.globals;

import java.util.ArrayList;

public class ResultIzvodjaciAdapter extends ArrayAdapter<Izvodjaci> {

    private AdapterCallback mAdapterCallback;

    public interface AdapterCallback {
        void onAdapterCallback();
    }

    public ResultIzvodjaciAdapter(Context context, ArrayList<Izvodjaci> izvodjaci, AdapterCallback callback) {
        super(context, R.layout.fragment_result_izvodjaci_list, izvodjaci);

        this.mAdapterCallback = callback;
    }

    private static class ViewHolder {
        TextView my_izvodjac;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Izvodjaci izvodjaci = getItem(position);
        ViewHolder viewHolder = new ViewHolder();

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.fragment_result_izvodjaci_list, parent, false);
            viewHolder.my_izvodjac = convertView.findViewById(R.id.my_pjesma);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        convertView.setBackgroundColor(Color.parseColor(new globals(getContext()).BojaAplikacije()));
        viewHolder.my_izvodjac.setTag("" + izvodjaci.getID());
        viewHolder.my_izvodjac.setText("" + izvodjaci.getNaziv());

        return convertView;
    }
}
