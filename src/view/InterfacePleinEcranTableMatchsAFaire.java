package view;

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

import model.Tournoi;

public class InterfacePleinEcranTableMatchsAFaire {
	
	private static final int matchs_a_faire_colonne_table          = 0;
	private static final int matchs_a_faire_colonne_numero_equipe1 = 1;
	private static final int matchs_a_faire_colonne_numero_equipe2 = 2;	

	public static JTable constructTable(Tournoi t, Statement st){

		JTable a_faire;
		ResultSet rs;
		Vector< Vector<Object>> to =new Vector<Vector<Object>>();
		Vector<Object> v;
		try {
			rs = st.executeQuery("SELECT * FROM matchs m WHERE id_tournoi=" + t.getId() + " AND (score1=0 OR score1 is null) AND (score2 = 0 OR score2 is null) ORDER BY num_match_t ASC;");
			while(rs.next()){
				v = new Vector<Object>();
				v.add(rs.getInt("num_match_t"));
				v.add(rs.getInt("equipe1"));
				v.add(rs.getInt("equipe2"));
				to.add(v);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Vector<String> columnNames = new Vector<String>();
		columnNames.add("Numéro de table");
		columnNames.add("Équipe 1");
		columnNames.add("Équipe 2");
		a_faire = new JTable(to, columnNames);
		
		a_faire.setRowHeight(a_faire.getRowHeight()+10);
		
		TableCellRenderer renderer = new TableCellRenderer() {

			JLabel label = new JLabel();
			
	        @Override
	        public Component getTableCellRendererComponent(JTable table,
	                Object value, boolean isSelected, boolean hasFocus,
	                int row, int column) {
	            //label.setOpaque(true);
	        	label.setHorizontalAlignment(JLabel.CENTER);
	            label.setText("" + value);
	           // label.setAlignmentX(RIGHT_ALIGNMENT);
	            label.setFont(new Font("Serif", Font.BOLD, 22));
	            label.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
	            return label;
	        }

	    };
	    a_faire.setDefaultRenderer(Object.class, renderer);
	    return a_faire;
	}
	
}
