package com.fish.fishapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.fish.fishapp.utils.MyCallback;
import com.fish.fishapp.utils.Server;

import java.util.ArrayList;

public class Eula_Activity extends Activity {
	

	@Override
	public void onStart()
	{
        super.onStart();

        App.getInstance()._eULAActivity = this;
	}
	
	@Override
	public void onDestroy()
	{
        App.getInstance().log("*");
        App.getInstance().log("************** Tanquem la classe '" + this.getClass().getSimpleName() + "' **************");
        App.getInstance().log("*");

        super.onDestroy();

        App.getInstance()._eULAActivity = null;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

        App.getInstance().log("*");
        App.getInstance().log("************** Iniciem la classe '" + this.getClass().getSimpleName() + "' **************");
        App.getInstance().log("*");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.eula);

        // Habilitem el botó per retrocedir de pantalla

        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Obtenim el text corresponent al EULA que ha d'acceptar l'usuari, i el presentem en pantalla

        Server.getLocalizedString("EULA", new MyCallback() {

			@Override
			public void done(String object, Exception e) {

				App.getInstance().log("Hem descarregat l'EULA del servidor");

				if (App.getInstance()._eULAActivity != null){

					// El contigut del "object" és la cadena HTML que conté el text del EULA (<html>...</html>). Codificació UTF-8 pels accents

					WebView webViewEula = (WebView) App.getInstance()._eULAActivity.findViewById(R.id.webViewEula);

					WebSettings webViewSettings = webViewEula.getSettings();

					webViewSettings.setDefaultTextEncodingName("utf-8");

					webViewEula.loadData(object, "text/html; charset=utf-8", "utf-8");
				}
			}

			@Override
			public void done(ArrayList object, Exception e) {}
		});
	}

	public void clickBack(View view){

        finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // Acció al prémer el botó per retrocedir de pantalla

            case android.R.id.home:

                finish();

                return true;
        }

        return super.onOptionsItemSelected(item);
	}
}
