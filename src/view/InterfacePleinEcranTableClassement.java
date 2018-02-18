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
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

import ext.TableColumnAdjuster;
import model.Tournoi;

public class InterfacePleinEcranTableClassement {
	
	
	private static final String text_nonsaisi = "Non saisi";
	private static final String text_saisi = "Saisi";
	
	
	private static final int classement_colonne_rang          = 0;
	private static final int classement_colonne_numero_equipe = 1;
	private static final int classement_colonne_nom_equipe    = 2;
	private static final int classement_colonne_score         = 3;
	private static final int classement_colonne_matchs_gagnes = 4;
	private static final int classement_colonne_statut_saisie = 5;
	
	public static JTable constructTable(Tournoi t, Statement st){
		try {
			final Vector< Vector<Object>> to =new Vector<Vector<Object>>();
			Vector<Object> v;
			int i=1;
			ResultSet rs = st.executeQuery("SELECT equipe,(SELECT nom_j1 FROM equipes e WHERE e.num_equipe = equipe AND e.id_tournoi = " + t.getId() + ") as joueur1, SUM(score) as score, (SELECT count(*) FROM matchs m WHERE (m.equipe1 = equipe AND m.score1 > m.score2  AND m.id_tournoi = " + t.getId() +") OR (m.equipe2 = equipe AND m.score2 > m.score1 )) as matchs_gagnes, (SELECT COUNT(*) FROM matchs m WHERE (m.equipe1 = equipe OR m.equipe2=equipe) AND m.id_tournoi=" + t.getId() +") as matchs_joues, (SELECT COUNT(*) FROM matchs m WHERE m.id_tournoi="+t.getId()+" AND (m.equipe1 = equipe OR m.equipe2=equipe) AND ( (m.score1=0 OR m.score1 is null)  AND (m.score2=0 OR m.score2 is null)) ) as match_att FROM  (select equipe1 as equipe,score1 as score from matchs where id_tournoi=" + t.getId() + " UNION ALL select equipe2 as equipe,score2 as score from matchs where id_tournoi=" + t.getId() + ") GROUP BY equipe ORDER BY matchs_gagnes DESC,score DESC, equipe ASC;");
			while(rs.next()){
				v = new Vector<Object>();
				v.add(i++);
				v.add(rs.getInt("equipe"));
				v.add(rs.getString("joueur1"));
				v.add(rs.getInt("score") + "  ");
				v.add(rs.getInt("matchs_gagnes"));
				v.add(rs.getInt("match_att")== 0 ? text_saisi:text_nonsaisi);
				to.add(v);
				
			}
			rs.close();
			Vector<String> columnNames = new Vector<String>();
			columnNames.add("Rang");
			columnNames.add("N° équipe");
			columnNames.add("Nom d'équipe");
			columnNames.add("Score total");
			columnNames.add("Matchs gagnés");
			columnNames.add("État saisie");
			
			TableCellRenderer renderer = new TableCellRenderer() {

				JLabel label = new JLabel();
				
		        @Override
		        public Component getTableCellRendererComponent(JTable table,
		                Object value, boolean isSelected, boolean hasFocus,
		                int row, int column) {
		            label.setOpaque(true);
		            label.setText("" + value);
		           // label.setAlignmentX(RIGHT_ALIGNMENT);

		            Color alternate = UIManager.getColor("Table.alternateRowColor");
		            label.setHorizontalAlignment(JLabel.CENTER);
		            switch (column){
		            	case classement_colonne_score:
		            		label.setHorizontalAlignment(JLabel.RIGHT);
		            	break;
			            case classement_colonne_statut_saisie:
			            	label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
			            	label.setHorizontalAlignment(JLabel.CENTER);
				            if(((String)to.get(row).get(5)).equals(text_nonsaisi) ){
				            	label.setBackground(Color.ORANGE);
				            } else {
				                label.setBackground(Color.GREEN);
				            }
			            break;
			            default:
			            	if(row%2 == 0)
			            		label.setBackground(Color.WHITE);
			            	else label.setBackground(alternate);
			            	
		            }

		            label.setBorder(BorderFactory.createCompoundBorder());
		            return label;
		        }

		    };

			
			
			
			JTable resultats_jt = new JTable(to,columnNames );
			resultats_jt.setDefaultRenderer(Object.class, renderer);
			//resultats_jt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			TableColumnAdjuster tca = new TableColumnAdjuster(resultats_jt);
			
			resultats_jt.setFont(new Font("Serif", Font.BOLD, 22));
			resultats_jt.setRowHeight(resultats_jt.getRowHeight()+10);
			tca.adjustColumns();
			return resultats_jt;
			
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
}
