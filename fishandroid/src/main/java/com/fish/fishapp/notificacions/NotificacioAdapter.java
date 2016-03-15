package com.fish.fishapp.notificacions;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fish.fishapp.R;

import java.util.ArrayList;


public class NotificacioAdapter extends ArrayAdapter<Notificacio> {

    ArrayList<Notificacio> notificacions;
    Context context;

    public NotificacioAdapter(Context context, ArrayList<Notificacio> notificacions) {
        super(context, 0, notificacions);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Notificacio notificacio = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.llista_notificacions_item, parent, false);
        }
        TextView jobOffered = (TextView) convertView.findViewById(R.id.text_item_job_offered);
        TextView dateStart = (TextView) convertView.findViewById(R.id.text_item_date_start);
        TextView dateEnd = (TextView) convertView.findViewById(R.id.text_item_date_end);
        TextView status = (TextView) convertView.findViewById(R.id.text_item_status);
        LinearLayout layoutStatus = (LinearLayout) convertView.findViewById(R.id.layout_item_estado);

        jobOffered.setText(notificacio.getOfferedJob());
        dateStart.setText(notificacio.getDateStart());
        dateEnd.setText(notificacio.getDateEnd());

        if (notificacio.getStatus() == 1){
            status.setText("Pendiente");
            layoutStatus.setBackgroundColor(Color.rgb(200, 200, 200));
        } else if (notificacio.getStatus() == 2){
            status.setText("Aceptado");
            layoutStatus.setBackgroundColor(Color.rgb(200, 250, 200));
        }else if (notificacio.getStatus() == 4){
            status.setText("Rechazado");
            layoutStatus.setBackgroundColor(Color.rgb(250, 200, 200));
        } else {
            status.setText("No especificado");
        }




        return convertView;
    }
}
