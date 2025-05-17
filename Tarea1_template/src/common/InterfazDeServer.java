package common;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;


public interface InterfazDeServer extends Remote{
	public void conectarBD ();
	
	public ArrayList <Auto> getAutos() throws RemoteException;
	public Auto Auto(String patente, String conductor, String tipoCombustible);
	
	public String getToken() throws RemoteException;
	String getDataFromApi() throws RemoteException;
	
	public void agregarAuto() throws IOException;
	public ArrayList<Estacion> getBencinerasPorComunaYMarca(String comuna, String marca) throws RemoteException, JsonMappingException, JsonProcessingException;
	public ArrayList<Estacion> getPrecioxComuna(String tipoDeCombustible, String comuna) throws JsonMappingException, JsonProcessingException, RemoteException;
}
