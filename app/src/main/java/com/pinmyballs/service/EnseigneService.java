package com.pinmyballs.service;

import java.util.List;

import android.content.Context;
import com.pinmyballs.metier.Enseigne;
import com.pinmyballs.service.base.BaseEnseigneService;
import com.pinmyballs.service.parse.ParseEnseigneService;

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
