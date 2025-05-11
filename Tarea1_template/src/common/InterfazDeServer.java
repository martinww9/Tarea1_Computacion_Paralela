package common;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface InterfazDeServer extends Remote{
	public ArrayList <Auto> getAutos() throws RemoteException;
	public Auto Auto(int patente, String conductor, String tipoCombustible);
	public void agregarPersona() throws IOException;
	
	Object[] getUF() throws RemoteException;
	String getDataFromApi() throws RemoteException;
	
}
