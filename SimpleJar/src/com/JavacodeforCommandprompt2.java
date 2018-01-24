package com;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Random;

// create new alias
public  class JavacodeforCommandprompt2 {
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

	public static void main(String[] args) throws Exception {
	/*	Properties prop = new Properties();
		InputStream input = null;
		input = new FileInputStream("config.properties");
		prop.load(input);
		String keyFile1 = prop.getProperty("keyFile1");
		File file  = new File(keyFile1);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line =  reader.readLine();
		String testFilePath = prop.getProperty("testFilePath");
		File testFile = new File(testFilePath);
	    BufferedWriter writer = new BufferedWriter(new FileWriter(testFile));
	    writer.write(line);
	    System.out.println("String writing to file completed");
	    reader.close();
	    writer.close();
		*/
		
		

		Runtime rt = Runtime.getRuntime();
		JavacodeforCommandprompt2 jc = new JavacodeforCommandprompt2();
		printOutput errorReported, outputMessage;
		
		/*PasswordManagement pm = new PasswordManagement();
		String keystorePassword = pm.encrypt("hiii", "");
		System.out.println("Password for jceks is:"+keystorePassword);*/
		
		String keystorePassword = Password.pass();
//		System.out.println("Password For jceks file: "+keystorePassword);
		String key = "";
		// Random id generation
		Random rand = new Random();
		int r = rand.nextInt(1000000) + 1;
		key = Integer.toString(r);
//		System.out.println("Alias id to store string:" + key);
		
		
		String path = JavacodeforCommandprompt2.path();

		try {
			// jceks generation command
			Process proc = rt.exec("keytool -genseckey -alias " + key + " -keypass " + key
					+ " -keyalg aes -keysize 256 -keystore " +path+" -storetype jceks -storepass "
					+ keystorePassword);
			errorReported = jc.getStreamWrapper(proc.getErrorStream(), "ERROR");
			outputMessage = jc.getStreamWrapper(proc.getInputStream(), "OUTPUT");
			errorReported.start();
			outputMessage.start();
			System.out.println("Keystore created successfully");
		} catch (IOException e) {
			e.printStackTrace();
		}

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