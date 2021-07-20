
import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
	private ArrayList<Item> item_list;

	public ShoppingCart() {
		item_list = new ArrayList<Item>();
	}

	public List<Item> items() {
		return item_list;
	}
	
	public int size() {
		int size = 0;
		for (Item i : item_list) {
			size += i.quantity();
		}
		return size;
	}
	public synchronized void add(String id, String title) {
		Item item;
		//increment quantity if item already exists
		for (int i = 0; i < item_list.size(); i++) {
			item = (Item) item_list.get(i);
			if (item.id().equals(id)){
				int quantity = item.quantity();
				item.updateQuantity(quantity+1);
				return;
			}
		}
		//add the item to cart if not 
		Item newItem = new Item(id, title);
		item_list.add(newItem);
	}
	
	public synchronized void update(String id, int quantity) {
		Item item;
		for (int i = 0; i < item_list.size(); i++) {
			item = (Item) item_list.get(i);
			if (item.id().equals(id)) {
				if (quantity < 1) {
					item_list.remove(i);
				} else {
					item.updateQuantity(quantity);
				}
				return;
			}
		}
	}

	public synchronized void remove(String id) {
		Item item;
		for (int i = 0; i < item_list.size(); i++) {
			item = (Item) item_list.get(i);
			if (item.id().equals(id)) {
				item_list.remove(i);
				return;
			}
		}
	}


	public synchronized void removeAll() {
		item_list.clear();
	}
}
