package com.pinmyballs.service;

import com.parse.ParseObject;

import com.pinmyballs.database.FlipperDatabaseHandler;
import com.pinmyballs.metier.Commentaire;
import com.pinmyballs.metier.Enseigne;
import com.pinmyballs.metier.Flipper;

public class ParseFactory {

    public ParseObject getParseObject(Flipper flipper){
        ParseObject parseObject = new ParseObject(FlipperDatabaseHandler.FLIPPER_TABLE_NAME);
        parseObject.put(FlipperDatabaseHandler.FLIPPER_ACTIF, flipper.isActif()?1:0);
        parseObject.put(FlipperDatabaseHandler.FLIPPER_DATMAJ, flipper.getDateMaj());
        parseObject.put(FlipperDatabaseHandler.FLIPPER_ENSEIGNE, flipper.getIdEnseigne());
        parseObject.put(FlipperDatabaseHandler.FLIPPER_ID, flipper.getId());
        parseObject.put(FlipperDatabaseHandler.FLIPPER_MODELE, flipper.getIdModele());
        parseObject.put(FlipperDatabaseHandler.FLIPPER_NB_CREDITS_2E, "");
        return parseObject;
    }

    public ParseObject getParseObject(Enseigne enseigne){
        ParseObject parseEnseigne = new ParseObject("ENSEIGNE2");
        parseEnseigne.put(FlipperDatabaseHandler.ENSEIGNE_ID, enseigne.getId());
        parseEnseigne.put(FlipperDatabaseHandler.ENSEIGNE_NOM, enseigne.getNom());
        //parseEnseigne.put(FlipperDatabaseHandler.ENSEIGNE_TYPE, enseigne.getType());
        //parseEnseigne.put(FlipperDatabaseHandler.ENSEIGNE_HORAIRE, enseigne.getHoraire());
        parseEnseigne.put(FlipperDatabaseHandler.ENSEIGNE_LATITUDE, Double.valueOf(enseigne.getLatitude()));
        parseEnseigne.put(FlipperDatabaseHandler.ENSEIGNE_LONGITUDE, Double.valueOf(enseigne.getLongitude()));
        parseEnseigne.put(FlipperDatabaseHandler.ENSEIGNE_ADRESSE, enseigne.getAdresse());
        parseEnseigne.put(FlipperDatabaseHandler.ENSEIGNE_CODE_POSTAL, enseigne.getCodePostal());
        parseEnseigne.put(FlipperDatabaseHandler.ENSEIGNE_VILLE, enseigne.getVille());
        parseEnseigne.put(FlipperDatabaseHandler.ENSEIGNE_PAYS, enseigne.getPays());
        parseEnseigne.put(FlipperDatabaseHandler.ENSEIGNE_DATMAJ, enseigne.getDateMaj());

        return parseEnseigne;
    }

    public ParseObject getParseObject(Commentaire commentaire){
        ParseObject parseCommentaire = new ParseObject(FlipperDatabaseHandler.COMMENTAIRE_TABLE_NAME);
        parseCommentaire.put(FlipperDatabaseHandler.COMM_ID, commentaire.getId());
        parseCommentaire.put(FlipperDatabaseHandler.COMM_FLIPPER_ID, commentaire.getFlipperId());
        parseCommentaire.put(FlipperDatabaseHandler.COMM_DATE, commentaire.getDate());
        parseCommentaire.put(FlipperDatabaseHandler.COMM_PSEUDO, commentaire.getPseudo());
        parseCommentaire.put(FlipperDatabaseHandler.COMM_TEXTE, commentaire.getTexte());
        parseCommentaire.put(FlipperDatabaseHandler.COMM_ACTIF, commentaire.getActif());
        return parseCommentaire;
    }
}
