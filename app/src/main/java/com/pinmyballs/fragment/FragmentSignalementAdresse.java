package com.pinmyballs.fragment;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.BindView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.pinmyballs.R;
import com.pinmyballs.metier.Enseigne;
import com.pinmyballs.utils.LocationUtil;

import java.util.HashMap;

public class FragmentSignalementAdresse extends SignalementWizardFragment implements PlaceSelectionListener {

    @BindView(R.id.place_attribution_wizard)
    TextView mPlaceAttribution;
    @BindView(R.id.champNomEnseigne)
    TextView champNomEnseigne;
    @BindView(R.id.champAdresse)
    TextView champAdresse;
    @BindView(R.id.champCodePostal)
    TextView champCodePostal;
    @BindView(R.id.champVille)
    TextView champVille;
    @BindView(R.id.champPays)
    TextView champPays;
    PlaceAutocompleteFragment autocompleteFragment = new PlaceAutocompleteFragment();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wizard_adresse, container, false);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this, rootView);

        //Insert the PlaceAutoCompleteFragment
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.placeSearchBar, autocompleteFragment);
        fragmentTransaction.commit();

        // Register a listener to receive callbacks when a place has been selected or an error has
        // occurred.
        autocompleteFragment.setOnPlaceSelectedListener(this);

        return rootView;
    }

    public boolean mandatoryFieldsComplete() {
        boolean isError = false;
        if (champAdresse.getText().length() == 0) {
            new AlertDialog.Builder(getActivity()).setTitle("Envoi impossible!").setMessage("Vous devez renseigner l'adresse du flipper.").setNeutralButton("Fermer", null).setIcon(R.drawable.ic_delete).show();
            isError = true;
        }
        if (champCodePostal.getText().length() == 0) {
            new AlertDialog.Builder(getActivity()).setTitle("Envoi impossible!").setMessage("Vous devez renseigner le code postal du flipper.").setNeutralButton("Fermer", null).setIcon(R.drawable.ic_delete).show();
            isError = true;
        }
        if (champVille.getText().length() == 0) {
            new AlertDialog.Builder(getActivity()).setTitle("Envoi impossible!").setMessage("Vous devez renseigner la ville.").setNeutralButton("Fermer", null).setIcon(R.drawable.ic_delete).show();
            isError = true;
        }
        if (champPays.getText().length() == 0) {
            new AlertDialog.Builder(getActivity()).setTitle("Envoi impossible!").setMessage("Vous devez renseigner le pays.").setNeutralButton("Fermer", null).setIcon(R.drawable.ic_delete).show();
            isError = true;
        }
        if (champNomEnseigne.getText().length() == 0) {
            new AlertDialog.Builder(getActivity()).setTitle("Envoi impossible!").setMessage("Vous devez renseigner le nom de l'enseigne.").setNeutralButton("Fermer", null).setIcon(R.drawable.ic_delete).show();
            isError = true;
        }
        return !isError;
    }

    public void completeStep() {
        Enseigne newEnseigne = new Enseigne();
        newEnseigne.setAdresse(champAdresse.getText().toString());
        newEnseigne.setCodePostal(champCodePostal.getText().toString());
        newEnseigne.setDateMaj(getFormattedDate());
        newEnseigne.setId(getNewEnseigneId());
        newEnseigne.setLatitude(String.valueOf(getCurrentLocation().latitude));
        newEnseigne.setLongitude(String.valueOf(getCurrentLocation().longitude));
        newEnseigne.setNom(champNomEnseigne.getText().toString());
        newEnseigne.setPays(champPays.getText().toString());
        newEnseigne.setVille(champVille.getText().toString());
        getParentActivity().setEnseigne(newEnseigne);
    }


    @Override
    public void onPlaceSelected(Place place) {

        champNomEnseigne.setText(place.getName().toString());

        HashMap HM = LocationUtil.getDetailsfromLatLng(getContext(), place.getLatLng());
        champAdresse.setText(String.valueOf(HM.get("address")));
        champCodePostal.setText(String.valueOf(HM.get("postalcode")));
        champVille.setText(String.valueOf(HM.get("city")));
        champPays.setText(String.valueOf(HM.get("country")));
        getParentActivity().setNewLocation(new LatLng(place.getLatLng().latitude,place.getLatLng().longitude));

/*
        List<Address> listAddress = null;
        Geocoder geocoder = new Geocoder(getContext());
        try {
            listAddress = geocoder.getFromLocationName(place.getAddress().toString(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (listAddress != null && !listAddress.isEmpty()) {
            Address address = listAddress.get(0);
            champAdresse.setText(address.getAddressLine(0));
            champCodePostal.setText(address.getPostalCode());
            champVille.setText(address.getLocality());
            champPays.setText(address.getCountryName());
            getParentActivity().setNewLocation(new LatLng(listAddress.get(0).getLatitude(),listAddress.get(0).getLongitude()));


        } else {
            android.util.Log.i("LieuVide", listAddress.isEmpty() ? "list nulle" : "list non nulle");
            Toast.makeText(getContext(), "Lieu non trouvé par Google", Toast.LENGTH_SHORT).show();
        }

        //Attributions
        //TODO USEFUL OR NOT ?
        CharSequence attributions = place.getAttributions();
        if (!TextUtils.isEmpty(attributions)) {
            mPlaceAttribution.setText(Html.fromHtml(attributions.toString()));
        } else {
            mPlaceAttribution.setText("");
        }
        */
    }

    @Override
    public void onError(Status status) {
        Toast.makeText(getContext(), "Place selection failed: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        /*if (autocompleteFragment != null) {
            FragmentManager fragmentManager = getActivity().getFragmentManager();
            android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(autocompleteFragment);
            fragmentTransaction.commit();
        }*/
        super.onDestroyView();
    }
}
