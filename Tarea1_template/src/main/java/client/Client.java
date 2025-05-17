package client;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import common.InterfazDeServer;
import common.RegistroCompra;
import common.Auto;
import common.Estacion;

public class Client {

    private InterfazDeServer server;
    
    public Client() {};

    public void startClient() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1032);
        server = (InterfazDeServer) registry.lookup("server");
    }

    public void mostrarAutos() throws RemoteException {
        ArrayList<Auto> autos = server.getAutos();
        int contador = 1;

        System.out.println("Autos:");

        for(Auto auto : autos) {

            System.out.println(contador + ". Patente: " + auto.getPatente() + ", Conductor: " + auto.getConductor() + ", Combustible: " + auto.getTipoCombustible());
            contador++;
        }
        System.out.println("");
    }

    public void agregarAuto() throws IOException {
        server.agregarAuto();  // Si necesitas modificar la lógica de agregar, debes adaptarla al contexto de "Auto"
    }

    String getDataFromApi() throws RemoteException {
        
    	System.out.println(server.getDataFromApi());
    	
    	return server.getDataFromApi();
    }


	public void seleccionarAuto() throws RemoteException, JsonMappingException, JsonProcessingException {
		ArrayList<Auto> autos = server.getAutos();
		
		System.out.println("Seleccione un auto:");
	    for (int i = 0; i < autos.size(); i++) {
	        Auto auto = autos.get(i);
	        System.out.println((i + 1) + ". Patente: " + auto.getPatente());
	    }
	    
	    // Leer la entrada del usuario para seleccionar un auto
	    Scanner scanner = new Scanner(System.in);
	    System.out.print("Ingrese el número del auto que desea seleccionar: ");
	    int seleccion = scanner.nextInt();
	    
	    Auto autoSeleccionado = null;
	    
	    // Validar la selección
	    if (seleccion < 1 || seleccion > autos.size()) {
	        System.out.println("Selección inválida.");
	    } else {
	        // Obtener el auto seleccionado
	        autoSeleccionado = autos.get(seleccion - 1);
	        System.out.println("Auto seleccionado: Patente " + autoSeleccionado.getPatente());
	    }
	    
	    menuSeleccion(autoSeleccionado);
	    
	    
	    
}

	private void menuSeleccion(Auto autoSeleccionado) throws JsonMappingException, JsonProcessingException, RemoteException {

		Scanner scanner = new Scanner(System.in);
	    boolean salir = false;
	    
	    while (!salir) {
	        // Mostrar opciones del menú
	        System.out.println("1. Registrar una compra");
	        System.out.println("2. Buscar bencineras por comuna");
	        System.out.println("3. Salir");
	        System.out.print("Seleccione una opción: ");
	        
	        // Leer la entrada del usuario
	        int opcion = scanner.nextInt();
	        
	        switch (opcion) {
	            case 1:
	                // Si selecciona registrar una compra
	                registrarCompra(autoSeleccionado);
	                break;
	                
	            case 2:
	                // Si selecciona buscar bencineras por comuna
	                buscarBencinerasPorComuna(autoSeleccionado);
	                break;
	                
	            case 3:
	                // Si selecciona salir
	                salir = true;
	                System.out.println("Saliendo...");
	                break;
	                
	            default:
	                System.out.println("Opción inválida, por favor seleccione nuevamente.");
	        }
	    }
	}
	
	private int generarIdCompra() {
	    Random random = new Random();
	    return random.nextInt(100000) + 1; // genera un número entre 1 y 100000
	}


	private void registrarCompra(Auto autoSeleccionado) throws JsonMappingException, JsonProcessingException, RemoteException {

		System.out.println("Registrar compra para el auto con patente: " + autoSeleccionado.getPatente());

	    System.out.print("Ingrese la comuna donde realizó la compra: ");
	    Scanner scanner = new Scanner(System.in);;
		
	    String comuna = scanner.nextLine();

	    System.out.print("Ingrese la marca de la estación de servicio donde compró: ");
	    String marca = scanner.nextLine();

	    
	    ArrayList<Estacion> estaciones = server.getBencinerasPorComunaYMarca(comuna, marca);
	    
	    if (estaciones.isEmpty())  System.out.println("No se encontraron estaciones para esa comuna y marca.");
	    
	    System.out.println("Estaciones disponibles en " + comuna + " de la marca " + marca + ":");

	    for (int i = 0; i < estaciones.size(); i++) {
	        System.out.println((i + 1) + ". " + estaciones.get(i).getDireccion());
	    }

	    System.out.print("Seleccione una estación ingresando el número correspondiente: ");
	    int seleccion = scanner.nextInt();
	    scanner.nextLine(); 

	    if (seleccion < 1 || seleccion > estaciones.size()) {
	        System.out.println("Selección inválida.");
	        return;
	    }

	    Estacion estacionSeleccionada = estaciones.get(seleccion - 1);
	    System.out.println("Has seleccionado la estación en: " + estacionSeleccionada.getDireccion());
	    
	    String tipoBencinaAuto = autoSeleccionado.getTipoCombustible();
	    String precioSeleccionado = "";

	    switch (tipoBencinaAuto) {
	        case "93":
	            precioSeleccionado = estacionSeleccionada.getPrecio93();
	            break;
	        case "95":
	            precioSeleccionado = estacionSeleccionada.getPrecio95();
	            break;
	        case "97":
	            precioSeleccionado = estacionSeleccionada.getPrecio97();
	            break;
	        case "DI":
	            precioSeleccionado = estacionSeleccionada.getPrecioDi();
	            break;
	        case "KE":
	            precioSeleccionado = estacionSeleccionada.getPrecioKe();
	            break;
	        default:
	            System.out.println("Tipo de bencina no válido.");
	            return;
	            
	    }
	    
	    float gastoTotal = 0;
	    boolean entradaValida = false;

	    while (!entradaValida) {
	        System.out.println("Ingrese el gasto total en bencina (en moneda): ");
	        String entrada = scanner.nextLine();
	        try {
	            gastoTotal = Float.parseFloat(entrada);
	            entradaValida = true;
	        } catch (NumberFormatException e) {
	            System.out.println("El ingreso no corresponde a un número válido. Intente nuevamente.");
	        }
	    }
	    
	    entradaValida = false;
	    

	    float precioPorLitro = Float.parseFloat(precioSeleccionado);
	    float litros = gastoTotal / precioPorLitro;

        System.out.println("La cantidad de litros comprados es: " + litros);
        
        
	    System.out.println("Ingrese la fecha de la compra. FORMATO: Año-Mes-Día, Ejemplo: 2000-02-30");
        String fechaCompra = scanner.nextLine();
        
        
        int idCompra = generarIdCompra();
        RegistroCompra registroCompra = new RegistroCompra(idCompra, autoSeleccionado.getPatente(), litros, gastoTotal, fechaCompra);
    
	    System.out.println("Compra registrada en la comuna '" + comuna + "' en la estación de servicio '" + marca + "'.");
	    
	}
	
	

	private void buscarBencinerasPorComuna(Auto autoSeleccionado) throws JsonMappingException, JsonProcessingException, RemoteException {
	    System.out.println("Buscar bencineras por comuna...");
	    Scanner scanner = new Scanner(System.in);;
	    
	    System.out.println("Ingrese comuna");
	    String comuna = scanner .nextLine();

	    String tipoDeCombustible = autoSeleccionado.getTipoCombustible();
	     
	    ArrayList<Estacion> bencineras = server.getPrecioxComuna(tipoDeCombustible,  comuna);
	    
	    for(int i = 0 ; i < bencineras.size() ; i++) {
	    	Estacion bencineraActual = bencineras.get(i);
	    	String ubicacion = bencineraActual.getDireccion();
	    	String precio = bencineraActual.getPrecio(tipoDeCombustible);
	    	String marca = bencineraActual.getMarcaActual();
	    	
		    System.out.println("Marca: " + marca + "Precio: " + precio + "Ubicación: "+ ubicacion);
	    }
	    
	}
	
}
