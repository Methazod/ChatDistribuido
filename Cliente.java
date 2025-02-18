package practica6ChatDistribuido;

/**
 * Clase que crea un cliente
 * 
 * @author Jorge Manzano Anchelergues y Jaime Usero Aranda
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;

public class Cliente extends Conexion {
	
	/**
	 * InputStream del cliente
	 */
	private ObjectInputStream in;
	
	/**
	 * OutputStream del cliente
	 */
	private ObjectOutputStream out;	
	
	/**
	 * Llave privada del cliente
	 */
	PrivateKey clavePrivada;
	
	/**
	 * LLave publica del cliente y servidor
	 */
	PublicKey clavePublica, servidor;
	
	/**
	 * Construye un cliente
	 * 
	 * @param hostServidor el host del servidor a conectar
	 * @param puertoServidor puerto del servidor a conectar
	 * @throws Exception 
	 */
    public Cliente(String hostServidor, int puertoServidor) throws Exception {
    	super(hostServidor, puertoServidor);
    	out = new ObjectOutputStream(this.cs.getOutputStream());
		in = new ObjectInputStream(this.cs.getInputStream());
		KeyPair claves = Utilities.generarClaves();
		clavePrivada = claves.getPrivate();
		clavePublica = claves.getPublic();
    }  

    /**
     * Metodo para iniciar el cliente, intercambiara claves con el servidor
     * e iniciara la comunicacion con el para unirse a una sala
     * 
     * @param escaner el escaner a usar
     * @throws Exception si algun cifrado falla
     */
	public void startClient(Scanner escaner) throws Exception {		
		out.writeObject(clavePublica);
		out.flush();
		servidor = (PublicKey) in.readObject();
				
		int opcion = 0;
		while(true) {			
			opcion = obtenerOpcion(escaner);			
			if(opcion == 4) break;			
			enviar(""+opcion);			
	    	String respuesta = recibir();
	    	System.out.print(respuesta);
	    	if(opcion == 2) {
	    		enviar(escaner.nextLine());
		    	respuesta = recibir();		    	
		    	System.out.print(respuesta+"(Si no quieres que tenga contraseña, introduce no): ");
		    	String pass = escaner.nextLine();
		    	if(pass.equalsIgnoreCase("No")) enviar("");
		    	else enviar(pass);
		    	respuesta = recibir();
		    	System.out.println(respuesta);
	    	}
	    	if(opcion == 1) {
	    		String nombre = escaner.nextLine();
	    		enviar(nombre);
	    		respuesta = recibir();
	    		System.out.println(respuesta);
	    		if(respuesta.equalsIgnoreCase("Unido con exito a la sala " + nombre)) break;	    		
	    		enviar(escaner.nextLine());
	    		respuesta = recibir();
	    		System.out.println(respuesta);
	    		if(respuesta.equalsIgnoreCase("Unido con exito a la sala " + nombre)) break;	    		    		
	    	}	    	
	    	System.out.println("\n");
		}
		if(opcion == 1) {						
			new ThreadClienteLeer(in, clavePrivada, servidor).start();
			ThreadClienteEscribir h = new ThreadClienteEscribir(out, escaner, clavePrivada, servidor);
			h.start();
			h.join();
			escaner.close();
			this.in.close();
			this.out.close();
			this.cs.close();
		}		
    }
	  
	/**
	 * Metodo que obtiene una opcion valida
	 * que el cliente proporcionara
	 * 
	 * @param escaner el escaner para obtener la opcion
	 * @return la opcion
	 */
	private int obtenerOpcion(Scanner escaner) {	  
		int opcion = -129648;		  
		while(!(opcion == 1 || opcion == 2 || opcion == 3 || opcion == 4)) {
			if(opcion != -129648) System.out.println("La opcion no es valida, porfavor, introduzca una valida");
			System.out.println("1. Unirse a una sala de chat ya creada anteriormente");
			System.out.println("2. Crear una nueva sala de chat");
			System.out.println("3. Listar las salas disponibles");
			System.out.println("4. Fin");
			opcion = escaner.nextInt();
			escaner.nextLine();			  
		}				
		return opcion;
	}  
	
	/**
	 * Metodo que firma un mensaje que anteriormente ha sido hasheado
	 * 
	 * @param texto mensaje a firmar
	 * @return el mensaje firmado
	 * @throws Exception por si el cifrado falla
	 */
	private byte[] firmar(String texto) throws Exception {
		return Utilities.encryptPrivate(clavePrivada, Utilities.hashear(texto));
	}
	  
	/**
	 * Metodo que cifra un mensaje
	 * 
	 * @param texto mensaje a cifrar
	 * @return el mensaje cifrado
	 * @throws Exception por si el cifrado falla
	 */
	private byte[] cifrar(String texto) throws Exception {
		return Utilities.encryptPublic(servidor, texto);
	}
	  
	/**
	 * Metodo que desfirma un mensaje
	 * 
	 * @param texto mensaje a desfirmar
	 * @return el mensaje desfirmado
	 * @throws Exception por si el descifrado falla
	 */
	private String desfirmar(byte[] encriptado) throws Exception {
		return Utilities.decryptPublic(servidor, encriptado);
	}
	  
	/**
	 * Metodo que descifra un mensaje
	 * 
	 * @param texto mensaje a descifrar
	 * @return el mensaje descifrado
	 * @throws Exception por si el descifrado falla
	 */
	private String descifrar(byte[] encriptado) throws Exception {
		return Utilities.decryptPrivate(clavePrivada, encriptado);
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
