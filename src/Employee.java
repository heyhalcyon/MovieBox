/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class Employee {

    private final String username;
    //private HashMap<String, Integer> cartMap;
    private String id;
    
    public Employee(String username, String id) {
        this.username = username;
        this.id = id;
    }

    
    public String getUsername() { 
    	return this.username; 
    }
    public String getId() {
    	return this.id;
    }
}
