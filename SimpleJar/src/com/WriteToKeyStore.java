package com;

import static com.KeyUtil.loadKeyStoreFromFile;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.util.Properties;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class WriteToKeyStore {

	public printOutput getStreamWrapper(InputStream is, String type) {
		return new printOutput(is, type);
	}
	public static String path() throws IOException {
		Properties prop = new Properties();
		InputStream input = null;
		input = new FileInputStream("config.properties");
		prop.load(input);
		String path = prop.getProperty("path");
		return  path;
	}
	
	private static void writePasswordToKeyStore(String pathToKeyStore, String keyStorePassword, String passwordPassword,
			String alias, String password) throws Exception {

		KeyStore keyStore = loadKeyStoreFromFile(pathToKeyStore, keyStorePassword);

		KeyStore.PasswordProtection keyStorePP = new KeyStore.PasswordProtection(passwordPassword.toCharArray());
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
		SecretKey generatedSecret = factory
				.generateSecret(new PBEKeySpec(password.toCharArray(), "oh Naidu salty".getBytes(), 19));
		keyStore.setEntry(alias, new KeyStore.SecretKeyEntry(generatedSecret), keyStorePP);

		FileOutputStream outputStream = new FileOutputStream(new File(pathToKeyStore));
		keyStore.store(outputStream, keyStorePassword.toCharArray());
		System.out.println("String added to Keystore");
		
	}

	public static void main(String[] args) throws Exception {

		Runtime rt = Runtime.getRuntime();
		WriteToKeyStore jc = new WriteToKeyStore();
		printOutput errorReported, outputMessage;
		
		/*PasswordManagement pm = new PasswordManagement();
		String keystorePassword = pm.encrypt("hiii", "");
		System.out.println("Password for jceks is:"+keystorePassword);*/
		
		String keystorePassword = Password.pass();
//		System.out.println("Password For jceks file: "+keystorePassword);
		String key = "";
		String pathToKeyStore = JCEKSStringStore.path();
		// Random id generation
		Random rand = new Random();
		int r = rand.nextInt(1000000) + 1;
		key = Integer.toString(r);
		Properties prop = new Properties();
		InputStream input = null;
		input = new FileInputStream("config.properties");
		prop.load(input);
		String path = prop.getProperty("path");

		try {
			// jceks generation command
			Process proc = rt.exec("keytool -genseckey -alias " + key + " -keypass " + key
					+ " -keyalg aes -keysize 256 -keystore " +path+" -storetype jceks -storepass "
					+ keystorePassword);
			errorReported = jc.getStreamWrapper(proc.getErrorStream(), "ERROR");
			outputMessage = jc.getStreamWrapper(proc.getInputStream(), "OUTPUT");
			errorReported.start();
			outputMessage.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Console console = System.console() ;

		String password= new String(console.readPassword("Enter String: "));
		/*System.out.println("Enter String:");
		Scanner sc = new Scanner(System.in);
		String password = sc.next();
		*/
		
		// Call write jar to store string
		writePasswordToKeyStore(pathToKeyStore, keystorePassword, key, key, password);
//		sc.close();
		System.out.println("Alias id to store string:" + key);

	}

	private class printOutput extends Thread {
		InputStream is = null;

		printOutput(InputStream is, String type) {
			this.is = is;
		}

		public void run() {
			String s = null;
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				while ((s = br.readLine()) != null) {
					System.out.println(s);
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

}
