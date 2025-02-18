package practica6ChatDistribuido;


 /**
 * Clase que crea un cliente y lo inicia.
 * 
 * @author Jorge Manzano Ancglergues y Jaime Usero Aranda
 */

import java.util.Scanner;

public class MainCliente {
  public static void main(String[] args) {
	  try {
		  Scanner escaner = new Scanner(System.in);
		  
		  System.out.print("Introduce el puerto del servidor: ");
		  int puertoServidor = escaner.nextInt();	  
		  escaner.nextLine();
		  
		  System.out.print("Introduce el host del servidor: ");
		  String hostServidor = escaner.nextLine();	  
		  
		  Cliente cliente = new Cliente(hostServidor, puertoServidor);		  
		  cliente.startClient(escaner);		  	 
	  } catch (Exception e) {
		e.printStackTrace();
	}
  }  
  
}