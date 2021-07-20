

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Random;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class DashboarServlet
 */
@WebServlet(name = "DashboarServlet", urlPatterns = "/api/meta")
public class DashboardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	//@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DashboardServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json"); // Response mime type
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
            System.out.println("-----IN MOVIESERVLET, lookup db is "+ jdbcdb);
            //dataSource = (DataSource) envCtx.lookup("jdbc/TestDB");
            dataSource = (DataSource) envCtx.lookup(jdbcdb);

        	Connection dbcon = dataSource.getConnection();
			
			String query1 = "show tables in moviedb;";
			PreparedStatement statement1 = dbcon.prepareStatement(query1);
			ResultSet rs1 = statement1.executeQuery();
			JsonArray tableJsonArray = new JsonArray();
			while(rs1.next()) {
				JsonObject tableJson = new JsonObject();
				String tableName = rs1.getString("Tables_in_moviedb");
				String query2 = "show fields from ";
				query2+=tableName + ";";
				PreparedStatement statement2 = dbcon.prepareStatement(query2);
				ResultSet rs2 = statement2.executeQuery();
				tableJson.addProperty("tableName", tableName);
				while(rs2.next()) {
					tableJson.addProperty("Field", rs2.getString("Field"));
					tableJson.addProperty("Type", rs2.getString("Type"));
					tableJson.addProperty("Nullable", rs2.getString("Null"));
				}
				tableJsonArray.add(tableJson);
			}
			out.write(tableJsonArray.toString());
			response.setStatus(200);
			rs1.close();
			statement1.close();
			dbcon.close();
			
		}catch (Exception e) {
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
		String type = request.getParameter("type");
		if(type.equals("star")) {
			String name = request.getParameter("name");
			String year = request.getParameter("year");
			String addStarQuery = "CALL insert_star(?,?,?);";
			try {				
//				Class.forName("com.mysql.jdbc.Driver").newInstance();
//				Connection connection = dataSource.getConnection();
				
	            Context initCtx = new InitialContext();
	            Context envCtx = (Context) initCtx.lookup("java:comp/env");
	            if (envCtx == null)
	            	response.getWriter().println("envCtx is NULL");
	            
	            //dataSource = (DataSource) envCtx.lookup("jdbc/TestDB");
	            //WRITE: go to masterDB only
	            dataSource = (DataSource) envCtx.lookup("jdbc/masterDB");

	        	Connection connection = dataSource.getConnection();
				
		        CallableStatement s = connection.prepareCall(addStarQuery);
		        s.setString(1,name);
		        if(!year.equals("")){
		        	s.setString(2, year);
		        }else {
		        	s.setString(2, null);
		        }
		        
		        //s.registerOutParameter(3, Types.VARCHAR);
		        //String starId = s.getString(3);
		        s.execute();
		        s.close();
		        connection.close();
		        //System.out.println(starId);
			} catch(Exception e) {
				e.printStackTrace();
				
			}
//			(InstantiationException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (ClassNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
		}else if(type.equals("movie")) {
			String title = request.getParameter("title");
			String year = request.getParameter("year");
			String dir = request.getParameter("dir");
			String genre = request.getParameter("genre");
			String star = request.getParameter("star");
			String star_year = request.getParameter("syear");
			String addStarQuery = "CALL add_movie(?,?,?,?,?,?,?,?);";
			try {
//				Class.forName("com.mysql.jdbc.Driver").newInstance();
//				Connection connection = dataSource.getConnection();
//				
				Context initCtx = new InitialContext();

                Context envCtx = (Context) initCtx.lookup("java:comp/env");
                if (envCtx == null)
                	response.getWriter().println("envCtx is NULL");

                // Look up our data source
                //WRITE
                dataSource = (DataSource) envCtx.lookup("jdbc/masterDB");
                System.out.println("-----IN CheckOutServlet, lookup db is masterDB");
            	Connection connection = dataSource.getConnection();
            	
		        CallableStatement s = connection.prepareCall(addStarQuery);
		        s.setString(1,title);
		        s.setString(2, year);
		        s.setString(3, dir);
		        s.setString(4, genre);
		        s.setString(5, star);
		        if(!star_year.equals("")){
		        	s.setString(6, star_year);
		        }else {
		        	s.setString(6, null);
		        }
		        
		       
		        s.execute();
		        s.close();
		        connection.close();
		        JsonObject responseJsonObject = new JsonObject();
		        responseJsonObject.addProperty("status", "success");
	            responseJsonObject.addProperty("message", "success");
	            response.getWriter().write(responseJsonObject.toString());
		        //System.out.println(starId);
			}catch(Exception e) {
				e.printStackTrace();
				
			}
//			catch (
//					InstantiationException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (ClassNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch(NamingException e) {
//				e.printStackTrace();
//					
//			}
		}
        
	}

}
