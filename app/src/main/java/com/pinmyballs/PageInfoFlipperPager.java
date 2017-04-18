package com.pinmyballs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.TextView;

import com.pinmyballs.fragment.FragmentActionsFlipper;
import com.pinmyballs.fragment.FragmentCarteFlipper;
import com.pinmyballs.fragment.FragmentCommentaireFlipper;
import com.pinmyballs.fragment.InfoFlipperPagerAdapter;
import com.pinmyballs.metier.Flipper;

public class PageInfoFlipperPager extends AppCompatActivity {

	public final static String INTENT_FLIPPER_ONGLET_DEFAUT = "com.pinmyballs.PageInfoFlipperPager.INTENT_FLIPPER_ONGLET_DEFAUT";


	private ViewPager mPager;

	ActionBar mActionbar;

	Flipper flipper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		// On récupère le flipper concerné
		Intent i = getIntent();
		flipper = (Flipper) i.getSerializableExtra(PageCarteFlipper.INTENT_FLIPPER_POUR_INFO);

		// On récupère l'onglet par défaut.
		int ongletDefaut = i.getIntExtra(PageInfoFlipperPager.INTENT_FLIPPER_ONGLET_DEFAUT, 0);

		setContentView(R.layout.activity_info_flipper);

		/** Getting a reference to action bar of this activity */
		mActionbar = getSupportActionBar();


		Intent actionsIntent = new Intent(this, FragmentActionsFlipper.class);
		actionsIntent.putExtra(PageCarteFlipper.INTENT_FLIPPER_POUR_INFO,flipper);

		Intent carteIntent = new Intent(this, FragmentCarteFlipper.class);
		carteIntent.putExtra(FragmentCarteFlipper.INTENT_FLIPPER_INFO_TABMAP,flipper);

		Intent commentaireIntent = new Intent(this, FragmentCommentaireFlipper.class);
		commentaireIntent.putExtra(PageCarteFlipper.INTENT_FLIPPER_POUR_INFO, flipper);

		/** Getting a reference to action bar of this activity */
		mActionbar = getSupportActionBar();

		/** Set tab navigation mode */
		mActionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mActionbar.setTitle(flipper.getModele().getNom());

		/** Getting a reference to ViewPager from the layout */
		mPager = (ViewPager) findViewById(R.id.pager);

		/** Getting a reference to FragmentManager */
		FragmentManager fm = getSupportFragmentManager();

		/** Defining a listener for pageChange */
		ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener(){
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				mActionbar.setSelectedNavigationItem(position);
			}
		};

		/** Setting the pageChange listener to the viewPager */
		mPager.setOnPageChangeListener(pageChangeListener);

		/** Creating an instance of FragmentPagerAdapter */
		InfoFlipperPagerAdapter fragmentPagerAdapter = new InfoFlipperPagerAdapter(fm, flipper);

		/** Setting the FragmentPagerAdapter object to the viewPager object */
		mPager.setAdapter(fragmentPagerAdapter);

		mActionbar.setDisplayShowTitleEnabled(true);

		/** Defining tab listener */
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				mPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
			}
		};

		/** Creating fragment1 Tab */
		Tab tab = mActionbar.newTab()
			.setText("Carte")
			.setTabListener(tabListener);

		mActionbar.addTab(tab);

		/** Creating fragment2 Tab */
		tab = mActionbar.newTab()
			.setText("Actions")
			.setTabListener(tabListener);

		mActionbar.addTab(tab);

		/** Creating fragment3 Tab */
		tab = mActionbar.newTab()
			.setText("Avis")
			.setTabListener(tabListener);

		mActionbar.addTab(tab);

		/** Creating fragment3 Tab */
		/*
		   tab = mActionbar.newTab()
		   .setText("Hi Scores")
		   .setTabListener(tabListener);

		   mActionbar.addTab(tab);
		   */

		// On écrit les trois infos Adresse / Nom de l'enseigne / Date de mise à jour.


		TextView adresseEnseigne = (TextView) findViewById(R.id.adresseEnseigne);
		TextView nomEnseigne = (TextView) findViewById(R.id.nomEnseigne);
		TextView dateMajFlip = (TextView) findViewById(R.id.dateMajFlip);
		nomEnseigne.setText(flipper.getEnseigne().getNom());
		adresseEnseigne.setText(flipper.getEnseigne().getAdresseCompleteSansPays());

		// Si la date de mise à jour est nulle, on affiche la valeur par défaut.
		if (flipper.getDateMaj() != null && flipper.getDateMaj().length() != 0) {
			dateMajFlip.setText(getResources().getString(R.string.dateMaj) + " " + flipper.getDateMaj());
		} else {
			dateMajFlip.setText(getResources().getString(R.string.dateMajDefault));
		}
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
