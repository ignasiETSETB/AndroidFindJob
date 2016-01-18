package com.fish.fishapp.contact;

import android.content.Context;
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

public class Xat_Missatge_Adapter_Activity extends ArrayAdapter {

    private Context context;

    ArrayList<Xat_Missatge> arrayMissatges;

    public Xat_Missatge_Adapter_Activity(Context context, ArrayList<Xat_Missatge> arrayMissatges) {

        super(context, R.layout.xat_missatges);

        this.context = context;

        this.arrayMissatges = arrayMissatges;
    }

    public int getCount() {

        return arrayMissatges.size();
    }

    private static class PlaceHolder {

        // Creem un contenidor amb tots els controls de la vista

        TextView nomRemitent;
        TextView textMissatge;
        TextView dataMissatge;

        LinearLayout linearLayout;

        RelativeLayout layoutAdapter;

        public static PlaceHolder generate(View convertView) {

            PlaceHolder placeHolder = new PlaceHolder();

            placeHolder.nomRemitent = (TextView) convertView.findViewById(R.id.nom_remitent);
            placeHolder.textMissatge = (TextView) convertView.findViewById(R.id.missatge);
            placeHolder.dataMissatge = (TextView) convertView.findViewById(R.id.data_missatge);
            placeHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.layout_dades);
            placeHolder.layoutAdapter = (RelativeLayout) convertView.findViewById(R.id.layout_adapter);

            return placeHolder;
        }
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        // Creem un contenidor per tots els control de la vista

        PlaceHolder placeHolder;

        if (convertView == null) {

            convertView = View.inflate(context, R.layout.xat_missatges, null);

            placeHolder = PlaceHolder.generate(convertView);

            convertView.setTag(placeHolder);

        } else {

            placeHolder = (PlaceHolder) convertView.getTag();
        }

        //Omplim tots els controls amb les dades corresponents

        placeHolder.nomRemitent.setText(arrayMissatges.get(position).getRemitent());

        placeHolder.textMissatge.setText(arrayMissatges.get(position).getBody());

        String dateString = new SimpleDateFormat("dd/MM/yyyy hh:mm").format(arrayMissatges.get(position).getData());

        placeHolder.dataMissatge.setText(dateString);

        // Comprovem si hem enviat o rebut un missatge

        if (arrayMissatges.get(position).isEnviat()){

            // Si el missatge és propi

            placeHolder.nomRemitent.setText("Yo");
            placeHolder.nomRemitent.setGravity(Gravity.LEFT);
            placeHolder.dataMissatge.setGravity(Gravity.RIGHT);
            placeHolder.textMissatge.setGravity(Gravity.LEFT);

            //placeHolder.nomRemitent.setTextColor(Color.rgb(100, 175, 100));

            placeHolder.layoutAdapter.setBackgroundResource(R.drawable.bocadillo_verd);

            /*
            placeHolder.linearLayout.removeViewAt(1);
            placeHolder.linearLayout.removeViewAt(0);

            placeHolder.linearLayout.addView(placeHolder.dataMissatge, 0);
            placeHolder.linearLayout.addView(placeHolder.nomRemitent, 1);
            */

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

            params.leftMargin = 50;// setMarginStart(30);

            placeHolder.layoutAdapter.setLayoutParams(params);

        } else {

            // Si el missatge és rebut

            placeHolder.nomRemitent.setGravity(Gravity.LEFT);
            placeHolder.dataMissatge.setGravity(Gravity.RIGHT);
            placeHolder.textMissatge.setGravity(Gravity.LEFT);

            //placeHolder.nomRemitent.setTextColor(Color.rgb(50, 50, 50));

            placeHolder.layoutAdapter.setBackgroundResource(R.drawable.bocadillo_groc);

            /*
            placeHolder.linearLayout.removeViewAt(1);
            placeHolder.linearLayout.removeViewAt(0);

            placeHolder.linearLayout.addView(placeHolder.nomRemitent, 0);
            placeHolder.linearLayout.addView(placeHolder.dataMissatge, 1);
            */

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

            params.rightMargin = 50;// setMarginEnd(30);

            placeHolder.layoutAdapter.setLayoutParams(params);
        }

        return (convertView);
    }
}
