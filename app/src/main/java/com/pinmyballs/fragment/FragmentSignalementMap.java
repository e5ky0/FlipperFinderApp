package com.pinmyballs.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.pinmyballs.R;
import com.pinmyballs.utils.LocationUtil;

public class FragmentSignalementMap extends SignalementWizardFragment {

    private static GoogleMap mMap = null;
    private static View rootView;
    @InjectView(R.id.geolocaliseSignalement) Button boutonGeolocalisation;
    LatLng chosenPosition = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null){
                parent.removeView(rootView);
            }
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_wizard_map, container, false);
            ButterKnife.inject(this, rootView);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }

        setUpMapIfNeeded();
        return rootView;
    }

    public void setUpMapIfNeeded() {
        if (mMap == null) {
            SupportMapFragment m = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapSignalementFragment));
            mMap = m.getMap();
            if (mMap != null){
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mMap.clear();
                //add marker to Map
                mMap.addMarker(new MarkerOptions().position(point));
                chosenPosition = point;
            }
        });
    }

    @OnClick(R.id.geolocaliseSignalement)
    public void geolocaliseEnseigne(View view) {
        setUpMapIfNeeded();
        String adresseTexte = getParentActivity().getEnseigne().getAdresseCompleteAvecPays();
        LatLng currentUserLocation = getCurrentLocation();
        LatLng localisationEnseigne = LocationUtil.getAddressFromText(getActivity(), adresseTexte, currentUserLocation.latitude, currentUserLocation.longitude);
        if (localisationEnseigne == null){
            adresseTexte = getParentActivity().getEnseigne().getCodePostal() + " " + getParentActivity().getEnseigne().getVille() + " " + getParentActivity().getEnseigne().getPays();
            localisationEnseigne = LocationUtil.getAddressFromText(getActivity(), adresseTexte, currentUserLocation.latitude, currentUserLocation.longitude);
        }
        if (localisationEnseigne != null){
            updateMapPosition(localisationEnseigne);
            chosenPosition = localisationEnseigne;
        }else{
            new AlertDialog.Builder(getActivity()).setTitle("Géolocalisation impossible!")
                .setMessage("Impossible de géolocaliser l'adresse, vous devez manuellement cliquer sur la carte pour indiquer la position.").setNeutralButton("Fermer", null)
                .setIcon(R.drawable.ic_delete).show();
        }
    }

    private void updateMapPosition(LatLng location){
        // petit marqueur qui va bien
        MarkerOptions markerOpt = new MarkerOptions().position(location);
        if (mMap != null){
            mMap.clear();
            mMap.addMarker(markerOpt);
            // On centre la carte
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setUpMapIfNeeded();
    }

    @Override
    public void completeStep() {
        getParentActivity().setNewLocation(chosenPosition);
        getParentActivity().envoyer();
    }

    @Override
    public boolean mandatoryFieldsComplete() {
        if (chosenPosition == null){
            new AlertDialog.Builder(getActivity()).setTitle("Envoi impossible!")
                .setMessage("Cliquer sur la carte pour renseigner la précision précise de l'enseigne.").setNeutralButton("Fermer", null)
                .setIcon(R.drawable.ic_delete).show();
            return false;
        }else{
            return true;
        }

    }
}
