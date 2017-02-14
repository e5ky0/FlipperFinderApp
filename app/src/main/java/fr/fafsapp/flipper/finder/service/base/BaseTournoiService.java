package fr.fafsapp.flipper.finder.service.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import fr.fafsapp.flipper.finder.database.dao.TournoiDAO;
import fr.fafsapp.flipper.finder.metier.Tournoi;

public class BaseTournoiService {
	
	public ArrayList<Tournoi> getAllTournoi(Context pContext){
		ArrayList<Tournoi> listeRetour = new ArrayList<Tournoi>();
		TournoiDAO tournoiDao = new TournoiDAO(pContext);
		tournoiDao.open();
		listeRetour = tournoiDao.getAllTournoi();
		tournoiDao.close();
		return listeRetour;
	}

	public boolean initListeTournoi(List<Tournoi> listeObjets, SQLiteDatabase db){
		TournoiDAO tournoiDao = new TournoiDAO(db);
		for (Tournoi tournoi: listeObjets){
			tournoiDao.save(tournoi);
		}
		return true;
	}

	public boolean remplaceListeTournoi(List<Tournoi> listeTournoi, Context pContext){
		if(listeTournoi == null){
			return true;
		}
		TournoiDAO tournoiDao = new TournoiDAO(pContext);
		SQLiteDatabase db = tournoiDao.open();
		db.beginTransaction();
		tournoiDao.truncate();
		for (Tournoi modele : listeTournoi){
			tournoiDao.save(modele);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		tournoiDao.close();
		return true;
	}

}
