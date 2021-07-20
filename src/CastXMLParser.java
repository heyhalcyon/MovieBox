import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Node;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class CastXMLParser {
	// Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")  
    private DataSource dataSource;
    //List<Employee> myEmpls;
    
    Document dom;
    Connection dbcon;
    HashMap<String, Film> filmmap;
    HashMap<String,Integer> actormap;
    

    public CastXMLParser(HashMap<String, Film> fmap, HashMap<String,Integer> amap) {
    	filmmap = fmap;
    	actormap = amap;
        
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
            dom = db.parse("src/XMLFiles/casts124.xml");

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
        NodeList nldirfilms = docEle.getElementsByTagName("dirfilms");
        if (nldirfilms != null && nldirfilms.getLength() > 0) {
            for (int i = 0; i < nldirfilms.getLength(); i++) {
 
                Element dirfilm= (Element) nldirfilms.item(i);  
                String director = getTextValue(dirfilm, "is");
                
//                System.out.println("director:   "+director);
                
                NodeList nlfilmc = dirfilm.getElementsByTagName("filmc");
                for (int j = 0; j < nlfilmc.getLength(); j++) {
                	Element filmc = (Element) nlfilmc.item(j);
                	
                	NodeList nlm = filmc.getElementsByTagName("m");
                	for(int k = 0; k < nlm.getLength(); k++) {
                		Element m = (Element) nlm.item(k);
                		String fid = getTextValue(m,"f");
                		String mtitle = getTextValue(m, "t");
                		String actor = getTextValue(m,"a");
//                		System.out.println("fid:   "+fid);
//                		System.out.println("mtitle:   "+mtitle);
//                		System.out.println("actor:   "+actor);
//                		
                		if(actor != null) {
                			Actor a = new Actor();
                			a.name = actor;
                			if(actormap.containsKey(actor)){              			
                				Integer actoryear = actormap.get(actor);
                				a.year = actoryear;
	                	
                			}else {
                				a.year = null;
                			}
                		
	                		try {
	                			filmmap.get(fid).actors.add(a);
	                		
	                		}catch(Exception e) {
	                			System.out.println("Inconsistent Data bewteen Cast and Main: Cannot find fid = "+fid+" in main.xml");
	                			continue;
	                		}
                		}   	
                		
                	}
                }
            }
        }
    }
               
     
    private String getSglTagValue(Element e, String tag) {
        return e.getElementsByTagName(tag).item(0).getFirstChild().getNodeValue();
    }
    
    
    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
           el.getFirstChild();
            if(el.getFirstChild()== null) {
            	return null;
            }else {
            	textVal =  el.getFirstChild().getNodeValue();
            }
            
        }
        return textVal;
    }
    
    public HashMap<String,Film> getFilmMap(){
    	return filmmap;
    }
    
    public HashMap<String,Integer> getActorMap(){
    	return actormap;
    }

//    public static void main(String[] args) {
//        //create an instance
//        CastXMLParser dpe = new CastXMLParser();
//
//        //call run example
//        System.out.println("run example");
//        dpe.runExample();
//    }


}
