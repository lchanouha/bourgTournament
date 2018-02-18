package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import controller.ApplicationController;
import model.Tournoi;

public class VueTournoiSaisieMatchs extends Vue {

    
    public static String ID = "Matchs";
    
    private JScrollPane        match_js;



    JLabel                     match_statut;
    JButton                    match_valider;
    JButton                    match_addm;
    JButton                    match_delm;
    JTable jt;
    
    
	final static int colonneTour = 0;
	final static int colonneTable = 1;
    final static int colonneNumEquipe1 = 2;
    final static int colonneScoreEquipe1 = 3;
    final static int colonneNumEquipe2 = 4;
    final static int colonneScoreEquipe2 = 5;
    

	public VueTournoiSaisieMatchs(ApplicationController ac) {
		this.ac = ac;
	}
	
	@Override
	protected void initialiserDonnees(){
		getTournoi().majMatch();
		majStatutM();
		JTable matchs_jt = creerTable(getTournoi(), getSQLConnector());
		this.jt = matchs_jt;
		match_js.setViewportView(matchs_jt);
		
		match_addm.setEnabled(getTournoi().isManuel() && getTournoi().getNbEquipes()>1);
		match_delm.setEnabled(getTournoi().isManuel() && getTournoi().getNbEquipes()>1);
		matchs_jt.getColumnModel().getColumn(colonneTour).setPreferredWidth(30);
		matchs_jt.getColumnModel().getColumn(colonneTable).setPreferredWidth(30);
		matchs_jt.getColumnModel().getColumn(colonneNumEquipe1).setPreferredWidth(50);
		matchs_jt.getColumnModel().getColumn(colonneNumEquipe2).setPreferredWidth(50);
		((AbstractTableModel) matchs_jt.getModel()).fireTableDataChanged();
		
	}
	private JTable creerTable (Tournoi t, Statement s){
		
		JTable match_jt = new JTable();
		//new EditableCellFocusAction(match_jt, KeyStroke.getKeyStroke("TAB"));
		VueTournoiSaisieMatchsTableModele m = new VueTournoiSaisieMatchsTableModele(this.ac, this);
		match_jt.setModel(m);
		match_jt.setRowHeight(40);
		match_jt.setDefaultRenderer(Object.class, new VueTournoiSaisieMatchsTableRenderer(ac));
		
		return match_jt;
	}
	protected void initialiserStructure (){
			conteneurPrimaire      = new JPanel();
			BoxLayout match_layout = new BoxLayout(conteneurPrimaire, BoxLayout.Y_AXIS);
			conteneurPrimaire.setLayout(match_layout);
			JLabel match_desc = new JLabel("Matchs du tournoi");
			conteneurPrimaire.add(match_desc);
			conteneurPrimaire.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
			//c.add(match_p, MATCHS );


			//match_jt.setRowSelectionAllowed(true);
			//match_jt.setSurrendersFocusOnKeystroke(true);

			match_js = new JScrollPane();
			conteneurPrimaire.add(match_js);
			//jt.setPreferredSize(getMaximumSize());

			
			JPanel match_bas = new JPanel();
			match_bas.add(match_statut = new JLabel("?? Matchs joués"));
			match_bas.add(match_valider = new JButton("Afficher les résultats"));
			match_bas.add(match_addm = new JButton("Ajouter match"));
			match_bas.add(match_delm = new JButton("Supprimer match"));
			match_valider.setEnabled(false);
			match_addm.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent arg0) {
					VueTournoiSaisieMatchs.this.getTournoi().addMatch();
					VueTournoiSaisieMatchs.this.initialiserDonnees();
					VueTournoiSaisieMatchs.this.ac.notifyDataUpdate();
				}
			});
			match_delm.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if(jt.getSelectedRow() >=0) {
						VueTournoiSaisieMatchs.this.getTournoi().delMatch(jt.getSelectedRow());
						VueTournoiSaisieMatchs.this.initialiserDonnees();
						VueTournoiSaisieMatchs.this.ac.notifyDataUpdate();
					}
				}
			});
			match_valider.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					VueTournoiSaisieMatchs.this.ac.afficherVue(VueTournoiResultats.ID);
				}
			});
			conteneurPrimaire.add(match_bas);
			//majStatutM();

			
		
		//match_addm.setEnabled(t.isManuel() && t.getNbEquipes()>1);
		//match_delm.setEnabled(t.isManuel() && t.getNbEquipes()>1);
		//fen.show(c, MATCHS);
		
	}
	void majStatutM(){
		int total=-1, termines=-1;
		try {
			ResultSet rs = getSQLConnector().executeQuery("Select count(*) as total, (Select count(*) from matchs m2  WHERE m2.id_tournoi = m.id_tournoi  AND m2.termine='oui' ) as termines from matchs m  WHERE m.id_tournoi=" + getTournoi().getId() +" GROUP by id_tournoi ;");
			if(rs.next()){
				total    = rs.getInt(1);
				termines = rs.getInt(2);
			}
			else total=termines=0;
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
		match_statut.setText(termines + "/" + total + " matchs terminés");
		match_valider.setEnabled(total == termines);
	}


	@Override
	public String getID() {
		return ID;
	}
	public String getTitle() {
		return "Saisie des matchs";
	}
	@Override
	public boolean hasButton(){
		return true;
	}
	@Override
	public String getButtonText(){
		return "Matchs";
	}
	@Override
	public int getButtonOrder(){
		return 50; 
	}
	@Override
	public boolean buttonIsEnabled(){
		return getTournoi() != null 
				&& getTournoi().getNbTours() > 0
				&&
				(getTournoi().isManuel() || this.getTournoi().getStatut() > Tournoi.STATUS_INSCRIPTION_TERMINEE);
	}


}
