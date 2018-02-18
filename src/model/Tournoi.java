package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Tournoi {
	
	// Attributs-modèle
	private String  nomTournoi;
	private int     statut;
	private int     idTournoi;
	private boolean manuel;

	// États du tournoi
	public final static int STATUS_INSCRIPTION_JOUEURS    = 0;
	public final static int STATUS_INSCRIPTION_TERMINEE   = 1;
	public final static int STATUS_SAISIE_MATCHS          = 2;
	public final static int STATUS_TERMINE                = 3;

	private TournoiListeEquipes      eqList               = null;
	private TournoiListeMatchs       matchsList           = null;
	private TournoiGestionnaireTours gestionnaireTours    = null;
	private Statement                st                   = null;
	
    /* *****************************************************
     * Accès aux propriétés ********************************
     ******************************************************/
	
		public int getId(){
			return this.idTournoi;
		}
		public int getStatut(){
			return statut;
		}
		private int getCurrTourNumber(){
			int nbg = getNbTours();
			return (nbg == 0) ? 1: nbg;
		}
		public String getNStatut(){
			switch(this.statut){
				case STATUS_INSCRIPTION_JOUEURS:
					return "Inscription des joueurs";
				case STATUS_INSCRIPTION_TERMINEE:
					return "Génération des matchs";
				case STATUS_SAISIE_MATCHS:
					return "Matchs en cours";
				case STATUS_TERMINE:
					return "Terminé";
				default:
					return "Inconnu";
			}
		}
		public String getNom() {
			return nomTournoi;
		}
		public boolean isManuel(){
			return manuel;
		}

    /* *****************************************************
     * Cycle de vie de l'objet *****************************
     ******************************************************/

		private Tournoi(Statement s){
			this.st = s;
			this.eqList = new TournoiListeEquipes(this, s);
			this.matchsList = new TournoiListeMatchs(this, s);
		}
		
		public static Tournoi fromName(String name, Statement statement){
			Tournoi t = new Tournoi(statement);
			try {
				ResultSet rs = statement.executeQuery("SELECT * FROM tournois WHERE nom_tournoi = '" + Database.mysql_real_escape_string(name) + "';");
				if(!rs.next()){
					return null;
				}
				t.idTournoi  = rs.getInt("id_tournoi");
				t.statut     = rs.getInt("statut");
				String m        = rs.getString("manuel");
				t.manuel     = (m != null && m.contains("oui"));
				t.nomTournoi = rs.getString("nom_tournoi");
				System.out.println("ID tournoi:" + t.idTournoi);
				rs.close();
	
			} catch (SQLException e) {
				System.out.println("Erreur SQL: " + e.getMessage());
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return t;
		}

		public static int deleteByName(Statement s2, String nomtournoi){
			try {
				int idt;
				ResultSet rs = s2.executeQuery("SELECT id_tournoi FROM tournois WHERE nom_tournoi = '" + Database.mysql_real_escape_string(nomtournoi) + "';");
				rs.next();
				idt = rs.getInt(1);
				rs.close();
				System.out.println("ID du tournoi à supprimer:" + idt);
				s2.executeUpdate("DELETE FROM matchs   WHERE id_tournoi = " + idt);
				s2.executeUpdate("DELETE FROM equipes  WHERE id_tournoi = " + idt);
				s2.executeUpdate("DELETE FROM tournois WHERE id_tournoi = " + idt);
			} catch (SQLException e) {
				System.out.println("Erreur suppression" + e.getMessage());
				
			} catch (Exception e) {
				System.out.println("Erreur inconnue");
			} 
			return 0;
		}
		public static Tournoi createNew(Statement st, String name){
			try {
				st.executeUpdate("INSERT INTO tournois (id_tournoi, nb_matchs, nom_tournoi, statut) VALUES (NULL, 0, '"+ name +"', 0)");
				return Tournoi.fromName(name, st);
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
		}
		
    /* *****************************************************
     * Gestion des équipes* ********************************
     ******************************************************/

		public void majEquipes(){
			this.eqList.updateFromDB();
		}
		
		public void equipeNew(){
			int numeroNouvelleEquipe= eqList.lookupForNewEqNumber();
			Equipe.createNew(this, numeroNouvelleEquipe, st);
			eqList.updateFromDB();

		}
		public void equipeUpdate(int index){
			Equipe e = getEquipe(index);
			e.commitToDB(st);
			majEquipes();
		}
		public Equipe getEquipe(int index){
			return eqList.get(index);
			
		}
		public int getNbEquipes(){
			return eqList.size();
		}
		
    /* *****************************************************
     * Gestion des matchs **********************************
     ******************************************************/

		public void majMatch(){
			matchsList.updateFromDB();
		}
		public void addMatch(){
			TournoiMatch.createNew(st, this, 1, 2, getCurrTourNumber());
		}
		public void delMatch(int num){
			 matchsList.get(num).delete(st);
	
		}
		public TournoiMatch getMatch(int index){
			return matchsList.get(index);
		}
		public int getNbMatchs(){
			return matchsList.size();
		}

		public int getNbTours(){
			return TournoiMatch.getMaxRegisteredTour(st, this);
		}
		public TournoiGestionnaireTours getGestionnaireTours(){
			if (gestionnaireTours == null){
				gestionnaireTours = new TournoiGestionnaireTours(st, this);
			}
			return gestionnaireTours;
		}
	
    /* *****************************************************
     * Modification des propriétés *************************
     ******************************************************/
	
		private void commitToDB(){
			String db_manuel = this.manuel ? "oui":"non";
			try {
				st.executeUpdate(
						  "UPDATE tournois SET "
						+ "	manuel='" + db_manuel + "',"
						+ " statut=" + this.statut
						+ "WHERE id_tournoi=" + this.idTournoi);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		public void setManuel(){
			if(!manuel){
				this.manuel=true;
				commitToDB();
			}
		}
		public void setStatut(int status){
			this.statut = status;
			commitToDB();
		}






	public void supprimerEquipe(int ideq){
		try {
			
			Equipe equipe = eqList.getById(ideq);
			equipe.delete(st);
			
			if( !isManuel() && statut <= STATUS_INSCRIPTION_TERMINEE){
				st.executeUpdate("UPDATE equipes SET num_equipe = num_equipe - 1 WHERE id_tournoi = " + idTournoi + " AND num_equipe > " + equipe.num);
			}
		    majEquipes();
		    
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
    
	/*
  

*/


}
