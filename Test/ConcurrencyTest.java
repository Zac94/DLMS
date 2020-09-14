package Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import Interface.LibraryInterface;

public class ConcurrencyTest {
	static LibraryInterface libraryInterface;
	static Runnable[] task = new Runnable[20];

	
	public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException, InvalidName, NotFound, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, InterruptedException {
		URL compURL = new URL("http://localhost:3000/DLMS?wsdl");
		QName compQName = new QName("http://LibraryImplementation/", "LibraryImplService");
		Service libraryService = Service.create(compURL, compQName);
		
		libraryInterface = libraryService.getPort(LibraryInterface.class);
		task[0] = ()->{
			libraryInterface.borrowItem("CONU9990", "CON1234");
			
		};
		
		task[1] = ()->{
			libraryInterface.borrowItem("CONU9991", "CON1234");
			
		};
		
		task[2] = ()->{
			libraryInterface.borrowItem("CONU9992", "CON1234");
		};
		
		task[3] = ()->{
			libraryInterface.borrowItem("CONU9993", "CON1234");
		};
		
		task[4] = ()->{
			libraryInterface.borrowItem("CONU9994", "CON1234");
		};
		
		task[5] = ()->{
			libraryInterface.borrowItem("CONU9995", "CON1234");
		};
		
		task[6] = ()->{
			libraryInterface.borrowItem("CONU9996", "CON1234");
		};
		
		task[7] = ()->{
			libraryInterface.borrowItem("CONU9997", "CON1234");
		};
		
		task[8] = ()->{
			libraryInterface.borrowItem("CONU9998", "CON1234");
		};
		
		task[9] = ()->{
			libraryInterface.borrowItem("CONU9999", "CON1234");
		};
		
		task[10] = () ->{
			libraryInterface.exchangeItem("CONU9990", "CON6758", "CON1234");
		};
		
		task[11] = () ->{
			libraryInterface.exchangeItem("CONU9991", "CON6758", "CON1234");
		};
		
		task[12] = () ->{
			libraryInterface.exchangeItem("CONU9992", "CON6758", "CON1234");
		};
		
		task[13] = () ->{
			libraryInterface.exchangeItem("CONU9993", "CON6758", "CON1234");
		};
		
		task[14] = () ->{
			libraryInterface.exchangeItem("CONU9994", "CON6758", "CON1234");
		};
		
		task[15] = () ->{
			libraryInterface.exchangeItem("CONU9995", "CON6758", "CON1234");
		};
		
		task[16] = () ->{
			libraryInterface.exchangeItem("CONU9996", "CON6758", "CON1234");
		};
		
		task[17] = () ->{
			libraryInterface.exchangeItem("CONU9997", "CON6758", "CON1234");
		};
		
		task[18] = () ->{
			libraryInterface.exchangeItem("CONU9998", "CON6758", "CON1234");
		};
		
		task[19] = () ->{
			libraryInterface.exchangeItem("CONU9999", "CON6758", "CON1234");
		};
		
		Thread thread1 = new Thread(task[0]);
		Thread thread2 = new Thread(task[1]);
		Thread thread3 = new Thread(task[2]);
		Thread thread4 = new Thread(task[3]);
		Thread thread5 = new Thread(task[4]);
		Thread thread6 = new Thread(task[5]);
		Thread thread7 = new Thread(task[6]);
		Thread thread8 = new Thread(task[7]);
		Thread thread9 = new Thread(task[8]);
		Thread thread10 = new Thread(task[9]);
		Thread thread11 = new Thread(task[10]);
		Thread thread12 = new Thread(task[11]);
		Thread thread13 = new Thread(task[12]);
		Thread thread14 = new Thread(task[13]);
		Thread thread15 = new Thread(task[14]);
		Thread thread16 = new Thread(task[15]);
		Thread thread17 = new Thread(task[16]);
		Thread thread18 = new Thread(task[17]);
		Thread thread19 = new Thread(task[18]);
		Thread thread20 = new Thread(task[19]);
		thread1.start();
		thread2.start();
		thread3.start();
		thread4.start();
		thread5.start();
		thread6.start();
		thread7.start();
		thread8.start();
		thread9.start();
		thread10.start();
		thread10.join();
		thread11.start();
		thread12.start();
		thread13.start();
		thread14.start();
		thread15.start();
		thread16.start();
		thread17.start();
		thread18.start();
		thread19.start();
		thread20.start();
		
	}
}

