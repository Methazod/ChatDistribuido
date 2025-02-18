package practica6ChatDistribuido;

/**
 * Clase que crea un hilo servidor.
 * 
 * @author Jorge Manzano Anchelergues y Jaime Usero Aranda.
 */

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class ThreadServidor extends Thread{	
	
	/**
	 * Opciones relacionadas a los chats
	 */
	private final int UNIRSE = 1;
	private final int CREAR = 2;
	private final int LISTAR = 3;
	
	/**
	 * InputStream del cliente
	 */
	private ObjectInputStream in;
	
	/**
	 * OutputStream del cliente
	 */
	private ObjectOutputStream out;				
	
	/**
	 * Salas disponibles
	 */
	private ArrayList<Sala> salas;
	
	/**
	 * Llave privada del servidor
	 */
	PrivateKey clavePrivada;
	
	/**
	 * LLave publica del servidor y cliente
	 */
	PublicKey clavePublica, claveCliente;
	
	/**
	 * Construye un hilo servidor
	 * 
	 * @param cliente el socket del cliente
	 * @param salas lista con todas las salas
	 * @param priv llave privada del servidor
	 * @param pub llave publica del servidor
	 * @throws IOException si el stream falla
	 */
	public ThreadServidor(Socket cliente, ArrayList<Sala> salas, PrivateKey priv, PublicKey pub) throws IOException {
		super();		
		this.salas = salas;
		this.clavePrivada = priv;
		this.clavePublica = pub;
		// Cargamos los stream de los clientes.
		in = new ObjectInputStream(cliente.getInputStream());		
		out = new ObjectOutputStream(cliente.getOutputStream());
	}
	
	/**
	 * Metodo que recibe la llave publica del cliente, que guardara para mantener la
	 * conversacion cifrada. Despues, recibira una opcion con la que creara, listara o
	 * unira a un cliente a una de las salas. Por ultimo, se comunicara con la sala 
	 * elegida por el cliente y enviara los mensajes del cliente a los demas usuarios.
	 */
	public void run() {				
		try {		
			claveCliente = (PublicKey) in.readObject();
			out.writeObject(clavePublica);
			out.flush();
			
			System.out.println("Servidor conectado");
			Sala salaGestionada = null;
			boolean running = true;
			while(running) {
				int opcion = Integer.parseInt(recibir());				
				if(opcion == LISTAR) {
					enviar(getSalas());					
				}
				else if(opcion == CREAR) {
					enviar("Introduce el nombre de la sala: ");					
					String nombre = recibir() + crearIdentificador();
					enviar("Introduce la contraseña de la sala");					
					String pass;					
					pass = recibir();
					if(this.salas.add(pass.isBlank()?new Sala(nombre):new Sala(nombre, pass))) enviar("Sala creada correctamente");						
					else enviar("Error, la sala no pudo ser creada");					
				}
				else if(opcion == UNIRSE) {
					enviar("Salas:\n" + getSalas() + "\n" + "Introduce el nombre de la sala: ");					
					String nombre = recibir();
					for(Sala sala : salas) {						
						if(sala.getNombre().equalsIgnoreCase(nombre)) {
							if(sala.getPass().isBlank()) {
								sala.addUser(out);
								sala.addKey(claveCliente);
								enviar("Unido con exito a la sala " + sala.getNombre());								
								salaGestionada = sala;
								running = false;
							} else {
								enviar("Introduce la contraseña de la sala");								
								if(sala.getPass().equalsIgnoreCase(recibir())) {
									sala.addUser(out);
									sala.addKey(claveCliente);
									enviar("Unido con exito a la sala " + sala.getNombre());									
									salaGestionada = sala;
									running = false;
								} else {
									enviar("Error al unirse, la contraseña no es correcta");																	
								}								
							}							
						} else {
							enviar("Error al unirse, el chat no existe");							
						}
					}
				}
			}		
			ArrayList<PublicKey> llavesBorrar = new ArrayList<PublicKey>();
			ArrayList<ObjectOutputStream> usuariosBorrar = new ArrayList<ObjectOutputStream>();
			while(true) {						
				if(!llavesBorrar.isEmpty()) llavesBorrar.clear();
				if(!usuariosBorrar.isEmpty()) usuariosBorrar.clear();
				String mensaje = recibir();
				if(mensaje.equals("/disconnect")) break;
				salaGestionada.addMensaje(mensaje);				
				for(int i = 0; i < salaGestionada.getCantidadUsuarios(); i++) {
					try {
						// Firma
						salaGestionada.getUser(i).writeObject(firmar(salaGestionada.getUltimoMensaje()));
						salaGestionada.getUser(i).flush();
						// Cifrado
						salaGestionada.getUser(i).writeObject(Utilities.encryptPublic(salaGestionada.getKey(i), salaGestionada.getUltimoMensaje()));
						salaGestionada.getUser(i).flush();
					} catch (SocketException e) {
						llavesBorrar.add(salaGestionada.getKey(i));
						usuariosBorrar.add(salaGestionada.getUser(i));
					}					
				}			
				if(llavesBorrar.isEmpty()) continue;
				if(usuariosBorrar.isEmpty()) continue;
				salaGestionada.desconectar(llavesBorrar, usuariosBorrar);
			}
			salaGestionada.desconectar(claveCliente, out);
			out.close();
			in.close();
		} catch (EOFException e) {
			System.out.println("Conexion terminada");
		} catch (IOException | ClassNotFoundException | InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}	
	
	/**
	 * Metodo que listara todas las salas
	 * 
	 * @return todas las salas listadas
	 */
	private String getSalas() {
		String respuesta = "";
		for(Sala sala : salas)
			respuesta += sala.getNombre() + "\n";
		return respuesta;
	}
	
	/**
	 * Metodo que creara un identificador unico para cada sala
	 * 
	 * @return el identificador
	 */
	private String crearIdentificador() {
		String id = "#";
		for (int i = 0; i < 6; i++) {
			id += Math.random() < 0.5 ? obtenerNumero() : (Math.random() < 0.5 ? obtenerLetra().toUpperCase() : obtenerLetra());
		}
		return id;
	}
	
	/**
	 * Metodo que obtiene un numero aleatorio entre 0 y 9
	 * y lo devuelve siendo un String
	 * 
	 * @return
	 */
	private String obtenerNumero() {		
		return ""+((int)(Math.random()*10));
	}
	
	/**
	 * Metodo que devuelve una letra del abecedario de manera aleatoria
	 * 
	 * @return
	 */
	private String obtenerLetra() {
		String[] letras = {
			"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
            "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
		};
		return letras[((int)(Math.random()*(letras.length-1)))];
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
		return Utilities.encryptPublic(claveCliente, texto);
	}
	  
	/**
	 * Metodo que desfirma un mensaje
	 * 
	 * @param texto mensaje a desfirmar
	 * @return el mensaje desfirmado
	 * @throws Exception por si el descifrado falla
	 */
	private String desfirmar(byte[] encriptado) throws Exception {
		return Utilities.decryptPublic(claveCliente, encriptado);
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
	 * que el mensaje pertenece al usuario
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
	 * Metodo que envia un mensaje cifrado y firmado por el servidor
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