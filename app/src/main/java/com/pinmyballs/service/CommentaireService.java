package com.pinmyballs.service;

import java.util.ArrayList;

import android.content.Context;
import com.pinmyballs.fragment.FragmentCommentaireFlipper.FragmentCallback;
import com.pinmyballs.metier.Commentaire;
import com.pinmyballs.service.base.BaseCommentaireService;
import com.pinmyballs.service.parse.ParseCommentaireService;

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
