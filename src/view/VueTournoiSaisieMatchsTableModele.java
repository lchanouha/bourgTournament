package view;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import controller.ApplicationController;
import model.TournoiMatch;
import model.Tournoi;

public class VueTournoiSaisieMatchsTableModele extends AbstractTableModel {
	private Tournoi t;
	private ApplicationController ac;
	private VueTournoiSaisieMatchs v;
	
	public VueTournoiSaisieMatchsTableModele (ApplicationController ac, VueTournoiSaisieMatchs v){
		this.t = ac.getTournoi();
		this.ac = ac;
		this.v = v;
		/*
		addTableModelListener(new TableModelListener() {
			
			@Override
			 public void tableChanged(TableModelEvent e) {
		        if (e.getType() == TableModelEvent.UPDATE) {
		            final int row = e.getFirstRow();
		            final int column = e.getColumn();
		            System.out.println("row:" + row + ", col:" + column);
		            
		        }
		    }
		});*/

	}
	private static final long serialVersionUID = 1L;
	public Object getValueAt(int arg0, int arg1) {
		Object r=null;
		switch(arg1){
		case VueTournoiSaisieMatchs.colonneTour:
			r= t.getMatch(arg0).getNum_tour();
		break;
		case VueTournoiSaisieMatchs.colonneTable:
			r= t.getMatch(arg0).getNum_mt();
		break;
		case VueTournoiSaisieMatchs.colonneNumEquipe1:
			r= t.getMatch(arg0).getEq1();
		break;
		case VueTournoiSaisieMatchs.colonneScoreEquipe1:
			r= t.getMatch(arg0).getScore1();
		break;
		case VueTournoiSaisieMatchs.colonneNumEquipe2:
			r= t.getMatch(arg0).getEq2();
		break;
		case VueTournoiSaisieMatchs.colonneScoreEquipe2:
			r= t.getMatch(arg0).getScore2();
		break;
		}
		return r;

	}
    
	public String getColumnName(int col) {
		switch (col){
			case VueTournoiSaisieMatchs.colonneTour:
				return "Tour";
			case VueTournoiSaisieMatchs.colonneTable:
				return "Table";
			case VueTournoiSaisieMatchs.colonneNumEquipe1:
				return "Équipe 1";
			case VueTournoiSaisieMatchs.colonneScoreEquipe1:
				return "Score équipe 1";
			case VueTournoiSaisieMatchs.colonneNumEquipe2:
				return "Équipe 2";
			case VueTournoiSaisieMatchs.colonneScoreEquipe2:
				return "Score équipe 2";
			default:
				return "???";
		}
	 }
	public int getRowCount() {
		if(t == null) return 0;
		
		return t.getNbMatchs();
	}
	
	public int getColumnCount() {
		return 6;
	}
	public boolean isCellEditable(int x, int y){
		return t.isManuel() || ((y == 3 || y == 5) && t.getMatch(x).getNum_tour() == t.getNbTours());
	}
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		TournoiMatch m = t.getMatch(rowIndex);
		if( columnIndex == VueTournoiSaisieMatchs.colonneTour){
			// Tour
			int tour= Integer.parseInt((String)aValue);
			if(tour > 0){
				m.setNum_tour(tour, ac.getSQLConnector());
			}
		}else if(columnIndex == VueTournoiSaisieMatchs.colonneTable){
			// Numéro de table
			
			try{
				int sco = Integer.parseInt((String)aValue);
				m.setNum_mt(sco, ac.getSQLConnector());
			}catch(Exception e){
				return ;
			}
			
		}else if( columnIndex == VueTournoiSaisieMatchs.colonneNumEquipe1){
			try{
				int sco = Integer.parseInt((String)aValue);
				if(sco > 0 && sco <= t.getNbEquipes()){
					m.setEq1(sco, ac.getSQLConnector());
				}
				
			}catch(Exception e){
				return ;
			}
			
		}else if( columnIndex == VueTournoiSaisieMatchs.colonneNumEquipe2){
			try{
				int sco = Integer.parseInt((String)aValue);
				if(sco > 0 && sco <= t.getNbEquipes()){
					m.setEq2(sco, ac.getSQLConnector());
				}

				
			}catch(Exception e){
				return ;
			}
			
		}else if( columnIndex == VueTournoiSaisieMatchs.colonneScoreEquipe1){
			try{
				int sco = Integer.parseInt((String)aValue);
				if(sco >= 0){
					m.setScore1(sco, ac.getSQLConnector());
				}
				//this.jt.getSelectionModel().setSelectionInterval(0, 0);
				/*this.jt.editCellAt(rowIndex, VueTournoiSaisieMatchs.colonneScoreEquipe2);
				this.jt.setSurrendersFocusOnKeystroke(true);
				this.jt.getSelectionModel().setSelectionInterval(rowIndex,rowIndex);
				this.jt.changeSelection(rowIndex, VueTournoiSaisieMatchs.colonneScoreEquipe2, false, false);*/
				
			}catch(Exception e){
				return ;
			}
			
		}else if( columnIndex == VueTournoiSaisieMatchs.colonneScoreEquipe2){
			try{
				int sco = Integer.parseInt((String)aValue);
				if(sco >= 0){
					m.setScore2(sco, ac.getSQLConnector());
				}

			}catch(Exception e){
				return ;
			}
		}

		fireTableDataChanged();
		v.majStatutM();
		ac.notifyDataUpdate();
	}
}
