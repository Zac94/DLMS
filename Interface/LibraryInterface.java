package Interface;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface LibraryInterface {
	public String addItem(String managerID, String itemID, String itemName, int quantity);
	public String removeItem(String managerID, String itemID, int quantity);
	public String listItemAvailability(String managerID);
	public String borrowItem(String userID, String itemID);
	public String findItem(String userID, String itemName);
	public String returnItem(String userID, String itemID);
	public String addToQueue(String userID, String itemID);
	public String exchangeItem(String userID, String newItemID, String oldItemID);
}
