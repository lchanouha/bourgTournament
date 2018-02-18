package view;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import controller.ApplicationController;
import model.Tournoi;

public class VueTournoiDetails extends Vue {

	JLabel                     detailt_nom;
	JLabel                     detailt_statut;
	JLabel                     detailt_nbtours;
	JLabel                     detailt_manuauto;
	JButton                    detailt_activermanu;

	public static String ID = "DetailsTournoi";
	
	public VueTournoiDetails(ApplicationController ac) {
		this.ac = ac;
	}
	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return ID;
	}
	@Override
	protected void initialiserDonnees () {
		Tournoi t = ac.getTournoi();
		detailt_nom.setText(t.getNom());
		detailt_statut.setText(t.getNStatut());
		detailt_nbtours.setText(Integer.toString(t.getNbTours()));	
		detailt_activermanu.setEnabled(!t.isManuel());
		detailt_manuauto .setText(t.isManuel() ? "Mode manuel":"Automatique");
	}
	protected void initialiserStructure(){
		Tournoi t = ac.getTournoi();
		conteneurPrimaire = new JPanel();
		conteneurPrimaire.setLayout(new BoxLayout(conteneurPrimaire, BoxLayout.Y_AXIS));
		conteneurPrimaire.add(new JLabel("Détail du tournoi"));
		
		JPanel tab = new JPanel( new GridLayout(4,2));
		detailt_nom = new JLabel(t.getNom());
		tab.add(new JLabel("Nom du tournoi"));
		tab.add(detailt_nom);

		detailt_statut = new JLabel(t.getNStatut());
		tab.add(new JLabel("Statut"));
		tab.add(detailt_statut);
		
		detailt_nbtours = new JLabel(Integer.toString(t.getNbTours()));
		tab.add(new JLabel("Nombre de tours:"));
		tab.add(detailt_nbtours);
		
		tab.add(new JLabel("Mode de gestion:"));
		tab.add(detailt_manuauto = new JLabel("Krum"));
		
		//detailt_nbtours.setPreferredSize(new Dimension(40,40));

		conteneurPrimaire.add(tab);
		conteneurPrimaire.add(detailt_activermanu = new JButton("Passer au mode manuel"));
		detailt_activermanu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
		
				int ret = JOptionPane.showConfirmDialog(null, "Êtes-vous sur de passer à la gestion manuelle ? \nVous pourrez éditer les équipes et les matchs joués, mais certaines fonctionnalités risquent de ne plus fonctionner. Cette action est irréversible.", "Passage au mode manuel",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.YES_NO_CANCEL_OPTION);
				switch(ret){
				case 0:
					// Oui
					VueTournoiDetails.this.ac.getTournoi().setManuel();
					initialiserDonnees();
					
				break;
				default:
					
				break;
				}
			}
		});
		//detailt_enregistrer = new JButton("Enregistrer");
		//p.add(Box.createHorizontalGlue());
		//p.add(detailt_enregistrer);
		conteneurPrimaire.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
		//p.add(new JLabel("  e"));
		//tab.setPreferredSize(new Dimension(-1, 200));
		
	}
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Informations sur le tournoi";
	}
	@Override
	public boolean hasButton(){
		return true;
	}
	@Override
	public String getButtonText(){
		return "Informations";
	}
	@Override
	public int getButtonOrder(){
		return 20; 
	}
	@Override
	public boolean buttonIsEnabled(){
		return this.ac.getTournoi() != null;
	}

}
