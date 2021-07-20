import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.jasypt.util.password.StrongPasswordEncryptor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This class is declared as LoginServlet in web annotation, 
 * which is mapped to the URL pattern /api/login
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Create a dataSource which registered in web.xml
    //@Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("username");
        String password = request.getParameter("password");
        

        String userAgent = request.getHeader("User-Agent");
        System.out.println("recieved login request");
        System.out.println("userAgent: " + userAgent);
        
        // only verify recaptcha if login is from Web (not Android)
        if (userAgent != null && !userAgent.contains("Android")) {
            String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
            System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

         // Verify reCAPTCHA
            try {
                RecaptchaVerifyUtils.verify(gRecaptchaResponse);
            } catch (Exception e) {
            	e.printStackTrace();
                return;
            }
            
        }
        
              
        try {
        	String userId = verifyCredentials(email, password);
        	JsonObject responseJsonObject = new JsonObject();
        	if(userId != null) {
        		String sessionId = ((HttpServletRequest) request).getSession().getId();
	            User user = new User(email, userId);
	            request.getSession().setAttribute("user", user);
	            
	            responseJsonObject.addProperty("status", "success");
	            responseJsonObject.addProperty("message", "success");
        	}else {
        		responseJsonObject.addProperty("status", "fail");
	            responseJsonObject.addProperty("message", "incorrect password or email address");
        	}
        	
			response.getWriter().write(responseJsonObject.toString());
			
			
        }catch(Exception e) {
        	e.printStackTrace();
        }
        
    }

	private String verifyCredentials(String email, String password) throws Exception {
			
	
			//Class.forName("com.mysql.jdbc.Driver").newInstance();
			// the following few lines are for connection pooling
	        // Obtain our environment naming context
	
	        Context initCtx = new InitialContext();
	
	        Context envCtx = (Context) initCtx.lookup("java:comp/env");
	
	      //READ
            Random ran = new Random(); 
            int nxt = ran.nextInt(2);
            String jdbcdb = "";
            if( nxt == 1) {
            	//jdbcdb = "jdbc/masterDB";
            	dataSource = (DataSource) envCtx.lookup("jdbc/masterDB");
            	
            }else {
            	//jdbcdb = "jdbc/slaveDB";
            	dataSource = (DataSource) envCtx.lookup("jdbc/slaveDB");
            }
            
            //dataSource = (DataSource) envCtx.lookup("jdbc/TestDB");
           //dataSource = (DataSource) envCtx.lookup(jdbcdb);
			Connection connection = dataSource.getConnection();
			Statement statement = connection.createStatement();
	
			String query = String.format("SELECT * from customers where email='%s'", email);
	
			ResultSet rs = statement.executeQuery(query);
	
			boolean success = false;
			String userId = null;
			if (rs.next()) {
			    // get the encrypted password from the database
				String encryptedPassword = rs.getString("password");
				
				// use the same encryptor to compare the user input password with encrypted password stored in DB
				success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
				if (success)
					userId = rs.getString("id");
			}
			
				
	
			rs.close();
			statement.close();
			connection.close();
			
			System.out.println("verify " + email + " - " + password);
	
			return userId;
		}

}
