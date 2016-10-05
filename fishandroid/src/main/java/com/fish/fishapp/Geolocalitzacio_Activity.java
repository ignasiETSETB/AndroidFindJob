package com.fish.fishapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fish.fishapp.feines.FeinesLlistat_Activity;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.location.QBLocations;
import com.quickblox.location.model.QBLocation;
import com.quickblox.location.request.QBLocationRequestBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Geolocalitzacio_Activity extends Activity {

	LocationManager locationManager;

	LocationListener locationListener;

    String locationProvider = null;

    Button retry = null;

    Button cancel = null;

	ProgressBar progressBar = null;

	TextView allow = null;

    int temps = 0;

    Double altitude, latitude;

    // Comptador de seguretat. Esperem 30 segons abans d'aturar la geoposició i de presentar el botó "Reintentar". A cada segon actualitzem el temps dedicat

    CountDownTimer comptador = new CountDownTimer(30000, 1000) {

        public void onFinish() {

            App.getInstance().log("Aturem la geoposició del usuari. Ja han passat 30 segons");

            progressBar.setVisibility(View.INVISIBLE);

            allow.setVisibility(View.VISIBLE);

            retry.setVisibility(View.VISIBLE);

            retry.setEnabled(true);

            cancel.setVisibility(View.VISIBLE);

            cancel.setEnabled(true);

            // Aturem el comptador per no entrar recursivament en aquest event

            comptador.cancel();

            // Aturem la geoposició

            locationManager.removeUpdates(locationListener);
        }

        public void onTick(long millisUntilFinished) {

            // Controlem el temps que triguem en geoposicionar al usuari

            temps = temps + 1;
        }
    };

    @Override
	public void onStart()
	{
        super.onStart();

        App.getInstance()._getLocationActivity = this;
	}
	
	@Override
	public void onDestroy()
	{
        App.getInstance().log("*");
        App.getInstance().log("************** Tanquem la classe '" + this.getClass().getSimpleName() + "' **************");
        App.getInstance().log("*");

        super.onDestroy();

        App.getInstance()._getLocationActivity = null;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

        App.getInstance().log("*");
        App.getInstance().log("************** Iniciem la classe '" + this.getClass().getSimpleName() + "' **************");
        App.getInstance().log("*");

        super.onCreate(savedInstanceState);

        // Amaguem la barra superior per ocupar tota la pantalla

        this.getActionBar().hide();

		setContentView(R.layout.geolocalitzacio);

		retry = (Button) findViewById(R.id.buttonRetryLocation);

		retry.setVisibility(View.INVISIBLE);

        cancel = (Button) findViewById(R.id.buttonCancelLocation);

        cancel.setVisibility(View.INVISIBLE);

        progressBar = (ProgressBar) findViewById(R.id.progressBarGetPosition);

        allow = (TextView) findViewById(R.id.textViewAllowGetYourPosition);

        allow.setVisibility(View.INVISIBLE);

        tryGetLocation();
	}

    private void tryGetLocation(){

   	    App.getInstance().log("Intentant geoposicionar al usuari...");
        App.getInstance().log("1");

         locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Obtenim l'estat del GPS

        boolean GPS_Actiu = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Obtenim l'estat de la xarxa

        boolean Xarxa_Activa = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        // Comprovem si tenim senyal GPS o de xarxa

        if (!GPS_Actiu && !Xarxa_Activa) {

            // No tenim cap senyal per geoposicionar

            App.getInstance().log("No tenim cap senyal per geoposicionar");

        } else {

            if (GPS_Actiu && Xarxa_Activa) {

                // Donem prioritat a la geolocalització per la xarxa, encara que no sigui tant precisa; no ens ve de 100 metres més o menys

                locationProvider = LocationManager.NETWORK_PROVIDER;

                App.getInstance().log("GPS i xarxa actius. Obtenim la geoposició mitjançant la xarxa");

            } else {

                // Donem prioritat a la geolocalització per la xarxa, encara que no sigui tant precisa; no ens ve de 100 metres més o menys

                if (Xarxa_Activa) {

                    // Obtenim la geoposició mitjançant la xarxa

                    locationProvider = LocationManager.NETWORK_PROVIDER;
                    App.getInstance().log("Intentant geoposicionar al usuari...");
                    App.getInstance().log("Obtenim la geoposició mitjançant la xarxa");

                } else if (GPS_Actiu) {

                    locationProvider = LocationManager.GPS_PROVIDER;
                    App.getInstance().log("Obtenim la geoposició mitjançant el gps");
                    // Obtenim la geoposició mitjançant el GPS


                }
            }
        }

        // Preparem el geoposicionament...

        locationListener = new LocationListener() {

            // Quan trobem una nova geoposició...

            public void onLocationChanged(Location location) {

                String Localitat = null;
                String CodiPais = null;

                App.getInstance().log("onLocationChanged");

                // Aturem la geoposició

                locationManager.removeUpdates(locationListener);

                // Obtenim les dades de la geoposició
                QBLocation location2 = new QBLocation(location.getLatitude(), location.getLongitude(), "I'm geolocalized!");
                QBLocations.createLocation(location2, new QBEntityCallback<QBLocation>() {
                    @Override
                    public void onSuccess(QBLocation qbLocation, Bundle args) {
                        App.getInstance().log("Usuari geolocalitzat");


                    }

                    @Override
                    public void onError(QBResponseException errors) {
                        App.getInstance().log("Error al geolocalitzar");
                    }
                });

                Geocoder geo = new Geocoder(App.getInstance().appContext, Locale.getDefault());

                List<Address> Adreces = null;

                // Obtenim la llista d'adreçes, en el cas de que hi hagin més d'una

                try {

                    App.getInstance().log("Agafem la primera adreça de la llista obtinguda");

                    Adreces = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                } catch (IOException ioException) {

                    // Error de xarxa o I/O

                    App.getInstance().log("Error de xarxa o I/O");

                    App.getInstance().log(ioException.toString());

                } catch (IllegalArgumentException illegalArgumentException) {

                    // Hem obtingut una longitud i latitud no vàlides

                    App.getInstance().log("Hem obtingut una longitud i latitud no vàlides");

                    App.getInstance().log("Latitud = " + location.getLatitude() + ", Longitud = " + location.getLongitude());

                    App.getInstance().log(illegalArgumentException.toString());

                } catch (Exception e){

                    // Altres errors

                    App.getInstance().log("Error al intentar obtenir la geoposició del usuari");

                    App.getInstance().log(e.toString());

                    e.printStackTrace();
                }

                // Comprovem si tenim la geolocalització

                if (Adreces == null || Adreces.size() == 0) {

                    App.getInstance().log("No hem obtingut cap adreça en el geoposicionament del usuari");

                } else {

                    // De l'adreça sencera obtinguda, extraiem la localitat i el codi de pais

                    Localitat = Adreces.get(0).getLocality();

                    CodiPais = Adreces.get(0).getCountryCode();

                    App.getInstance().log("Localitat: " + Localitat + ", Pais: " + Adreces.get(0).getCountryName() + ", Codi pais: " + CodiPais);

                    App.getInstance().log("Adreces: " + Adreces.get(0).toString());
                }

                // Aturem el comptador de seguretat, perquè ja hem geoposicionat al usuari

                comptador.cancel();

                App.getInstance().log("Comptador aturat");

                // Guardem les dades del geoposicionament obtingut, encara que siguin null

                //App.getInstance().usuari.setLocation(location, Localitat, CodiPais);


                App.getInstance().log("Location: " + location.toString());

                App.getInstance().log("Hem realitzat el geoposicionament en: " + temps + " segons");

                // Comprovem si s'està realitzant una geolocalització des d'un altre lloc

                if (!App.getInstance().ActualizandoGeoposicionUsuario){

                    // Presentem la pantalla principal

                    App.getInstance().log("provo de mostrar pantalla feines llistat");
                    Intent intent = new Intent(App.getInstance().appContext, FeinesLlistat_Activity.class);

                    startActivity(intent);

                    finish();

                } else {

                    finish();
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {

                App.getInstance().log("onStatusChanged");
            }

            public void onProviderEnabled(String provider) {

                App.getInstance().log("onProviderEnabled");
            }

            public void onProviderDisabled(String provider) {

                App.getInstance().log("onProviderDisabled");
            }
        };

        // Realitzem la petició d'actualització de la geoposició del usuari

        locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);

        App.getInstance().log("Geoposicionament iniciat");

        // Iniciem el comptador per evitar que es pugui quedar en aquesta pantalla, indefinidament, esperant a geoposicionar al usuari

        comptador.start();

        App.getInstance().log("Comptador iniciat");

    }

    public void clickRetry(View view){

        App.getInstance().log("Reintentem la geoposició, a petició del usuari");

        progressBar.setVisibility(View.VISIBLE);

        allow.setVisibility(View.INVISIBLE);

        retry.setVisibility(View.INVISIBLE);

        cancel.setVisibility(View.INVISIBLE);

        temps = 0;

        // Reiniciem el geoposicionament

        tryGetLocation();
    }

    public void clickCancel(View view){

        // L'usuar ha cancel·lat la geolocalització. Presentem la pantalla principal

        App.getInstance().log("L'usuar ha cancel·lat la geolocalització");

        Intent intent = new Intent(App.getInstance().appContext, FeinesLlistat_Activity.class);

        startActivity(intent);

        finish();
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
}
