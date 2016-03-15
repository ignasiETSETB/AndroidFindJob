package com.fish.fishapp.contact;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fish.fishapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Xat_Contacte_Adapter extends ArrayAdapter<Xat_Contacte> {

    ArrayList<Xat_Contacte> arrayContactes;
    private Context context;


    public Xat_Contacte_Adapter(Context context, ArrayList<Xat_Contacte> arrayContactes) {

        super(context, 0, arrayContactes);
        this.context = context;
    }


    public View getView(final int position, View convertView, ViewGroup parent) {

        Xat_Contacte xatContacte = getItem(position);

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.xat_contacte_list_item, null);

        }

        TextView nomContacte = (TextView) convertView.findViewById(R.id.xat_list_nom_contacte);
        TextView missatgePendent = (TextView) convertView.findViewById(R.id.xat_list_missatge_pendent);

        if (xatContacte.getMissatgePendent().length() > 0) {
            nomContacte.setTypeface(null, Typeface.BOLD);
            missatgePendent.setVisibility(View.VISIBLE);
        }else {
            nomContacte.setTypeface(null, Typeface.NORMAL);
            missatgePendent.setVisibility(View.INVISIBLE);
        }

        nomContacte.setText(xatContacte.getNomContacte());

        return convertView;
    }
}
