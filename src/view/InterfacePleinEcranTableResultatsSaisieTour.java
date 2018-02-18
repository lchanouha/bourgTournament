package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import model.Tournoi;

public class InterfacePleinEcranTableResultatsSaisieTour {
	
	private static final int matchs_effectues_colonne_table          = 0;
	private static final int matchs_effectues_colonne_numero_equipe1 = 1;
	private static final int matchs_effectues_colonne_score_equipe1  = 2;
	private static final int matchs_effectues_colonne_numero_equipe2 = 3;
	private static final int matchs_effectues_colonne_score_equipe2  = 4;

	public static JTable constructTable(Tournoi t, Statement st){
		try {
			ResultSet rs = st.executeQuery("SELECT num_match_t, equipe1, equipe2,score1,score2 FROM matchs WHERE id_tournoi="+ t.getId() + " AND timestamp is not null AND num_tour="+ t.getNbTours() +" ORDER BY timestamp DESC " );
			Vector< Vector<Object>> to =new Vector<Vector<Object>>();
			Vector<Object> v;
			while(rs.next()){
				v = new Vector<Object>();
				v.add(rs.getInt("num_match_t"));
				v.add(rs.getInt("equipe1"));
				v.add(rs.getInt("score1"));
				v.add(rs.getInt("equipe2"));
				v.add(rs.getInt("score2"));
				to.add(v);
			}
			rs.close();
			Vector<String> columnNames = new Vector<String>();
			columnNames.add("Numéro de table");
			columnNames.add("Equipe 1");
			columnNames.add("Score 1");
			columnNames.add("Equipe 2");
			columnNames.add("Score 2");
			JTable last_updates = new JTable(to, columnNames);
			last_updates.setFont(new Font("Serif", Font.BOLD, 22));
			last_updates.setRowHeight(last_updates.getRowHeight()+10);
			
			
			TableCellRenderer renderer = new TableCellRenderer() {

				JLabel label = new JLabel();
				
		        @Override
		        public Component getTableCellRendererComponent(JTable table,
		                Object value, boolean isSelected, boolean hasFocus,
		                int row, int column) {
		            label.setOpaque(true);
		            label.setText("" + value);
		            label.setHorizontalAlignment(JLabel.CENTER);
		            TableModel model = table.getModel();
		            label.setBackground(Color.WHITE);
		            label.setForeground(Color.BLACK);

		            switch (column){
		            	case matchs_effectues_colonne_score_equipe1:
		            		int score1_1 = (int) value;
		            		int score2_1 = (int) model.getValueAt(row, matchs_effectues_colonne_score_equipe2);
		            		label.setBackground(score1_1 > score2_1 ? Color.RED: Color.CYAN);
		            		if(score1_1 > score2_1){
		            			label.setForeground(Color.WHITE);
		            		}
		            		break;
		            	case matchs_effectues_colonne_score_equipe2:
		            		int score2_2 = (int) value;
		            		int score1_2 = (int) model.getValueAt(row, matchs_effectues_colonne_score_equipe1);
		            		label.setBackground(score2_2 > score1_2 ? Color.RED: Color.CYAN);
		            		if(score2_2 > score1_2){
		            			label.setForeground(Color.WHITE);
		            		}
		            		break;
			            	
		            }
		            
		            label.setBorder(BorderFactory.createCompoundBorder());
		            return label;
		        }

		    };
		    last_updates.setDefaultRenderer(Object.class, renderer);
		    return last_updates;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
}
