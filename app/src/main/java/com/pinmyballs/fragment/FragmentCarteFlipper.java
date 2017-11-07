package com.pinmyballs.fragment;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

import com.pinmyballs.R;
import com.pinmyballs.metier.Flipper;

public class FragmentCarteFlipper extends SupportMapFragment implements LocationListener, LocationSource  {

	public final static String INTENT_FLIPPER_INFO_TABMAP = "com.pinmyballs.fragment.FragmentCarteFlipper.INTENT_FLIPPER_INFO_TABMAP";

	private OnLocationChangedListener mListener;

	private GoogleMap gMap = null;
	LatLngBounds.Builder builder = null;

	private Flipper flipperToDisplay = new Flipper();

	public void setFlipperToDisplay(Flipper flip){
		flipperToDisplay = flip;
	}

	private LocationManager locationManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = super.onCreateView(inflater, container, savedInstanceState);

		locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

		gMap = getMap();
		if (gMap != null){
			gMap.setMyLocationEnabled(true);
			gMap.clear();

			builder = new LatLngBounds.Builder();

			final Map<String, Flipper> markerObjMap = new HashMap<String, Flipper>();

			// On parcourt la liste des flippers pour les afficher avec la
			// magnifique icone
			Marker marker;
			if (flipperToDisplay != null && flipperToDisplay.getEnseigne() != null){
				LatLng pos = new LatLng(Double.valueOf(flipperToDisplay.getEnseigne().getLatitude()),
						Double.valueOf(flipperToDisplay.getEnseigne().getLongitude()));

				MarkerOptions markerOpt = new MarkerOptions().position(pos).icon(
						BitmapDescriptorFactory.fromResource(R.drawable.ic_flipper));
				marker = gMap.addMarker(markerOpt);
				markerObjMap.put(marker.getId(), flipperToDisplay);

				builder.include(pos);

				// On centre la carte
				LatLngBounds bounds = builder.build();
				gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.northeast, 15));
			}
		}else{
			int isGPSAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity().getApplicationContext());
			if (isGPSAvailable != ConnectionResult.SUCCESS){
				//EasyTracker.getTracker().sendEvent("ui_error", "GPS_ERROR", "fragmentMapActivity", 0L);
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isGPSAvailable, getActivity(), 0);
				if (dialog != null){
					dialog.show();
				}
			}
		}

		return rootView;
	}

	@Override
	public void onPause() {
		if (locationManager != null) {
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
	public void onStart() {
		super.onStart();
		//EasyTracker.getInstance().activityStart(getActivity());
	}

	@Override
	public void onStop() {
		super.onStop();
		//EasyTracker.getInstance().activityStop(getActivity());
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

}
