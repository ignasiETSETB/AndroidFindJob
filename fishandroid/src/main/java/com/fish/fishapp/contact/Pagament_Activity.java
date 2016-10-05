package com.fish.fishapp.contact;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fish.fishapp.App;
import com.fish.fishapp.R;

public class Pagament_Activity extends Activity {

    String IdUsuariEmissor;         // Usuari que contracta
    String IdUsuariReceptor;        // Usuari contractat

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
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

        setContentView(R.layout.pagament);

        // Obtenim les dades de l'usuari desti, per iniciar el xat i recuperar la conversa anterior, si existeix

        IdUsuariEmissor = getIntent().getStringExtra("id_Emissor");
        IdUsuariReceptor = getIntent().getStringExtra("id_Receptor");

        App.getInstance().log("Id de l'usuari que fa el pagament: " + IdUsuariEmissor);
        App.getInstance().log("Id de l'usuari pel qual es fa el pagament: " + IdUsuariReceptor);

        // Creem el navegador Web

        WebView navegadorWeb = (WebView) findViewById(R.id.webViewPagament);

        // Activem el Javascript i el zoom

        navegadorWeb.getSettings().setJavaScriptEnabled(true);
        navegadorWeb.getSettings().setBuiltInZoomControls(true);

        //WebSettings webSettings = webViewPagament.getSettings();

        //webSettings.setJavaScriptEnabled(true);

        // Especifiquem la codificacio

        //webSettings.setDefaultTextEncodingName("utf-8");

        // Carreguem la Url en el navegador

        navegadorWeb.loadUrl("http://www.google.com");

        navegadorWeb.setWebViewClient(new WebViewClient() {

            // Evitem que els enllacos s'obrin fora de l'App

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                return false;
            }
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

            // Accio al premer el boto per retrocedir de pantalla

            case android.R.id.home:

                finish();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
