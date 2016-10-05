package com.fish.fishapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.fish.fishapp.feines.FeinesLlistat_Activity;
import com.fish.fishapp.utils.Server;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBProvider;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.core.request.QBRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Login_Activity extends Activity {

	public static String adress1;

	@Override
	public void onStart()
	{
	    super.onStart();

	    App.getInstance()._loginActivity = this;
	}
	
	@Override
	public void onDestroy()
	{
        App.getInstance().log("*");
        App.getInstance().log("************** Tanquem la classe '" + this.getClass().getSimpleName() + "' **************");
        App.getInstance().log("*");

        super.onDestroy();

	    App.getInstance()._loginActivity = null;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

        App.getInstance().log("*");
        App.getInstance().log("************** Iniciem la classe '" + this.getClass().getSimpleName() + "' **************");
        App.getInstance().log("*");

        super.onCreate(savedInstanceState);

        // Amaguem la barra superior per ocupar tota la pantalla

        this.getActionBar().hide();
		
		setContentView(R.layout.login);

        // Fem servir una font personalitzada i l'apliquem als texts

		Typeface type = Typeface.createFromAsset(getAssets(),"fonts/courbd.ttf");

		TextView fish = (TextView) this.findViewById(R.id.textViewWelcomeFish);

		fish.setTypeface(type);

		TextView behappy = (TextView) this.findViewById(R.id.textViewBeHappy);

        behappy.setTypeface(type);

		//TextView welcome = (TextView) this.findViewById(R.id.textViewWelcome);

		//welcome.setTypeface(type);

        //Button btnfacebook = (Button) this.findViewById(R.id.buttonConnectWithFacebook);

        //btnfacebook.setTypeface(type);

		xatSesion();

	}

	public void facebookLogin(View view) {

		final EditText[] et1 = new EditText[1];
		final EditText[] et2 = new EditText[1];
		//Creació de la sessió QB

		QBAuth.createSession(new QBEntityCallback<QBSession>() {

			@Override
			public void onSuccess(QBSession session, Bundle params) {
				// You have successfully created the session
				//
				// Now you can use QuickBlox API!
				App.getInstance().log("Sessió de QB creada");
				et1[0] = (EditText)findViewById(R.id.nombre);
				et2[0] = (EditText)findViewById(R.id.password);
				final String nombre= et1[0].getText().toString();
				final String password=et2[0].getText().toString();
				App.getInstance().log("Crear usuari amb Nombre:"+nombre+" password:"+password);
				final QBUser user = new QBUser(nombre, password);
				//user.setEmail("introduce tu email");

				QBUsers.signUp(user, new QBEntityCallback<QBUser>() {
					@Override
					public void onSuccess(QBUser user, Bundle args) {
						App.getInstance().log("creat nou usuari");
						//iniciar sessio
						QBUser user_signin = new QBUser(nombre, password);
						QBUsers.signIn(user_signin, new QBEntityCallback<QBUser>() {
							@Override
							public void onSuccess(QBUser user, Bundle params) {
								App.getInstance().log("sesio iniciada");

								//creo un custom object per l'usuari nou

								final QBCustomObject object = new QBCustomObject();
								object.setClassName("users");
								object.putString("username",nombre);
								object.putString("password",password);
								object.putBoolean("eulaAccepted",false);

								QBCustomObjects.createObject(object, new QBEntityCallback<QBCustomObject>() {
									@Override
									public void onSuccess(QBCustomObject createdObject, Bundle params) {
										App.getInstance().log("creats customobject users per al nou usuari");
										//Comprovem si ha acceptat l'EULA
										if(object.getBoolean("eulaAccepted")==true){

											App.getInstance().log("Presentem la pantalla principal. Ha iniciat sessió i ha acceptat l'EULA");

											Intent intent = new Intent(App.getInstance().appContext, FeinesLlistat_Activity.class);



											startActivity(intent);

											finish();

										}else{
											App.getInstance().log("Presentem la pantalla de l'EULA perquè les accepti");

											Intent intent = new Intent(App.getInstance().appContext, EulaAcceptar_Activity.class);

											startActivity(intent);

											finish();
										}
										int userID = createdObject.getUserId();
										String ID = createdObject.getCustomObjectId();
										App.setUserID(userID);
										App.setID(ID);
										App.setUserData(createdObject);
									}

									@Override
									public void onError(QBResponseException errors) {
										App.getInstance().log("Error al crear customobject users per al nou usuari");
									}
								});
							}

							@Override
							public void onError(QBResponseException errors) {
								App.getInstance().log("error al iniciar sessio");
							}
						});






					}



					@Override
					public void onError(QBResponseException errors) {
						App.getInstance().log("error al crear usuari, potser ja te conta, provem de log in");

						//QBUser user = new QBUser(nombre, password);

						QBUsers.signIn(user, new QBEntityCallback<QBUser>() {
							@Override
							public void onSuccess(QBUser user, Bundle params) {
								App.getInstance().log("Iniciar sessio usuari amb Nombre:"+nombre+" password:"+password);
								App.getInstance().log("user ID:"+Integer.toString(user.getId()) );

								QBRequestGetBuilder req = new QBRequestGetBuilder();
								req.eq("user_id", Integer.toString(user.getId()) );

								QBCustomObjects.getObjects("users", req, new QBEntityCallback<ArrayList<QBCustomObject>>(){
									@Override
									public void onSuccess(ArrayList<QBCustomObject> customObject, Bundle params) {
										// Check the 1st (and only!) result of the query, if it has eulaAccepted or not
										if(customObject.get(0).getBoolean("eulaAccepted")==true){

											App.getInstance().log("Presentem la pantalla principal. Ha iniciat sessió i ha acceptat l'EULA");

											Intent intent = new Intent(App.getInstance().appContext, FeinesLlistat_Activity.class);



											startActivity(intent);

											finish();
										} else {
											App.getInstance().log("Presentem la pantalla de l'EULA perquè les accepti");

											Intent intent = new Intent(App.getInstance().appContext, EulaAcceptar_Activity.class);

											startActivity(intent);

											finish();
										}
										int userID = customObject.get(0).getUserId();
										String ID = customObject.get(0).getCustomObjectId();
										App.setUserID(userID);
										App.setID(ID);
										App.setUserData(customObject.get(0));
									}

									@Override
									public void onError(QBResponseException errors) {

									}
								});


							}

							@Override
							public void onError(QBResponseException errors) {
								App.getInstance().log("error al iniciar sessió");
							}
						});


					}
				});


				// ALF *** http://quickblox.com/developers/SimpleSample-users-android#Sign_In_using_Facebook.2FTwitter_access_token
				// *** Enabling social integration
				/*
				QBUsers.signInUsingSocialProvider(QBProvider.FACEBOOK, token , null, new QBEntityCallback<QBUser>() {
					@Override
					public void onSuccess(QBUser user, Bundle args) {
						App.getInstance().log("FB OK");
					}

					@Override
					public void onError(QBResponseException errors) {
						App.getInstance().log("Error FB");
					}
				});
				*/

			}



			@Override
			public void onError(QBResponseException errors) {
				App.getInstance().log("Problema al crear sessió de QB");
			}
		});



	/*
		ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {

			@Override
			public void done(ParseUser user, ParseException e) {

				if (user == null) {
					App.getInstance().log("User = NULL");
					App.getInstance().log("Usuari no identificat. Nom d'usuari i/o contrasenya incorrectes.");

					if (e != null) {
						App.getInstance().log(e.getMessage());
					}
				} else {

					if (user.isNew()) {

						App.getInstance().log("Nou usuari, registrat i connectat a través de Facebook!");

					} else {

						App.getInstance().log("Usuari connectat a través de Facebook!");
					}

					// Guardem les dades del usuari identificat

					Server.setUserData(user);

					// Comprovem si ja ha acceptat l'EULA

					if (App.getInstance().usuari.eulaAccepted) {

						// Presentem la pantalla principal. Ha iniciat sessió i ha acceptat l'EULA

						ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
						parseInstallation.put("user", ParseUser.getCurrentUser());
						parseInstallation.saveInBackground();

						App.getInstance().log("Presentem la pantalla principal. Ha iniciat sessió i ha acceptat l'EULA");

						Intent intent = new Intent(App.getInstance().appContext, FeinesLlistat_Activity.class);

						startActivity(intent);

						finish();

					} else {

						ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
						parseInstallation.put("user", ParseUser.getCurrentUser());
						parseInstallation.saveInBackground();

						// Presentem la pantalla de l'EULA perquè les accepti

						App.getInstance().log("Presentem la pantalla de l'EULA perquè les accepti");

						Intent intent = new Intent(App.getInstance().appContext, EulaAcceptar_Activity.class);

						startActivity(intent);

						finish();
					}
				}
            }
    	});
		*/
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		/*
		Handle action bar item clicks here. The action bar will
		automatically handle clicks on the Home/Up button, so long
		as you specify a parent activity in AndroidManifest.xml.
		*/

        return super.onOptionsItemSelected(item);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	  	super.onActivityResult(requestCode, resultCode, data);
		//ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
		//ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	private void xatSesion() {
		QBAuth.createSession(new QBEntityCallback<QBSession>() {
			@Override
			public void onSuccess(QBSession result, Bundle params) {
				App.getInstance().log("Sessió de XAT creada");
			}

			@Override
			public void onError(QBResponseException e) {

			}
		});
	}
}
