package com.fish.fishapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.fish.fishapp.feines.FeinesLlistat_Activity;
import com.fish.fishapp.utils.Server;

public class EulaAcceptar_Activity extends Activity {

	@Override
	public void onStart()
	{
        super.onStart();

	    App.getInstance()._acceptEULAActivity = this;
	}
	
	@Override
	public void onDestroy()
	{
        App.getInstance().log("*");
        App.getInstance().log("************** Tanquem la classe '" + this.getClass().getSimpleName() + "' **************");
        App.getInstance().log("*");

        super.onDestroy();

	    App.getInstance()._acceptEULAActivity = null;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

        App.getInstance().log("*");
        App.getInstance().log("************** Iniciem la classe '" + this.getClass().getSimpleName() + "' **************");
        App.getInstance().log("*");

        super.onCreate(savedInstanceState);

        // Amaguem la barra superior per ocupar tota la pantalla

		this.getActionBar().hide();

        setContentView(R.layout.eula_acceptar);

        // Fem servir una font personalitzada i l'apliquem al text de benvinguda a fish

        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/courbd.ttf");

		TextView textViewWelcomeFish = (TextView) this.findViewById(R.id.textViewWelcomeFish);

        textViewWelcomeFish.setTypeface(type);
		
		ImageView imageViewUser = (ImageView) this.findViewById(R.id.imageViewUser);

        TextView textViewUserName = (TextView) this.findViewById(R.id.textViewUserName);

        TextView textViewUserEmail = (TextView ) this.findViewById(R.id.textViewUserEmail);

        // Carreguem la foto del perfil de l'usuari

		App.getInstance().imageCache.loadBitmap(App.getInstance().usuari.profilePictureURL, imageViewUser, App.getInstance().usuari.profilePicture);

        // Presentem el nom i cognoms, i el correu electrònic

		textViewUserName.setText(App.getUserName());

		textViewUserEmail.setText(App.getInstance().usuari.profileEmail);

        // Presentem la pantalla amb el text corresponent al EULA que ha d'acceptar

        WebViewClient myWebClient = new WebViewClient(){

		    @Override
		    public boolean shouldOverrideUrlLoading(WebView  view, String  url){

                App.getInstance().log("Presentem els termes i condicions (EULA)");

                Intent intent = new Intent(App.getInstance().appContext, Eula_Activity.class);

                startActivity(intent);

		        return true;
		    }
		};
		
		WebView webViewTerms = (WebView) this.findViewById(R.id.webViewTerms);

		webViewTerms.setWebViewClient(myWebClient);
		
		Resources res = getResources();

		String str = res.getString(R.string.aceptar_condiciones) + " <a href='http://terms.com'>" + res.getString(R.string.condiciones_de_uso) + "</a>";

		String htmlBody = "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />" + str;

        webViewTerms.loadDataWithBaseURL("file:///android_asset/", htmlBody, "text/html", "utf-8", null);
	}

    public void clickAcceptEULA(View view) {

        // Informem que l'usuari ha acceptat l'EULA

		App.getInstance().usuari.EULA_Aceptado();
		Server.setEULA(true);

        // Comprovem si tenim la geoposició, si no, la obtenim

		if (App.getInstance().usuari.profileLocation == null){

            App.getInstance().log("L'usuari no està geolocalitzat. Presentem la pantalla de geolocalització");

            Intent intent = new Intent(App.getInstance().appContext, Geolocalitzacio_Activity.class);

	        startActivity(intent);

		} else {

            App.getInstance().log("L'usuari sí està geolocalitzat. Accedim a la pantalla principal. Ja ha iniciat sessió, acceptat l'EULA i està geoposicionat");

            Intent intent = new Intent(App.getInstance().appContext, FeinesLlistat_Activity.class);

	        startActivity(intent);
		}

        finish();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.

        //getMenuInflater().inflate(R.menu.accept_eula, menu);

        return true;
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {

		/*
		Handle action bar item clicks here. The action bar will
		automatically handle clicks on the Home/Up button, so long
		as you specify a parent activity in AndroidManifest.xml.
		*/

        switch (item.getItemId()) {

            // Acció al prémer el botó per retrocedir de pantalla

            case android.R.id.home:

                finish();

                return true;
        }

        return super.onOptionsItemSelected(item);
	}
}
