package com.pinmyballs;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.pinmyballs.metier.Commentaire;
import com.pinmyballs.service.GlobalService;
import com.pinmyballs.utils.BottomNavigationViewHelper;
import com.pinmyballs.utils.ListeCommentaireAdapter;

public class CommentaireActivity extends AppCompatActivity {

	private static final String TAG = "CommentaireActivity";
	private static final int ACTIVITY_NUM = 1;
	private Context mContext = CommentaireActivity.this;


	ArrayList<Commentaire> listeCommentaires = new ArrayList<Commentaire>();

	ListView listeCommentaireView = null;
	private int NB_MAX_COMMENTAIRE = 40;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_actus);
		setupBottomNavigationView();

		listeCommentaireView = (ListView) findViewById(R.id.listeCommentairesRecents);

		GlobalService globalService = new GlobalService();
		listeCommentaires = globalService.getLastCommentaire(getApplicationContext(), NB_MAX_COMMENTAIRE);
		ListeCommentaireAdapter customAdapter = new ListeCommentaireAdapter(this, R.layout.simple_list_item_commentaire, listeCommentaires);
		listeCommentaireView.setAdapter(customAdapter);
	}

    /**
     * Bottom Navigation View Setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

}
