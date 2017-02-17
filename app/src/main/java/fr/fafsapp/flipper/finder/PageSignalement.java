package fr.fafsapp.flipper.finder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import fr.fafsapp.flipper.finder.service.base.BaseModeleService;
import fr.fafsapp.flipper.finder.utils.LocationUtil;
import fr.fafsapp.flipper.finder.utils.MyLocation;
import fr.fafsapp.flipper.finder.utils.MyLocation.LocationResult;

public class PageSignalement extends ActionBarActivity {

	MyLocation myLocation = new MyLocation();
	double latitude = 48.862731; // bar à côté du cirque d'hiver
	double longitude = 2.367354; // bar à côté du cirque d'hiver

	EditText adresseUtilisateurTV = null;
	EditText champNomEnseigne = null;
	ImageButton boutonClear = null;
	Button boutonEnvoi = null;
	ImageButton boutonLocalisation = null;
	AutoCompleteTextView champModeleFlipper = null;

	ActionBar mActionbar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_signalement);

		// Affichage du header
		mActionbar = getSupportActionBar();
		mActionbar.setTitle(R.string.headerSignalement);

		adresseUtilisateurTV = (EditText)findViewById(R.id.champAdresseLocalisation);
		champNomEnseigne = (EditText)findViewById(R.id.champNomEnseigne);
		champModeleFlipper = (AutoCompleteTextView)findViewById(R.id.autocompletionModeleFlipper);
		boutonClear = (ImageButton) findViewById(R.id.boutonClearAdresse);
		boutonEnvoi = (Button) findViewById(R.id.BoutonEnvoiInfosFlipper);
		boutonLocalisation = (ImageButton) findViewById(R.id.boutonLocalisation);


		champModeleFlipper.setHint(R.string.SectionFlipper);
		champNomEnseigne.setHint(R.string.SectionNomEnseigne);
		adresseUtilisateurTV.setHint(R.string.hintSoumettreAdresse);
		adresseUtilisateurTV.setImeOptions(EditorInfo.IME_ACTION_DONE);
		boutonClear.setOnClickListener(ClearClickListener);
		boutonEnvoi.setOnClickListener(EnvoyerClickListener);
		boutonLocalisation.setOnClickListener(BoutonLocalisationListener);

		BaseModeleService modeleFlipperService = new BaseModeleService();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, modeleFlipperService.getAllNomModeleFlipper(getApplicationContext()));
		champModeleFlipper.setAdapter(adapter);
		champModeleFlipper.setImeOptions(EditorInfo.IME_ACTION_DONE);
		champModeleFlipper.setDropDownAnchor(R.id.autocompletionModeleFlipper);
		champModeleFlipper.setOnItemClickListener(itemSelectionneListener);

	}

	private OnItemClickListener itemSelectionneListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(champModeleFlipper.getWindowToken(), 0);
		}
	};

	private OnClickListener ClearClickListener = new OnClickListener() {
		public void onClick(View v) {
			adresseUtilisateurTV.setText("");
		}
	};

	private OnClickListener EnvoyerClickListener = new OnClickListener() {
		public void onClick(View v) {
			// On regarde d'abord si les champs sont renseignés
			boolean isError = false;
			if (adresseUtilisateurTV.getText().length() == 0){
				new AlertDialog.Builder(PageSignalement.this).setTitle("Envoi impossible!").setMessage("Vous devez renseigner l'adresse du flipper.").setNeutralButton("Fermer", null).setIcon(R.drawable.ic_delete).show();
				isError = true;
			}
			if (champModeleFlipper.getText().length() == 0){
				new AlertDialog.Builder(PageSignalement.this).setTitle("Envoi impossible!").setMessage("Vous devez renseigner le modèle du flipper.").setNeutralButton("Fermer", null).setIcon(R.drawable.ic_delete).show();
				isError = true;
			}

			if (champNomEnseigne.getText().length() == 0){
				new AlertDialog.Builder(PageSignalement.this).setTitle("Envoi impossible!").setMessage("Vous devez renseigner le nom de l'enseigne.").setNeutralButton("Fermer", null).setIcon(R.drawable.ic_delete).show();
				isError = true;
			}

			if (!isError){
				// C'est bon, on envoie le mail
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/html");
				Resources resources = getResources();
				String emailsTo = resources.getString(R.string.mailContact);

				i.putExtra(Intent.EXTRA_EMAIL, new String[]{emailsTo});
				i.putExtra(Intent.EXTRA_SUBJECT, "Nouveau Flipper");
				i.putExtra(Intent.EXTRA_TEXT, "Adresse : "+adresseUtilisateurTV.getText()+"\nModèle : "+champModeleFlipper.getText()+
						"\nNom de l'enseigne : " + champNomEnseigne.getText());
				try {
					startActivity(Intent.createChooser(i, "Envoi du mail"));
				} catch (android.content.ActivityNotFoundException ex) {
					new AlertDialog.Builder(PageSignalement.this).setTitle("Envoi impossible!").setMessage("Vous n'avez pas de mail configuré sur votre téléphone.").setNeutralButton("Fermer", null).setIcon(R.drawable.ic_tristesse).show();
				}
			}
		}
	};

	private OnClickListener BoutonLocalisationListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			localiseTelephone();
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
	};


	private void localiseTelephone(){
		LocationResult locationResult = new LocationResult(){
			@Override
			public void gotLocation(Location location){
				// Méthode appelée lorsque la localisation a fonctionné
				latitude = location.getLatitude();
				longitude = location.getLongitude();
			}
		};

		myLocation.getLocation(this, locationResult);

		// On commence par récupèrer la dernière location connue du téléphone, et on remplit le champ
		// Adresse avec.
		Location locationCourante =  LocationUtil.getLastKnownLocation(this);
		adresseUtilisateurTV.setText("");
		if (locationCourante != null){
			latitude = locationCourante.getLatitude();
			longitude = locationCourante.getLongitude();
			String addressText = LocationUtil.getAdresseFromCoordGPS(getApplicationContext(), latitude, longitude);
			if (addressText == null || addressText.length() == 0){
				new AlertDialog.Builder(PageSignalement.
						this).setTitle("Argh!").setMessage("Votre adresse n'a pas pu être trouvée! Veuillez activer le GPS ou entrer votre adresse.").setNeutralButton("Fermer", null).setIcon(R.drawable.tete_martiens).show();
			}
			adresseUtilisateurTV.setText(addressText);
		}else{
			new AlertDialog.Builder(PageSignalement.
					this).setTitle("Argh!").setMessage("Votre adresse n'a pas pu être trouvée! Veuillez activer le GPS et la connexion Wifi sur votre téléphone.").setNeutralButton("Fermer", null).setIcon(R.drawable.tete_martiens).show();
		}
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
