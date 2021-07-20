import java.io.IOException;
import java.sql.Connection;
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


public class ActorXMLParser {
	// Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")  
    private DataSource dataSource;
    //List<Employee> myEmpls;
    
    Document dom;
    HashMap<String,Integer> actormap;

    public ActorXMLParser() {
        actormap = new HashMap<String,Integer>();
    }
    public HashMap<String,Integer> getActorMap(){
    	return actormap;
    }
    
    public void runExample() {

        //parse the xml file and get the dom object
        parseXmlFile();
        
        parseDocument();
      
   
    }

    private void parseXmlFile() {
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            dom = db.parse("src/XMLFiles/actors63.xml");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void parseDocument() {
        //get the root elememt: <casts>
        Element docEle = dom.getDocumentElement();

        //get a nodelist of <dirfilms> elements
        NodeList nlactor = docEle.getElementsByTagName("actor");
        if (nlactor != null && nlactor.getLength() > 0) {
            for (int i = 0; i < nlactor.getLength(); i++) {
 
                Element actor= (Element) nlactor.item(i);  
               
                String actorname = getTextValue(actor, "stagename");
                Integer dob = getIntValue(actor, "dob");
//                System.out.println("actorname:   "+actorname);
//                System.out.println("birthYear:   "+dob);
//                Actor a = new Actor();
//                a.name = actorname;
//                a.year = dob;
                actormap.put(actorname,dob);
         
            }
        }
    }
               
  
    private String getSglTagValue(Element e, String tag) {
        return e.getElementsByTagName(tag).item(0).getFirstChild().getNodeValue();
    }
    
    private Integer getIntValue(Element ele, String tagName) {
        //in production application you would catch the exception
    	try {
        return Integer.parseInt(getTextValue(ele, tagName));
    	}catch(Exception e) {
    		return null;
    	}
    }
    
    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
        }
        return textVal;
    }
//
//    public static void main(String[] args) {
//        //create an instance
//        ActorXMLParser dpe = new ActorXMLParser();
//
//        //call run example
//        System.out.println("run example");
//        dpe.runExample();
//    }


}
