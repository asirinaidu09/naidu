package com;

import static com.KeyUtil.loadKeyStoreFromFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Properties;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class ReadFull {

	public static void main(String[] args) throws Exception {
		// current directory path 
		Properties prop = new Properties();
		InputStream input = null;
		input = new FileInputStream("config.properties");
		prop.load(input);
		String path = prop.getProperty("keyStorePath");
		String pathToKeyStore = path;
		// Read alias id
		String id=args[0];
		//String id=prop.getProperty("id");
		String passwordAlias=id;
//		System.out.println(pathToKeyStore);
		Password ps = new Password();
		String keystorePassword = ps.pass();
		KeyStore keyStore = null;
		// Read string from jceks
		keyStore = loadKeyStoreFromFile(pathToKeyStore, keystorePassword);
		String output = readPasswordFromKeyStore(keyStore, passwordAlias, passwordAlias);
		/*if(!output.isEmpty()) {
			System.out.println(output);
		}*/
	}
	// private static void checkArgs(String[] args) {
	// if(args.length != 4) {
	// throw new IllegalArgumentException("Usage: ReadPasswordFromKeyStore <full
	// path to keystore> <keystore password> <password password> <key alias>");
	// }
	// }

	private static String readPasswordFromKeyStore(KeyStore keyStore, String passwordPassword, String passwordAlias) {
		
		try {
			KeyStore.PasswordProtection keyStorePP = new KeyStore.PasswordProtection(passwordPassword.toCharArray());

			KeyStore.SecretKeyEntry ske = (KeyStore.SecretKeyEntry) keyStore.getEntry(passwordAlias, keyStorePP);

			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
			PBEKeySpec keySpec = (PBEKeySpec) factory.getKeySpec(ske.getSecretKey(), PBEKeySpec.class);
			return new String(keySpec.getPassword());
		} catch (Exception e) {
			System.out.println("Keystore was tampered or password was incorrect");
		}
		return null;
	}

}

