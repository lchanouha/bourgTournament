package view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

import controller.ApplicationController;
import model.Database;
import model.Tournoi;

public class VueAccueilGestionTournois extends Vue {
	
	public static String ID = "Tournois";
	
	
	private JList<String> listeTournois;
	private JButton       creerTournoi;
	private JButton       selectTournoi;	
	private JButton       deleteTournoi;
	
	public VueAccueilGestionTournois(ApplicationController ac) {
		this.ac = ac;
	}
	@Override
	protected void initialiserDonnees (){
		//delFullScreen();
		int nbdeLignes = 0;
		Vector<String> noms_tournois = new Vector<String>();
		
		ResultSet rs;
		try {
			rs = ac.getSQLConnector().executeQuery("SELECT * FROM tournois;");
			while( rs.next() ){
				nbdeLignes++;
				noms_tournois.add(rs.getString("nom_tournoi"));
			}
			rs.close();
		} catch (SQLException e) {
			System.out.println("Erreur lors de la requète :" + e.getMessage());
			e.printStackTrace();
		}
		
		listeTournois.setListData(noms_tournois);

        if(nbdeLignes == 0){
        	selectTournoi.setEnabled(false);
        	deleteTournoi.setEnabled(false);
        }else{
        	selectTournoi.setEnabled(true);
        	deleteTournoi.setEnabled(true);
        	listeTournois.setSelectedIndex(0);
        }
	}

	public void initialiserStructure(){
		conteneurPrimaire = new JPanel();
		
		conteneurPrimaire.setLayout(new BoxLayout(conteneurPrimaire, BoxLayout.Y_AXIS));
		conteneurPrimaire.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
        JLabel description = new JLabel("Description :");
        
        Box bh3 = Box.createHorizontalBox();
        
        bh3.add(description);
        bh3.add(Box.createHorizontalGlue());
        conteneurPrimaire.add(bh3);
		
        JTextArea gt = new JTextArea("Gestion de tournoi\nLouis Chanouha, 2018\nlouis@chanouha.fr\nCréé initialement pour les besoins du tournoi de belote de Bournazel");
		gt.setAlignmentX(Component.CENTER_ALIGNMENT);
		gt.setEditable(false);
		conteneurPrimaire.add(gt);
		
		// Recherche de la liste des tournois
		JPanel ListeTournois = new JPanel();
		
		conteneurPrimaire.add(ListeTournois);		

		listeTournois = new JList<String>(new Vector<String>()); 
		listeTournois.setAlignmentX(Component.LEFT_ALIGNMENT); 
		listeTournois.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		//list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
	    listeTournois.setVisibleRowCount(-1);
	    JScrollPane listScroller = new JScrollPane(listeTournois);
        listScroller.setPreferredSize(new Dimension(250, 180));
        //listScroller.setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel label = new JLabel("Liste des tournois :");
        label.setLabelFor(listeTournois);
        
        Box bh2 = Box.createHorizontalBox();
        
        bh2.add(label);
        bh2.add(Box.createHorizontalGlue());
        conteneurPrimaire.add(bh2);
        conteneurPrimaire.add(listScroller);
        Box bh = Box.createHorizontalBox();
        conteneurPrimaire.add(bh);
		creerTournoi = new JButton("Créer un nouveau tournoi");
		selectTournoi = new JButton("Sélectionner le tournoi");
		deleteTournoi = new JButton("Supprimer le tournoi");
		bh.add(creerTournoi);
		bh.add(selectTournoi);	
		bh.add(deleteTournoi);
		
		conteneurPrimaire.updateUI();

        creerTournoi.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				VueAccueilGestionTournois.this.creerTournoi();
				ac.setTournoi(null);
				initialiserDonnees();
			}
		});
        
        deleteTournoi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Tournoi.deleteByName(VueAccueilGestionTournois.this.ac.getSQLConnector(), VueAccueilGestionTournois.this.listeTournois.getSelectedValue());
				ac.setTournoi(null);
				initialiserDonnees();
			}
		});
        selectTournoi.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				String nt = VueAccueilGestionTournois.this.listeTournois.getSelectedValue();
				Tournoi tournoi_selectionne = Tournoi.fromName(nt, VueAccueilGestionTournois.this.ac.getSQLConnector());
				ac.setTournoi(tournoi_selectionne);
				
				String defaultVue = VueTournoiDetails.ID;
				switch (tournoi_selectionne.getStatut()){
				case Tournoi.STATUS_INSCRIPTION_JOUEURS:
					defaultVue = VueTournoiEquipes.ID;
					break;
				case Tournoi.STATUS_INSCRIPTION_TERMINEE:
					defaultVue = VueTournoiTours.ID;
					break;
				case Tournoi.STATUS_SAISIE_MATCHS:
					defaultVue = VueTournoiSaisieMatchs.ID;
				}
				
				ac.afficherVue(defaultVue);
			}
		});
		
	}

	@Override
	public String getID() {
		return ID;
	}
	@Override
	public String getTitle() {
		if (this.ac.getTournoi() == null){
			return "Accueil de l'application - Aucun tournoi sélectionné";
		} else {
			return "Accueil de l'application";
		}
		
	}
	@Override
	public boolean hasButton(){
		return true;
	}
	@Override
	public String getButtonText(){
		return "Tournois";
	}
	@Override
	public int getButtonOrder(){
		return 10; 
	}
	@Override
	public boolean buttonIsEnabled(){
		return true;
	}

	private void creerTournoi(){
		String s = (String)JOptionPane.showInputDialog(
                null,
                "Entrez le nom du tournoi",
                "Nom du tournoi",
                JOptionPane.PLAIN_MESSAGE);
		
		
		if(s == null || s.equals("")){
			return ;
		}else{
			try {
				s =  Database.mysql_real_escape_string(s);
				if(s.length() < 3){
					JOptionPane.showMessageDialog(null, "Le tournoi n'a pas été créé. Nom trop court.");
					return ;					
				}
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, e.getMessage());
				return ;	
			}
			if(s == ""){
				JOptionPane.showMessageDialog(null, "Le tournoi n'a pas été cré. Ne pas mettre de caractères spéciaux ou accents dans le nom");
				return ;
			}else{
				Tournoi t = Tournoi.fromName(s, ac.getSQLConnector());
				
				if(t != null){
					JOptionPane.showMessageDialog(null, "Le tournoi n'a pas été créé. Un tournoi du même nom existe déjà");
					return ;							
				}
				Tournoi.createNew(ac.getSQLConnector(), s);

			}
		}
	}
}
