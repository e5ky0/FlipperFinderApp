package com.pinmyballs.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import com.pinmyballs.fragment.FragmentActionsFlipper.FragmentActionCallback;
import com.pinmyballs.metier.Commentaire;
import com.pinmyballs.metier.Flipper;
import com.pinmyballs.service.base.BaseFlipperService;
import com.pinmyballs.service.parse.ParseFlipperService;

public class FlipperService {

	private FragmentActionCallback mFragmentCallback;

	public FlipperService(FragmentActionCallback fragmentCallback) {
		mFragmentCallback = fragmentCallback;
	}

	public boolean remplaceToutFlipper(Context pContext){
		boolean retour = true;
		BaseFlipperService baseFlipperService = new BaseFlipperService();
		ParseFlipperService parseFlipperService = new ParseFlipperService(null);
		List<Flipper> nvlleListe = parseFlipperService.getAllFlipper();
		retour = baseFlipperService.majListeFlipper(nvlleListe, pContext);
		return retour;
	}


	public boolean valideFlipper(Context pContext, Flipper flipper){
		ParseFlipperService parseFlipperService = new ParseFlipperService(mFragmentCallback);
		final String dateToSave = new SimpleDateFormat("yyyy/MM/dd", Locale.FRANCE).format(new Date());
		parseFlipperService.updateDateFlipper(pContext, flipper, dateToSave);

		return true;
	}


	//remplace supprimeflip
	public boolean modifieEtatFlip(Context pContext, Flipper flipper){
		Date dateDuJour = new Date();
		String dateMaj = new SimpleDateFormat("yyyy/MM/dd", Locale.FRANCE).format(dateDuJour);
		flipper.setDateMaj(dateMaj);
		flipper.setActif(flipper.isActif() ? 0 : 1);
		// Update of MongoDb
		ParseFlipperService parseFlipperService =new ParseFlipperService(mFragmentCallback);
		parseFlipperService.modifieEtatFlipper(pContext,flipper);
		return true;
	}

	public boolean remplaceFlipper(Context pContext, Flipper flipper, long idNouveauModele, String commentaire, String pseudo){
		Date dateDuJour = new Date();

		String dateMaj = new SimpleDateFormat("yyyy/MM/dd", Locale.FRANCE).format(dateDuJour);

		flipper.setDateMaj(dateMaj);
		flipper.setActif(0);

		Flipper nouveauFlipper = new Flipper(dateDuJour.getTime(), idNouveauModele, 0, flipper.getIdEnseigne(), true,
				dateMaj);
		Commentaire commentaireToAdd = null;
		if (commentaire != null && commentaire.length() > 0){
			commentaireToAdd = new Commentaire(dateDuJour.getTime(), dateDuJour.getTime(), commentaire, dateMaj,
					pseudo, true);
		}

		ParseFlipperService parseFlipperService = new ParseFlipperService(mFragmentCallback);


		parseFlipperService.remplaceModeleFlipper(pContext, flipper, nouveauFlipper, commentaireToAdd);

		return true;
	}

}
