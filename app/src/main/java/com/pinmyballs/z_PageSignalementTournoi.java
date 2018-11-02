package com.pinmyballs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.pinmyballs.fragment.DatePickerFragment;
import com.pinmyballs.metier.Tournoi;
import com.pinmyballs.service.ParseFactory;
import com.pinmyballs.utils.LocationUtil;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class z_PageSignalementTournoi extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = z_PageSignalementTournoi.class.getSimpleName();
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public final static String INTENT_LATITUDE = "com.pinmyballs.z_PageSignalementTournoi.INTENT_LATITUDE";
    public final static String INTENT_LONGITUDE = "com.pinmyballs.z_PageSignalementTournoi.INTENT_LONGITUDE";
    public final static String INTENT_ADDRESSTEXT = "com.pinmyballs.z_PageSignalementTournoi.INTENT_ADDRESSTEXT";

    SimpleDateFormat dateTarget = new SimpleDateFormat("yyyy/MM/dd");
    SimpleDateFormat dateSource = new SimpleDateFormat("dd/MM/yyyy");

    @BindView(R.id.NewTournoiNom)
    TextView NewTournoiNom;
    @BindView(R.id.NewTournoiDate)
    TextView NewTournoiDate;
    @BindView(R.id.NewTournoiURL)
    TextView NewTournoiURL;
    @BindView(R.id.NewTournoiEnseigne)
    TextView NewTournoiEnseigne;
    @BindView(R.id.NewTournoiAdresse)
    TextView NewTournoiAdresse;
    @BindView(R.id.NewTournoiCP)
    TextView NewTournoiCP;
    @BindView(R.id.NewTournoiVille)
    TextView NewTournoiVille;
    @BindView(R.id.NewTournoiPays)
    TextView NewTournoiPays;
    @BindView(R.id.picDateButton)
    ImageButton picDateButton;
    @BindView(R.id.NewTournoiButtonSearchAdress)
    ImageButton NewTournoiButtonSearchAdress;
    @BindView(R.id.NewTournoiButtonCheckAddress)
    ImageButton NewTournoiButtonCheckAdress;

    @BindView(R.id.NewTournoiAnnuler)
    Button NewTournoiAnnuler;
    @BindView(R.id.NewTournoiSuivant)
    Button NewTournoiSuivant;

    Tournoi newTournoi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_signalement_tournoi);
        ButterKnife.bind(this);
        NewTournoiDate.setKeyListener(null);

    }

    @OnClick(R.id.picDateButton)
    public void datePicker(View view) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), "date");
    }

    @OnClick(R.id.NewTournoiButtonSearchAdress)
    public void usePlaceAutocomplete() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @OnClick(R.id.NewTournoiButtonCheckAddress)
    public void showEnteredAddress() {

        if (NewTournoiAdresse.getText().length() == 0 && NewTournoiVille.getText().length() == 0 && NewTournoiCP.getText().length() == 0) {
            new AlertDialog.Builder(z_PageSignalementTournoi.this)
                    .setTitle("Erreur")
                    .setMessage(
                            "Vous devez remplir les champs Adresse, Code Postal et Ville pour localiser le tournoi sur la carte")
                    .setNeutralButton("Fermer", null).setIcon(R.drawable.ic_delete).show();
            return;
        }

        Intent intentWithLatLng = new Intent(z_PageSignalementTournoi.this, PopMap.class);
        String adresseComplete = String.format("%s %s %s %s",
                NewTournoiAdresse.getText(),
                NewTournoiCP.getText(),
                NewTournoiVille.getText(),
                NewTournoiPays.getText());
        Double latitude = LocationUtil.getAddressFromText(getApplicationContext(), adresseComplete).latitude;
        Double longitude = LocationUtil.getAddressFromText(getApplicationContext(), adresseComplete).longitude;

        intentWithLatLng.putExtra(INTENT_LATITUDE, latitude);
        intentWithLatLng.putExtra(INTENT_LONGITUDE, longitude);
        intentWithLatLng.putExtra(INTENT_ADDRESSTEXT, adresseComplete);
        startActivity(intentWithLatLng);
    }

    @OnClick(R.id.NewTournoiAnnuler)
    public void annuler() {
        NavUtils.navigateUpFromSameTask(z_PageSignalementTournoi.this);
        Log.d(TAG, NewTournoiNom.getText().toString());
    }

    @OnClick(R.id.NewTournoiSuivant)
    public void suivant() {
        if (FormNotCompleted()) {
            Log.d(TAG, "Form incomplete");
            return;
        }
        Date dateDuJour = new Date();
        long tournoiID = dateDuJour.getTime();

        String DateDDMMYYYY = NewTournoiDate.getText().toString();
        String DateYYYYMMDD = "";
        try {
            DateYYYYMMDD = dateTarget.format(dateSource.parse(DateDDMMYYYY));
            Log.d(TAG,"Date transformed" + DateYYYYMMDD);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        if (DateYYYYMMDD.equals("")){
            return;
        }

        //Make Tournoi Object
        newTournoi = new Tournoi();
        newTournoi.setId(tournoiID);
        newTournoi.setNom(NewTournoiNom.getText().toString());
        newTournoi.setUrl(NewTournoiURL.getText().toString());
        newTournoi.setDate(DateYYYYMMDD);
        newTournoi.setEns(NewTournoiEnseigne.getText().toString());
        newTournoi.setAdresse(NewTournoiAdresse.getText().toString());
        newTournoi.setCodePostal(NewTournoiCP.getText().toString());
        newTournoi.setVille(NewTournoiVille.getText().toString());
        newTournoi.setPays(NewTournoiPays.getText().toString());
        //TODO AJOUTER UN CHAMP COMMENTAIRE DANS LE FORMULAIRE
        newTournoi.setCommentaire("");

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocationName(newTournoi.getAdresseComplete(), 1);
            if (addresses.size() != 0) {
                Log.d(TAG, "Address found: " + addresses.get(0).getLocality());
                LatLng latlng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                newTournoi.setLatitude(String.valueOf(latlng.latitude));
                newTournoi.setLongitude(String.valueOf(latlng.longitude));
                Log.d(TAG, "Coordinates found: " + newTournoi.getLatitude() + ", " + newTournoi.getLongitude());
            } else {
                new AlertDialog.Builder(z_PageSignalementTournoi.this)
                        .setTitle("Adresse non reconnue")
                        .setMessage(
                                "Votre adresse n'a pas pu être trouvée! Vérifiez les coordonnées du tournoi. Si le problème persiste, contactez moi par email.")
                        .setNeutralButton("Fermer", null).setIcon(R.drawable.ic_delete).show();
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG,String.valueOf(newTournoi.getId()));
        Log.d(TAG,newTournoi.getNom());
        Log.d(TAG,newTournoi.getDate());
        Log.d(TAG, newTournoi.getUrl());
        Log.d(TAG, newTournoi.getCommentaire());
        Log.d(TAG, newTournoi.getEns());
        Log.d(TAG, newTournoi.getAdresse());
        Log.d(TAG, newTournoi.getCodePostal());
        Log.d(TAG, newTournoi.getVille());
        Log.d(TAG, newTournoi.getPays());
        Log.d(TAG, newTournoi.getLatitude());
        Log.d(TAG, newTournoi.getLongitude());



        envoyer(newTournoi);
    }


    public void envoyer(Tournoi tournoi){
        Toast toast = Toast.makeText(getApplicationContext(), "Envoi en cours", Toast.LENGTH_SHORT);
        toast.show();
        ParseFactory parseFactory = new ParseFactory();
        //creation de l'objet à envoyer
        ParseObject Tournoi = parseFactory.getParseObject(tournoi);
        //Tournoi.saveInBackground();


        Tournoi.saveInBackground( new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast toast = Toast.makeText(getApplicationContext(), "Tournoi enregistré, rafraichir la page pour le voir apparaitre dans la liste )", Toast.LENGTH_LONG);
                toast.show();
                //close activity z_PageSignalementTournoi
                finish();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                NewTournoiEnseigne.setText(place.getName());

                HashMap HM = LocationUtil.getDetailsfromLatLng(this, place.getLatLng());
                NewTournoiAdresse.setText(String.valueOf(HM.get("address")));
                NewTournoiCP.setText(String.valueOf(HM.get("postalcode")));
                NewTournoiVille.setText(String.valueOf(HM.get("city")));
                NewTournoiPays.setText(String.valueOf(HM.get("country")));

                Log.d(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public boolean FormNotCompleted() {
        boolean notcomplete = false;
        if (NewTournoiNom.getText().length() == 0) {
            new AlertDialog.Builder(z_PageSignalementTournoi.this).setTitle("Envoi impossible!").setMessage("Vous devez renseigner le nom du tournoi.").setNeutralButton("Fermer", null).setIcon(R.drawable.ic_delete).show();
            notcomplete = true;
        } else if (NewTournoiDate.getText().length() == 0) {
            new AlertDialog.Builder(z_PageSignalementTournoi.this).setTitle("Envoi impossible!").setMessage("Vous devez renseigner la date du tournoi.").setNeutralButton("Fermer", null).setIcon(R.drawable.ic_delete).show();
            notcomplete = true;
        } else if (NewTournoiEnseigne.getText().length() == 0) {
            new AlertDialog.Builder(z_PageSignalementTournoi.this).setTitle("Envoi impossible!").setMessage("Vous devez renseigner le nom de l'enseigne accueillant le tournoi.").setNeutralButton("Fermer", null).setIcon(R.drawable.ic_delete).show();
            notcomplete = true;
        } else if (NewTournoiAdresse.getText().length() == 0 || NewTournoiCP.getText().length() == 0 || NewTournoiVille.getText().length() == 0) {
            new AlertDialog.Builder(z_PageSignalementTournoi.this).setTitle("Envoi impossible!").setMessage("Vous devez renseigner tous les champs de l'adresse.").setNeutralButton("Fermer", null).setIcon(R.drawable.ic_delete).show();
            notcomplete = true;
        }
        return notcomplete;
    }

    /**
     * To receive a callback when the user sets the date.
     *
     * @param view
     * @param year
     * @param month
     * @param day
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar cal = new GregorianCalendar(year, month, day);
        setDate(cal);
    }

    /**
     * To set date on TextView
     *
     * @param calendar
     */
    private void setDate(final Calendar calendar) {
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE);
        ((TextView) findViewById(R.id.NewTournoiDate)).setText(dateFormat.format(calendar.getTime()));
    }

}
