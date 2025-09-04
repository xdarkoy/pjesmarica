package com.dsinet.pjesmaricapro.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.dsinet.pjesmaricapro.R;
import com.dsinet.pjesmaricapro.db.DbMain;
import com.dsinet.pjesmaricapro.model.My;
import com.dsinet.pjesmaricapro.ostalo.globals;

import java.util.ArrayList;

public class MyAdapter extends ArrayAdapter<My> {

    private AdapterCallback mAdapterCallback;

    public interface AdapterCallback {
        void onAdapterCallback();
    }

    public MyAdapter(Context context, ArrayList<My> my, AdapterCallback callback) {
        super(context, R.layout.fragment_my_list, my);

        this.mAdapterCallback = callback;
    }
    private static class ViewHolder {
        TextView my_pjesma;
        ImageButton cdIzmjena;
        ImageButton cdBrisanje;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        My my = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.fragment_my_list, parent, false);
            viewHolder.my_pjesma = convertView.findViewById(R.id.my_pjesma);
            viewHolder.cdIzmjena = convertView.findViewById(R.id.cdIzmjena);
            viewHolder.cdBrisanje = convertView.findViewById(R.id.cdBrisanje);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        convertView.setBackgroundColor(Color.parseColor(new globals(getContext()).BojaAplikacije()));
        viewHolder.my_pjesma.setText("" + my.getNaslov());
        viewHolder.cdIzmjena.setTag(my.getID());

        ImageButton izmjena = viewHolder.cdIzmjena;
        ImageButton brisanje = viewHolder.cdBrisanje;

        brisanje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                new DbMain(getContext()).deleteMojaPjesma(my.getID());

                                mAdapterCallback.onAdapterCallback();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(getContext().getResources().getString(R.string.Upit)).setPositiveButton(getContext().getResources().getString(R.string.Da), dialogClickListener)
                        .setNegativeButton(getContext().getResources().getString(R.string.Ne), dialogClickListener).show();
            }
        });

        izmjena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NavController nav = Navigation.findNavController(v);

                Bundle bundle = new Bundle();
                bundle.putString("ID", my.getID());

                nav.navigate(R.id.action_my_to_edit, bundle);
            }
        });

        return convertView;
    }
}
