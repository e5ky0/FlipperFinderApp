package fr.fafsapp.flipper.finder.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import fr.fafsapp.flipper.finder.database.DAOBase;
import fr.fafsapp.flipper.finder.database.FlipperDatabaseHandler;
import fr.fafsapp.flipper.finder.metier.Commentaire;

public class CommentaireDAO extends DAOBase{

	public CommentaireDAO(Context pContext) {
		super(pContext);
	}

	public CommentaireDAO(SQLiteDatabase pDb) {
		super(pDb);
	}

	public ArrayList<Commentaire> getLastCommentaire(int nbMaxCommentaire){
		ArrayList<Commentaire> listeRetour = new ArrayList<Commentaire>();
		String strWhere =  " Where " + FlipperDatabaseHandler.COMM_ACTIF + " = 1 ";
		String strOrder =  " ORDER BY " + FlipperDatabaseHandler.COMM_DATE + " DESC ";

		Cursor cursor = mDb.rawQuery("select " + FlipperDatabaseHandler.COMM_ID +
									 " , " +  FlipperDatabaseHandler.COMM_FLIPPER_ID +
									 " , " +  FlipperDatabaseHandler.COMM_TEXTE +
									 " , " +  FlipperDatabaseHandler.COMM_PSEUDO +
									 " , " +  FlipperDatabaseHandler.COMM_DATE + 
									 " , " +  FlipperDatabaseHandler.COMM_ACTIF + 
									 " from " + FlipperDatabaseHandler.COMMENTAIRE_TABLE_NAME +
									 strWhere + strOrder, null);
		
		int i = 0;
		while (cursor.moveToNext() && i++ < nbMaxCommentaire) {
			listeRetour.add(convertCursorToCommentaire(cursor));
		}
		cursor.close();
		
		return listeRetour;
	}
	
	public ArrayList<Commentaire> getCommentairePourFlipperId(long flipperId){
		ArrayList<Commentaire> listeRetour = new ArrayList<Commentaire>();
		String strWhere =  " Where "
		        + FlipperDatabaseHandler.COMM_FLIPPER_ID + " = " + String.valueOf(flipperId)
		        + " AND "+ FlipperDatabaseHandler.COMM_ACTIF+ " = 1 ";
		String strOrder =  " ORDER BY " + FlipperDatabaseHandler.COMM_DATE + " DESC ";

		Cursor cursor = mDb.rawQuery("select " + FlipperDatabaseHandler.COMM_ID +
									 " , " +  FlipperDatabaseHandler.COMM_FLIPPER_ID +
									 " , " +  FlipperDatabaseHandler.COMM_TEXTE +
									 " , " +  FlipperDatabaseHandler.COMM_PSEUDO +
									 " , " +  FlipperDatabaseHandler.COMM_DATE + 
									 " , " +  FlipperDatabaseHandler.COMM_ACTIF + 
									 " from " + FlipperDatabaseHandler.COMMENTAIRE_TABLE_NAME +
									 strWhere + strOrder, null);
		
		while (cursor.moveToNext()) {
			listeRetour.add(convertCursorToCommentaire(cursor));
		}
		cursor.close();
		return listeRetour;
	}
	
	private Commentaire convertCursorToCommentaire(Cursor c){
		Commentaire commentaire = new Commentaire();
		commentaire.setId(c.getLong(0));
		commentaire.setFlipperId(c.getLong(1));
		commentaire.setTexte(c.getString(2));
		commentaire.setPseudo(c.getString(3));
		commentaire.setDate(c.getString(4));
		commentaire.setActif(c.getLong(5));

		return commentaire;
	}

	public void save(Commentaire commentaire) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(FlipperDatabaseHandler.COMM_ID, commentaire.getId());
		contentValues.put(FlipperDatabaseHandler.COMM_DATE, commentaire.getDate());
		contentValues.put(FlipperDatabaseHandler.COMM_FLIPPER_ID, commentaire.getFlipperId());
		contentValues.put(FlipperDatabaseHandler.COMM_PSEUDO, commentaire.getPseudo());
		contentValues.put(FlipperDatabaseHandler.COMM_TEXTE, commentaire.getTexte());
		contentValues.put(FlipperDatabaseHandler.COMM_ACTIF, commentaire.getActif());

		mDb.delete(FlipperDatabaseHandler.COMMENTAIRE_TABLE_NAME, FlipperDatabaseHandler.COMM_ID + "=?", new String[]{String.valueOf(commentaire.getId())});
		mDb.insert(FlipperDatabaseHandler.COMMENTAIRE_TABLE_NAME, null, contentValues);
	}

	public void truncate(){
		mDb.delete(FlipperDatabaseHandler.COMMENTAIRE_TABLE_NAME, null, null);
	}

}
