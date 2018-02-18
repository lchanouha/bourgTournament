package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.sql.Statement;

public class TournoiListeEquipes {
	private Tournoi t;
	private Statement st;

	private Vector<Equipe> dataeq = null;
	private Vector<Integer>ideqs  = null; 
	
	public TournoiListeEquipes(Tournoi t, Statement s){
		this.t  = t;
		this.st = s;
	}
	public void updateIfMandatory(){
		if (this.dataeq == null){
			updateFromDB();
		}
	}
	public Equipe get(int index){
		updateIfMandatory();
		return dataeq.get(index);
	}
	public Equipe getById(int id){
		updateIfMandatory();
		for(Equipe e: dataeq){
			if (e.id == id){
				return e;
			}
		}
		return null;
	}
	public int size(){
		updateIfMandatory();
		return dataeq.size();
	}
	public int lookupForNewEqNumber(){
		updateFromDB();
		int a_aj= this.dataeq.size()+1;
		for ( int i=1;i <= this.dataeq.size(); i++){
			if(!ideqs.contains(i)){
				a_aj=i;
				break;
			}
		}
		return a_aj;
	}
	public void updateFromDB(){
		this.dataeq = new Vector<Equipe>();
		this.ideqs  = new Vector<Integer>();
		try {
			ResultSet rs = st.executeQuery("SELECT * FROM equipes WHERE id_tournoi = " + this.t.getId() + " ORDER BY num_equipe;");
			while(rs.next()){
				dataeq.add(new Equipe(t, rs.getInt("id_equipe"),rs.getInt("num_equipe"), rs.getString("nom_j1")));
				ideqs .add(rs.getInt("num_equipe"));
			}
			rs.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}
