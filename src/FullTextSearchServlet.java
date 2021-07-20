import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "FullTextSearchServlet", urlPatterns = "/api/ft")
public class FullTextSearchServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    //@Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type
        
        String rpp = request.getParameter("rpp");
        String p = request.getParameter("p");
        
        
        //Logging Time
        String contextPath = getServletContext().getRealPath("/");
        String xmlFilePath=contextPath+"test";
        System.out.println(xmlFilePath);
        //Get TS start
        long TS = System.nanoTime();
        
        File myfile = new File(xmlFilePath);
        if(!myfile.exists()) {
        	myfile.createNewFile();
        }
        FileWriter fileWriter = new FileWriter(myfile, true); //Set true for append mode
        PrintWriter printWriter = new PrintWriter(fileWriter);
 
        boolean android = true;
        int lower = 0;
        int higher = 50;
        int resultsPerPage = 0;
        if(rpp==null & p==null) {
        	android = false;
        }else{
        	int page = Integer.parseInt(p);
	        resultsPerPage = Integer.parseInt(rpp);
	        lower = resultsPerPage * (page - 1);
			higher = resultsPerPage * page;
	     
        }
        String querystring = request.getParameter("search");
        
        String[] keyword_list = request.getParameter("search").split(" ");
    
        PrintWriter out = response.getWriter();
        System.out.println(android);
        try {
        	// the following few lines are for connection pooling
            // Obtain our environment naming context
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
            	response.getWriter().println("envCtx is NULL");

          //READ
            Random ran = new Random(); 
            int nxt = ran.nextInt(2);
            String jdbcdb = "";
            if( nxt == 1) {
            	jdbcdb = "jdbc/masterDB";	
            }else {
            	jdbcdb = "jdbc/slaveDB";
            }
            
            //dataSource = (DataSource) envCtx.lookup("jdbc/TestDB");
            dataSource = (DataSource) envCtx.lookup(jdbcdb);
           
            Connection dbcon = dataSource.getConnection();
            
			String a = "(SELECT * FROM movies WHERE MATCH (title) AGAINST ( ?  in boolean mode) ) "
					+ "UNION (select * from movies where ed(title,?) <=3);";

			PreparedStatement statement = dbcon.prepareStatement(a);
			
			if(keyword_list.length == 1) {
            	statement.setString(1, "+"+keyword_list[0]+"*");
            	statement.setString(2, querystring);
            }else {
            	String qstring = "";
            	for(int i = 0; i< keyword_list.length; i++) {
                	qstring += "+"+keyword_list[i]+"* ";
                }  	
            	statement.setString(1, qstring);
            	statement.setString(2, querystring);        	
            }
			
            System.out.println("fulltext query: "+statement.toString());
            
			
			
			
           
           
//            String query = "(SELECT * FROM movies WHERE MATCH (title) AGAINST ('";    
//            for(int i = 0; i< keyword_list.length; i++) {
//            	query += "+"+keyword_list[i]+"* ";
//            }
//            query += "' IN BOOLEAN MODE)) ";
//            query += "UNION ";
//            query += "(select * from movies where ed(title,'"+ request.getParameter("search") +"')<=3) ";   // For now, threshold is 3.
//            
//            if(android) {            	
//	            query += "limit "+resultsPerPage+" offset "+lower+";";
//	            
//	            System.out.println("ft query");
//	            System.out.println(query);	            
//	            
//            }else {
//
//	            query += ";";
//            }
//            System.out.println(query);	  
//          
//            PreparedStatement statement = dbcon.prepareStatement(query);
            
//
//            if(android) {            	
//	            query += "limit "+resultsPerPage+" offset "+lower+";";
//	            
//	            System.out.println("ft query");
//	            System.out.println(query);	            
//	            
//            }else {
//
//	            query += ";";
//            }
//            System.out.println(query);	  
//          
//            PreparedStatement statement = dbcon.prepareStatement(query);
            //Get TJ start
        	long TJ = System.nanoTime();

            ResultSet rs = statement.executeQuery();
            //Get TJ end
            long TJEnd = System.nanoTime();
            
            JsonArray jsonArray = new JsonArray();
           
            while(rs.next()) {
            	System.out.println(1111111);
            	String movieId = rs.getString("id");
				String movieTitle = rs.getString("title");
				String movieYear = rs.getString("year");
				String movieDirector = rs.getString("director");
				System.out.println(movieId);
				
				String queryStar = "select * from stars as s, stars_in_movies as sim where sim.movieid = ? and s.id = sim.starId";
				PreparedStatement statementStar = dbcon.prepareStatement(queryStar);
				statementStar.setString(1,  movieId);
				ResultSet rsStar = statementStar.executeQuery();
				JsonArray starJsonArray = new JsonArray();
				System.out.println(22222222);
				while(rsStar.next()) {
					JsonObject starJson = new JsonObject();
					String starId = rsStar.getString("id");
					String starName = rsStar.getString("name");
					String starBirthYear = rsStar.getString("birthYear");
					starJson.addProperty("star_id", starId);
					starJson.addProperty("star_name", starName);
					starJson.addProperty("star_birth_year", starBirthYear);
					starJsonArray.add(starJson);
				}
				statementStar.close();
				rsStar.close();
				System.out.println(33333333);
				
				String queryGenre = "select * from genres as g, genres_in_movies as gim, movies as m where gim.movieid = ? and g.id = gim.genreId and m.id = gim.movieId";
				PreparedStatement statementGenre = dbcon.prepareStatement(queryGenre);
				statementGenre.setString(1,  movieId);
				ResultSet rsGenre = statementGenre.executeQuery();
				JsonArray genreJsonArray = new JsonArray();
				System.out.println(4444444);
				
				while(rsGenre.next()) {
					JsonObject genreJson = new JsonObject();
					String genreId = rsGenre.getString("id");
					String genreName = rsGenre.getString("name");
					genreJson.addProperty("genre_id", genreId);
					genreJson.addProperty("genre_name", genreName);
					genreJsonArray.add(genreJson);
				}
				statementGenre.close();
				rsGenre.close();
				System.out.println(5555555);
				String queryRating = "SELECT rating FROM movies, ratings WHERE movies.id  = ratings.movieId and movies.id = ?;";
				PreparedStatement statementRating = dbcon.prepareStatement(queryRating);
				statementRating.setString(1,  movieId);
				ResultSet rsRating = statementRating.executeQuery();
				
				String movieRating = null;
				if (rsRating.next() ) {
					movieRating = rsRating.getString("rating");
				}
				
				statementRating.close();
				rsRating.close();
				
				
				System.out.println(666666);
				JsonObject jsonObject = new JsonObject();
				
				jsonObject.addProperty("movie_id", movieId);
				jsonObject.addProperty("movie_title", movieTitle);
				jsonObject.addProperty("movie_year", movieYear);
				jsonObject.addProperty("movie_director", movieDirector);
				jsonObject.addProperty("movie_rating", movieRating);
				jsonObject.add("movie_stars", starJsonArray);
				jsonObject.add("movie_genres", genreJsonArray);			
				jsonArray.add(jsonObject);
				
            }
            System.out.println("jsonarray");
            System.out.println(jsonArray.toString());
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);
            //Logging TS
            long TSEnd = System.nanoTime();
			printWriter.println("FULLTEXT,TS,"+(TSEnd-TS));
			printWriter.close();

            rs.close();
            statement.close();
           
            
            dbcon.close();
        } catch (Exception e) {
        	
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);

        }
        out.close();

    }
}
