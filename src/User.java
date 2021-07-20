import java.util.HashMap;

/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class User {

    private final String username;
    //private HashMap<String, Integer> cartMap;
    private ShoppingCart cart;
    private String id;
    
    public User(String username, String id) {
        this.username = username;
        //this.cartMap = null;
        this.cart = new ShoppingCart();
        this.id = id;
    }
    public ShoppingCart getCart() {
		return cart;
	}
    
    public String getUsername() { 
    	return this.username; 
    }
    public String getId() {
    	return this.id;
    }
}
