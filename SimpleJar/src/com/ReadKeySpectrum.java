package com;

import static com.KeyUtil.loadKeyStoreFromFile;

import java.security.KeyStore;
import java.util.Scanner;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class ReadKeySpectrum {

	public static void main(String[] args) throws Exception {
		JavacodeforCommandprompt2 jc = new JavacodeforCommandprompt2();
		// current directory path 
		String path= jc.path();
		String pathToKeyStore = path+"\\uob1.jceks";
//		System.out.println(pathToKeyStore);
		Password ps = new Password();
		String keystorePassword = ps.pass();
		
		// Read alias id
		System.out.println("Enter alias id:");
		Scanner sc = new Scanner(System.in);
		String passwordAlias = sc.nextLine();
		KeyStore keyStore = null;
		// Read string from jceks
		keyStore = loadKeyStoreFromFile(pathToKeyStore, keystorePassword);

		System.out.println("String stored in keystore is: "+readPasswordFromKeyStore(keyStore, passwordAlias, passwordAlias));
		sc.close();
	}
	// private static void checkArgs(String[] args) {
	// if(args.length != 4) {
	// throw new IllegalArgumentException("Usage: ReadPasswordFromKeyStore <full
	// path to keystore> <keystore password> <password password> <key alias>");
	// }
	// }

	private static String readPasswordFromKeyStore(KeyStore keyStore, String passwordPassword, String passwordAlias)
			throws Exception {
	
		KeyStore.PasswordProtection keyStorePP = new KeyStore.PasswordProtection(passwordPassword.toCharArray());

		KeyStore.SecretKeyEntry ske = (KeyStore.SecretKeyEntry) keyStore.getEntry(passwordAlias, keyStorePP);

		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
		PBEKeySpec keySpec = (PBEKeySpec) factory.getKeySpec(ske.getSecretKey(), PBEKeySpec.class);
		return new String(keySpec.getPassword());
	}

}

