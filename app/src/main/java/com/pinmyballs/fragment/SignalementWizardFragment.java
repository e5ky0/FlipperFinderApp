package com.pinmyballs.fragment;

import android.support.v4.app.Fragment;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.pinmyballs.PageSignalementNew;

public abstract class SignalementWizardFragment extends Fragment{

    public abstract void completeStep();
    public abstract boolean mandatoryFieldsComplete();

    public PageSignalementNew getParentActivity(){
        return (PageSignalementNew) getActivity();
    }

    public long getNewEnseigneId(){
        return getParentActivity().getNewId();
    }

    public LatLng getCurrentLocation(){
        return getParentActivity().getCurrentLocation();
    }

    public String getFormattedDate(){
        Date dateDuJour = new Date();
        String dateMaj = new SimpleDateFormat("yyyy/MM/dd", Locale.FRANCE).format(dateDuJour);
        return dateMaj;
    }
}
