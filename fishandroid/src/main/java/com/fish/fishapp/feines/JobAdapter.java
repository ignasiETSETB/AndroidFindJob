package com.fish.fishapp.feines;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fish.fishapp.App;
import com.fish.fishapp.R;

public class JobAdapter extends ArrayAdapter<Job>{
	Context context; 
    int layoutResourceId;    
    Job data[] = null;


    public JobAdapter(Context context, int layoutResourceId, Job[] data) {
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
        /*
        Integer imageInt=0;
        switch(job.tags){
            case "analista": case "Analista": imageInt=R.drawable.analista;
                App.getInstance().log("----------------------------- IMAGE ID ="+imageInt);

        }
        */
        holder.nombre.setText(job.nombre);
        holder.edad.setText(job.edad);
        holder.locationName.setText(job.ciudad+", ");
        holder.distancia.setText(job.distancia);
        holder.tags.setText(job.tags);
        //holder.foto.setImageBitmap(job.foto);
        App.getInstance().log("------------------------------"+job.tags);
        switch(job.tags) {
            case "Analista": holder.foto.setImageResource(R.drawable.analista);
                job.imageInt=R.drawable.analista;
                break;
            case "Transportista": holder.foto.setImageResource(R.drawable.transportista);
                job.imageInt=R.drawable.transportista;
                break;
            case "Ejecutivo": holder.foto.setImageResource(R.drawable.ejecutivo);
                job.imageInt=R.drawable.ejecutivo;
                break;
            case "Camarera": holder.foto.setImageResource(R.drawable.camarera);
                job.imageInt=R.drawable.camarera;
                break;
                    }
        holder.precioHora.setText(job.precioHora.toString());
        holder.moneda.setText(job.moneda+"/h");
        //App.getInstance().imageCache.loadBitmap(job.fotoURL, holder.foto, job.foto);
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
