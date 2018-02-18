package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import model.Tournoi;

public class InterfacePleinEcran extends JWindow {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	public DisplayMode originalDM;
	public Tournoi t;
	public Statement st;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane4;
	private JScrollPane scrollPane2;
	final static String VUE1 = "vue1";
    final static String VUE2 = "vue2";
    private CardLayout fen;
	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FullScreenResult frame = new FullScreenResult();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/
	private JScrollPane scrollPane3;
	private JPanel contenu;
	private JPanel contenu2;
	private JPanel deniers_enregistrement;
	private JPanel classements;
	private JTextArea infostournoi;
	public GraphicsDevice d;
	/**
	 * Create the frame.
	 */
	public InterfacePleinEcran(int numaff, Tournoi _t, Statement _st,final Fenetre f) {
		t = _t;
		st = _st;
		GraphicsEnvironment env = GraphicsEnvironment.
	            getLocalGraphicsEnvironment();
		
		final GraphicsDevice[] devices = env.getScreenDevices();
		originalDM = devices[numaff].getDisplayMode();
		d = devices[numaff];
		System.out.println("W:" + originalDM.getWidth() + ", H: " + originalDM.getHeight());
		setBounds(0, 0, originalDM.getWidth(), originalDM.getHeight());
	        	
	    devices[numaff].setFullScreenWindow(this);
	            
		// DisplayModeTest test = new DisplayModeTest(devices[i]);
		//test.initComponents(test.getContentPane());
		//test.begin();
		setVisible(true);
	
		contentPane = new JPanel();
		//contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		infostournoi=new JTextArea("Infos sur le tournoi!!!!\n\n\n\nhfghf");
		
		panel.setLayout(new BorderLayout(0, 0));
		panel.add(infostournoi,BorderLayout.CENTER);
		
		JButton btnQuitter = new JButton("Quitter le plein écran");
		btnQuitter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				detruire();
				f.notifyFullScreenExit();
			}
		});
		panel.add(btnQuitter, BorderLayout.EAST);
		
		JTextArea txtrTournoiDeBelote = new JTextArea();
		txtrTournoiDeBelote.setText("Tournoi de belote\\nkjkjkl");
		//panel.add(txtrTournoiDeBelote, BorderLayout.WEST);
	
		fen=new CardLayout();
		contenu = new JPanel(fen);
		//contenu.setBorder(BorderFactory.createLineBorder(Color.black));
		//contenu.setLayout();
		contentPane.add(contenu,BorderLayout.CENTER);
		
		scrollPane = new JScrollPane();
		contenu.add(scrollPane,VUE1);
		
		contenu2 = new JPanel();
		contenu2.setLayout(new GridLayout(0,2));
		//contentPane.add(scrollPane, BorderLayout.WEST);
		deniers_enregistrement =new JPanel();
		//deniers_enregistrement.setBorder(BorderFactory.createEtchedBorder());
		deniers_enregistrement.setMaximumSize(getMaximumSize());
		deniers_enregistrement.setLayout(new BoxLayout(deniers_enregistrement, BoxLayout.PAGE_AXIS));
		scrollPane3 = new JScrollPane();
		JLabel titre_matchs_saisis_ce_tour = new JLabel("Derniers scores enregistrés");
		titre_matchs_saisis_ce_tour.setFont(new Font("Serif", Font.BOLD, 30));
		deniers_enregistrement.add(titre_matchs_saisis_ce_tour);
		deniers_enregistrement.add(scrollPane3);
		JLabel titre_matchs_en_cours = new JLabel("Parties en cours ou non saisies");
		titre_matchs_en_cours.setFont(new Font("Serif", Font.BOLD, 30));
		deniers_enregistrement.add(titre_matchs_en_cours);
		scrollPane4 = new JScrollPane();
		deniers_enregistrement.add(scrollPane4);
		
		
		contenu2.add(deniers_enregistrement ,BorderLayout.WEST);

		classements = new JPanel();
		classements.setMaximumSize(getMaximumSize());
		classements.setLayout(new BoxLayout(classements, BoxLayout.PAGE_AXIS));
		
		scrollPane2 = new JScrollPane();
		JLabel titre_classement = new JLabel("Classement général");
		titre_classement.setFont(new Font("Serif", Font.BOLD, 30));
		classements.add(titre_classement);
		classements.add(scrollPane2);
		
		contenu2.add(classements);
		contenu.add(contenu2 ,VUE1);
		
		maj();
	}
	public void detruire(){
		//d.setDisplayMode(originalDM);
		InterfacePleinEcran.this.dispose();	
		//FullScreenResult.this.setVisible(false);
		//FullScreenResult.this.repaint();
		
	}
	

	

	
	public void maj(){
		

		if(t == null || this.st == null){
			System.out.println("Erreur le tournoi est vide!");
			return ;
		}
		String text_info="Tournoi " + t.getNom() + "\n";
		if(t.getStatut() == 0){
			text_info+="Inscription des équipes en cours (" + t.getNbEquipes() + " pour l'instant)\n";
		}else{
			text_info+=t.getNbEquipes() + " équipes sont enregistrées\n";
			text_info+="Tour numéro " + t.getNbTours() + " en cours, ";
			
			int total=-1, termines=-1;
			try {
				ResultSet rs = st.executeQuery("Select count(*) as total, (Select count(*) from matchs m2  WHERE m2.id_tournoi = m.id_tournoi AND m2.termine='oui' ) as termines from matchs m  WHERE m.id_tournoi=" + this.t.getId() +" GROUP by id_tournoi ;");

				//ResultSet rs = st.executeQuery("Select count(*) as total, (Select count(*) from matchs m2  WHERE m.num_tour="+this.t.getNbTours()+" AND m2.id_tournoi = m.id_tournoi  AND m2.termine='oui' ) as termines from matchs m  WHERE m.num_tour="+this.t.getNbTours()+" AND  m.id_tournoi=" + this.t.getId() +" GROUP by id_tournoi ;");
				if(rs.next()){
					total    = rs.getInt(1);
					termines = rs.getInt(2);
				}
				else total=termines=0;
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();

			}
			text_info += termines + "/" + total + " matchs enregistrés";
		}
		infostournoi.setText(text_info);

		/*************************************************************
		// Équipes ***************************************************
		 ************************************************************/
		
			JTable table = InterfacePleinEcranTableEquipes.constructTable(t, st);
			scrollPane.setViewportView(table);
			
		/*************************************************************
		// Matchs à faire ********************************************
		 *********************************************************** */

			JTable a_faire = InterfacePleinEcranTableMatchsAFaire.constructTable(t, st);
			scrollPane4.setViewportView(a_faire);

		/*************************************************************
		// Classement final ******************************************
		 *********************************************************** */		
		
			JTable resultats_jt = InterfacePleinEcranTableClassement.constructTable(t, st);
			scrollPane2.setViewportView(resultats_jt);
			
		/*************************************************************
		// Matchs effectués ce tour **********************************
		 *********************************************************** */
			
			JTable last_updates = InterfacePleinEcranTableResultatsSaisieTour.constructTable(t,st);
			scrollPane3.setViewportView(last_updates);
			
		if(t.getNbEquipes() == 0 || t.getStatut() == 0){
			//scrollPane2.setVisible(false);
			//scrollPane3.setVisible(false);
			//scrollPane.setVisible(true);
			fen.show(contenu, VUE2);
			//contentPane.add(scrollPane, BorderLayout.CENTER);
		}else{
			//scrollPane2.setVisible(true);
			//scrollPane3.setVisible(true);
			//scrollPane.setVisible(false);	
			fen.show(contenu, VUE1);
			//contentPane.add(scrollPane2,BorderLayout.WEST);
			//contentPane.add(scrollPane3,BorderLayout.EAST);
		}	
	}
	
}
