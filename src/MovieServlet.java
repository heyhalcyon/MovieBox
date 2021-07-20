import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class MovieServlet
 */
@WebServlet(name = "MovieServlet",urlPatterns = "/api/index")
public class MovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	// Create a dataSource which registered in web.xml
    //@Resource(name = "jdbc/moviedb")
    
    private DataSource dataSource;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */ 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
        
		response.setContentType("application/json"); // Response mime type, correspond to the index.js file

		//request.getSession()
		
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();   
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
        
        
        
        try {

        	//Get TJ start
        	long TJ = System.nanoTime();
        	//Class.forName("com.mysql.jdbc.Driver").newInstance();
            
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
            System.out.println("-----IN MOVIESERVLET, lookup db is "+ jdbcdb);
            //dataSource = (DataSource) envCtx.lookup("jdbc/TestDB");
            dataSource = (DataSource) envCtx.lookup(jdbcdb);

        	Connection dbcon = dataSource.getConnection();
        	String query1 = "select * from ratings, movies where ratings.movieId = movies.id ORDER BY rating DESC LIMIT 20";

			PreparedStatement statement1 = dbcon.prepareStatement(query1);

			// Perform the query
			ResultSet rs1 = statement1.executeQuery();
			
			//Logging TJ
			long TJEnd = System.nanoTime();
			printWriter.println("Index,TJ,"+(TJEnd-TJ));
			
			JsonArray jsonArray = new JsonArray();

			// Iterate through each row of rs1
			while (rs1.next()) {

				String movieId = rs1.getString("movieId");
				String movieTitle = rs1.getString("title");
				String movieYear = rs1.getString("year");
				String movieDirector = rs1.getString("director");
				String movieRating = rs1.getString("rating");
		
				//Constructing StarJson
				String queryStar = "select * from stars as s, stars_in_movies as sim where sim.movieid = ? and s.id = sim.starId";
				PreparedStatement statementStar = dbcon.prepareStatement(queryStar);
				statementStar.setString(1,  movieId);
				ResultSet rsStar = statementStar.executeQuery();
				JsonArray starJsonArray = new JsonArray();
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

				//Constructing GenreJson
				String queryGenre = "select * from genres as g, genres_in_movies as gim, movies as m where gim.movieid = ? and g.id = gim.genreId and m.id = gim.movieId";
				PreparedStatement statementGenre = dbcon.prepareStatement(queryGenre);
				statementGenre.setString(1,  movieId);
				ResultSet rsGenre = statementGenre.executeQuery();
				JsonArray genreJsonArray = new JsonArray();
				while(rsGenre.next()) {
					JsonObject genreJson = new JsonObject();
					String genreId = rsGenre.getString("id");
					String genreName = rsGenre.getString("name");
					genreJson.addProperty("genre_id", genreId);
					genreJson.addProperty("genre_name", genreName);
					genreJsonArray.add(genreJson);
				}
				
				rsGenre.close();
				statementGenre.close();
				
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
			
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);
            
            //Logging TS
            long TSEnd = System.nanoTime();
			printWriter.println("Index,TS,"+(TSEnd-TS));
			printWriter.close();

			rs1.close();
			statement1.close();	
			dbcon.close();
	
        } catch (Exception e) {
        	
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			
			// set response status to 500 (Internal Server Error)
			response.setStatus(500);

        }
        out.close();
	}
	

}



