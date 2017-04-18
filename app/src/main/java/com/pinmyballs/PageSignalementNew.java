package com.pinmyballs;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.pinmyballs.fragment.SignalementPagerAdapter;
import com.pinmyballs.fragment.SignalementWizardFragment;
import com.pinmyballs.metier.Commentaire;
import com.pinmyballs.metier.Enseigne;
import com.pinmyballs.metier.Flipper;
import com.pinmyballs.service.ParseFactory;
import com.pinmyballs.utils.MyLocation;
import com.pinmyballs.utils.MyLocation.LocationResult;

public class PageSignalementNew extends AppCompatActivity {

    LatLng currentLocation = null;

    LatLng newLocation = null;
    @InjectView(R.id.next_button) Button mNextButton;
    @InjectView(R.id.prev_button) Button mPreviousButton;
    @InjectView(R.id.signalementPager) ViewPager mPager;

    ActionBar mActionbar;
    private SignalementPagerAdapter mPagerAdapter;
    GoogleMap supportMap = null;

    ArrayList<Flipper> listeFlipper = null;
    String pseudo = null;
    Commentaire commentaire = null;
    private long newId;
    Enseigne enseigne = null;

    SharedPreferences settings;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentLocation = new LatLng(48.862731, 2.367354);

        setContentView(R.layout.activity_signalement_wizard);
        settings = getSharedPreferences(PagePreferences.PREFERENCES_FILENAME, 0);

        ButterKnife.inject(this);

        mNextButton.setOnClickListener(NextClickListener);

        TypedValue v = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, v, true);
        mPreviousButton.setTextAppearance(this, v.resourceId);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mPagerAdapter = new SignalementPagerAdapter(fragmentManager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(pageChangeListener);

        newId = new Date().getTime();

        // Affichage du header
        mActionbar = getSupportActionBar();
        mActionbar.setTitle(R.string.headerSignalement);
        updateBottomBar();
        localiseTelephone();
    }

    private void localiseTelephone(){
        LocationResult locationResult = new LocationResult(){
            @Override
            public void gotLocation(Location location){
                // Méthode appelée lorsque la localisation a fonctionné
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }
        };
    }

    public void completeModele(ArrayList<Flipper> newListeFlipper, Commentaire newCommentaire, String newPseudo){
        listeFlipper = newListeFlipper;
        commentaire = newCommentaire;
        pseudo = newPseudo;
        // On sauvegarde le pseudo
        Editor editor = settings.edit();
        editor.putString(PagePreferences.KEY_PSEUDO_FULL, pseudo);
        editor.commit();
    }

    @OnClick(R.id.prev_button)
    public void previousClickListener(View view) {
        mPager.setCurrentItem(mPager.getCurrentItem() - 1, true);
    }

    private SimpleOnPageChangeListener pageChangeListener = new SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            updateBottomBar();
        }
    };

    private OnClickListener NextClickListener = new OnClickListener() {
        public void onClick(View v) {
            SignalementWizardFragment currentFragment = mPagerAdapter.getRegisteredFragment(mPager.getCurrentItem());
            if (currentFragment.mandatoryFieldsComplete()){
                currentFragment.completeStep();
                mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
            }

        }
    };

    public void updateEnseigneLocalisation(){
        getEnseigne().setLatitude(String.valueOf(newLocation.latitude));
        getEnseigne().setLongitude(String.valueOf(newLocation.longitude));
    }

    private void updateBottomBar() {
        int position = mPager.getCurrentItem();
        if (position == SignalementPagerAdapter.PAGE_COUNT - 1) {
            //mNextButton.setOnClickListener(EnvoyerClickListener);
            mNextButton.setText(R.string.SignalementWizardFinish);
            mNextButton.setBackgroundResource(R.drawable.finish_background);
            mNextButton.setTextAppearance(this, R.style.TextAppearanceFinish);
        } else {
            mNextButton.setText(R.string.SignalementWizardNext);
            mNextButton.setBackgroundResource(R.drawable.selectable_item_background);
            TypedValue v = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, v, true);
            mNextButton.setTextAppearance(this, v.resourceId);
        }
        mPreviousButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
    }

    public void envoyer(){
        updateEnseigneLocalisation();
        Toast toast = Toast.makeText(getApplicationContext(), "Envoi en cours", Toast.LENGTH_SHORT);
        toast.show();
        ParseFactory parseFactory = new ParseFactory();
        ArrayList<ParseObject> objectsToSend = new ArrayList<ParseObject>();
        objectsToSend.add(parseFactory.getParseObject(getEnseigne()));
        if (getCommentaire() != null){
            objectsToSend.add(parseFactory.getParseObject(getCommentaire()));
        }
        for (Flipper flipperToAdd : listeFlipper){
            objectsToSend.add(parseFactory.getParseObject(flipperToAdd));
        }
        ParseObject.saveAllInBackground(objectsToSend, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast toast = Toast.makeText(getApplicationContext(), "Envoi effectué, Merci pour votre contribution :)", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    private OnClickListener EnvoyerClickListener = new OnClickListener() {
        public void onClick(View v) {
            // C'est bon, on envoie le mail
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/html");
            Resources resources = getResources();
            String emailsTo = resources.getString(R.string.mailContact);

            i.putExtra(Intent.EXTRA_EMAIL, new String[] { emailsTo });
            i.putExtra(Intent.EXTRA_SUBJECT, "Nouveau Flipper");
            /*
             * i.putExtra(Intent.EXTRA_TEXT,
             * "Adresse : "+adresseUtilisateurTV.getText
             * ()+"\nModèle : "+champModeleFlipper.getText()+
             * "\nNom de l'enseigne : " + champNomEnseigne.getText());
             */
            try {
                startActivity(Intent.createChooser(i, "Envoi du mail"));
            } catch (android.content.ActivityNotFoundException ex) {
                new AlertDialog.Builder(PageSignalementNew.this).setTitle("Envoi impossible!")
                    .setMessage("Vous n'avez pas de mail configuré sur votre téléphone.")
                    .setNeutralButton("Fermer", null).setIcon(R.drawable.ic_tristesse).show();
            }
        }
    };

    public Enseigne getEnseigne(){
        return this.enseigne;
    }

    public void setEnseigne(Enseigne newEnseigne){
        this.enseigne = newEnseigne;
    }

    public LatLng getCurrentLocation() {
        return this.currentLocation;
    }

    public void setCurrentLocation(LatLng currentLocation) {
        this.currentLocation = currentLocation;
    }

    public long getNewId() {
        return newId;
    }

    public void setNewId(long newId) {
        this.newId = newId;
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

    public LatLng getNewLocation() {
        return newLocation;
    }

    public void setNewLocation(LatLng newLocation) {
        this.newLocation = newLocation;
    }
    public Commentaire getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(Commentaire commentaire) {
        this.commentaire = commentaire;
    }
}
