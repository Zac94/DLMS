package Clients;
import DataClasses.*;
import Interface.LibraryInterface;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.util.Scanner;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.util.ArrayList;

public class userClient {
	
	static LibraryInterface libraryInterface;
	
	public static String[] reqItemInfo = {
			"Please enter item ID: ",
			"Please enter item Name: ",
			"Please enter old item ID: ",
			"Please enter new item ID: "
	};
	
	public static void showFunctions() {
		System.out.println("Please choose a function:");
		System.out.println("1 to find items.");
		System.out.println("2 to borrow item.");
		System.out.println("3 to return item.");
		System.out.println("4 to exchange item.");
		System.out.println("5 to exit.");
	}
	
	public static void main(String[] args) throws MalformedURLException {
		URL compURL = new URL("http://localhost:3000/DLMS?wsdl");
		QName compQName = new QName("http://LibraryImplementation/", "LibraryImplService");
		Service libraryService = Service.create(compURL, compQName);
		libraryInterface = libraryService.getPort(LibraryInterface.class);
		
		String userID = "";
		String message = "";
		int choice;
		Logging log = new Logging();
		try {
			Scanner in = new Scanner(System.in);
			System.out.println("Input your userID");
			Scanner input = new Scanner(System.in);
			userID = input.next();
			while(!userID.matches("^CONU\\d{4}") && !userID.matches("^MCGU\\d{4}") && !userID.matches("^MONU\\d{4}")) {
				System.out.println("Invalid ID, please try again.");
				userID = input.next();
			}
			showFunctions();
			while(true) {
				choice = in.nextInt();
				switch(choice) {
					case 1:
						System.out.println(reqItemInfo[1]);
						Scanner iID = new Scanner(System.in);
						String name = iID.next();
						message = libraryInterface.findItem(userID, name);
						System.out.println(message);
						showFunctions();
						break;
					case 2:
						System.out.println(reqItemInfo[0]);
						String ID = input.next();
						message = libraryInterface.borrowItem(userID, ID);
						System.out.println(message);
						if(ID.substring(0, 3).equals("CON")) {
							log.writeToUserLog("Concordia", "borrow an item(", userID,ID, message);
						}
						if(ID.substring(0, 3).equals("MCG")) {
							log.writeToUserLog("McGill", "borrow an item(", userID,ID, message);
						}
						if(ID.substring(0, 3).equals("MON")) {
							log.writeToUserLog("Montreal", "borrow an item(", userID,ID, message);
						}
						if(message.equals("Out of Stock.")) {
							System.out.println("Do you want to be added to this book's queue?");
							System.out.println("1 for yes or 2 for no:");
							int selection = Integer.parseInt(input.next());
							if(selection == 1) {
								String result = "";
								result = libraryInterface.addToQueue(userID, ID);
								System.out.println(result);
							}else {
								System.out.println("No selected.");
								showFunctions();
								break;
							}
						}
						showFunctions();
						break;
					case 3:
						System.out.println(reqItemInfo[0]);
						String itemID = input.next();
						message = libraryInterface.returnItem(userID, itemID);
						System.out.println(message);
						if(userID.substring(0, 3).equals("CON")) {
							if(itemID.substring(0, 3).equals("CON")) {
								log.writeToUserLog("Concordia", "return an item(", userID,itemID, message);
								showFunctions();
								break;
							}
							if(itemID.substring(0, 3).equals("MCG")) {
								log.writeToUserLog("McGill", "return an item(", userID,itemID, message);
								showFunctions();
								break;
							}
							if(itemID.substring(0, 3).equals("MON")) {
								log.writeToUserLog("Montreal", "return an item(", userID,itemID, message);
								showFunctions();
								break;
							}
							log.writeToUserLog("Concordia", "return an item(", userID,itemID, message);
							showFunctions();
							break;
						}
						if(userID.substring(0, 3).equals("MCG")) {
							if(itemID.substring(0, 3).equals("CON")) {
								log.writeToUserLog("Concordia", "return an item(", userID,itemID, message);
								showFunctions();
								break;
							}
							if(itemID.substring(0, 3).equals("MCG")) {
								log.writeToUserLog("McGill", "return an item(", userID,itemID, message);
								showFunctions();
								break;
							}
							if(itemID.substring(0, 3).equals("MON")) {
								log.writeToUserLog("Montreal", "return an item(", userID,itemID, message);
								showFunctions();
								break;
							}
							log.writeToUserLog("McGill", "return an item(", userID,itemID, message);
							showFunctions();
							break;
						}
						if(userID.substring(0, 3).equals("MON")) {
							if(itemID.substring(0, 3).equals("CON")) {
								log.writeToUserLog("Concordia", "return an item(", userID,itemID, message);
								showFunctions();
								break;
							}
							if(itemID.substring(0, 3).equals("MCG")) {
								log.writeToUserLog("McGill", "return an item(", userID,itemID, message);
								showFunctions();
								break;
							}
							if(itemID.substring(0, 3).equals("MON")) {
								log.writeToUserLog("Montreal", "return an item(", userID,itemID, message);
								showFunctions();
								break;
							}
							log.writeToUserLog("Montreal", "return an item(", userID,itemID, message);
							showFunctions();
							break;
						}
						
						showFunctions();
						break;
					case 4:
						System.out.println(reqItemInfo[2]);
						String oldItemID = input.next().trim();
						System.out.println(reqItemInfo[3]);
						String newItemID = input.next().trim();
						message = libraryInterface.exchangeItem(userID, newItemID, oldItemID);
						System.out.println(message);
						showFunctions();
						break;
						
					case 5:
						System.out.println("Exiting...");
						in.close();
						input.close();
						System.exit(0);
					default:
						System.out.println("Invalid input, please try again.");
				}
			}
			
		}catch (Exception e) {
			System.out.println("Exception Caught: " + e);
		}
	}
}