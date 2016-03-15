package com.fish.fishapp.workerprofiles;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.fish.fishapp.App;
import com.fish.fishapp.R;
import com.fish.fishapp.utils.Server;
import com.fish.fishapp.utils.ServerException;
import com.fish.fishapp.utils.Utils;
import com.squareup.timessquare.CalendarPickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PerfilAfegirPerfilAddicional_Activity extends Activity {

	WorkerProfile workerProfile;
	String workerProfileId;
	ImageView imageViewWorkerProfilePicture;
	Spinner spinnerWorkerProfileWorkType;
	//TextView textViewWorkerProfileLocationName;
	//Spinner spinnerWorkerProfileDistance;
	WebView webMap;
	LinearLayout linearLayoutWorkerProfileTags;
	ArrayList<String> tagList;
	EditText editTextNewTag;	
	EditText editTextWorkerProfilePriceHour;
	EditText editTextWorkerProfilePriceDay;
	EditText editTextWorkerProfilePriceWeek;
	ArrayList<Date> datesList;
	
	ProgressDialog ringProgressDialog;
	Boolean pictureChanged=false;
	
	CalendarPart calPart;
	ViewGroup vg;
	SimpleDateFormat df;
	Date avui;

	CalendarPickerView calendari;

	/*
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		App.getInstance().log("on resume");
		calPart.paintCalendar(vg, this, df.format(avui), true, true);
	}
	*/

	@Override
	public void onDestroy(){

		App.getInstance().log("*");
		App.getInstance().log("************** Tanquem la classe '" + this.getClass().getSimpleName() + "' **************");
		App.getInstance().log("*");

		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		App.getInstance().log("*");
		App.getInstance().log("************** Iniciem la classe '" + this.getClass().getSimpleName() + "' **************");
		App.getInstance().log("*");

		super.onCreate(savedInstanceState);

		setContentView(R.layout.perfil_afegir_perfil_addicional);

		// Habilitem el botó per retrocedir de pantalla

		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Amaguem el teclat

		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// Comprovem si és el primer cop que iniciem aquesta pantalla

		if (savedInstanceState != null){

			// Guardem el ID del treballador

			 workerProfileId = savedInstanceState.getString("workerProfileId");

		} else {

			// És el primer cop que iniciem aquesta pantalla. Intentem obtenir el ID del treballador

			if (getIntent() != null && getIntent().getExtras() != null){ //retorna el primer Intent quan mor i resuccita es null

				Bundle b = getIntent().getExtras();

				workerProfileId = b.getString("workerProfileId");

			} else {

				this.finish();

				return;
			}
		}

		// Comprovem si és un nou perfil

		if (workerProfileId.equals("nuevo")){

			workerProfile = new WorkerProfile();

			workerProfile.getNewWorkerProfile();

			pictureChanged = true;

		} else {

			// Carreguem les dades del perfil a editar

			try {

				workerProfile = Server.readWorkerProfile(workerProfileId);

			} catch (ServerException e) {

				e.printStackTrace();

				App.getInstance().log(e.getMessage());

				App.getInstance().missatgeEnPantalla(App.getInstance().getStringResource(R.string.server_error));

				this.finish();

				return;
			}
		}

		// Carreguem la imatge del perfil

		imageViewWorkerProfilePicture = (ImageView) this.findViewById(R.id.imageViewWorkerProfilePicture);

		App.getInstance().imageCache.loadBitmap(workerProfile.pictureURL, imageViewWorkerProfilePicture, workerProfile.picture);

		//imageViewWorkerProfilePicture.setImageBitmap(workerProfile.picture);

		// Carreguem la resta de dades del perfil

		spinnerWorkerProfileWorkType = (Spinner) this.findViewById(R.id.spinnerWorkerProfileWorkType);

		String myString = Utils.getWorkTypeDisplayString(workerProfile.workType);

		ArrayAdapter myAdap = (ArrayAdapter) spinnerWorkerProfileWorkType.getAdapter();

		int spinnerPosition = myAdap.getPosition(myString);

		spinnerWorkerProfileWorkType.setSelection(spinnerPosition);
		
		//textViewWorkerProfileLocationName = (TextView) this.findViewById(R.id.textViewWorkerProfileLocationName);
		//textViewWorkerProfileLocationName.setText(workerProfile.locationName);
		
		//spinnerWorkerProfileDistance =(Spinner) this.findViewById(R.id.spinnerWorkerProfileDistance);

		String myString2 = workerProfile.distance + " Km";

		//ArrayAdapter myAdap2 = (ArrayAdapter) spinnerWorkerProfileDistance.getAdapter();
		//int spinnerPosition2 = myAdap2.getPosition(myString2);
		//spinnerWorkerProfileDistance.setSelection(spinnerPosition2);
		//spinnerWorkerProfileDistance.setOnItemSelectedListener(new OnDistanceChangedListener());
		
		webMap = (WebView) this.findViewById(R.id.webViewWorkerProfileMap);
		
		// Carreguem el mapa amb la geoposició

		setupWebMap(webMap, Utils.getRadiusFromDisplay(workerProfile.distance));

		// Carreguem la resta de dades del perfil

		tagList = workerProfile.tags;
		
		editTextNewTag = (EditText) this.findViewById(R.id.editTextNewTag);

		linearLayoutWorkerProfileTags = (LinearLayout) this.findViewById(R.id.linearLayoutWorkerProfileTags);
		
		updateTags();

		// Actualitzem el títol amb la moneda del perfil del treballador

		TextView textViewWorkerProfileCurrency = (TextView) this.findViewById(R.id.TextViewPricesSection);

		textViewWorkerProfileCurrency.setText(App.getInstance().getStringResource(R.string.prices_in) + " " + Utils.getCurrencyDisplayString(workerProfile.currency));
		
		editTextWorkerProfilePriceHour = (EditText) this.findViewById(R.id.editTextWorkerProfilePriceHour);

		if (workerProfile.priceHour > 0){
			editTextWorkerProfilePriceHour.setText(workerProfile.priceHour.toString());
		}
		
		editTextWorkerProfilePriceDay = (EditText) this.findViewById(R.id.editTextWorkerProfilePriceDay);

		if (workerProfile.priceDay > 0){
			editTextWorkerProfilePriceDay.setText(workerProfile.priceDay.toString());
		}
		
		editTextWorkerProfilePriceWeek = (EditText) this.findViewById(R.id.editTextWorkerProfilePriceWeek);

		if (workerProfile.priceWeek > 0){
			editTextWorkerProfilePriceWeek.setText(workerProfile.priceWeek.toString());
		}

		/*
		calPart = new CalendarPart();

		vg = (ViewGroup) this.findViewById(R.id.calendarLinearLayout);
		
		df = new SimpleDateFormat("yyyy-M-dd");
		avui = new Date();
		App.getInstance().inEditionAvailabylity=workerProfile.availabilityCalendar;
		calPart.paintCalendar(vg, this, df.format(avui),true,true);
		App.getInstance().log("try to start from top");
		final ScrollView mainScrollView = (ScrollView) this.findViewById(R.id.mainScrollView);
		mainScrollView.requestFocus();
		new CountDownTimer(500,1){

            @Override
            public void onTick(long miliseconds){}

            @Override
            public void onFinish(){
               //after 1/2 second scroll to top
            	runOnUiThread( new Runnable(){
     			   @Override
     			   public void run(){
     			      mainScrollView.fullScroll(ScrollView.FOCUS_UP);
     			   }
     			});
            }
        }.start();
        */

		// Inicialitzem el calendari

		inicialitzarCalendari();
	}

	public void inicialitzarCalendari(){

		Calendar dataFinal = Calendar.getInstance();
		Calendar dataInicial = Calendar.getInstance();

		// Calculem 1 any, a partir de la data d'avui, com data màxima del calendari

		dataFinal.add(Calendar.YEAR, 1);

		// Obtenim la data d'avui

		dataInicial.get(Calendar.DATE);

		// Inicialitzem el calendari amb múltiple selecció

		calendari = (CalendarPickerView) findViewById(R.id.calendar_view);

		calendari.init(dataInicial.getTime(), dataFinal.getTime())
				.withSelectedDate(dataInicial.getTime())
				.inMode(CalendarPickerView.SelectionMode.MULTIPLE);

		// Marquem les dates disponibles del treballador

		calendari.highlightDates(diesSeleccionats());
	}

	private ArrayList<Date> diesSeleccionats(){

		App.getInstance().log("Actualitzant el calendari amb les dates disponibles del perfil...");

		ArrayList<Date> diesSeleccionats = new ArrayList<>();

		// Obtenim la data d'avui

		Date dataActual = new Date();

		// Comprovem si la data a afegir al calendari és igual o superior al moment actual

		for (int i = 0; i < workerProfile.availabilityCalendar.size(); i++){

			if (workerProfile.availabilityCalendar.get(i).compareTo(dataActual) >= 0 ) {

				App.getInstance().log("Data afegida: " + workerProfile.availabilityCalendar.get(i));

				diesSeleccionats.add(workerProfile.availabilityCalendar.get(i));

			} else {

				App.getInstance().log("Data descartada: " + workerProfile.availabilityCalendar.get(i));
			}
		}

		return diesSeleccionats;
	}

	private void updateTags(){

		View tagLine;

		TextView valor;

		Button botoEliminar;

		Integer i;

		linearLayoutWorkerProfileTags.removeAllViews();

		if (tagList != null){

			for (i = 0; i < tagList.size(); i++){

				LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				tagLine = inflater.inflate(R.layout.tag_item, null);

				valor = (TextView) tagLine.findViewById(R.id.textViewDescription);

				valor.setText(tagList.get(i));

				botoEliminar = (Button) tagLine.findViewById(R.id.imageButton1);

				botoEliminar.setTag(i);

				botoEliminar.setOnClickListener(new Button.OnClickListener(){
	
					@Override
					public void onClick(View v) {

						tagList.remove(Integer.parseInt(v.getTag().toString()));

						App.getInstance().log("Eliminada la etiqueta '" + v.getTag().toString() + "'");

						updateTags();

						//ViewGroup vg = (ViewGroup) v.getParent();

						//vg.setVisibility(View.GONE);
					}
				});

				linearLayoutWorkerProfileTags.addView(tagLine);
			}
		}
	}

	public void clickAddTag(View view){
		
		if (editTextNewTag.getText().toString().length() > 0){

			if (tagList == null) {

				tagList = new ArrayList<String>();
			}

			tagList.add(editTextNewTag.getText().toString());

			updateTags();

			editTextNewTag.setText("");
		}
	}
	
	private void setupWebMap(final WebView webView, Integer radius){

		try{
		
			final String centerURL = "javascript:centerAt(" + App.getInstance().usuari.profileLocation.getLatitude() + ", " + App.getInstance().usuari.profileLocation.getLongitude()+ ", " + radius + ")";
	     
		     webView.getSettings().setJavaScriptEnabled(true);

		     // Esperem a que carregui la pàgina per enviar la localització

	    	 webView.setWebViewClient(new WebViewClient(){

				@Override
				public void onPageFinished(WebView view, String url) {

				   	webView.loadUrl(centerURL);

					App.getInstance().log("Mapa carregat");
				}
			 });

		     String MAP_URL = "file:///android_asset/simplemap.html";

		     App.getInstance().log("Tornem a carregar la URL");

	    	 webView.loadUrl(MAP_URL);

		} catch (Exception e){

			App.getInstance().log(e.getMessage());
		}
	}

	public class OnDistanceChangedListener implements android.widget.AdapterView.OnItemSelectedListener {
		 
		  public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {

			  App.getInstance().log("Distancia modificada. Tornem a calcular la distancia: " + parent.getItemAtPosition(pos).toString());
			  
			  setupWebMap(webMap, Utils.getRadiusFromDisplay(parent.getItemAtPosition(pos).toString()));
		  }  

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
    }
	
	public void clickImage(View view){

		Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		startActivityForResult(pickPhoto, 1);
	}
	
	private Boolean readFromControls(){

		Boolean res = true;

		//workerProfile.picture = ((BitmapDrawable)imageViewWorkerProfilePicture.getDrawable()).getBitmap();

		workerProfile.workType = Utils.getWorkTypeCode(spinnerWorkerProfileWorkType.getSelectedItem().toString());

		//No se pueden modificar:

		//workerProfile.Location;
		//workerProfile.workerProfileLocationName;
		//workerProfile.workerProfileCurrency;

		//workerProfile.distance = spinnerWorkerProfileDistance.getSelectedItem().toString();

		workerProfile.tags = tagList;

		if (tagList == null || tagList.size() == 0){

			editTextNewTag.setError("Se requiere una etiqueta, como mínimo.");

			res = false;
		}

		workerProfile.priceHour = Utils.parseInt(editTextWorkerProfilePriceHour.getText().toString());

		if (editTextWorkerProfilePriceHour.getText().toString().trim().equals("")){

			editTextWorkerProfilePriceHour.setError( "Price Hour is required!" );
			res = false;
		}
		
		workerProfile.priceDay = Utils.parseInt(editTextWorkerProfilePriceDay.getText().toString());
		workerProfile.priceWeek = Utils.parseInt(editTextWorkerProfilePriceWeek.getText().toString());
		workerProfile.availabilityCalendar = datesList;
		
		return res;
	}

	public void clickEditCalendar(View view){

		SimpleDateFormat df = new SimpleDateFormat("yyyy-M-dd");

		Intent intent = new Intent(App.getInstance().appContext, CalendarViewActivity.class);
		Date avui = new Date();
		App.getInstance().log("date:" + df.format(avui));
		intent.putExtra("date", df.format(avui));
		startActivity(intent);

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		switch(requestCode) {
			case 1:
				if(resultCode == RESULT_OK){
					Uri selectedImage = imageReturnedIntent.getData();
					imageViewWorkerProfilePicture.setImageURI(selectedImage);
					pictureChanged=true;
				}
				break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Carreguem el menú
		getMenuInflater().inflate(R.menu.edit_worker_profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        case R.id.action_save:
        	if (readFromControls()){
	        	ringProgressDialog = ProgressDialog.show(this, "", App.getInstance().getStringResource(R.string.downloading), true, false);
	        	ringProgressDialog.setCancelable(true);
	        	new Thread(new Runnable() {
	        		@Override
	        		public void run() {
	        			try {
	        				//Time consuming 
	        				
	        				workerProfile.save(pictureChanged);
	        				pictureChanged=false;
	        				finish();
	        				//Refresh
	        				android.os.Message message = new android.os.Message();
	        				message.what = WorkerProfilesActivity.REFRESH;
	        				WorkerProfilesActivity ma = (WorkerProfilesActivity) App.getInstance()._workerProfilesActivity;
	        				ma.fullScreenHandler.sendMessage(message);
	        			} catch (Exception e) {
                            e.printStackTrace();
	        			}
	        			ringProgressDialog.dismiss();
	        		}
	        	}).start();
        	}
        	return true;
        case R.id.action_delete:
        	ringProgressDialog = ProgressDialog.show(this, "", App.getInstance().getStringResource(R.string.downloading), true, false);
        	ringProgressDialog.setCancelable(true);
        	new Thread(new Runnable() {
        		@Override
        		public void run() {
        			try {
        				//Time consuming 
        				workerProfile.deletedAt=new Date();
        	        	workerProfile.save(pictureChanged);
        	        	pictureChanged=false;
        				finish();
        			} catch (Exception e) {
                        e.printStackTrace();
        			}
        			ringProgressDialog.dismiss();
        		}
        	}).start();
        default:
            return super.onOptionsItemSelected(item);
        }
		
	}
}

