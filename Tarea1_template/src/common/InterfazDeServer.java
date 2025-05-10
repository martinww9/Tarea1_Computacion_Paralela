package common;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface InterfazDeServer extends Remote{
	public ArrayList <Persona> getPersonas() throws RemoteException;
	public Persona Persona(String nombre, int edad) throws RemoteException;
	public void agregarPersona() throws IOException;
	
	String getDataFromAPi() throws RemoteException;
	Object[] getUF() throws RemoteException;
}
