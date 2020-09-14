package LibraryImplementation;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import DataClasses.Item;
import DataClasses.Logging;
import Interface.LibraryInterface;
import Servers.ConcordiaServer;
import Servers.LibHelper;
import Servers.McGillServer;
import Servers.MontrealServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Properties;

@WebService(endpointInterface = "Interface.LibraryInterface")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class LibraryImpl implements LibraryInterface{
	
	@Override
	public synchronized String addItem(String managerID, String itemID, String itemName, int quantity) {
		Logging log = new Logging();
		String message = "Failed to add item.";
		if(managerID.substring(0, 3).equals("CON")) {
			if(!itemID.matches("^CON\\d{4}")) {
				log.writeToSvLog("Concordia", "add an item(", managerID, itemID, "failed", message);
				return message.trim();
			}
			if(LibHelper.isEmpty(ConcordiaServer.ConcordiaItem)) {
				LibHelper.addToLibrary(ConcordiaServer.ConcordiaItem, itemID, itemName, quantity);
				message = "Item added successfully";
				log.writeToSvLog("Concordia", "add an item(", managerID, itemID, "success", message);
				return message.trim();
			}
			for(int i = 0; i < ConcordiaServer.ConcordiaItem.size(); i++) {
				Item item = ConcordiaServer.ConcordiaItem.get(i);
				if(LibHelper.isItemIDExist(item, itemID) || LibHelper.isItemNameExist(item, itemName)) {
					LibHelper.increaseItem(ConcordiaServer.ConcordiaItem, itemID, itemName, quantity, item, i);
					System.out.println(ConcordiaServer.ConcordiaItem.get(i).getBorrowedBy());
					message = "Item added successfully";
					log.writeToSvLog("Concordia", "add an item(", managerID, itemID, "success", message);
					return message.trim();
				}
			}
			LibHelper.addToLibrary(ConcordiaServer.ConcordiaItem, itemID, itemName, quantity);
			message = "Item added successfully";
			log.writeToSvLog("Concordia", "add an item(", managerID, itemID, "success", message);
			return message.trim();
		}
		
		if(managerID.substring(0, 3).equals("MCG")) {
			if(!itemID.matches("^MCG\\d{4}")) {
				log.writeToSvLog("McGill", "add an item(", managerID, itemID, "fail", message);
				return message.trim();
			}
			if(LibHelper.isEmpty(McGillServer.McGillItem)) {
				LibHelper.addToLibrary(McGillServer.McGillItem, itemID, itemName, quantity);
				message = "Item added successfully";
				log.writeToSvLog("McGill", "add an item(", managerID, itemID, "success", message);
				return message.trim();
			}
			for(int i = 0; i < McGillServer.McGillItem.size(); i++) {
				Item item = McGillServer.McGillItem.get(i);
				if(LibHelper.isItemIDExist(item, itemID) || LibHelper.isItemNameExist(item, itemName)) {
					LibHelper.increaseItem(McGillServer.McGillItem, itemID, itemName, quantity, item, i);
					message = "Item added successfully";
					log.writeToSvLog("McGill", "add an item(", managerID, itemID, "success", message);
					return message.trim();
				}
			}
			LibHelper.addToLibrary(McGillServer.McGillItem, itemID, itemName, quantity);
			message = "Item added successfully";
			log.writeToSvLog("McGill", "add an item", managerID, itemID, "success", message);
			return message.trim();
		}
		
		if(managerID.substring(0, 3).equals("MON")) {
			if(!itemID.matches("^MON\\d{4}")) {
				log.writeToSvLog("Montreal", "add an item(", managerID, itemID, "fail", message);
				return message.trim();
			}
			if(LibHelper.isEmpty(MontrealServer.MontrealItem)) {
				LibHelper.addToLibrary(MontrealServer.MontrealItem, itemID, itemName, quantity);
				message = "Item added successfully";
				log.writeToSvLog("Montreal", "add an item(", managerID, itemID, "success", message);
				return message.trim();
			}
			for(int i = 0; i < MontrealServer.MontrealItem.size(); i++) {
				Item item = MontrealServer.MontrealItem.get(i);
				if(LibHelper.isItemIDExist(item, itemID) || LibHelper.isItemNameExist(item, itemName)) {
					LibHelper.increaseItem(MontrealServer.MontrealItem, itemID, itemName, quantity, item, i);
					message = "Item added successfully";
					log.writeToSvLog("Montreal", "add an item(", managerID, itemID, "success", message);
					return message.trim();
				}
			}
			LibHelper.addToLibrary(MontrealServer.MontrealItem, itemID, itemName, quantity);
			message = "Item added successfully";
			log.writeToSvLog("Montreal", "add an item(", managerID, itemID, "success", message);
			return message.trim();
		}
		return message.trim();
		
	}

	@Override
	public synchronized String removeItem(String managerID, String itemID, int quantity) {
		Logging log = new Logging();
		if(managerID.substring(0, 3).equals("CON")) {
			String message = "Failed to remove item.";
			if(!itemID.matches("^CON\\d{4}")) {
				log.writeToSvLog("Concordia", "remove an item(", managerID, itemID, "fail", "Item does not exist");
				return "Item does not exist";
			}
			if(LibHelper.isEmpty(ConcordiaServer.ConcordiaItem)) {
				log.writeToSvLog("Concordia", "remove an item(", managerID, itemID, "fail", "No item to remove");
				return "No item to remove";
			}
			for(int i = 0; i < ConcordiaServer.ConcordiaItem.size(); i++) {
				Item item = ConcordiaServer.ConcordiaItem.get(i);
				if(LibHelper.isItemIDExist(item, itemID) && LibHelper.isInputQuantityGreaterThanCurrentQuantitiy(quantity, item.getQuantity())) {
					message = "Cannot decrease, input number is higher than quantity available.";
					log.writeToSvLog("Concordia", "remove an item(", managerID, itemID, "fail", message);
					return message.trim();
				}else if (LibHelper.isItemIDExist(item, itemID) && quantity == -1) {
					LibHelper.deleteItem(ConcordiaServer.ConcordiaItem, i);
					message = "Item removed successfully";
					log.writeToSvLog("Concordia", "remove an item(", managerID, itemID, "success", message);
					return message.trim();
				}else if(LibHelper.isItemIDExist(item, itemID) && !LibHelper.isInputQuantityGreaterThanCurrentQuantitiy(quantity, item.getQuantity())) {
					LibHelper.decreaseItem(ConcordiaServer.ConcordiaItem, item, quantity, i);
					message = "Item decreased successfully";
					log.writeToSvLog("Concordia", "remove an item(", managerID, itemID, "success", message);
					return message.trim();
				} else if (LibHelper.isItemIDExist(item, itemID) && !LibHelper.isAvailable(item)) {
					message = "Cannot decrease as item quantity is already 0.";
					log.writeToSvLog("Concordia", "remove an item(", managerID, itemID, "fail", message);
					return message.trim();
				} 
			}
			return message.trim();
		}
		if(managerID.substring(0, 3).equals("MCG")) {
			String message = "Failed to remove item.";
			if(!itemID.matches("^MCG\\d{4}")) {
				log.writeToSvLog("McGill", "remove an item(", managerID, itemID, "fail", "Item does not exist");
				return "Item does not exist";
			}
			if(LibHelper.isEmpty(McGillServer.McGillItem)) {
				log.writeToSvLog("McGill", "remove an item(", managerID, itemID, "fail", "No item to remove");
				return "No item to remove.";
			}
			for(int i = 0; i < McGillServer.McGillItem.size(); i++) {
				Item item = McGillServer.McGillItem.get(i);
				if(LibHelper.isItemIDExist(item, itemID) && LibHelper.isInputQuantityGreaterThanCurrentQuantitiy(quantity, item.getQuantity())) {
					message = "Cannot decrease, input number is higher than quantity available.";
					log.writeToSvLog("McGill", "remove an item(", managerID, itemID, "fail", message);
					return message.trim();
				}else if (LibHelper.isItemIDExist(item, itemID) && quantity == -1) {
					LibHelper.deleteItem(McGillServer.McGillItem, i);
					message = "Item removed successfully";
					log.writeToSvLog("McGill", "remove an item(", managerID, itemID, "success", message);
					return message.trim();
				}else if(LibHelper.isItemIDExist(item, itemID) && !LibHelper.isInputQuantityGreaterThanCurrentQuantitiy(quantity, item.getQuantity())) {
					LibHelper.decreaseItem(McGillServer.McGillItem, item, quantity, i);
					message = "Item decreased successfully";
					log.writeToSvLog("McGill", "remove an item(", managerID, itemID, "success", message);
					return message.trim();
				} else if (LibHelper.isItemIDExist(item, itemID) && !LibHelper.isAvailable(item)) {
					message = "Cannot decrease as item quantity is already 0.";
					log.writeToSvLog("McGill", "remove an item(", managerID, itemID, "fail", message);
					return message.trim();
				}
			}
			return message.trim();
		}
		if(managerID.substring(0, 3).equals("MON")) {
			String message = "Failed to remove item.";
			if(!itemID.matches("^MON\\d{4}")) {
				log.writeToSvLog("Montreal", "remove an item(", managerID, itemID, "fail", "Item does not exist");
				return "Item does not exist.";
			}
			if(LibHelper.isEmpty(MontrealServer.MontrealItem)) {
				log.writeToSvLog("Montreal", "remove an item(", managerID, itemID, "fail", "No item to return");
				return "No item to return";
			}
			for(int i = 0; i < MontrealServer.MontrealItem.size(); i++) {
				Item item = MontrealServer.MontrealItem.get(i);
				if(LibHelper.isItemIDExist(item, itemID) && LibHelper.isInputQuantityGreaterThanCurrentQuantitiy(quantity, item.getQuantity())) {
					message = "Cannot decrease, input number is higher than quantity available.";
					log.writeToSvLog("Montreal", "remove an item(", managerID, itemID, "fail", message);
					return message.trim();
				}else if (LibHelper.isItemIDExist(item, itemID) && quantity == -1) {
					LibHelper.deleteItem(MontrealServer.MontrealItem, i);
					message = "Item removed successfully";
					log.writeToSvLog("Montreal", "remove an item(", managerID, itemID, "success", message);
					return message.trim();
				}else if(LibHelper.isItemIDExist(item, itemID) && !LibHelper.isInputQuantityGreaterThanCurrentQuantitiy(quantity, item.getQuantity())) {
					LibHelper.decreaseItem(MontrealServer.MontrealItem, item, quantity, i);
					message = "Item decreased successfully";
					log.writeToSvLog("Montreal", "remove an item(", managerID, itemID, "success", message);
					return message.trim();
				} else if (LibHelper.isItemIDExist(item, itemID) && !LibHelper.isAvailable(item)) {
					message = "Cannot decrease as item quantity is already 0.";
					log.writeToSvLog("Montreal", "remove an item(", managerID, itemID, "fail", message);
					return message.trim();
				} 
			}
			return message.trim();
		}
		return "Failed to remove item.";
	}

	@Override
	public String listItemAvailability(String managerID) {
		String result = "";
		if(managerID.substring(0, 3).equals("CON")) {
			result = LibHelper.listItem(ConcordiaServer.ConcordiaItem);
			return result.trim();
		}
		if(managerID.substring(0, 3).equals("MCG")) {
			result = LibHelper.listItem(McGillServer.McGillItem);
			return result.trim();
		}
		if(managerID.substring(0, 3).equals("MON")) {
			result = LibHelper.listItem(MontrealServer.MontrealItem);
			return result.trim();
		}
		return result.trim();
	}

	@Override
	public synchronized String borrowItem(String userID, String itemID) {
		Logging log = new Logging();
		String result = "";
		if(userID.substring(0, 3).equals("CON")) {
			if(itemID.matches("^CON\\d{4}")) {
				for(int i = 0; i < ConcordiaServer.ConcordiaItem.size(); i++) {
					if(LibHelper.isItemIDExist(ConcordiaServer.ConcordiaItem.get(i), itemID) && LibHelper.isBorrowedByUser(ConcordiaServer.ConcordiaItem.get(i), userID)) {
						result = "Already borrowed.";
						log.writeToSvLog("Concordia", "borrow an item(", userID, itemID, "fail", result);
						return result.trim();
					}
					if(LibHelper.isItemIDExist(ConcordiaServer.ConcordiaItem.get(i), itemID) && LibHelper.isAvailable(ConcordiaServer.ConcordiaItem.get(i))) {
						LibHelper.borrowItem(ConcordiaServer.ConcordiaItem, userID, i);
						result = "Borrowed successfully";
						log.writeToSvLog("Concordia", "borrow an item(", userID, itemID, "success", result);
						return result.trim();
					}else if(LibHelper.isItemIDExist(ConcordiaServer.ConcordiaItem.get(i), itemID) && !LibHelper.isAvailable(ConcordiaServer.ConcordiaItem.get(i))) {
						result = "Out of Stock.";
						log.writeToSvLog("Concordia", "borrow an item(", userID, itemID, "fail", result);
						return result.trim();
					}
				}
			}
			if(itemID.matches("^MCG\\d{4}")) {
				result = requestBorrowBook(userID, itemID, 8100).trim();
				if(result.equals("")) {
					return "Item not found.";
				}
				return result.trim();
			}
			if(itemID.matches("^MON\\d{4}")) {
				result = requestBorrowBook(userID, itemID, 9100).trim();
				if(result.equals("")) {
					return "Item not found.";
				}
				return result.trim();
			}
		}
		if(userID.substring(0, 3).equals("MCG")) {
			if(itemID.matches("^MCG\\d{4}")) {
				for(int i = 0; i < McGillServer.McGillItem.size(); i++) {
					if(LibHelper.isItemIDExist(McGillServer.McGillItem.get(i), itemID) && LibHelper.isBorrowedByUser(McGillServer.McGillItem.get(i), userID)) {
						result = "Already borrowed.";
						log.writeToSvLog("McGill", "borrow an item(", userID, itemID, "fail", result);
						return result.trim();
					}
					if(LibHelper.isItemIDExist(McGillServer.McGillItem.get(i), itemID) && LibHelper.isAvailable(McGillServer.McGillItem.get(i))) {
						LibHelper.borrowItem(McGillServer.McGillItem, userID, i);
						result = "Borrowed successfully";
						log.writeToSvLog("McGill", "borrow an item(", userID, itemID, "success", result);
						return result.trim();
					}else if(LibHelper.isItemIDExist(McGillServer.McGillItem.get(i), itemID) && !LibHelper.isAvailable(McGillServer.McGillItem.get(i))) {
						result = "Out of Stock.";
						log.writeToSvLog("McGill", "borrow an item(", userID, itemID, "fail", result);
						return result.trim();
					}
				}
			}
			
			if(itemID.matches("^CON\\d{4}")) {
				result = requestBorrowBook(userID, itemID, 7100).trim();
				if(result.equals("")) {
					return "Item not found.";
				}
				return result.trim();
			}
			
			if(itemID.matches("^MON\\d{4}")) {
				result = requestBorrowBook(userID, itemID, 9100).trim();
				if(result.equals("")) {
					return "Item not found.";
				}
				return result.trim();
			}
		}
		if(userID.substring(0, 3).equals("MON")) {
			if(itemID.matches("^MON\\d{4}")) {
				for(int i = 0; i < MontrealServer.MontrealItem.size(); i++) {
					if(LibHelper.isItemIDExist(MontrealServer.MontrealItem.get(i), itemID) && LibHelper.isBorrowedByUser(MontrealServer.MontrealItem.get(i), userID)) {
						result = "Already borrowed.";
						log.writeToSvLog("Montreal", "borrow an item(", userID, itemID, "fail", result);
						return result.trim();
					}
					if(LibHelper.isItemIDExist(MontrealServer.MontrealItem.get(i), itemID) && LibHelper.isAvailable(MontrealServer.MontrealItem.get(i))) {
						LibHelper.borrowItem(MontrealServer.MontrealItem, userID, i);
						result = "Borrowed successfully";
						log.writeToSvLog("Montreal", "borrow an item(", userID, itemID, "success", result);
						return result.trim();
					}else if(LibHelper.isItemIDExist(MontrealServer.MontrealItem.get(i), itemID) && !LibHelper.isAvailable(MontrealServer.MontrealItem.get(i))) {
						result = "Out of Stock.";
						log.writeToSvLog("Montreal", "borrow an item(", userID, itemID, "fail", result);
						return result.trim();
					}
				}
			}
			
			if(itemID.matches("^CON\\d{4}")) {
				result = requestBorrowBook(userID, itemID, 7100).trim();
				if(result.equals("")) {
					return "Item not found.";
				}
				return result.trim();
			}
			
			if(itemID.matches("^MCG\\d{4}")) {
				result = requestBorrowBook(userID, itemID, 8100).trim();
				if(result.equals("")) {
					return "Item not found.";
				}
				return result.trim();
			}
		}
		return "Wrong item ID.";
	}

	@Override
	public String findItem(String userID, String itemName) {
		String result = "";
		if(userID.substring(0, 3).equals("CON")) {
			for(int i = 0; i < ConcordiaServer.ConcordiaItem.size(); i++) {
				Item item = ConcordiaServer.ConcordiaItem.get(i);
				if(LibHelper.isItemNameExist(item, itemName)) {
					result = LibHelper.findItemInfo(item);
				}
			}
			if(!requestBookInfo(itemName,8000).trim().equals("")) {
				if(!result.equals("")){
					result = result + ", " + requestBookInfo(itemName, 8000).trim();
				}else {
					result = requestBookInfo(itemName, 8000).trim();
				}
			}
			if(!requestBookInfo(itemName,9000).trim().equals("")) {
				if(!result.equals("")){
					result = result + ", " + requestBookInfo(itemName, 9000).trim();
				}else {
					result = requestBookInfo(itemName, 9000).trim();
				}
			}
			if(result.equals("")) {
				return "Item not found.";
			}
			return result.trim();	
		}
		if(userID.substring(0, 3).equals("MCG")) {
			for(int i = 0; i < McGillServer.McGillItem.size(); i++) {
				Item item = McGillServer.McGillItem.get(i);
				if(LibHelper.isItemNameExist(item, itemName)) {
					result = LibHelper.findItemInfo(item);
				}
			}
			if(!requestBookInfo(itemName,7000).trim().equals("")) {
				if(!result.equals("")) {
					result = result + ", " + requestBookInfo(itemName, 7000).trim();
				}else {
					result = requestBookInfo(itemName, 7000).trim();
				}
			}
			if(!requestBookInfo(itemName,9000).trim().equals("")) {
				if(!result.equals("")){
					result = result + ", " + requestBookInfo(itemName, 9000).trim();
				}else {
					result = requestBookInfo(itemName, 9000).trim();
				}
			}
			if(result.equals("")) {
				return "Item not found.";
			}
			return result.trim();	
		}
		if(userID.substring(0, 3).equals("MON")) {
			for(int i = 0; i < MontrealServer.MontrealItem.size(); i++) {
				Item item = MontrealServer.MontrealItem.get(i);
				if(LibHelper.isItemNameExist(item, itemName)) {
					result = LibHelper.findItemInfo(item);
				}
			}
			if(!requestBookInfo(itemName,7000).trim().equals("")) {
				if(!result.equals("")) {
					result = result + ", " + requestBookInfo(itemName, 7000).trim();
				}else {
					result = requestBookInfo(itemName, 7000).trim();
				}
			}
			if(!requestBookInfo(itemName,8000).trim().equals("")) {
				if(!result.equals("")){
					result = result + ", " + requestBookInfo(itemName, 8000).trim();
				}else {
					result = requestBookInfo(itemName, 8000).trim();
				}
			}
			if(result.equals("")) {
				return "Item not found.";
			}
			return result.trim();
		}
		return result.trim();
	}

	@Override
	public synchronized String returnItem(String userID, String itemID) {
		Logging log = new Logging();
		String result = "";
		if(userID.substring(0, 3).equals("CON")) {
			if(itemID.matches("^CON\\d{4}")) {
				for(int i = 0; i < ConcordiaServer.ConcordiaItem.size(); i++) {
					if(LibHelper.isBorrowedByUser(ConcordiaServer.ConcordiaItem.get(i), userID) && LibHelper.isItemIDExist(ConcordiaServer.ConcordiaItem.get(i), itemID)) {
						LibHelper.returnItem(ConcordiaServer.ConcordiaItem, userID, i);
						result = "Item returned.";
						log.writeToSvLog("Concordia", "return an item(", userID, itemID, "success", result);
						return result;
					}else if(!LibHelper.isBorrowedByUser(ConcordiaServer.ConcordiaItem.get(i), userID) && LibHelper.isItemIDExist(ConcordiaServer.ConcordiaItem.get(i), itemID)) {
						result = "You did not borrow this item";
						log.writeToSvLog("Concordia", "return an item(", userID, itemID, "fail", result);
						return result;
					}
				}
			}
			if(itemID.matches("^MCG\\d{4}")) {
				result = requestReturnBook(userID, itemID, 8300);
				return result.trim();
			}
			
			if(itemID.matches("^MON\\d{4}")) {
				result = requestReturnBook(userID, itemID, 9300);
				return result.trim();
			}
			log.writeToSvLog("Concordia", "return an item(", userID, itemID, "fail", "Item does not exist");
			return "Item does not exist";
		}
		if(userID.substring(0, 3).equals("MCG")) {
			for(int i = 0; i < McGillServer.McGillItem.size(); i++) {
				if(LibHelper.isBorrowedByUser(McGillServer.McGillItem.get(i), userID) && LibHelper.isItemIDExist(McGillServer.McGillItem.get(i), itemID)) {
					LibHelper.returnItem(McGillServer.McGillItem, userID, i);
					result = "Item returned.";
					log.writeToSvLog("McGill", "return an item(", userID, itemID, "success", result);
					return result.trim();
				}else if(!LibHelper.isBorrowedByUser(McGillServer.McGillItem.get(i), userID) && LibHelper.isItemIDExist(McGillServer.McGillItem.get(i), itemID)) {
					result = "You did not borrow this item";
					log.writeToSvLog("McGill", "return an item(", userID, itemID, "fail", result);
					return result.trim();
				}
			}
			if(itemID.matches("^CON\\d{4}")) {
				result = requestReturnBook(userID, itemID, 7300);
				return result.trim().trim();
			}
			
			if(itemID.matches("^MON\\d{4}")) {
				result = requestReturnBook(userID, itemID, 9300);
				return result.trim().trim();
			}
			log.writeToSvLog("McGill", "return an item(", userID, itemID, "fail", "Item does not exist");
			return "Item does not exist";
		}
		if(userID.substring(0, 3).equals("MON")) {
			for(int i = 0; i < MontrealServer.MontrealItem.size(); i++) {
				if(LibHelper.isBorrowedByUser(MontrealServer.MontrealItem.get(i), userID) && LibHelper.isItemIDExist(MontrealServer.MontrealItem.get(i), itemID)) {
					LibHelper.returnItem(MontrealServer.MontrealItem, userID, i);
					result = "Item returned.";
					log.writeToSvLog("Montreal", "return an item(", userID, itemID, "success", result);
					return result.trim();
				}else if(!LibHelper.isBorrowedByUser(MontrealServer.MontrealItem.get(i), userID) && LibHelper.isItemIDExist(MontrealServer.MontrealItem.get(i), itemID)) {
					result = "You did not borrow this item";
					log.writeToSvLog("Montreal", "return an item(", userID, itemID, "fail", result);
					return result.trim();
				}
			}
			if(itemID.matches("^CON\\d{4}")) {
				result = requestReturnBook(userID, itemID, 7300);
				return result.trim();
			}
			
			if(itemID.matches("^MCG\\d{4}")) {
				result = requestReturnBook(userID, itemID, 8300);
				return result.trim();
			}
			log.writeToSvLog("Montreal", "return an item(", userID, itemID, "fail", "Item does not exist");
			return "Item does not exist";
		}
		return "Item does not exist";
	}

	@Override
	public synchronized String addToQueue(String userID, String itemID) {
		String result = "";
		if(userID.substring(0, 3).equals("CON")) {
			if(itemID.matches("^CON\\d{4}")) {
				for(int i = 0; i < ConcordiaServer.ConcordiaItem.size(); i++) {
					if(LibHelper.isItemIDExist(ConcordiaServer.ConcordiaItem.get(i), itemID) && LibHelper.isAlreadyInQueue(ConcordiaServer.ConcordiaItem.get(i), userID)) {
						result = "Already in queue for this item.";
						return result.trim();
					}
					if(LibHelper.isItemIDExist(ConcordiaServer.ConcordiaItem.get(i), itemID)) {
						LibHelper.addToQueue(ConcordiaServer.ConcordiaItem.get(i), userID);
						result = "Added to queue.";
						return result.trim();
					}
				}
			}
			if(itemID.matches("^MCG\\d{4}")) {
				result = requestAddToQueue(userID, itemID, 8200);
				return result.trim();
			}
			if(itemID.matches("^MON\\d{4}")) {
				result = requestAddToQueue(userID, itemID, 9200);
				return result.trim();
			}
			return result.trim();
		}
		if(userID.substring(0, 3).equals("MCG")) {
			if(itemID.matches("^MCG\\d{4}")) {
				for(int i = 0; i < McGillServer.McGillItem.size(); i++) {
					if(LibHelper.isItemIDExist(McGillServer.McGillItem.get(i), itemID) && LibHelper.isAlreadyInQueue(McGillServer.McGillItem.get(i), userID)) {
						result = "Already in queue for this item.";
						return result.trim();
					}
					if(LibHelper.isItemIDExist(McGillServer.McGillItem.get(i), itemID)) {
						LibHelper.addToQueue(McGillServer.McGillItem.get(i), userID);
						result = "Added to queue.";
						return result.trim();
					}
				}
			}
			if(itemID.matches("^CON\\d{4}")) {
				result = requestAddToQueue(userID, itemID, 7200);
				return result.trim();
			}
			if(itemID.matches("^MON\\d{4}")) {
				result = requestAddToQueue(userID, itemID, 9200);
				return result.trim();
			}
			return result.trim();
		}
		if(userID.substring(0, 3).equals("MON")) {
			if(itemID.matches("^MON\\d{4}")) {
				for(int i = 0; i < MontrealServer.MontrealItem.size(); i++) {
					if(LibHelper.isItemIDExist(MontrealServer.MontrealItem.get(i), itemID) && LibHelper.isAlreadyInQueue(MontrealServer.MontrealItem.get(i), userID)) {
						result = "Already in queue for this item.";
						return result.trim();
					}
					if(LibHelper.isItemIDExist(MontrealServer.MontrealItem.get(i), itemID)) {
						LibHelper.addToQueue(MontrealServer.MontrealItem.get(i), userID);
						result = "Added to queue.";
						return result.trim();
					}
				}
			}
			if(itemID.matches("^CON\\d{4}")) {
				result = requestAddToQueue(userID, itemID, 7200);
				return result.trim();
			}
			if(itemID.matches("^MCG\\d{4}")) {
				result = requestAddToQueue(userID, itemID, 8200);
				return result.trim();
			}
			return result.trim();
		}
		return result.trim();
	}
	
	@Override
	public synchronized String exchangeItem(String userID, String newItemID, String oldItemID) {
		String result = "";
		boolean exist = false;
		boolean existnew = false;
		boolean isBorrowed = false;
		if(newItemID.equals(oldItemID)) {
			result = "Cannot exchange an item for the same item";
			return result.trim();
		}
		if(userID.substring(0, 3).equals("CON")) {
			if(oldItemID.substring(0, 3).equals("CON") && newItemID.substring(0, 3).equals("CON")) {
				for(int i = 0; i < ConcordiaServer.ConcordiaItem.size(); i++) {
					if(LibHelper.isItemIDExist(ConcordiaServer.ConcordiaItem.get(i), oldItemID)) {
						exist = true;
					}
				}
				if(exist == false) {
					return "Item " + oldItemID + " does not exist";
				}
				for(int i = 0; i < ConcordiaServer.ConcordiaItem.size(); i++) {
					if(LibHelper.isItemIDExist(ConcordiaServer.ConcordiaItem.get(i), newItemID)) {
						existnew = true;
					}
				}
				if(existnew == false) {
					return "Item " + newItemID + " does not exist";
				}
				for(int i = 0; i < ConcordiaServer.ConcordiaItem.size(); i++) {
					if(LibHelper.isItemIDExist(ConcordiaServer.ConcordiaItem.get(i), oldItemID) && LibHelper.isBorrowedByUser(ConcordiaServer.ConcordiaItem.get(i), userID)) {
						isBorrowed = true;
					}
				}
				if(isBorrowed == false) {
					result = "You did not borrow " + oldItemID;
					return result.trim();
				}
				for(int i = 0; i < ConcordiaServer.ConcordiaItem.size(); i++) {
					if(LibHelper.isItemIDExist(ConcordiaServer.ConcordiaItem.get(i), newItemID) && !LibHelper.isAvailable(ConcordiaServer.ConcordiaItem.get(i))) {
						result = "Item " + newItemID + " is not available";
						return result.trim();
					}
				}
				result = borrowItem(userID, newItemID);
				if(!result.trim().equals("Borrowed successfully")) {
					return "You have already had item " + newItemID;
				}
				returnItem(userID, oldItemID);
				result = "Exchange successfully.";
				return result.trim();
			}else if (oldItemID.substring(0, 3).equals("CON") && !newItemID.substring(0, 3).equals("CON")) {
				for(int i = 0; i < ConcordiaServer.ConcordiaItem.size(); i++) {
					if(LibHelper.isItemIDExist(ConcordiaServer.ConcordiaItem.get(i), oldItemID)) {
						exist = true;
					}
				}
				for(int i = 0; i < ConcordiaServer.ConcordiaItem.size(); i++) {
					if(LibHelper.isItemIDExist(ConcordiaServer.ConcordiaItem.get(i), oldItemID) && LibHelper.isBorrowedByUser(ConcordiaServer.ConcordiaItem.get(i), userID)) {
						isBorrowed = true;
					}
				}
				if(exist == false) {
					return "Item " + oldItemID + " does not exist";
				}
				if(isBorrowed == false) {
					result = "You did not borrow " + oldItemID;
					return result.trim();
				}
				if(newItemID.substring(0, 3).equals("MCG")) {
					result = requestBookAvailability(newItemID, 8400);
				}else if(newItemID.substring(0, 3).equals("MON")) {
					result = requestBookAvailability(newItemID, 9400);
				}
				if(result.trim().equals("Item does not exist")) {
					return "Item " + newItemID + " does not exist";
				}
				if(result.trim().equals("0")) {
					result = "Item " + newItemID + " is not available";
					return result.trim();
				}
				if(newItemID.substring(0, 3).equals("MCG")) {
					result = requestBorrowBook(userID, newItemID, 8100);
				}else if(newItemID.substring(0, 3).equals("MON")) {
					result = requestBorrowBook(userID, newItemID, 9100);
				}
				if(!result.trim().equals("Borrowed successfully")) {
					return "You have already borrowed an item from " + newItemID.substring(0, 3) + " Library";
				}
				returnItem(userID, oldItemID);
				result = "Exchange successfully.";
				return result.trim();
			}else if(!oldItemID.substring(0, 3).equals("CON") && !newItemID.substring(0, 3).equals("CON")) {
				if(oldItemID.substring(0, 3).equals("MCG")) {
					result = requestBookAvailability(oldItemID, 8400);
				}else if(oldItemID.substring(0, 3).equals("MON")) {
					result = requestBookAvailability(oldItemID, 9400);
				}
				if(result.trim().equals("Item does not exist")) {
					return "Item " + oldItemID + " does not exist";
				}
				if(newItemID.substring(0, 3).equals("MCG")) {
					result = requestBookAvailability(newItemID, 8400);
				}else if(newItemID.substring(0, 3).equals("MON")) {
					result = requestBookAvailability(newItemID, 9400);
				}
				if(result.trim().equals("Item does not exist")) {
					return "Item " + newItemID + " does not exist";
				}
				if(result.trim().equals("0")) {
					result = "Item " + newItemID + " is not available";
					return result.trim();
				}
				if(oldItemID.substring(0, 3).equals("MCG")) {
					result = requestCheckBookBorrow(userID, oldItemID, 8500);
				}else if(oldItemID.substring(0, 3).equals("MON")) {
					result = requestCheckBookBorrow(userID, oldItemID, 9500);
				}
				if(!result.trim().equals("true")) {
					return result.trim();
				}
				if(oldItemID.substring(0, 3).equals(newItemID.substring(0, 3))) {
					if(oldItemID.substring(0, 3).equals("MCG")) {
						requestReturnBook(userID, oldItemID, 8300);
					}else if(oldItemID.substring(0, 3).equals("MON")) {
						requestReturnBook(userID, oldItemID, 9300);
					}
					if(newItemID.substring(0, 3).equals("MCG")) {
						requestBorrowBook(userID, newItemID, 8100);
					}else if(newItemID.substring(0, 3).equals("MON")) {
						requestBorrowBook(userID, newItemID, 9100);
					}
				}else {
					if(newItemID.substring(0, 3).equals("MCG")) {
						result = requestBorrowBook(userID, newItemID, 8100);
					}else if(newItemID.substring(0, 3).equals("MON")) {
						result = requestBorrowBook(userID, newItemID, 9100);
					}
					if(!result.trim().equals("Borrowed successfully")) {
						return result.trim();
					}
					if(oldItemID.substring(0, 3).equals("MCG")) {
						requestReturnBook(userID, oldItemID, 8300);
					}else if(oldItemID.substring(0, 3).equals("MON")) {
						requestReturnBook(userID, oldItemID, 9300);
					}
				}			
				result = "Exchange Successfully.";
				return result.trim();
			}else if(!oldItemID.substring(0, 3).equals("CON") && newItemID.substring(0, 3).equals("CON")) {
				if(oldItemID.substring(0, 3).equals("MCG")) {
					result = requestBookAvailability(oldItemID, 8400);
				}else if(oldItemID.substring(0, 3).equals("MON")) {
					result = requestBookAvailability(oldItemID, 9400);
				}
				if(result.trim().equals("Item does not exist")) {
					return "Item " + oldItemID + " does not exist";
				}
				for(int i = 0; i < ConcordiaServer.ConcordiaItem.size(); i++) {
					if(LibHelper.isItemIDExist(ConcordiaServer.ConcordiaItem.get(i), newItemID)) {
						exist = true;
					}
				}
				if(exist == false) {
					return "Item " + newItemID + " does not exist";
				}
				if(oldItemID.substring(0, 3).equals("MCG")) {
					result = requestCheckBookBorrow(userID, oldItemID, 8500);
				}else if(oldItemID.substring(0, 3).equals("MON")) {
					result = requestCheckBookBorrow(userID, oldItemID, 9500);
				}
				if(!result.trim().equals("true")) {
					return result.trim();
				}
				result = borrowItem(userID, newItemID);
				if(!result.trim().equals("Borrowed successfully")) {
					return result.trim();
				}
				if(oldItemID.substring(0, 3).equals("MCG")) {
					requestReturnBook(userID, oldItemID, 8300);
				}else if(oldItemID.substring(0, 3).equals("MON")) {
					requestReturnBook(userID, oldItemID, 9300);
				}
				result = "Exchange Successfully";
				return result.trim();
			}
		}
		if(userID.substring(0, 3).equals("MCG")) {
			if(oldItemID.substring(0, 3).equals("MCG") && newItemID.substring(0, 3).equals("MCG")) {
				for(int i = 0; i < McGillServer.McGillItem.size(); i++) {
					if(LibHelper.isItemIDExist(McGillServer.McGillItem.get(i), oldItemID)) {
						exist = true;
					}
				}
				if(exist == false) {
					return "Item " + oldItemID + " does not exist";
				}
				for(int i = 0; i < McGillServer.McGillItem.size(); i++) {
					if(LibHelper.isItemIDExist(McGillServer.McGillItem.get(i), newItemID)) {
						existnew = true;
					}
				}
				if(existnew == false) {
					return "Item " + newItemID + " does not exist";
				}
				for(int i = 0; i < McGillServer.McGillItem.size(); i++) {
					if(LibHelper.isItemIDExist(McGillServer.McGillItem.get(i), oldItemID) && LibHelper.isBorrowedByUser(McGillServer.McGillItem.get(i), userID)) {
						isBorrowed = true;
					}
				}
				if(isBorrowed == false) {
					result = "You did not borrow " + oldItemID;
					return result.trim();
				}
				for(int i = 0; i < McGillServer.McGillItem.size(); i++) {
					if(LibHelper.isItemIDExist(McGillServer.McGillItem.get(i), newItemID) && !LibHelper.isAvailable(McGillServer.McGillItem.get(i))) {
						result = "Item " + newItemID + " is not available";
						return result.trim();
					}
				}
				result = borrowItem(userID, newItemID);
				if(!result.trim().equals("Borrowed successfully")) {
					return "You have already had item " + newItemID;
				}
				returnItem(userID, oldItemID);
				result = "Exchange successfully.";
				return result.trim();
			}else if (oldItemID.substring(0, 3).equals("MCG") && !newItemID.substring(0, 3).equals("MCG")) {
				for(int i = 0; i < McGillServer.McGillItem.size(); i++) {
					if(LibHelper.isItemIDExist(McGillServer.McGillItem.get(i), oldItemID)) {
						exist = true;
					}
				}
				for(int i = 0; i < McGillServer.McGillItem.size(); i++) {
					if(LibHelper.isItemIDExist(McGillServer.McGillItem.get(i), oldItemID) && LibHelper.isBorrowedByUser(McGillServer.McGillItem.get(i), userID)) {
						isBorrowed = true;
					}
				}
				if(exist == false) {
					return "Item " + oldItemID + " does not exist";
				}
				if(isBorrowed == false) {
					result = "You did not borrow " + oldItemID;
					return result.trim();
				}
				if(newItemID.substring(0, 3).equals("CON")) {
					result = requestBookAvailability(newItemID, 7400);
				}else if(newItemID.substring(0, 3).equals("MON")) {
					result = requestBookAvailability(newItemID, 9400);
				}
				if(result.trim().equals("Item does not exist")) {
					return "Item " + newItemID + " does not exist";
				}
				if(result.trim().equals("0")) {
					result = "Item " + newItemID + " is not available";
					return result.trim();
				}
				if(newItemID.substring(0, 3).equals("CON")) {
					result = requestBorrowBook(userID, newItemID, 7100);
				}else if(newItemID.substring(0, 3).equals("MON")) {
					result = requestBorrowBook(userID, newItemID, 9100);
				}
				if(!result.trim().equals("Borrowed successfully")) {
					return "You have already borrowed an item from " + newItemID.substring(0, 3) + " Library";
				}
				returnItem(userID, oldItemID);
				result = "Exchange successfully.";
				return result.trim();
			}else if(!oldItemID.substring(0, 3).equals("MCG") && !newItemID.substring(0, 3).equals("MCG")) {
				if(oldItemID.substring(0, 3).equals("CON")) {
					result = requestBookAvailability(oldItemID, 7400);
				}else if(oldItemID.substring(0, 3).equals("MON")) {
					result = requestBookAvailability(oldItemID, 9400);
				}
				if(result.trim().equals("Item does not exist")) {
					return "Item " + oldItemID + " does not exist";
				}
				if(newItemID.substring(0, 3).equals("CON")) {
					result = requestBookAvailability(newItemID, 7400);
				}else if(newItemID.substring(0, 3).equals("MON")) {
					result = requestBookAvailability(newItemID, 9400);
				}
				if(result.trim().equals("Item does not exist")) {
					return "Item " + newItemID + " does not exist";
				}
				if(result.trim().equals("0")) {
					result = "Item " + newItemID + " is not available";
					return result.trim();
				}
				if(oldItemID.substring(0, 3).equals("CON")) {
					result = requestCheckBookBorrow(userID, oldItemID, 7500);
				}else if(oldItemID.substring(0, 3).equals("MON")) {
					result = requestCheckBookBorrow(userID, oldItemID, 9500);
				}
				if(!result.trim().equals("true")) {
					return result.trim();
				}
				if(oldItemID.substring(0, 3).equals(newItemID.substring(0, 3))) {
					if(oldItemID.substring(0, 3).equals("CON")) {
						requestReturnBook(userID, oldItemID, 7300);
					}else if(oldItemID.substring(0, 3).equals("MON")) {
						requestReturnBook(userID, oldItemID, 9300);
					}
					if(newItemID.substring(0, 3).equals("CON")) {
						requestBorrowBook(userID, newItemID, 7100);
					}else if(newItemID.substring(0, 3).equals("MON")) {
						requestBorrowBook(userID, newItemID, 9100);
					}
				}else {
					if(newItemID.substring(0, 3).equals("CON")) {
						result = requestBorrowBook(userID, newItemID, 7100);
					}else if(newItemID.substring(0, 3).equals("MON")) {
						result = requestBorrowBook(userID, newItemID, 9100);
					}
					if(!result.trim().equals("Borrowed successfully")) {
						return result.trim();
					}
					if(oldItemID.substring(0, 3).equals("CON")) {
						requestReturnBook(userID, oldItemID, 7300);
					}else if(oldItemID.substring(0, 3).equals("MON")) {
						requestReturnBook(userID, oldItemID, 9300);
					}
				}			
				result = "Exchange Successfully.";
				return result.trim();
			}else if(!oldItemID.substring(0, 3).equals("MCG") && newItemID.substring(0, 3).equals("MCG")) {
				if(oldItemID.substring(0, 3).equals("CON")) {
					result = requestBookAvailability(oldItemID, 7400);
				}else if(oldItemID.substring(0, 3).equals("MON")) {
					result = requestBookAvailability(oldItemID, 9400);
				}
				if(result.trim().equals("Item does not exist")) {
					return "Item " + oldItemID + " does not exist";
				}
				for(int i = 0; i < McGillServer.McGillItem.size(); i++) {
					if(LibHelper.isItemIDExist(McGillServer.McGillItem.get(i), newItemID)) {
						exist = true;
					}
				}
				if(exist == false) {
					return "Item " + newItemID + " does not exist";
				}
				if(oldItemID.substring(0, 3).equals("CON")) {
					result = requestCheckBookBorrow(userID, oldItemID, 7500);
				}else if(oldItemID.substring(0, 3).equals("MON")) {
					result = requestCheckBookBorrow(userID, oldItemID, 9500);
				}
				if(!result.trim().equals("true")) {
					return result.trim();
				}
				result = borrowItem(userID, newItemID);
				if(!result.trim().equals("Borrowed successfully")) {
					return result.trim();
				}
				if(oldItemID.substring(0, 3).equals("CON")) {
					requestReturnBook(userID, oldItemID, 7300);
				}else if(oldItemID.substring(0, 3).equals("MON")) {
					requestReturnBook(userID, oldItemID, 9300);
				}
				result = "Exchange Successfully";
				return result.trim();
			}
		}
		
		if(userID.substring(0, 3).equals("MON")) {
			if(oldItemID.substring(0, 3).equals("MON") && newItemID.substring(0, 3).equals("MON")) {
				for(int i = 0; i < MontrealServer.MontrealItem.size(); i++) {
					if(LibHelper.isItemIDExist(MontrealServer.MontrealItem.get(i), oldItemID)) {
						exist = true;
					}
				}
				if(exist == false) {
					return "Item " + oldItemID + " does not exist";
				}
				for(int i = 0; i < MontrealServer.MontrealItem.size(); i++) {
					if(LibHelper.isItemIDExist(MontrealServer.MontrealItem.get(i), newItemID)) {
						existnew = true;
					}
				}
				if(existnew == false) {
					return "Item " + newItemID + " does not exist";
				}
				for(int i = 0; i < MontrealServer.MontrealItem.size(); i++) {
					if(LibHelper.isItemIDExist(MontrealServer.MontrealItem.get(i), oldItemID) && LibHelper.isBorrowedByUser(MontrealServer.MontrealItem.get(i), userID)) {
						isBorrowed = true;
					}
				}
				if(isBorrowed == false) {
					result = "You did not borrow " + oldItemID;
					return result.trim();
				}
				for(int i = 0; i < MontrealServer.MontrealItem.size(); i++) {
					if(LibHelper.isItemIDExist(MontrealServer.MontrealItem.get(i), newItemID) && !LibHelper.isAvailable(MontrealServer.MontrealItem.get(i))) {
						result = "Item " + newItemID + " is not available";
						return result.trim();
					}
				}
				result = borrowItem(userID, newItemID);
				if(!result.trim().equals("Borrowed successfully")) {
					return "You have already had item " + newItemID;
				}
				returnItem(userID, oldItemID);
				result = "Exchange successfully.";
				return result.trim();
			}else if (oldItemID.substring(0, 3).equals("MON") && !newItemID.substring(0, 3).equals("MON")) {
				for(int i = 0; i < MontrealServer.MontrealItem.size(); i++) {
					if(LibHelper.isItemIDExist(MontrealServer.MontrealItem.get(i), oldItemID)) {
						exist = true;
					}
				}
				for(int i = 0; i < MontrealServer.MontrealItem.size(); i++) {
					if(LibHelper.isItemIDExist(MontrealServer.MontrealItem.get(i), oldItemID) && LibHelper.isBorrowedByUser(MontrealServer.MontrealItem.get(i), userID)) {
						isBorrowed = true;
					}
				}
				if(exist == false) {
					return "Item " + oldItemID + " does not exist";
				}
				if(isBorrowed == false) {
					result = "You did not borrow " + oldItemID;
					return result.trim();
				}
				if(newItemID.substring(0, 3).equals("CON")) {
					result = requestBookAvailability(newItemID, 7400);
				}else if(newItemID.substring(0, 3).equals("MCG")) {
					result = requestBookAvailability(newItemID, 8400);
				}
				if(result.trim().equals("Item does not exist")) {
					return "Item " + newItemID + " does not exist";
				}
				if(result.trim().equals("0")) {
					result = "Item " + newItemID + " is not available";
					return result.trim();
				}
				if(newItemID.substring(0, 3).equals("CON")) {
					result = requestBorrowBook(userID, newItemID, 7100);
				}else if(newItemID.substring(0, 3).equals("MCG")) {
					result = requestBorrowBook(userID, newItemID, 8100);
				}
				if(!result.trim().equals("Borrowed successfully")) {
					return "You have already borrowed an item from " + newItemID.substring(0, 3) + " Library";
				}
				returnItem(userID, oldItemID);
				result = "Exchange successfully.";
				return result.trim();
			}else if(!oldItemID.substring(0, 3).equals("MON") && !newItemID.substring(0, 3).equals("MON")) {
				if(oldItemID.substring(0, 3).equals("CON")) {
					result = requestBookAvailability(oldItemID, 7400);
				}else if(oldItemID.substring(0, 3).equals("MCG")) {
					result = requestBookAvailability(oldItemID, 8400);
				}
				if(result.trim().equals("Item does not exist")) {
					return "Item " + oldItemID + " does not exist";
				}
				if(newItemID.substring(0, 3).equals("CON")) {
					result = requestBookAvailability(newItemID, 7400);
				}else if(newItemID.substring(0, 3).equals("MCG")) {
					result = requestBookAvailability(newItemID, 8400);
				}
				if(result.trim().equals("Item does not exist")) {
					return "Item " + newItemID + " does not exist";
				}
				if(result.trim().equals("0")) {
					result = "Item " + newItemID + " is not available";
					return result;
				}
				if(oldItemID.substring(0, 3).equals("CON")) {
					result = requestCheckBookBorrow(userID, oldItemID, 7500);
				}else if(oldItemID.substring(0, 3).equals("MCG")) {
					result = requestCheckBookBorrow(userID, oldItemID, 8500);
				}
				if(!result.trim().equals("true")) {
					return result.trim();
				}
				if(oldItemID.substring(0, 3).equals(newItemID.substring(0, 3))) {
					if(oldItemID.substring(0, 3).equals("CON")) {
						requestReturnBook(userID, oldItemID, 7300);
					}else if(oldItemID.substring(0, 3).equals("MCG")) {
						requestReturnBook(userID, oldItemID, 8300);
					}
					if(newItemID.substring(0, 3).equals("CON")) {
						requestBorrowBook(userID, newItemID, 7100);
					}else if(newItemID.substring(0, 3).equals("MCG")) {
						requestBorrowBook(userID, newItemID, 8100);
					}
				}else {
					if(newItemID.substring(0, 3).equals("CON")) {
						result = requestBorrowBook(userID, newItemID, 7100);
					}else if(newItemID.substring(0, 3).equals("MCG")) {
						result = requestBorrowBook(userID, newItemID, 8100);
					}
					if(!result.trim().equals("Borrowed successfully")) {
						return result;
					}
					if(oldItemID.substring(0, 3).equals("CON")) {
						requestReturnBook(userID, oldItemID, 7300);
					}else if(oldItemID.substring(0, 3).equals("MCG")) {
						requestReturnBook(userID, oldItemID, 8300);
					}
				}			
				result = "Exchange Successfully.";
				return result.trim();
			}else if(!oldItemID.substring(0, 3).equals("MON") && newItemID.substring(0, 3).equals("MON")) {
				if(oldItemID.substring(0, 3).equals("CON")) {
					result = requestBookAvailability(oldItemID, 7400);
				}else if(oldItemID.substring(0, 3).equals("MCG")) {
					result = requestBookAvailability(oldItemID, 8400);
				}
				if(result.trim().equals("Item does not exist")) {
					return "Item " + oldItemID + " does not exist";
				}
				for(int i = 0; i < MontrealServer.MontrealItem.size(); i++) {
					if(LibHelper.isItemIDExist(MontrealServer.MontrealItem.get(i), newItemID)) {
						exist = true;
					}
				}
				if(exist == false) {
					return "Item " + newItemID + " does not exist";
				}
				if(oldItemID.substring(0, 3).equals("CON")) {
					result = requestCheckBookBorrow(userID, oldItemID, 7500);
				}else if(oldItemID.substring(0, 3).equals("MCG")) {
					result = requestCheckBookBorrow(userID, oldItemID, 8500);
				}
				if(!result.trim().equals("true")) {
					return result.trim();
				}
				result = borrowItem(userID, newItemID);
				if(!result.trim().equals("Borrowed successfully")) {
					return result.trim();
				}
				if(oldItemID.substring(0, 3).equals("CON")) {
					requestReturnBook(userID, oldItemID, 7300);
				}else if(oldItemID.substring(0, 3).equals("MCG")) {
					requestReturnBook(userID, oldItemID, 8300);
				}
				result = "Exchange Successfully";
				return result.trim();
			}
		}
		return result.trim();
	}
	
	private static String requestBookInfo(String itemName, int serverPort) {
		DatagramSocket aSocket = null;
		String result = "";
		try {
			aSocket = new DatagramSocket();
			byte[] message = itemName.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(message, itemName.length(), aHost, serverPort);
			aSocket.send(request);
			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

			aSocket.receive(reply);
			result = new String(reply.getData());
			
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null) {
				aSocket.close();
			}
		}
		return result.trim();
	}
	
	private synchronized static String requestBorrowBook(String userID, String itemID, int serverPort) {
		DatagramSocket aSocket = null;
		String result = "";
		try {
			aSocket = new DatagramSocket();
			byte[] message = (userID+itemID).getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(message, (userID+itemID).length(), aHost, serverPort);
			aSocket.send(request);
			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

			aSocket.receive(reply);
			result = new String(reply.getData());
			
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null) {
				aSocket.close();
			}
		}
		return result.trim();
	}
	
	private synchronized static String requestReturnBook(String userID, String itemID, int serverPort) {
		DatagramSocket aSocket = null;
		String result = "";
		try {
			aSocket = new DatagramSocket();
			byte[] message = (userID+itemID).getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(message, (userID+itemID).length(), aHost, serverPort);
			aSocket.send(request);
			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

			aSocket.receive(reply);
			result = new String(reply.getData());
			
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null) {
				aSocket.close();
			}
		}
		return result.trim();
	}
	
	private synchronized static String requestAddToQueue(String userID, String itemID, int serverPort) {
		DatagramSocket aSocket = null;
		String result = "";
		try {
			aSocket = new DatagramSocket();
			byte[] message = (userID+itemID).getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(message, (userID+itemID).length(), aHost, serverPort);
			aSocket.send(request);
			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

			aSocket.receive(reply);
			result = new String(reply.getData());
			
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null) {
				aSocket.close();
			}
		}
		return result.trim();
	}
	
	private static String requestBookAvailability(String itemID, int serverPort) {
		DatagramSocket aSocket = null;
		String result = "";
		try {
			aSocket = new DatagramSocket();
			byte[] message = itemID.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(message, itemID.length(), aHost, serverPort);
			aSocket.send(request);
			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

			aSocket.receive(reply);
			result = new String(reply.getData());
			
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null) {
				aSocket.close();
			}
		}
		return result.trim();
	}
	
	private static String requestCheckBookBorrow(String userID, String itemID, int serverPort) {
		DatagramSocket aSocket = null;
		String result = "";
		try {
			aSocket = new DatagramSocket();
			byte[] message = (userID+itemID).getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(message, (userID+itemID).length(), aHost, serverPort);
			aSocket.send(request);
			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

			aSocket.receive(reply);
			result = new String(reply.getData());
			
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null) {
				aSocket.close();
			}
		}
		return result.trim();
	}
}
