package com.pinmyballs;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.pinmyballs.database.DAOBase;
import com.pinmyballs.database.dao.FlipperDAO;
import com.pinmyballs.service.GlobalService;
import com.pinmyballs.service.base.BaseFlipperService;
import com.pinmyballs.service.parse.ParseFlipperService;

public class PagePreferences extends AppCompatActivity {

	public static final String KEY_PREFERENCES_RAYON = "rayonRecherche";
	public static final String KEY_PREFERENCES_MAX_RESULT = "listeMaxResult";
	public static final String KEY_PSEUDO_FULL = "fullPseudo";
	public static final String KEY_PREFERENCES_DATE_LAST_UPDATE = "dateLastUpdate";
	public static final String KEY_PREFERENCES_DATABASE_VERSION = "";
	public static final String PREFERENCES_FILENAME = "FlipperLocPrefs.txt";

	public static final int DEFAULT_VALUE_RAYON = 100;
	public static final int DEFAULT_VALUE_NB_MAX_LISTE = 50;

	SeekBar seekBarRayon;
	SeekBar seekBarNbMaxListe;

	TextView tvRayon;
	TextView tvNbMaxListe;

	TextView pseudo;
	TextView datedernieremaj;
	TextView nbflips;


	ActionBar mActionbar;
	SharedPreferences settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_preferences);


		// Affichage du header
		mActionbar = getSupportActionBar();

		mActionbar.setTitle(R.string.headerPreferences);
        settings = getSharedPreferences(PagePreferences.PREFERENCES_FILENAME, 0);

		seekBarRayon = (SeekBar) findViewById(R.id.seekBarRayon);
		seekBarNbMaxListe = (SeekBar) findViewById(R.id.seekBarNbMax);

		tvRayon = (TextView) findViewById(R.id.TVRayon);
		tvNbMaxListe = (TextView) findViewById(R.id.TVMaxResult);

		pseudo = (TextView) findViewById(R.id.pseudo);
		pseudo.setText(settings.getString(PagePreferences.KEY_PSEUDO_FULL, "non défini"));
		datedernieremaj = (TextView) findViewById(R.id.datedernieremaj);
        datedernieremaj.setText(settings.getString(PagePreferences.KEY_PREFERENCES_DATE_LAST_UPDATE, ""));
		nbflips =(TextView) findViewById(R.id.nbflips);

		GlobalService globalService = new GlobalService();
		nbflips.setText(globalService.getNbFlips(getApplicationContext()));

		settings = getSharedPreferences(PREFERENCES_FILENAME, 0);


		int rayon = settings.getInt(KEY_PREFERENCES_RAYON, DEFAULT_VALUE_RAYON);
		seekBarRayon.setProgress(rayon);
		tvRayon.setText(String.valueOf(rayon) + " km");

		int listeMaxResult = settings.getInt(KEY_PREFERENCES_MAX_RESULT, DEFAULT_VALUE_NB_MAX_LISTE);
		tvNbMaxListe.setText(String.valueOf(listeMaxResult));
		seekBarNbMaxListe.setProgress(listeMaxResult);

		seekBarRayon.setOnSeekBarChangeListener(rayonChangeListener);

		seekBarNbMaxListe.setOnSeekBarChangeListener(nbMaxListeChangeListener);

	}

	private OnSeekBarChangeListener rayonChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			tvRayon.setText(String.valueOf(progress) + " km");
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// Mettre à jour la valeur dans le fichier
			Editor editor = settings.edit();
			editor.putInt(KEY_PREFERENCES_RAYON, seekBar.getProgress());
			editor.commit();
		}
	};

	private OnSeekBarChangeListener nbMaxListeChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			tvNbMaxListe.setText(String.valueOf(progress));
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// Mettre à jour la valeur dans le fichier
			Editor editor = settings.edit();
			editor.putInt(KEY_PREFERENCES_MAX_RESULT, seekBar.getProgress());
			editor.commit();
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
