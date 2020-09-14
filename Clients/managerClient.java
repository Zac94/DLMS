package Clients;
import DataClasses.*;
import Interface.LibraryInterface;

import java.util.Scanner;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class managerClient {
	
	static LibraryInterface libraryInterface;
	
	public static String[] reqItemInfo = {
			"Please enter item ID: ",
			"Please enter item Name: ",
			"Please enter item quantity: "
	};
	
	public static void showFunctions() {
		System.out.println("Please choose a function:");
		System.out.println("1 to add an item.");
		System.out.println("2 to decrease an item quantity or remove completely.");
		System.out.println("3 to list all items.");
		System.out.println("4 to exit.");
	}
	
	public static void main(String[] args) throws MalformedURLException {
		URL compURL = new URL("http://localhost:3000/DLMS?wsdl");
		QName compQName = new QName("http://LibraryImplementation/", "LibraryImplService");
		Service libraryService = Service.create(compURL, compQName);
		
		libraryInterface = libraryService.getPort(LibraryInterface.class);
		String managerID;
		String message = "";
		int choice;
		Logging log = new Logging();
		try {
			Scanner in = new Scanner(System.in);
			System.out.println("Input your managerID");
			Scanner input = new Scanner(System.in);
			managerID = input.next();
			while(!managerID.matches("^CONM\\d{4}") && !managerID.matches("^MCGM\\d{4}") && !managerID.matches("^MONM\\d{4}")) {
				System.out.println("Invalid ID, please try again.");
				managerID = input.next();
			}
			showFunctions();
			while(true) {
				choice = in.nextInt();
				switch(choice) {
					case 1:
						input = new Scanner(System.in);
						String [] itemInfo = new String[2];
						for (int i = 0; i < 2; i++) {
							System.out.println(reqItemInfo[i]);
							itemInfo[i] = input.nextLine();
						}
						System.out.println(reqItemInfo[2]);
						int quantity = input.nextInt();
						message = libraryInterface.addItem(managerID, itemInfo[0], itemInfo[1], quantity);
						System.out.println(message);
						if(managerID.substring(0, 3).equals("CON")) {
							log.writeToManagerLog("Concordia", "add an item(", managerID,itemInfo[0], message);
						}
						if(managerID.substring(0, 3).equals("MCG")) {
							log.writeToManagerLog("McGill", "add an item(", managerID,itemInfo[0], message);
						}
						if(managerID.substring(0, 3).equals("MON")) {
							log.writeToManagerLog("Montreal", "add an item(", managerID,itemInfo[0], message);
						}
						showFunctions();
						break;
					case 2:
						System.out.println(reqItemInfo[0]);
						String ID = input.next();
						System.out.println(reqItemInfo[2]);
						int decrease = input.nextInt();
						message = libraryInterface.removeItem(managerID, ID, decrease);
						System.out.println(message);
						if(managerID.substring(0, 3).equals("CON")) {
							log.writeToManagerLog("Concordia", "remove an item(", managerID,ID, message);
						}
						if(managerID.substring(0, 3).equals("MCG")) {
							log.writeToManagerLog("McGill", "remove an item(", managerID,ID, message);
						}
						if(managerID.substring(0, 3).equals("MON")) {
							log.writeToManagerLog("Montreal", "remove an item(", managerID,ID, message);
						}
						showFunctions();
						break;
					case 3:
						String result = "";
						result = libraryInterface.listItemAvailability(managerID);
						System.out.println(result);
						showFunctions();
						break;
					case 4:
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