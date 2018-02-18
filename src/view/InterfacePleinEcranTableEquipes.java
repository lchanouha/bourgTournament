package view;

import java.awt.Font;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JTable;

import model.Tournoi;

public class InterfacePleinEcranTableEquipes {
	public static JTable constructTable(Tournoi t, Statement st){
		try {
			ResultSet rs = st.executeQuery("Select * FROM equipes WHERE id_tournoi="+t.getId() + " ORDER BY num_equipe ASC");
			Vector<Object> v;
			Vector< Vector<Object>> to =new Vector<Vector<Object>>();
			while(rs.next()){
				v =new Vector<Object>();
				v.add(rs.getInt("num_equipe"));
				v.add(rs.getString("nom_j1"));
				to.add(v);
			}
			rs.close();
			Vector<String> columnNames = new Vector<String>();
			columnNames.add("Numéro d'équipe");
			columnNames.add("Nom d'équipe");
			JTable table = new JTable(to,columnNames);
			table.setFont(new Font("Serif", Font.BOLD, 22));
			table.setRowHeight(40);
			//table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF );
			table.getColumnModel().getColumn(0).setWidth(70);
			table.getColumnModel().getColumn(1).setPreferredWidth(470);
			return table;
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
}
