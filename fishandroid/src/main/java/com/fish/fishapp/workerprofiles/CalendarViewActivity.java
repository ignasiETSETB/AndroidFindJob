/*
* Copyright 2011 Lauri Nevala.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.fish.fishapp.workerprofiles;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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


public class CalendarViewActivity extends Activity {

	public Calendar month;
	public CalendarAdapter adapter;
	public Handler handler;
	public ArrayList<String> items; // container to store some random calendar items
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.calendar);
	    
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	    
	    month = Calendar.getInstance();
	    onNewIntent(getIntent());
	    items = new ArrayList<String>();
	    adapter = new CalendarAdapter(this, month);
	    
	    GridView gridview = (GridView) findViewById(R.id.gridview);
	    gridview.setAdapter(adapter);
	    
	    handler = new Handler();
	    handler.post(calendarUpdater);
	    
	    TextView title  = (TextView) findViewById(R.id.title);
	    title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	    
	    Button buttonEdit =(Button) findViewById(R.id.buttonEdit);
	    buttonEdit.setVisibility(View.GONE);
		
	    
	    TextView previous  = (TextView) findViewById(R.id.previous);
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
	    
	    TextView next  = (TextView) findViewById(R.id.next);
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
	    
		gridview.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		    	App.getInstance().log("Click on day");
		    	TextView date = (TextView)v.findViewById(R.id.date);
		        if(date instanceof TextView && !date.getText().equals("")) {
		        	
		        	Intent intent = new Intent();
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
			
		        	// return chosen date as string format 
		        	//intent.putExtra("date", android.text.format.DateFormat.format("yyyy-MM", month)+"-"+day);
		        	//setResult(RESULT_OK, intent);
		        	//finish();
		        }
		        
		    }
		});
	}
	
	public void refreshCalendar()
	{
		TextView title  = (TextView) findViewById(R.id.title);
		
		adapter.refreshDays();
		adapter.notifyDataSetChanged();				
		handler.post(calendarUpdater); // generate some random calendar items				
		
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	}
	
	public void onNewIntent(Intent intent) {
		String date = intent.getStringExtra("date");
		String[] dateArr = date.split("-"); // date format is yyyy-mm-dd
		
		//JordiB: BUG ya que los meses en java empiezan en 0, luego poner un 5 implica saltar a junio
		//month.set(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]), Integer.parseInt(dateArr[2]));
		month.set(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1])-1, Integer.parseInt(dateArr[2]));
	}
	
	public Runnable calendarUpdater = new Runnable() {
		
		@Override
		public void run() {
			items.clear();
			List<Date> diasDisponibles = App.getInstance().inEditionAvailabylity;
			App.getInstance().log("updating calendar items");
			for(int i=0;i!=diasDisponibles.size();i++) {
				//Si el dia disponible es de este mes y a?, cojo el dia y lo a?do a items
				Calendar cal = Calendar.getInstance();
				cal.setTime(diasDisponibles.get(i));
				if (cal.get(Calendar.YEAR) == month.get(Calendar.YEAR)){
					if (cal.get(Calendar.MONTH)==month.get(Calendar.MONTH)){
						Integer dia =cal.get(Calendar.DAY_OF_MONTH); 
						items.add(dia.toString());		
					}
				}
			}

			adapter.setItems(items);
			adapter.notifyDataSetChanged();
		}
	};
	
	/*
	private void recogerFechasSeleccionadas(){
		List<Date> workerProfileAvailabilityCalendar = App.getInstance().inEditionAvailabylity;
		
		workerProfileAvailabilityCalendar.clear();
		for (int i=0; i!=items.size();i++){
			Calendar cal = (Calendar) month.clone();
			cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(items.get(i)));
			workerProfileAvailabilityCalendar.add(cal.getTime());
		}
	}
	*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
        case android.R.id.home:
        	//recogerFechasSeleccionadas();
            NavUtils.navigateUpFromSameTask(this);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
		
	}
	
	
	
}
