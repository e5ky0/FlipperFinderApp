package com.pinmyballs;


import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionBarContainer;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
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

import static com.pinmyballs.PagePreferences.KEY_PREFERENCES_RAYON;
import static com.pinmyballs.PagePreferences.PREFERENCES_FILENAME;

public class PageCarteSearch extends AppCompatActivity implements OnMapReadyCallback {

	public final static String INTENT_LATITUDE = "com.pinmyballs.PageCarteFlipper.INTENT_LATITUDE";
    public final static String INTENT_LONGITUDE = "com.pinmyballs.PageCarteFlipper.INTENT_LONGITUDE";

	//private OnLocationChangedListener mListener;
	// LatLngBounds.Builder builder = null;
    LatLng coord = new LatLng(45, 3 );
    SharedPreferences settings;
    double circleradius;
    ActionBar mActionbar;
	private GoogleMap gMap = null;
    private LocationManager locationManager;
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
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
        settings = getSharedPreferences(PREFERENCES_FILENAME, 0);
        circleradius = settings.getInt(KEY_PREFERENCES_RAYON, 50);

        Button cherche = (Button) findViewById(R.id.searchThisArea);
        cherche.setOnClickListener(chercheListener);

        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);

        mActionbar = getSupportActionBar();
        mActionbar.setTitle(R.string.headerPageCarteSearch);
        mActionbar.setHomeButtonEnabled(true);
        mActionbar.setDisplayHomeAsUpEnabled(true);

        //gMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView)).getMap();
        //builder = new LatLngBounds.Builder();
    }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            gMap = googleMap;
            gMap.setMyLocationEnabled(true);
            gMap.getUiSettings().setMyLocationButtonEnabled(true);
            gMap.getUiSettings().setZoomControlsEnabled(true);
            Intent i = getIntent();
            coord = new LatLng(Double.parseDouble(i.getStringExtra(PageListeResultat.INTENT_LATITUDE)),
                    Double.parseDouble(i.getStringExtra(PageListeResultat.INTENT_LONGITUDE)));

            gMap.addMarker(new MarkerOptions().position(coord));
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord, 7));

            googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    gMap.clear();
                    gMap.addMarker(new MarkerOptions()
                            .position(new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            .title("new")
                            .visible(false));
                    gMap.addCircle(new CircleOptions()
                            .center(new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude))
                            .radius(circleradius*1000)
                            .fillColor(0x4033cc33)
                            .strokeColor(Color.GREEN)
                            .strokeWidth((float) 5));
                }
            });
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_preferences, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_preferences:
                ////EasyTracker.getTracker().sendEvent("ui_action", "button_press", "preferences", 0L);
                Intent intent4 = new Intent(PageCarteSearch.this, PagePreferences.class);
                startActivity(intent4);
                break;
            default:
                Log.i("Erreur action bar","default");
                break;
        }
        return false;
    }

	@Override
	public void onResume() {
		super.onResume();
        settings = getSharedPreferences(PREFERENCES_FILENAME, 0);
        circleradius = settings.getInt(KEY_PREFERENCES_RAYON, 50);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
	    super.onResume();
	}

	@Override
	public void onPause() {
        super.onPause();
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
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//remove super() call
	}
}
