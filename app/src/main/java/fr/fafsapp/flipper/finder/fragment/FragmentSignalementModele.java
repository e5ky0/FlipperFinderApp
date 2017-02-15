package fr.fafsapp.flipper.finder.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.fafsapp.flipper.finder.PagePreferences;
import fr.fafsapp.flipper.finder.R;
import fr.fafsapp.flipper.finder.metier.Commentaire;
import fr.fafsapp.flipper.finder.metier.Flipper;
import fr.fafsapp.flipper.finder.metier.ModeleFlipper;
import fr.fafsapp.flipper.finder.service.base.BaseModeleService;

public class FragmentSignalementModele extends SignalementWizardFragment {

    @InjectView(R.id.champPseudo) EditText champPseudo;
    @InjectView(R.id.texteCommentaire) EditText champCommentaire;
    @InjectView(R.id.autocompletionModeleFlipper) AutoCompleteTextView champModeleFlipper;
    @InjectView(R.id.autocompletionModeleFlipper2) AutoCompleteTextView champModeleDeuxiemeFlipper;
    @InjectView(R.id.autocompletionModeleFlipper3) AutoCompleteTextView champModeleTroisiemeFlipper;

    SharedPreferences settings;
    BaseModeleService modeleFlipperService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wizard_modele, container, false);

        super.onCreate(savedInstanceState);
        ButterKnife.inject(this, rootView);
        modeleFlipperService = new BaseModeleService();

        // Initialisation du champ Pseudo
        settings = getActivity().getSharedPreferences(PagePreferences.PREFERENCES_FILENAME, 0);
        String pseudoText = settings.getString(PagePreferences.KEY_PSEUDO_FULL, "");
        champPseudo.setText(pseudoText);

        // Initialisation du champ Modele
        modeleFlipperService = new BaseModeleService();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, modeleFlipperService.getAllNomModeleFlipper(getActivity().getApplicationContext()));
        initChampModele(champModeleFlipper, adapter);
        initChampModele(champModeleDeuxiemeFlipper, adapter);
        initChampModele(champModeleTroisiemeFlipper, adapter);
        return rootView;
    }

    public Commentaire getCommentaireToAdd(){
        Commentaire commentaireToAdd = null;
        if (champCommentaire.getText().length() != 0) {
            String pseudoCommentaire = getResources().getString(R.string.pseudoCommentaireAnonyme);
            Date dateDuJour = new Date();
            if (champPseudo.getText().length() > 0){
                pseudoCommentaire = champPseudo.getText().toString();
            }
            String htmlString = Html.toHtml(champCommentaire.getText());
            htmlString = htmlString.replaceAll("[\n]", "");
            commentaireToAdd = new Commentaire(getParentActivity().getNewId(),
                    getParentActivity().getNewId(),
                    htmlString,
                    new SimpleDateFormat("yyyy/MM/dd", Locale.FRANCE).format(dateDuJour),
                    pseudoCommentaire,
                    1);
        }
        return commentaireToAdd;
    }

    public ArrayList<ModeleFlipper> getModelesToAdd(){
        ArrayList<ModeleFlipper> listeRetour = new ArrayList<ModeleFlipper>();
        ModeleFlipper modeleChoisi = modeleFlipperService.getModeleFlipperByName(getActivity().getApplicationContext(),champModeleFlipper.getText().toString());
        if (modeleChoisi != null){
            listeRetour.add(modeleChoisi);
        }
        modeleChoisi = modeleFlipperService.getModeleFlipperByName(getActivity().getApplicationContext(),champModeleDeuxiemeFlipper.getText().toString());
        if (modeleChoisi != null){
            listeRetour.add(modeleChoisi);
        }
        modeleChoisi = modeleFlipperService.getModeleFlipperByName(getActivity().getApplicationContext(),champModeleTroisiemeFlipper.getText().toString());
        if (modeleChoisi != null){
            listeRetour.add(modeleChoisi);
        }
        return listeRetour;
    }

    @Override
    public boolean mandatoryFieldsComplete() {
        boolean isError = false;
        if (champModeleFlipper.getText().length() == 0) {
            new AlertDialog.Builder(getActivity()).setTitle("Envoi impossible!")
                .setMessage("Vous devez renseigner au moins un modèle du flipper.").setNeutralButton("Fermer", null)
                .setIcon(R.drawable.ic_delete).show();
            isError = true;
        }
        return !isError;
    }

    @Override
    public void completeStep() {
        ArrayList<Flipper> listeRetour = new ArrayList<Flipper>();
        int i = 0;
        for (ModeleFlipper modele : getModelesToAdd()){
            Flipper flipper = new Flipper();
            flipper.setActif(1);
            flipper.setDateMaj(getFormattedDate());
            flipper.setIdEnseigne(getNewEnseigneId());
            flipper.setId(getNewEnseigneId() + i++);
            flipper.setIdModele(modele.getId());
            listeRetour.add(flipper);
        }
        getParentActivity().completeModele(listeRetour, getCommentaireToAdd(), champPseudo.getText().toString());
    }

    private OnItemClickListener itemSelectionneListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(arg1.getWindowToken(), 0);
        }
    };
    private void initChampModele(AutoCompleteTextView champModeleView, ArrayAdapter<String> adapter){
        champModeleView.setAdapter(adapter);
        champModeleView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        champModeleView.setDropDownAnchor(R.id.autocompletionModeleFlipper);
        champModeleView.setOnItemClickListener(itemSelectionneListener);
    }
}
