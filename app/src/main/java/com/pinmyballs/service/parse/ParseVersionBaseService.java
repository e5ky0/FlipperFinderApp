package fr.fafsapp.flipper.finder.service.parse;


public class ParseVersionBaseService {


	/**
	 * Retourne la dernière version de la base à partir du cloud.
	 * @return int
	 */
	public int getDerniereVersionBase(){
		int retour = 0;
		/*
		   ParseQuery query = new ParseQuery("VersionBase");
		   try {
		   ParseObject poVersion = (ParseObject)query.orderByDescending("VB_ID").find().get(0);
		   retour = poVersion.getInt("VB_VERSION");
		   } catch (ParseException e1) {
		   e1.printStackTrace();
		   }
		   */
		return retour;
	}
}
