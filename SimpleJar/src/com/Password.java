package com;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Scanner;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;



public class Password {
	
	
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

		String key1 = getKeyFromFile(filePath);

		String key2 = "82227dce-5e75-4960-8da0-3bf3391e2112";

		String key3 = getMacAddress();

		String passphrase = key1.concat(key2).concat(key3);
		return passphrase;
	}
	
	public static String getMacAddress() throws UnknownHostException, SocketException {

		/*InetAddress ip = InetAddress.getLocalHost();

		NetworkInterface net = NetworkInterface.getByInetAddress(ip);

		byte[] mac = net.getHardwareAddress();

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
		}

		String macAddress = sb.toString();
		return macAddress;*/
		try {
			String OSName = System.getProperty("os.name");
			if (OSName.contains("Windows")) {
				return (getMAC4Windows());
			} else {
				String mac = getMAC4Linux("eth0");
				if (mac == null) {
					mac = getMAC4Linux("eth1");
					if (mac == null) {
						mac = getMAC4Linux("eth2");
						if (mac == null) {
							mac = getMAC4Linux("VMware Virtual Ethernet Adapter for VMnet8");
							if (mac == null) {
								mac = getMAC4Linux("usb0");
								if (mac == null) {
									mac = getMAC4Linux("ens37");
								}
							}
						}
					}
				}
				return mac;
			}
		} catch (Exception E) {
//			System.err.println("System Mac Exp : " + E.getMessage());
			return null;
		} 
	
	}
	
	private static String getMAC4Windows() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			NetworkInterface network = NetworkInterface.getByInetAddress(addr);

			byte[] mac = network.getHardwareAddress();

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}

			return sb.toString();
		} catch (Exception E) {
//			System.err.println("System Windows MAC Exp : " + E.getMessage());
			return null;
		}
	}
	
	private static String getMAC4Linux(String name) {
		try {
			NetworkInterface network = NetworkInterface.getByName(name);
			byte[] mac = network.getHardwareAddress();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}
			return (sb.toString());
		} catch (Exception E) {
//			System.err.println("System Linux MAC Exp : " + E.getMessage());
			return null;
		}
	}
	
	public static String getKeyFromFile(String filePath) {
		File file = new File(filePath);
//		System.out.println(file.exists());
		if(file.exists()==false)
		{
		try {
			if(file.createNewFile()){
//			    System.out.println(filePath+" File Created");
			    String str = "key=";
			    BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
			    writer.write(str);
			    UUID randomNumber = UUID.randomUUID();
			    writer.write(String.valueOf(randomNumber));
			    writer.close();
			
			}
//			else 
//				System.out.println("File "+filePath+" already exists");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		}
		
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
		input = new FileInputStream("config.properties");
		prop.load(input);
//		System.out.println(prop.getProperty("passphrase"));
		String passphrase = getPassPhrase(prop.getProperty("keyFile1"));
		Password p = new Password();
		String jkspass= p.encrypt(passphrase, "test");
		return jkspass;
	}
	public static void main(String[] args) throws Exception {
//		String passphrase = getPassPhrase("F:\\Work\\Imp\\UOB Keystore\\chunkfile.txt");
		Properties prop = new Properties();
		InputStream input = null;
		input = new FileInputStream("config.properties");
		prop.load(input);
		String passphrase = getPassPhrase(prop.getProperty("keyFile1"));
//		System.out.println("Combined string:	"+passphrase);

		Password p = new Password();
		String jkspass= p.encrypt(passphrase, "test");
		System.out.println("Encrypted String:	"+jkspass);
		
		String de= p.decrypt(jkspass, "test");
		System.out.println("Decryted String:	"+de);
		
	System.out.println(Password.pass());
	}

}
