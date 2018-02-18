package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TournoiMatch{
	// Attributs-modèle
	private int idmatch,eq1,eq2,score1,score2,num_tour,num_mt;
	private boolean termine;
	
    /* *****************************************************
     * Cycle de vie de l'objet *****************************
     ******************************************************/
	
		private TournoiMatch(int _idmatch,int _e1,int _e2,int _score1, int _score2, int _num_tour, boolean _termine,int _num_mt){
			idmatch = _idmatch;
			eq1     = _e1;
			eq2     = _e2;
			score1  = _score1;
			score2  = _score2;
			num_tour= _num_tour;
			termine = _termine;
			num_mt  = _num_mt;
		}
		public static TournoiMatch fromDBRow(ResultSet rs){
			try {
				return new TournoiMatch(
						rs.getInt("id_match"),
						rs.getInt("equipe1"),
						rs.getInt("equipe2"), 
						rs.getInt("score1"),
						rs.getInt("score2"),
						rs.getInt("num_tour"),
						rs.getString("termine") != null && rs.getString("termine").equals("oui"),
						rs.getInt("num_match_t"));
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
		}
		public static void createNew(Statement st, Tournoi t, int numeroEq1, int numeroEq2, int numTour){
			try {
				st.executeUpdate(
						  "INSERT INTO MATCHS (id_tournoi,equipe1,equipe2,score1,score2,num_tour) "
						+ "VALUES ("+t.getId()+", " + numeroEq1 + ", " + numeroEq2 + ", null, null," + numTour +");");
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		public void delete(Statement st){
			try {
				st.executeUpdate("DELETE FROM matchs WHERE id_match=" + idmatch + ";");
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		
    /* *****************************************************
     * Accès aux propriétés ********************************
     ******************************************************/
		
	// Statiques
		public int getIdmatch() {
			return idmatch;
		}
	
		public int getEq1() {
			return eq1;
		}
		public int getEq2() {
			return eq2;
		}
	
		public int getScore1() {
			return score1;
		}
	
		public int getScore2() {
			return score2;
		}
	
		public int getNum_tour() {
			return num_tour;
		}
	
		public int getNum_mt() {
			return num_mt;
		}
		public boolean isTermine() {
			return termine;
		}
		
	// Dynamiques
		public String toString(){
			if(eq1 < eq2){
				return "  " + eq1 + " contre " + eq2;
			}else{
				return "  " + eq2 + " contre " + eq1;
			}
		}
		
		public boolean estSaisi(){
			return score1 > 0 || score2 > 0;
		}
		public boolean equipe1Gagne(){
			return estSaisi() && score1 > score2;
		}
		public static int getMaxRegisteredTour(Statement st, Tournoi tournoi){
			try {
				ResultSet rs = st.executeQuery("SELECT MAX (num_tour) FROM matchs WHERE id_tournoi="+ tournoi.getId() +"; ");
				rs.next();
				int nbt=rs.getInt(1);
				rs.close();
				return nbt;
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				return -1;
			}
		}
		
    /* *****************************************************
     * Modification des propriétés *************************
     ******************************************************/
	
		public void setEq1(int eq1, Statement st) {
			this.eq1 = eq1;
			commitToDB(st);
		}
		public void setEq2(int eq2, Statement st) {
			this.eq2 = eq2;
			commitToDB(st);
		}
		public void setScore1(int score1, Statement st) {
			this.score1 = score1;
			commitToDB(st);
		}
		public void setScore2(int score2, Statement st) {
			this.score2 = score2;
			commitToDB(st);
		}	
		public void setNum_tour(int num_tour, Statement st) {
			this.num_tour = num_tour;
			commitToDB(st);
		}
		public void setNum_mt(int num_mt, Statement st) {
			this.num_mt = num_mt;
			commitToDB(st);
		}

    /* *****************************************************
     * Base de données *************************************
     ******************************************************/		
		 
		public void commitToDB(Statement st){
			String termine = (score1 > 0 || score2 > 0) ? "oui":"non";
			String req="UPDATE matchs SET "
					+ "timestamp="+(int)System.currentTimeMillis() + ","
					+ "num_tour="+num_tour+",num_match_t="+num_mt+", "
					+ "equipe1='" + eq1 + "', "
					+ "equipe2='" + eq2 + "', "
					+ "score1='"  + score1 + "', "
					+ "score2='"  + score2 + "',"
					+ "termine='" + termine + "' "
					+ "WHERE id_match = " + idmatch + ";";
			try {
				st.executeUpdate(req);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
 }