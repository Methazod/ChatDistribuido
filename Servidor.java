package practica6ChatDistribuido;

/**
 * Clase que crea un servidor
 * 
 * @author Jorge Manzano Anchelergues y Jaime Usero Aranda
 */

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Servidor extends Conexion { //Se hereda de conexión para hacer uso de los sockets y demás
	
	/**
	 * Llave privada del servidor
	 */
	PrivateKey clavePrivada;
	
	/**
	 * LLave publica del servidor
	 */
	PublicKey clavePublica;
	
	/**
	 * Construye un servidor
	 * 
	 * @param puertoServidor puerto del servidor
	 * @throws Exception 
	 */
	public Servidor(int puertoServidor) throws Exception {
    	super(puertoServidor);
    	KeyPair claves = Utilities.generarClaves();
    	clavePrivada = claves.getPrivate();
		clavePublica = claves.getPublic();
    }

	/**
	 * Método para iniciar el servidor
	 */
    public void startServer() {
        try {         
        	ArrayList<Sala> salas = new ArrayList<Sala>();
        	while(true) {        		        		
        		//Esperando conexión
        		System.out.println("El servidor esta esperando...");	        		
        		//Accept comienza el socket y espera una conexión desde un cliente
        		ThreadServidor s = new ThreadServidor(this.ss.accept(), salas, clavePrivada, clavePublica);
        		s.start();
        	}
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }               
}
