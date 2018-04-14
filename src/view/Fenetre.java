package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import controller.ApplicationController;
import main.bourgTournament;




public class Fenetre extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    
    
    private final String statut_deft = "Gestion de tournois - " + bourgTournament.VERSION;

	// Liens objets
	private Statement s;
	private ApplicationController ac = null;
	
	// Conteneurs de la VUE
	private JPanel 			conteneurVues = null;
	private JList<String>   listAffichagesDisponibles = null;
	private JPanel			conteurSelecteurPages = null;
	private JLabel 			statut_slect = null;
	
	private CardLayout fen;


    
    
    
    
	public Fenetre(ApplicationController ac){

		this.ac = ac;
		this.vues = new ArrayList<Vue>();

		this.s = ac.getSQLConnector();
		
		try {
		    UIManager.setLookAndFeel(UIManager
		                               .getSystemLookAndFeelClassName());
		} catch(Exception e) {}
		
		this.initialiserStructure();
	
		/*
		try {
			//UIManager.setLookAndFeel(new MaterialLookAndFeel());
		} catch (Exception e) {
		    e.printStackTrace();
		}
        SwingUtilities.invokeLater ( new Runnable ()
        {
            public void run ()
            {
                // Install WebLaF as application L&F
                WebLookAndFeel.install ();

                // You can also do that with one of old-fashioned ways:
                // UIManager.setLookAndFeel ( new WebLookAndFeel () );
                // UIManager.setLookAndFeel ( "com.alee.laf.WebLookAndFeel" );
                // UIManager.setLookAndFeel ( WebLookAndFeel.class.getCanonicalName () );

                // Create you application here using Swing components
                // JFrame frame = ...

                // Or use similar Web* components to get access to some extended features
                // WebFrame frame = ...
            }
        } );*/
			
		
		
	}

    public void notifyDataUpdate() {
    	if (this.fulls != null) {
    		this.fulls.maj();
    	}
    }
	public void setStatutSelect(String t){
		statut_slect.setText(statut_deft + "" + t);
	}	
	
	public void initialiserStructure(){
		
		this.setTitle("Gestion de tournois");
		setSize(800,600);
		//setUndecorated(true);
		this.setVisible(true);
		this.setLocationRelativeTo(this.getParent());
		
		
		JPanel contenu = new JPanel();
		contenu.setLayout(new BorderLayout());
		this.setContentPane(contenu);
		
		/****************************************
		 * Partie haute *************************
		 ****************************************/
		
			JPanel phaut = new JPanel();
			contenu.add(phaut,BorderLayout.NORTH);
			phaut.add(statut_slect = new JLabel());
			this.setStatutSelect("Pas de tournoi sélectionné");
			
		/****************************************
		 * Partie droite (vue) ******************
		 ****************************************/
	
			this.fen 		   = new CardLayout();
			this.conteneurVues = new JPanel(fen);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			contenu.add(conteneurVues,BorderLayout.CENTER);
		
		/****************************************
		 * Partie gauche (menu) ******************
		 ****************************************/		

		
			JPanel pgauche = new JPanel();
			pgauche.setPreferredSize(new Dimension(120,0));
			pgauche.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));
			contenu.add(pgauche,BorderLayout.WEST);
			
			//pgauche.setLayout(new FlowLayout(FlowLayout.LEFT));
		

			conteurSelecteurPages = new JPanel();
			pgauche.add(conteurSelecteurPages);
		
			// Vue plein écran: menu de sélection de l'affichage
		
			listAffichagesDisponibles      = new JList<String>();
			constructDisplayList();
			new Thread(new Runnable() {
				@Override
				public void run() {
					while(true){
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						constructDisplayList();
					}
					
				}
			}).start();
			//listaff.setSelectionMode(selectionMode)
			listAffichagesDisponibles.setSelectedIndex(0);
			listAffichagesDisponibles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			listAffichagesDisponibles.addListSelectionListener(new ListSelectionListener() {
				 
				@Override
				public void valueChanged(ListSelectionEvent arg0) {
					if (arg0.getValueIsAdjusting() ) return ;
					if(Fenetre.this.ac.getTournoi() == null) listAffichagesDisponibles.setSelectedIndex(0);
					@SuppressWarnings("unchecked")
					JList<String> jList = ((JList<String>)arg0.getSource());
					int s= jList.getSelectedIndex();
					if(s == 0){
						System.out.println("quitter le plein écran");
						delFullScreen();
					}else{
						delFullScreen(true);
						fulls=new InterfacePleinEcran(s-1,Fenetre.this.ac.getTournoi(),Fenetre.this.s,Fenetre.this);
					} 
					return ;
					//new FullScreenResult(arg0.getFirstIndex());
	
					
				}
			});
			JPanel pInti = new JPanel();
			pInti.add(new JLabel("Vue plein écran:   "));

			listAffichagesDisponibles.setFixedCellWidth(100);
			pgauche.add(pInti);
			pgauche.add(listAffichagesDisponibles);		
		
	}
	
	public void initialiserMenu(){
		conteurSelecteurPages.add(constuctMenu());
	}
    
    
    /* *****************************************************
     * Gestion des vues  ***********************************
     ******************************************************/
	    
        // Générique: panel droit (une seule vue à la fois)
	    private ArrayList<Vue> vues;
	    private String currentView = "";
	    public void enregistrerConteneurVue(String vID, Component conteneur){
	    	conteneurVues.add(conteneur, vID);
	    }
	    public void dessinerVue(String vID){
	    	fen.show(conteneurVues, vID);
	    	currentView = vID;
	    	conteneurVues.revalidate();
	    	conteneurVues.repaint();
	    }
	    public Vue getCurrentView(){
	    	for(Vue v: vues) {
	    		if (v.getID().equals(currentView)){
	    			return v;
	    		}
	    	}
	    	return null;
	    }
	    public void afficherVue(String vID){
	    	for(Vue v: vues) {
	    		if (v.getID().equals(vID)){
	    			v.afficher();
	    			updateMenu();
	    			return ;
	    		}
	    	}
	    	System.out.println("??? vue non existante lors de afficherVue");
	    }
	    
	    // Interface de visualisation plein écran
	    
	    	private InterfacePleinEcran fulls = null;
	    	
		    public void majFullScreen(){
		    	if(fulls != null){
		    		fulls.t = this.ac.getTournoi();
		    		fulls.maj();
		    	}
		    }
		    public void delFullScreen(){
		    	delFullScreen(false);
		    }
		    public void delFullScreen(boolean b){
		    	if(fulls!=null)
		    	fulls.detruire();
		    	if(!b)
		    	listAffichagesDisponibles.setSelectedIndex(0);
		    	fulls = null;
		    }
		    private void constructDisplayList(){
		    	DefaultListModel<String> mod_list = new DefaultListModel<>();
				mod_list.addElement("Aucun");
				GraphicsEnvironment env = GraphicsEnvironment.
			            getLocalGraphicsEnvironment();
				final GraphicsDevice[] devices = env.getScreenDevices();
				for(int i=1;i<=devices.length;i++){
					mod_list.addElement("Affichage "+ i);
				}
				if (listAffichagesDisponibles.getModel().getSize() != mod_list.size()){
					listAffichagesDisponibles.setModel(mod_list);
				}
		    }
		    void notifyFullScreenExit(){
		    	listAffichagesDisponibles.setSelectedIndex(0);
		    }
 
    /* *****************************************************
     * Gestion du menu *************************************
     ******************************************************/
    
	    private Map<String, JButton> menuItems;
	    private JPanel constuctMenu(){
	    	menuItems = new HashMap<String, JButton>();
			JPanel panel_buttons = new JPanel();
			panel_buttons.setLayout(new GridBagLayout());
			GridBagConstraints cons = new GridBagConstraints();
			cons.fill = GridBagConstraints.HORIZONTAL;
			cons.weightx = 1;
			cons.gridx = 0;

			panel_buttons.setBackground(Color.ORANGE);
	    	for(int i = 0;i < vues.size(); i++){
	    		final Vue v = vues.get(i);
	    		if (v.hasButton()){
	    			JButton bt = new JButton(v.getButtonText());
	    			menuItems.put(v.getID(), bt);
	    			//bt.setPreferredSize(new Dimension(taille_boutons,hauteur_boutons));
	    			bt.addActionListener(new ActionListener() {
	    				public void actionPerformed(ActionEvent arg0) {
	    					afficherVue(v.getID());
	    				}
	    			});
	    			panel_buttons.add(bt, cons);
	    				
	    		}
	    	}
	    	return panel_buttons;
	    }
	    public void updateMenu(){
	    	for(int i = 0;i < vues.size(); i++){
	    		Vue v = vues.get(i);
	    		JButton b = menuItems.get(v.getID());
	    		if (b == null){
	    			continue;
	    		}
	    		b.setEnabled(v.buttonIsEnabled());	 
	    		b.setBackground((Color) (currentView.equals(v.getID()) ? Color.GRAY: Color.WHITE));
	    		b.setForeground((Color) (currentView.equals(v.getID()) ? Color.RED: Color.BLACK));
	    		
	    	}
	    	listAffichagesDisponibles.setEnabled(this.ac.getTournoi() != null);
	    	if (getCurrentView() != null){
	    		this.statut_slect.setText(getCurrentView().getTitle());
	    	}

	    }
	    public void ajouterVue(Vue v){
	    	vues.add(v);
	    }


}
