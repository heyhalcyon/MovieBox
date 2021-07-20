import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import java.util.HashMap;
import java.util.Map;


import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.sql.DriverManager;

public class XMLParser {
	// Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")  
    private DataSource dataSource;
    //List<Employee> myEmpls;
    Document dom;
    Connection dbcon;
    HashMap<String, Film> filmmap;
    
    public XMLParser() {
        //create a list to hold the employee objects
        //myEmpls = new ArrayList<>();
    	filmmap = new HashMap<String,Film>();
    }

    public void runExample() {

        //parse the xml file and get the dom object
        parseXmlFile();
        
//        System.out.println("parsexml done");
        //Connect to the database

        try {
        	String user = "root";
        	String passwd = "122byitanz";
        	String url = "jdbc:mysql://localhost:3306/moviedb";
        	Class.forName("com.mysql.jdbc.Driver").newInstance();
        	dbcon = DriverManager.getConnection(url,user, passwd);
        	
//        	System.out.println("start parse Document");
        	//get each employee element and create a Employee object
        	parseDocument();     
        }
        catch(Exception e) {
        	e.printStackTrace();
        	return;
        }     
   
    }

    private void parseXmlFile() {
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            dom = db.parse("src/XMLFiles/mains243.xml");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void parseDocument() {
        //get the root elememt: <movies>
        Element docEle = dom.getDocumentElement();

        //get a nodelist of <directorfilms> elements
        NodeList nl = docEle.getElementsByTagName("directorfilms");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
            	
                Element el= (Element) nl.item(i);  //<director> <films>
                Element director = (Element) el.getElementsByTagName("director").item(0);
                String dirname = null;   //FOR DATABASE
                try {
                    dirname = getSglTagValue(director, "dirname");
//                    System.out.println("dirname  "+dirname);
                } catch (Exception e) {
                    continue;
                }
                
                Element films = (Element) el.getElementsByTagName("films").item(0);
                NodeList nlfilms = films.getElementsByTagName("film");
                
                for(int j = 0; j<nlfilms.getLength(); j++) {
                	
                	Element elfilm = (Element) nlfilms.item(j);
                	String title = null; //FOR DB
                	String fid = null;	//FOR DB
                	Integer year = null;
                	
                	try {
                		title = getSglTagValue(elfilm, "t");
//                		System.out.println("title  "+title);
                    } catch (Exception e) {
                    	System.out.println("Inconsistent Error for fid = "+fid+" : movie title cannot be null");
                    	continue;
                    }
                	
                	try {
                		fid = getSglTagValue(elfilm, "fid");
//                		System.out.println("fid  "+fid);
                    } catch (Exception e) {
                    	System.out.println("Inconsistent Error for title = "+title+" : movie id cannot be null");
                    	continue;
                    }
                	
                	try {
                		year = Integer.parseInt(getSglTagValue(elfilm, "year"));
//                		System.out.println("year  "+year);
                    } catch (Exception e) {
                    	System.out.println("Inconsistent Error for fid = "+fid+" : year cannot be null");
                    	continue;
                    }
              	               	
                	NodeList nlcats = elfilm.getElementsByTagName("cat");
                	
                	//HashSet<String> genres= new HashSet<String>();
                	ArrayList<String> genres = new ArrayList<String>();
                	for(int k = 0; k<nlcats.getLength(); k++) {
                		Element elcat = (Element) nlcats.item(k);
                		try {
                			String cat = elcat.getFirstChild().getNodeValue();
                			genres.add(cat);
//                			System.out.println("genre  "+cat);
                		}catch(Exception e) {
                			continue;
                		}
                	}
                	
                	Film f = new Film();
                	f.title = title;
                	f.id = fid;
                	f.year = year;
                	f.dirname = dirname;      
                	f.genres = genres;
                	if(dirname != null & title != null & year != null & fid != null) {
                		filmmap.put(fid, f);
                	}             

            }
            }
        }
    }
    
    private String getSglTagValue(Element e, String tag) {
        return e.getElementsByTagName(tag).item(0).getFirstChild().getNodeValue();
    }
    
    public HashMap<String,Film> getFilmMap(){
    	return filmmap;
    }

//    public static void main(String[] args) {
//        //create an instance
//        XMLParser dpe = new XMLParser();
//
//        //call run example
//        System.out.println("run example");
//        dpe.runExample();
//    }


}
