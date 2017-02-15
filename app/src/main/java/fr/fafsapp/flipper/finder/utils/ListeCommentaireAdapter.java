package fr.fafsapp.flipper.finder.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import fr.fafsapp.flipper.finder.PageCarteFlipper;
import fr.fafsapp.flipper.finder.PageInfoFlipperPager;
import fr.fafsapp.flipper.finder.R;
import fr.fafsapp.flipper.finder.metier.Commentaire;

/**
 * @author Fafouche
 */
public class ListeCommentaireAdapter extends ArrayAdapter<Commentaire> {

	public ListeCommentaireAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	private List<Commentaire> listeCommentaire;

	public ListeCommentaireAdapter(Context context, int resource, List<Commentaire> items) {

		super(context, resource, items);

		this.listeCommentaire = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;

		if (v == null) {
			LayoutInflater vi;
			vi = LayoutInflater.from(getContext());
			v = vi.inflate(R.layout.simple_list_item_commentaire, null);
		}

		// On set les tags pour pouvoir retrouver sur quelle ligne on a cliqu�.
		v.setTag(position);
		v.setOnClickListener(CommentaireClickListener);

		Commentaire p = listeCommentaire.get(position);

		if (p != null) {

			TextView pseudoTV = (TextView) v.findViewById(R.id.textePseudo);
			TextView dateTV = (TextView) v.findViewById(R.id.textDate);
			TextView commentaireTV = (TextView) v.findViewById(R.id.texteCommentaire);

			if (pseudoTV != null && p.getFlipper() != null){
				if (p.getPseudo().length()>0){
					Spanned html = Html.fromHtml(getContext().getResources().getString(R.string.fulltextCommentaire, p.getPseudo(), p.getFlipper().getModele().getNom(), p.getFlipper().getEnseigne().getVille()));
					CharSequence trimmed = trim(html, 0, html.length());
					pseudoTV.setText(trimmed);
				}else{
					Spanned html = Html.fromHtml(getContext().getResources().getString(R.string.fulltextCommentaireAnonyme, p.getFlipper().getModele().getNom(), p.getFlipper().getEnseigne().getVille()));
					CharSequence trimmed = trim(html, 0, html.length());
					pseudoTV.setText(trimmed);
				}
			}else if (pseudoTV != null && p.getPseudo() != null && p.getPseudo().length()>0) {
				pseudoTV.setText(p.getPseudo());
				pseudoTV.setTypeface(null, Typeface.BOLD);
			}
			if (dateTV != null) {
				dateTV.setText(p.getDate());
			}
			if (commentaireTV != null) {
				Spanned html = Html.fromHtml(p.getTexte());
				CharSequence trimmed = trim(html, 0, html.length());
				commentaireTV.setText(trimmed);
			}
		}
		return v;
	}

	private OnClickListener CommentaireClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//EasyTracker.getTracker().sendEvent("ui_action", "button_press", "item_info_flipper", 0L);
			Commentaire commentaire = listeCommentaire.get((Integer) v.getTag());
			if (commentaire.getFlipper() != null){
				Intent infoActivite = new Intent(getContext(), PageInfoFlipperPager.class);
				infoActivite.putExtra(PageCarteFlipper.INTENT_FLIPPER_POUR_INFO, commentaire.getFlipper());
				// On va sur l'onglet de la carte
				infoActivite.putExtra(PageInfoFlipperPager.INTENT_FLIPPER_ONGLET_DEFAUT, 0);
				getContext().startActivity(infoActivite);
			}
		}
	};

	public static CharSequence trim(CharSequence s, int start, int end) {
		while (start < end && Character.isWhitespace(s.charAt(start))) {
			start++;
		}

		while (end > start && Character.isWhitespace(s.charAt(end - 1))) {
			end--;
		}

		return s.subSequence(start, end);
	}

}
