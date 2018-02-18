package model;

import java.sql.SQLException;
import java.sql.Statement;

public 	class Equipe{
	Equipe( Tournoi t, int _id, int _num, String _eq1){
		this.t = t;
		id = _id;
		num = _num;
		eq1 = _eq1;
	}
	public int id;
	public int num;
	private Tournoi t;
	public String eq1;
	
	public static void createNew(Tournoi t, int numeroNouvelleEquipe, Statement st){
		try {
			st.executeUpdate("INSERT INTO equipes (id_equipe,num_equipe,id_tournoi,nom_j1) VALUES (NULL,"+numeroNouvelleEquipe+", "+t.getId() + ",'');");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void commitToDB(Statement st){
		try {
			String req = "UPDATE equipes SET nom_j1 = '" + Database.mysql_real_escape_string(eq1) + "' WHERE id_equipe = " + id + ";";
			System.out.println(req);
			st.executeUpdate(req);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	public void delete(Statement st){
		try {
			st.executeUpdate("DELETE FROM equipes WHERE id_tournoi = " + t.getId()+ " AND id_equipe = " + id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
