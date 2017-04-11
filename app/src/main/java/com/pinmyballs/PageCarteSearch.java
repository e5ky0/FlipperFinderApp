package com.pinmyballs;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pinmyballs.metier.Flipper;
import com.pinmyballs.utils.LocationUtil;
import com.pinmyballs.utils.MyLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PageCarteSearch extends FragmentActivity implements
LocationListener, LocationSource {

	public final static String INTENT_LATITUDE = "com.pinmyballs.PageCarteFlipper.INTENT_LATITUDE";
    public final static String INTENT_LONGITUDE = "com.pinmyballs.PageCarteFlipper.INTENT_LONGITUDE";

	private OnLocationChangedListener mListener;

	private GoogleMap gMap = null;
	LatLngBounds.Builder builder = null;
    LatLng coord = new LatLng(45, 3 );
    private LocationManager locationManager;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_search);

        Button cherche = (Button) findViewById(R.id.searchThisArea);
        cherche.setOnClickListener(chercheListener);

		locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);

		gMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView)).getMap();
        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        gMap.getUiSettings().setZoomControlsEnabled(true);


        Intent i = getIntent();
        coord = new LatLng(Double.parseDouble( i.getStringExtra(PageListeResultat.INTENT_LATITUDE)),
                Double.parseDouble(i.getStringExtra(PageListeResultat.INTENT_LONGITUDE)));

        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord, 7));
        gMap.clear();

		builder = new LatLngBounds.Builder();





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

    private View.OnClickListener chercheListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent coordIntent = new Intent(PageCarteSearch.this, PageListeResultat.class);
            LatLng center = gMap.getCameraPosition().target;
            coordIntent.putExtra(INTENT_LATITUDE, String.valueOf(center.latitude));
            coordIntent.putExtra(INTENT_LONGITUDE, String.valueOf(center.longitude));
            startActivity(coordIntent);
        }
    };


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
}
