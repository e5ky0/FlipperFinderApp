package com.pinmyballs.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pinmyballs.R;



public class FragmentFlipperInfo extends Fragment implements OnMapReadyCallback{

    private GoogleMap mMap;
    MapView mapView;
    View mView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View rootView = inflater.inflate(R.layout.fragment_tab1_info, container, false);
        //return rootView;
        mView = inflater.inflate(R.layout.fragment_tab1_info,container,false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) mView.findViewById(R.id.mapinfo);
        if (mapView!=null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        MapsInitializer.initialize(getContext());

        mMap = map;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        map.addMarker(new MarkerOptions().position(new LatLng(40,20)).title("mymarker").snippet("hey hey i like that"));
        CameraPosition liberty = CameraPosition.builder().target(new LatLng(40,20)).zoom(16).bearing(0).tilt(45).build();

        map.moveCamera(CameraUpdateFactory.newCameraPosition(liberty));
    }
}
