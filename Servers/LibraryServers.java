package Servers;

import javax.xml.ws.Endpoint;

import LibraryImplementation.LibraryImpl;

public class LibraryServers {
	public static LibraryImpl libraryImpl;
	
	public static void main(String[] args) {
		System.out.println("Library Server Started...");
		libraryImpl = new LibraryImpl();
		new Thread(new ConcordiaServer()).start();
		new Thread(new McGillServer()).start();
		new Thread(new MontrealServer()).start();
		Endpoint endpoint = Endpoint.publish("http://localhost:3000/DLMS", libraryImpl);

	}

}
