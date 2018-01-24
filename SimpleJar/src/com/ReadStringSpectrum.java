package com;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.Properties;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class ReadStringSpectrum {
	
	public static FileInputStream getFileInputStreamFromArg(String filePath) throws FileNotFoundException {
		File file = new File(filePath);
		return new FileInputStream(file);
	}
	
	public static KeyStore loadKeyStoreFromFile(String pathToFile, String keystorePassword) throws Exception {
		KeyStore keyStore = KeyStore.getInstance("JCEKS");
		keyStore.load(getFileInputStreamFromArg(pathToFile), keystorePassword.toCharArray());
		return keyStore;
	}
	
	public static String path() throws IOException {
		Properties prop = new Properties();
		InputStream input = null;
		File resourceFile = new File("config.properties");
		String path2=resourceFile.getAbsolutePath();
		
		input = new FileInputStream(path2);
		prop.load(input);
//		System.out.println(prop.getProperty("path"));
		String path = prop.getProperty("path");
		return  path;
	}
	
	public String encrypt(String plainString, String passphrase) throws Exception {

		// salt is an intermediate key to generate secret key
		String salt = "82227dce-5e75-4960-8da0-3bf3391e2112";
		byte[] saltBytes = salt.getBytes("UTF-8");

		// number of iterations
		int pswdIterations = 10000;

		// key size after iterations
		int keySize = 256;

		// generating secret key by using PBKDF2-HMAC algorithm
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		PBEKeySpec spec = new PBEKeySpec(passphrase.toCharArray(), saltBytes, pswdIterations, keySize);

		SecretKey secretKey = factory.generateSecret(spec);
		SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
	
		// encrypting the given string
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, secret);
		byte[] encryptedTextBytes = cipher.doFinal(plainString.getBytes("UTF-8"));
		String encodedString = java.util.Base64.getEncoder().encodeToString(encryptedTextBytes);
		return encodedString;
	}
	
	public String decrypt(String encryptedString, String passphrase) throws Exception {

		// salt is an intermediate key to generate secret key
		String salt = "82227dce-5e75-4960-8da0-3bf3391e2112";
		byte[] saltBytes = salt.getBytes("UTF-8");

		// number of iterations
		int pswdIterations = 10000;

		// key size after iterations
		int keySize = 256;

		byte[] encryptedStringBytes = java.util.Base64.getDecoder().decode(encryptedString);

		// generating secret key by using PBKDF2-HMAC algorithm
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		PBEKeySpec spec = new PBEKeySpec(passphrase.toCharArray(), saltBytes, pswdIterations, keySize);

		SecretKey secretKey = factory.generateSecret(spec);
		SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

		// decrypting the given string
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, secret);

		byte[] decryptedString = null;
		try {
			decryptedString = cipher.doFinal(encryptedStringBytes);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}

		return new String(decryptedString);
	}
	
	public static String getPassPhrase(String filePath)
			throws FileNotFoundException, UnknownHostException, SocketException {

		String chunk1 = getKeyFromFile(filePath);

		String chunk2 = "82227dce-5e75-4960-8da0-3bf3391e2112";

		String chunk3 = getMacAddress();

		String passphrase = chunk1.concat(chunk2).concat(chunk3);
//		System.out.println("Concat String: "+passphrase);
		return passphrase;
	}
	
	public static String getMacAddress() throws UnknownHostException, SocketException {

		InetAddress ip = InetAddress.getLocalHost();

		NetworkInterface net = NetworkInterface.getByInetAddress(ip);

		byte[] mac = net.getHardwareAddress();

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
		}

		String macAddress = sb.toString();
		return macAddress;
	}
	
	public static String getKeyFromFile(String filePath) {
		File file = new File(filePath);
		String key = null;
		try (Scanner scanner = new Scanner(file);) {

			String line = scanner.nextLine();

			if (line.contains("key")) {
				key = line.substring(line.lastIndexOf("=") + 1, line.length());

			} else {
				throw new Exception("Unable to read key string from file.");
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return key;
	}
	
	public static String pass() throws Exception {
		Properties prop = new Properties();
		InputStream input = null;
		File resourceFile = new File("config.properties");
		String path2=resourceFile.getAbsolutePath();
		
		input = new FileInputStream(path2);
		prop.load(input);
//		System.out.println(prop.getProperty("passphrase"));
		String passphrase = getPassPhrase(prop.getProperty("passphrase"));
		ReadStringSpectrum p = new ReadStringSpectrum();
//		System.out.println("In pass"+passphrase);
		String jkspass= p.encrypt(passphrase, "test");
//		System.out.println(jkspass);
		return jkspass;
	}
	
	public static KeyStore Keystore() throws Exception {
		
		String pathToKeyStore=ReadStringSpectrum.path();
		String keystorePassword= ReadStringSpectrum.pass();
		KeyStore keyStore = loadKeyStoreFromFile(pathToKeyStore, keystorePassword);
		return keyStore;
		
	}
	
	
	public static String readPasswordFromKeyStore(KeyStore keyStore, String passwordPassword, String passwordAlias) throws Exception {
		
//		KeyStore keyStore = loadKeyStoreFromFile("F:\\Work\\Imp\\UOB Keystore\\uob1.jceks", "vId5hLgB02MX3IBMEgiwainCfQXBNiivcLhw/vW83z2qDcGms4tQPr0memmHXsUDVwstQ96ELqW7NF+xJ3VnuWR810wjKvumlRRL8Aj8+sw=");
	
		KeyStore.PasswordProtection keyStorePP = new KeyStore.PasswordProtection(passwordAlias.toCharArray());

		KeyStore.SecretKeyEntry ske = (KeyStore.SecretKeyEntry) keyStore.getEntry(passwordAlias, keyStorePP);

		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
		PBEKeySpec keySpec = (PBEKeySpec) factory.getKeySpec(ske.getSecretKey(), PBEKeySpec.class);
		return new String(keySpec.getPassword());
	
	}

	
	
/*	public static void main(String[] args) throws Exception {
		String path= ReadStringSpectrum.path();
//		File resourceFile = new File("myFile.txt");
//		InputStream input = new FileInputStream("C:\\Program Files\\Pitney Bowes\\Spectrum\\server\\app\\conf\\config.properties");
//		String path2=resourceFile.getAbsolutePath();
//		  System.out.println(resourceFile.getAbsolutePath());
		String jceks=path+"uob1.jceks";
		String pass = ReadStringSpectrum.pass();
		KeyStore ks= ReadStringSpectrum.loadKeyStoreFromFile(jceks, pass);
		String Alias="294533";
		String Value= ReadStringSpectrum.readPasswordFromKeyStore(ks, Alias, Alias);
		System.out.println(Value);
	}*/


}
