package com.pinmyballs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.pinmyballs.fragment.FragmentActionsFlipper;
import com.pinmyballs.metier.Enseigne;
import com.pinmyballs.metier.Flipper;
import com.pinmyballs.service.FlipperService;
import com.pinmyballs.service.GlobalService;
import com.pinmyballs.utils.NetworkUtil;

public class PageAdmin extends AppCompatActivity {

    TextView searchBynumberInput;
    //Button searchBynumberButton;
    ImageButton clearNumberButton;
    ImageButton searchBynumberImageButton;
    Button saveButton;
    SwitchCompat actifToggle;
    TextView actifState;
    TextView R_flipID;
    TextView R_flipEnseigneId;
    TextView R_flipEnseigne;
    TextView R_flipEnseigneCP;
    TextView R_flipEnseigneVille;
    TextView R_flipModeleId;
    TextView R_flipModele;
    ActionBar mActionbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin);

        //Autocomplete
        //PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
        //getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


        // Affichage du header
        mActionbar = getSupportActionBar();

        mActionbar.setTitle(R.string.headerAdmin);
        mActionbar.setHomeButtonEnabled(true);
        mActionbar.setDisplayHomeAsUpEnabled(true);

        searchBynumberInput = (TextView) findViewById(R.id.searchByNumberEditText);

        searchBynumberImageButton =(ImageButton) findViewById(R.id.searchbynumberImagebutton);
        searchBynumberImageButton.setOnClickListener(SearchByNumberListener);
        clearNumberButton =(ImageButton) findViewById(R.id.boutonClearNumber);
        clearNumberButton.setOnClickListener(ClearNumberButtonListener);

        R_flipID = (TextView) findViewById(R.id.searchbynumberResultFlipID);
        R_flipEnseigneId = (TextView) findViewById(R.id.searchbynumberResultFlipEnseigneID);
        R_flipEnseigne = (TextView) findViewById(R.id.searchbynumberResultFlipEnseigne);
        R_flipEnseigneCP = (TextView) findViewById(R.id.searchbynumberResultFlipEnseigneCP);
        R_flipEnseigneVille = (TextView) findViewById(R.id.searchbynumberResultFlipEnseigneVille);
        R_flipModeleId = (TextView) findViewById(R.id.searchbynumberResultFlipModeleID);
        R_flipModele = (TextView) findViewById(R.id.searchbynumberResultFlipModele);

        actifToggle = (SwitchCompat) findViewById(R.id.actifToggle);
        actifState = (TextView) findViewById(R.id.actifState);

        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(SaveButtonListener);

        actifToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //On passe le flip Inactif
                    actifState.setText("Now Actif");
                } else {
                    actifState.setText("Now Inactif");
                }
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
                Intent intent4 = new Intent(PageAdmin.this, PagePreferences.class);
                startActivity(intent4);
                break;
            default:
                Log.i("Erreur action bar","default");
                break;
        }
        return false;
    }

    private View.OnClickListener SearchByNumberListener = new View.OnClickListener() {
        public void onClick(View v) {

            String flipflop = searchBynumberInput.getText().toString();
            Flipper flip;
            Enseigne flipEnseigne;
            String flipModele;
            GlobalService globalService = new GlobalService();
            flip = globalService.getFlip(getApplicationContext(), Long.parseLong(flipflop));
            flipEnseigne = flip.getEnseigne();
            flipModele = flip.getModele().getNom();

            R_flipID.setText(Long.toString(flip.getId()));
            R_flipEnseigneId.setText(Long.toString(flip.getIdEnseigne()));
            R_flipEnseigne.setText(flipEnseigne.getNom());
            R_flipEnseigneCP.setText(flipEnseigne.getCodePostal());
            R_flipEnseigneVille.setText(flipEnseigne.getVille());
            R_flipModeleId.setText(Long.toString(flip.getIdModele()));
            R_flipModele.setText(flipModele);
            actifToggle.setChecked(flip.isActif());
            if (flip.isActif()) {
                actifState.setText("Actif");
            } else {
                actifState.setText("Inactif");
            }
        }
    };


    private View.OnClickListener SaveButtonListener = new View.OnClickListener() {
        public void onClick(View v) {

            String flipflop = searchBynumberInput.getText().toString();
            Boolean flipactifinDB;
            Flipper flip;
            GlobalService globalService = new GlobalService();
            flip = globalService.getFlip(getApplicationContext(), Long.parseLong(flipflop));
            flipactifinDB = flip.isActif();

            //On vérifie qu'on a la connection
            if (NetworkUtil.isConnected(getApplicationContext())) {
                FlipperService flipperService = new FlipperService(new FragmentActionsFlipper.FragmentActionCallback() {
                    @Override
                    public void onTaskDone() {
                        //finish();  uncomment pour fermer la fenetre
                    }
                });
                //On vérifie que l'état du flip a été changé
                if (!actifToggle.isChecked() == flipactifinDB) {
                    //On modifie l'état du flip dans la base et online
                    flipperService.modifieEtatFlip(getApplicationContext(), flip);

                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.toastPasdeChangement), Toast.LENGTH_SHORT);
                    toast.show();
                }

            } else {
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.toastChangeModelePasPossibleReseau), Toast.LENGTH_SHORT);
                toast.show();
            }

        }
    };

    private View.OnClickListener ClearNumberButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            searchBynumberInput.setText("");
        }
    };

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

}
