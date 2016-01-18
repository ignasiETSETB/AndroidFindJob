package com.fish.fishapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.fish.fishapp.feines.FeinesLlistat_Activity;
import com.fish.fishapp.utils.Server;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

public class Login_Activity extends Activity {

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
	protected void onCreate(Bundle savedInstanceState) {

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

	}

	public void facebookLogin(View view){

		/*
		List<String> permissions = Arrays.asList(Permissions.User.EMAIL,
				ParseFacebookUtils.Permissions.User.ABOUT_ME,
				ParseFacebookUtils.Permissions.User.BIRTHDAY,
				ParseFacebookUtils.Permissions.User.HOMETOWN,
				ParseFacebookUtils.Permissions.User.PHOTOS);
				
		ParseUser.logOut();
		
		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {

*/

		ParseUser.logOut();

		List<String> permissions = Arrays.asList("email", "user_about_me", "user_birthday", "user_hometown", "user_photos");

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
		ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
		//ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}
}
