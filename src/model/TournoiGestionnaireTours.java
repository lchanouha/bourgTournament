package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class TournoiGestionnaireTours {
	private Tournoi tournoi;
	private Statement st;
	
	public TournoiGestionnaireTours(Statement s, Tournoi t){
		this.tournoi = t;
		this.st = s;
	}
	
    /* *****************************************************
     * Interface publique : ajouter ou supprimer un tour ***
     ******************************************************/

		public boolean ajouterTour(){
			// Recherche du nombre de tours actuel
			
			int nbtoursav = TournoiMatch.getMaxRegisteredTour(st, tournoi);
			if(nbtoursav >=  (tournoi.getNbEquipes() -1) ) return false;
			System.out.println("Nombre d'équipes:" + tournoi.getNbEquipes() + "  tours" + nbtoursav);
			System.out.println("Nombre de tours avant:" + nbtoursav);
	
			if(nbtoursav == 0){
				// Premier tour, aucun match n'a été effectué
				ajouterPremierTour();
	
			}else{
				ajouterTourN(nbtoursav);
			}
			return true;
		}
		
		public void supprimerTour(){
			int nbtoursav;
			try {
				ResultSet rs = st.executeQuery("SELECT MAX (num_tour)  FROM matchs WHERE id_tournoi="+tournoi.getId()+"; ");
				rs.next();
				nbtoursav = rs.getInt(1);
				rs.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				return ;
			}
			//if(tour != nbtoursav) return ;
			
			try {
				st.executeUpdate("DELETE FROM matchs WHERE id_tournoi="+ tournoi.getId()+" AND num_tour=" + nbtoursav);
			} catch (SQLException e) {
				System.out.println("Erreur del tour : " + e.getMessage());
			}
		}
		
    /* *****************************************************************************
     * Interface privée: implémentation de l'algorithme de génération des matchs ***
     ******************************************************************************/		
		
			private void ajouterPremierTour(){
				String req = "INSERT INTO matchs ( id_match, id_tournoi, num_tour, equipe1, equipe2, termine,num_match_t ) VALUES\n";
	
				Vector<Integer> alea = new Vector<Integer>();
				for(int i=0;i< tournoi.getNbEquipes();i++){
					alea.add(i+1);
				}
				int eqslect1;
				int eqslect2;
				int v_eqslect1;
				int v_eqslect2;
				char v = ' ';
				int nbt=1;
				Random rand = new Random(); 
				while(alea.size() > 0){
					eqslect1 = (int) (rand.nextInt(alea.size()));
					v_eqslect1=alea.get(eqslect1);
					alea.remove(eqslect1);
	
					eqslect2 = (int) (rand.nextInt(alea.size()));
					v_eqslect2=alea.get(eqslect2);
					alea.remove(eqslect2);
	
					req += v + "(NULL," + tournoi.getId() + ", " + (1) + ", "+  v_eqslect1 + ", " +  v_eqslect2 + ", 'non',"+nbt+")";
					v = ',';
					nbt++;
				}
				try{
					st.executeUpdate(req);
					
				}catch(SQLException e){
					System.out.println("Erreur validation équipes : " + e.getMessage());
				}		
				tournoi.setStatut(Tournoi.STATUS_SAISIE_MATCHS);
			}
			private void ajouterTourN(int nbtoursav){
			try {
				ResultSet rs;
				//rs = st.executeQuery("SELECT equipe, (SELECT count(*) FROM matchs m WHERE (m.equipe1 = equipe AND m.score1 > m.score2 AND m.id_tournoi = id_tournoi) OR (m.equipe2 = equipe AND m.score2 > m.score1 AND m.id_tournoi = id_tournoi )) as matchs_gagnes FROM  (select equipe1 as equipe,score1 as score from matchs where id_tournoi=" + this.id_tournoi + " UNION select equipe2 as equipe,score2 as score from matchs where id_tournoi=" + this.id_tournoi + ") GROUP BY equipe ORDER BY matchs_gagnes DESC;");

				//rs = st.executeQuery("SELECT equipe, (SELECT count(*) FROM matchs m WHERE (m.equipe1 = equipe AND m.score1 > m.score2  AND m.id_tournoi = "+id_tournoi+") OR (m.equipe2 = equipe AND m.score2 > m.score1 AND m.id_tournoi="+id_tournoi+" )) as matchs_gagnes FROM  (select equipe1 as equipe,score1 as score from matchs where id_tournoi=" + this.id_tournoi + " UNION select equipe2 as equipe,score2 as score from matchs where id_tournoi=" + this.id_tournoi + ") GROUP BY equipe  ORDER BY matchs_gagnes DESC,score DESC;");
				rs =   st.executeQuery(
						  "SELECT equipe, SUM(score) as score, "
								  +	"(SELECT count(*) FROM matchs m WHERE (m.equipe1 = equipe AND m.score1 > m.score2  AND m.id_tournoi = " + tournoi.getId() +") OR (m.equipe2 = equipe AND m.score2 > m.score1 )) as matchs_gagnes, "
								  + "(SELECT COUNT(*) FROM matchs m WHERE (m.equipe1 = equipe OR m.equipe2=equipe) AND m.id_tournoi=" + tournoi.getId() +") as matchs_joues "
						+ "FROM  	(SELECT equipe1 as equipe,score1 as score from matchs where id_tournoi=" + tournoi.getId() + " "
								+ "UNION ALL "
								+ 	 "SELECT equipe2 as equipe,score2 as score from matchs where id_tournoi=" + tournoi.getId() + ") "
						+ "GROUP BY equipe "
						+ "ORDER BY matchs_gagnes DESC,score DESC;");

				
				ArrayList<Integer> ordreeq= new ArrayList<Integer>();
				while(rs.next()){
					ordreeq.add(rs.getInt("equipe"));
					System.out.println(rs.getInt(1) +" _ " + rs.getString(2));
				}
				rs.close();
				System.out.println("Taille"+ordreeq.size());
				int i;
				boolean fini;
				String req = "INSERT INTO matchs ( id_match, id_tournoi, num_match_t,num_tour, equipe1, equipe2, termine ) VALUES\n";
				char v = ' ';
				int num_mt=1;
				while(ordreeq.size() > 1){
					System.out.println("Taille " + ordreeq.size());
					int j=0;
					while(j<ordreeq.size()) {
						System.out.println(ordreeq.get(j));
						j++;
					}
					i=1;
					do{
						rs = st.executeQuery("SELECT COUNT(*) FROM matchs m WHERE ( (m.equipe1 = " + ordreeq.get(0) + " AND m.equipe2 = " + ordreeq.get(i) + ") OR (m.equipe2 = " + ordreeq.get(0) + " AND m.equipe1 = " + ordreeq.get(i) + ")  )");  
						rs.next();
						if(rs.getInt(1) > 0 && i< ordreeq.size()-1){
							// Le match est d�j� jou�
							i++;
							fini = false;

						}else{ 
							fini = true;
							req += v + "(NULL," + tournoi.getId() + ", "+num_mt+", " + (nbtoursav + 1) + ", "+  ordreeq.get(0) + ", " +  ordreeq.get(i) + ", 'non')";
							//System.out.println(ordreeq.get(0) + ", " +  ordreeq.get(i));
							ordreeq.remove(0);
							ordreeq.remove(i-1);
							v = ',';
							num_mt++;
						}
						rs.close();
					}while(!fini);
				}
				System.out.println(req);
				st.executeUpdate(req);
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		}
			
	// Premier algorithme: n'est plus utilisé (ne prenait pas en compte les matchs joués, c'était un algorithme statique)
	/*
	private static class Match{
		public int eq1,eq2;
		public Match(int e1,int e2){
			eq1 = e1;
			eq2 = e2;
		}
		public String toString(){
			if(eq1 < eq2){
				return "  " + eq1 + " contre " + eq2;
			}else{
				return "  " + eq2 + " contre " + eq1;
			}
		}
	}
	public void genererMatchs(){
		int nbt = 1;

		System.out.println("Nombre d'équipes : " + tournoi.getNbEquipes());
		System.out.println("Nombre de tours  : " + nbt);
		//String req = "INSERT INTO matchs ( id_match, id_tournoi, num_tour, equipe1, equipe2, termine ) VALUES\n";
		Vector<Vector<Match>> ms;
		ms = TournoiGestionMatchs.getMatchsToDo(tournoi.getNbEquipes(), nbt);
		//int z = 1;
		//char v = ' ';
		for(Vector<Match> t :ms){
			for(Match m:t){
				MatchM.createNew(st, tournoi , m.eq1, m.eq2, 1);
				//req += v + "(NULL," + idTournoi + ", " + z + ", "+  m.eq1 + ", " +  m.eq2 + ", 'non')";
				//v = ',';
			}
			req += "\n";
			z++;
		}
		System.out.println(req);
		try{
			st.executeUpdate(req);
			this.setStatut(STATUS_SAISIE_MATCHS);
		}catch(SQLException e){
			System.out.println("Erreur validation �quipes : " + e.getMessage());
		}
	}
	*/
	/*
		public static Vector<Vector<Match>> getMatchsToDo(int nbJoueurs, int nbTours){
			if( nbTours  >= nbJoueurs){
				System.out.println("Erreur tours < equipes");
				return null;
			}
			
			int[]   tabJoueurs;
			if((nbJoueurs % 2) == 1){
				// Nombre impair de joueurs, on rajoute une équipe fictive
				tabJoueurs   = new int[nbJoueurs+1];
				tabJoueurs[nbJoueurs] = -1;
				for(int z = 0; z < nbJoueurs;z++){
					tabJoueurs[z] = z+1;
				}
				nbJoueurs++;
			}else{
				tabJoueurs   = new int[nbJoueurs];
				for(int z = 0; z < nbJoueurs;z++){
					tabJoueurs[z] = z+1;
				}
			}
			
			boolean quitter;
			int     i, increment  = 1, temp;

			Vector<Vector<Match>> retour = new Vector<Vector<Match>>();
			
			Vector<Match> vm;
			
			for( int r = 1; r <= nbTours;r++){
				if(r > 1){
					temp = tabJoueurs[nbJoueurs - 2];
					for(i = (nbJoueurs - 2) ; i > 0; i--){
						tabJoueurs[i] = tabJoueurs[i-1];
					}
					tabJoueurs[0] = temp;
				}
				i       = 0;
				quitter = false;
				vm = new Vector<Match>();
				while(!quitter){
					if (tabJoueurs[i] == -1 || tabJoueurs[nbJoueurs - 1  - i] == -1){
						// Nombre impair de joueur, le joueur n'a pas d'adversaire
					}else{
						vm.add(new Match(tabJoueurs[i], tabJoueurs[nbJoueurs - 1  - i]));
					}

			        i+= increment;
					if(i >= nbJoueurs / 2){
						if(increment == 1){
							quitter = true;
							break;
						}else{
							increment = -2;
							if( i > nbJoueurs / 2){
								i = ((i > nbJoueurs / 2) ? i - 3 : --i) ;
							}
							if ((i < 1) && (increment == -2)){
								quitter = true;
								break;
							}
						}
					}
				}
				retour.add(vm);
			}
			return retour;
		}  */



		
}
