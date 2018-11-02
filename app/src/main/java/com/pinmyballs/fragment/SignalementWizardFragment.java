package com.pinmyballs.fragment;

import android.support.v4.app.Fragment;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.pinmyballs.SignalementActivity;

public abstract class SignalementWizardFragment extends Fragment{

    public abstract void completeStep();
    public abstract boolean mandatoryFieldsComplete();

    public SignalementActivity getParentActivity(){
        return (SignalementActivity) getActivity();
    }

    public long getNewEnseigneId(){
        return getParentActivity().getNewId();
    }

    public LatLng getCurrentLocation(){
        return getParentActivity().getCurrentLocation();
    }

    public String getFormattedDate(){
        Date dateDuJour = new Date();
        return new SimpleDateFormat("yyyy/MM/dd", Locale.FRANCE).format(dateDuJour);
    }
}
