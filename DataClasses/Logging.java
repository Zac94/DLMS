package DataClasses;
import java.time.LocalDateTime;
import java.util.*;
import java.io.*;

public class Logging {
	
	
	public Logging() {
		super();
	}
	
	public void writeToSvLog(String Server, String RequestOps, String userID, String itemID, String result, String response) {
		BufferedWriter write = null;
		try {
			LocalDateTime time = LocalDateTime.now();
			String fileName = "src/ServersLogs/" + Server + "Log.txt";
			String log = time.toString() + " " + userID + " requested to " + RequestOps + itemID + ") to/from " + Server + ", the result: " + result + ", response is: " + response + ".";
			File file = new File(fileName);
			if(file.exists()) {
				write = new BufferedWriter(new FileWriter(fileName, true));
				write.write(log);
				write.newLine();
				write.flush();
			}else {
				write = new BufferedWriter(new FileWriter(fileName, false));
				write.write(log);
				write.newLine();
				write.flush();
			}
		}catch (IOException e) {
			System.out.println("Exception caught: " + e);
		}finally {
			if(write != null) {
				try {
					write.close();
				}catch (IOException e1) {
					System.out.println("Exception caught: " + e1);
				}
				
			}
		}

	}
	
	public void writeToUserLog(String Server, String RequestOps, String userID, String itemID, String result) {
		BufferedWriter write = null;
		try {
			LocalDateTime time = LocalDateTime.now();
			String fileName = "src/UserLogs/" + userID + "Log.txt";
			String log = time.toString() + " user: " + userID + " requested to " + RequestOps + itemID + ") to/from " + Server + ", the result: " + result + ".";
			File file = new File(fileName);
			if(file.exists()) {
				write = new BufferedWriter(new FileWriter(fileName, true));
				write.write(log);
				write.newLine();
				write.flush();
			}else {
				write = new BufferedWriter(new FileWriter(fileName, false));
				write.write(log);
				write.newLine();
				write.flush();
			}
		}catch (IOException e) {
			System.out.println("Exception caught: " + e);
		}finally {
			if(write != null) {
				try {
					write.close();
				}catch (IOException e1) {
					System.out.println("Exception caught: " + e1);
				}
				
			}
		}

	}
	
	public void writeToManagerLog(String Server, String RequestOps, String managerID, String itemID, String result) {
		BufferedWriter write = null;
		try {
			LocalDateTime time = LocalDateTime.now();
			String fileName = "src/ManagerLogs/" + managerID + "Log.txt";
			String log = time.toString() + " user: " + managerID + " requested to " + RequestOps + itemID + ") to/from " + Server + ", the result: " + result + ".";
			File file = new File(fileName);
			if(file.exists()) {
				write = new BufferedWriter(new FileWriter(fileName, true));
				write.write(log);
				write.newLine();
				write.flush();
			}else {
				write = new BufferedWriter(new FileWriter(fileName, false));
				write.write(log);
				write.newLine();
				write.flush();
			}
		}catch (IOException e) {
			System.out.println("Exception caught: " + e);
		}finally {
			if(write != null) {
				try {
					write.close();
				}catch (IOException e1) {
					System.out.println("Exception caught: " + e1);
				}
				
			}
		}

	}
}
