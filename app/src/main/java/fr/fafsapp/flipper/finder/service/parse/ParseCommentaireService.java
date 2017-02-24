package fr.fafsapp.flipper.finder.service.parse;

import android.content.Context;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import fr.fafsapp.flipper.finder.R;
import fr.fafsapp.flipper.finder.database.FlipperDatabaseHandler;
import fr.fafsapp.flipper.finder.fragment.FragmentCommentaireFlipper.FragmentCallback;
import fr.fafsapp.flipper.finder.metier.Commentaire;
import fr.fafsapp.flipper.finder.service.base.BaseCommentaireService;

public class ParseCommentaireService {
	private FragmentCallback mFragmentCallback;

	public ParseCommentaireService(FragmentCallback fragmentCallback) {
		mFragmentCallback = fragmentCallback;
	}
	/**
	 * Retourne la liste des commantaires à mettre à jour à partir d'une date donnée.
	 * @param dateDerniereMaj
	 * @return
	 */
	public List<Commentaire> getMajCommentaireByDate(String dateDerniereMaj){
		List<Commentaire> listeCommentaire = new ArrayList<Commentaire>();

		List<ParseObject> listePo = new ArrayList<ParseObject>();
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(FlipperDatabaseHandler.COMMENTAIRE_TABLE_NAME);
		try {
			query.setLimit(2000);
			query.whereGreaterThanOrEqualTo(FlipperDatabaseHandler.COMM_DATE, dateDerniereMaj);
			listePo = query.find();
		} catch (ParseException e1) {
			e1.printStackTrace();
			return null;
		}
		for (ParseObject po : listePo){
			Commentaire commentaire = new Commentaire(po.getLong(FlipperDatabaseHandler.COMM_ID),
					po.getLong(FlipperDatabaseHandler.COMM_FLIPPER_ID),
					po.getString(FlipperDatabaseHandler.COMM_TEXTE),
					po.getString(FlipperDatabaseHandler.COMM_DATE),
					po.getString(FlipperDatabaseHandler.COMM_PSEUDO),
					po.getBoolean(FlipperDatabaseHandler.COMM_ACTIF));
			listeCommentaire.add(commentaire);
		}
		return listeCommentaire;
	}

	public boolean ajouteCommentaire(final Context pContext, final Commentaire commentaire){
		ParseObject parseCommentaire = new ParseObject(FlipperDatabaseHandler.COMMENTAIRE_TABLE_NAME);
		parseCommentaire.put(FlipperDatabaseHandler.COMM_ID, commentaire.getId());
		parseCommentaire.put(FlipperDatabaseHandler.COMM_FLIPPER_ID, commentaire.getFlipperId());
		parseCommentaire.put(FlipperDatabaseHandler.COMM_DATE, commentaire.getDate());
		parseCommentaire.put(FlipperDatabaseHandler.COMM_PSEUDO, commentaire.getPseudo());
		parseCommentaire.put(FlipperDatabaseHandler.COMM_TEXTE, commentaire.getTexte());
		parseCommentaire.put(FlipperDatabaseHandler.COMM_ACTIF, commentaire.getActif());
		parseCommentaire.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null){
					BaseCommentaireService baseCommentaireService = new BaseCommentaireService();
					baseCommentaireService.addCommentaire(commentaire, pContext);
					Toast toast = Toast.makeText(pContext, pContext.getResources().getString(R.string.toastAjouteCommentaireCloudOK), Toast.LENGTH_LONG);
					toast.show();
					if (mFragmentCallback != null){
						mFragmentCallback.onTaskDone();
					}
				}else{
					Toast toast = Toast.makeText(pContext, pContext.getResources().getString(R.string.toastAjouteCommentaireCloudKO), Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
		return true;
	}

}
