package practica6ChatDistribuido;

/**
 * Clase que crea un hilo del cliente para lectura.
 * 
 * @author Jorge Manzano Anchelergues y Jaime Usero Aranda.
 */

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class ThreadClienteLeer extends Thread{	
			
	/**
	 * InputStream del cliente
	 */
	private ObjectInputStream in;	
	
	/**
	 * LLave privada del cliente
	 */
	private PrivateKey privateCliente;
	
	/**
	 * LLave publica del servidor
	 */
	private PublicKey servidorKey;
		
	/**
	 * Construye un hilo cliente lector
	 * 
	 * @param in tuberia de lectura del cliente
	 * @param key llave privada del cliente
	 * @param serverKey llave publica del servidor
	 * @throws IOException si el stream falla
	 */
	public ThreadClienteLeer(ObjectInputStream in, PrivateKey key, PublicKey serverKey) throws IOException {
		super();		 
		this.in = in;	
		this.privateCliente = key;		
		this.servidorKey = serverKey;
	}
	
	/**
	 * Metodo que recibe un mensaje y lo saca por pantalla
	 */
	public void run() {				
		try {								
			while(true) {
				String respuesta = recibir();
				if(respuesta.equals("/disconnect")) break;
				System.out.println(respuesta);
			}		
		} catch (EOFException | SocketException e) {
			System.out.println("Conexion terminada");
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {			
			e.printStackTrace();
		}       
	}		
	
	/**
	 * Metodo que descifra un mensaje
	 * 
	 * @param texto mensaje a descifrar
	 * @return el mensaje descifrado
	 * @throws Exception por si el descifrado falla
	 */
	private String desfirmar(byte[] encriptado) throws Exception {
		return Utilities.decryptPublic(servidorKey, encriptado);
	}
	  
	/**
	 * Metodo que descifra un mensaje
	 * 
	 * @param texto mensaje a descifrar
	 * @return el mensaje descifrado
	 * @throws Exception por si el descifrado falla
	 */
	private String descifrar(byte[] encriptado) throws Exception {
		return Utilities.decryptPrivate(privateCliente, encriptado);
	}
	  
	
	/**
	 * Metodo que recibe un mensaje descifrado y otro desfirmado,
	 * hashea el descifrado y comprueba si son iguales, lo que asegura
	 * que el mensaje pertenece al servidor
	 * 
	 * @param mensaje el mensaje descifrado
	 * @param mensajeHasheado el mensaje desfirmado
	 * @return el mensaje descifrado si son iguales,
	 * cadena vacia si son distintos
	 * @throws Exception si falla el cifrado
	 */
	private String comprobarMensaje(String mensaje, String mensajeHasheado) throws Exception {
		if (mensajeHasheado.equals(Utilities.hashear(mensaje))) return mensaje; 
		return "";	  
	}
	
	/**
	 * Metodo que recibe un mensaje cifrado y firmado y
	 * los descifra y comprueba
	 * 
	 * @return el mensaje descifrado si son iguales,
	 * cadena vacia si son distintos
	 * @throws ClassNotFoundException si falla al castear la clase
	 * @throws IOException si falla la tuberia
	 * @throws Exception si falla el cifrado
	 */
	private String recibir() throws ClassNotFoundException, IOException, Exception {
		String desfirmado = desfirmar((byte[])in.readObject());
		String descifrado = descifrar((byte[])in.readObject());
		return comprobarMensaje(descifrado, desfirmado);
	}
}