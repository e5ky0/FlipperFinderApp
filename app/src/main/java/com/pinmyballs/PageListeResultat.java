package com.pinmyballs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import com.pinmyballs.metier.Flipper;
import com.pinmyballs.service.base.BaseFlipperService;
import com.pinmyballs.service.base.BaseModeleService;
import com.pinmyballs.utils.ListeFlipperAdapter;
import com.pinmyballs.utils.LocationUtil;
import com.pinmyballs.utils.MyLocation;
import com.pinmyballs.utils.MyLocation.LocationResult;

public class PageListeResultat extends Activity {

    MyLocation myLocation = new MyLocation();

    double latitude = 0;
    double longitude = 0;
    int DISTANCE_MAX = 25; // On cherche les flippers à 10km à la ronde
    int ENSEIGNE_LIST_MAX_SIZE = 50; // On cherche les flippers à 5km à la ronde

    ArrayList<Flipper> listeFlipper = new ArrayList<Flipper>();

    public final static String INTENT_FLIPPER_LIST_POUR_MAP = "com.pinmyballs.PageListeResultat.INTENT_FLIPPER_LIST_POUR_MAP";
    public final static String INTENT_LATITUDE = "com.pinmyballs.PageListeResultat.INTENT_LATITUDE";
    public final static String INTENT_LONGITUDE = "com.pinmyballs.PageListeResultat.INTENT_LONGITUDE";

    EditText adresseUtilisateurTV = null;
    ImageButton boutonClearAdresse = null;
    ImageButton boutonClearModeleFlipper = null;
    ImageButton boutonLocalisation = null;
    ImageButton boutonSearchByMap = null;
    ListView listeFlipperView = null;
    ImageView boutonAfficheCarte = null;
    AutoCompleteTextView champModeleFlipper = null;

    int isGPSAvailable = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_liste_resultat);

        SharedPreferences settings;

        settings = getSharedPreferences(PagePreferences.PREFERENCES_FILENAME, 0);
        DISTANCE_MAX = settings.getInt(PagePreferences.KEY_PREFERENCES_RAYON, PagePreferences.DEFAULT_VALUE_RAYON);

        ENSEIGNE_LIST_MAX_SIZE = settings.getInt(PagePreferences.KEY_PREFERENCES_MAX_RESULT,
                PagePreferences.DEFAULT_VALUE_NB_MAX_LISTE);

        adresseUtilisateurTV = (EditText) findViewById(R.id.champAdresseLocalisation);
        adresseUtilisateurTV.setHint(R.string.hintRechercheAdresse);
        adresseUtilisateurTV.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        boutonClearAdresse = (ImageButton) findViewById(R.id.boutonClearAdresse);
        boutonClearModeleFlipper = (ImageButton) findViewById(R.id.boutonClearModeleFlipper);
        boutonAfficheCarte = (ImageView) findViewById(R.id.afficherCarteIcon);
        boutonLocalisation = (ImageButton) findViewById(R.id.boutonLocalisation);
        boutonSearchByMap = (ImageButton) findViewById(R.id.boutonSearchByMap) ;
        champModeleFlipper = (AutoCompleteTextView) findViewById(R.id.autocompletionModeleFlipper);

        listeFlipperView = (ListView) findViewById(R.id.listeResultats);

        boutonClearAdresse.setOnClickListener(ClearAdresseClickListener);
        boutonLocalisation.setOnClickListener(BoutonLocalisationListener);
        boutonSearchByMap.setOnClickListener(BoutonSearchByMapListener);
        boutonAfficheCarte.setOnClickListener(AfficherListeListener);


        boutonClearModeleFlipper.setOnClickListener(ClearModeleFlipperClickListener);

        adresseUtilisateurTV.setOnEditorActionListener(ClickNewSearch);

        // Initialisation de l'autocomplétion
        BaseModeleService modeleFlipperService = new BaseModeleService();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                modeleFlipperService.getAllNomModeleFlipper(getApplicationContext()));
        champModeleFlipper.setAdapter(adapter);
        champModeleFlipper.setImeOptions(EditorInfo.IME_ACTION_DONE);
        champModeleFlipper.setDropDownAnchor(R.id.autocompletionModeleFlipper);
        champModeleFlipper.setOnItemClickListener(itemModeleSelectionneListener);
        champModeleFlipper.setOnEditorActionListener(valideModeleFlipper);

        isGPSAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());


        Intent intent = getIntent();

        if (intent != null) {
            Bundle extra = intent.getExtras();
            if (extra != null) {
                latitude = Double.parseDouble(intent.getStringExtra(PageCarteSearch.INTENT_LATITUDE));
                longitude = Double.parseDouble(intent.getStringExtra(PageCarteSearch.INTENT_LONGITUDE));
                Log.w(PageListeResultat.class.getName(), "FROM MAP SEARCH -> Lat , Long " + latitude + ", " + longitude);
            }

                else {
                localiseTelephone();
                Log.w(PageListeResultat.class.getName(), "Default Start : use GPS");
            }
        }

        rafraichitListeFlipper();
    }

    private void localiseTelephone() {
        LocationResult locationResult = new LocationResult() {
            @Override
            public void gotLocation(Location location) {
                if (location != null) {
                    // Méthode appelée lorsque la localisation a fonctionné
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        };

        if (!MyLocation.checkLocationPermission(this)) {
            return;
        }

        myLocation.getLocation(this, locationResult);

        // On commence par récupèrer la dernière location connue du téléphone,
        // et on remplit le champ
        // Adresse avec.
        Location locationCourante = LocationUtil.getLastKnownLocation(this);
        adresseUtilisateurTV.setText("");
        if (locationCourante != null) {
            latitude = locationCourante.getLatitude();
            longitude = locationCourante.getLongitude();
            String addressText = LocationUtil.getAdresseFromCoordGPS(getApplicationContext(), latitude, longitude);
            if (addressText == null || addressText.length() == 0) {
                addressText = "Votre lieu actuel";
            }
            adresseUtilisateurTV.setText(addressText);
        } else {
            if (isGPSAvailable != ConnectionResult.SUCCESS) {
                ////EasyTracker.getTracker().sendEvent("ui_error", "GPS_ERROR", "localiseTelephone", 0L);
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isGPSAvailable, this, 0);
                if (dialog != null) {
                    dialog.show();
                }
            } else {
                new AlertDialog.Builder(PageListeResultat.this)
                    .setTitle("Argh!")
                    .setMessage(
                            "Votre adresse n'a pas pu être trouvée! Rappuyez sur le bouton de localisation, ou entrez votre adresse manuellement. Si le problème persiste, contactez moi :)")
                    .setNeutralButton("Fermer", null).setIcon(R.drawable.tete_martiens).show();
            }
        }
    }

    private void rafraichitListeFlipper() {
        if (latitude == 0 && longitude == 0) {
            boutonAfficheCarte.setVisibility(View.INVISIBLE);
            return;
        }
        // Récupère la liste des flippers les plus proches
        BaseFlipperService rechercheService = new BaseFlipperService();

        listeFlipper = rechercheService.rechercheFlipper(getApplicationContext(), latitude, longitude,
                DISTANCE_MAX * 1000, ENSEIGNE_LIST_MAX_SIZE, champModeleFlipper.getText().toString());

        ListeFlipperAdapter customAdapter = new ListeFlipperAdapter(this, R.layout.simple_list_item_flipper, listeFlipper, latitude, longitude);


        listeFlipperView.setAdapter(customAdapter);

        if (listeFlipper.size() == 0) {
            if (champModeleFlipper.getText() == null || champModeleFlipper.getText().length() == 0) {
                new AlertDialog.Builder(this).setTitle("Argh!")
                    .setMessage("Pas un seul flipper à " + String.valueOf(DISTANCE_MAX) + "km à la ronde!")
                    .setNeutralButton("Fermer", null).setIcon(R.drawable.tete_martiens).show();
            } else {
                new AlertDialog.Builder(this)
                    .setTitle("Argh!")
                    .setMessage(
                            "Le flipper recherché n'a pas été trouvé à " + String.valueOf(DISTANCE_MAX)
                            + "km à la ronde!").setNeutralButton("Fermer", null)
                    .setIcon(R.drawable.tete_martiens).show();
            }
            boutonAfficheCarte.setVisibility(View.INVISIBLE);
        } else {
            boutonAfficheCarte.setVisibility(View.VISIBLE);
        }

    }

    private OnEditorActionListener ClickNewSearch = new OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (event == null || event.getAction() != KeyEvent.ACTION_DOWN) {
                return false;
            }
            LatLng adresseRecherchee = LocationUtil.getAddressFromText(getApplicationContext(), adresseUtilisateurTV
                    .getText().toString(), latitude, longitude);
            if (adresseRecherchee != null) {
                latitude = adresseRecherchee.latitude;
                longitude = adresseRecherchee.longitude;

                // TODO Trouver une façon propre d'afficher un choix quand il y
                // a plusieurs adresses trouvées
                adresseUtilisateurTV.setText(LocationUtil.getAdresseFromCoordGPS(getApplicationContext(), latitude,
                            longitude));
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                rafraichitListeFlipper();
            } else {
                if (isGPSAvailable != ConnectionResult.SUCCESS) {
                    //EasyTracker.getTracker().sendEvent("ui_error", "GPS_ERROR", "ClickNewSearch", 0L);
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isGPSAvailable, PageListeResultat.this, 0);
                    if (dialog != null) {
                        dialog.show();
                    }
                } else {
                    new AlertDialog.Builder(PageListeResultat.this)
                        .setTitle("Argh!")
                        .setMessage(
                                "Votre adresse n'a pas pu être trouvée! Veuillez activer le GPS et la connexion Wifi sur votre téléphone. Si le problème persiste, contactez moi :)")
                        .setNeutralButton("Fermer", null).setIcon(R.drawable.tete_martiens).show();
                }
            }
            return false;
        }
    };

    private OnClickListener ClearAdresseClickListener = new OnClickListener() {
        public void onClick(View v) {
            adresseUtilisateurTV.setText("");
        }
    };

    private OnClickListener ClearModeleFlipperClickListener = new OnClickListener() {
        public void onClick(View v) {
            champModeleFlipper.setText("");
            rafraichitListeFlipper();
        }
    };

    private OnClickListener AfficherListeListener = new OnClickListener() {
        public void onClick(View v) {
            //EasyTracker.getTracker().sendEvent("ui_action", "button_press", "carte_button", 0L);
            Intent mapActivite = new Intent(PageListeResultat.this, PageCarteFlipper.class);
            mapActivite.putExtra(INTENT_FLIPPER_LIST_POUR_MAP, listeFlipper);
            startActivity(mapActivite);
        }
    };

    private OnClickListener BoutonLocalisationListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            localiseTelephone();
            rafraichitListeFlipper();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    };

    private OnClickListener BoutonSearchByMapListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent mapSearch = new Intent(PageListeResultat.this, PageCarteSearch.class);
            //Si Localisation Telephone a marché, on récupère la position sinon on met des valeurs par défaut
            Double[] Position ={latitude, longitude};
            if (longitude == 0) {
                Position[0] = 48.859274;
                Position[1] = 2.294438;
            }
            mapSearch.putExtra(INTENT_LATITUDE, String.valueOf(Position[0]));
            mapSearch.putExtra(INTENT_LONGITUDE, String.valueOf(Position[1]));
            startActivity(mapSearch);
        }
    };


    private OnItemClickListener itemModeleSelectionneListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(champModeleFlipper.getWindowToken(), 0);
            rafraichitListeFlipper();
        }
    };

    private OnEditorActionListener valideModeleFlipper = new OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(champModeleFlipper.getWindowToken(), 0);
            rafraichitListeFlipper();
            return false;
        }
    };


    @Override
    public void onPause() {
        super.onPause();
        // Pour éviter que ça crash si l'utiisateur quitte l'appli alors que la
        // localisation n'est pas
        // terminée
        myLocation.cancelTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        rafraichitListeFlipper();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Google Analytics
        //EasyTracker.getInstance().activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Google Analytics
        //EasyTracker.getInstance().activityStop(this);
    }

}
