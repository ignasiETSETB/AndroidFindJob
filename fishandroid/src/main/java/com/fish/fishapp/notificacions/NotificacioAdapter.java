package com.fish.fishapp.notificacions;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fish.fishapp.App;
import com.fish.fishapp.R;
import com.fish.fishapp.feines.Job;
import com.fish.fishapp.feines.JobHolder;

public class NotificacioAdapter extends ArrayAdapter<Job>{
	Context context;
    int layoutResourceId;
    Job data[] = null;

    public NotificacioAdapter(Context context, int layoutResourceId, Job[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	App.getInstance().log("getView from JobAdapter position:" + position);
        View row = convertView;
        JobHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new JobHolder();
            
            holder.nombre = (TextView)row.findViewById(R.id.textViewNombre);
            
            Typeface type = Typeface.createFromAsset(context.getAssets(),"fonts/courbd.ttf"); 
    		
            holder.nombre.setTypeface(type);
            
            holder.edad = (TextView)row.findViewById(R.id.textViewEdad);
            holder.foto = (ImageView) row.findViewById(R.id.imageViewWorkerProfiles);
	        holder.precioHora = (TextView)row.findViewById(R.id.textViewPrecioHora);
	        holder.tags =  (TextView)row.findViewById(R.id.textViewTags);
	        holder.distancia = (TextView)row.findViewById(R.id.textViewDistancia);
	        holder.locationName = (TextView)row.findViewById(R.id.textViewLocationName);
	        holder.moneda = (TextView)row.findViewById(R.id.textViewMoneda);
	            
            row.setTag(holder);
        }
        else
        {
            holder = (JobHolder)row.getTag();
        }
        
        Job job = data[position];
        holder.nombre.setText(job.nombre);
        holder.edad.setText(job.edad);
        holder.locationName.setText(job.ciudad+", ");
        holder.distancia.setText(job.distancia);
        holder.tags.setText(job.tags);
        //holder.foto.setImageResource(0);
        holder.precioHora.setText(job.precioHora.toString());
        holder.moneda.setText(job.moneda+"/h");
        App.getInstance().imageCache.loadBitmap(job.fotoURL, holder.foto, job.foto);
        /*
        if (job.foto==null) {
        	//todavía no ha terminado de cargarse
        	//new DownloadImageTask(holder.foto, job.getFoto()).execute(job.getFotoURL());
        	//no hago nada 
        } else {
        	//ya la tengo cargada
        	holder.foto.setImageBitmap(job.foto);
        }
        */
       
        return row;
    }
}
