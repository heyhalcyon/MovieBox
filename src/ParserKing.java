import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import javax.sql.DataSource;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;

public class ParserKing {
	
	 @Resource(name = "jdbc/moviedb")  
	    private DataSource dataSource;
	 
	 public static void main(String[] args) {
		 XMLParser dpe = new XMLParser();
		 System.out.println("run example");
	     dpe.runExample();
	     HashMap<String, Film> filmmap = dpe.getFilmMap(); 
	     
	     ActorXMLParser ap = new ActorXMLParser();
	     ap.runExample();
	     HashMap<String,Integer> actormap = ap.getActorMap();
	     
	     CastXMLParser cp = new CastXMLParser(filmmap,actormap);    
	     cp.runExample();
	     
	     filmmap = cp.getFilmMap();   
	     
	     String user = "root";
         String passwd = "122byitanz";
         String url = "jdbc:mysql://localhost:3306/moviedb";
         System.out.println("DONE PARSING #############################");
         int count = 0;
         int batchCount = 0;
         
         try {
	         Class.forName("com.mysql.jdbc.Driver").newInstance();
	         Connection dbcon = DriverManager.getConnection(url,user, passwd);
	         dbcon.setAutoCommit(false);
	         
	         
//	         String query100 = "select * from movies;";
//	         PreparedStatement statement100 = dbcon.prepareStatement(query100);	  
//	         ResultSet rs100 = statement100.executeQuery();
//	         HashMap<String, Film> moviesdb = new HashMap<String, Film>();
//	         while(rs100.next()) {
//	        	 String mid = rs100.getString("id");
//	        	 String title = rs100.getString("title");
//	        	 String director = rs100.getString(columnIndex)
//	        	 
//	         }
	         

	         
	        String sqlMovieExist = "SELECT * FROM movies WHERE title=? AND year=? AND director=?;";
	        PreparedStatement statement1 = dbcon.prepareStatement(sqlMovieExist);	  
	         
	         
	        String query2 = "insert into movies (id, title, year, director) VALUES (?, ?, ?, ?);";
     		PreparedStatement statement2 = dbcon.prepareStatement(query2);
     		
     		String query3 = "SELECT * FROM genres WHERE name = ?;";
     		PreparedStatement statement3 = dbcon.prepareStatement(query3);
     		
     		String query4 = "INSERT INTO genres (id, name) values (null,?);";
     		PreparedStatement statement4 = dbcon.prepareStatement(query4);
     		
     		String query5 = "INSERT INTO genres_in_movies (genreId, movieId) values (?, ?);";
     		PreparedStatement statement5 = dbcon.prepareStatement(query5);
     		
     		String query6 = "SELECT * FROM genres_in_movies WHERE genreId = ? and movieId = ?;";
     		PreparedStatement statement6 = dbcon.prepareStatement(query6);
     		
     		String query7 = "SELECT * FROM stars WHERE name = ? and birthYear = ?;";
     		PreparedStatement statement7 = dbcon.prepareStatement(query7);
     		
     		String query8 = "INSERT ignore INTO stars (id, name, birthYear) values (?,?,?);";
     		PreparedStatement statement8 = dbcon.prepareStatement(query8);
     		
     		String query9 = "INSERT INTO stars_in_movies (starId, movieId) values (?, ?);";
     		PreparedStatement statement9 = dbcon.prepareStatement(query9);
     		
     		String query10 = "SELECT * FROM stars_in_movies WHERE starId = ? and movieId = ?;";
     		PreparedStatement statement10 = dbcon.prepareStatement(query10);
     		
     		long startTime = System.nanoTime();
     		
     		String sqlmaxid = "SELECT max(id) as id from movies";
			PreparedStatement statement = dbcon.prepareStatement(sqlmaxid);
			ResultSet rs = statement.executeQuery();
			rs.next();
			String maxid = rs.getString(1);
			Integer intid = Integer.parseInt(maxid.substring(2));

	         for (Map.Entry<String, Film> entry : filmmap.entrySet()) {
	        	 	//RETRIEVE DATA FROM EACH ELEMENT IN FILMMAP
	        	    String fid = entry.getKey();
	        	    Film film = entry.getValue();
	        	    String dirname = film.dirname;
	        	    String title = film.title;
	        	    Integer year = film.year;
	        	    ArrayList<String> genres = film.genres;
	        		ArrayList<Actor> actors = film.actors;
	        		 
	        		//CHECK WHETHER MOVIE EXISTS
	        		statement1.setString(1,title);	
	        		statement1.setInt(2,year);	
	        		statement1.setString(3,dirname);
	        		ResultSet rs1 = statement1.executeQuery();
	        		
	        		String movieid = null;
	        		if(rs1.next()) {   // movie exists
	        			movieid =  rs1.getString("id");
	        			
	        		}else {  
	        			//if movieid doesn't exist
	        			//GET NEW MOVIE ID
//	        			String sqlmaxid = "SELECT max(id) as id from movies";
//	        			PreparedStatement statement = dbcon.prepareStatement(sqlmaxid);
//	        			ResultSet rs = statement.executeQuery();
//	        			rs.next();
//	        			String maxid = rs.getString(1);
	        			
//	        			Integer intid = Integer.parseInt(maxid.substring(2));
	        			intid = intid + 1;
	        			String newid = Integer.toString(intid);
	        			movieid = "tt"+newid;	
	        			
	        			//INSERT NEW MOVIE INTO MOVIE TABLE
	        			statement2.setString(1, movieid);
	        			statement2.setString(2,title);
	        			statement2.setInt(3, year);
	        			statement2.setString(4,dirname);
	        			statement2.addBatch();
	        			batchCount++;
	        			//statement2.executeUpdate();
	        			
	        			for(String genre:genres) {
			        		
			        		if(genre != null) {
			        			//CEHCK WHETHER GENRE IS IN TABLE
			        			statement3.setString(1, genre);
			        			ResultSet rs3 = statement3.executeQuery();
			        			Integer genreid = null;
			        			
			        			if(rs3.next()) {
			        				genreid = rs3.getInt("id");
			        			}else {  
			        				//if not, create a new genre
			        				statement4.setString(1, genre);
			        				statement4.executeUpdate();
			        				statement4.addBatch();
			        				batchCount++;
			        				
			        				//get new genreid
			        				statement3.setString(1, genre);
			        				ResultSet rs33 = statement3.executeQuery();
				        			
				        			if(rs33.next()) {
				        				genreid = rs33.getInt("id");
				        			}
			        			}
			        			
			        			statement6.setInt(1, genreid);
			        			statement6.setString(2, movieid);
			        			ResultSet rs6 = statement3.executeQuery();
			        			if(!rs6.next()) {
			        				//CREATE GENRES_IN_MOVIE 
			        				statement5.setInt(1,genreid);
			        				statement5.setString(2, movieid);
			        				statement5.addBatch();
			        				batchCount++;
			        				//statement5.executeUpdate();
			        			}
			        			
			        		}
			        	}
			        	
				        for(Actor actor:actors) {
				        	if(actor != null) {
				        		String actorname = actor.name;
				        		Integer actoryear = actor.year;	  
//				        		System.out.println("actorname:   "+actorname);
//				        		System.out.println("actoryear:   "+actoryear);
				        		
				        		statement7.setString(1, actorname);
				        		if(actoryear == null) {
				        			statement7.setNull(2, Types.INTEGER);
				        		}else {
				        			statement7.setInt(2, actoryear);
				        		}
				        		ResultSet rs7 = statement7.executeQuery();
				        		String starid = null;
				        		if(rs7.next()) {  //if star exsits
				        			starid = rs7.getString("id");
				        		}else { // if star doesn't exsits
				        			
				        			String sqlmaxstarid = "SELECT max(id) from stars";
				        			PreparedStatement statementstarid = dbcon.prepareStatement(sqlmaxstarid);
				        			ResultSet rs88 = statementstarid.executeQuery();
				        			rs88.next();
				        			String maxstarid = rs88.getString(1);
				        			Integer intstarid = Integer.parseInt(maxstarid.substring(2));
				        			intstarid = intstarid + 1;
				        			String newstarid = Integer.toString(intstarid);
				        			starid = "nm"+newstarid;	

				        			statement8.setString(1, starid);
				        			statement8.setString(2, actorname);
				        			
				        			
				        			if(actoryear == null) {
					        			statement8.setNull(3, Types.INTEGER);
					        		}else {
					        			statement8.setInt(3, actoryear);
					        		}
				        			statement8.addBatch();
				        			batchCount++;
				        			//statement8.executeUpdate();	
				        		}
				        		
				        		statement10.setString(1,starid);
				        		statement10.setString(2, movieid);
				        		ResultSet rs10 = statement10.executeQuery();
				        		if(!rs10.next()) {  //if stars_in_movies record not exist
				        			statement9.setString(1, starid);
				        			statement9.setString(2, movieid);
				        			statement9.addBatch();
				        			batchCount++;
				        			//statement9.executeUpdate();
				        		}
				        	
				        	}    		    	   
			        	}
	        	        			
	        		}
	
		        	
			        long endTime = System.nanoTime();
		        	long duration = (endTime - startTime)/1000000000;
		        	
			        //Insert Movie
			       
			        if(batchCount > 1000) {
			        	statement2.executeBatch();
			        	System.out.println(count);
			        	System.out.println("Time: " + duration);
			        	
			        	
			        	//Genres
				        statement4.executeBatch();
				        //GIM
				        statement5.executeBatch();
				        //Stars
				        statement8.executeBatch();
				        //SIM
			        	statement9.executeBatch();
			        	
			        	batchCount = 0;
			        	dbcon.commit();
			        }
   	
		        	
		        	count++;
	         }
	         System.out.println("------officially done-----");
	         
	         dbcon.close();
        }catch(Exception e) {
        	e.printStackTrace();
        }
         
         
//        try {
//	         Class.forName("com.mysql.jdbc.Driver").newInstance();
//	         Connection dbcon = DriverManager.getConnection(url,user, passwd);
//	         	         
//	         String addMovieQuery = "CALL add_movie(?,?,?,?,?,?);";
//	         CallableStatement s = dbcon.prepareCall(addMovieQuery);
//	         dbcon.setAutoCommit(false);
//	         
//	         for (Map.Entry<String, Film> entry : filmmap.entrySet()) {
//	        	    String fid = entry.getKey();
//	        	    Film film = entry.getValue();
//	        	    String dirname = film.dirname;
//	        	    String title = film.title;
//	        	    Integer year = film.year;
//	        	    ArrayList<String> genres = film.genres;
//	        		ArrayList<Actor> actors = film.actors;
//	        		
////	        		System.out.println("fid:   "+fid);
////	        		System.out.println("dirname:   "+dirname);
////	        		System.out.println("title:   "+title);
////	        		System.out.println("year:   "+year);
////	        		System.out.println("genres size:   "+genres.size());
////	        		System.out.println("actors size:   "+actors.size());
//	        		
//     		
//		        	for(String genre:genres) {					        							
//			        	for(Actor actor:actors) {
//			        				String actorname = actor.name;
//			        				Integer actoryear = actor.year;	        				
//			        				s.setString(1,title);
//			        				s.setString(3,dirname);			
//			        				s.setInt(2,year);	        			
//			        				
//			        				if(genre == null) {
//			        					s.setNull(4,Types.VARCHAR);
//			        				}else {
//			        					s.setString(4, genre);
//			        				}
//			        				
//			        				if(actorname == null) {
//			        					s.setNull(5, Types.VARCHAR);
//			        				}else {
//			        					s.setString(5, actorname);
//			        				}
//			        				
//			        				if(actoryear == null) {
//			        					s.setNull(6, Types.INTEGER);
//			        				}else {
//			        					s.setInt(6, actoryear);
//			        				}
//			        				System.out.println("CALLstatement    "+s);
//			        				s.addBatch();
//			        				//s.clearParameters();
//			        				batchCount++;
//			        				System.out.println(count++);
//			        	}    		    	   
//		        	}
//		        	if(batchCount==50) {
//	        			s.executeBatch();
//	        			batchCount=0;
//	        			dbcon.commit();
//	        		}
//	         }
//		     s.close();
//	         dbcon.close();
//        }catch(Exception e) {
//        	e.printStackTrace();
//        }
              
	 }
}


//
//if(genres != null) {
//	for(String genre:genres) {			
//		if(actors != null) {	        				
//			for(Actor actor:actors) {
//				String actorname = actor.name;
//				Integer actoryear = actor.year;	        				
//				s.setString(1,title);
//				s.setString(3,dirname);			
//				s.setInt(2,year);	        			
//				
//				if(genre == null) {
//					s.setNull(4,Types.VARCHAR);
//				}else {
//					s.setString(4, genre);
//				}
//				
//				if(actorname == null) {
//					s.setNull(5, Types.VARCHAR);
//				}else {
//					s.setString(5, actorname);
//				}
//				
//				if(actoryear == null) {
//					s.setNull(6, Types.INTEGER);
//				}else {
//					s.setInt(6, actoryear);
//				}
//				System.out.println("CALLstatement    "+s);
//				s.addBatch();
//				//s.clearParameters();
//				batchCount++;
////				s.execute();
////				System.out.println("count CALLs");
//				System.out.println(count++);
//				
////				s.registerOutParameter(7, Types.BOOLEAN);
////        		s.registerOutParameter(8, Types.VARCHAR);
////        		boolean already_exists = s.getBoolean(7);
////        		String c = s.getString(8);	
////        		
//        			
//			}
//		
//			
//		}else { // if actors is null
////			System.out.println("*******Genres not null, actors null******");
//			s.setString(1,title);
//			s.setString(3,dirname);
//
//			s.setInt(2,year);			
//			if(genre == null) {
//				s.setNull(4,Types.VARCHAR);
//			}else {
//				s.setString(4, genre);
//			}
//			
//			s.setNull(5,Types.VARCHAR);
//			s.setNull(6,Types.INTEGER);
//			
//			System.out.println("CALLstatement    "+s);
////			s.execute();
//			System.out.println("count CALLs");
//			System.out.println(count++);
////			s.registerOutParameter(7, Types.BOOLEAN);
////    		s.registerOutParameter(8, Types.VARCHAR);
////    		boolean already_exists = s.getBoolean(7);
////    		String c = s.getString(8);
//		}
//	
//	}
//}else{ // if genres = null
//	if(actors != null) {
//		System.out.println("*******Genres null, actors not null******");
//		for(Actor actor:actors) {
//			String actorname = actor.name;
//			Integer actoryear = actor.year;
//			s.setString(1,title);
//			s.setString(3,dirname);	
//			s.setInt(2,year);
//			s.setNull(4,Types.VARCHAR);
//			if(actorname == null) {
//				s.setNull(5, Types.VARCHAR);
//			}else {
//				s.setString(5, actorname);
//			}
//			
//			if(actoryear == null) {
//				s.setNull(6, Types.INTEGER);
//			}else {
//				s.setInt(6, actoryear);
//			}
//			System.out.println("CALLstatement    "+s);
//			s.execute();
//			s.registerOutParameter(7, Types.BOOLEAN);
//    		s.registerOutParameter(8, Types.VARCHAR);
//    		boolean already_exists = s.getBoolean(7);
//    		String c = s.getString(8);
//    		
//		}
//	}else { // if actors is null
//		System.out.println("*******Genres null, actors null******");
//		s.setString(1,title);
//		s.setString(3,dirname);
//		s.setInt(2,year);			     				
//		s.setNull(4,Types.VARCHAR);
//		s.setNull(5,Types.VARCHAR);
//		s.setNull(6,Types.INTEGER);
//		System.out.println("CALLstatement    "+s);
//		s.execute();
//		s.registerOutParameter(7, Types.BOOLEAN);
//		s.registerOutParameter(8, Types.VARCHAR);
//		boolean already_exists = s.getBoolean(7);
//		String c = s.getString(8);
//		
//	}
//
//	
//}
//
//if(batchCount==50) {
//	s.executeBatch();
//	batchCount=0;
//	dbcon.commit();
//}
//
////try {
////s.registerOutParameter(7, Types.BOOLEAN);
////s.registerOutParameter(8, Types.VARCHAR);
////boolean already_exists = s.getBoolean(7);
////String c = s.getString(8);
////}catch(Exception e){
////	
////}
////dbcon.commit();
//
////
//    	   
//}
//s.close();
//dbcon.close();
//}catch(Exception e) {
//e.printStackTrace();
//}
//
//
//
//}
//}