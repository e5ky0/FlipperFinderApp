package fr.fafsapp.flipper.finder;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import java.util.ArrayList;

import fr.fafsapp.flipper.finder.metier.Commentaire;
import fr.fafsapp.flipper.finder.service.GlobalService;
import fr.fafsapp.flipper.finder.utils.ListeCommentaireAdapter;

public class PageListeCommentaire extends ActionBarActivity {

	ArrayList<Commentaire> listeCommentaires = new ArrayList<Commentaire>();

	ListView listeCommentaireView = null;
	private int NB_MAX_COMMENTAIRE = 20;
	ActionBar mActionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_liste_commentaire);
		mActionBar = getSupportActionBar();
		mActionBar.setTitle(R.string.headerCommentaire);

		listeCommentaireView = (ListView) findViewById(R.id.listeCommentairesRecents);

		GlobalService globalService = new GlobalService();

		listeCommentaires = globalService.getLastCommentaire(getApplicationContext(), NB_MAX_COMMENTAIRE);
		ListeCommentaireAdapter customAdapter = new ListeCommentaireAdapter(this, R.layout.simple_list_item_commentaire, listeCommentaires);
		listeCommentaireView.setAdapter(customAdapter);
	}


	@Override
	public void onStart() {
		super.onStart();
		// Google Analytics
		//EasyTracker.getInstance().activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		// Google Analytics
		//EasyTracker.getInstance().activityStop(this);
	}

}
