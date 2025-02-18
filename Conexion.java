package practica6ChatDistribuido;

/**
 * Clase que crea protocolos de conexion
 *
 * @author Jorge Manzano Anchelergues y Jaime usero Aranda
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Conexion {
	
	/**
	 * Puerto para la conexión
	 */
    private int puerto; 
    
    /**
     * Host para la conexión
     */
    private String host; 
    
    /**
     * Socket del servidor
     */
    protected ServerSocket ss; 
    
    /**
     * Socket del cliente
     */
    protected Socket cs;        
        
    /**
     * Constructor del Servidor que crea un ServerSocket
     * 
     * @param puerto entero que especifica el puerto
     * @throws IOException
     */
    public Conexion(int puerto) throws IOException {
    	this.puerto = puerto;    			
    	//Se crea el socket para el servidor en un puerto
        ss = new ServerSocket(this.puerto);        
    }
    
    /**
     * Constructor del Cliente que crea un Socket
     * 
     * @param host string que especifica la ip
     * @param puerto entero que especifica el puerto
     * @throws IOException
     */
    public Conexion(String host, int puerto) throws IOException {
    	this.host = host;
    	this.puerto = puerto;
    	//Socket para el cliente en un host en un puerto
        cs = new Socket(this.host, this.puerto);
    }        
}