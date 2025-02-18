package practica6ChatDistribuido;

import java.io.ObjectOutputStream;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Clase que crea una sala
 * 
 * @author Jorge Manzano Anchelergues y Jaime Usero Aranda
 */

public class Sala {
	
	/**
	 * El nombre de la sala
	 */
	private String nombre;
	
	/**
	 * La contraseña de sala, si no tiene,
	 * se queda en blanco
	 */
	private String pass;
	
	/**
	 * La lista de los socket de usuarios
	 */
	private ArrayList<ObjectOutputStream> usuarios;
	
	/**
	 * La lista de los mensajes
	 */
	private ArrayList<String> mensajes;
	
	/**
	 * Lista con las llaves publicas de los clientes
	 */
	private ArrayList<PublicKey> usersKeys;
	
	/**
	 * Semaforo que no dejara que se guarden mensajes a la vez
	 */
	private Semaphore escritura;
	
	/**
	 * Construye una sala con contraseña
	 * 
	 * @param nombre el nombre de la sala
	 * @param pass la contraseña de la sala
	 */
	public Sala(String nombre, String pass) {
		this.nombre = nombre;
		this.pass = pass;
		this.usuarios = new ArrayList<ObjectOutputStream>();
		this.mensajes = new ArrayList<String>();
		this.usersKeys = new ArrayList<PublicKey>();
		this.escritura = new Semaphore(1);
	}
	
	/**
	 * Construye una sala sin contraseña
	 * 
	 * @param nombre el nombre de la sala
	 */
	public Sala(String nombre) {
		this.nombre = nombre;
		this.pass = "";
		this.usuarios = new ArrayList<ObjectOutputStream>();
		this.mensajes = new ArrayList<String>();
		this.usersKeys = new ArrayList<PublicKey>();
		this.escritura = new Semaphore(1);
	}
	
	/**
	 * Getter del nombre de la sala
	 * 
	 * @return el nombre de la sala
	 */
	public String getNombre() {
		return nombre;
	}
	
	/**
	 * Getter de la contraseña de la sala
	 * 
	 * @return la contraseña de la sala
	 */
	public String getPass() {
		return pass;
	}
	
	/**
	 * Añade un usuario a la sala
	 * 
	 * @param usuario el usuario a añadir
	 */
	public void addUser(ObjectOutputStream usuario) {
		usuarios.add(usuario);
	}
	
	/**
	 * Devuelve la cantidad de usuarios
	 */
	public int getCantidadUsuarios() {	
		return this.usuarios.size();
	}
	
	/**
	 * Devuelve el usuario en la posicion requerida
	 * 
	 * @param posicion la posicion del elemento
	 * @return el usuario
	 */
	public ObjectOutputStream getUser(int posicion) {
		return usuarios.get(posicion);
	}
	
	/**
	 * Añade un usuario a la sala
	 * 
	 * @param usuario el usuario a añadir
	 * @throws InterruptedException 
	 */
	public void addMensaje(String mensaje) throws InterruptedException {
		escritura.acquire();
		mensajes.add(mensaje);
		escritura.release();
	}
	
	/**
	 * Devuelve todos los mensajes
	 */
	public String getMensajes() throws InterruptedException {
		escritura.acquire();
		String mensajes = "";
		for (String mensaje : this.mensajes) {
			if(mensaje.equals(getUltimoMensaje())) mensajes += mensaje;
			else mensajes += mensaje + "\n";
		}
		escritura.release();
		return mensajes;
	}
	
	/**
	 * Devuelve el ultimo mensaje
	 */
	public String getUltimoMensaje() throws InterruptedException {		
		escritura.acquire();
		String mensaje = this.mensajes.getLast();
		escritura.release();
		return mensaje;
	}			
	
	/**
	 * Devuelve la llave publica en la posicion requerida
	 * 
	 * @param posicion la posicion del elemento
	 * @return la llave publica
	 */
	public PublicKey getKey(int posicion) {
		return usersKeys.get(posicion);
	}
	
	/**
	 * Agrega una llave publica a la lista
	 * 
	 * @param key la lista a añadir.
	 */
	public void addKey(PublicKey key) {
		usersKeys.add(key);
	}
	
	/**
	 * Desconecta a los usuarios cuando abandonan la sala
	 * 
	 * @param llaves lista con las llaves a borrar
	 * @param usuarios lista con los usuarios a borrar
	 */
	public void desconectar(ArrayList<PublicKey> llaves, ArrayList<ObjectOutputStream> usuarios) {
		this.usersKeys.removeAll(llaves);
		this.usuarios.removeAll(usuarios);
	}
	
	/**
	 * Desconecta al usuario cuando abandona la sala
	 * 
	 * @param llave la llave a borrar
	 * @param usuario el usuario a borrar
	 */
	public void desconectar(PublicKey llave, ObjectOutputStream usuario) {
		this.usersKeys.remove(llave);
		this.usuarios.remove(usuario);
	}
}