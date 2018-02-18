package main;
  
import controller.ApplicationController;
import model.Database;


public class bourgTournament {


	public static void main(String[] args) {
		
		// Connection à la base de données
		// et création de la structure si nécessaire
  
			Database db = new Database();
			db.initialize();
			
		// Démarrage de l'application (via le Controller principal)

			ApplicationController ac = new ApplicationController(db);
			ac.startApplication();
		
	}
}


