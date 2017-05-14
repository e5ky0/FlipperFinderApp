package com.pinmyballs.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.pinmyballs.metier.Flipper;

public class LocationUtil {

	/**
	 * Méthode utilisée pour renvoyer sous forme de String l'adresse en fonction des
	 * coordonnées GPS.
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public static String getAdresseFromCoordGPS(Context context, double latitude, double longitude){
		// On devrait pouvoir utiliser GeoCoder, mais ça ne foncitonne pas avec l'émulateur, donc
		// j'utilise le service en ligne de google.
		/*String adresseCourante = "";

		  String url = "http://maps.google.com/maps/geo?q="+latitude+","+longitude+"&output=csv&sensor=false";
		  HttpUriRequest request = new HttpGet(url);
		  HttpClient mClient= new DefaultHttpClient();
		  HttpResponse res;
		  try {
		  res = mClient.execute(request);
		  adresseCourante = EntityUtils.toString(res.getEntity());
		  } catch (Exception e) {
		  e.printStackTrace();
		  }
		  String[] addressInfo = adresseCourante.split(",");
		  adresseCourante ="";
		  for (int i = 2; i < addressInfo.length; i++) {
		  adresseCourante = adresseCourante + addressInfo[i];
		  }
		// On enlève les useless guillemets
		if (adresseCourante.length() > 1){
		adresseCourante = adresseCourante.substring(1, adresseCourante.length() - 1);
		}

		return adresseCourante;*/

		String adresseCourante = "";
		Geocoder geocoder = new Geocoder(context, Locale.getDefault());
		List<Address> addresses = null;
		try {
			addresses = geocoder.getFromLocation(latitude, longitude, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (addresses != null && addresses.size() > 0) {
			Address address = addresses.get(0);
			// Format the first line of address (if available) and city.
			adresseCourante = String.format("%s %s",
					address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
					address.getLocality(),
					address.getCountryName());

		}

		return adresseCourante;

	}

	/**
	 * Calculates the end-point from a given source at a given range (meters)
	 * and bearing (degrees). This methods uses simple geometry equations to
	 * calculate the end-point.
	 *
	 * @param point
	 *            Point of origin
	 * @param range
	 *            Range in meters
	 * @param bearing
	 *            Bearing in degrees
	 * @return End-point from the source given the desired range and bearing.
	 */
	public static PointF calculateDerivedPosition(PointF point,
			double range, double bearing)
	{
		double EarthRadius = 6371000; // m

		double latA = Math.toRadians(point.x);
		double lonA = Math.toRadians(point.y);
		double angularDistance = range / EarthRadius;
		double trueCourse = Math.toRadians(bearing);

		double lat = Math.asin(
				Math.sin(latA) * Math.cos(angularDistance) +
				Math.cos(latA) * Math.sin(angularDistance)
				* Math.cos(trueCourse));

		double dlon = Math.atan2(
				Math.sin(trueCourse) * Math.sin(angularDistance)
				* Math.cos(latA),
				Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat));

		double lon = ((lonA + dlon + Math.PI) % (Math.PI * 2)) - Math.PI;

		lat = Math.toDegrees(lat);
		lon = Math.toDegrees(lon);

		PointF newPoint = new PointF((float) lat, (float) lon);

		return newPoint;
	}


	/**
	 *  Retourne la dernière position connue du téléphone
	 *  Attention, peut retourner null
	 * @param context
	 * @return
	 */
	public static Location getLastKnownLocation(Context context) {
		LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		List<String> providers = lm.getProviders(true);

		/* Loop over the array backwards, and if you get an accurate location, then break out the loop*/
		Location l = null;

		for (int i=providers.size()-1; i>=0; i--) {
			l = lm.getLastKnownLocation(providers.get(i));
			if (l != null) break;
		}

		return l;
	}

	/**
	 * Permet de formater les distances pour un affichage propre
	 * @param meters
	 * @return
	 */
	public static String formatDist(float meters) {
		if (meters < 1000) {
			return ((int) meters) + " m";
		} else if (meters < 10000) {
			return formatDec(meters / 1000f, 1) + " km";
		} else {
			return ((int) (meters / 1000f)) + " km";
		}
	}

	private static String formatDec(float val, int dec) {
		int factor = (int) Math.pow(10, dec);

		int front = (int) (val);
		int back = (int) Math.abs(val * (factor)) % factor;

		return front + "." + back;
	}

	/**
	 * Retourne l'objet Addresse correspondant à l'adresse passée en paramètre
	 * sous forme de String
	 * Renvoie null si pas d'adresse trouvée
	 * @param context
	 * @param adresse
	 * @return
	 */
	public static LatLng getAddressFromText(Context context, String adresse, double latitude, double longitude){
		List<Address> listeAdresseRetour = null;
		Geocoder geocoder = new Geocoder(context, Locale.getDefault());
		try {
			listeAdresseRetour = geocoder.getFromLocationName(adresse, 5);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		if (listeAdresseRetour != null && !listeAdresseRetour.isEmpty()){
			float[] resultDistance=new float[5];
			// On va ressortir l'adresse la plus proche parmi toute celle retournées
			Address adresseARetourner = listeAdresseRetour.get(0);
			Location.distanceBetween(latitude, longitude, adresseARetourner.getLatitude(), adresseARetourner.getLongitude(), resultDistance);
			Float distanceRetour =  resultDistance[0];
			for (Address adresseEnCours : listeAdresseRetour){
				Location.distanceBetween(latitude, longitude, adresseEnCours.getLatitude(), adresseEnCours.getLongitude(), resultDistance);
				Float distanceFloat = resultDistance[0];
				if (distanceFloat < distanceRetour){
					adresseARetourner = adresseEnCours;
				}
			}
			LatLng locationToReturn = new LatLng(adresseARetourner.getLatitude(), adresseARetourner.getLongitude());
			return locationToReturn;
		}

		return null;
	}

	public static boolean canHandleIntent(Context context, Intent intent){
		PackageManager packageManager = context.getPackageManager();
		List activities = packageManager.queryIntentActivities(
				intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return activities.size() > 0;
	}

	/**
	 * Retourne le nombre de jour depuis la dernière maj du flipper.
	 * Retourne -1 si la date de màj est nulle
	 * @param flipper
	 * @return
	 */
	public static int getDaysSinceMajFlip(Flipper flipper){
		if (flipper.getDateMaj() != null && flipper.getDateMaj().length() != 0) {
			int nbJours = 0;
			try {
				Date dateMajFlip = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).parse(flipper.getDateMaj());
				nbJours = Days.daysBetween(new DateTime(dateMajFlip), new DateTime(new Date())).getDays();
			} catch (ParseException e) {
				// Date mal formattée, on fait comme si la date est nulle
				return -1;
			}
			// La date n'est pas nulle et bien formattée.
			return nbJours;
		}else{
			// La date est nulle, on retourne -1
			return -1;
		}
	}
}
