package fr.fafsapp.flipper.finder.service;

import java.util.ArrayList;

import android.content.Context;
import fr.fafsapp.flipper.finder.fragment.FragmentCommentaireFlipper.FragmentCallback;
import fr.fafsapp.flipper.finder.metier.Commentaire;
import fr.fafsapp.flipper.finder.service.base.BaseCommentaireService;
import fr.fafsapp.flipper.finder.service.parse.ParseCommentaireService;

public class CommentaireService {
	private FragmentCallback mFragmentCallback;
	
    public CommentaireService(FragmentCallback fragmentCallback) {
        mFragmentCallback = fragmentCallback;
    }
	
	public boolean ajouteCommentaire(Context pContext, Commentaire commentaire){
		ParseCommentaireService parseCommentaireService = new ParseCommentaireService(mFragmentCallback);
		parseCommentaireService.ajouteCommentaire(pContext, commentaire);
		
		return true;
	}
	public ArrayList<Commentaire> getCommentaireByFlipperId(Context pContext, long idFlipper){
		BaseCommentaireService baseCommentaireService = new BaseCommentaireService();
		return baseCommentaireService.getCommentaireByFlipperId(pContext, idFlipper);
	}
}
