package practica6ChatDistribuido;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HexFormat;
import javax.crypto.Cipher;

/**
 * Clase con metodos estaticos para cifrar, descifrar y hashear textos.
 * 
 * @author Jorge Manzano Anchelergues y Jaime Usero Aranda
 */

public class Utilities {
	
	/**
	 * Metodo que genera un par de claves asimetricas
	 * 
	 * @return el par de claves
	 * @throws Exception
	 */
	public static KeyPair generarClaves() throws Exception {        
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();        
	}
	
	/**
	 * Metodo que encripta con la llave publica
	 * 
	 * @param publicKey la llave para encriptar
	 * @param text el contenido a encriptar
	 * @return el contenido encriptado
	 * @throws Exception
	 */
	public static byte[] encryptPublic(PublicKey publicKey, String text) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
    }
	
	/**
	 * Metodo que encripta con la llave publica
	 * 
	 * @param publicKey la llave para encriptar
	 * @param text el contenido a encriptar
	 * @return el contenido encriptado
	 * @throws Exception
	 */
	public static byte[] encryptPublic(PublicKey publicKey, byte[] text) throws Exception {
		return encryptPublic(publicKey, new String(text, StandardCharsets.UTF_8));		
    }
	
	/**
	 * Metodo que encripta con la llave privada
	 * 
	 * @param publicKey la llave para encriptar
	 * @param text el contenido a encriptar
	 * @return el contenido encriptado
	 * @throws Exception
	 */
	public static byte[] encryptPrivate(PrivateKey privateKey, String text) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
    }
	
	/**
	 * Metodo que encripta con la llave privada
	 * 
	 * @param publicKey la llave para encriptar
	 * @param text el contenido a encriptar
	 * @return el contenido encriptado
	 * @throws Exception
	 */
	public static byte[] encryptPrivate(PrivateKey privateKey, byte[] text) throws Exception {
		return encryptPrivate(privateKey, new String(text, StandardCharsets.UTF_8));		
    }
	
	/**
	 * Metodo que desencripta con la llave publica
	 * 
	 * @param key la llave publica
	 * @param encrypted el contenido encriptado
	 * @return el contenido desencriptado
	 * @throws Exception
	 */
	public static String decryptPublic(PublicKey key, byte[] encrypted) throws Exception {        		
		Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
    }
	
	/**
	 * Metodo que desencripta con la llave privada
	 * 
	 * @param key la llave privada
	 * @param encrypted el contenido encriptado
	 * @return el contenido desencriptado
	 * @throws Exception
	 */
	public static String decryptPrivate(PrivateKey key, byte[] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
    }
	
	/**
	 * Metodo que hashea un contenido
	 * 
	 * @param algoritmo el algoritmo a usar
	 * @param contenido el contenido a hashear
	 * @return el contenido hasheado
	 * @throws Exception
	 */
	public static String hashear(String contenido) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        byte[] salt = "1234".getBytes();
        messageDigest.update(salt);        
        byte[] hash = messageDigest.digest(contenido.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);                                        
    }
}