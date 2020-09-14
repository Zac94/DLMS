package Servers;

import java.util.ArrayList;

import DataClasses.Item;

public class LibHelper {
	public static boolean isEmpty(ArrayList<Item> items) {
		if(items.size() == 0) {
			return true;
		}
		return false;
	}
	
	public static void addToLibrary(ArrayList<Item> items, String itemID, String itemName, int quantity) {
		items.add(new Item(itemID, itemName, quantity, new ArrayList<>(), new ArrayList<>()));
	}
	
	public static void increaseItem(ArrayList<Item> items, String itemID, String itemName, int quantity, Item item, int index) {
		items.set(index, new Item(item.getItemID(), item.getItemName(), quantity + item.getQuantity(), item.getBorrowedBy(), item.getQueue()));
		int queue = items.get(index).getQueue().size();
		if(queue > 0 && items.get(index).getQuantity() > 0) {
			for(int j = 0; j < queue; j++) {
				if(items.get(index).getQuantity() > 0) {
					items.get(index).setQuantity(items.get(index).getQuantity()-1);
					items.get(index).getBorrowedBy().add(items.get(index).getQueue().get(0));
					items.get(index).getQueue().remove(0);
				}
			}
		}
	}
	
	public static boolean isItemIDExist(Item item, String itemID) {
		if(item.getItemID().equals(itemID)) {
			return true;
		}
		return false;
	}
	
	public static boolean isItemNameExist(Item item, String itemName) {
		if(item.getItemName().equals(itemName)) {
			return true;
		}
		return false;
	}
	
	public static boolean isInputQuantityGreaterThanCurrentQuantitiy(int inputQuantity, int currentQuantity) {
		if(inputQuantity > currentQuantity) {
			return true;
		}
		return false;
	}
	
	public static void deleteItem(ArrayList<Item> items, int index) {
		items.remove(index);
	}
	
	public static void decreaseItem(ArrayList<Item> items, Item item, int decreaseQuantity, int index) {
		items.set(index, new Item(item.getItemID(), item.getItemName(), item.getQuantity() - decreaseQuantity, item.getBorrowedBy(), item.getQueue()));
	}
	
	public static boolean isAvailable(Item item) {
		if(item.getQuantity() > 0) {
			return true;
		}
		return false;
	}
	
	public static String listItem(ArrayList<Item> items) {
		String result = "";
		for(int i = 0; i < items.size(); i++) {
			result += items.get(i).getItemID() + " " + items.get(i).getItemName() + " " + items.get(i).getQuantity();
			if(i != items.size() - 1){
				result += ", ";
			}
		}
		return result;
	}
	
	public static boolean isBorrowedByUser(Item item, String userID) {
		if(item.getBorrowedBy().contains(userID)) {
			return true;
		}
		return false;
	}
	
	public static void borrowItem(ArrayList<Item> items, String userID, int index) {
		items.set(index, new Item(items.get(index).getItemID(), items.get(index).getItemName(), items.get(index).getQuantity() - 1, items.get(index).getBorrowedBy(), items.get(index).getQueue()));
		items.get(index).getBorrowedBy().add(userID);
	}
	
	public static void returnItem(ArrayList<Item> items, String userID, int index) {
		items.get(index).getBorrowedBy().remove(userID);
		items.get(index).setQuantity(items.get(index).getQuantity()+1);
		if(items.get(index).getQueue().size() > 0) {
			items.get(index).getBorrowedBy().add(items.get(index).getQueue().get(0));
			items.get(index).getQueue().remove(0);
			items.get(index).setQuantity(items.get(index).getQuantity()-1);
		}
	}
	
	public static boolean isAlreadyInQueue(Item item, String userID) {
		if(item.getQueue().contains(userID)) {
			return true;
		}
		return false;
	}
	
	public static void addToQueue(Item item, String userID) {
		item.getQueue().add(userID);
	}
	
	public static void removeFromQueue(Item item, String userID) {
		item.getQueue().remove(userID);
	}
	
	public static String findItemInfo(Item item) {
		return item.getItemID() + " " + Integer.toString(item.getQuantity());
	}
}
