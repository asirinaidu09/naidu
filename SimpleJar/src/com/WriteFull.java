package com;

import static com.KeyUtil.loadKeyStoreFromFile;

import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class WriteFull {

	/*
	 * private static void checkArgs(String[] args) { if(args.length != 5) { throw
	 * new
	 * IllegalArgumentException("Usage: WritePasswordToKeyStore <full path to keystore> <keystore password> <password password> <key alias> <password to store>"
	 * ); } }
	 */

	private static void writePasswordToKeyStore(String pathToKeyStore, String keyStorePassword, String passwordPassword,
			String alias, String password) throws Exception {
		
		
		/*Console console = System.console() ;

		password= new String(console.readPassword("Enter String: "));*/
		
		Console console = System.console();
		if (console == null) {
			System.out.println("Couldn't get Console instance");
			System.exit(0);
		}

		password = new String(console.readPassword("Enter keyValue to be inserted in the keyStore:"));
		
		//System.out.println("Enter the String to be inserted in the keyStore:");
		/*Scanner sc = new Scanner(System.in);
		password = sc.next();
		sc.close();*/

		KeyStore keyStore = loadKeyStoreFromFile(pathToKeyStore, keyStorePassword);

		KeyStore.PasswordProtection keyStorePP = new KeyStore.PasswordProtection(passwordPassword.toCharArray());
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
		SecretKey generatedSecret = factory.generateSecret(new PBEKeySpec(password.toCharArray(), "randomSaltValue".getBytes(), 19));
		keyStore.setEntry(alias, new KeyStore.SecretKeyEntry(generatedSecret), keyStorePP);

		FileOutputStream outputStream = new FileOutputStream(new File(pathToKeyStore));
		keyStore.store(outputStream, keyStorePassword.toCharArray());
		System.out.println("Key added to Keystore with Id: "+alias);
	}

	public static void main(String[] args) {
		
		try {
		/*
		 * checkArgs(args);
		 * 
		 * String pathToKeyStore = args[0]; String keystorePassword = args[1]; String
		 * passwordPassword = args[2]; String passwordAlias = args[3]; String
		 * passwordToStore = args[4];
		 */

		String path= JavacodeforCommandprompt2.path();
		String pathToKeyStore = path;
		//System.out.println(pathToKeyStore);
		String keystorePassword = Password.pass();
		
		//TODO:Generate random ids instead of asking from command line
		// Read Alias id
		//System.out.println("Enter alias id:");
		//Scanner sc = new Scanner(System.in);
		UUID randomNumber = UUID.randomUUID();
		//System.out.println(randomNumber);
		String passwordAlias = String.valueOf(randomNumber);
		String passwordToStore = null;
		
		// Call write jar to store string
		writePasswordToKeyStore(pathToKeyStore, keystorePassword, passwordAlias, passwordAlias, passwordToStore);
		//sc.close();
		
		} catch (Exception ex) {
			System.out.println("Keystore was tampered or password was incorrect");
		}
	}
}