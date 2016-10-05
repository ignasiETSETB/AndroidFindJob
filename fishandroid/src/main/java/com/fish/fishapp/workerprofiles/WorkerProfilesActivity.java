package com.fish.fishapp.workerprofiles;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.fish.fishapp.App;
import com.fish.fishapp.R;
import com.fish.fishapp.feines.FeinesDetall_Activity;
import com.fish.fishapp.feines.FeinesLlistat_Activity;
import com.fish.fishapp.feines.Job;
import com.fish.fishapp.utils.Server;
import com.fish.fishapp.utils.ServerException;

import java.util.ArrayList;

public class WorkerProfilesActivity extends Activity {

	public static final int REFRESH = 100;
	
	private ListView listViewWorkerProfiles;
	// ArrayList WorkerProfile
	ArrayList<WorkerProfile> arrayListWorkerProfiles;
	WorkerProfileAdapter adapter;
	
	private ProgressDialog pd = null;
    private Object data = null;
    
    public Handler fullScreenHandler = new Handler(new IncomingHandlerCallback());
    
	class IncomingHandlerCallback implements Handler.Callback {
		@Override
		public boolean handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case WorkerProfilesActivity.REFRESH:
				// Show the ProgressDialog on this thread
				WorkerProfilesActivity.this.pd = ProgressDialog.show(WorkerProfilesActivity.this, "", App.getInstance().getStringResource(R.string.downloading), true, false);
				
		        // Start a new thread that will download all the data
		        DownloadT();
			}
			
			return true;
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}
	
	@Override
	public void onStart()
	{
	    // RUN SUPER | REGISTER ACTIVITY AS INSTANTIATED IN APP CLASS

	        super.onStart();
	        App.getInstance()._workerProfilesActivity = this;
	}
	
	@Override
	public void onDestroy()
	{
	    // RUN SUPER | REGISTER ACTIVITY AS NULL IN APP CLASS

	        super.onDestroy();
	        App.getInstance()._workerProfilesActivity = null;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_worker_profiles);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		//this.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
		//this.getSupportActionBar().setTitle(Html.fromHtml("<font color=\"white\">" + getString(R.string.app_name) + "</font>"));
		
		
		
		// Show the ProgressDialog on this thread
        this.pd = ProgressDialog.show(WorkerProfilesActivity.this, "", App.getInstance().getStringResource(R.string.downloading), true, false);

        // Start a new thread that will download all the data
        this.DownloadT();
        
	}

	public void DownloadT(String... args){
		// This is where you would do all the work of downloading your data
		//Time consuming
		ArrayList<WorkerProfile> resultObject = null;
		try {
			resultObject = Server.queryWorkerProfiles("");
		} catch (ServerException e) {
			e.printStackTrace();
			App.getInstance().missatgeEnPantalla(App.getInstance().getStringResource(R.string.server_error));

		} catch (Exception e) {
			App.getInstance().log("DownloadTask Exception");
			e.printStackTrace();
		}

		Handler handler = new Handler();
		App.getInstance().log("esperem 10 s");
		final ArrayList<WorkerProfile> finalResultObject = resultObject;
		handler.postDelayed(new Runnable() {
			public void run() {
				// Actions to do after 10 seconds

				// Pass the result data back to the mnu_main activity
				if (finalResultObject == null) {
					App.getInstance().log("WorkerProfilesActivity onPostExecute (NULL)");
				} else {
					App.getInstance().log("WorkerProfilesActivity onPostExecute " + finalResultObject.toString());
				}
				if (WorkerProfilesActivity.this.pd != null) {
					ArrayList<WorkerProfile> arr = finalResultObject;
					WorkerProfilesActivity.this.arrayListWorkerProfiles = arr;

					WorkerProfilesActivity.this.arrayListWorkerProfiles = (ArrayList<WorkerProfile>) finalResultObject;
					adapter = new WorkerProfileAdapter(WorkerProfilesActivity.this, R.layout.item_worker_profile, arrayListWorkerProfiles.toArray(new WorkerProfile[arrayListWorkerProfiles.size()]));
					listViewWorkerProfiles = (ListView) findViewById(R.id.listViewWorkerProfiles);
					listViewWorkerProfiles.setAdapter(adapter);

					// ListView Item Click Listener
					listViewWorkerProfiles.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view,
												int position, long id) {
							/*
							// ListView Clicked item index
							int itemPosition = position;

							// ListView Clicked item value
							WorkerProfile itemValue = (WorkerProfile) listViewWorkerProfiles.getItemAtPosition(position);

							Intent intent = new Intent(App.getInstance().appContext, PerfilAfegirPerfilAddicional_Activity.class);
							Bundle b = new Bundle();
							b.putString("workerProfileId", itemValue.workerProfileObjectId);
							intent.putExtras(b); //Put your id to your next Intent
							startActivity(intent);
							*/
							// Obtenim les dades de la feina seleccionada
							WorkerProfile  itemJob = (WorkerProfile) listViewWorkerProfiles.getItemAtPosition(position);

							//App.getInstance().log("Nom del candidat seleccionat: " + itemJob.nombre);
							App.getInstance().log("Id del candidat seleccionat: " + itemJob.ObjectId);

							// Presentem el detall de la feina seleccionada
							Intent intent = new Intent(App.getInstance().appContext, PerfilAfegirPerfilAddicional_Activity.class);//FeinesDetall_Activity.class);

							// Informem al sistema del ID de la feina seleccionada
							Bundle b = new Bundle();
							// sirve para saber si se añade uno nuevo o se edita uno antiguo
							b.putString("workerProfileId","old");
							b.putString("JobId", itemJob.ObjectId);
							b.putInt("Id",itemJob.userID);
							//b.putString("nombre",itemJob.nombre);
							b.putString("jobOffered", itemJob.tags.get(0));
							b.putString("priceHour", itemJob.precioHora.toString());
							//b.putString("edad",itemJob.edad);
							b.putString("ciudad",itemJob.ciudad);
							intent.putExtras(b);
							startActivity(intent);
						}

					});


					WorkerProfilesActivity.this.pd.dismiss();
				}
			}
		}, 4000);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.worker_profiles, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        case R.id.action_add_worker_profile:
        	Intent intent = new Intent(App.getInstance().appContext, PerfilAfegirPerfilAddicional_Activity.class);
        	Bundle b = new Bundle();
        	b.putString("workerProfileId","nuevo");
        	intent.putExtras(b); //Put your id to your next Intent
            startActivity(intent);
			return true;
        default:
            return super.onOptionsItemSelected(item);
        }
	}

	@Override
	public void onBackPressed() {
		finish();
	}

}
