package common;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;


public interface InterfazDeServer extends Remote{
	public ArrayList <Auto> getAutos() throws RemoteException;
	public Auto Auto(int patente, String conductor, String tipoCombustible);
	
	Object[] getUF() throws RemoteException;
	String getDataFromApi() throws RemoteException;
	
	public void agregarAuto() throws IOException;
	public ArrayList<Estacion> getBencinerasPorComunaYMarca(String comuna, String marca) throws RemoteException, JsonMappingException, JsonProcessingException;
	
}
