package controller;

import java.sql.Statement;
import model.Database;
import model.Tournoi;
import view.Fenetre;
import view.VueAccueilGestionTournois;
import view.VueTournoiDetails;
import view.VueTournoiEquipes;
import view.VueTournoiResultats;
import view.VueTournoiSaisieMatchs;
import view.VueTournoiTours;

public class ApplicationController {
	private Tournoi   tournoi   = null;
	private Fenetre   fenetre   = null;
	private Database  database  = null;
	
    /* *****************************************************
     * Cycle de vie de l'objet *****************************
     ******************************************************/
	
		public ApplicationController(Database db){
			this.database = db;
		}

		public void startApplication(){

			this.fenetre = new Fenetre(this);
			
			this.fenetre.ajouterVue(new VueAccueilGestionTournois (this));
			this.fenetre.ajouterVue(new VueTournoiDetails         (this));
			this.fenetre.ajouterVue(new VueTournoiEquipes         (this));
			this.fenetre.ajouterVue(new VueTournoiTours           (this));
			this.fenetre.ajouterVue(new VueTournoiSaisieMatchs    (this));
			this.fenetre.ajouterVue(new VueTournoiResultats       (this));

			this.fenetre.initialiserMenu();
			this.fenetre.afficherVue(VueAccueilGestionTournois.ID);
		}
		
    /* *****************************************************
     * Gestion du modèle ***********************************
     ******************************************************/
    	
        public Tournoi getTournoi(){
        	return this.tournoi;
        }
        public void setTournoi (Tournoi t){
        	this.tournoi = t;
        	notifyDataUpdate();
        }
        
    /* *****************************************************
     * Gestion de la vue ***********************************
     ******************************************************/
        
        public Fenetre getFenetre(){
        	return this.fenetre;
        }
	    public void afficherVue(String vID){
	    	this.fenetre.afficherVue(vID);
	    }
        
    /* *****************************************************
     * Gestion des objets "techniques" *********************
     ******************************************************/

        public Statement getSQLConnector(){
        	return this.database.statement;
        }
 
	/* *****************************************************
     * Gestion des événements ******************************
     ******************************************************/
	    
	    public void notifyDataUpdate() {
	    	this.fenetre.updateMenu();
	    	this.fenetre.notifyDataUpdate();
	    }
	    
		public void setStatutSelect(String t){
			this.fenetre.setStatutSelect(t);
		}

}
