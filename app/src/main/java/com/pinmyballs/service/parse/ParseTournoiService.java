package com.pinmyballs.service.parse;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import com.pinmyballs.database.FlipperDatabaseHandler;
import com.pinmyballs.metier.Tournoi;

public class ParseTournoiService {

	/**
	 * Retourne tous les tournois à partir du cloud
	 */
	public List<Tournoi> getAllTournoi() {
		List<Tournoi> listeTournoi = new ArrayList<Tournoi>();
		List<ParseObject> listePo = new ArrayList<ParseObject>();
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				FlipperDatabaseHandler.TOURNOI_TABLE_NAME);
		try {
			listePo = query.find();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		for (ParseObject po : listePo) {
			Tournoi tournoi = new Tournoi(
					po.getLong(FlipperDatabaseHandler.TOUR_ID),
					po.getString(FlipperDatabaseHandler.TOUR_NOM),
					po.getString(FlipperDatabaseHandler.TOUR_COMMENTAIRE),
					po.getString(FlipperDatabaseHandler.TOUR_DATE),
					po.getNumber(FlipperDatabaseHandler.TOUR_LATITUDE).toString(),
					po.getNumber(FlipperDatabaseHandler.TOUR_LONGITUDE).toString(),
					po.getString(FlipperDatabaseHandler.TOUR_ADRESSE),
					po.getString(FlipperDatabaseHandler.TOUR_CODE_POSTAL),
					po.getString(FlipperDatabaseHandler.TOUR_VILLE),
					po.getString(FlipperDatabaseHandler.TOUR_PAYS),
					po.getString(FlipperDatabaseHandler.TOUR_URL));
			listeTournoi.add(tournoi);
		}
		return listeTournoi;
	}

}
