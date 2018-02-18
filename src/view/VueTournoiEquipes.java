package view;


import java.awt.Color;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import controller.ApplicationController;
import model.Equipe;
import model.Tournoi;

public class VueTournoiEquipes extends Vue {
	
	private AbstractTableModel eq_modele;
    private JButton            eq_ajouter;
    private JButton            eq_supprimer;
    private JButton            eq_valider;
    private JScrollPane        eq_js;
    JTable                     eq_jt;
    LayoutManager                  eq_layout;
    //JLabel                     eq_desc;

	//JPanel p;

	public static String ID = "Equipes";
	
	public VueTournoiEquipes(ApplicationController ac) {
		this.ac = ac;
	}
	@Override
	public String getID() {
		return ID;
	}
	@Override
	protected void initialiserDonnees () {
		Tournoi t = ac.getTournoi();
		t.majEquipes();
		if(t.getNbEquipes() > 0){
			eq_jt.getSelectionModel().setSelectionInterval(0, 0);
		}
		
		eq_modele.fireTableDataChanged();
		if(t.isManuel()){
			System.out.println("manuel");
			eq_ajouter.setEnabled(true);
			eq_supprimer.setEnabled(true);
			eq_valider.setEnabled(false);	
		}else{
			if(t.getStatut() != 0){
				System.out.println(t.getStatut());
				eq_ajouter.setEnabled(false);
				eq_supprimer.setEnabled(false);
				eq_valider.setEnabled(t.getStatut() == 1);
			}else{
				System.out.println("t=0");
				eq_ajouter.setEnabled(true);
				eq_supprimer.setEnabled(true);	
				eq_valider.setEnabled(VueTournoiEquipes.this.ac.getTournoi().getNbEquipes() > 0
						&& VueTournoiEquipes.this.ac.getTournoi().getNbEquipes() % 2 == 0) ;
			}
		}
	}
	
	protected void initialiserStructure(){

		conteneurPrimaire      = new JPanel();
		eq_layout = new BoxLayout(conteneurPrimaire, BoxLayout.Y_AXIS);
		conteneurPrimaire.setLayout(eq_layout);
		conteneurPrimaire.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

		eq_modele = new AbstractTableModel() {
			

			private static final long serialVersionUID = 1L;

			
			public Object getValueAt(int arg0, int arg1) {
				Object r=null;
				switch(arg1){
				case 0:
					r = VueTournoiEquipes.this.ac.getTournoi().getEquipe(arg0).num;
				break;
				case 1:
					r= VueTournoiEquipes.this.ac.getTournoi().getEquipe(arg0).eq1;
				break;

				}
				return r;
	
			}
			public String getColumnName(int col) {
			        if(col == 0){
			        	return "N°";
			        }else if(col == 1){
			        	return "Nom d'équipe";
			        }else{
			        	return "??";
			        }
			 }
			public int getRowCount() {
				if(VueTournoiEquipes.this.ac.getTournoi() == null)
					return 0;
				return VueTournoiEquipes.this.ac.getTournoi().getNbEquipes();
			}
			
			public int getColumnCount() {
				return 2;
			}
			public boolean isCellEditable(int x, int y){
				//if(t.getStatut() != 0 && !t.isManuel()) return false;
				return y > 0;
			}
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				Equipe e = VueTournoiEquipes.this.ac.getTournoi().getEquipe(rowIndex);
				if( columnIndex == 0){
					
				}else if( columnIndex == 1){
					e.eq1 = (String)aValue;
				}
				VueTournoiEquipes.this.ac.getTournoi().equipeUpdate(rowIndex);
				fireTableDataChanged();
				//todo
				VueTournoiEquipes.this.ac.notifyDataUpdate();
			}
		};
		eq_jt = new JTable(eq_modele);
		
		TableCellRenderer renderer = new TableCellRenderer() {

	       
	        @Override
	        public Component getTableCellRendererComponent(JTable table,
	                Object value, boolean isSelected, boolean hasFocus,
	                int row, int column) {
	        	 JLabel label = new JLabel();
	        	 label.setOpaque(true);
		           label.setText("" + value);
	            if (isSelected || hasFocus){
	            	label.setBackground(Color.CYAN);
	            	label.setForeground(Color.RED);
	            	label.setText(value.toString());
	            	return label;
	            }
	        	label.setBackground(Color.BLACK);
	            Color alternate = UIManager.getColor("Table.alternateRowColor");
	            label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
	            label.setBackground(Color.BLACK);
	            // Quadrillage lignes
	            
	            
	            if(row%2 == 0)
            		label.setBackground(Color.WHITE);
            	else 
            		label.setBackground(alternate);
	            

	            
	            label.setFont (label.getFont ().deriveFont (20.0f));
	            label.setForeground(Color.BLACK);
	            switch (column){
	            	case 0:
	            		label.setHorizontalAlignment(SwingConstants.CENTER);
	            		//if(match.num_tour % 2 == 0){
	            		//	label.setBackground(new Color(107, 106, 104));
	            		//	label.setForeground(Color.WHITE);
	            		//}
	            		break;
	            	case 1:
	            		
	            		label.setHorizontalAlignment(SwingConstants.LEFT);
	            		
	    
	            }
	            //label.setBackground(Color.BLACK);
	            return label;
	        }

	    };

		eq_jt.setRowHeight(40);
		//eq_jt.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		eq_jt.getColumnModel().getColumn(0).setWidth(70);
		eq_jt.getColumnModel().getColumn(1).setPreferredWidth(470);
		eq_jt.setDefaultRenderer(Object.class, renderer);
		eq_jt.setSelectionBackground(Color.RED);
		eq_js = new JScrollPane(eq_jt);
		conteneurPrimaire.add(eq_js);

		JPanel bt    = new JPanel();
		eq_ajouter   = new JButton("Ajouter une équipe");
		eq_supprimer = new JButton("Supprimer une équipe");
		eq_valider   = new JButton("Valider les équipes");
		
		eq_ajouter.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				VueTournoiEquipes.this.ac.getTournoi().equipeNew();
				initialiserDonnees();
				if(VueTournoiEquipes.this.ac.getTournoi().getNbEquipes() > 0){
					eq_jt.getSelectionModel().setSelectionInterval(0, 0);
					eq_jt.editCellAt(VueTournoiEquipes.this.ac.getTournoi().getNbEquipes() - 1, 1);
					eq_jt.getSelectionModel().setSelectionInterval(VueTournoiEquipes.this.ac.getTournoi().getNbEquipes() - 1, VueTournoiEquipes.this.ac.getTournoi().getNbEquipes() - 1);
					eq_jt.setSurrendersFocusOnKeystroke(true);
					Component editor = eq_jt.getEditorComponent();
					editor.requestFocus();

				}
				
				VueTournoiEquipes.this.ac.notifyDataUpdate();
				
				
			}
		});
		eq_supprimer.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if(VueTournoiEquipes.this.eq_jt.getSelectedRow() != -1){
					VueTournoiEquipes.this.ac.getTournoi().supprimerEquipe(VueTournoiEquipes.this.ac.getTournoi().getEquipe(VueTournoiEquipes.this.eq_jt.getSelectedRow()).id);
				}
				initialiserDonnees();
				if(VueTournoiEquipes.this.ac.getTournoi().getNbEquipes() > 0){
					eq_jt.getSelectionModel().setSelectionInterval(0, 0);
				}
				
				VueTournoiEquipes.this.ac.notifyDataUpdate();
			}
		});
		eq_valider.addActionListener(new ActionListener() {
			
			
			public void actionPerformed(ActionEvent e) {
				//t.genererMatchs();
				VueTournoiEquipes.this.ac.getTournoi().getGestionnaireTours().ajouterTour();
				VueTournoiEquipes.this.ac.afficherVue(VueTournoiSaisieMatchs.ID);
				VueTournoiEquipes.this.ac.notifyDataUpdate();
			}
			
		});

		bt.add(eq_ajouter);
		bt.add(eq_supprimer);
		bt.add(eq_valider);
		conteneurPrimaire.add(bt);
		conteneurPrimaire.add(new JLabel("Dans le cas de nombre d'équipes impair, créer une équipe virtuelle"));
		conteneurPrimaire.add(new JLabel("Il n'y a pas de validation d'équipes en mode manuel"));
	}
	public String getTitle() {
		return "Gestion des équipes";
	}
	@Override
	public boolean hasButton(){
		return true;
	}
	@Override
	public String getButtonText(){
		return "Équipes";
	}
	@Override
	public int getButtonOrder(){
		return 30; 
	}
	@Override
	public boolean buttonIsEnabled(){
		return this.ac.getTournoi() != null;
	}
}

