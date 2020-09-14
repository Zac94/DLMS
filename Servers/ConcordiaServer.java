package Servers;

import DataClasses.Item;
import DataClasses.Logging;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Properties;

public class ConcordiaServer implements Runnable{
	public static ArrayList<Item> ConcordiaItem = new ArrayList<Item>();
	
	@Override
	public void run() {
		try{
			
			
			  ConcordiaItem.add(new Item("CON1234", "Java", 12, new ArrayList<>(), new
			  ArrayList<>())); ConcordiaItem.add(new Item("CON3451", "Distributed System",
			  3, new ArrayList<>(), new ArrayList<>())); ConcordiaItem.add(new
			  Item("CON2314", "OOP", 4, new ArrayList<>(), new ArrayList<>()));
			  ConcordiaItem.add(new Item("CON6758", "C++", 20, new ArrayList<>(), new
			  ArrayList<>()));
			 
			 
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
	 
	      System.out.println("Concordia Server ready and waiting ...");
	    }
	 
	      catch (Exception e) {
	        System.err.println("ERROR: " + e);
	        e.printStackTrace(System.out);
	      }
		
	}
	/*
	 * public static void main(String args[]) { try{ ConcordiaItem.add(new
	 * Item("CON1234", "Java", 12, new ArrayList<>(), new ArrayList<>()));
	 * ConcordiaItem.add(new Item("CON3451", "Distributed System", 3, new
	 * ArrayList<>(), new ArrayList<>())); ConcordiaItem.add(new Item("CON2314",
	 * "OOP", 4, new ArrayList<>(), new ArrayList<>())); ConcordiaItem.add(new
	 * Item("CON6758", "C++", 20, new ArrayList<>(), new ArrayList<>())); Thread
	 * thread = new Thread(() -> receiveBookInfo()); Thread thread1 = new Thread(()
	 * -> receiveBookBorrow()); Thread thread2 = new Thread(() ->
	 * receiveBookQueue()); Thread thread3 = new Thread(() -> receiveBookReturn());
	 * Thread thread4 = new Thread(() -> receiveBookAvailability()); Thread thread5
	 * = new Thread(() -> receiveCheckBookBorrow()); thread.start();
	 * thread1.start(); thread2.start(); thread3.start(); thread4.start();
	 * thread5.start();
	 * 
	 * System.out.println("Concordia Server ready and waiting ..."); }
	 * 
	 * catch (Exception e) { System.err.println("ERROR: " + e);
	 * e.printStackTrace(System.out); }
	 * 
	 * }
	 */
	
	private static void receiveBookInfo() {
		DatagramSocket aSocket = null;
		String result = "";

		try {
			aSocket = new DatagramSocket(7000);
			
			while (true) {
				boolean found = false;
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				result = new String(request.getData()).trim();
				for(int i = 0; i < ConcordiaItem.size(); i++) {
					if(LibHelper.isItemNameExist(ConcordiaItem.get(i), result)) {
						result = LibHelper.findItemInfo(ConcordiaItem.get(i));
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
			aSocket = new DatagramSocket(7100);
			
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
				for(int i = 0; i < ConcordiaItem.size(); i++) {
					if(LibHelper.isBorrowedByUser(ConcordiaItem.get(i), userID)) {
						already = true;
						found = true;
					}
					if(LibHelper.isAlreadyInQueue(ConcordiaItem.get(i), userID)) {
						alreadyInQueue = true;
						found = true;
					}
				}
				for(int i = 0; i < ConcordiaItem.size(); i++) {
					if(!already && LibHelper.isItemIDExist(ConcordiaItem.get(i), itemID) && LibHelper.isAvailable(ConcordiaItem.get(i))) {
						LibHelper.borrowItem(ConcordiaItem, result.substring(0, 8), i);
						for(int j = 0; j < ConcordiaItem.size(); j++) {
							if(LibHelper.isAlreadyInQueue(ConcordiaItem.get(i), userID)) {
								LibHelper.removeFromQueue(ConcordiaItem.get(i), userID);
								alreadyInQueue = false;
							}
						}
						found = true;
						log.writeToSvLog("Concordia", "borrow an item(", result.substring(0, 8), result.substring(8), "success", "Borrow Successfully");
						result = "Borrowed successfully";
					}else if(!alreadyInQueue && !already && LibHelper.isItemIDExist(ConcordiaItem.get(i), result.substring(8)) && !LibHelper.isAvailable(ConcordiaItem.get(i))) {
						log.writeToSvLog("Concordia", "borrow an item(", result.substring(0, 8), result.substring(8), "fail", "Out of Stock.");
						result = "Out of Stock.";
						found = true;
					}
				}
				if(!found) {
					log.writeToSvLog("Concordia", "borrow an item(", result.substring(0, 8), result.substring(8), "fail", "item not found");
					result = "";
				}
				if(already) {
					log.writeToSvLog("Concordia", "borrow an item(", result.substring(0, 8), result.substring(8), "fail", "You already borrowed 1 item from this library");
					result = "You're already borrowed 1 item from Concordia library";
				}
				if(alreadyInQueue) {
					log.writeToSvLog("Concordia", "borrow an item(", result.substring(0, 8), result.substring(8), "fail", "You already in queue for 1 item from this library.");
					result = "You're already in queue for 1 item from Concordia library";
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
			aSocket = new DatagramSocket(7200);
			
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
				for(int i = 0; i < ConcordiaItem.size(); i++) {
					if(LibHelper.isBorrowedByUser(ConcordiaItem.get(i), userID)) {
						alreadyBorrow = true;
					}
				}
				for(int i = 0; i < ConcordiaItem.size(); i++) {
					if(LibHelper.isAlreadyInQueue(ConcordiaItem.get(i), userID)) {
						already = true;
					}
				}
				for(int i = 0; i < ConcordiaItem.size(); i++) {
					if(!already && !alreadyBorrow && LibHelper.isItemIDExist(ConcordiaItem.get(i), itemID)) {
						LibHelper.addToQueue(ConcordiaItem.get(i), userID);
						result = "Added to queue.";
					}
				}
				if(already == true) {
					result = "You can only get in queue for 1 item.";
				}
				if(alreadyBorrow == true) {
					result = "Cannot add to queue, you've already borrowed 1 item from Concordia Library";
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
			aSocket = new DatagramSocket(7300);
			
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
				for(int i = 0; i < ConcordiaItem.size(); i++) {
					if(LibHelper.isItemIDExist(ConcordiaItem.get(i), itemID) && LibHelper.isBorrowedByUser(ConcordiaItem.get(i), userID)) {
						LibHelper.returnItem(ConcordiaItem, userID, i);
						log.writeToSvLog("Concordia", "return an item(", userID, itemID, "success", "Item returned");
						result = "Item returned.";
						valid = true;
						found = true;
					}else if(LibHelper.isItemIDExist(ConcordiaItem.get(i), itemID) && !LibHelper.isBorrowedByUser(ConcordiaItem.get(i), userID)) {
						log.writeToSvLog("Concordia", "return an item(", userID, itemID, "fail", "You did not borrow this item.");
						result = "You did not borrow this item.";
						valid = false;
						found = true;
					}
				}
				if(found == false && valid == false) {
					log.writeToSvLog("Concordia", "return an item(", userID, itemID, "fail", "Wrong item ID.");
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
	
	private static void receiveBookAvailability() {
		DatagramSocket aSocket = null;
		

		try {
			aSocket = new DatagramSocket(7400);
			
			while (true) {
				String result = "";
				boolean exist = false;
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				result = new String(request.getData()).trim();
				for(int i = 0; i < ConcordiaItem.size(); i++) {
					if(LibHelper.isItemIDExist(ConcordiaItem.get(i), result)) {
						result = Integer.toString(ConcordiaItem.get(i).getQuantity());
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
			aSocket = new DatagramSocket(7500);
			
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
				for(int i = 0; i < ConcordiaItem.size(); i++) {
					if(LibHelper.isItemIDExist(ConcordiaItem.get(i), itemID)) {
						exist = true;
					}
				}
				for(int i = 0; i < ConcordiaItem.size(); i++) {
					if(LibHelper.isItemIDExist(ConcordiaItem.get(i), itemID) && LibHelper.isBorrowedByUser(ConcordiaItem.get(i), userID)) {
						isBorrowed = true;
					}
				}
				for(int i = 0; i < ConcordiaItem.size(); i++) {
					if(LibHelper.isItemIDExist(ConcordiaItem.get(i), itemID) && !LibHelper.isBorrowedByUser(ConcordiaItem.get(i), userID)) {
						isBorrowed = false;
						exist = true;
					}
				}
				if(exist == false) {
					result = "Item does not exist";
				}
				if(isBorrowed == false && exist == true) {
					result = "You did not borrow " + itemID;
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
