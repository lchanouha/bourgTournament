package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class TournoiListeMatchs {
	private Vector<TournoiMatch> datam  = null;
	
	private Tournoi t;
	private Statement st;
	
	public TournoiListeMatchs(Tournoi t, Statement s){
		this.t  = t;
		this.st = s;
	}
	public void updateIfMandatory(){
		if (this.datam == null){
			updateFromDB();
		}
	}
	public TournoiMatch get(int index){
		updateIfMandatory();
		return datam.get(index);
	}
	public int size(){
		updateIfMandatory();
		return datam.size();
	}
	public void updateFromDB(){
		this.datam = new Vector<TournoiMatch>();
		try {
			ResultSet rs= st.executeQuery("SELECT * FROM matchs WHERE id_tournoi="+ t.getId() + " ORDER BY num_tour DESC,num_match_t ASC;");
			while(rs.next()) 
				datam.add(TournoiMatch.fromDBRow(rs));
			rs.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}
