package com.pinmyballs.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.pinmyballs.PageInfoTournoi;
import com.pinmyballs.PageListeResultatTournois;
import com.pinmyballs.R;
import com.pinmyballs.metier.Tournoi;

/**
 * Classe pour un item de la liste de flipper de l'activité
 * PageListeResultat
 * @author Fafouche
 *
 */
public class ListeTournoiAdapter extends ArrayAdapter<Tournoi> {

	public ListeTournoiAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	private List<Tournoi> listeTournois;
	private double latitude = 0;
	private double longitude = 0;
	private Context mContext;

	public ListeTournoiAdapter(Context context, int resource, List<Tournoi> items, double latitude, double longitude) {

		super(context, resource, items);

		this.listeTournois = items;
		this.latitude = latitude;
		this.longitude = longitude;
		this.mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;

		if (v == null) {
			LayoutInflater vi;
			vi = LayoutInflater.from(getContext());
			v = vi.inflate(R.layout.simple_list_item_tournoi, null);
		}

		// On set les tags pour pouvoir retrouver sur quelle ligne on a cliqué.
		v.setTag(position);
		v.setOnClickListener(InfoTournoiClickListener);

		Tournoi p = listeTournois.get(position);

		if (p != null) {

			TextView nomTV = (TextView) v.findViewById(R.id.textModeleFlipper);
			TextView adresseTV = (TextView) v.findViewById(R.id.textAdresseFlipper);
			TextView distanceTV = (TextView) v.findViewById(R.id.distance);
			TextView dateTV = (TextView) v.findViewById(R.id.dateMaj);
			LinearLayout navigationLayout = (LinearLayout) v.findViewById(R.id.navigationLayout);

			if (nomTV != null) {
				nomTV.setText(p.getNom());
			}
			if (adresseTV != null) {
				adresseTV.setText(p.getAdresse() + " " + p.getVille());
			}
			if (distanceTV != null) {
				float[] resultDistance = new float[5];
				Location.distanceBetween(latitude, longitude, Double.valueOf(p.getLatitude()),
						Double.valueOf(p.getLongitude()), resultDistance);
				Float distanceFloat = resultDistance[0];
				distanceTV.setText(LocationUtil.formatDist(distanceFloat));
			}

			dateTV.setText("Tournoi prévu le " + p.getDate() + ".");
			navigationLayout.setTag(position);
			navigationLayout.setOnClickListener(NavigationTournoiClickListener);
		}
		return v;
	}

	private OnClickListener NavigationTournoiClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Tournoi p = listeTournois.get((Integer) v.getTag());
			Intent navIntentGoogleNav = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="
						+ p.getAdresseComplete()));

			Intent navIntentWaze = new Intent(Intent.ACTION_VIEW, Uri.parse("waze://?q="
						+ p.getAdresseComplete()));

			if (LocationUtil.canHandleIntent(mContext.getApplicationContext(), navIntentGoogleNav)) {
				navIntentGoogleNav.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(navIntentGoogleNav);
			} else if (LocationUtil.canHandleIntent(mContext.getApplicationContext(), navIntentWaze)) {
				navIntentGoogleNav.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(navIntentWaze);
			} else {
				Toast.makeText(mContext.getApplicationContext(), "Merci d'installer Google Navigation ou Waze", Toast.LENGTH_SHORT).show();
			}
		}
	};

	private OnClickListener InfoTournoiClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			if (NetworkUtil.isConnected(getContext())){
				//EasyTracker.getTracker().sendEvent("ui_action", "button_press", "item_tournoi", 0L);
				Tournoi p = listeTournois.get((Integer) v.getTag());
				Intent infoActivite = new Intent(getContext(), PageInfoTournoi.class);
				infoActivite.putExtra(PageListeResultatTournois.INTENT_TOURNOI_POUR_INFO, p);
				getContext().startActivity(infoActivite);
			}else{
				new AlertDialog.Builder(getContext())
					.setTitle("Argh!")
					.setMessage(
							"Vous devez être connecté à internet pour voir les infos du tournoi.")
					.setNeutralButton("Fermer", null).setIcon(R.drawable.tete_martiens).show();
			}
		}
	};

}
