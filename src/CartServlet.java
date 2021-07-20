import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;


/**
 * This CartServlet is declared in the web annotation below, 
 * which is mapped to the URL pattern /api/cart.
 */
@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
   
    //@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

    /**
     * handles GET requests to add and show the item list information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {       
    	HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        response.setContentType("application/json"); // Response mime type, correspond to the index.js file
        PrintWriter out = response.getWriter();   
        
        ShoppingCart cart = user.getCart();       
        	
	    String movie_id = request.getParameter("id");
	       
	    String req = request.getParameter("req");
	        
	    String qty = request.getParameter("qty");
	       
	        if(movie_id != null){
	        	
	        	
	        	int quantity = Integer.parseInt(qty);
	        	
	        	if(req.equals("remove")) {     		
	        		cart.remove(movie_id);
	        	}
	        	if(req.equals("update")){
	        		cart.update(movie_id, quantity);
	        	}
	        	if(req.equals("add")) {
	        		try {
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
	                    
	                    //dataSource = (DataSource) envCtx.lookup("jdbc/TestDB");
	                    dataSource = (DataSource) envCtx.lookup(jdbcdb);
	                    
	        			Connection dbcon = dataSource.getConnection();
	        			
	        			String query1 = "select * from movies as m where m.id = ?";
	        			PreparedStatement statement1 = dbcon.prepareStatement(query1);
	        			statement1.setString(1,movie_id);
	        			ResultSet rs1 = statement1.executeQuery();
	        			if (rs1.next()) {
	        				String movie_title = rs1.getString("title");
	        				cart.add(movie_id, movie_title);
	        			}else {
	        				//if no such movie id in the database
	        				JsonObject jsonObject = new JsonObject();
	        				jsonObject.addProperty("errorMessage","movie id doesn't exsit");
	        				out.write(jsonObject.toString());
	        			}
	        			
	        		}catch(Exception e){
	        			// if unable to connect to database
	        			response.setStatus(500);
	        		}
	        		
	        	}  
	
	        }
        
	        JsonObject jsonUserObject = new JsonObject();
	        jsonUserObject.addProperty("user_name", user.getUsername());
	        
	        JsonArray jsonArray = new JsonArray();
	        jsonArray.add(jsonUserObject);
	        
	        for(Item i:cart.items()) {
	        	JsonObject jsonObject = new JsonObject();
	            jsonObject.addProperty("item_title", i.title());
	 			jsonObject.addProperty("item_quantity", i.quantity());
	 			jsonObject.addProperty("movie_id", i.id());
	 			jsonArray.add(jsonObject);
	        }
	        out.write(jsonArray.toString());
	        
//        catch(Exception e) {
//        	JsonObject jsonObject = new JsonObject();
//        	jsonObject.addProperty("errorMessage",e.getMessage());
//        	out.write(jsonObject.toString());
//        	response.setStatus(500);
//        }
        out.close();
    }
}
