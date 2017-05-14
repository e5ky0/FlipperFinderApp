package com.pinmyballs.metier;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Tournoi  implements Serializable{

	private static final long serialVersionUID = 4803600387182980034L;
	@SerializedName("TOUR_ID")
	private long id;

	@SerializedName("TOUR_NOM")
	private String nom;

	@SerializedName("TOUR_DATE")
	private String date;

	@SerializedName("TOUR_COMMENTAIRE")
	private String commentaire;

	@SerializedName("TOUR_LATITUDE")
	private String latitude;

	@SerializedName("TOUR_LONGITUDE")
	private String longitude;

	@SerializedName("TOUR_ADRESSE")
	private String adresse;

	@SerializedName("TOUR_CODE_POSTAL")
	private String codePostal;

	@SerializedName("TOUR_VILLE")
	private String ville;

	@SerializedName("TOUR_PAYS")
	private String pays;

	@SerializedName("TOUR_URL")
	private String url;

	public Tournoi(){
	}

	public Tournoi(long id, String nom, String commentaire, String date,
			String latitude, String longitude, String adresse,
			String codePostal, String ville, String pays, String url) {
		this.id = id;
		this.nom = nom;
		this.date = date;
		this.commentaire = commentaire;
		this.latitude = latitude;
		this.longitude = longitude;
		this.adresse = adresse;
		this.codePostal = codePostal;
		this.ville = ville;
		this.pays = pays;
		this.url=url;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getCommentaire() {
		return commentaire;
	}
	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getAdresse() {
		return adresse;
	}
	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}
	public String getCodePostal() {
		return codePostal;
	}
	public void setCodePostal(String codePostal) {
		this.codePostal = codePostal;
	}
	public String getVille() {
		return ville;
	}
	public void setVille(String ville) {
		this.ville = ville;
	}
	public String getPays() {
		return pays;
	}
	public void setPays(String pays) {
		this.pays = pays;
	}

	public String getAdresseComplete(){
		return adresse + " " + codePostal + " " + ville;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


}
