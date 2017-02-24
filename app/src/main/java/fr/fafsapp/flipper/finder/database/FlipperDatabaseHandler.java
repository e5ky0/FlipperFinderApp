package fr.fafsapp.flipper.finder.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import fr.fafsapp.flipper.finder.service.GlobalService;

public class FlipperDatabaseHandler extends SQLiteOpenHelper{

	Context mContext = null;
	// V41 le 04/10/2015
	public static final int DATABASE_VERSION = 44;
	public static final String DATABASE_DATE_MAJ = "2015/10/26";

	public static final String FLIPPER_BASE_NAME = "flipper.db";

	public static final String MODELE_FLIPPER_TABLE_NAME = "MODELE_FLIPPER";
	public static final String MODELE_FLIPPER_ID = "MOFL_ID";
	public static final String MODELE_FLIPPER_NOM = "MOFL_NOM";
	public static final String MODELE_FLIPPER_MARQUE = "MOFL_MARQUE";
	public static final String MODELE_FLIPPER_ANNEE_LANCEMENT = "MOFL_ANNEE_LANCEMENT";

	public static final String ENSEIGNE_TABLE_NAME = "ENSEIGNE";
	public static final String ENSEIGNE_ID = "ENS_ID";
	public static final String ENSEIGNE_TYPE = "ENS_TYPE";
	public static final String ENSEIGNE_NOM = "ENS_NOM";
	public static final String ENSEIGNE_HORAIRE = "ENS_HORAIRE";
	public static final String ENSEIGNE_LATITUDE = "ENS_LATITUDE";
	public static final String ENSEIGNE_LONGITUDE = "ENS_LONGITUDE";
	public static final String ENSEIGNE_ADRESSE = "ENS_ADRESSE";
	public static final String ENSEIGNE_CODE_POSTAL = "ENS_CODE_POSTAL";
	public static final String ENSEIGNE_VILLE = "ENS_VILLE";
	public static final String ENSEIGNE_PAYS = "ENS_PAYS";
	public static final String ENSEIGNE_DATMAJ = "ENS_DATMAJ";

	public static final String FLIPPER_TABLE_NAME = "FLIPPER";
	public static final String FLIPPER_ID = "FLIP_ID";
	public static final String FLIPPER_MODELE = "FLIP_MODELE";
	public static final String FLIPPER_NB_CREDITS_2E = "FLIP_NB_CREDITS_2E";
	public static final String FLIPPER_ENSEIGNE = "FLIP_ENSEIGNE";
	public static final String FLIPPER_DATMAJ = "FLIP_DATMAJ";
	public static final String FLIPPER_ACTIF = "FLIP_ACTIF";

	public static final String SCORE_TABLE_NAME = "SCORE";
	public static final String SCORE_ID = "SCORE_ID";
	public static final String SCORE_FLIPPER_ID = "SCORE_FLIPPER_ID";
	public static final String SCORE_SCORE = "SCORE_SCORE";
	public static final String SCORE_PSEUDO = "SCORE_PSEUDO";
	public static final String SCORE_DATE = "SCORE_DATE";

	public static final String TOURNOI_TABLE_NAME = "TOURNOI";
	public static final String TOUR_ID = "TOUR_ID";
	public static final String TOUR_NOM = "TOUR_NOM";
	public static final String TOUR_COMMENTAIRE = "TOUR_COMMENTAIRE";
	public static final String TOUR_DATE = "TOUR_DATE";
	public static final String TOUR_LATITUDE = "TOUR_LATITUDE";
	public static final String TOUR_LONGITUDE = "TOUR_LONGITUDE";
	public static final String TOUR_ADRESSE = "TOUR_ADRESSE";
	public static final String TOUR_CODE_POSTAL = "TOUR_CODE_POSTAL";
	public static final String TOUR_VILLE = "TOUR_VILLE";
	public static final String TOUR_PAYS = "TOUR_PAYS";
	public static final String TOUR_URL = "TOUR_URL";

	public static final String COMMENTAIRE_TABLE_NAME = "COMMENTAIRE";
	public static final String COMM_ID = "COMM_ID";
	public static final String COMM_FLIPPER_ID = "COMM_FLIPPER_ID";
	public static final String COMM_TEXTE = "COMM_TEXTE";
	public static final String COMM_DATE = "COMM_DATE";
	public static final String COMM_PSEUDO = "COMM_PSEUDO";
	public static final String COMM_ACTIF = "COMM_ACTIF";

	public static final String COMMENTAIRE_TABLE_CREATE =
		"CREATE TABLE " + COMMENTAIRE_TABLE_NAME + " (" +
		COMM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		COMM_FLIPPER_ID + " INTEGER NOT NULL, " +
		COMM_TEXTE + " TEXT NOT NULL, " +
		COMM_DATE + " TEXT NOT NULL, " +
		COMM_PSEUDO + " TEXT NOT NULL, " +
		COMM_ACTIF + " INTEGER NOT NULL);";

	public static final String SCORE_TABLE_CREATE =
		"CREATE TABLE " + SCORE_TABLE_NAME + " (" +
		SCORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		SCORE_FLIPPER_ID + " INTEGER NOT NULL, " +
		SCORE_SCORE + " INTEGER NOT NULL, " +
		SCORE_DATE + " TEXT NOT NULL, " +
		SCORE_PSEUDO + " TEXT NOT NULL);";


	public static final String TOURNOI_TABLE_CREATE =
		"CREATE TABLE " + TOURNOI_TABLE_NAME + " (" +
		TOUR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		TOUR_NOM + " TEXT, " +
		TOUR_COMMENTAIRE + " TEXT, " +
		TOUR_DATE + " TEXT, " +
		TOUR_LATITUDE + " TEXT, " +
		TOUR_LONGITUDE + " TEXT, " +
		TOUR_ADRESSE + " TEXT, " +
		TOUR_CODE_POSTAL + " TEXT, " +
		TOUR_VILLE + " TEXT, " +
		TOUR_PAYS + " TEXT, " +
		TOUR_URL + " TEXT);";


	public static final String MODELE_FLIPPER_TABLE_CREATE =
		"CREATE TABLE " + MODELE_FLIPPER_TABLE_NAME + " (" +
		MODELE_FLIPPER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		MODELE_FLIPPER_NOM + " TEXT NOT NULL, " +
		MODELE_FLIPPER_MARQUE + " TEXT, " +
		MODELE_FLIPPER_ANNEE_LANCEMENT + " INTEGER);";

	public static final String ENSEIGNE_TABLE_CREATE =
		"CREATE TABLE " + ENSEIGNE_TABLE_NAME + " (" +
		ENSEIGNE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		ENSEIGNE_TYPE + " TEXT, " +
		ENSEIGNE_NOM + " TEXT, " +
		ENSEIGNE_HORAIRE + " TEXT, " +
		ENSEIGNE_LATITUDE + " TEXT, " +
		ENSEIGNE_LONGITUDE + " TEXT, " +
		ENSEIGNE_ADRESSE + " TEXT, " +
		ENSEIGNE_CODE_POSTAL + " TEXT, " +
		ENSEIGNE_VILLE + " TEXT, " +
		ENSEIGNE_PAYS + " TEXT, " +
		ENSEIGNE_DATMAJ + " TEXT);";

	public static final String FLIPPER_TABLE_CREATE =
		"CREATE TABLE " + FLIPPER_TABLE_NAME + " (" +
		FLIPPER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		FLIPPER_MODELE + " INTEGER NOT NULL, " +
		FLIPPER_NB_CREDITS_2E + " INTEGER, " +
		FLIPPER_ENSEIGNE + " INTEGER NOT NULL, " +
		FLIPPER_DATMAJ + " TEXT, " +
		FLIPPER_ACTIF + " INTEGER NOT NULL, " +
		" FOREIGN KEY ("+FLIPPER_ENSEIGNE+") REFERENCES "+ENSEIGNE_TABLE_NAME+" ("+ENSEIGNE_ID+"), " +
		" FOREIGN KEY ("+FLIPPER_MODELE+") REFERENCES "+MODELE_FLIPPER_TABLE_NAME+" ("+MODELE_FLIPPER_ID+"));";


	public static final String HI_SCORE_TABLE_DROP = "DROP TABLE IF EXISTS " + SCORE_TABLE_NAME + ";";
	public static final String FLIPPER_TABLE_DROP = "DROP TABLE IF EXISTS " + FLIPPER_TABLE_NAME + ";";
	public static final String ENSEIGNE_TABLE_DROP = "DROP TABLE IF EXISTS " + ENSEIGNE_TABLE_NAME + ";";
	public static final String MODELE_FLIPPER_TABLE_DROP = "DROP TABLE IF EXISTS " + MODELE_FLIPPER_TABLE_NAME + ";";
	public static final String SCORE_TABLE_DROP = "DROP TABLE IF EXISTS " + SCORE_TABLE_NAME + ";";
	public static final String COMMENTAIRE_TABLE_DROP = "DROP TABLE IF EXISTS " + COMMENTAIRE_TABLE_NAME + ";";
	public static final String TOURNOI_TABLE_DROP = "DROP TABLE IF EXISTS " + TOURNOI_TABLE_NAME + ";";

	public FlipperDatabaseHandler(Context context,  CursorFactory factory) {
		super(context, FLIPPER_BASE_NAME, factory, DATABASE_VERSION);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(MODELE_FLIPPER_TABLE_CREATE);
		db.execSQL(ENSEIGNE_TABLE_CREATE);
		db.execSQL(FLIPPER_TABLE_CREATE);
		db.execSQL(SCORE_TABLE_CREATE);
		db.execSQL(TOURNOI_TABLE_CREATE);
		db.execSQL(COMMENTAIRE_TABLE_CREATE);
		GlobalService globalService = new GlobalService(mContext);
		globalService.reinitDatabase(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion){
			db.execSQL(FLIPPER_TABLE_DROP);
			db.execSQL(ENSEIGNE_TABLE_DROP);
			db.execSQL(MODELE_FLIPPER_TABLE_DROP);
			db.execSQL(SCORE_TABLE_DROP);
			db.execSQL(TOURNOI_TABLE_DROP);
			db.execSQL(COMMENTAIRE_TABLE_DROP);
			onCreate(db);
		}
	}

}
