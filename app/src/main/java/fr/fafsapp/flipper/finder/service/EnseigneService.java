package fr.fafsapp.flipper.finder.service;

import java.util.List;

import android.content.Context;
import fr.fafsapp.flipper.finder.metier.Enseigne;
import fr.fafsapp.flipper.finder.service.base.BaseEnseigneService;
import fr.fafsapp.flipper.finder.service.parse.ParseEnseigneService;

public class EnseigneService {
	
	public boolean remplaceToutEnseigne(Context pContext){
		boolean retour = true;
		BaseEnseigneService baseEnseigneService = new BaseEnseigneService();
		ParseEnseigneService parseEnseigneService = new ParseEnseigneService();
		List<Enseigne> nvlleListe = parseEnseigneService.getAllEnseigne();
		retour = baseEnseigneService.majListeEnseigne(nvlleListe, pContext);
		return retour;
	}

}
