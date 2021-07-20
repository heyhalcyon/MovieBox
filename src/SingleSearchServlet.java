import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
 * Servlet implementation class SearchServlet
 */
@WebServlet(name = "SingleSearchServlet", urlPatterns = "/api/singlesearch")
public class SingleSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	//@Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SingleSearchServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		
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
            System.out.println("-----IN SingleSearchServlet, lookup db is "+ jdbcdb);
            //dataSource = (DataSource) envCtx.lookup("jdbc/TestDB");
            dataSource = (DataSource) envCtx.lookup(jdbcdb);
            
			Connection dbcon = dataSource.getConnection();
			
			String startWith = request.getParameter("sw");
			String count = request.getParameter("c");
			String genre = request.getParameter("g");
			String title = request.getParameter("t");
			String year = request.getParameter("y");
			String star = request.getParameter("s");
			String director = request.getParameter("d");
			String page = request.getParameter("p");
			String order = request.getParameter("o");
			String resultPerPage = request.getParameter("rpp");
			System.out.println(order);
			
			ResultSet rs;
			JsonArray resultJsonArray = new JsonArray();
			
			if(count != null) {
				String query = "";
				PreparedStatement statement;
				String c = "";
				if(genre!=null) {
					query = constructQuery("genreAll", Integer.parseInt(page), order, resultPerPage);
					statement = dbcon.prepareStatement(query);
					statement.setString(1,  genre);
					rs = statement.executeQuery();
					rs.first();
					c = rs.getString("c");
				}else if(startWith != null){
					query = constructQuery("startAll", Integer.parseInt(page), order, resultPerPage);
					statement = dbcon.prepareStatement(query);
					statement.setString(1,  startWith+"%");
					rs = statement.executeQuery();
					rs.first();
					c = rs.getString("c");
				}

				
				JsonObject countJson = new JsonObject();
				if(genre!=null) {
					countJson.addProperty("genre", genre);
				}else if(startWith!=null) {
					countJson.addProperty("startWith", startWith);
				}
				
				countJson.addProperty("count", c);
				System.out.println(countJson);
				JsonArray countJsonArray = new JsonArray();
				countJsonArray.add(countJson);
				out.write(countJsonArray.toString());
			}else if(startWith != null) {
				String query = constructQuery("start", Integer.parseInt(page), order, resultPerPage);
				PreparedStatement statement = dbcon.prepareStatement(query);
				statement.setString(1,  startWith+"%");
				System.out.println(statement);
				rs = statement.executeQuery();
				while(rs.next()) {
					String movieId = rs.getString("movieId");
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
				
				
				// write JSON string to output
	            out.write(resultJsonArray.toString());
			}else if(genre != null) {
				String query = constructQuery("genre", Integer.parseInt(page), order, resultPerPage);
				PreparedStatement statement = dbcon.prepareStatement(query);
				statement.setString(1,  genre);
				rs = statement.executeQuery();
				while(rs.next()) {
					String movieId = rs.getString("movieId");
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
				
				
				// write JSON string to output
	            out.write(resultJsonArray.toString());
			}
			
			
			

            // set response status to 200 (OK)
            response.setStatus(200);
            
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
	
	//TODO: allow paging
	private String constructQuery(String key, int page, String order, String resultPerPage) {
		String query = "";
		int resultsPerPage = Integer.parseInt(resultPerPage);
		
		int lower = resultsPerPage * (page - 1);
		int higher = resultsPerPage * page;
		if (order.equals("title")) {
			order = "title asc";
		}else if(order.equals("rating")) {
			order = "rating desc";
		}
		
		switch(key) {
		
		case "start":
			query = "select m.id as movieId, m.title, m.year, m.director, r.rating as rating from movies as m, ratings as r where  r.movieId = m.id and m.title LIKE ? order by " + order + " limit " + lower + "," + higher;
			break;
			
		case "startAll":
			query = "select count(*) as c from movies where title LIKE ?";
			break;
			
		case "genre":
			query = "select * from movies as m, genres_in_movies as gim, genres as g, ratings where ratings.movieId = m.id and m.id=gim.movieId and gim.genreId=g.id and g.name=? order by " + order + " limit " + lower + "," + higher;
			break;
			
		case "genreAll":
			query = "select count(*) as c from movies as m, genres_in_movies as gim, genres as g, ratings where ratings.movieId = m.id and m.id=gim.movieId and gim.genreId=g.id and g.name=?";
			break;
				
		case "year":
			query = "select * from (select * from ratings, movies where ratings.movieId = movies.id ORDER BY rating DESC LIMIT 20) as t," + 	
					"	genres_in_movies as gim, " + 
					"    genres as g, " + 
					"    stars_in_movies as sim, " + 
					"    stars as s " + 
					"    where t.movieId = gim.movieId and g.id = gim.genreId and sim.movieId = t.movieId and sim.starId = s.id and m.year = ?;";
			break;
		
		}
		return query;
		
	}
 

}
