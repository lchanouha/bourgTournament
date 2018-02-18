package view;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

import controller.ApplicationController;
import model.TournoiMatch;
import model.Tournoi;

public class VueTournoiSaisieMatchsTableRenderer implements TableCellRenderer {
	private Tournoi t;
	
	public VueTournoiSaisieMatchsTableRenderer (ApplicationController ac){
		this.t = ac.getTournoi();
	}

    JLabel label = new JLabel();

    @Override
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
    	TournoiMatch match = t.getMatch(row); 
    	
    	// Style générique
        label.setOpaque(true);
        label.setText("" + value);
        
        label.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        
        Color alternate = UIManager.getColor("Table.alternateRowColor");
        

        // Quadrillage lignes
        if(row%2 == 0)
    		label.setBackground(Color.WHITE);
    	else 
    		label.setBackground(alternate);
        
        label.setFont (label.getFont ().deriveFont (24.0f));
        label.setForeground(Color.BLACK);
        switch (column){
        	case VueTournoiSaisieMatchs.colonneTour:
        		label.setHorizontalAlignment(SwingConstants.CENTER);
        		if(match.getNum_tour() % 2 == 0){
        			label.setBackground(new Color(107, 106, 104));
        			label.setForeground(Color.WHITE);
        		}
        		break;
        	case VueTournoiSaisieMatchs.colonneTable:
        		label.setHorizontalAlignment(SwingConstants.CENTER);
        		break;
            case VueTournoiSaisieMatchs.colonneNumEquipe1:
            case VueTournoiSaisieMatchs.colonneNumEquipe2:
            	label.setHorizontalAlignment(SwingConstants.CENTER);
	            if(match.estSaisi()) {
	            	label.setBackground(Color.GREEN);
	            } else {
	                label.setBackground(Color.ORANGE);
	            }		            	
	            break;
            case VueTournoiSaisieMatchs.colonneScoreEquipe1:
            	label.setHorizontalAlignment(SwingConstants.RIGHT);
            	if (match.estSaisi()) {
            		label.setBackground(match.equipe1Gagne() ? Color.RED: Color.CYAN);
            	}
            	break;
            case VueTournoiSaisieMatchs.colonneScoreEquipe2:
            	label.setHorizontalAlignment(SwingConstants.RIGHT);
            	if (match.estSaisi()) {
            		label.setBackground(match.equipe1Gagne() ? Color.CYAN: Color.RED);
            	}
        }
        if (isSelected ){
        	label.setBackground(Color.GRAY);
        	label.setForeground(Color.WHITE);
        }
        if(hasFocus){
        	label.setForeground(Color.GREEN);
        }
        
        return label;
    }

}
