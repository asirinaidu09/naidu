package com;

import static com.KeyUtil.loadKeyStoreFromFile;

import java.io.BufferedReader;
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

public class JCEKSStringStore {

	public printOutput getStreamWrapper(InputStream is, String type) {
		return new printOutput(is, type);
	}
	public static String path() throws IOException {
		Properties prop = new Properties();
		InputStream input = null;
		input = new FileInputStream("config.properties");
		prop.load(input);
		String path = prop.getProperty("keyStorePath");
		return  path;
	}
	
	private static void writePasswordToKeyStore(String pathToKeyStore, String keyStorePassword, String passwordPassword,
			String alias, String password) throws Exception {

		try {
			KeyStore keyStore = loadKeyStoreFromFile(pathToKeyStore, keyStorePassword);
			KeyStore.PasswordProtection keyStorePP = new KeyStore.PasswordProtection(passwordPassword.toCharArray());
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
			SecretKey generatedSecret = factory.generateSecret(new PBEKeySpec(password.toCharArray(), "randomSaltValue".getBytes(), 19));
			keyStore.setEntry(alias, new KeyStore.SecretKeyEntry(generatedSecret), keyStorePP);

			FileOutputStream outputStream = new FileOutputStream(new File(pathToKeyStore));
			keyStore.store(outputStream, keyStorePassword.toCharArray());
			//System.out.println("String added to Keystore");
		} catch (Exception e) {
			
		}
	}

	public static void main(String[] args) throws Exception {

		Runtime rt = Runtime.getRuntime();
		JCEKSStringStore jc = new JCEKSStringStore();
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
		String path = prop.getProperty("keyStorePath");

		try {
			// jceks generation command
			Process proc = rt.exec("keytool -genseckey -alias " + key + " -keypass " + key + " -keyalg aes -keysize 256 -keystore " + path +" -storetype jceks -storepass "+ keystorePassword);
			//Process proc = rt.exec("keytool -genkey \"CN=Raghav Padakanti, OU=OU, O=PB, L=Hyd, ST=AP, C=IN -alias " + key + " -keyalg RSA -keysize 2048 -keystore " + path +" -storetype pkcs12 -storepass "+ keystorePassword);
			//keytool -genkey -dname "CN=Raghav Padakanti, OU=OU, O=PB, L=Hyd, ST=AP, C=IN" -alias raghav2 -keyalg RSA -keysize 2048 -keystore F:\masterkey.jks -storetype pkcs12 -storepass 123456
			errorReported = jc.getStreamWrapper(proc.getErrorStream(), "ERROR");
			outputMessage = jc.getStreamWrapper(proc.getInputStream(), "OUTPUT");
			errorReported.start();
			outputMessage.start();
		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("Keystore was tampered or password was incorrect");
		}
		
		//Console console = System.console() ;
        //TODO: check why this is not taking hardcoded String
		//String password= new String(console.readPassword("Enter String: "));
		//TODO: put one dummy string instead of taking from cmd line
		/*System.out.println("Enter any random String:");
		Scanner sc = new Scanner(System.in);
		*/String password ="intialKeyToKeyStore";
		
		
		// Call write jar to store string
		writePasswordToKeyStore(pathToKeyStore, keystorePassword, key, key, password);
		//sc.close();
		System.out.println("KeyStore created");

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
