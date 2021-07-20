import com.google.gson.JsonArray;
import java.util.*;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
 * Servlet implementation class SearchServlet
 */
@WebServlet(name = "AdvanceSearchServlet", urlPatterns = "/api/advancesearch")
public class AdvanceSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	//@Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdvanceSearchServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		
        //Get TS start
//        long TS = System.nanoTime();
//        
//        File myfile = new File(xmlFilePath);
//        if(!myfile.exists()) {
//        	myfile.createNewFile();
//        }
//        FileWriter fileWriter = new FileWriter(myfile, true); //Set true for append mode
//        PrintWriter printWriter = new PrintWriter(fileWriter);
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

			// the following few lines are for connection pooling
            // Obtain our environment naming context

            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            // Look up our data source
            
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
					
			String count = request.getParameter("c");
			String genre = request.getParameter("g");
			String title = request.getParameter("t");
			String year = request.getParameter("y");
			String star = request.getParameter("s");
			String director = request.getParameter("d");
			String resultPerPage = request.getParameter("rpp");
			String page = request.getParameter("p");
			String order = request.getParameter("o");
			String subStringMatch = request.getParameter("sub");
			System.out.println(order);
			
			ResultSet rs;
			JsonArray resultJsonArray = new JsonArray();
			
			if(count != null) {
				String query = "select count(*) as c from genres as g, movies as m, genres_in_movies as gim where gim.movieId = m.id and g.id = gim.genreId and g.name = ?;";
				PreparedStatement statement = dbcon.prepareStatement(query);
				statement.setString(1,  genre);
				
				//Get TJ
				long TJ = System.nanoTime();
				rs = statement.executeQuery();
				//Log TJ
				long TJEnd = System.nanoTime();
				printWriter.println("AdvanceSearch,TJ,"+(TJEnd-TJ));
				
				rs.first();
				String c = rs.getString("c");
				JsonObject countJson = new JsonObject();
				countJson.addProperty("genre", genre);
				countJson.addProperty("count", c);

				JsonArray countJsonArray = new JsonArray();
				countJsonArray.add(countJson);
				out.write(countJsonArray.toString());
				statement.close();
				rs.close();
				
				
			}else{
				/**
				 * @param genre
				 * @param title
				 * @param year
				 * @param star
				 * @param director
				 * @param rpp
				 * @param page
				 * @param order
				 * @return
				 */
				PreparedStatement statement = constructQuery(genre, title, year, star, director, resultPerPage , 1, order, subStringMatch);
				
//				
//				//Get TJ start
//	        	long TJ = System.nanoTime();
//	        	//Logging TJ
//				long TJEnd = System.nanoTime();
//				printWriter.println("Index,TJ,"+(TJEnd-TJ));
				
				System.out.println(title);
				
				//Get TJ
				long TJ = System.nanoTime();
				System.out.println("start query");
				
				rs = statement.executeQuery();
				
				System.out.println("ENd Query");
				
				//Log TJ
				long TJEnd = System.nanoTime();
				printWriter.println("AdvanceSearch,TJ,"+(TJEnd-TJ));
				System.out.println("AdvanceSearch,TJ,"+(TJEnd-TJ));
				
				String previousId = "";
				while(rs.next()) {
					
					String movieId = rs.getString("movieId");
					if(!previousId.equals(movieId)) {
						previousId=movieId;
						String movieTitle = rs.getString("title");
						String movieYear = rs.getString("year");
						String movieDirector = rs.getString("director");
						String movieRating = rs.getString("rating");
						
						
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
						
						// Create a JsonObject based on the data we retrieve from rs

						JsonObject jsonObject = new JsonObject();
					
						jsonObject.addProperty("movie_id", movieId);
						jsonObject.addProperty("movie_title", movieTitle);
						jsonObject.addProperty("movie_year", movieYear);
						jsonObject.addProperty("movie_director", movieDirector);
						jsonObject.addProperty("movie_rating", movieRating);
						jsonObject.add("movie_stars", starJsonArray);
						jsonObject.add("movie_genres", genreJsonArray);		
						resultJsonArray.add(jsonObject);
					}
					
				}
				rs.close();
				
	            out.write(resultJsonArray.toString());
	            statement.close();
	            rs.close();
	            
	            
			}
			
			
			
			
            // set response status to 200 (OK)
            response.setStatus(200);
            //Logging TS
            long TSEnd = System.nanoTime();
			printWriter.println("AdvanceSearch,TS,"+(TSEnd-TS));
			printWriter.close();

            
			//rs.close();
			//statement.close();
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	
	/**
	 * @param genre
	 * @param title
	 * @param year
	 * @param star
	 * @param director
	 * @param rpp
	 * @param page
	 * @param order
	 * @return
	 * @throws SQLException 
	 */
	private PreparedStatement constructQuery(String genre, String title, String year, String star, String director, String rpp, int page, String order, String startWith) throws SQLException {
		Connection dbcon = dataSource.getConnection();
		
		if(startWith.equals("false")) {
			String query = "select m.id as movieId, m.title, m.year, m.director, g.name as genreName, r.rating as rating, s.name as starName from genres as g, genres_in_movies as gim, movies as m, ratings as r, stars as s, stars_in_movies as sim where g.id = gim.genreId and m.id = gim.movieId and r.movieId = m.id and s.id = sim.starId and sim.movieId = m.id";
			int resultsPerPage = Integer.parseInt(rpp);
			
			int lower = resultsPerPage * (page - 1);
			int higher = resultsPerPage * page;
			if (order.equals("title")) {
				order = "title asc";
			}else if(order.equals("rating")) {
				order = "rating desc";
			}
			
			if(genre != null) {
				query += " and g.name = ?";
			}
			if(title != null) {
				query += " and m.title = ?";
			}
			if(year != null) {
				query += " and m.year = ?";
			}
			if(director != null) {
				query += " and m.director = ?";
			}
			
			int parameterCount = 1;
			
			PreparedStatement statement = dbcon.prepareStatement(query);
			if(genre != null) {
				statement.setString(parameterCount,  genre);
				parameterCount += 1;
			}
			if(title != null) {
				statement.setString(parameterCount,  title);
				parameterCount += 1;
			}
			if(year != null) {
				statement.setString(parameterCount,  year);
				parameterCount += 1;
			}
			if(director != null) {
				statement.setString(parameterCount,  director);
				parameterCount += 1;
			}
			System.out.println(statement);
			//query += " limit " + lower + "," + higher;
			return statement;
			
		}else {
			String query = "select m.id as movieId, m.title, m.year, m.director, g.name as genreName, r.rating as rating, s.name as starName from genres as g, genres_in_movies as gim, movies as m, ratings as r, stars as s, stars_in_movies as sim where g.id = gim.genreId and m.id = gim.movieId and r.movieId = m.id and s.id = sim.starId and sim.movieId = m.id";
			int resultsPerPage = Integer.parseInt(rpp);
			
			int lower = resultsPerPage * (page - 1);
			int higher = resultsPerPage * page;
			if (order.equals("title")) {
				order = "title asc";
			}else if(order.equals("rating")) {
				order = "rating desc";
			}
			
			if(genre != null) {
				query += " and g.name LIKE ?";
			}
			if(title != null) {
				query += " and m.title LIKE ?";
			}
			if(year != null) {
				query += " and m.year LIKE ?";
			}
			if(director != null) {
				query += " and m.director LIKE ?";
			}
			
			int parameterCount = 1;
			
			PreparedStatement statement = dbcon.prepareStatement(query);
			if(genre != null) {
				statement.setString(parameterCount,  "%"+genre+"%");
				parameterCount += 1;
			}
			if(title != null) {
				statement.setString(parameterCount,  "%"+title+"%");
				parameterCount += 1;
			}
			if(year != null) {
				statement.setString(parameterCount,  "%"+year+"%");
				parameterCount += 1;
			}
			if(director != null) {
				statement.setString(parameterCount,  "%"+director+"%");
				parameterCount += 1;
			}
			
			System.out.println(query);
			
			return statement;
		}
		
		
	}
 

}
