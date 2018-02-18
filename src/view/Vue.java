package view;

import controller.ApplicationController;
import model.Tournoi;
import java.sql.Statement;

import javax.swing.JPanel;  

public abstract class Vue {
	protected boolean estInitialise = false;
	protected ApplicationController ac;
	protected JPanel        conteneurPrimaire;
	protected Vue(){
		
	}

	public void afficher() {
		if (!estInitialise){
			estInitialise = true;
			initialiserStructure();
			ac.getFenetre().enregistrerConteneurVue(getID(), conteneurPrimaire);
		}
		initialiserDonnees();
		ac.getFenetre().dessinerVue(getID());
		
	}
	protected abstract void initialiserDonnees();
	protected abstract void initialiserStructure();
	protected abstract String getID();
	
	public boolean isVisible(){
		return false;
	}
	public boolean hasButton(){
		return false;
	}
	public String getButtonText(){
		return "";
	}
	public int getButtonOrder(){
		return -1;
	}
	public boolean buttonIsEnabled(){
		return false;
	}
	
	protected Tournoi getTournoi(){
		return ac.getTournoi();
	}
	protected Statement getSQLConnector(){
		return ac.getSQLConnector();
	}
	
	public abstract String getTitle();
	
	//public abstract Component getConteneur();

	//public abstract String getTitle();
}
