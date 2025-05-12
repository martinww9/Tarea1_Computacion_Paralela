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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.Auto;
import common.Estacion;
import common.InterfazDeServer; 
import common.Persona;

public class ServerImpl implements InterfazDeServer{
	
	
	
	public ServerImpl() throws RemoteException {
		conectarBD();
		UnicastRemoteObject.exportObject(this, 0);
		//crearBD();
	}
	
	private ArrayList<Auto> BD_copia = new ArrayList<>();
	
	
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
				String patente = resultados.getString("patente");
				String conductor = resultados.getString("conductor");
				String tipoCombustible = resultados.getString("Tipo de Combustible");
				
				
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
	public ArrayList<Auto> getAutos() {
		return BD_copia;
	}
	
	@Override
	public Auto Auto(String patente, String conductor, String tipoCombustible) {
		Auto auto = new Auto(patente, conductor, tipoCombustible);
		return auto;
	}
	
	@Override
	public void agregarAuto() throws IOException {
		
		System.out.println("Ingrese la patente del vehículo:");
		String patente = (new BufferedReader(new InputStreamReader(System.in)).readLine());
		System.out.println("");
		
		System.out.println("Ingrese el conductor del vehículo: ");
		String conductor = (new BufferedReader(new InputStreamReader(System.in)).readLine());
		System.out.println("");
		
		System.out.println("Ingrese el tipo de combustible del vehículo: ");
		String tipoCombustible = (new BufferedReader(new InputStreamReader(System.in)).readLine());
		System.out.println("");
		
		Auto auto = new Auto(patente, conductor, tipoCombustible);
		
		BD_copia.add(auto);
	}
	
	
	public String getToken() {
		String email = "CORREO";
        String password = "CONTRASEÑA";
        String urlString = "https://api.cne.cl/api/login?email=" + email + "&password=" + password;

        try {
            // Crear la URL de autenticación
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            // Verificar el estado de la respuesta
            int status = con.getResponseCode();
            if (status != 200) {
                System.out.println("Error al autenticar. Código HTTP: " + status);
                return null;
            }

            // Leer la respuesta de la API
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            // Extraer el token de la respuesta
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
	
	
	public String getPrecioxComuna(String tipoDeCombustible) {
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		try { 
			JsonNode jsonNode = objectMapper.readTree(output);
 			String codigo = jsonNode.get("").get("").asText();
 			String nombre = jsonNode.get("").get("").asText();
 			
 			return new Object[] {codigo, nombre};
 			
		} catch(JsonMappingException e) {
			e.printStackTrace();
		} 
		return null;
		
		
	}
	
}
