package fr.fafsapp.flipper.finder.service;

import java.util.List;

import android.content.Context;
import fr.fafsapp.flipper.finder.metier.ModeleFlipper;
import fr.fafsapp.flipper.finder.service.base.BaseModeleService;
import fr.fafsapp.flipper.finder.service.parse.ParseModeleService;

public class ModeleService {
	
	public boolean remplaceToutModele(Context pContext){
		boolean retour = true;
		BaseModeleService baseModeleService = new BaseModeleService();
		ParseModeleService parseModeleService = new ParseModeleService();
		List<ModeleFlipper> nvlleListe = parseModeleService.getAllModeleFlipper();
		retour = baseModeleService.majListeModele(nvlleListe, pContext);
		return retour;
	}

}
