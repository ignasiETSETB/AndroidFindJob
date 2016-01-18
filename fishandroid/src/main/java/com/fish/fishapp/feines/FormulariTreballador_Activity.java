package com.fish.fishapp.feines;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.fish.fishapp.App;
import com.fish.fishapp.R;
import com.fish.fishapp.Usuari;
import com.fish.fishapp.utils.ServerException;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.List;

public class FormulariTreballador_Activity extends Activity {

    final Calendar c = Calendar.getInstance();

    ProgressDialog progressDialog;
    ProgressDialog ringProgressDialog;
    ScrollView scrollForm;
    Drawable originalDrawable;

    ParseObject registreFeina;
    String idRegistreFeina = "";

    private static EditText nom;
    private static EditText cognom1;
    private static EditText cognom2;
    private static Spinner tipusIdentificacio;
    private static String tipusIdentificacioString;
    private static EditText document;
    private static Spinner nacionalitat;
    private static String nacionalitatString;
    private static EditText sigla;
    private static EditText carrer;
    private static EditText numero;
    private static EditText porta;
    private static EditText codiPostal;
    private static EditText poblacio;
    private static Spinner municipi;
    private static String municipiString;
    private static Spinner provincia;
    private static String provinciaString;
    private static Spinner pais;
    private static String paisString;
    private static EditText  telefon;
    private static EditText  numeroSS;
    private static EditText  email;
    private static EditText  dataNaixement;
    private static Spinner sexe;
    private static String sexeString;
    private static Spinner  nivellFormatiu;
    private static String nivellFormatiuString;
    private static EditText  iban;
    private static EditText  swift;

    String idJobsHiring = "";
    String priceHour = "";
    String dateFinish = "";
    String dateStart = "";
    String jobOffered = "";
    String fromUser = "";

    Resources res;

    @Override
    public void onResume(){

        super.onResume();
    }

    @Override
    public void onStart(){

        super.onStart();
    }

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

        setContentView(R.layout.formulari_treballador);

        // Habilitem el botó per retrocedir de pantalla
        getActionBar().setDisplayHomeAsUpEnabled(true);

        idRegistreFeina = getIntent().getStringExtra("id_registre_feina");


        if (getIntent().getStringExtra("idJobsHiring") != null){
            idJobsHiring = getIntent().getStringExtra("idJobsHiring");
        }
        if (getIntent().getStringExtra("priceHour") != null) {
            priceHour = getIntent().getStringExtra("priceHour");
        }
        if (getIntent().getStringExtra("dateFinish") != null){
            dateFinish = getIntent().getStringExtra("dateFinish");
        }
        if (getIntent().getStringExtra("dateStart") != null){
            dateStart = getIntent().getStringExtra("dateStart");
        }
        if (getIntent().getStringExtra("jobOffered") != null){
            jobOffered = getIntent().getStringExtra("jobOffered");
        }
        if (getIntent().getStringExtra("fromUser") != null){
            fromUser = getIntent().getStringExtra("fromUser");
        }




        res = getResources();


        init();

    }

    /**
     * Inicialitzar components formulari
     */
    public void init() {

        scrollForm = (ScrollView) findViewById(R.id.scrollForm);

        // Camps del formulari
        nom = (EditText) findViewById(R.id.nom_treballador);
        cognom1 = (EditText) findViewById(R.id.cognom1_treballador);
        cognom2 = (EditText) findViewById(R.id.cognom2_treballador);
        tipusIdentificacio = (Spinner) findViewById(R.id.tipus_identificacio_treballador);
        document = (EditText) findViewById(R.id.identificacio_treballador);
        nacionalitat = (Spinner) findViewById(R.id.nacionalitat_treballador);
        sigla = (EditText) findViewById(R.id.sigla_treballador);
        carrer = (EditText) findViewById(R.id.carrer_treballador);
        numero = (EditText) findViewById(R.id.numero_treballador);
        porta = (EditText) findViewById(R.id.porta_treballador);
        codiPostal = (EditText) findViewById(R.id.codi_postal_treballador);
        poblacio = (EditText) findViewById(R.id.poblacio_treballador);
        municipi = (Spinner) findViewById(R.id.municipi_treballador);
        provincia = (Spinner) findViewById(R.id.provincia_treballador);
        pais = (Spinner) findViewById(R.id.pais_treballador);
        telefon = (EditText) findViewById(R.id.telefon_treballador);
        numeroSS = (EditText) findViewById(R.id.cotitzacio_treballador);
        email = (EditText) findViewById(R.id.email_treballador);
        sexe = (Spinner) findViewById(R.id.sexe_treballador);
        nivellFormatiu = (Spinner) findViewById(R.id.nivell_formatiu_treballador);
        swift = (EditText) findViewById(R.id.swift_treballador);
        iban = (EditText) findViewById(R.id.iban_treballador);
        telefon = (EditText) findViewById(R.id.telefon_treballador);


        dataNaixement = (EditText) findViewById(R.id.data_naixement_treballador);
        dataNaixement.setFocusable(false);
        dataNaixement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialogDataFi(v);
            }

        });

        originalDrawable = nom.getBackground();

        progressDialog = new ProgressDialog(this);


        // SPINNER TIPUS IDENTIFICACIÓ
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.form_tipus_identificacio_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tipusIdentificacio.setAdapter(adapter);
        tipusIdentificacio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipusIdentificacioString = getResources().getStringArray(R.array.form_tipus_identificacio_array_values)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        // SPINNER MUNICIPI
        ArrayAdapter<CharSequence> adapterMunicipi = ArrayAdapter.createFromResource(
                this, R.array.form_municipis_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        municipi.setAdapter(adapterMunicipi);
        municipi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                municipiString = getResources().getStringArray(R.array.form_municipis_array_values)[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // SPINNER PROVINCIA
        ArrayAdapter<CharSequence> adapterProvincia = ArrayAdapter.createFromResource(
                this, R.array.form_provincies_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        provincia.setAdapter(adapterProvincia);
        provincia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                provinciaString = getResources().getStringArray(R.array.form_provincies_array_values)[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // SPINNER PAIS
        ArrayAdapter<CharSequence> adapterPais = ArrayAdapter.createFromResource(
                this, R.array.form_paisos_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        pais.setAdapter(adapterPais);
        pais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                paisString = getResources().getStringArray(R.array.form_paisos_array_values)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // SPINNER NACIONALITAT
        nacionalitat.setAdapter(adapterPais);
        nacionalitat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nacionalitatString = getResources().getStringArray(R.array.form_paisos_array_values)[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // SPINNER SEXE
        ArrayAdapter<CharSequence> adapterSexe = ArrayAdapter.createFromResource(
                this, R.array.list_of_genders,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sexe.setAdapter(adapterSexe);
        sexe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sexeString = getResources().getStringArray(R.array.list_of_genders)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // SPINNER NIVELL FORMATIU
        ArrayAdapter<CharSequence> adapterNivellFormatiu = ArrayAdapter.createFromResource(
                this, R.array.form_nivells_formatius_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        nivellFormatiu.setAdapter(adapterNivellFormatiu);
        nivellFormatiu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nivellFormatiuString = getResources().getStringArray(R.array.form_nivells_formatius_array_values)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });




    }



    public void clickCancelar(View view) {
        finish();
    }


    public void clickAcceptar(View view) {

        App.getInstance().log(" Botó enviar formulari del treballador");
        boolean campsCorrectes = true;

        // Comprovar camps obligatoris

        // LLOC TREBALL

        if (nom.getText().length() == 0){
            campsCorrectes = false;
            nom.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, nom.getTop());
        } else {
            nom.setBackground(originalDrawable);
        }

        if (cognom1.getText().length() == 0){
            campsCorrectes = false;
            cognom1.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, cognom1.getTop());
        } else {
            cognom1.setBackground(originalDrawable);
        }

        if (cognom2.getText().length() == 0){
            campsCorrectes = false;
            cognom2.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, cognom2.getTop());
        } else {
            cognom2.setBackground(originalDrawable);
        }

        if (tipusIdentificacioString.length() == 0){
            campsCorrectes = false;
            tipusIdentificacio.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, tipusIdentificacio.getTop());
        } else {
            tipusIdentificacio.setBackground(originalDrawable);
        }

        if (document.getText().length() == 0){
            campsCorrectes = false;
            document.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, document.getTop());
        } else {
            document.setBackground(originalDrawable);
        }


        if (nacionalitatString.length() == 0){
            campsCorrectes = false;
            nacionalitat.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, nacionalitat.getTop());
        } else {
            nacionalitat.setBackground(originalDrawable);
        }


        if (sigla.getText().length() == 0){
            campsCorrectes = false;
            sigla.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, sigla.getTop());
        } else {
            sigla.setBackground(originalDrawable);
        }

        if (carrer.getText().length() == 0){
            campsCorrectes = false;
            carrer.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, carrer.getTop());
        } else {
            carrer.setBackground(originalDrawable);
        }

        if (numero.getText().length() == 0){
            campsCorrectes = false;
            numero.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, numero.getTop());
        } else {
            numero.setBackground(originalDrawable);
        }

        if (porta.getText().length() == 0){
            campsCorrectes = false;
            porta.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, porta.getTop());
        } else {
            porta.setBackground(originalDrawable);
        }

        if (codiPostal.getText().length() < 5){
            campsCorrectes = false;
            codiPostal.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, codiPostal.getTop());
        } else {
            codiPostal.setBackground(originalDrawable);
        }

        if (poblacio.getText().length() == 0){
            campsCorrectes = false;
            poblacio.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, poblacio.getTop());
        } else {
            poblacio.setBackground(originalDrawable);
        }

        if (municipiString.length() == 0){
            campsCorrectes = false;
            municipi.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, municipi.getTop());
        } else {
            municipi.setBackground(originalDrawable);
        }

        if (provinciaString.length() == 0){
            campsCorrectes = false;
            provincia.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, provincia.getTop());
        } else {
            provincia.setBackground(originalDrawable);
        }

        if (paisString.length() == 0){
            campsCorrectes = false;
            pais.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, pais.getTop());
        } else {
            pais.setBackground(originalDrawable);
        }

        if (telefon.getText().length() == 0){
            campsCorrectes = false;
            telefon.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, telefon.getTop());
        } else {
            telefon.setBackground(originalDrawable);
        }

        if (numeroSS.getText().length() == 0){
            campsCorrectes = false;
            numeroSS.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, numeroSS.getTop());
        } else {
            numeroSS.setBackground(originalDrawable);
        }

        if (email.getText().length() == 0){
            campsCorrectes = false;
            email.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, email.getTop());
        } else {
            email.setBackground(originalDrawable);
        }

        if (dataNaixement.getText().length() == 0){
            campsCorrectes = false;
            dataNaixement.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, dataNaixement.getTop());
        } else {
            dataNaixement.setBackground(originalDrawable);
        }

        if (sexeString.length() == 0){
            campsCorrectes = false;
            sexe.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, sexe.getTop());
        } else {
            sexe.setBackground(originalDrawable);
        }

        if (nivellFormatiuString.length() == 0){
            campsCorrectes = false;
            nivellFormatiu.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, nivellFormatiu.getTop());
        } else {
            nivellFormatiu.setBackground(originalDrawable);
        }

        // Alfanuméric de 24 a 34
        if (iban != null && iban.getText().length() < 24){
            campsCorrectes = false;
            iban.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, iban.getTop());
        } else {
            iban.setBackground(originalDrawable);
        }

        // Alfanuméric de 8 a 11
        if (swift != null && swift.getText().length() < 8){
            campsCorrectes = false;
            swift.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, swift.getTop());
        } else {
            swift.setBackground(originalDrawable);
        }



        if (campsCorrectes) {
            // Presentem la barra de progrés
            ringProgressDialog = ProgressDialog.show(this, "", App.getInstance().getStringResource(R.string.uploading), true, false);
            ringProgressDialog.setCancelable(true);

            // Guardem les dades actualitzades del contracte

            new Thread(new Runnable() {

                @Override
                public void run() {

                    try {

                        Usuari usr = App.getInstance().usuari;
                        FormulariTreballador ft = new FormulariTreballador();
                        ft.setHirerUserId(fromUser);
                        ft.setJobOffered(jobOffered);


                        ft.setNom(nom.getText().toString());
                        ft.setCognom1(cognom1.getText().toString());
                        ft.setCognom2(cognom2.getText().toString());
                        ft.setTipusIdentificacio(tipusIdentificacioString);
                        ft.setNacionalitat(nacionalitatString);
                        ft.setSigla(sigla.getText().toString());
                        ft.setCarrer(carrer.getText().toString());
                        ft.setNumero(numero.getText().toString());
                        ft.setPorta(porta.getText().toString());
                        ft.setCodiPostal(codiPostal.getText().toString());
                        ft.setPoblacio(poblacio.getText().toString());
                        ft.setMunicipi(municipiString);
                        ft.setProvincia(provinciaString);
                        ft.setPais(paisString);
                        ft.setTelefon(telefon.getText().toString());
                        ft.setNumeroSS(numeroSS.getText().toString());
                        ft.setEmail(email.getText().toString());
                        ft.setDataNaixement(dataNaixement.getText().toString());
                        ft.setSexe(sexeString);
                        ft.setNivellFormatiu(nivellFormatiuString);
                        ft.setIban(iban.getText().toString());
                        ft.setSwift(swift.getText().toString());
                        ft.setIdRegistreFeina(idJobsHiring);

                        //ft.setRegistreFeina(registreFeina);


                        ft.update();

                        finish();

                    } catch (Exception e) {

                    }

                    ringProgressDialog.dismiss();
                }

            }).start();
        } else {
            Toast.makeText(this, "Hay campos incompletos o incorrectos", Toast.LENGTH_LONG).show();
        }


    }





    // DATEPICKER

    // NAIXEMENT
    public static class datePickerNaixement extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String ceroDia = "";
            String ceroMes = "";
            if (day < 10) {
                ceroDia = "0";
            }

            if ( (month + 1) < 10) {
                ceroMes = "0";
            }

            dataNaixement.setText( ceroDia + day + "/" +  ceroMes + (month + 1) + "/" + year);
        }
    }
    public void showDatePickerDialogDataFi(View v) {
        DialogFragment newFragment = new datePickerNaixement();
        newFragment.show(getFragmentManager(), "datePicker");
    }


}
