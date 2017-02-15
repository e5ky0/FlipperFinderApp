package fr.fafsapp.flipper.finder.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.fafsapp.flipper.finder.R;
import fr.fafsapp.flipper.finder.metier.Enseigne;

public class FragmentSignalementAdresse extends SignalementWizardFragment {

    @InjectView(R.id.champNomEnseigne) TextView champNomEnseigne;
    @InjectView(R.id.champAdresse) TextView champAdresse;
    @InjectView(R.id.champCodePostal) TextView champCodePostal;
    @InjectView(R.id.champVille) TextView champVille;
    @InjectView(R.id.champPays) TextView champPays;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wizard_adresse, container, false);
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    public boolean mandatoryFieldsComplete(){
        boolean isError = false;
        if (champAdresse.getText().length() == 0){
            new AlertDialog.Builder(getActivity()).setTitle("Envoi impossible!").setMessage("Vous devez renseigner l'adresse du flipper.").setNeutralButton("Fermer", null).setIcon(R.drawable.ic_delete).show();
            isError = true;
        }
        if (champCodePostal.getText().length() == 0){
            new AlertDialog.Builder(getActivity()).setTitle("Envoi impossible!").setMessage("Vous devez renseigner le code postal du flipper.").setNeutralButton("Fermer", null).setIcon(R.drawable.ic_delete).show();
            isError = true;
        }
        if (champVille.getText().length() == 0){
            new AlertDialog.Builder(getActivity()).setTitle("Envoi impossible!").setMessage("Vous devez renseigner la ville.").setNeutralButton("Fermer", null).setIcon(R.drawable.ic_delete).show();
            isError = true;
        }
        if (champPays.getText().length() == 0){
            new AlertDialog.Builder(getActivity()).setTitle("Envoi impossible!").setMessage("Vous devez renseigner le pays.").setNeutralButton("Fermer", null).setIcon(R.drawable.ic_delete).show();
            isError = true;
        }
        if (champNomEnseigne.getText().length() == 0){
            new AlertDialog.Builder(getActivity()).setTitle("Envoi impossible!").setMessage("Vous devez renseigner le nom de l'enseigne.").setNeutralButton("Fermer", null).setIcon(R.drawable.ic_delete).show();
            isError = true;
        }
        return !isError;
    }

    public void completeStep(){
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
}
