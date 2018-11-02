package com.pinmyballs.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pinmyballs.PageCarteFlipper;
import com.pinmyballs.PageInfoFlipperPager;
import com.pinmyballs.R;
import com.pinmyballs.metier.Flipper;

/**
 * Classe pour un item de la liste de flipper de l'activité
 * PageListeResultat
 * @author Fafouche
 *
 */
public class ListeFlipperAdapter extends ArrayAdapter<Flipper> {

	public ListeFlipperAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	private List<Flipper> listeFlippers;
	private double latitude = 0;
	private double longitude = 0;

	public ListeFlipperAdapter(Context context, int resource, List<Flipper> items, double latitude, double longitude) {

		super(context, resource, items);

		this.listeFlippers = items;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;

		if (v == null) {
			LayoutInflater vi;
			vi = LayoutInflater.from(getContext());
			v = vi.inflate(R.layout.simple_list_item_flipper, null);
		}

		// On set les tags pour pouvoir retrouver sur quelle ligne on a cliqué.
		v.setTag(position);
		v.setOnClickListener(InfoFlipperClickListener);

		Flipper p = listeFlippers.get(position);

		if (p != null) {

			ImageView markerIcone = (ImageView) v.findViewById(R.id.markericon);
			TextView modeleTV = (TextView) v.findViewById(R.id.textModeleFlipper);
			TextView adresseTV = (TextView) v.findViewById(R.id.textAdresseFlipper);
			TextView distanceTV = (TextView) v.findViewById(R.id.distance);
			TextView dateMajTV = (TextView) v.findViewById(R.id.dateMaj);
			TextView nomBar = (TextView) v.findViewById(R.id.nomBar);
			ImageView warningImage =(ImageView) v.findViewById(R.id.warningicon);

			if (modeleTV != null) {
				modeleTV.setText(p.getModele().getNomComplet());
			}
			if (adresseTV != null) {
				adresseTV.setText(p.getEnseigne().getAdresse() + " " + p.getEnseigne().getVille());
			}
			if (distanceTV != null) {
				float[] resultDistance = new float[5];
				Location.distanceBetween(latitude, longitude, Double.valueOf(p.getEnseigne().getLatitude()),
						Double.valueOf(p.getEnseigne().getLongitude()), resultDistance);
				Float distanceFloat = resultDistance[0];
				distanceTV.setText(LocationUtil.formatDist(distanceFloat));
			}
            if (nomBar != null) {
                nomBar.setText(p.getEnseigne().getNom());
            }

			// Affichage de la date de mise à jour
			int nbJours = LocationUtil.getDaysSinceMajFlip(p);
            dateMajTV.setTextColor(Color.parseColor("#04B404"));
            warningImage.setVisibility(View.GONE);
            markerIcone.setImageResource((MarkerChoice(nbJours)));


            if (nbJours == -1){
				// Date nulle ou mal formattée : Rouge!
				dateMajTV.setTextColor(Color.parseColor("#FE2E2E"));
				dateMajTV.setText(getContext().getResources().getString(R.string.dateMajDefault));
                warningImage.setVisibility(View.VISIBLE);
            }else if (nbJours > 365){
				// Mis à jour il y a plus de 365 jours, on met en Rouge
				dateMajTV.setTextColor(Color.parseColor("#FE2E2E"));
				dateMajTV.setText("Vu il y a " + String.valueOf(nbJours) + " jours.");
				warningImage.setVisibility(View.VISIBLE);
			}else if (nbJours > 60){
                // Mis à jour il y a plus de 60 jours, on met en Orange
				dateMajTV.setTextColor(Color.parseColor("#E68A00"));
				dateMajTV.setText("Vu il y a " + String.valueOf(nbJours) + " jours.");
			}else if (nbJours == 0){
				// Mis à jour aujourd'hui
				//dateMajTV.setTextColor(Color.parseColor("#04B404"));
				dateMajTV.setText("Vu aujourd'hui.");
			}else if (nbJours == 1){
				// Confirmé hier
				//dateMajTV.setTextColor(Color.parseColor("#04B404"));
				dateMajTV.setText("Vu hier.");
			}else{
				// Mis à jour récemment, on met en vert
				//dateMajTV.setTextColor(Color.parseColor("#04B404"));
				dateMajTV.setText("Vu il y a " + String.valueOf(nbJours) + " jours.");
			}
		}

		return v;
	}

	private int MarkerChoice(int nbJours){
		if (nbJours < 8) {
			return R.mipmap.ic_flipmarker_new;
		}
		if (nbJours < 60) {
			return R.mipmap.ic_flipmarker_blue;
		}
		if (nbJours < 365) {
			return R.mipmap.ic_flipmarker_lightblue;
		}
		if (nbJours > 365) {
			return R.mipmap.ic_flipmarker_grey;
		}
		return R.mipmap.ic_flipmarker_grey;
	}


	private OnClickListener InfoFlipperClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//EasyTracker.getTracker().sendEvent("ui_action", "button_press", "item_info_flipper", 0L);

			Flipper p = listeFlippers.get((Integer) v.getTag());
			Intent infoActivite = new Intent(getContext(), PageInfoFlipperPager.class);
			infoActivite.putExtra(PageCarteFlipper.INTENT_FLIPPER_POUR_INFO, p);
			// On va sur l'onglet de la carte
			infoActivite.putExtra(PageInfoFlipperPager.INTENT_FLIPPER_ONGLET_DEFAUT, 0);
			getContext().startActivity(infoActivite);

			//TODO Supprimer ancienne lien vers interface
			//Flipper p = listeFlippers.get((Integer) v.getTag());
			//Intent toflipperpage = new Intent(getContext(), PageFlipper.class);
			//toflipperpage.putExtra(PageCarteFlipper.INTENT_FLIPPER_POUR_INFO, p);

			//getContext().startActivity(toflipperpage);
        }
	};

}
