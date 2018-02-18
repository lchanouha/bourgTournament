package view;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import controller.ApplicationController;

import java.sql.Statement;
import model.Tournoi;

public class VueTournoiResultats extends Vue {
    private JScrollPane        resultats_js;
    BoxLayout                  resultats_layout;
    JLabel                     resultats_desc;
    JPanel                     resultats_bas;
    JLabel                     resultats_statut;
    
    public static String ID = "Resultats";
    
   
	public VueTournoiResultats(ApplicationController ac) {
		this.ac = ac;
	}

	@Override
	protected void initialiserDonnees() {
		JTable resultats_jt = creerTable(getTournoi(), getSQLConnector());
		resultats_js.setViewportView(resultats_jt);
	}
	
	@Override
	protected void initialiserStructure (){
		conteneurPrimaire= new JPanel();
		resultats_layout = new BoxLayout(conteneurPrimaire, BoxLayout.Y_AXIS);
		conteneurPrimaire.setLayout(resultats_layout);
		resultats_desc = new JLabel("Résultats du tournoi");
		conteneurPrimaire.add(resultats_desc);
		conteneurPrimaire.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
		
		resultats_js = new JScrollPane();
		conteneurPrimaire.add(resultats_js);
		//jt.setPreferredSize(getMaximumSize());

		
		resultats_bas = new JPanel();
		resultats_bas.add(resultats_statut = new JLabel("Gagnant:"));
		
		conteneurPrimaire.add(resultats_bas);		
	}

	private JTable creerTable(Tournoi t, Statement s){
		
		Vector< Vector<Object>> to =new Vector<Vector<Object>>();
		Vector<Object> v;
		int i=1;
		try {
			ResultSet rs = s.executeQuery("SELECT equipe,(SELECT nom_j1 FROM equipes e WHERE e.num_equipe = equipe AND e.id_tournoi = " + t.getId() + ") as joueur1, SUM(score) as score, (SELECT count(*) FROM matchs m WHERE (m.equipe1 = equipe AND m.score1 > m.score2  AND m.id_tournoi = " + t.getId() +") OR (m.equipe2 = equipe AND m.score2 > m.score1 )) as matchs_gagnes, (SELECT COUNT(*) FROM matchs m WHERE (m.equipe1 = equipe OR m.equipe2=equipe) AND m.id_tournoi=" + t.getId() +") as matchs_joues FROM  (select equipe1 as equipe,score1 as score from matchs where id_tournoi=" + t.getId() + " UNION ALL select equipe2 as equipe,score2 as score from matchs where id_tournoi=" + t.getId() + ") GROUP BY equipe ORDER BY matchs_gagnes DESC,score DESC, equipe ASC;");
			while(rs.next()){
				v = new Vector<Object>();
				v.add(i++);
				v.add(rs.getInt("equipe"));
				v.add(rs.getString("joueur1"));
				v.add(rs.getInt("score"));
				v.add(rs.getInt("matchs_gagnes"));
				v.add(rs.getInt("matchs_joues"));
				to.add(v);
				
			}
			rs.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		Vector<String> columnNames = new Vector<String>();
		columnNames.add("Rang");
		columnNames.add("Numéro d'équipe");
		columnNames.add("Nom d'équipe");
		columnNames.add("Score");
		columnNames.add("Matchs gagnés");
		columnNames.add("Matchs joués");
		JTable resultats_jt= new JTable(to,columnNames );		
		resultats_jt.setAutoCreateRowSorter(true);

		return resultats_jt;
		
	}

	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return ID;
	}
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Résultats";
	}
	@Override
	public boolean hasButton(){
		return true;
	}
	@Override
	public String getButtonText(){
		return "Résultats";
	}
	@Override
	public int getButtonOrder(){
		return 60; 
	}
	@Override
	public boolean buttonIsEnabled(){
		if (this.ac.getTournoi() == null) {
			return false;
		}
		
		int total=-1, termines=-1;
		try {
			ResultSet rs = ac.getSQLConnector().executeQuery("Select count(*) as total, (Select count(*) from matchs m2  WHERE m2.id_tournoi = m.id_tournoi  AND m2.termine='oui' ) as termines from matchs m  WHERE m.id_tournoi=" + this.ac.getTournoi().getId() +" GROUP by id_tournoi ;");
			if (!rs.next()) { 
				return false;
			}
			total    = rs.getInt(1);
			termines = rs.getInt(2);
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return total == termines && total > 0;
	}




}

