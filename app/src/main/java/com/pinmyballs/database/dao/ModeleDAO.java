package com.pinmyballs.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import com.pinmyballs.database.DAOBase;
import com.pinmyballs.database.FlipperDatabaseHandler;
import com.pinmyballs.metier.ModeleFlipper;

public class ModeleDAO extends DAOBase{

	public ModeleDAO(Context pContext) {
		super(pContext);
	}

	public ModeleDAO(SQLiteDatabase pDb) {
		super(pDb);
	}

	public ArrayList<ModeleFlipper> getAllModeleFlipper(){
		ArrayList<ModeleFlipper> listeRetour = new ArrayList<ModeleFlipper>();

		Cursor cursor = mDb.query(FlipperDatabaseHandler.MODELE_FLIPPER_TABLE_NAME,
				new String[]{FlipperDatabaseHandler.MODELE_FLIPPER_ID,
					FlipperDatabaseHandler.MODELE_FLIPPER_MARQUE,
					FlipperDatabaseHandler.MODELE_FLIPPER_NOM,
					FlipperDatabaseHandler.MODELE_FLIPPER_ANNEE_LANCEMENT},
					null,null, null, null, null);

		while (cursor.moveToNext()) {
			listeRetour.add(convertCursorToModeleFlipper(cursor));
		}
		cursor.close();
		return listeRetour;
	}

	public ModeleFlipper getModeleFlipperByName(String nameFlipper){
		ModeleFlipper modeleRetour = null;
		Cursor cursor = mDb.query(FlipperDatabaseHandler.MODELE_FLIPPER_TABLE_NAME,
				new String[]{FlipperDatabaseHandler.MODELE_FLIPPER_ID,
					FlipperDatabaseHandler.MODELE_FLIPPER_MARQUE,
					FlipperDatabaseHandler.MODELE_FLIPPER_NOM,
					FlipperDatabaseHandler.MODELE_FLIPPER_ANNEE_LANCEMENT},
					FlipperDatabaseHandler.MODELE_FLIPPER_NOM + "=?",
					new String[] { nameFlipper }, null, null, null);
		if (cursor.moveToNext()) {
			modeleRetour = convertCursorToModeleFlipper(cursor);
		}
		cursor.close();
		return modeleRetour;
	}

	public void save(ModeleFlipper modele){
		ContentValues contentValues = new ContentValues();
		contentValues.put(FlipperDatabaseHandler.MODELE_FLIPPER_ID, modele.getId());
		contentValues.put(FlipperDatabaseHandler.MODELE_FLIPPER_NOM, modele.getNom());
		contentValues.put(FlipperDatabaseHandler.MODELE_FLIPPER_MARQUE, modele.getMarque());
		contentValues.put(FlipperDatabaseHandler.MODELE_FLIPPER_ANNEE_LANCEMENT, modele.getAnneeLancement());
		mDb.delete(FlipperDatabaseHandler.MODELE_FLIPPER_TABLE_NAME, FlipperDatabaseHandler.MODELE_FLIPPER_ID + "=?", new String[] { String.valueOf(modele.getId()) });
		mDb.insert(FlipperDatabaseHandler.MODELE_FLIPPER_TABLE_NAME, null, contentValues);
	}

	public void truncate(){
		mDb.delete(FlipperDatabaseHandler.MODELE_FLIPPER_TABLE_NAME, null, null);
	}

	private ModeleFlipper convertCursorToModeleFlipper(Cursor c){
		return new ModeleFlipper(c.getLong(0), c.getString(2), c.getString(1), c.getLong(3));
	}

}
