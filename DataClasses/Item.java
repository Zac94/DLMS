package DataClasses;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Item implements Serializable {
	private String itemName;
	private String itemID;
	private int quantity;
	private ArrayList<String> borrowedBy;
	private ArrayList<String> queue;
	
	public ArrayList<String> getQueue(){
		return queue;
	}
	
	public ArrayList<String> getBorrowedBy(){
		return borrowedBy;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public String getItemID() {
		return itemID;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	public void setItemID(String itemID) {
		this.itemID = itemID;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public void setBorrowedBy(ArrayList<String> borrowedBy) {
		this.borrowedBy = borrowedBy;
	}
	
	public void setQueue(ArrayList<String> queue) {
		this.queue = queue;
	}
	
	public Item(String itemID, String itemName, int quantity, ArrayList<String> borrowedBy, ArrayList<String> queue) {
		this.itemID = itemID;
		this.itemName = itemName;
		this.quantity = quantity;
		this.borrowedBy = borrowedBy;
		this.queue = queue;
	}
}