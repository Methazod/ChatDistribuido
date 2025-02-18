package practica6ChatDistribuido;

/**
 * Clase que crea un hilo del cliente para escritura.
 * 
 * @author Jorge Manzano Anchelergues y Jaime Usero Aranda.
 */

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;

public class ThreadClienteEscribir extends Thread{	
	
	/**
	 * OutputStream del cliente
	 */
	private ObjectOutputStream out;				
		
	/**
	 * Escaner por el que escribira el cliente
	 */
	private Scanner escaner;
	
	/**
	 * LLave privada del cliente
	 */
	private PrivateKey privadaCliente;
	
	/**
	 * LLave privada del servidor
	 */
	private PublicKey serverKey;	
	
	/**
	 * Construye un hilo cliente escritor
	 * 
	 * @param out tuberia de escritura del cliente
	 * @param escaner el escaner por el que escribira el cliente
	 * @param key llave privada del cliente
	 * @param sKey llave publica del servidor	 
	 * @throws IOException si el stream falla
	 */
	public ThreadClienteEscribir(ObjectOutputStream out, Scanner escaner, PrivateKey key, PublicKey sKey) throws IOException {
		super();		 
		this.out = out;			
		this.escaner = escaner;
		this.privadaCliente = key;
		this.serverKey = sKey;		
	}
	
	/**
	 * Metodo que manda un mensaje por la tuberia.
	 */
	public void run() {				
		try {				
			while(true) {		
				String mensaje = escaner.nextLine();				
				enviar(mensaje);
				if(mensaje.equals("/disconnect")) break;
			}		
		} catch (EOFException e) {
			System.out.println("Conexion terminada");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {			
			e.printStackTrace();
		}	
	}			
	
	/**
	 * Metodo que firma un mensaje que anteriormente ha sido hasheado
	 * 
	 * @param texto mensaje a firmar
	 * @return el mensaje firmado
	 * @throws Exception por si el cifrado falla
	 */
	private byte[] firmar(String texto) throws Exception {
		return Utilities.encryptPrivate(privadaCliente, Utilities.hashear(texto));
	}
	  
	/**
	 * Metodo que cifra un mensaje
	 * 
	 * @param texto mensaje a cifrar
	 * @return el mensaje cifrado
	 * @throws Exception por si el cifrado falla
	 */
	private byte[] cifrar(String texto) throws Exception {
		return Utilities.encryptPublic(serverKey, texto);
	}
	 
	/**
	 * Metodo que envia un mensaje cifrado y firmado por el cliente
	 * 
	 * @param text el mensaje a enviar
	 * @throws IOException si las tuberias fallan
	 * @throws Exception si el cifrado falla
	 */
	private void enviar(String text) throws IOException, Exception {
		out.writeObject(firmar(text));
		out.flush();
		out.writeObject(cifrar(text));
		out.flush();
	}
}