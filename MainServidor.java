package practica6ChatDistribuido;

/**
 * Clase que crea un servidor y lo inicia.
 * 
 * @author Jorge Manzano Ancglergues y Jaime Usero Aranda
 */

import java.util.Scanner;

public class MainServidor {		
  public static void main(String[] args) throws Exception {
	  Scanner escaner = new Scanner(System.in);
	  System.out.print("Introduce el puerto del servidor: ");
	  Servidor serv = new Servidor(escaner.nextInt()); //Se crea el servidor
	  escaner.close();
	  System.out.println("");
	  
      System.out.println("Iniciando Servidor\n");
      serv.startServer(); //Se inicia el servidor
  }    
}