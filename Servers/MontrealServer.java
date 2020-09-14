package Servers;

import DataClasses.Item;
import DataClasses.Logging;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Properties;

public class MontrealServer implements Runnable{
	public static ArrayList<Item> MontrealItem = new ArrayList<Item>();
	
	@Override
	public void run() {
		try{
			
			/*
			 * MontrealItem.add(new Item("MON0912", "Java", 12, new ArrayList<>(), new
			 * ArrayList<>())); MontrealItem.add(new Item("MON8906", "Distributed System",
			 * 4, new ArrayList<>(), new ArrayList<>())); MontrealItem.add(new
			 * Item("MON2309", "HTML", 9, new ArrayList<>(), new ArrayList<>()));
			 * MontrealItem.add(new Item("MON3407", "PHP", 11, new ArrayList<>(), new
			 * ArrayList<>()));
			 */
			 
			Thread thread = new Thread(() -> receiveBookInfo());
			Thread thread1 = new Thread(() -> receiveBookBorrow());
			Thread thread2 = new Thread(() -> receiveBookQueue());
			Thread thread3 = new Thread(() -> receiveBookReturn());
			Thread thread4 = new Thread(() -> receiveBookAvailability());
			Thread thread5 = new Thread(() -> receiveCheckBookBorrow());
			thread.start();
			thread1.start();
			thread2.start();
			thread3.start();
			thread4.start();
			thread5.start();
			 System.out.println("Montreal Server ready and waiting ...");
	    } 
	 
	      catch (Exception e) {
	        System.err.println("ERROR: " + e);
	        e.printStackTrace(System.out);
	      }
		
	}
	/*
	 * public static void main(String args[]) { try{ MontrealItem.add(new
	 * Item("MON0912", "Java", 12, new ArrayList<>(), new ArrayList<>()));
	 * MontrealItem.add(new Item("MON8906", "Distributed System", 4, new
	 * ArrayList<>(), new ArrayList<>())); MontrealItem.add(new Item("MON2309",
	 * "HTML", 9, new ArrayList<>(), new ArrayList<>())); MontrealItem.add(new
	 * Item("MON3407", "PHP", 11, new ArrayList<>(), new ArrayList<>())); Thread
	 * thread = new Thread(() -> receiveBookInfo()); Thread thread1 = new Thread(()
	 * -> receiveBookBorrow()); Thread thread2 = new Thread(() ->
	 * receiveBookQueue()); Thread thread3 = new Thread(() -> receiveBookReturn());
	 * Thread thread4 = new Thread(() -> receiveBookAvailability()); Thread thread5
	 * = new Thread(() -> receiveCheckBookBorrow()); thread.start();
	 * thread1.start(); thread2.start(); thread3.start(); thread4.start();
	 * thread5.start(); System.out.println("Montreal Server ready and waiting ...");
	 * }
	 * 
	 * catch (Exception e) { System.err.println("ERROR: " + e);
	 * e.printStackTrace(System.out); }
	 * 
	 * }
	 */
	
	private static void receiveBookInfo() {
		DatagramSocket aSocket = null;
		

		try {
			aSocket = new DatagramSocket(9000);
			
			while (true) {
				String result = "";
				boolean found = false;
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				result = new String(request.getData()).trim();
				for(int i = 0; i < MontrealItem.size(); i++) {
					if(LibHelper.isItemNameExist(MontrealItem.get(i), result)) {
						result = LibHelper.findItemInfo(MontrealItem.get(i));
						found = true;
					}
				}
				if(found != true) {
					result = "";
				}
				DatagramPacket reply = new DatagramPacket(result.getBytes(), result.length(), request.getAddress(),
						request.getPort());
				aSocket.send(reply);
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
	}
	
	private static void receiveBookBorrow() {
		DatagramSocket aSocket = null;
		

		try {
			aSocket = new DatagramSocket(9100);
			
			while (true) {
				String result = "";
				Logging log = new Logging();
				boolean found = false;
				boolean already = false;
				boolean alreadyInQueue = false;
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				result = new String(request.getData()).trim();
				String userID = result.substring(0, 8);
				String itemID = result.substring(8);
				for(int i = 0; i < MontrealItem.size(); i++) {
					if(LibHelper.isBorrowedByUser(MontrealItem.get(i), userID)) {
						already = true;
						found = true;
					}
					if(LibHelper.isAlreadyInQueue(MontrealItem.get(i), userID)) {
						alreadyInQueue = true;
						found = true;
					}
				}
				for(int i = 0; i < MontrealItem.size(); i++) {
					if(!already && LibHelper.isItemIDExist(MontrealItem.get(i), itemID) && LibHelper.isAvailable(MontrealItem.get(i))) {
						LibHelper.borrowItem(MontrealItem, result.substring(0, 8), i);
						for(int j = 0; j < MontrealItem.size(); j++) {
							if(LibHelper.isAlreadyInQueue(MontrealItem.get(i), userID)) {
								LibHelper.removeFromQueue(MontrealItem.get(i), userID);
								alreadyInQueue = false;
							}
						}
						found = true;
						log.writeToSvLog("Montreal", "borrow an item(", userID, itemID, "success", "Borrow successfully.");
						result = "Borrowed successfully";
					}else if(!alreadyInQueue && !already && LibHelper.isItemIDExist(MontrealItem.get(i), itemID) && !LibHelper.isAvailable(MontrealItem.get(i))) {
						log.writeToSvLog("Montreal", "borrow an item(", userID, itemID, "fail", "Out of Stock.");
						result = "Out of Stock.";
						found = true;
					}
				}
				if(!found) {
					log.writeToSvLog("Montreal", "borrow an item(", userID, itemID, "fail", "Item not found");
					result = "";
				}
				if(already) {
					log.writeToSvLog("Montreal", "borrow an item(", userID, itemID, "fail", "You already borrowed 1 item from this library");
					result = "You're already borrowed 1 item from Montreal library";
				}
				if(alreadyInQueue) {
					log.writeToSvLog("Montreal", "borrow an item(", userID, itemID, "fail", "You already in queue for 1 item from this library.");
					result = "You're already in queue for 1 item from Montreal library.";
				}
				DatagramPacket reply = new DatagramPacket(result.getBytes(), result.length(), request.getAddress(),
						request.getPort());
				aSocket.send(reply);
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
	}
	
	private static void receiveBookReturn() {
		DatagramSocket aSocket = null;
		

		try {
			aSocket = new DatagramSocket(9300);
			
			while (true) {
				String result = "";
				Logging log = new Logging();
				boolean found = false;
				boolean valid = false;
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				result = new String(request.getData()).trim();
				String userID = result.substring(0, 8);
				String itemID = result.substring(8);
				for(int i = 0; i < MontrealItem.size(); i++) {
					if(LibHelper.isItemIDExist(MontrealItem.get(i), itemID) && LibHelper.isBorrowedByUser(MontrealItem.get(i), userID)) {
						LibHelper.returnItem(MontrealItem, userID, i);
						log.writeToSvLog("Montreal", "return an item(", userID,itemID, "success", "Item returned.");
						result = "Item returned.";
						valid = true;
						found = true;
					}else if(LibHelper.isItemIDExist(MontrealItem.get(i), itemID) && !LibHelper.isBorrowedByUser(MontrealItem.get(i), userID)) {
						log.writeToSvLog("Montreal", "return an item(", userID,itemID, "fail", "You did not borrow this item.");
						result = "You did not borrow this item.";
						valid = false;
						found = true;
					}
				}
				if(found == false && valid == false) {
					log.writeToSvLog("Montreal", "return an item(", userID,itemID, "fail", "Wrong item ID.");
					result = "Wrong item ID.";
				}
				DatagramPacket reply = new DatagramPacket(result.getBytes(), result.length(), request.getAddress(),
						request.getPort());
				aSocket.send(reply);
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
	}
	
	private static void receiveBookQueue() {
		DatagramSocket aSocket = null;

		try {
			aSocket = new DatagramSocket(9200);
			
			while (true) {
				String result = "";
				boolean already = false;
				boolean alreadyBorrow = false;
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				result = new String(request.getData()).trim();
				String userID = result.substring(0, 8);
				String itemID = result.substring(8);
				for(int i = 0; i < MontrealItem.size(); i++) {
					if(LibHelper.isBorrowedByUser(MontrealItem.get(i), userID)) {
						alreadyBorrow = true;
					}
				}
				for(int i = 0; i < MontrealItem.size(); i++) {
					if(LibHelper.isAlreadyInQueue(MontrealItem.get(i), userID)) {
						already = true;
					}
				}
				for(int i = 0; i < MontrealItem.size(); i++) {
					if(!already && !alreadyBorrow && LibHelper.isItemIDExist(MontrealItem.get(i), itemID)) {
						LibHelper.addToQueue(MontrealItem.get(i), userID);
						result = "Added to queue.";
					}
				}
				if(already == true) {
					result = "You can only get in queue for 1 item.";
				}
				if(alreadyBorrow == true) {
					result = "Cannot add to queue, you've already borrowed 1 item from Montreal Library";
				}
				DatagramPacket reply = new DatagramPacket(result.getBytes(), result.length(), request.getAddress(),
						request.getPort());
				aSocket.send(reply);
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
	}
	
	private static void receiveBookAvailability() {
		DatagramSocket aSocket = null;
		

		try {
			aSocket = new DatagramSocket(9400);
			
			while (true) {
				String result = "";
				boolean exist = false;
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				result = new String(request.getData()).trim();
				for(int i = 0; i < MontrealItem.size(); i++) {
					if(LibHelper.isItemIDExist(MontrealItem.get(i), result)) {
						result = Integer.toString(MontrealItem.get(i).getQuantity());
						exist = true;
					}
				}
				if(exist == false) {
					result = "Item does not exist";
				}
				DatagramPacket reply = new DatagramPacket(result.getBytes(), result.length(), request.getAddress(),
						request.getPort());
				aSocket.send(reply);
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
	}
	
	private static void receiveCheckBookBorrow() {
		DatagramSocket aSocket = null;
		

		try {
			aSocket = new DatagramSocket(9500);
			
			while (true) {
				String result = "";
				boolean exist = false;
				boolean isBorrowed = false;
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				result = new String(request.getData()).trim();
				String userID = result.substring(0, 8);
				String itemID = result.substring(8);
				for(int i = 0; i < MontrealItem.size(); i++) {
					if(LibHelper.isItemIDExist(MontrealItem.get(i), itemID)) {
						exist = true;
					}
				}
				for(int i = 0; i < MontrealItem.size(); i++) {
					if(LibHelper.isItemIDExist(MontrealItem.get(i), itemID) && LibHelper.isBorrowedByUser(MontrealItem.get(i), userID)) {
						isBorrowed = true;
					}
				}
				for(int i = 0; i < MontrealItem.size(); i++) {
					if(LibHelper.isItemIDExist(MontrealItem.get(i), itemID) && !LibHelper.isBorrowedByUser(MontrealItem.get(i), userID)) {
						isBorrowed = false;
						exist = true;
					}
				}
				if(exist == false) {
					result = "Item does not exist";
				}
				if(isBorrowed == false && exist == true) {
					result = "You did not borrow " + result.substring(8);
				}		
				if(isBorrowed == true && exist == true) {
					result = "true";
				}
				DatagramPacket reply = new DatagramPacket(result.getBytes(), result.length(), request.getAddress(),
						request.getPort());
				aSocket.send(reply);
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
	}
}
