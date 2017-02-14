package fr.fafsapp.flipper.finder.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.fafsapp.flipper.finder.R;
import fr.fafsapp.flipper.finder.metier.Commentaire;
import fr.fafsapp.flipper.finder.metier.Enseigne;
import fr.fafsapp.flipper.finder.metier.Flipper;
import fr.fafsapp.flipper.finder.metier.ModeleFlipper;
import fr.fafsapp.flipper.finder.metier.Tournoi;
import fr.fafsapp.flipper.finder.service.base.BaseCommentaireService;
import fr.fafsapp.flipper.finder.service.base.BaseEnseigneService;
import fr.fafsapp.flipper.finder.service.base.BaseFlipperService;
import fr.fafsapp.flipper.finder.service.base.BaseModeleService;
import fr.fafsapp.flipper.finder.service.base.BaseTournoiService;
import fr.fafsapp.flipper.finder.service.parse.ParseCommentaireService;
import fr.fafsapp.flipper.finder.service.parse.ParseEnseigneService;
import fr.fafsapp.flipper.finder.service.parse.ParseFlipperService;
import fr.fafsapp.flipper.finder.service.parse.ParseModeleService;
import fr.fafsapp.flipper.finder.service.parse.ParseTournoiService;

public class GlobalService {

	Context mContext = null;

	public GlobalService(Context context){
		mContext = context;
	}

	public GlobalService(){
	}
	public ArrayList<Commentaire> getLastCommentaire(Context pContext, int nbMaxCommentaire){
		ArrayList<Commentaire> listeRetour = null;
		BaseCommentaireService baseCommentaireService = new BaseCommentaireService();
		BaseFlipperService baseFlipperService = new BaseFlipperService();
		
		listeRetour = baseCommentaireService.getLastCommentaire(pContext, nbMaxCommentaire);
		
		for (Commentaire commentaire : listeRetour){
			Flipper flipper = baseFlipperService.getFlipperById(pContext, commentaire.getFlipperId());
			commentaire.setFlipper(flipper);
		}
		
		return listeRetour;
		
	}
	
	public String majBaseAvecNouveaute(Context pContext, String dateDerniereMaj) throws InterruptedException{
		
		BaseModeleService baseModeleService = new BaseModeleService();
		ParseModeleService parseModeleService = new ParseModeleService();
		BaseEnseigneService baseEnseigneService = new BaseEnseigneService();
		ParseEnseigneService parseEnseigneService = new ParseEnseigneService();
		BaseFlipperService baseFlipperService = new BaseFlipperService();
		ParseFlipperService parseFlipperService = new ParseFlipperService(null);

		BaseCommentaireService baseCommentaireService = new BaseCommentaireService();
		ParseCommentaireService parseCommentaireService = new ParseCommentaireService(null);
		
		BaseTournoiService baseTournoiService = new BaseTournoiService();
		ParseTournoiService parseTournoiService = new ParseTournoiService();
		
		Long idMaxModele = baseModeleService.getIdMaxModele(pContext); 

		// On récupère les données du cloud
		List<ModeleFlipper> nvlleListeModele = parseModeleService.getMajModeleById(idMaxModele);
		List<Enseigne> nvlleListeEnseigne = parseEnseigneService.getMajEnseigneByDate(dateDerniereMaj);
		List<Flipper> nvlleListeFlipper = parseFlipperService.getMajFlipperByDate(dateDerniereMaj);
		List<Commentaire> nvlleListeCommentaire = parseCommentaireService.getMajCommentaireByDate(dateDerniereMaj);
		List<Tournoi> nvlleListeTournoi = parseTournoiService.getAllTournoi();

		boolean maj = false;
		String textPopup = "";

		// On met à jour la table des enseignes
		if (nvlleListeEnseigne != null && nvlleListeEnseigne.size() > 0){
			maj = true;
			textPopup = pContext.getResources().getString(R.string.toastMajEnseigne, nvlleListeEnseigne.size()) + "\n";
			baseEnseigneService.majListeEnseigne(nvlleListeEnseigne, pContext);
		}
		
		// On met à jour la table des modèles de flipper
		if (nvlleListeModele != null && nvlleListeModele.size() > 0){
			maj = true;
			textPopup += pContext.getResources().getString(R.string.toastMajModele, nvlleListeModele.size())+ "\n";;
			baseModeleService.majListeModele(nvlleListeModele, pContext);
		}
		
		// On met à jour la table des flippers
		if (nvlleListeFlipper != null && nvlleListeFlipper.size() > 0){
			maj = true;
			textPopup += pContext.getResources().getString(R.string.toastMajFlipper, nvlleListeFlipper.size())+ "\n";;
			baseFlipperService.majListeFlipper(nvlleListeFlipper, pContext);
		}

		// On met à jour la table des commentaires
		if (nvlleListeCommentaire != null && nvlleListeCommentaire.size() > 0){
			maj = true;
			textPopup += pContext.getResources().getString(R.string.toastMajCommentaire, nvlleListeCommentaire.size());
			baseCommentaireService.majListeCommentaire(nvlleListeCommentaire, pContext);
		}
		
		// On met à jour la table des tournois
		baseTournoiService.remplaceListeTournoi(nvlleListeTournoi, pContext);
		
		
		// S'il y a eu une mise à jour, on envoie le récap en sortie.
		if (maj){
			return textPopup;
		}
		return null;
	}

	private void populateEnseigne(SQLiteDatabase db) {
		BaseEnseigneService baseEnseigneService = new BaseEnseigneService();
		List<Enseigne> returnList = new ArrayList<Enseigne>();
		Gson gson = new GsonBuilder().create();
		Reader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(mContext.getAssets().open("ENSEIGNE2.json"), "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		returnList = Arrays.asList(gson.fromJson(reader, Enseigne[].class));
		baseEnseigneService.initListeEnseigne(returnList, db);
	}

	private void populateFlipper(SQLiteDatabase db) {
		BaseFlipperService baseFlipperService= new BaseFlipperService();
		List<Flipper> returnList = new ArrayList<Flipper>();
		Gson gson = new GsonBuilder().create();
		Reader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(mContext.getAssets().open("FLIPPER.json"), "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		returnList = Arrays.asList(gson.fromJson(reader, Flipper[].class));
		baseFlipperService.initListeFlipper(returnList, db);
	}

	private void populateCommentaire(SQLiteDatabase db) {
		BaseCommentaireService baseCommentaireService= new BaseCommentaireService();
		List<Commentaire> returnList = new ArrayList<Commentaire>();
		Gson gson = new GsonBuilder().create();
		Reader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(mContext.getAssets().open("COMMENTAIRE.json"), "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		returnList = Arrays.asList(gson.fromJson(reader, Commentaire[].class));
		baseCommentaireService.initListeCommentaire(returnList, db);
	}

	private void populateModele(SQLiteDatabase db) {
		BaseModeleService baseModeleService= new BaseModeleService();
		List<ModeleFlipper> returnList = new ArrayList<ModeleFlipper>();
		Gson gson = new GsonBuilder().create();
		Reader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(mContext.getAssets().open("MODELE_FLIPPER.json"), "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		returnList = Arrays.asList(gson.fromJson(reader, ModeleFlipper[].class));
		baseModeleService.initListModele(returnList, db);
	}

	private void populateTournoi(SQLiteDatabase db) {
		BaseTournoiService baseTournoiService = new BaseTournoiService();
		List<Tournoi> returnList = new ArrayList<Tournoi>();
		Gson gson = new GsonBuilder().create();
		Reader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(mContext.getAssets().open("TOURNOI.json"), "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		returnList = Arrays.asList(gson.fromJson(reader, Tournoi[].class));
		baseTournoiService.initListeTournoi(returnList, db);
	}

	public void reinitDatabase(SQLiteDatabase db){
		populateModele(db);
		populateEnseigne(db);
		populateFlipper(db);
		populateCommentaire(db);
		populateTournoi(db);
	}


}
