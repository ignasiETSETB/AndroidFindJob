package com.fish.fishapp.feines;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.fish.fishapp.Ajuda_Activity;
import com.fish.fishapp.App;
import com.fish.fishapp.Condicions_Activity;
import com.fish.fishapp.PerfilEditar_Activity;
import com.fish.fishapp.R;
import com.fish.fishapp.contact.Xat_Contacte;
import com.fish.fishapp.contact.Xat_Llista_Activity;
import com.fish.fishapp.contact.Xat_Missatge;
import com.fish.fishapp.notificacions.LlistaNotificacions_Activity;
import com.fish.fishapp.utils.MyCallback;
import com.fish.fishapp.utils.Server;
import com.fish.fishapp.utils.ServerException;
import com.fish.fishapp.utils.Utils;
import com.fish.fishapp.workerprofiles.WorkerProfilesActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FeinesLlistat_Activity extends Activity {

	ArrayList<Job> arrayListJob;
	JobAdapter adapter;
    LinearLayout linearLayoutWorkerProfileTags;
    ArrayList<String> tagList;
    EditText editTextNewTag;
    private Boolean exit = false;

    Activity self;
    SharedPreferences prefs;

    private ListView listViewJobs;
    private ProgressDialog pd = null;
    private Object data = null;
    private View linearLayoutSearchSettings;
    private EditText editTextSearch;
    private Button buttonBuscar2;
    //private EditText editTextPriceSearch;
	//private Spinner spinnerGenderSearch;
	private LinearLayout linearLayoutSearch;
	private TextView textViewNoHayResultados;
    private ScrollView scrollViewSearch;
	private ArrayAdapter<String> textSearchAdapter;
	private QueryJobsParameters queryJobsParameters = new QueryJobsParameters();
	private ListView mListView;
	private Adapter mAdapter;
	private List<String> mMain = new ArrayList<String>();
	private Context context = this;
	private Menu menu;

    private ArrayList<Xat_Contacte> arrayContactes = new ArrayList<>();
    PacketCollector collector;
    Thread threadPacketCollector;

    private boolean pantallaFavorits = false;
    public static boolean serverDownload = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

        App.getInstance().log("*");
        App.getInstance().log("************** Iniciem la classe '" + this.getClass().getSimpleName() + "' **************");
        App.getInstance().log("*");

        super.onCreate(savedInstanceState);

		setContentView(R.layout.feines_llistat);
        context = this;

        if (getIntent().getBooleanExtra("EXIT", false)) {
            App.getInstance().log("LOGOUT");
            this.finish();
        } else {

            // Amaguem el botó per retrocedir de pantalla

            getActionBar().setDisplayHomeAsUpEnabled(false);

            // Amaguem el teclat

            this.getWindow().setSoftInputMode(0);

            // Configurem la presentació dels elements de la vista
            App.getInstance().log("Configurem presentacio feines llistat");
            linearLayoutSearchSettings = this.findViewById(R.id.linearLayoutSearchSettings);
            linearLayoutSearchSettings.setVisibility(View.GONE);
            editTextSearch = (EditText) this.findViewById(R.id.editTextSearch);
            buttonBuscar2 = (Button) this.findViewById(R.id.buttonBuscar2);
            //editTextPriceSearch  = (EditText) this.findViewById(R.id.editTextPriceSearch);
            //spinnerGenderSearch = (Spinner)  this.findViewById(R.id.spinnerGenderSearch);
            //spinnerGenderSearch.setSelection(2);
            textViewNoHayResultados = (TextView) this.findViewById(R.id.textViewNoHayResultados);
            textViewNoHayResultados.setVisibility(View.GONE);
            linearLayoutSearch = (LinearLayout) this.findViewById(R.id.linearLayoutSearch);
            tagList = new ArrayList<String>();
            editTextNewTag = (EditText) this.findViewById(R.id.editTextNewTag);
            linearLayoutWorkerProfileTags = (LinearLayout) this.findViewById(R.id.linearLayoutWorkerProfileTags);
            scrollViewSearch = (ScrollView) this.findViewById(R.id.scrollViewSearch);

            //updateTags();

            editTextSearch.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    Server.getJobsSearchWords(s.toString(), new MyCallback() {

                        @Override
                        public void done(ArrayList object, Exception e) {

                            //Elimino

                            linearLayoutSearch.removeAllViews();

                            for (Object anObject : object) {

                                //creo un text i li poso listener
                                TextView rowTextView = new TextView(FeinesLlistat_Activity.this);
                                rowTextView.setText(anObject.toString());
                                rowTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                                rowTextView.setClickable(true);

                                rowTextView.setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {

                                        TextView tv = (TextView) v;
                                        editTextSearch.setText(tv.getText());
                                        linearLayoutSearch.removeAllViews();

                                    }
                                });

                                linearLayoutSearch.addView(rowTextView);
                            }
                        }

                        @Override
                        public void done(String object, Exception e) {

                        }
                    });
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


            buttonBuscar2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    App.getInstance().log("Amaguem els criteris de cerca i iniciem la cerca");
                    linearLayoutSearchSettings.setVisibility(View.GONE);
                    doSearch();
                }
            });

            // Comprovar si el llista completa o de favorits
            try {
                Bundle b = getIntent().getExtras();
                pantallaFavorits = b.getBoolean("pantallaFavorits");
                App.getInstance().log("PANTALLA FAVORITS");

            } catch (Exception e) {
                App.getInstance().log("PANTALLA TOTES FEINES");
            }


            // Presentem la barra de progrès mentre es carreguen les feines

            this.pd = ProgressDialog.show(this, "", "", true, false);

            // Iniciem un thread pere descarregar la llista de feines

            queryJobsParameters = new QueryJobsParameters();

            //new DownloadTask().execute(queryJobsParameters);

            downloadT(queryJobsParameters);


            /*
            // Iniciem la connexió amb el xat
            App.getInstance().log("Inici conexio amb el chat");
            if (App.getInstance().connection == null || !App.getInstance().connection.isConnected()) {
                App.getInstance().connectarXMPP();
            }
            createPacketCollector();
            arrayContactes = Utils.carregarContactesSharedPreferences(context);
            comprovarMissatgesPendents();
        */
        }
	}



    @Override
    public void onDestroy(){

        App.getInstance().log("*");
        App.getInstance().log("************** Tanquem la classe '" + this.getClass().getSimpleName() + "' **************");
        App.getInstance().log("*");

        //App.getInstance().connection.disconnect();
        destroyPacketCollector();
        super.onDestroy();

        App.getInstance()._jobsActivity = null;
    }

    public void downloadT(QueryJobsParameters... args) {
        ArrayList<Job> resultObject = new ArrayList<>();
        App.getInstance().log("************************************************************************************");
        App.getInstance().log("*                  Detall de les dades per la cerca de feines                      *");
        App.getInstance().log("************************************************************************************");

        App.getInstance().log("----------------- Text:              " + args[0].texto);
        App.getInstance().log("----------------- Preu/hora:         " + args[0].precioHora);
        App.getInstance().log("----------------- Requisits:         " + args[0].requisitos);
        //App.getInstance().log("----------------- Sexe:              " + Utils.ObtenirSexeNom(args[0].sexo) + " (" + args[0].sexo + ")");

        App.getInstance().log("************************************************************************************");

        App.getInstance().log("Fem la consulta a Quickblox per obtenir la llista de feines disponibles, segons el filtre donat...");

        try {
            // Llista completa
            if (!pantallaFavorits) {
                App.getInstance().log("Fem la query");
                resultObject = Server.queryJobs(args[0], null);


                App.getInstance().log("Després de la query"+resultObject.toString());
                // Llista favorits
            } else {
                //resultObject = Server.queryJobsFavorits(args[0], null);
            }

        } catch (ServerException e) {
            App.getInstance().log("Error al intentar obtenir la llista de feines disponibles: " + e.getMessage());
            e.printStackTrace();
            App.getInstance().missatgeEnPantalla(App.getInstance().getStringResource(R.string.server_error));
        }


        Handler handler = new Handler();
        App.getInstance().log("esperem 2 s");
        final ArrayList<Job> finalResultObject = resultObject;
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds

                if (FeinesLlistat_Activity.this.pd != null) {
                    App.getInstance().log("********** resultobject");
                    ArrayList<Job> arr = finalResultObject;
                    FeinesLlistat_Activity.this.arrayListJob = finalResultObject;
                    App.getInstance().log("Total feines obtingudes: " + arr.size());

                    // Si no tenim feines, presentem el títol "No hay resultados", si no, l'amaguem
                    if (arr.size() == 0){
                        textViewNoHayResultados.setVisibility(View.VISIBLE);
                    } else {
                        textViewNoHayResultados.setVisibility(View.GONE);
                    }

                    // Carreguem les dades de les feines obtingudes a la vista 'activity_item_job', i omplim la llista
                    adapter = new JobAdapter(FeinesLlistat_Activity.this, R.layout.activity_item_job, arrayListJob.toArray(new Job[arrayListJob.size()]));
                    listViewJobs = (ListView) findViewById(R.id.listViewJobs);
                    listViewJobs.setAdapter(adapter);

                    // L'usuari ha seleccionat una feina del llistat mostrat
                    listViewJobs.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // Obtenim les dades de la feina seleccionada
                            Job  itemJob = (Job) listViewJobs.getItemAtPosition(position);

                            App.getInstance().log("Nom del candidat seleccionat: " + itemJob.nombre);
                            App.getInstance().log("Id del candidat seleccionat: " + itemJob.ObjectId);
                            App.getInstance().log("UserId candidat seleccionat: " + itemJob.userId);

                            // Presentem el detall de la feina seleccionada
                            Intent intent = new Intent(FeinesLlistat_Activity.this, FeinesDetall_Activity.class);

                            // Informem al sistema del ID de la feina seleccionada
                            Bundle b = new Bundle();
                            b.putString("JobId", itemJob.ObjectId);
                            b.putString("nombre",itemJob.nombre);
                            b.putString("jobOffered", itemJob.tags);
                            b.putInt("priceHour", itemJob.precioHora);
                            b.putString("edad",itemJob.edad);
                            b.putString("ciudad",itemJob.ciudad);
                            b.putInt("imageInt",itemJob.imageInt);
                            b.putString("userid",itemJob.userId);
                            intent.putExtras(b);
                            startActivity(intent);
                        }
                    });

                    FeinesLlistat_Activity.this.pd.dismiss();
                }
            }
        }, 2000);
    }

    public boolean getServerDownload() {
        return this.serverDownload;
    }

    public void setServerDownload(boolean data) {
        this.serverDownload = data;
    }

    @Override
    protected void onResume() {
        App.getInstance().log("ONRESUME FEINES LLISTAT ACTIVITY");
        /*
        createPacketCollector();
        arrayContactes = Utils.carregarContactesSharedPreferences(context);
        comprovarMissatgesPendents();
        */
        super.onResume();

    }


    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Carreguem el menú en el formulari
		getMenuInflater().inflate(R.menu.mnu_main, menu);
		this.menu = menu;
        comprovarMissatgesPendents();
        if (pantallaFavorits){
            getActionBar().setTitle("Favoritos");
            //menu.getItem(R.id.action_favoritos).getActionView().setVisibility(View.INVISIBLE);
        }
        return super.onCreateOptionsMenu(menu);

	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

        // Comprovem l'opció seleccionada del menú

        switch (item.getItemId()) {

            case R.id.action_buscar:

                if (linearLayoutSearchSettings.getVisibility() == View.VISIBLE){
                    App.getInstance().log("Amaguem els criteris de cerca i iniciem la cerca");
                    linearLayoutSearchSettings.setVisibility(View.GONE);
                    doSearch();
                    // HIDE KEYBOARD -- NOT WORKING
                    View view = this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                } else {
                    App.getInstance().log("Visualitzem els criteris de cerca");
                    linearLayoutSearchSettings.setVisibility(View.VISIBLE);
                }

                return true;

            case R.id.action_notificaciones:

                App.getInstance().log("Opció notificacions seleccionada");
                /*
                Intent intentNotificacions = new Intent(App.getInstance().appContext, LlistaNotificacions_Activity.class);
                startActivity(intentNotificacions);
                */
                return true;

            case R.id.action_chat:

                App.getInstance().log("Opció xat seleccionada");
                menu.findItem(R.id.action_chat).setIcon(getResources().getDrawable(R.drawable.ic_action_chat));
                /*
                // Intent intent_chat = new Intent(App.getInstance().appContext, ChatListActivity.class);

                // Obrim el xat amb el nom de la persona seleccionada per iniciar el xat

                Intent intent_chat = new Intent(App.getInstance().appContext, Xat_Llista_Activity.class);
                destroyPacketCollector();

                // Assignem l'usuari i el nom del contacte per iniciar el xat i recuperar la conversa anterior, si existeix

                intent_chat.putExtra("usuari_contacte", "test");
                //intent_chat.putExtra("nom_contacte", job.nombre);
                intent_chat.putExtra("nom_contacte", "Remitente");
                startActivity(intent_chat);
                */
                return true;

            case R.id.action_trabajo:

                App.getInstance().log("Opció perfil del treballador seleccionada");
                //Intent intent_trabajo = new Intent(App.getInstance().appContext, FeinesLlistat_Activity.class);
                Intent intent_trabajos = new Intent(App.getInstance().appContext, WorkerProfilesActivity.class);
                startActivity(intent_trabajos);

                return true;

            case R.id.action_favoritos:

                if (!pantallaFavorits) {
                    App.getInstance().log("Opció favorits seleccionada");
                    /*
                    Intent intent_favorits = new Intent(App.getInstance().appContext, FeinesLlistat_Activity.class);
                    intent_favorits.putExtra("pantallaFavorits", true);
                    intent_favorits.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                            | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    startActivity(intent_favorits);
                    */
                }
                return true;

            case R.id.action_perfil:

                App.getInstance().log("Opció perfil de l'usuari seleccionada");
                /*
                Intent intent_perfil = new Intent(App.getInstance().appContext, PerfilEditar_Activity.class);

                startActivity(intent_perfil);
                */
                return true;

            case R.id.action_condiciones:

                App.getInstance().log("Opció Condicions seleccionada");
                /*
                Intent intentCondicions = new Intent(App.getInstance().appContext, Condicions_Activity.class);
                startActivity(intentCondicions);
                */
                return true;

            case R.id.action_ayuda:

                App.getInstance().log("Opció Ajuda seleccionada");
                /*
                Intent intentAjuda = new Intent(App.getInstance().appContext, Ajuda_Activity.class);
                startActivity(intentAjuda);
                */
                return true;
            /*
            case R.id.action_refresh:

                // Show the ProgressDialog on this thread
                this.pd = ProgressDialog.show(this, "", App.getInstance().getStringResource(R.string.downloading), true, false);
                // Start a new thread that will download all the data
                new DownloadTask().execute(queryJobsParameters);
                return true;
            */
            default:

                return super.onOptionsItemSelected(item);
        }
	}


	public void doSearch() {

        // Amaguem els criteris de cerca

		linearLayoutSearchSettings.setVisibility(View.GONE);

        // Amaguem el teclat
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Presentem la barra de progrès
        this.pd = ProgressDialog.show(this, "", App.getInstance().getStringResource(R.string.downloading), true, false);

        // Assignem els criteris per la cerca de feines
		queryJobsParameters = new QueryJobsParameters();

		if (editTextSearch.getText().length() > 0){
			queryJobsParameters.texto = editTextSearch.getText().toString();
		}

		Integer precio_h = 0;

        /*
        if (editTextPriceSearch.getText().length() > 0) {
            try {
                precio_h = Integer.parseInt(editTextPriceSearch.getText().toString());

            } catch (Exception e) {
                App.getInstance().log("Error en el preu/hora donat per la cerca: " + e.getMessage());
                e.printStackTrace();
            }
        }

		if (precio_h > 0){
            queryJobsParameters.precioHora = precio_h;
        }

		if (tagList.size() > 0){
            queryJobsParameters.requisitos = tagList;
        }

		queryJobsParameters.sexo = Utils.ObtenirSexeValor(spinnerGenderSearch.getSelectedItem().toString());
*/
        // Llencem la cerca

        //new DownloadTask().execute(queryJobsParameters);
        downloadT(queryJobsParameters);
	}

/*
	private void updateTags(){

		View tagLine;
		TextView valor;
		ImageButton botoEliminar;
		Integer i;

		linearLayoutWorkerProfileTags.removeAllViews();

		if (tagList != null){

			for (i = 0; i < tagList.size(); i++){

				LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				tagLine = inflater.inflate(R.layout.tag_item,null);
				valor = (TextView) tagLine.findViewById(R.id.textViewDescription);
				valor.setText(tagList.get(i));
				botoEliminar = (ImageButton) tagLine.findViewById(R.id.imageButton1);
				botoEliminar.setTag(i);
				botoEliminar.setOnClickListener(new Button.OnClickListener(){

					@Override
					public void onClick(View v) {
						App.getInstance().log("Eliminem l'etiqueta :" + v.getTag().toString());
						tagList.remove(Integer.parseInt(v.getTag().toString()));
						updateTags();
					}
				});

				linearLayoutWorkerProfileTags.addView(tagLine);
			}

            // Ens posicionem al final de la llista perquè es pugui veure el botó reset
            scrollViewSearch.fullScroll(scrollViewSearch.FOCUS_DOWN);
		}
	}
*/

	/*
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
*/

    public void clickReset(View view){

        // Buidem els valors anterior
        if (editTextSearch != null) editTextSearch.setText("");
        /*
        if (editTextPriceSearch != null) editTextPriceSearch.setText("");

        // Per defecte, el sexe és "Cualquiera"
        spinnerGenderSearch.setSelection(2);

        // Buidem la llista d'etiquetes
        if (tagList != null) {
            tagList.clear();
            updateTags();
        }
        */
    }




    // XMPP
    ///////////////////////////////////

    private void createPacketCollector(){

        threadPacketCollector = new Thread(new Runnable(){

            @Override
            public void run() {

                    collector = App.getInstance().connection.createPacketCollector(new AndFilter());
                    while (true) {
                        Packet packet = collector.nextResult();
                        if (packet instanceof Message) {
                            Message message = (Message) packet;
                            if (message != null && message.getBody() != null) {
                                App.getInstance().log("FEINES - MISSATGE REBUT " + packet.getFrom() + " : " + (message != null ? message.getBody() : "NULL"));


                                // Guardar missatge a Prefs

                                String usuariDesti = packet.getFrom().split("/")[0];
                                String nomDesti = ((Message) packet).getSubject();

                                try {
                                    // GET NOM
                                } catch (Exception e) {

                                }

                                Xat_Contacte contacte = new Xat_Contacte();
                                contacte.setNomContacte(nomDesti);
                                contacte.setUsuari(usuariDesti);
                                contacte.setMissatgePendent("1");

                                arrayContactes = Utils.afegirContacte(arrayContactes, contacte, context);
                                prefs = PreferenceManager.getDefaultSharedPreferences(context);


                                // Carregar array de missatges

                                Gson gsonMissatges = new Gson();
                                String jsonMissatges = prefs.getString(usuariDesti, "");
                                Type typeMissatges = new TypeToken<ArrayList<Xat_Missatge>>() {
                                }.getType();

                                ArrayList<Xat_Missatge> arrayListMissatges = gsonMissatges.fromJson(jsonMissatges, typeMissatges);

                                ArrayList<Xat_Missatge> arrayMissatges;

                                if (arrayListMissatges != null) {
                                    arrayMissatges = arrayListMissatges;
                                } else {
                                    arrayMissatges = new ArrayList<>();
                                }

                                App.getInstance().log("Missatges a la conversa amb l'usuari de destí '" + usuariDesti + "' = " + arrayMissatges);

                                Xat_Missatge missatgeRebut = new Xat_Missatge();
                                missatgeRebut.setBody(((Message) packet).getBody());
                                missatgeRebut.setRemitent(((Message) packet).getSubject());
                                arrayMissatges.add(missatgeRebut);


                                // Guardar les prefs amb el missatge rebut
                                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                                SharedPreferences.Editor editor = sharedPrefs.edit();
                                Gson gson = new Gson();
                                String jsonArrayMissatges = gson.toJson(arrayMissatges);
                                editor.putString(usuariDesti, jsonArrayMissatges);
                                editor.commit();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        menu.findItem(R.id.action_chat).setIcon(getResources().getDrawable(R.drawable.ic_action_chat_red));
                                        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                                        // Vibrate for 500 milliseconds
                                        v.vibrate(400);
                                    }
                                });

                            }
                        }
                    }

            }
        });

        threadPacketCollector.start();
    }


    private void destroyPacketCollector(){
        App.getInstance().log("DESTROY PACKET COLLECTOR (FEINES)");
        try {
            threadPacketCollector.interrupt();
        } catch (Exception e){
            App.getInstance().log("ERROR Thread: " + e.toString());
        }

        try{
            App.getInstance().connection.removePacketCollector(collector);
        } catch (Exception e) {
            App.getInstance().log("ERROR PacketCollector: " + e.toString());
        }
    }


    private void comprovarMissatgesPendents(){

        boolean missatgesPendents = false;
        for (Xat_Contacte contacte:arrayContactes) {
            if (contacte.getMissatgePendent().length() > 0){
                App.getInstance().log("Missatges Pendents!");
                missatgesPendents = true;
            }

        }

        if (missatgesPendents) {
            try {
                menu.findItem(R.id.action_chat).setIcon(getResources().getDrawable(R.drawable.ic_action_chat_red));
            } catch (Exception e) {
                App.getInstance().log("ERROR: comprobarMissatgesPendents: " + e.toString());
            }
        } else {
            try {
                menu.findItem(R.id.action_chat).setIcon(getResources().getDrawable(R.drawable.ic_action_chat));
            } catch (Exception e) {
                App.getInstance().log("ERROR: comprobarMissatgesPendents: " + e.toString());
            }
        }
    }


    @Override
    public void onBackPressed() {

        if (exit) {
            finish(); // finish activity
        } else {
            App.getInstance().missatgeEnPantalla(getResources().getString(R.string.press_again_to_exit));
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }

    }

 }
