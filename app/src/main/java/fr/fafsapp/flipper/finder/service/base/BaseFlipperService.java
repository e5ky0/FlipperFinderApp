package fr.fafsapp.flipper.finder.service.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

import fr.fafsapp.flipper.finder.database.dao.FlipperDAO;
import fr.fafsapp.flipper.finder.metier.Flipper;

public class BaseFlipperService {

	
	public ArrayList<Flipper> rechercheFlipper(Context pContext, double latitude, double longitude, int rayon, int maxListeSize, String modele){
		ArrayList<Flipper> listeRetour = new ArrayList<Flipper>();
		
		FlipperDAO flipperDao = new FlipperDAO(pContext);
		
		flipperDao.open();
		PointF center = new PointF((float)latitude, (float)longitude);

		ArrayList<Flipper> listeFlipper = flipperDao.getFlipperByDistance(center, rayon, modele);
		flipperDao.close();
		
		for (int i = 0 ; i < listeFlipper.size() && i < maxListeSize ;i++){
			listeRetour.add(listeFlipper.get(i));
		}
		
		return listeRetour;
	}
	
	public Flipper getFlipperById(Context pContext, long idFlipper){
		Flipper flipperRetour = null;
		
		FlipperDAO flipperDao = new FlipperDAO(pContext);
		
		flipperDao.open();
		flipperRetour = flipperDao.getFlipperById(idFlipper);
		flipperDao.close();
		
		return flipperRetour;
	}

	public boolean majListeFlipper(List<Flipper> listeFlipper, Context pContext){
		return majListeFlipper(listeFlipper, pContext, false);
	}

	public boolean initListeFlipper(List<Flipper> listeObjets, SQLiteDatabase db){
		FlipperDAO flipperDao = new FlipperDAO(db);
		for (Flipper flipper: listeObjets){
			flipperDao.save(flipper);
		}
		return true;
	}

	public boolean majListeFlipper(List<Flipper> listeFlipper, Context pContext, boolean truncate){
		FlipperDAO flipperDao = new FlipperDAO(pContext);
		SQLiteDatabase db = flipperDao.open();
		db.beginTransaction();
		if (truncate){
			flipperDao.truncate();
		}
		for (Flipper flipper : listeFlipper){
			flipperDao.save(flipper);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		flipperDao.close();
		return true;
	}
	
	public boolean majFlipper(Flipper flipper, Context pContext){
		FlipperDAO flipperDao = new FlipperDAO(pContext);
		flipperDao.open();
		flipperDao.save(flipper);
		flipperDao.close();
		return true;
	}
	
}