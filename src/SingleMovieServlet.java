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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

//Declaring a WebServlet called SingleMovieServlet, which maps to url "/api/single-movie"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;
       
	// Create a dataSource which registered in web.xml
	//@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SingleMovieServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("application/json"); // Response mime type

		// Retrieve parameter id from url request.
		String id = request.getParameter("id");
		

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
			// Get a connection from dataSource
			
			// the following few lines are for connection pooling
            // Obtain our environment naming context

            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
            	response.getWriter().println("envCtx is NULL");

            // Look up our data source
          //READ
            Random ran = new Random(); 
            int nxt = ran.nextInt(2);
            String jdbcdb = "";
            if( nxt == 1) {
            	jdbcdb = "jdbc/masterDB";	
            }else {
            	jdbcdb = "jdbc/slaveDB";
            }
            System.out.println("-----IN SingleMovieSERVLET, lookup db is "+ jdbcdb);
            //dataSource = (DataSource) envCtx.lookup("jdbc/TestDB");
            dataSource = (DataSource) envCtx.lookup(jdbcdb);
            
			Connection dbcon = dataSource.getConnection();
			
			String query1 = "select * from stars as s, stars_in_movies as sim where sim.movieid = ? and s.id = sim.starId";
			PreparedStatement statement1 = dbcon.prepareStatement(query1);
			statement1.setString(1,  id);
			ResultSet rs1 = statement1.executeQuery();
			JsonArray starJsonArray = new JsonArray();
			while(rs1.next()) {
				JsonObject starJson = new JsonObject();
				String starId = rs1.getString("id");
				String starName = rs1.getString("name");
				String starBirthYear = rs1.getString("birthYear");
				starJson.addProperty("star_id", starId);
				starJson.addProperty("star_name", starName);
				starJson.addProperty("star_birth_year", starBirthYear);
				starJsonArray.add(starJson);
			}
			
			
			// Construct a query with parameter represented by "?"
			String query2 = "select * from genres as g, genres_in_movies as gim where gim.movieid = ? and g.id = gim.genreId";

			// Declare our statement
			PreparedStatement statement2 = dbcon.prepareStatement(query2);

			// Set the parameter represented by "?" in the query to the id we get from url,
			// num 1 indicates the first "?" in the query
			statement2.setString(1, id);

			// Perform the query
			ResultSet rs2 = statement2.executeQuery();

			JsonArray genreJsonArray = new JsonArray();

			// Iterate through each row of rs
			while (rs2.next()) {
				String movieGenre = rs2.getString("name");

				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("movie_genre", movieGenre);

				genreJsonArray.add(jsonObject);
			}
			
			//System.out.println(genreJsonArray.toString());
			// Construct a query with parameter represented by "?"
				String query3 = "select * from movies where id = ?";

				// Declare our statement
				PreparedStatement statement3 = dbcon.prepareStatement(query3);

				// Set the parameter represented by "?" in the query to the id we get from url,
				// num 1 indicates the first "?" in the query
				statement3.setString(1, id);

				// Perform the query
				ResultSet rs3 = statement3.executeQuery();


				// Create a JsonObject based on the data we retrieve from rs

				JsonArray resultJsonArray = new JsonArray();
				JsonObject jsonObject = new JsonObject();
				// Iterate through each row of rs
				while (rs3.next()) {
					

					//String starId = rs.getString("starId");
					//String starName = rs.getString("name");
					//String starDob = rs.getString("birthYear");

					String movieId = rs3.getString("id");
					String movieTitle = rs3.getString("title");
					String movieYear = rs3.getString("year");
					String movieDirector = rs3.getString("director");


					//jsonObject.addProperty("star_id", starId);
					//jsonObject.addProperty("star_name", starName);
					//jsonObject.addProperty("star_dob", starDob);
					jsonObject.addProperty("movie_id", movieId);
					jsonObject.addProperty("movie_title", movieTitle);
					jsonObject.addProperty("movie_year", movieYear);
					jsonObject.addProperty("movie_director", movieDirector);
					jsonObject.add("movie_stars", starJsonArray);
					jsonObject.add("movie_genre", genreJsonArray);

					resultJsonArray.add(jsonObject);
				}
				resultJsonArray.add(jsonObject);
				System.out.println(resultJsonArray.toString());
			
            // write JSON string to output
            out.write(resultJsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

			rs1.close();
			statement1.close();
			rs2.close();
			statement2.close();
			rs3.close();
			statement3.close();
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

}
