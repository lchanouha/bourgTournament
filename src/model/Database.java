package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class Database {
    public Connection connection = null;  
    public Statement  statement = null;
    
	public void initialize(){
		try {  

			Class.forName("org.hsqldb.jdbcDriver").newInstance();

			String dos = System.getenv("APPDATA") + "\\LCbourgTournament";
			System.out.println("Dossier de stockage:" + dos);
			if( !new File(dos).isDirectory() ){
				new File(dos).mkdir();
			}
			connection = DriverManager  
			        .getConnection("jdbc:hsqldb:file:" + dos + "\\database","sa","");  
				//	.getConnection("jdbc:hsqldb:hsql://localhost/");
			statement = connection.createStatement();  
			
		}catch(SQLException e){
			JOptionPane.showMessageDialog(null, "Impossible de se connecter à la base de donnée. Vérifier qu'une autre instance du logiciel n'est pas déjà ouverte.");
			System.out.println(e.getMessage());
			System.exit(1);
		}catch (Exception e) {  
		    JOptionPane.showMessageDialog(null, "Erreur lors de l'initialisation du logiciel. Vérifiez votre installation Java et vos droits d'accès sur le dossier AppData.");
		    System.out.println(e.getMessage());
		    System.exit(1);
		} 
		try {
			importSQL(connection, new File("create.sql"));
		}catch(SQLException e){
			JOptionPane.showMessageDialog(null, e.getMessage());
			System.out.println(e.getMessage());
		}
		catch (FileNotFoundException e1) {
			System.out.println("Impossible de trouver le fichier create.sql. Vérifiez votre installation.");
			JOptionPane.showMessageDialog(null, "Impossible de trouver le fichier create.sql. Vérifiez votre installation.");
		}
	}
	private static void importSQL(Connection conn, File in) throws SQLException, FileNotFoundException{
	        @SuppressWarnings("resource")
			Scanner s = new Scanner(in);
	        s.useDelimiter("(;(\r)?\n)|(--\n)");
	        Statement st = null;
	        try
	        {
	                st = conn.createStatement();
	                while (s.hasNext())
	                {
	                        String line = s.next();
	                        if (line.startsWith("/*!") && line.endsWith("*/"))
	                        {
	                                int i = line.indexOf(' ');
	                                line = line.substring(i + 1, line.length() - " */".length());
	                        }

	                        if (line.trim().length() > 0)
	                        {
	                        	//System.out.println("Req:" + line);
	                                st.execute(line);
	                        }
	                }
	        }
	        finally
	        {
	                if (st != null) st.close();
	        }
	}
	public static String mysql_real_escape_string( String str) 
            throws Exception
      {
          if (str == null) {
              return null;
          }
                                      
          if (str.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/? ]","").length() < 1) {
              return str;
          }
              
          String clean_string = str;
          //clean_string = clean_string.replaceAll("\\\\", "\\\\\\");

          clean_string = clean_string.replaceAll("\\n","\\\\n");
          clean_string = clean_string.replaceAll("\\r", "\\\\r");
          clean_string = clean_string.replaceAll("\\t", "\\\\t");
          clean_string = clean_string.replaceAll("\\00", "\\\\0");
          clean_string = clean_string.replaceAll("'", "''");
          //clean_string = clean_string.replaceAll("\\'", "\\\\'");
          return clean_string;
          /*if (clean_string.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/?\\\\\"' ]"
            ,"").length() < 1) 
          {
              return clean_string;
          }else{
        	  return "";
          }*/
      }
}
