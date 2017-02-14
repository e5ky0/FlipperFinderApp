package fr.fafsapp.flipper.finder.service.parse;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import fr.fafsapp.flipper.finder.database.FlipperDatabaseHandler;
import fr.fafsapp.flipper.finder.metier.ModeleFlipper;

public class ParseModeleService {

	/**
	 * Retourne tous les modèles de flipper à partir du cloud
	 * 
	 * @return int
	 */
	public List<ModeleFlipper> getAllModeleFlipper() {
		List<ModeleFlipper> listeModele = new ArrayList<ModeleFlipper>();
		List<ParseObject> listePo = new ArrayList<ParseObject>();
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				FlipperDatabaseHandler.MODELE_FLIPPER_TABLE_NAME);
		try {
			listePo = query.find();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		for (ParseObject po : listePo) {
			ModeleFlipper modeleFlipper = new ModeleFlipper(
					po.getLong(FlipperDatabaseHandler.MODELE_FLIPPER_ID),
					po.getString(FlipperDatabaseHandler.MODELE_FLIPPER_NOM),
					po.getString(FlipperDatabaseHandler.MODELE_FLIPPER_MARQUE),
					po.getLong(FlipperDatabaseHandler.MODELE_FLIPPER_ANNEE_LANCEMENT));
			listeModele.add(modeleFlipper);
		}

		return listeModele;
	}
	
	/**
	 * Retourne la liste des modèles de flippers à mettre à jour à partir d'un id
	 * @return
	 */
	public List<ModeleFlipper> getMajModeleById(long id){
		List<ModeleFlipper> listeModele = new ArrayList<ModeleFlipper>();

		List<ParseObject> listePo = new ArrayList<ParseObject>();
    	ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(FlipperDatabaseHandler.MODELE_FLIPPER_TABLE_NAME);
    	try {
    		query.setLimit(2000);
    		query.whereGreaterThan(FlipperDatabaseHandler.MODELE_FLIPPER_ID, id);
    		listePo = query.find();
		} catch (ParseException e1) {
			e1.printStackTrace();
			return null;
		}
    	for (ParseObject po : listePo){
    		ModeleFlipper modele = new ModeleFlipper(po.getLong(FlipperDatabaseHandler.MODELE_FLIPPER_ID),
					po.getString(FlipperDatabaseHandler.MODELE_FLIPPER_NOM),
					po.getString(FlipperDatabaseHandler.MODELE_FLIPPER_MARQUE),
					po.getLong(FlipperDatabaseHandler.MODELE_FLIPPER_ANNEE_LANCEMENT));
    		listeModele.add(modele);
    	}
		return listeModele;
	}
	
	
}
