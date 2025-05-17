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

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.Comparator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.Auto;
import common.Estacion;
import common.InterfazDeServer; 

public class ServerImpl implements InterfazDeServer{
	
	public ServerImpl() throws RemoteException {
		conectarBD();
		UnicastRemoteObject.exportObject(this, 0);
	}
	
	private ArrayList<Auto> BD_copia = new ArrayList<>();
	

	@Override
	public void conectarBD () throws RemoteException {
		Connection connection = null;
		Statement query = null;
		ResultSet resultados = null;
		//PreparedStatement test = null
		
		try {
			String url = "jdbc:mysql://localhost:3306/empresa_colectivos";
			String username = "root";
			String password_BD = "";
			
			connection = DriverManager.getConnection(url, username, password_BD);
			
			//Met
			query = connection.createStatement();
			String sql = "SELECT * FROM auto";
			//INSERT para agregar datos a la BD, PreparedStatement
			//Delete
			//UPDATE
			resultados = query.executeQuery(sql);
			
			while(resultados.next()) {
				//Revisar VALORES de BD
				String patente = resultados.getString("patente");
				String conductor = resultados.getString("conductor");
				String tipoCombustible = resultados.getString("tipo_combustible");
				
				
				Auto newAuto = new Auto(patente, conductor, tipoCombustible);
				
				BD_copia.add(newAuto);
				
				System.out.println("" + patente + "" + conductor + "" + tipoCombustible);
			}
			
			System.out.println(resultados);
			
			connection.close();
			
			
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("No se pudo conectar a la BD");
		}
	}
	
	@Override
	public ArrayList<Auto> getAutos() throws RemoteException {
		return BD_copia;
	}
	
	@Override
	public Auto Auto(String patente, String conductor, String tipoCombustible) throws RemoteException{
		Auto auto = new Auto(patente, conductor, tipoCombustible);
		return auto;
	}
	
	@Override
	public void agregarAuto() throws IOException, RemoteException {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Ingrese la patente del vehículo:");
		String patente = reader.readLine();
		System.out.println("");
		
		System.out.println("Ingrese el conductor del vehículo: ");
		String conductor = reader.readLine();
		System.out.println("");
		
		String tipoCombustible = "";
	    boolean entradaValida = false;

	    while (!entradaValida) {
	        System.out.println("Seleccione el tipo de combustible del vehículo:");
	        System.out.println("1. 93");
	        System.out.println("2. 95");
	        System.out.println("3. 97");
	        System.out.println("4. Kerosene");
	        System.out.println("5. Diesel");
	        System.out.print("Ingrese el número o nombre de la opción: ");
	        String entrada = reader.readLine().trim();

	        switch (entrada) {
	            case "1": tipoCombustible = "93"; entradaValida = true; break;
	            case "2": tipoCombustible = "95"; entradaValida = true; break;
	            case "3": tipoCombustible = "97"; entradaValida = true; break;
	            case "4": tipoCombustible = "KE"; entradaValida = true; break;
	            case "5": tipoCombustible = "DI"; entradaValida = true; break;
	            default:
	                System.out.println("Opción inválida. Intente nuevamente.\n");
	        }
	    }
		
		Auto auto = new Auto(patente, conductor, tipoCombustible);
		
		BD_copia.add(auto);
		insertar_BD(auto);

	}
	
	public void insertar_BD(Auto auto) {
		Connection connection = null;
		PreparedStatement ps = null;
	    try {
	        String url = "jdbc:mysql://localhost:3306/empresa_colectivos";
	        String username = "root";
	        String password_BD = "";

	        connection = DriverManager.getConnection(url, username, password_BD);

	        String sql = "INSERT INTO auto (patente, conductor, tipo_combustible) VALUES (?, ?, ?)";
	        ps = connection.prepareStatement(sql);
	        ps.setString(1, auto.getPatente());
	        ps.setString(2, auto.getConductor());
	        ps.setString(3, auto.getTipoCombustible());

	        int filas = ps.executeUpdate();

	        if (filas > 0) {
	            System.out.println("Auto insertado correctamente.");
	        } else {
	            System.out.println("No se insertó el auto.");
	        }
	    } catch (SQLException e) {
			e.printStackTrace();
			System.out.println("No se pudo conectar a la BD");
		}
	}



	
	@Override
	public String getToken() throws RemoteException{
		String email = "davidm2201@hotmail.com";
        String password = "proyectoparalela1";
        String urlString = "https://api.cne.cl/api/login?email=" + email + "&password=" + password;

        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            int status = con.getResponseCode();
            if (status != 200) {
                System.out.println("Error al autenticar. Código HTTP: " + status);
                return null;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            String response = content.toString();
            String token = response.split(":\"")[1].split("\"")[0];
            return token;

        } catch (Exception e) {
            System.out.println("Error al obtener token: " + e.getMessage());
            return null;
        }
		
	}
	
	@Override
	public String getDataFromApi() throws RemoteException{
		
		String token = getToken();
		
		
		String output = null;
		
		try {
			URL apiUrl = new URL("https://api.cne.cl/api/v4/estaciones");
			
			HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
			
			connection.setRequestMethod("GET");
			
			connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Accept-Encoding", "gzip");
			
			int responseCode = connection.getResponseCode();
			
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuilder response = new StringBuilder();
				
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				
				in.close();
				output = response.toString();
			} else {
				System.out.println("Error al conectar a la API. Código de respuesta: " + responseCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return output;
		
	}
	
	@Override
	public ArrayList<Estacion> getBencinerasPorComunaYMarca(String comuna, String marca) throws RemoteException, JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
        String json = getDataFromApi();
		JsonNode root = mapper.readTree(json);
        JsonNode data = root.path("data");
        ArrayList<Estacion> resultado = new ArrayList<>();
        
        
        for (JsonNode estacion : data) {
		    String comunaActual = estacion.path("ubicacion").path("nombre_comuna").asText();
		    String marcaActual = estacion.path("distribuidor").path("marca").asText();

		    if (comunaActual.equalsIgnoreCase(comuna) && marcaActual.equalsIgnoreCase(marca)) {
		        String direccion = estacion.path("ubicacion").path("direccion").asText();

		        String precio93 = estacion.path("precios").path("93").path("precio").asText(null);
		        String precio95 = estacion.path("precios").path("95").path("precio").asText(null);
		        String precio97 = estacion.path("precios").path("97").path("precio").asText(null);
		        String precioDi  = estacion.path("precios").path("DI").path("precio").asText(null);
		        String precioKe  = estacion.path("precios").path("KE").path("precio").asText(null);
		        
		        Estacion estacionObjeto = new Estacion(marcaActual, comunaActual, direccion, precio93, precio95, precio97, precioDi, precioKe);
		        
		        resultado.add(estacionObjeto);
		    }
		}
	
	    return resultado;
	}

	@Override
	public ArrayList<Estacion> getPrecioxComuna(String tipoDeCombustible, String comuna) throws JsonMappingException, JsonProcessingException, RemoteException {
	    ObjectMapper objectMapper = new ObjectMapper();
	    String json = getDataFromApi();
	    JsonNode root = objectMapper.readTree(json); 
	    JsonNode data = root.path("data");
	    ArrayList<Estacion> resultado = new ArrayList<>();

	    for (JsonNode estacion : data) {
	        String comunaActual = estacion.path("ubicacion").path("nombre_comuna").asText();

	        if (comunaActual.equalsIgnoreCase(comuna)) {
	            String direccion = estacion.path("ubicacion").path("direccion").asText();
	            String marcaActual = estacion.path("distribuidor").path("marca").asText();
	            String precio = estacion.path("precios").path(tipoDeCombustible).path("precio").asText(null);
	            
	            if (precio == null) {
	                precio = "0"; 
	            }

	            String precio93 = estacion.path("precios").path("93").path("precio").asText(null);
	            String precio95 = estacion.path("precios").path("95").path("precio").asText(null);
	            String precio97 = estacion.path("precios").path("97").path("precio").asText(null);
	            String precioDi  = estacion.path("precios").path("DI").path("precio").asText(null);
	            String precioKe  = estacion.path("precios").path("KE").path("precio").asText(null);

	            Estacion estacionObjeto = new Estacion(marcaActual, comunaActual, direccion, precio93, precio95, precio97, precioDi, precioKe);
	            resultado.add(estacionObjeto);
	        }
	    }

	    Collections.sort(resultado, new Comparator<Estacion>() {
	        @Override
	        public int compare(Estacion e1, Estacion e2) {

	            Double precio1 = null;
	            Double precio2 = null;

	            try {
	                precio1 = Double.parseDouble(e1.getPrecio(tipoDeCombustible)); 
	                precio2 = Double.parseDouble(e2.getPrecio(tipoDeCombustible));
	            } catch (NumberFormatException e) {
	                return 0;
	            }

	            return precio1.compareTo(precio2);
	        }
	    });

	    return resultado;
	}
	
}
