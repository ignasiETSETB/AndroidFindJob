package com.fish.fishapp.workerprofiles;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fish.fishapp.App;
import com.fish.fishapp.R;
import com.fish.fishapp.utils.Utils;

public class WorkerProfileAdapter extends ArrayAdapter<WorkerProfile>{
	Context context; 
    int layoutResourceId;    
    WorkerProfile data[] = null;
    
    public WorkerProfileAdapter(Context context, int layoutResourceId, WorkerProfile[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        WorkerProfileHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new WorkerProfileHolder();
            
            holder.foto = (ImageView) row.findViewById(R.id.imageViewWorkerProfiles);
	        holder.precioHora = (TextView)row.findViewById(R.id.textViewPrecioHora);
	        holder.tags =  (TextView)row.findViewById(R.id.textViewTags);
	        holder.distancia = (TextView)row.findViewById(R.id.textViewDistancia);
	        holder.ciudad=(TextView)row.findViewById(R.id.textViewLocationName);
	            
            row.setTag(holder);
        }
        else
        {
            holder = (WorkerProfileHolder)row.getTag();
        }
        
        WorkerProfile workerProfile = data[position];
        
        holder.foto.setImageResource(0);
    	holder.precioHora.setText(workerProfile.priceHour.toString());
    	holder.tags.setText(Utils.tagListToString(workerProfile.tags));
    	holder.ciudad.setText(workerProfile.locationName + ",");
    	holder.distancia.setText(workerProfile.distance + " Km");
    	App.getInstance().imageCache.loadBitmap(workerProfile.pictureURL, holder.foto,workerProfile.picture);
    	
        return row;
    }
}
