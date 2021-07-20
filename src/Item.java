
public class Item {
	
	private String movie_id;
	private String movie_title;
	private int quantity;

	public Item(String id, String title) {
		this.movie_id = id;
		this.movie_title = title;
		this.quantity = 1;
	}

	public String id() {
		return movie_id;
	}

	public String title() {
		return movie_title;
	}

	public int quantity() {
		return quantity;
	}

	public void updateQuantity(int n) {
		quantity = n;
	}
}
