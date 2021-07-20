import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * This class is declared as LoginServlet in web annotation, 
 * which is mapped to the URL pattern /api/login
 */
@WebServlet(name = "ConfirmServlet", urlPatterns = "/api/confirm")
public class ConfirmServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    

 protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
	 doPost(request, response);
 }
   
 protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
 	HttpSession session = request.getSession();
    User user = (User) session.getAttribute("user");
    ShoppingCart cart = user.getCart();
    
    response.setContentType("application/json"); // Response mime type, correspond to the index.js file
    PrintWriter out = response.getWriter();

    
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
    out.close();
            
 }
}

//protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
// 	HttpSession session = request.getSession();
//    User user = (User) session.getAttribute("user");
//    ShoppingCart cart = user.getCart();
//    
//    response.setContentType("application/json"); // Response mime type, correspond to the index.js file
//    PrintWriter out = response.getWriter();
//
//    
//    JsonObject jsonUserObject = new JsonObject();
//    jsonUserObject.addProperty("user_name", user.getUsername());
//    
//    JsonArray jsonArray = new JsonArray();
//    jsonArray.add(jsonUserObject);
//    
//    try {
//	    Connection dbcon = dataSource.getConnection();
//	    for(Item i:cart.items()) {
//	    	
//	    	JsonObject jsonObject = new JsonObject();
//	    	String query = "SELECT * from sales where sales.customerId = ? and sales.movieId = ?";
//	    	PreparedStatement statement= dbcon.prepareStatement(query);
//	    	statement.setString(1,user.getId());
//	    	statement.setString(2,i.id());
//	    	System.out.println(statement);
//	    	ResultSet rs = statement.executeQuery();
//	    	   	
//	    	if(rs.next()) {
//	    		jsonObject.addProperty("item_title", i.title());
//				jsonObject.addProperty("item_quantity", i.quantity());
//				jsonObject.addProperty("movie_id", i.id());
//				jsonObject.addProperty("sale_id", rs.getString("id"));
//				jsonArray.add(jsonObject);
//	    	}
//	        
//    }
//    
//    }catch(Exception e) {
//    	
////		// write error message JSON object to output
////		JsonObject jsonObject = new JsonObject();
////		jsonObject.addProperty("errorMessage", e.getMessage());
////		out.write(jsonObject.toString());
////
////		// set reponse status to 500 (Internal Server Error)
//		response.setStatus(500);
//
//    }
//    out.close();
//    
//    
//    
//            
// }
//}