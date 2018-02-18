package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import controller.ApplicationController;
import model.Tournoi;

public class VueTournoiTours extends Vue {


	JScrollPane                tours_js;
	BoxLayout                  tours_layout;
	JLabel                     tours_desc;
	
	JButton                    tours_ajouter;
	JButton                    tours_supprimer;
	JButton                    tours_rentrer;
	
    

    public static String ID = "Tours";
    boolean peutajouter = true;
    Vector< Vector<Object>> to;
   
	public VueTournoiTours(ApplicationController ac) {
		this.ac = ac;
	}


	@Override
	protected void initialiserDonnees(){
		getTableData();
		if(to.size() == 0){
			tours_supprimer.setEnabled(false);
			tours_ajouter.setEnabled(true);
		}else{
			
			tours_supprimer.setEnabled( getTournoi().getNbTours() > 1);
			
			if(!peutajouter || getTournoi().getNbTours()  >= getTournoi().getNbEquipes()-1 ){
				tours_ajouter.setEnabled(false);
			}else
				tours_ajouter.setEnabled(true);
		}	
		JTable tours_t = creerTable(getTournoi(), getSQLConnector());
		tours_js.setViewportView(tours_t);
	}
	private Vector< Vector<Object>> getTableData(){
		to = new Vector<Vector<Object>>();

		peutajouter = true;
		try {
			ResultSet rs = getSQLConnector().executeQuery("Select num_tour,count(*) as tmatchs, (Select count(*) from matchs m2  WHERE m2.id_tournoi = m.id_tournoi  AND m2.num_tour=m.num_tour  AND m2.termine='oui' ) as termines from matchs m  WHERE m.id_tournoi=" + getTournoi().getId() + " GROUP BY m.num_tour,m.id_tournoi;");
			while(rs.next()){
				Vector<Object> v = new Vector<Object>();
				v.add(rs.getInt("num_tour"));
				v.add(rs.getInt("tmatchs"));
				v.add(rs.getString("termines"));
				to.add(v);
				peutajouter = peutajouter && rs.getInt("tmatchs") == rs.getInt("termines");
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return to;

	}
	private JTable creerTable(Tournoi t, Statement s){
		Vector<String> columnNames = new Vector<String>();
		columnNames.add("Numéro du tour");
		columnNames.add("Nombre de matchs");
		columnNames.add("Matchs joués");
		JTable tours_t = new JTable(getTableData(),columnNames );
		tours_t.setEnabled(false);
		return tours_t;
	}
	@Override
	protected void initialiserStructure(){

		conteneurPrimaire      = new JPanel();
		tours_layout = new BoxLayout( conteneurPrimaire, BoxLayout.Y_AXIS);
		conteneurPrimaire.setLayout( tours_layout);
		tours_desc = new JLabel("Tours");
		conteneurPrimaire.add(tours_desc);
		conteneurPrimaire.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

		
		tours_js = new JScrollPane();
		conteneurPrimaire.add(tours_js);
		
		JPanel bt    = new JPanel();
		tours_ajouter   = new JButton("Ajouter un tour");
		tours_supprimer = new JButton("Supprimer le dernier tour");
		//tours_rentrer   = new JButton("Rentrer les scores du tour s�lectionn�");
		bt.add(tours_ajouter);
		bt.add(tours_supprimer);
		//bt.add(tours_rentrer);
		conteneurPrimaire.add(bt);	
		conteneurPrimaire.add(new JLabel("Pour pouvoir ajouter un tour, terminez tous les matchs du précédent."));
		conteneurPrimaire.add(new JLabel("Le nombre maximum de tours est \"le nombre total d'équipes - 1\""));
		tours_ajouter.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				VueTournoiTours.this.getTournoi().getGestionnaireTours().ajouterTour();
				VueTournoiTours.this.ac.afficherVue(VueTournoiTours.ID);
				VueTournoiTours.this.ac.notifyDataUpdate();
				//Fenetre.this.tracer_tours_tournoi();
			}
		});
		tours_supprimer.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				VueTournoiTours.this.getTournoi().getGestionnaireTours().supprimerTour();
				VueTournoiTours.this.ac.afficherVue(VueTournoiTours.ID);
				VueTournoiTours.this.ac.notifyDataUpdate();
			}
		});
	}

	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return VueTournoiTours.ID;
	}
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Gestion des rounds";
	}
	@Override
	public boolean hasButton(){
		return true;
	}
	@Override
	public String getButtonText(){
		return "Tours";
	}
	@Override
	public int getButtonOrder(){
		return 40; 
	}
	@Override
	public boolean buttonIsEnabled(){
		return getTournoi() != null 
				&&
				(getTournoi().isManuel() || getTournoi().getStatut() > Tournoi.STATUS_INSCRIPTION_JOUEURS);
	}


}