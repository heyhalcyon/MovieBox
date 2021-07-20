
import java.io.IOException;
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

@WebServlet(name = "AutoCompleteServlet", urlPatterns = "/api/ac")
public class AutoCompleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// Create a dataSource which registered in web.xml
   // @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
	
    public AutoCompleteServlet() {
        super();
    }

    /*
     * 
     * Match the query against superheroes and return a JSON response.
     * 
     * For example, if the query is "super":
     * The JSON response look like this:
     * [
     * 	{ "value": "Superman", "data": { "heroID": 101 } },
     * 	{ "value": "Supergirl", "data": { "heroID": 113 } }
     * ]
     * 
     * The format is like this because it can be directly used by the 
     *   JSON auto complete library this example is using. So that you don't have to convert the format.
     *   
     * The response contains a list of suggestions.
     * In each suggestion object, the "value" is the item string shown in the dropdown list,
     *   the "data" object can contain any additional information.
     * 
     * 
     */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			// setup the response json arrray
			JsonArray jsonArray = new JsonArray();
			
			String querystring = request.getParameter("query");
			System.out.println("AC query string");
			System.out.println(querystring);
			// get the query string from parameter
			String[] query = querystring.split(" ");
			
			// return the empty json array if query is null or empty
			if (query == null || query.length == 0) {
				response.getWriter().write(jsonArray.toString());
				return;
			}	
			
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
					+ "UNION (select * from movies where ed(title,?) <=3) LIMIT 10;";
            
			PreparedStatement statement = dbcon.prepareStatement(a);
            
            if(query.length == 1) {
            	statement.setString(1, "+"+query[0]+"*");
            	statement.setString(2, querystring);
            }else {
            	String qstring = "";
            	for(int i = 0; i< query.length; i++) {
                	qstring += "+"+query[i]+"* ";
                }  	
            	statement.setString(1, qstring);
            	statement.setString(2, querystring);        	
            }
            System.out.println("autocomplete query: "+statement.toString());
            
            
//            String q = "(SELECT * FROM movies WHERE MATCH (title) AGAINST ('";//     Chie*' IN BOOLEAN MODE)";
//            for(int i = 0; i< query.length; i++) {
//            	q += "+"+query[i]+"* ";
//            }
//            q += "' IN BOOLEAN MODE) )";
//            q += "UNION ";
//            q += "(select * from movies where ed(title,'"+ request.getParameter("query") +"')<=3) "; 
//            q += "limit 10;";
   
            ResultSet rs = statement.executeQuery();
                 
            while(rs.next()) {
            	String movieId = rs.getString("id");
				String movieTitle = rs.getString("title");
				jsonArray.add(generateJsonObject(movieId, movieTitle));
            }

			response.getWriter().write(jsonArray.toString());
			
			rs.close();
			statement.close();
			dbcon.close();
			response.getWriter();
			return;
		} catch (Exception e) {
			System.out.println(e);
			response.sendError(500, e.getMessage());
		}
	}
	
	/*
	 * Generate the JSON Object from hero to be like this format:
	 * {
	 *   "value": "Iron Man",
	 *   "data": { "heroID": 11 }
	 * }
	 * 
	 */
	private static JsonObject generateJsonObject(String heroID, String heroName) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("value", heroName);
		
		JsonObject additionalDataJsonObject = new JsonObject();
		additionalDataJsonObject.addProperty("movieID", heroID);
		
		jsonObject.add("data", additionalDataJsonObject);
		return jsonObject;
	}


}
