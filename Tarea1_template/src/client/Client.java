package client;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import common.InterfazDeServer;
import common.Persona;

public class Client {

	private InterfazDeServer server;
	public Client() {};
	
	public void startClient() throws RemoteException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry("localhost", 1030);
		server = (InterfazDeServer) registry.lookup("server");
	}
	
	public void mostrarPersonas() throws RemoteException {
		ArrayList<Persona> personas = server.getPersonas();
		int contador = 1;
		
		System.out.println("Personas:");
		
		for(Persona persona : personas) {
			
			System.out.println(contador + ". " + persona.getNombre() + " " + persona.getEdad() + "." );
			contador++;
		}
		System.out.println("");
	}
	
	public void agregarPersona() throws IOException{
		
		server.agregarPersona();
		
	}
	
	String getDataFromApi() throws RemoteException {
		return server.getDataFromAPi();
	}
	
	Object[] getUF() throws RemoteException{
		Object[] valores_de_uf = server.getUF();
		
		if(valores_de_uf == null) {
			//error
		} else {
			String codigo = (String) valores_de_uf[0];
			String nombre = (String) valores_de_uf[1];
		}
		
		return valores_de_uf;
	}
	
}
