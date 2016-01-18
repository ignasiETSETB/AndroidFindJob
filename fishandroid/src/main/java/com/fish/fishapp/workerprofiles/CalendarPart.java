package com.fish.fishapp.workerprofiles;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.fish.fishapp.App;
import com.fish.fishapp.R;
import com.fish.fishapp.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarPart {
	
	public Calendar month;
	public CalendarAdapter adapter;
	public Handler handler;
	public ArrayList<String> items; // container to store some random calendar items
	public View calendarLayout;
	
	public void paintCalendar(ViewGroup viewgroup, Context context, String date, Boolean readOnly, Boolean editButton){
		//Equivalente al onCreate
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    calendarLayout = inflater.inflate(R.layout.calendar, viewgroup, true);
		
		App.getInstance().log("Inflado");
		
		month = Calendar.getInstance();
		
		//String date = intent.getStringExtra("date");
		String[] dateArr = date.split("-"); // date format is yyyy-mm-dd
		
		month.set(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1])-1, Integer.parseInt(dateArr[2]));
		
	    
	    items = new ArrayList<String>();
	    adapter = new CalendarAdapter(context, month);
	    
	    GridView gridview = (GridView) calendarLayout.findViewById(R.id.gridview);
	    gridview.setAdapter(adapter);
	    
	    handler = new Handler();
	    handler.post(calendarUpdater);
	    
	    TextView title  = (TextView) calendarLayout.findViewById(R.id.title);
	    title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	    
	    Button buttonEdit =(Button) calendarLayout.findViewById(R.id.buttonEdit);
	    if (editButton) {
	    	buttonEdit.setVisibility(View.VISIBLE);
		} else {
			buttonEdit.setVisibility(View.GONE);
		}
	    
	    TextView previous  = (TextView) calendarLayout.findViewById(R.id.previous);
	    
		    previous.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					App.getInstance().log("Retrocedo mes");
					if(month.get(Calendar.MONTH)== month.getActualMinimum(Calendar.MONTH)) {				
						month.set((month.get(Calendar.YEAR)-1),month.getActualMaximum(Calendar.MONTH),1);
					} else {
						month.set(Calendar.MONTH,month.get(Calendar.MONTH)-1);
					}
					refreshCalendar();
				}
			});
	    
	    
	    TextView next  = (TextView) calendarLayout.findViewById(R.id.next);
	    next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				App.getInstance().log("Avanzo mes");
				if(month.get(Calendar.MONTH)== month.getActualMaximum(Calendar.MONTH)) {				
					month.set((month.get(Calendar.YEAR)+1),month.getActualMinimum(Calendar.MONTH),1);
				} else {
					month.set(Calendar.MONTH,month.get(Calendar.MONTH)+1);
				}
				refreshCalendar();
				
			}
		});
	    
	    if (!readOnly){
	    
			gridview.setOnItemClickListener(new OnItemClickListener() {
			    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			    	TextView date = (TextView)v.findViewById(R.id.date);
			        if(date instanceof TextView && !date.getText().equals("")) {
			        	
			        	
			        	String day = date.getText().toString();
			        	if(day.length()==1) {
			        		day = "0"+day;
			        	}
			        	
			        	if (items.contains(day)){
			        		items.remove(day);
			        		Calendar x = (Calendar) month.clone();
			        		x.set(Calendar.DATE,Integer.parseInt(day));
			        		Utils.removeDateFromList(x,App.getInstance().inEditionAvailabylity);
			        		
			        	} else {
			        		items.add(day);	
			        		Calendar y = (Calendar) month.clone();
			        		y.set(Calendar.DATE,Integer.parseInt(day));
			        		App.getInstance().inEditionAvailabylity.add(y.getTime());
			        		
			        	}
			        	
			        	adapter.setItems(items);
			        	adapter.notifyDataSetChanged();
				
			        	
			        }
			        
			    }
			});
	    }
	}
	
	public void refreshCalendar()
	{
		TextView title  = (TextView) calendarLayout.findViewById(R.id.title);
		
		adapter.refreshDays();
		adapter.notifyDataSetChanged();				
		handler.post(calendarUpdater); // generate some random calendar items				
		
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	}
	
	
	
	public Runnable calendarUpdater = new Runnable() {
		
		@Override
		public void run() {
			items.clear();
			List<Date> diasDisponibles = App.getInstance().inEditionAvailabylity;
			App.getInstance().log("Dias disponibles:" + diasDisponibles.size());
			for(int i=0;i!=diasDisponibles.size();i++) {
				//Si el dia disponible es de este mes y a?, cojo el dia y lo a?do a items
				Calendar cal = Calendar.getInstance();
				cal.setTime(diasDisponibles.get(i));
				App.getInstance().log("Dia a a?dir:" + cal.toString());
				if (cal.get(Calendar.YEAR) == month.get(Calendar.YEAR)){
					if (cal.get(Calendar.MONTH)==month.get(Calendar.MONTH)){
						Integer dia =cal.get(Calendar.DAY_OF_MONTH); 
						App.getInstance().log("Dias a?dido a items:" + dia.toString());
						items.add(dia.toString());		
					}
				}
			}

			adapter.setItems(items);
			adapter.notifyDataSetChanged();
		}
	};
	
	
}
