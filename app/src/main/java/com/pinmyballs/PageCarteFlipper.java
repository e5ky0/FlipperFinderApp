package com.pinmyballs;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Display;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.pinmyballs.metier.Flipper;
import com.pinmyballs.utils.LocationUtil;
import com.pinmyballs.utils.MyLocation;

public class PageCarteFlipper extends FragmentActivity implements
LocationListener, LocationSource, OnMapReadyCallback {

	public final static String INTENT_FLIPPER_POUR_INFO = "com.pinmyballs.PageCarteFlipper.INTENT_FLIPPER_POUR_INFO";

	private OnLocationChangedListener mListener;
	private GoogleMap mMap;

	//private GoogleMap gMap = null;
	LatLngBounds.Builder builder = null;

	private LocationManager locationManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_flipper);

		locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);

		// Build the map.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mapView);
		mapFragment.getMapAsync(this);

		/*
		gMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView)).getMapAsync(this);

		gMap.setMyLocationEnabled(true);
		gMap.clear();

		Intent i = getIntent();
		listeFlipper = (ArrayList<Flipper>) i.getSerializableExtra(PageListeResultat.INTENT_FLIPPER_LIST_POUR_MAP);

		builder = new LatLngBounds.Builder();

		final Map<String, Flipper> markerObjMap = new HashMap<String, Flipper>();


		// On parcourt la liste des flippers pour les afficher avec la magnifique icone
		Marker marker = null;
		for (Flipper flipper : listeFlipper) {
			String nom = flipper.getModele().getNom();
			String snippet = flipper.getEnseigne().getNom() + " " + flipper.getEnseigne().getAdresse();
			LatLng pos = new LatLng(Double.valueOf(flipper.getEnseigne().getLatitude()), Double.valueOf(flipper.getEnseigne().getLongitude()));

			// On set l'icone que l'on va utiliser en fonction de l'antériorité de la màj du flipper
			int iconeFlipper = R.drawable.ic_flipper;

			int nbJours = LocationUtil.getDaysSinceMajFlip(flipper);
			if (nbJours == -1){
				// Date nulle on mal formattée : on laisse l'icone noire
			}else if (nbJours > 365){
			// Mis à jour il y a plus de 365 jours, on laisse en noir
			}else if (nbJours > 60){
				// Mis à jour il y a plus de 60 jours, on met en Orange
				iconeFlipper = R.drawable.ic_flipper_orange;
			}else{
				// Mis à jour récemment (moins de 60jours), on met en vert
				iconeFlipper = R.drawable.ic_flipper_vert;
			}

			MarkerOptions markerOpt = new MarkerOptions().position(pos).title(nom).snippet(snippet).icon(BitmapDescriptorFactory.fromResource(iconeFlipper));
			marker = gMap.addMarker(markerOpt);
			markerObjMap.put(marker.getId(), flipper);

			builder.include(pos);
		}

		// Dans le cas où il n'y a qu'un flip, on montre le title tout de suite.
		if (listeFlipper != null && listeFlipper.size() == 1 && marker != null){
			marker.showInfoWindow();
		}

		// On centre la carte
		LatLngBounds bounds = builder.build();
		if (bounds.northeast.equals(bounds.southwest)) {
			// one point
			gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.northeast, 15));
		} else {
			int padding = 100; // offset from edges of the map in pixels
			// On récupère la taille de l'écran, et on met un padding assez fort pour éviter la barre d'état du haut.
			Display display = getWindowManager().getDefaultDisplay();
			CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, display.getWidth(), display.getHeight(), padding);
			gMap.moveCamera(cu);
		}

		gMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				Flipper flipper = markerObjMap.get(marker.getId());
				if(flipper == null) {
					return;
				}
				Intent infoActivite = new Intent(PageCarteFlipper.this, PageInfoFlipperPager.class);
				// On va sur l'onglet des actions
				infoActivite.putExtra(PageInfoFlipperPager.INTENT_FLIPPER_ONGLET_DEFAUT, 1);
				infoActivite.putExtra(INTENT_FLIPPER_POUR_INFO, flipper);
				startActivity(infoActivite);
			}
		});*/


	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		if (!MyLocation.checkLocationPermission(this) && locationManager != null) {
			locationManager.removeUpdates(this);
		}
		super.onPause();
	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
	}

	@Override
	public void deactivate() {
		mListener = null;
	}

	@Override
	public void onProviderDisabled(String provider) {}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}

	@Override
	public void onLocationChanged(Location location) {}

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
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//remove super() call
	}

	@Override
	public void onMapReady(GoogleMap map) {
		//mMap.clear();
		mMap = map;

        Intent i = getIntent();

		ArrayList<Flipper> listeFlipper = (ArrayList<Flipper>) i.getSerializableExtra(PageListeResultat.INTENT_FLIPPER_LIST_POUR_MAP);

		builder = new LatLngBounds.Builder();

		final Map<String, Flipper> markerObjMap = new HashMap<String, Flipper>();


		// On parcourt la liste des flippers pour les afficher avec la magnifique icone
		Marker marker = null;
		for (Flipper flipper : listeFlipper) {
			String nom = flipper.getModele().getNom();
			String snippet = flipper.getEnseigne().getNom() + " " + flipper.getEnseigne().getAdresse();
			LatLng pos = new LatLng(Double.valueOf(flipper.getEnseigne().getLatitude()), Double.valueOf(flipper.getEnseigne().getLongitude()));

			// On set l'icone que l'on va utiliser en fonction de l'antériorité de la màj du flipper
			int iconeFlipper = R.drawable.ic_flipper;

			int nbJours = LocationUtil.getDaysSinceMajFlip(flipper);
			if (nbJours == -1){
				// Date nulle on mal formattée : on laisse l'icone noire
			}else if (nbJours > 365){
				// Mis à jour il y a plus de 365 jours, on laisse en noir
			}else if (nbJours > 60){
				// Mis à jour il y a plus de 60 jours, on met en Orange
				iconeFlipper = R.drawable.ic_flipper_orange;
			}else{
				// Mis à jour récemment (moins de 60jours), on met en vert
				iconeFlipper = R.drawable.ic_flipper_vert;
			}

			MarkerOptions markerOpt = new MarkerOptions().position(pos).title(nom).snippet(snippet).icon(BitmapDescriptorFactory.fromResource(iconeFlipper));
			marker = mMap.addMarker(markerOpt);
			markerObjMap.put(marker.getId(), flipper);
			builder.include(pos);
		}

		// Dans le cas où il n'y a qu'un flip, on montre le title tout de suite.
		if (listeFlipper != null && listeFlipper.size() == 1 && marker != null){
			marker.showInfoWindow();
		}

		// On centre la carte
		LatLngBounds bounds = builder.build();
		if (bounds.northeast.equals(bounds.southwest)) {
			// one point
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.northeast, 15));
		} else {
			int padding = 100; // offset from edges of the map in pixels
			// On récupère la taille de l'écran, et on met un padding assez fort pour éviter la barre d'état du haut.
			Display display = getWindowManager().getDefaultDisplay();
			CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, display.getWidth(), display.getHeight(), padding);
			mMap.moveCamera(cu);
		}

		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				Flipper flipper = markerObjMap.get(marker.getId());
				if(flipper == null) {
					return;
				}
				Intent infoActivite = new Intent(PageCarteFlipper.this, PageInfoFlipperPager.class);
				// On va sur l'onglet des actions
				infoActivite.putExtra(PageInfoFlipperPager.INTENT_FLIPPER_ONGLET_DEFAUT, 1);
				infoActivite.putExtra(INTENT_FLIPPER_POUR_INFO, flipper);
				startActivity(infoActivite);
			}
		});






	}
}
