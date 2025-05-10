package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import common.InterfazDeServer; 
import common.Persona;

public class ServerImpl implements InterfazDeServer{
	
	
	
	public ServerImpl() throws RemoteException {
		conectarBD();
		UnicastRemoteObject.exportObject(this, 0);
		//crearBD();
	}
	
	private ArrayList<Persona> BD_copia = new ArrayList<>();
	
	
	/*
	private void crearBD() { 
		Persona persona1 = new Persona("matias", 27);
		Persona persona2 = new Persona("Maria eugenia", 31);
		
		BD_personas.add(persona1);
		BD_personas.add(persona2);
	}*/
	
	public void conectarBD () {
		Connection connection = null;
		Statement query = null;
		ResultSet resultados = null;
		//PreparedStatement test = null
		
		try {
			String url = "jdbc:mysql://localhost:3306/ici4344";
			String username = "root";
			String password_BD = "";
			
			connection = DriverManager.getConnection(url, username, password_BD);
			
			//Met
			query = connection.createStatement();
			String sql = "SELECT * FROM bd_acme";
			//INSERT para agregar datos a la BD, PreparedStatement
			//Delete
			//UPDATE
			resultados = query.executeQuery(sql);
			
			while(resultados.next()) {
				//Revisar VALORES de BD
				String nombre = resultados.getString("nombre");
				String apellido = resultados.getString("nombre");
				
				Persona newPersona = new Persona(nombre, apellido);
				
				BD_copia.add(newPersona):
				
				System.out.println("" + nombre + "" + apellido);
			}
			
			System.out.println(resultados);
			
			connection.close();
			
			
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("No se pudo conectar a la BD");
		}
	}
	
	
	@Override
	public ArrayList<Persona> getPersonas() {
		return BD_personas;
	}
	
	@Override
	public Persona Persona(String nombre, int edad) {
		Persona persona = new Persona(nombre, edad);
		return persona;
	}
	
	@Override
	public void agregarPersona() throws IOException {
		
		System.out.println("Ingrese el nombre de la persona:");
		String nombre = new BufferedReader(new InputStreamReader(System.in)).readLine();
		System.out.println("");
		
		System.out.println("Ingrese la edad de la persona: ");
		int edad = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
		System.out.println("");
		
		Persona persona = new Persona(nombre, edad);
		
		BD_personas.add(persona);
	}
	
	/*@Override
	public Object[] getUF() {
		 return Object objeto;
	}
	*/
	
	
	@Override
	public String getDataFromApi() {
		String output = null;
		
		try {
			URL apiUrl = new URL("");
			
			
			HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
			
			connection.setRequestMethod("GET");
			
			int responseCode = connection.getResponseCode();
			
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream));
				String inputLine;
				StringBuilder response = new StringBuilder();
				
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				
				in.close();
				output = response.toString():
			} else {
				System.out.println("Error al conectar a la API. Código de respuesta: " + responseCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return output;
		
		
		
		
		
	}
}
