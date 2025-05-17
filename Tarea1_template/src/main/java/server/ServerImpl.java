package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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
import java.text.Normalizer;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;
import java.util.zip.GZIPInputStream;

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
	}
	
	@Override
	public String getToken() throws RemoteException {
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

	        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
	        StringBuilder content = new StringBuilder();
	        String inputLine;
	        while ((inputLine = in.readLine()) != null) {
	            content.append(inputLine);
	        }
	        in.close();
	        con.disconnect();

	        String response = content.toString();

	        ObjectMapper mapper = new ObjectMapper();
	        JsonNode root = mapper.readTree(response);
	        String token = root.path("token").asText(null);  // Ajusta "token" si la clave es distinta en la respuesta

	        return token;

	    } catch (Exception e) {
	        System.out.println("Error al obtener token: " + e.getMessage());
	        return null;
	    }
	}

	@Override
	public String getDataFromApi() throws RemoteException {
	    String token = getToken();
	    if (token == null) {
	        throw new RemoteException("No se pudo obtener el token de autenticación.");
	    }

	    String output = null;

	    try {
	        URL apiUrl = new URL("https://api.cne.cl/api/v4/estaciones");
	        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

	        connection.setRequestMethod("GET");
	        connection.setRequestProperty("Authorization", "Bearer " + token);
	        connection.setRequestProperty("Accept-Encoding", "gzip");

	        int responseCode = connection.getResponseCode();

	        if (responseCode == HttpURLConnection.HTTP_OK) {
	            String encoding = connection.getContentEncoding();
	            InputStream inputStream;

	            if ("gzip".equalsIgnoreCase(encoding)) {
	                inputStream = new GZIPInputStream(connection.getInputStream());
	            } else {
	                inputStream = connection.getInputStream();
	            }

	            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
	            StringBuilder response = new StringBuilder();
	            String inputLine;

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
	
	private String normalize(String input) {
	    return Normalizer.normalize(input, Normalizer.Form.NFD)
	                     .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
	                     .toLowerCase();
	}

	@Override
	public ArrayList<Estacion> getBencinerasPorComunaYMarca(String comuna, String marca) throws RemoteException, JsonProcessingException {
	    ObjectMapper mapper = new ObjectMapper();
	    String json = getDataFromApi();

	    if (json == null || json.isEmpty()) {
	        throw new RemoteException("No se recibió respuesta válida de la API.");
	    }

	    
	    JsonNode root = mapper.readTree(json);
	    JsonNode data = root.path("data");
	    ArrayList<Estacion> resultado = new ArrayList<>();

	    for (JsonNode estacion : data) {
	        String comunaActual = estacion.path("ubicacion").path("nombre_comuna").asText();
	        String marcaActual = estacion.path("distribuidor").path("marca").asText();

	        //if (comunaActual.equalsIgnoreCase(comuna) || marcaActual.equalsIgnoreCase(marca)) 
	        if (normalize(comunaActual).equals(normalize(comuna)) || normalize(marcaActual).equals(normalize(marca))) {
	        
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
