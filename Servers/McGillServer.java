package Servers;

import DataClasses.Item;
import DataClasses.Logging;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Properties;

public class McGillServer implements Runnable{
	public static ArrayList<Item> McGillItem = new ArrayList<Item>();
	
	@Override
	public void run() {
		try{
			
			/*
			 * McGillItem.add(new Item("MCG1675", "Python", 12, new ArrayList<>(), new
			 * ArrayList<>())); McGillItem.add(new Item("MCG2314", "Java", 6, new
			 * ArrayList<>(), new ArrayList<>())); McGillItem.add(new Item("MCG6548",
			 * "SOEN", 8, new ArrayList<>(), new ArrayList<>())); McGillItem.add(new
			 * Item("MCG1098", "COMP", 15, new ArrayList<>(), new ArrayList<>()));
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
			 System.out.println("McGill Server ready and waiting ...");
	    } 
	 
	      catch (Exception e) {
	        System.err.println("ERROR: " + e);
	        e.printStackTrace(System.out);
	      }
		
	}
	/*
	 * public static void main(String args[]) { try{ McGillItem.add(new
	 * Item("MCG1675", "Python", 12, new ArrayList<>(), new ArrayList<>()));
	 * McGillItem.add(new Item("MCG2314", "Java", 6, new ArrayList<>(), new
	 * ArrayList<>())); McGillItem.add(new Item("MCG6548", "SOEN", 8, new
	 * ArrayList<>(), new ArrayList<>())); McGillItem.add(new Item("MCG1098",
	 * "COMP", 15, new ArrayList<>(), new ArrayList<>())); Thread thread = new
	 * Thread(() -> receiveBookInfo()); Thread thread1 = new Thread(() ->
	 * receiveBookBorrow()); Thread thread2 = new Thread(() -> receiveBookQueue());
	 * Thread thread3 = new Thread(() -> receiveBookReturn()); Thread thread4 = new
	 * Thread(() -> receiveBookAvailability()); Thread thread5 = new Thread(() ->
	 * receiveCheckBookBorrow()); thread.start(); thread1.start(); thread2.start();
	 * thread3.start(); thread4.start(); thread5.start();
	 * System.out.println("McGill Server ready and waiting ..."); }
	 * 
	 * catch (Exception e) { System.err.println("ERROR: " + e);
	 * e.printStackTrace(System.out); }
	 * 
	 * }
	 */
	
	private static void receiveBookInfo() {
		DatagramSocket aSocket = null;
		

		try {
			aSocket = new DatagramSocket(8000);
			
			while (true) {
				String result = "";
				boolean found = false;
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				result = new String(request.getData()).trim();
				for(int i = 0; i < McGillItem.size(); i++) {
					if(LibHelper.isItemNameExist(McGillItem.get(i), result)) {
						result = LibHelper.findItemInfo(McGillItem.get(i));
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
			aSocket = new DatagramSocket(8100);
			
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
				for(int i = 0; i < McGillItem.size(); i++) {
					if(LibHelper.isBorrowedByUser(McGillItem.get(i), userID)) {
						already = true;
						found = true;
					}
					if(LibHelper.isAlreadyInQueue(McGillItem.get(i), userID)) {
						alreadyInQueue = true;
						found = true;
					}
				}
				for(int i = 0; i < McGillItem.size(); i++) {
					if(!already && LibHelper.isItemIDExist(McGillItem.get(i), itemID) && LibHelper.isAvailable(McGillItem.get(i))) {
						LibHelper.borrowItem(McGillItem, result.substring(0, 8), i);
						for(int j = 0; j < McGillItem.size(); j++) {
							if(LibHelper.isAlreadyInQueue(McGillItem.get(i), userID)) {
								LibHelper.removeFromQueue(McGillItem.get(i), userID);
								alreadyInQueue = false;
							}
						}
						found = true;
						log.writeToSvLog("McGill", "borrow an item(", userID, itemID, "success", "Borrow successfully.");
						result = "Borrowed successfully";
					}else if(!alreadyInQueue && !already && LibHelper.isItemIDExist(McGillItem.get(i), result.substring(8)) && !LibHelper.isAvailable(McGillItem.get(i))) {
						log.writeToSvLog("McGill", "borrow an item(", userID, itemID, "fail", "Out of Stock.");
						result = "Out of Stock.";
						found = true;
					}
				}
				if(!found) {
					log.writeToSvLog("McGill", "borrow an item from", userID, itemID, "fail", "Item not found");
					result = "";
				}
				if(already) {
					log.writeToSvLog("McGill", "borrow an item from", userID, itemID, "fail", "You already borrowed 1 item from this library");
					result = "You already borrowed 1 item from McGill library";
				}
				if(alreadyInQueue) {
					log.writeToSvLog("McGill", "borrow an item from", userID, itemID, "fail", "You already in queue for 1 item from this library.");
					result = "You already in queue for 1 item from McGill library.";
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
			aSocket = new DatagramSocket(8300);
			
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
				for(int i = 0; i < McGillItem.size(); i++) {
					if(LibHelper.isItemIDExist(McGillItem.get(i), itemID) && LibHelper.isBorrowedByUser(McGillItem.get(i), userID)) {
						LibHelper.returnItem(McGillItem, userID, i);
						log.writeToSvLog("McGill", "return an item(", userID,itemID, "success", "Item returned.");
						result = "Item returned.";
						valid = true;
						found = true;
					}else if(LibHelper.isItemIDExist(McGillItem.get(i), itemID) && !LibHelper.isBorrowedByUser(McGillItem.get(i), userID)) {
						log.writeToSvLog("McGill", "return an item(", userID, itemID, "fail", "You did not borrow this item.");
						result = "You did not borrow this item.";
						valid = false;
						found = true;
					}
				}
				if(found == false && valid == false) {
					log.writeToSvLog("McGill", "return an item to", userID, itemID, "fail", "Wrong item ID.");
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
			aSocket = new DatagramSocket(8200);
			
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
				for(int i = 0; i < McGillItem.size(); i++) {
					if(LibHelper.isBorrowedByUser(McGillItem.get(i), userID)) {
						alreadyBorrow = true;
					}
				}
				for(int i = 0; i < McGillItem.size(); i++) {
					if(LibHelper.isAlreadyInQueue(McGillItem.get(i), userID)) {
						already = true;
					}
				}
				for(int i = 0; i < McGillItem.size(); i++) {
					if(!already && !alreadyBorrow && LibHelper.isItemIDExist(McGillItem.get(i), itemID)) {
						LibHelper.addToQueue(McGillItem.get(i), userID);
						result = "Added to queue.";
					}
				}
				if(already == true) {
					result = "You can only get in queue for 1 item.";
				}
				if(alreadyBorrow == true) {
					result = "Cannot add to queue, you've already borrowed 1 item from McGill Library";
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
			aSocket = new DatagramSocket(8400);
			
			while (true) {
				String result = "";
				boolean exist = false;
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				result = new String(request.getData()).trim();
				for(int i = 0; i < McGillItem.size(); i++) {
					if(LibHelper.isItemIDExist(McGillItem.get(i), result)) {
						result = Integer.toString(McGillItem.get(i).getQuantity());
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
			aSocket = new DatagramSocket(8500);
			
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
				for(int i = 0; i < McGillItem.size(); i++) {
					if(LibHelper.isItemIDExist(McGillItem.get(i), itemID)) {
						exist = true;
					}
				}
				for(int i = 0; i < McGillItem.size(); i++) {
					if(LibHelper.isItemIDExist(McGillItem.get(i), itemID) && LibHelper.isBorrowedByUser(McGillItem.get(i), userID)) {
						isBorrowed = true;
					}
				}
				for(int i = 0; i < McGillItem.size(); i++) {
					if(LibHelper.isItemIDExist(McGillItem.get(i), itemID) && !LibHelper.isBorrowedByUser(McGillItem.get(i), userID)) {
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
