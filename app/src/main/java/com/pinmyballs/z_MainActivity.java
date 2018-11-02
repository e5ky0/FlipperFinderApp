package com.pinmyballs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.pinmyballs.database.FlipperDatabaseHandler;
import com.pinmyballs.metier.Flipper;
import com.pinmyballs.service.base.BaseFlipperService;
import com.pinmyballs.utils.FontManager;
import com.pinmyballs.utils.LocationUtil;
import com.pinmyballs.utils.NetworkUtil;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.pinmyballs.PagePreferences.KEY_PREFERENCES_RAYON;

public class z_MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnInfoWindowClickListener {

    private static final String TAG = z_MainActivity.class.getSimpleName();
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    final Map<String, Flipper> markerObjMap = new HashMap<String, Flipper>();
    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(48.869, 2.3541);
    Double maxDistance = (double) 1;
    Button boutonRechercher;
    Button boutonSignaler;
    Button boutonRechercherTournoi;
    Button boutonAdmin;
    SharedPreferences settings;
    //FOR THE MAP--------------------------------------------------------
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;

    //FOR THE MAP--------------------------------------------------------
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;
    /**
     * The {@link Tracker} used to record screen views.
     */
    private Tracker mTracker;

    int DISTANCE_MAX = 25; // On cherche les flippers à 25km à la ronde
    int ENSEIGNE_LIST_MAX_SIZE = 50; // On cherche les 50 flippers à la ronde

    //ImageView imageEnveloppe;
    //ImageView imagePreferences;
    private OnClickListener RechercherListener = new OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(z_MainActivity.this, PageListeResultat.class);
            startActivity(intent);
        }
    };
    private OnClickListener SignalerListener = new OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(z_MainActivity.this, SignalementActivity.class);
            //Intent intent = new Intent(z_MainActivity.this, SignalementMailActivity.class);
            startActivity(intent);
        }
    };

	/*private OnClickListener ContactPrincipalListener = new OnClickListener() {
		public void onClick(View v) {
			Resources resources = getResources();
			String emailsTo = resources.getString(R.string.mailContact);
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/html");

			i.putExtra(Intent.EXTRA_EMAIL, new String[]{emailsTo});
			i.putExtra(Intent.EXTRA_SUBJECT, "Commentaire sur l'application");
			try {
				startActivity(Intent.createChooser(i, "Envoi du mail"));
			} catch (android.content.ActivityNotFoundException ex) {
				new AlertDialog.Builder(z_MainActivity.this).setTitle("Envoi impossible!").setMessage("Vous n'avez pas de mail configuré sur votre téléphone.").setNeutralButton("Fermer", null).setIcon(R.drawable.ic_tristesse).show();
			}
		}
	};*/
    private OnClickListener RechercherTournoiListener = new OnClickListener() {
        public void onClick(View v) {

        }
    };
    private OnClickListener AdminListener = new OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(z_MainActivity.this, PageAdmin.class);
            startActivity(intent);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Make sure this is before calling super.onCreate

        // Obtain the shared Tracker instance.
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();

        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);

        Intent i = new Intent(z_MainActivity.this, HomeActivity.class);
        startActivity(i);
        finish();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.z_activity_main);

        boutonRechercher = (Button) findViewById(R.id.boutonMenuRechercher);
        boutonRechercher.setOnClickListener(RechercherListener);
        boutonRechercherTournoi = (Button) findViewById(R.id.boutonMenuTournoi);
        boutonRechercherTournoi.setOnClickListener(RechercherTournoiListener);
        boutonSignaler = (Button) findViewById(R.id.boutonMenuSignaler);
        boutonSignaler.setOnClickListener(SignalerListener);
        boutonAdmin = (Button) findViewById(R.id.boutonMenuAdmin);
        boutonAdmin.setOnClickListener(AdminListener);

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.mainactivitylayout), iconFont);

        //imageEnveloppe = (ImageView) findViewById(R.id.imageEnveloppe);
        //imageEnveloppe.setOnClickListener(ContactPrincipalListener);
        //imagePreferences = (ImageView) findViewById(R.id.imagePreferences);
        //imagePreferences.setOnClickListener(PreferencesListener);




        //NEW MINIMAP CODE-------------------------------------------------------------------------
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }


        mLastKnownLocation = new Location("");
        mLastKnownLocation.setLatitude(mDefaultLocation.latitude);
        mLastKnownLocation.setLongitude(mDefaultLocation.longitude);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);
        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.minimap);
        mapFragment.getMapAsync(this);

        showCurrentPlace();

        //NEW MINIMAP CODE-------------------------------------------------------------------------


        settings = getSharedPreferences(PagePreferences.PREFERENCES_FILENAME, 0);
        maxDistance = (double) settings.getInt(KEY_PREFERENCES_RAYON, 1);


        //First we check if PREFERENCES file is set up with values for DATABASE VERSION and DATE_LAST_UPDATE
        // if not we give it the values from FlipperDataBaseHandler
        if (settings.getString(PagePreferences.KEY_PREFERENCES_DATABASE_VERSION, "0").equals("0")) {
            Log.w(z_MainActivity.class.getName(), "Key Pref Database Version not set => InitDB");
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(PagePreferences.KEY_PREFERENCES_DATE_LAST_UPDATE, FlipperDatabaseHandler.DATABASE_DATE_MAJ);
            editor.putString(PagePreferences.KEY_PREFERENCES_DATABASE_VERSION, String.valueOf(FlipperDatabaseHandler.DATABASE_VERSION));
            editor.apply();
        }
        //Then we check if a DATABASE VERSION update has been done by the developer (used the reset the SQLite DB)
        // in which case we reset the DATE_LAST_UPDATE to the default value 2011/01/01
        else {
            if (Integer.parseInt(settings.getString(PagePreferences.KEY_PREFERENCES_DATABASE_VERSION, "0"))
                    < FlipperDatabaseHandler.DATABASE_VERSION) {
                Log.w(z_MainActivity.class.getName(), "New Database Version => Reset DATE_LAST_UPDATE");
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(PagePreferences.KEY_PREFERENCES_DATE_LAST_UPDATE, FlipperDatabaseHandler.DATABASE_DATE_MAJ);
                editor.putString(PagePreferences.KEY_PREFERENCES_DATABASE_VERSION, String.valueOf(FlipperDatabaseHandler.DATABASE_VERSION));
                editor.apply();
            }
        }
        //Check if Update is needed onCreate of Main_activity
        checkIfMajNeeded();

        ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 1);
    }

    private void checkIfMajNeeded() {
        String dateDerniereMajString = settings.getString(PagePreferences.KEY_PREFERENCES_DATE_LAST_UPDATE, FlipperDatabaseHandler.DATABASE_DATE_MAJ);
        int nbJours;
        try {
            Date dateDerniereMaj = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).parse(dateDerniereMajString);
            nbJours = Days.daysBetween(new DateTime(dateDerniereMaj), new DateTime(new Date())).getDays();
            if (nbJours > 365) {
                new AlertDialog.Builder(this).setTitle(R.string.dialogMajDBNeededTitle)
                        .setMessage(getResources().getString(R.string.dialogMajDBNeeded2))
                        .setPositiveButton(R.string.dialogMajDBNeededOK, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                updateDB();
                            }
                        }).setNegativeButton(R.string.dialogMajDBLater, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            } else if (nbJours > 3) {
                new AlertDialog.Builder(this).setTitle(R.string.dialogMajDBNeededTitle)
                        .setMessage(getResources().getString(R.string.dialogMajDBNeeded, nbJours))
                        .setPositiveButton(R.string.dialogMajDBNeededOK, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                updateDB();
                            }
                        }).setNegativeButton(R.string.dialogMajDBLater, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            } else
                Log.w(z_MainActivity.class.getName(), "Update of database not required, last update < 3 days");
        } catch (ParseException ignored) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_newMain:
                Intent i = new Intent(z_MainActivity.this, HomeActivity.class);
                startActivity(i);
                break;
            case R.id.action_update:
                updateDB();
                break;
            case R.id.action_viewComment:
                // [START custom_event]
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("ClickLastComments")
                        .build());
                // [END custom_event]
                //EasyTracker.getTracker().sendEvent("ui_action", "button_press", "viewComment", 0L);
                Intent intent1 = new Intent(z_MainActivity.this, CommentaireActivity.class);
                startActivity(intent1);
                break;
            case R.id.action_mail:
                ////EasyTracker.getTracker().sendEvent("ui_action", "button_press", "mail", 0L);
                Resources resources = getResources();
                String emailsTo = resources.getString(R.string.mailContact);
                Intent intent2 = new Intent(Intent.ACTION_SEND);
                intent2.setType("message/html");
                intent2.putExtra(Intent.EXTRA_EMAIL, new String[]{emailsTo});
                intent2.putExtra(Intent.EXTRA_SUBJECT, "Commentaire sur PinMyBalls");
                try {
                    startActivity(Intent.createChooser(intent2, "Envoi du mail"));
                } catch (android.content.ActivityNotFoundException ex) {
                    new AlertDialog.Builder(z_MainActivity.this).setTitle("Envoi impossible!").setMessage("Vous n'avez pas de mail configuré sur votre téléphone.").setNeutralButton("Fermer", null).setIcon(R.drawable.ic_tristesse).show();
                }
                break;
            case R.id.action_bugreport:
                ////EasyTracker.getTracker().sendEvent("ui_action", "button_press", "mail", 0L);
                Resources resources1 = getResources();
                String emailsTo1 = resources1.getString(R.string.mailContact);
                Intent intent3 = new Intent(Intent.ACTION_SEND);
                intent3.setType("message/html");
                intent3.putExtra(Intent.EXTRA_EMAIL, new String[]{emailsTo1});
                intent3.putExtra(Intent.EXTRA_SUBJECT, "Bug signalé dans PinMyBalls");
                try {
                    startActivity(Intent.createChooser(intent3, "Envoi du mail"));
                } catch (android.content.ActivityNotFoundException ex) {
                    new AlertDialog.Builder(z_MainActivity.this).setTitle("Envoi impossible!").setMessage("Vous n'avez pas de mail configuré sur votre téléphone.").setNeutralButton("Fermer", null).setIcon(R.drawable.ic_tristesse).show();
                }
                break;
            case R.id.action_preferences:
                // [START custom_event]
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("ClickPreferences")
                        .build());
                // [END custom_event]
                //EasyTracker.getTracker().sendEvent("ui_action", "button_press", "preferences", 0L);
                Intent intent4 = new Intent(z_MainActivity.this, PagePreferences.class);
                startActivity(intent4);
                break;
            default:
                Log.i("Erreur action bar", "default");
                break;
        }
        return false;
    }

    public void updateDB() {
        ////EasyTracker.getTracker().sendEvent("ui_action", "button_press", "maj_database", 0L);
        if (NetworkUtil.isConnected(getApplicationContext())) {
            if (NetworkUtil.isConnectedFast(getApplicationContext())) {
                new AsyncTaskMajDatabase(z_MainActivity.this, settings).execute();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.toastMajPasPossibleTropLent), Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.toastMajPasPossible), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

	/*private OnClickListener PreferencesListener = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(z_MainActivity.this, PagePreferences.class);
			startActivity(intent);
		}
	};*/

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }


    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnCameraIdleListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.minimap), false);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        // Do other setup activities here too, as described elsewhere in this tutorial.

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        /* Optional : show Marker on mLastKnowLocation
        if (mLastKnownLocation != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()))
                    .title("LastKnownLocation")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))

            );
        }
        */

    }


    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                mFusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    // Set the map's camera position to the current location of the device.
                                    mLastKnownLocation = location;

                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                            new LatLng(mLastKnownLocation.getLatitude(),
                                                    mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                } else {
                                    Log.d(TAG, "Current location is null. Using defaults.");
                                    mMap.moveCamera(CameraUpdateFactory
                                            .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                                    mMap.getUiSettings().setMyLocationButtonEnabled(false);

                                }
                                //Get the nearbyEnseignes
                                //EnseigneSearchTask enseignSearchTask = new EnseigneSearchTask();
                                //enseignSearchTask.execute(mMap.getCameraPosition().target.latitude,mMap.getCameraPosition().target.longitude,maxDistance );
                                //FlipperSearchTask flipperSearchTask =new FlipperSearchTask();
                                //flipperSearchTask.execute(mMap.getCameraPosition().target.latitude,mMap.getCameraPosition().target.longitude,maxDistance);
                                //LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                                //flipperSearchTask.execute(bounds);

                            }
                        });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }


    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission") final Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener
                    (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                                // Set the count, handling cases where less than 5 entries are returned.
                                int count;
                                if (likelyPlaces.getCount() < M_MAX_ENTRIES) {
                                    count = likelyPlaces.getCount();
                                } else {
                                    count = M_MAX_ENTRIES;
                                }

                                int i = 0;
                                mLikelyPlaceNames = new String[count];
                                mLikelyPlaceAddresses = new String[count];
                                mLikelyPlaceAttributions = new String[count];
                                mLikelyPlaceLatLngs = new LatLng[count];

                                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                    // Build a list of likely places to show the user.
                                    mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                                    mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace()
                                            .getAddress();
                                    mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
                                            .getAttributions();
                                    mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                                    i++;
                                    if (i > (count - 1)) {
                                        break;
                                    }
                                }

                                // Release the place likelihood buffer, to avoid memory leaks.
                                likelyPlaces.release();

                                // Show a dialog offering the user the list of likely places, and add a
                                // marker at the selected place.
                                openPlacesDialog();

                            } else {
                                Log.e(TAG, "Exception: %s", task.getException());
                            }
                        }
                    });
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));

            // Prompt the user for permission.
            getLocationPermission();
        }
    }

    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The "which" argument contains the position of the selected item.
                LatLng markerLatLng = mLikelyPlaceLatLngs[which];
                String markerSnippet = mLikelyPlaceAddresses[which];
                if (mLikelyPlaceAttributions[which] != null) {
                    markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
                }

                // Add a marker for the selected place, with an info window
                // showing information about that place.
                mMap.addMarker(new MarkerOptions()
                        .title(mLikelyPlaceNames[which])
                        .position(markerLatLng)
                        .snippet(markerSnippet));

                // Position the map's camera at the location of the marker.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                        DEFAULT_ZOOM));
            }
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.pick_place)
                .setItems(mLikelyPlaceNames, listener)
                .show();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onCameraIdle() {
        //Get the nearbyEnseignes
        //EnseigneSearchTask enseignSearchTask = new EnseigneSearchTask();
        //enseignSearchTask.execute(mMap.getCameraPosition().target.latitude,mMap.getCameraPosition().target.longitude,maxDistance);

        FlipperSearchTask flipperSearchTask = new FlipperSearchTask();
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        flipperSearchTask.execute(bounds);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d(TAG, marker.getId());
        Flipper flipper = markerObjMap.get(marker.getId());
        if (flipper == null) {
            return;
        }
        Intent infoActivite = new Intent(z_MainActivity.this, PageInfoFlipperPager.class);
        // On va sur l'onglet des actions
        infoActivite.putExtra(PageInfoFlipperPager.INTENT_FLIPPER_ONGLET_DEFAUT, 1);
        infoActivite.putExtra(PageCarteFlipper.INTENT_FLIPPER_POUR_INFO, flipper);
        startActivity(infoActivite);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Google Analytics
        //EasyTracker.getInstance().activityStart(this);
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "Setting screen name: " + TAG);
        mTracker.setScreenName("Image~" + TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Google Analytics
        //EasyTracker.getInstance().activityStop(this);
    }

    /**
     * From Coding with Mitch
     *
     * @return
     */
    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(z_MainActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesOK: an error occured but we can resolve it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(z_MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }



    class FlipperSearchTask extends AsyncTask<LatLngBounds, Integer, List<Flipper>> {

        @Override
        protected void onPostExecute(List<Flipper> listFlippers) {
            //TODO crash Ici quand pas de connection : Attempt to invoke interface method 'boolean java.util.List.isEmpty()' on a null object reference
            if(listFlippers == null){
                return;
            }
            for (Flipper flipper : listFlippers) {

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(flipper.getEnseigne().getLatitude()), Double.parseDouble(flipper.getEnseigne().getLongitude())))
                        .icon(BitmapDescriptorFactory.defaultMarker(MarkerColor(flipper)))
                        .title(flipper.getModele().getNom())
                        .snippet(flipper.getEnseigne().getNom() + "\n" + flipper.getEnseigne().getAdresse() + "\n" + flipper.getEnseigne().getCodePostal() + " " + flipper.getEnseigne().getVille()));
                markerObjMap.put(marker.getId(), flipper);
            }
        }

        private float MarkerColor(Flipper flipper) {
            int nbJours = LocationUtil.getDaysSinceMajFlip(flipper);
            if (nbJours > 365) {
                return BitmapDescriptorFactory.HUE_MAGENTA;
            } else if (nbJours > 60) {
                return (float) 45.0; // ORANGE
            }
            return (float) 100.0; // GREEN
        }


        @Override
        protected List<Flipper> doInBackground(LatLngBounds... params) {
            //Log.d("FlipperSearchTask","Background Task Started");
            List<Flipper> listFlippers = null;

            /*
            //Requête Parse
            ParseCloudService parseCloudService = new ParseCloudService();
            try {
                //Log.d("FlipperSearchTask","execute Fetch on " + params[0]);
                listFlippers = parseCloudService.FetchNearbyFlippersBounds(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("FlipperSearchTask","Exception");
            }
            */

            //Requête Locale Sqlite
            BaseFlipperService baseFlipperService = new BaseFlipperService();
            String modeleFlipper = "";
            SharedPreferences settings;
            settings = getSharedPreferences(PagePreferences.PREFERENCES_FILENAME, 0);
            DISTANCE_MAX = settings.getInt(PagePreferences.KEY_PREFERENCES_RAYON, PagePreferences.DEFAULT_VALUE_RAYON);

            ENSEIGNE_LIST_MAX_SIZE = settings.getInt(PagePreferences.KEY_PREFERENCES_MAX_RESULT,
                    PagePreferences.DEFAULT_VALUE_NB_MAX_LISTE);

            try {
                listFlippers = baseFlipperService.rechercheFlipper(getApplicationContext(), params[0].getCenter(),
                        DISTANCE_MAX * 1000, ENSEIGNE_LIST_MAX_SIZE, modeleFlipper);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("FlipperSearchTask", "Exception");
            }


            return listFlippers;
        }
    }


}
