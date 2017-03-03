package com.pinmyballs;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import com.pinmyballs.metier.Tournoi;
import com.pinmyballs.service.base.BaseTournoiService;
import com.pinmyballs.utils.ListeTournoiAdapter;
import com.pinmyballs.utils.LocationUtil;
import com.pinmyballs.utils.MyLocation;
import com.pinmyballs.utils.MyLocation.LocationResult;

public class PageListeResultatTournois extends ActionBarActivity {

	MyLocation myLocation = new MyLocation();

	double latitude = 0;
	double longitude = 0;

	ArrayList<Tournoi> listeTournoi = new ArrayList<Tournoi>();

	public final static String INTENT_TOURNOI_POUR_INFO = "com.pinmyballs.PageListeResultatTournois.INTENT_TOURNOI_POUR_INFO";

	EditText adresseUtilisateurTV = null;
	ImageButton boutonClearAdresse = null;
	ImageButton boutonLocalisation = null;
	ListView listeFlipperView = null;

	ActionBar mActionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_liste_resultat_tournoi);

		mActionBar = getSupportActionBar();
		mActionBar.setTitle(R.string.headerTournoi);
		mActionBar.setIcon(R.drawable.header_icon_tournoi);
		adresseUtilisateurTV = (EditText) findViewById(R.id.champAdresseLocalisation);
		adresseUtilisateurTV.setHint(R.string.hintRechercheAdresse);
		adresseUtilisateurTV.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		boutonClearAdresse = (ImageButton) findViewById(R.id.boutonClearAdresse);
		boutonLocalisation = (ImageButton) findViewById(R.id.boutonLocalisation);

		listeFlipperView = (ListView) findViewById(R.id.listeResultatsTournoi);

		boutonClearAdresse.setOnClickListener(ClearAdresseClickListener);
		boutonLocalisation.setOnClickListener(BoutonLocalisationListener);

		adresseUtilisateurTV.setOnEditorActionListener(ClickNewSearch);

		localiseTelephone();

		rafraichitListeTournoi();

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

        if (!myLocation.checkLocationPermission(this)) {
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
			new AlertDialog.Builder(PageListeResultatTournois.this)
				.setTitle("Argh!")
				.setMessage(
						"Votre adresse n'a pas pu être trouvée! Rappuyez sur le bouton de localisation, ou entrez votre adresse manuellement. Si le problème persiste, contactez moi :)")
				.setNeutralButton("Fermer", null).setIcon(R.drawable.tete_martiens).show();
		}
	}

	private void rafraichitListeTournoi() {
		if (latitude == 0 && longitude == 0) {
			return;
		}
		// Récupère la liste des flippers les plus proches
		BaseTournoiService rechercheService = new BaseTournoiService();

		listeTournoi = rechercheService.getAllTournoi(getApplicationContext());

		ListeTournoiAdapter customAdapter = new ListeTournoiAdapter(this, R.layout.simple_list_item_flipper, listeTournoi, latitude, longitude);

		listeFlipperView.setAdapter(customAdapter);

		if (listeTournoi.size() == 0) {
			new AlertDialog.Builder(this).setTitle("Argh!")
				.setMessage("Aucun tournoi à venir...")
				.setNeutralButton("Fermer", null).setIcon(R.drawable.tete_martiens).show();
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
				rafraichitListeTournoi();
			} else {
				new AlertDialog.Builder(PageListeResultatTournois.this)
					.setTitle("Argh!")
					.setMessage(
							"Votre adresse n'a pas pu être trouvée! Veuillez activer le GPS et la connexion Wifi sur votre téléphone. Si le problème persiste, contactez moi :)")
					.setNeutralButton("Fermer", null).setIcon(R.drawable.tete_martiens).show();
			}
			return false;
		}
	};

	private OnClickListener ClearAdresseClickListener = new OnClickListener() {
		public void onClick(View v) {
			adresseUtilisateurTV.setText("");
		}
	};


	private OnClickListener BoutonLocalisationListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			localiseTelephone();
			rafraichitListeTournoi();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
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
