package com.pinmyballs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.pinmyballs.database.FlipperDatabaseHandler;
import com.pinmyballs.utils.MyLocation;
import com.pinmyballs.utils.MyLocation.LocationResult;
import com.pinmyballs.utils.NetworkUtil;

public class MainActivity extends AppCompatActivity {

	Button boutonRechercher;
	Button boutonSignaler;
	Button boutonRechercherTournoi;
	SharedPreferences settings;

	ImageView imageEnveloppe;
	ImageView imagePreferences;
	ImageView mImageBleue;
	ImageView mImageGrise;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		boutonRechercher = (Button) findViewById(R.id.boutonMenuRechercher);
		boutonRechercher.setOnClickListener(RechercherListener);
		boutonRechercherTournoi = (Button) findViewById(R.id.boutonMenuTournoi);
		boutonRechercherTournoi.setOnClickListener(RechercherTournoiListener);
		boutonSignaler = (Button) findViewById(R.id.boutonMenuSignaler);
		boutonSignaler.setOnClickListener(SignalerListener);

		imageEnveloppe = (ImageView) findViewById(R.id.imageEnveloppe);
		imageEnveloppe.setOnClickListener(ContactPrincipalListener);

		imagePreferences = (ImageView) findViewById(R.id.imagePreferences);
		imagePreferences.setOnClickListener(PreferencesListener);

		settings = getSharedPreferences(PagePreferences.PREFERENCES_FILENAME, 0);
		checkIfMajNeeded();

		 ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 1);
	}

	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            MyLocation myLocation = new MyLocation();
            LocationResult locationResult = new LocationResult(){
                @Override
                public void gotLocation(Location location){
                    if (location != null){
                        // Méthode appelée lorsque la localisation a fonctionné
                    }
                }
            };
            myLocation.getLocation(this, locationResult);
        }
    }

	private void checkIfMajNeeded(){
		String dateDerniereMajString = settings.getString(PagePreferences.KEY_PREFERENCES_DATE_LAST_UPDATE, FlipperDatabaseHandler.DATABASE_DATE_MAJ);
		int nbJours = 0;
		try {
			Date dateDerniereMaj = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).parse(dateDerniereMajString);
			nbJours = Days.daysBetween(new DateTime(dateDerniereMaj), new DateTime(new Date())).getDays();
			if (nbJours > 5){
				new AlertDialog.Builder(this).setTitle(R.string.dialogMajDBNeededTitle)
					.setMessage(getResources().getString(R.string.dialogMajDBNeeded, nbJours)).setPositiveButton(R.string.dialogMajDBNeededOK, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							updateDB();
						}
					}).setNegativeButton(R.string.dialogMajDBLater, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).show();			}
		} catch (ParseException e) {
		}
	}

	private OnClickListener ContactPrincipalListener = new OnClickListener() {
		public void onClick(View v) {
			Resources resources = getResources();
			String emailsTo = resources.getString(R.string.mailContact);
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/html");

			i.putExtra(Intent.EXTRA_EMAIL, new String[]{emailsTo});
			i.putExtra(Intent.EXTRA_SUBJECT, "Commentaire sur l'application");
			try {
				startActivity(Intent.createChooser(i, "Envoi du mail"));
			} catch (android.content.ActivityNotFoundException ex) {
				new AlertDialog.Builder(MainActivity.this).setTitle("Envoi impossible!").setMessage("Vous n'avez pas de mail configuré sur votre téléphone.").setNeutralButton("Fermer", null).setIcon(R.drawable.ic_tristesse).show();
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.action_update:
				updateDB();
				return true;
			case R.id.action_viewComment:
				////EasyTracker.getTracker().sendEvent("ui_action", "button_press", "viewComment", 0L);
				Intent intent = new Intent(MainActivity.this, PageListeCommentaire.class);
				startActivity(intent);
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void updateDB(){
		////EasyTracker.getTracker().sendEvent("ui_action", "button_press", "maj_database", 0L);
		if (NetworkUtil.isConnected(getApplicationContext())){
			if (NetworkUtil.isConnectedFast(getApplicationContext())){
				new AsyncTaskMajDatabase(MainActivity.this, settings).execute();
			}else{
				Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.toastMajPasPossibleTropLent), Toast.LENGTH_SHORT);
				toast.show();
			}
		}else{
			Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.toastMajPasPossible), Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	private OnClickListener RechercherListener = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(MainActivity.this, PageListeResultat.class);
			startActivity(intent);
		}
	};
	private OnClickListener SignalerListener = new OnClickListener() {
		public void onClick(View v) {
			//Intent intent = new Intent(MainActivity.this, PageSignalementNew.class);
			Intent intent = new Intent(MainActivity.this, PageSignalement.class);
			startActivity(intent);
		}
	};
	private OnClickListener RechercherTournoiListener = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(MainActivity.this, PageListeResultatTournois.class);
			startActivity(intent);
		}
	};

	private OnClickListener PreferencesListener = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(MainActivity.this, PagePreferences.class);
			startActivity(intent);
		}
	};

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
