package roue.src;

import java.util.ArrayList;
import java.util.List;

import roue.src.Action;

class Mesure {
	private String nom;
	private float distancetotale ;
	List<Action> listAction = new ArrayList<Action>() ;
public Mesure(String n){
	nom = n;
	distancetotale = 0 ;
	
}
/**
 * @return the nom
 */
public String getNom() {
	return nom;
}
/**
 * @param nom the nom to set
 */
public void setNom(String nom) {
	this.nom = nom;
}
/**
 * @return the distancetotale
 */
public float getDistancetotale() {
	return distancetotale;
}
/**
 * @param distancetotale the distancetotale to set
 */
public void setDistancetotale(float distancetotale) {
	this.distancetotale = distancetotale;
}
/**
 * @return the action
 */
public List<Action> getListAction() {
	return listAction;
}
/**
 * @param action the action to set
 */
public void setListAction(List<Action> action) {
	this.listAction = action;
}
}
