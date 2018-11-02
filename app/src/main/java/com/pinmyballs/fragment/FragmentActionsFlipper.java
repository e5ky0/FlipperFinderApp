package com.pinmyballs.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.pinmyballs.PageCarteFlipper;
import com.pinmyballs.PagePreferences;
import com.pinmyballs.R;
import com.pinmyballs.metier.Flipper;
import com.pinmyballs.metier.ModeleFlipper;
import com.pinmyballs.service.FlipperService;
import com.pinmyballs.service.base.BaseModeleService;
import com.pinmyballs.utils.LocationUtil;
import com.pinmyballs.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class FragmentActionsFlipper extends Fragment {

    Boolean ajoutNouveauFlip = false;

	Button boutonChangement;
	Button boutonDisparition;
	Button boutonValidation;
	Button boutonNavigation;
	Button boutonNouveauFlip;

	Button boutonValideChangement;
	Button boutonAnnuleChangement;
	EditText pseudo = null;
	EditText commentaire = null;
	AutoCompleteTextView champNouveauModeleFlipper = null;

	String pseudoText = "";
	SharedPreferences settings;

	Flipper flipper;

	ScrollView changeModeleLayout = null;

	BaseModeleService modeleFlipperService = null;

	HashMap hashMapModeles;
	ArrayList<String> listeModelesComplet;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_action_flipper, container, false);
		super.onCreate(savedInstanceState);

		settings = getActivity().getSharedPreferences(PagePreferences.PREFERENCES_FILENAME, 0);
		pseudoText = settings.getString(PagePreferences.KEY_PSEUDO_FULL, "");

		Intent i = getActivity().getIntent();
		flipper = (Flipper) i.getSerializableExtra(PageCarteFlipper.INTENT_FLIPPER_POUR_INFO);

		boutonValidation = (Button) rootView.findViewById(R.id.boutonValidation);
		boutonChangement = (Button) rootView.findViewById(R.id.boutonChangement);
		boutonDisparition = (Button) rootView.findViewById(R.id.boutonDisparition);
		boutonNavigation = (Button) rootView.findViewById(R.id.boutonNavigation);
		boutonNouveauFlip = (Button) rootView.findViewById(R.id.boutonNouveauFlip);

		boutonValideChangement = (Button) rootView.findViewById(R.id.boutonValideChangementModele);
		boutonAnnuleChangement = (Button) rootView.findViewById(R.id.boutonCancelChangeModele);
		champNouveauModeleFlipper = (AutoCompleteTextView)rootView.findViewById(R.id.autocompletionNouveauModeleFlipper);
		pseudo = (EditText) rootView.findViewById(R.id.champPseudo);
		commentaire = (EditText) rootView.findViewById(R.id.texteCommentaire);
		changeModeleLayout = (ScrollView) rootView.findViewById(R.id.layoutChangeModele);

		boutonChangement.setOnClickListener(ChangerModeleListener);
		boutonDisparition.setOnClickListener(DisparitionListener);
		boutonValidation.setOnClickListener(ValidationListener);
		boutonNavigation.setOnClickListener(NavigationListener);
		boutonNouveauFlip.setOnClickListener(NouveauFlipListener);

		boutonAnnuleChangement.setOnClickListener(AnnuleChangementModeleListener);
		boutonValideChangement.setOnClickListener(ValideChangementListener);

		//iniatilisation des listes
		listeModelesComplet = new ArrayList<String>();
		hashMapModeles = new HashMap();

		// Initialisation du champ Modele
		modeleFlipperService = new BaseModeleService();
		ArrayList<ModeleFlipper> listModeleFlipper = new BaseModeleService().getAllModeleFlipper(getActivity());

		for (ModeleFlipper modele : listModeleFlipper) {
			String NomComplet = modele.getNomComplet();
			listeModelesComplet.add(NomComplet);
			hashMapModeles.put(NomComplet, modele.getId());
		}

		// Prépare la liste d'autocomplétion pour les modèle de flipper
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listeModelesComplet);
		/*modeleFlipperService = new BaseModeleService();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, modeleFlipperService.getAllNomModeleFlipper(getActivity().getApplicationContext()));
*/
		champNouveauModeleFlipper.setAdapter(adapter);
		champNouveauModeleFlipper.setImeOptions(EditorInfo.IME_ACTION_DONE);

		champNouveauModeleFlipper.setOnItemClickListener(itemSelectionneNouveauModeleListener);

		//Récupère le pseudo et préremplit le champ si besoin
		settings = getActivity().getSharedPreferences(PagePreferences.PREFERENCES_FILENAME, 0);
		pseudoText = settings.getString(PagePreferences.KEY_PSEUDO_FULL, "");
		pseudo.setText(pseudoText);

		// On cache le layout qui va servir à renseigner un nouveau modèle
		changeModeleLayout.setVisibility(View.GONE);

		return rootView;

	}

	private OnItemClickListener itemSelectionneNouveauModeleListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(champNouveauModeleFlipper.getWindowToken(), 0);
		}
	};

	private OnClickListener ValideChangementListener = new OnClickListener() {
		public void onClick(View v) {
			Context context = getActivity().getApplicationContext();

			if (champNouveauModeleFlipper.getText().length() != 0){
				//ModeleFlipper modeleChoisi = modeleFlipperService.getModeleFlipperByName(getActivity().getApplicationContext(),champNouveauModeleFlipper.getText().toString());
				ModeleFlipper modeleChoisi = modeleFlipperService.getModeleById(context,Long.parseLong(String.valueOf(hashMapModeles.get(champNouveauModeleFlipper.getText().toString()))));
				if (modeleChoisi != null){
					if (modeleChoisi.getId() != flipper.getModele().getId()){
						if (NetworkUtil.isConnected(getActivity().getApplicationContext())){
							FlipperService flipperService = new FlipperService(new FragmentActionCallback() {
								@Override
								public void onTaskDone() {
									getActivity().finish();
								}
							});
							String commentaireString = ajoutNouveauFlip ? "Nouveau" : "Changement";
							String pseudoCommentaire = getResources().getString(R.string.pseudoCommentaireAnonyme);
							if (commentaire.getText().length() > 0){
								// On sauvegarde le pseudo
								Editor editor = settings.edit();
								editor.putString(PagePreferences.KEY_PSEUDO_FULL, pseudo.getText().toString());
								editor.apply();
								pseudoCommentaire = getResources().getString(R.string.pseudoCommentaireAnonyme);
								if (pseudo.getText().length() > 0){
									pseudoCommentaire = pseudo.getText().toString();
								}
								commentaireString = Html.toHtml(commentaire.getText());
							}
							//((AppCompatActivity)getActivity()).setSupportProgressBarIndeterminateVisibility(true);
                            if (ajoutNouveauFlip){
                                flipperService.rajouterFlipperSurFlipper(getActivity(), flipper, modeleChoisi.getId(), commentaireString, pseudoCommentaire);
                                boutonAnnuleChangement.performClick();
                                ajoutNouveauFlip = false;
                            }else {
                                flipperService.remplaceFlipper(getActivity(), flipper, modeleChoisi.getId(), commentaireString, pseudoCommentaire);
                            }
						}else{
							Toast toast = Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.toastChangeModelePasPossibleReseau), Toast.LENGTH_SHORT);
							toast.show();
						}
					}else{
						new AlertDialog.Builder(getActivity()).setTitle("Envoi impossible!").setMessage("Le modèle est identique, pas la peine de le changer !").setNeutralButton("Fermer", null).setIcon(R.drawable.ic_delete).show();
					}
				}else{
					new AlertDialog.Builder(getActivity()).setTitle("Envoi par mail").setMessage("Le modèle que vous avez renseigné est inconnu. Votre notification sera traitée manuellement par mail.").setNeutralButton("OK", ChangerModeleParMailListener).show();
				}
			}else{
				new AlertDialog.Builder(getActivity()).setTitle("Envoi impossible!").setMessage("Vous n'avez pas rempli de nouveau modèle !").setNeutralButton("Fermer", null).setIcon(R.drawable.ic_delete).show();
			}
		}
	};

	private DialogInterface.OnClickListener ChangerModeleParMailListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			String message = "ID : " + flipper.getId() + "\nEnseigne : " + flipper.getEnseigne().getId()
				+ "\nAncien Modèle :" + flipper.getModele().getNom() + "\nNouveau Modèle : " + champNouveauModeleFlipper.getText().toString();
			envoiMail("Changement du flipper " + flipper.getId(), message);
		}
	};

	public interface FragmentActionCallback {
		void onTaskDone();
	}

	private OnClickListener ChangerModeleListener = new OnClickListener() {
		public void onClick(View v) {
			Animation slide = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_up);
			changeModeleLayout.setVisibility(View.VISIBLE);
			changeModeleLayout.startAnimation(slide);
			commentaire.setText(getString(R.string.changement_default_comment,flipper.getModele().getNom()));
		}
	};

    private OnClickListener NouveauFlipListener = new OnClickListener() {
        public void onClick(View v) {
            ajoutNouveauFlip = true;
            commentaire.setText(getString(R.string.ajout_default_comment));
            Animation slide = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_up);
            changeModeleLayout.setVisibility(View.VISIBLE);
            changeModeleLayout.startAnimation(slide);
        }
    };

	private OnClickListener AnnuleChangementModeleListener = new OnClickListener() {
		public void onClick(View v) {
			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			changeModeleLayout.setVisibility(View.GONE);
		}
	};

	private OnClickListener DisparitionListener = new OnClickListener() {
		public void onClick(View v) {
		    String message2 = "Le "+ flipper.getModele().getNom()
                    + "\nID : "+ flipper.getId()
                    + "\nAu : "+ flipper.getEnseigne().getNom()
                    + "\nsitué : " + flipper.getEnseigne().getAdresseCompleteSansPays()
                    + "\n n'existe plus."
                    + "\n"

                    + "\n----------Commentaire éventuel----------"
                    + "\n"
                    + "\n"
                    + "\n"

                    + "---------------------------------------------------------";

			String message = "ID : " + flipper.getId()
					+ "\nModèle : " + flipper.getModele().getNom()
					+ "\nDu : " + flipper.getEnseigne().getNom()
					+ "\nA : " + flipper.getEnseigne().getAdresseCompleteSansPays()
					+ "\nEnseigne : " + flipper.getEnseigne().getId()
				+ "\nCe flipper n'existe plus!";
			envoiMail("Retrait d'un flipper à " + flipper.getEnseigne().getVille(), message2);
		}
	};
	private OnClickListener ValidationListener = new OnClickListener() {
		public void onClick(View v) {
			if (NetworkUtil.isConnected(getActivity().getApplicationContext())){
				//EasyTracker.getTracker().sendEvent("ui_action", "button_press", "validation_button", 0L);
				FlipperService flipperService = new FlipperService(new FragmentActionCallback() {
					@Override
					public void onTaskDone() {
						//((AppCompatActivity)getActivity()).setSupportProgressBarIndeterminateVisibility(false);
					}
				});
				//((AppCompatActivity)getActivity()).setSupportProgressBarIndeterminateVisibility(true);
				flipperService.valideFlipper(getActivity().getApplicationContext(), flipper);
			}else{
				Toast toast = Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.toastValidationPasPossibleReseau), Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	};

	private OnClickListener NavigationListener = new OnClickListener() {
		public void onClick(View v) {
			Intent navIntentGoogleNav = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="
						+ flipper.getEnseigne().getAdresseCompleteSansPays()));

			Intent navIntentWaze = new Intent(Intent.ACTION_VIEW, Uri.parse("waze://?q="
						+ flipper.getEnseigne().getAdresseCompleteSansPays()));

			if (LocationUtil.canHandleIntent(getActivity().getApplicationContext(), navIntentGoogleNav)) {
				navIntentGoogleNav.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(navIntentGoogleNav);
			} else if (LocationUtil.canHandleIntent(getActivity().getApplicationContext(), navIntentWaze)) {
				navIntentGoogleNav.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(navIntentWaze);
			} else {
				Toast.makeText(getActivity().getApplicationContext(), "Merci d'installer Google Navigation ou Waze", Toast.LENGTH_SHORT).show();
			}

		}
	};

	private void envoiMail(String subject, String message) {
		Resources resources = getResources();
		String emailsTo = resources.getString(R.string.mailContact);
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/html");

		i.putExtra(Intent.EXTRA_EMAIL, new String[] { emailsTo });
		i.putExtra(Intent.EXTRA_SUBJECT, subject);
		i.putExtra(Intent.EXTRA_TEXT, message);
		try {
			startActivity(Intent.createChooser(i, "Envoi du mail"));
		} catch (android.content.ActivityNotFoundException ex) {
			new AlertDialog.Builder(getActivity()).setTitle("Envoi impossible!")
				.setMessage("Vous n'avez pas de mail configuré sur votre téléphone.")
				.setNeutralButton("Fermer", null).setIcon(R.drawable.ic_tristesse).show();
		}
	}


	@Override
	public void onStart() {
		super.onStart();
		//EasyTracker.getInstance().activityStart(getActivity());
	}

	@Override
	public void onStop() {
		super.onStop();
		//EasyTracker.getInstance().activityStop(getActivity());
	}

}
