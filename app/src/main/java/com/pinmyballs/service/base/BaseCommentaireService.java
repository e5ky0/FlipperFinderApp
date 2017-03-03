package fr.fafsapp.flipper.finder.service.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import fr.fafsapp.flipper.finder.database.dao.CommentaireDAO;
import fr.fafsapp.flipper.finder.metier.Commentaire;

public class BaseCommentaireService {

	public ArrayList<Commentaire> getCommentaireByFlipperId(Context pContext, long idFlipper){
		ArrayList<Commentaire> listeRetour = new ArrayList<Commentaire>();
		CommentaireDAO commentaireDao = new CommentaireDAO(pContext);
		commentaireDao.open();
		listeRetour = commentaireDao.getCommentairePourFlipperId(idFlipper);
		commentaireDao.close();
		return listeRetour;
	}

	public ArrayList<Commentaire> getLastCommentaire(Context pContext, int nbMaxCommentaire){
		ArrayList<Commentaire> listeRetour = new ArrayList<Commentaire>();
		CommentaireDAO commentaireDao = new CommentaireDAO(pContext);
		commentaireDao.open();
		listeRetour = commentaireDao.getLastCommentaire(nbMaxCommentaire);
		commentaireDao.close();
		return listeRetour;
	}

	public boolean addCommentaire(Commentaire commentaire, Context pContext){
		CommentaireDAO commentaireDao = new CommentaireDAO(pContext);
		commentaireDao.open();
		commentaireDao.save(commentaire);
		commentaireDao.close();
		return true;
	}

	public boolean majListeCommentaire(List<Commentaire> listeCommentaire, Context pContext){
		return majListeCommentaire(listeCommentaire, pContext, false);
	}

	public boolean initListeCommentaire(List<Commentaire> listeCommentaire, SQLiteDatabase db){
		CommentaireDAO commentaireDao = new CommentaireDAO(db);
		for (Commentaire commentaire: listeCommentaire){
			commentaireDao.save(commentaire);
		}
		return true;
	}

	public boolean majListeCommentaire(List<Commentaire> listeCommentaire, Context pContext, boolean truncate){
		CommentaireDAO commentaireDao = new CommentaireDAO(pContext);
		SQLiteDatabase db = commentaireDao.open();
		db.beginTransaction();
		if (truncate){
			commentaireDao.truncate();
		}
		for (Commentaire commentaire: listeCommentaire){
			commentaireDao.save(commentaire);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		commentaireDao.close();
		return true;
	}

}
