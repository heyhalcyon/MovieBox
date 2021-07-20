import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.text.ParseException;

/**
 * This class is declared as LoginServlet in web annotation, 
 * which is mapped to the URL pattern /api/login
 */
@WebServlet(name = "CheckOutServlet", urlPatterns = "/api/checkout")
public class CheckOutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Create a dataSource which registered in web.xml
   // @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    
//
// protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//	 doPost(request, response);
// }
   
 protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        ShoppingCart cart = user.getCart();
        boolean infoValid = false;
        
        JsonObject responseJsonObject = new JsonObject();
        
        if(request.getParameter("placed")!= null){
        	String first_name = request.getParameter("FirstName");
        	String last_name = request.getParameter("LastName");
        	String card_number = request.getParameter("CardNumber");
        	String exp_date = request.getParameter("ExpDate");
        	System.out.println(exp_date);
        	
//        	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
//        	java.util.Date date = sdf1.parse(exp_date);
//        	java.sql.Date sql_exp_date = new java.sql.Date(date.getTime());  
        
//			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//			String date = simpleDateFormat.format(new Date());
        	
        	//SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
//        	try {
//        	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
//        	java.util.Date date = sdf1.parse(exp_date);
//        	java.sql.Date sql_exp_date = new java.sql.Date(date.getTime());  
//        	}catch(ParseException e) {
//        		e.printStackTrace();
//        	}
            try {
            	//Class.forName("com.mysql.jdbc.Driver").newInstance();
            	
            	// the following few lines are for connection pooling
                // Obtain our environment naming context

                Context initCtx = new InitialContext();

                Context envCtx = (Context) initCtx.lookup("java:comp/env");
                if (envCtx == null)
                	response.getWriter().println("envCtx is NULL");

                // Look up our data source
                //WRITE
                dataSource = (DataSource) envCtx.lookup("jdbc/masterDB");
                System.out.println("-----IN CheckOutServlet, lookup db is masterDB");
            	Connection dbcon = dataSource.getConnection();
            	
            	String query1 = "select * from creditcards cd, customers c "+
            					"where cd.id = c.ccId and cd.id = ? and cd.firstName = ? and cd.lastName = ? and cd.expiration = ?";
            	PreparedStatement statement1 = dbcon.prepareStatement(query1);  	
            	statement1.setString(1, card_number);
            	statement1.setString(2, first_name);
            	statement1.setString(3, last_name);
            	statement1.setString(4, exp_date);
            	//statement1.setDate(4, sql_exp_date);
            	ResultSet rs1 = statement1.executeQuery();
            	
            	if(rs1.next()) {
            		
            		String query2 = "insert into sales (customerId, movieId, saleDate) VALUES (?, ?, ?)";
            		PreparedStatement statement2 = dbcon.prepareStatement(query2);
            		statement2.setString(1, user.getId());
            		for(Item i: cart.items()) {
            			int qty = i.quantity();
            			for(int j=0;j<qty;j++) {
            				statement2.setString(2, i.id());
            				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
 					        String today_date = dateFormat.format(Calendar.getInstance().getTime());
 					        statement2.setString(3, today_date);
							statement2.executeUpdate();
            			}
            		}
            		responseJsonObject.addProperty("status", "success");
		            responseJsonObject.addProperty("message", "success");
            		
            		statement2.close();
            	}
            	else {
            		responseJsonObject.addProperty("status", "fail");
		            responseJsonObject.addProperty("message", "incorrect check out info");
            	}
            	
            	//String result = responseJsonObject.toString();
            	//response.getWriter().write(result);
            	response.getWriter().write(responseJsonObject.toString());
            	
            	
            	rs1.close();
    			statement1.close();
    			dbcon.close();    		
            }catch(Exception e) {
            	response.setStatus(500);
            }
      
            
        }
}
}
