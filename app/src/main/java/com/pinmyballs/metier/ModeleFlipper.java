package com.pinmyballs.metier;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModeleFlipper  implements Serializable{

	private static final long serialVersionUID = -3899550250753290072L;
	@SerializedName("MOFL_ID")
	private long id;

	@SerializedName("MOFL_NOM")
	private String nom;

	@SerializedName("MOFL_MARQUE")
	private String marque;

	@SerializedName("MOFL_ANNEE_LANCEMENT")
	private long anneeLancement;

	public ModeleFlipper(long id, String nom, String marque, long anneeLancement){
		this.id = id;
		this.nom = nom;
		this.marque = marque;
		this.anneeLancement = anneeLancement;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getMarque() {
		return marque;
	}
	public void setMarque(String marque) {
		this.marque = marque;
	}
	public long getAnneeLancement() {
		return anneeLancement;
	}
	public void setAnneeLancement(long anneeLancement) {
		this.anneeLancement = anneeLancement;
	}

}
