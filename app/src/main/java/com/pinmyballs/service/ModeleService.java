package com.pinmyballs.service;

import java.util.List;

import android.content.Context;
import com.pinmyballs.metier.ModeleFlipper;
import com.pinmyballs.service.base.BaseModeleService;
import com.pinmyballs.service.parse.ParseModeleService;

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
