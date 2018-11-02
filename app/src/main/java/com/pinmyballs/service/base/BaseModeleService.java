package com.pinmyballs.service.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import com.pinmyballs.database.dao.ModeleDAO;
import com.pinmyballs.metier.ModeleFlipper;

public class BaseModeleService {

	public ArrayList<ModeleFlipper> getAllModeleFlipper(Context pContext){
		ArrayList<ModeleFlipper> listeRetour;
		ModeleDAO modeleDao = new ModeleDAO(pContext);
		modeleDao.open();
		listeRetour = modeleDao.getAllModeleFlipper();
		modeleDao.close();
		return listeRetour;
	}

	public long getIdMaxModele(Context pContext){
		long idRetour = 0;
		ArrayList<ModeleFlipper> listeModele = getAllModeleFlipper(pContext);
		for (ModeleFlipper modele : listeModele){
			if (modele.getId() > idRetour){
				idRetour = modele.getId();
			}
		}
		return idRetour;
	}

	public ArrayList<String> getAllNomModeleFlipper(Context pContext){
		ArrayList<String> listeRetour = new ArrayList<String>();
		ArrayList<ModeleFlipper> listeModeleFlipper = getAllModeleFlipper(pContext);
		for (ModeleFlipper modeleFlipper : listeModeleFlipper){
			listeRetour.add(modeleFlipper.getNom());
		}
		return listeRetour;
	}

	public ArrayList<String> getAllNomModeleFlipperAvecMarque(Context pContext){
		ArrayList<String> listeRetour = new ArrayList<String>();
		ArrayList<ModeleFlipper> listeModeleFlipper = getAllModeleFlipper(pContext);
		for (ModeleFlipper modeleFlipper : listeModeleFlipper){
			/*String modeleAvecMarque = String.format("%s (%s, %s)",
					modeleFlipper.getNom(),
					modeleFlipper.getMarque(),
					modeleFlipper.getAnneeLancement());*/

			listeRetour.add(modeleFlipper.getNomComplet());
		}
		return listeRetour;
	}

	public ModeleFlipper getModeleFlipperByName(Context pContext, String nomModele){
		ModeleFlipper modeleRetour;
		ModeleDAO modeleDao = new ModeleDAO(pContext);
		modeleDao.open();
		modeleRetour = modeleDao.getModeleFlipperByName(nomModele);
		modeleDao.close();
		return modeleRetour;
	}

	public ModeleFlipper getModeleFlipperByNameComplet(Context pContext, String nomModele){
		ModeleFlipper modeleRetour;
		ModeleDAO modeleDao = new ModeleDAO(pContext);
		modeleDao.open();
		modeleRetour = modeleDao.getModeleFlipperByNameComplet(nomModele);
		modeleDao.close();
		return modeleRetour;
	}

	public boolean majListeModele(List<ModeleFlipper> listeModeles, Context pContext){
		return majListeModele(listeModeles, pContext, false);
	}

	public boolean initListModele(List<ModeleFlipper> listeModeles, SQLiteDatabase db){
		ModeleDAO modeleDao = new ModeleDAO(db);
		for (ModeleFlipper modele : listeModeles){
			modeleDao.save(modele);
		}
		return true;
	}

	public boolean majListeModele(List<ModeleFlipper> listeModeles, Context pContext, boolean truncate){
		if(listeModeles == null){
			return true;
		}
		ModeleDAO modeleDao = new ModeleDAO(pContext);
		modeleDao.open();
		//db.beginTransaction();
		if (truncate){
			//modeleDao.truncate();
		}
		for (ModeleFlipper modele : listeModeles){
			modeleDao.save(modele);
		}
		//db.setTransactionSuccessful();
		//db.endTransaction();
		modeleDao.close();
		return true;
	}

	public ModeleFlipper getModeleById(Context pContext, Long modeleId){
		ModeleFlipper modeleFlipper = new ModeleFlipper();
		ModeleDAO modeleDao = new ModeleDAO(pContext);
		modeleDao.open();
		modeleFlipper = modeleDao.getModeleById(modeleId);
		modeleDao.close();
		return modeleFlipper;
	}
}
