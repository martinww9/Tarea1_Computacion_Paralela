package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;

public class RunClient {
	
	public static void main(String[] args) throws NotBoundException, NumberFormatException, IOException{
		
		Client client = new Client();
		client.startClient();
		
		System.out.println("Cliente Conectado!! \n");
		
		int opcion = 1;
		
		while(opcion != 0) {
			
			System.out.println("Escoja una de las siguientes opciones: \n");
			System.out.println("1. Mostrar Personas");
			System.out.println("2. Agregar Persona");
			System.out.println("3. Para ver datos de la API");
			System.out.println("4. Salir\n");
			
			
			
			
			opcion = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
			
			System.out.println("");
			
			switch (opcion) {
			
				case 1:
					
					client.mostrarPersonas();
					break;
				case 2:
					
					client.agregarPersona();
					
					break;
				case 3:
					client.getDataFromApi();
					
				case 4:
					opcion = 0;
					break;
			}
			
		}
		
	}
	
}
