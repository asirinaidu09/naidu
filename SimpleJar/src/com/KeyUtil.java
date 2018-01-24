package com;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;

public class KeyUtil {
	public static FileInputStream getFileInputStreamFromArg(String filePath) throws FileNotFoundException {
		File file = new File(filePath);
		return new FileInputStream(file);
	}

	// Load Keystore form path with path and password
	public static KeyStore loadKeyStoreFromFile(String pathToFile, String keystorePassword) {
		try {
			KeyStore keyStore = KeyStore.getInstance("JCEKS");
			keyStore.load(getFileInputStreamFromArg(pathToFile), keystorePassword.toCharArray());
			return keyStore;
		} catch (Exception e) {
			System.out.println("Keystore was tampered or password was incorrect");
		}
		return null;
	}
}