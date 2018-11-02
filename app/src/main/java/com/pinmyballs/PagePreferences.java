package com.pinmyballs;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.pinmyballs.database.FlipperDatabaseHandler;
import com.pinmyballs.service.GlobalService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PagePreferences extends AppCompatActivity {

	private static final String TAG = "PagePreferences";
	public static final String KEY_PSEUDO_FULL = "fullPseudo";
	public static final String KEY_PREFERENCES_RAYON = "rayonRecherche";
	public static final String KEY_PREFERENCES_MAX_RESULT = "listeMaxResult";
	public static final String KEY_PREFERENCES_DATE_LAST_UPDATE = "dateLastUpdate";
    public static final String KEY_PREFERENCES_DATABASE_VERSION = "databaseVersion";

    public static final String KEY_PREFERENCES_FAVORITE_LOCATION_LATITUDE = "FavLat";
    public static final String KEY_PREFERENCES_FAVORITE_LOCATION_LONGITUDE = "FavLng";

	public static final String KEY_PREFERENCES_CURRENT_LOCATION_LATITUDE = "CurrLat";
	public static final String KEY_PREFERENCES_CURRENT_LOCATION_LONGITUDE = "CurrLng";

	public static final String PREFERENCES_FILENAME = "FlipperLocPrefs.txt";

	public static final int DEFAULT_VALUE_RAYON = 100;
	public static final int DEFAULT_VALUE_NB_MAX_LISTE = 50;
	public static final String DEFAULT_VALUE_PSEUDO = "AAA";
    public static final String DEFAULT_VALUE_LATITUDE= "0";
    public static final String DEFAULT_VALUE_LONGITUDE= "0";


	@BindView(R.id.TVPseudoPref)
    EditText tvPseudo;
	@BindView(R.id.TVRayon)
	TextView tvRayon;
	@BindView(R.id.seekBarRayon)
	SeekBar seekBarRayon;
	@BindView(R.id.TVMaxResult)
	TextView tvNbMaxListe;
	@BindView(R.id.seekBarNbMax)
	SeekBar seekBarNbMaxListe;

	@BindView(R.id.currentlatlng)
	TextView currentLatLng;
	@BindView(R.id.dbchrono)
	TextView dbchrono;
	@BindView(R.id.datedernieremaj)
	TextView datedernieremaj;
	@BindView(R.id.nbflips)
	TextView nbflips;

	ActionBar mActionbar;
	SharedPreferences settings;

	@BindView(R.id.eraseDBbutton)
	Button EraseDB;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences);
		ButterKnife.bind(this);

		// Affichage du header
		mActionbar = getSupportActionBar();
		mActionbar.setTitle(R.string.headerPreferences);
		mActionbar.setHomeButtonEnabled(true);
		mActionbar.setDisplayHomeAsUpEnabled(true);

        setupPreferences();
	}

    /**
     * Responsible for loading and displaying Preferences and Info
     */
    private void setupPreferences(){
        settings = getSharedPreferences(PagePreferences.PREFERENCES_FILENAME, 0);
        tvPseudo.setText(settings.getString(PagePreferences.KEY_PSEUDO_FULL, DEFAULT_VALUE_PSEUDO));
        tvPseudo.addTextChangedListener(textChangedListener);

        int rayon = settings.getInt(KEY_PREFERENCES_RAYON, DEFAULT_VALUE_RAYON);
        seekBarRayon.setProgress(rayon);
		seekBarRayon.setOnSeekBarChangeListener(rayonChangeListener);
		Resources res =getResources();
        tvRayon.setText(String.format(res.getString(R.string.rayonmax), rayon));

        int listeMaxResult = settings.getInt(KEY_PREFERENCES_MAX_RESULT, DEFAULT_VALUE_NB_MAX_LISTE);
        seekBarNbMaxListe.setProgress(listeMaxResult);
		seekBarNbMaxListe.setOnSeekBarChangeListener(nbMaxListeChangeListener);
		tvNbMaxListe.setText(String.format(res.getString(R.string.listemax), listeMaxResult));

		currentLatLng.setText(String.format("(%s, %s)",settings.getString(KEY_PREFERENCES_CURRENT_LOCATION_LATITUDE,""), settings.getString(KEY_PREFERENCES_CURRENT_LOCATION_LONGITUDE,"")));

		dbchrono.setText(settings.getString(PagePreferences.KEY_PREFERENCES_DATABASE_VERSION,""));
        datedernieremaj.setText(settings.getString(PagePreferences.KEY_PREFERENCES_DATE_LAST_UPDATE, ""));
        nbflips.setText(new GlobalService().getNbFlips(getApplicationContext()));

    }

	private TextWatcher textChangedListener = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			Editor editor = settings.edit();
			editor.putString(PagePreferences.KEY_PSEUDO_FULL, s.toString());
			editor.apply();
			Log.d(TAG,"New pseudo: "+ s.toString());
		}
	};

	private OnSeekBarChangeListener rayonChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Resources res =getResources();
            tvRayon.setText(String.format(res.getString(R.string.rayonmax), progress));
        }

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			Editor editor = settings.edit();
			editor.putInt(KEY_PREFERENCES_RAYON, seekBar.getProgress());
			editor.apply();
		}
	};

	private OnSeekBarChangeListener nbMaxListeChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Resources res =getResources();
            tvNbMaxListe.setText(String.format(res.getString(R.string.listemax), progress));
        }

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			Editor editor = settings.edit();
			editor.putInt(KEY_PREFERENCES_MAX_RESULT, seekBar.getProgress());
			editor.apply();
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			onBackPressed();
			return  true;
		}
		return super.onOptionsItemSelected(item);
	}

	@OnClick(R.id.eraseDBbutton)
	public void EraseDB() {
		getApplicationContext().deleteDatabase(FlipperDatabaseHandler.FLIPPER_BASE_NAME);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PagePreferences.KEY_PREFERENCES_DATE_LAST_UPDATE, "");
		editor.putString(PagePreferences.KEY_PREFERENCES_DATABASE_VERSION, "0");
		editor.putString(PagePreferences.KEY_PSEUDO_FULL,DEFAULT_VALUE_PSEUDO);
		editor.putInt(PagePreferences.KEY_PREFERENCES_MAX_RESULT,DEFAULT_VALUE_NB_MAX_LISTE);
		editor.putInt(PagePreferences.KEY_PREFERENCES_RAYON,DEFAULT_VALUE_RAYON);
        editor.putString(PagePreferences.KEY_PREFERENCES_FAVORITE_LOCATION_LATITUDE,DEFAULT_VALUE_LATITUDE);
        editor.putString(PagePreferences.KEY_PREFERENCES_FAVORITE_LOCATION_LONGITUDE,DEFAULT_VALUE_LONGITUDE);
		editor.putString(PagePreferences.KEY_PREFERENCES_CURRENT_LOCATION_LATITUDE,DEFAULT_VALUE_LATITUDE);
		editor.putString(PagePreferences.KEY_PREFERENCES_CURRENT_LOCATION_LONGITUDE,DEFAULT_VALUE_LONGITUDE);

        editor.apply();
		setupPreferences();
	}

}
