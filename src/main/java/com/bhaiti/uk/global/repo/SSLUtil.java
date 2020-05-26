package com.bhaiti.uk.global.repo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;

public class SSLUtil {

	private static TrustManager[] trustAllCerts = null;
	private static String keymanageralgorithm = null;
	private static SSLContext sslContext = null;
	/*
	static {
	    //for localhost testing only
	    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
	    new javax.net.ssl.HostnameVerifier(){

	        public boolean verify(String hostname,
	                javax.net.ssl.SSLSession sslSession) {
	            if (hostname.equals("localhost")) {
	                return true;
	            }
	            return false;
	        }
	    });
	}*/

	public static void setupSslContext() {

		boolean trustall = false;
		
		try {
			String keyStorePath = "C:\\Users\\prate\\ssl\\client\\keyStore.jks";
			String trustStorePath = "C:\\Users\\prate\\ssl\\client\\trustStore.jks";
			String keyStorePw = "deva1972";
			String trustStorePw = "deva1972";
			String keyPass = "deva1972";
			String trustAllCertificate = "False";
			String keystoreType = "JKS";
			keymanageralgorithm = "SunX509"; // For IBM it should be IbmX509
			trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}

			} };
			if (trustAllCertificate.equalsIgnoreCase("True")) {
				trustall = true;
			}
			if (keystoreType.equalsIgnoreCase("JKS"))
				sslContext = initializeSSLContext(keyStorePath, keyStorePw, trustStorePath, trustStorePw, keyPass,
						trustall);
			else
				sslContext = initializeSSLContextP12Cert(keyStorePath, keyStorePw, trustStorePath, trustStorePw,
						keyPass, trustall);

		} catch (Exception exp) {
			System.out
					.println("ConfigException exception occurred while reading the config file : " + exp.getMessage());
			exp.printStackTrace();
		}
	}
	
	public static SSLSocketFactory getClientSSLSocketFactory() {
		if(sslContext != null) {
			return sslContext.getSocketFactory();
		}
		return null;
	}

	/**
	 * 
	 * @param keyStorePath
	 * @param pwKeyStore
	 * @param trustStorePath
	 * @param pwTrustStore
	 * @param keyPass
	 * @return
	 * @throws Exception
	 */
	private static SSLContext initializeSSLContext(final String keyStorePath, final String pwKeyStore,
			final String trustStorePath, final String pwTrustStore, final String keyPass, final boolean trustall) {
		System.out.println(" In initializeSSLContext");
		char[] keyStorePw = pwKeyStore.toCharArray();
		char[] trustStorePw = pwTrustStore.toCharArray();
		char[] keyPw = keyPass.toCharArray();
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextInt();

		KeyStore ks = null;
		try {
			ks = KeyStore.getInstance("JKS");
		} catch (KeyStoreException exp) {
			System.out.println("KeyStoreException exception occurred while reading the config file : " + exp.getMessage());
		}
		FileInputStream fis = null;
		try {
			try {
				fis = new FileInputStream(keyStorePath);
			} catch (FileNotFoundException exp) {
				System.out.println("FileNotFoundException exception occurred " + exp.getMessage());
			}
			try {
				ks.load(fis, keyStorePw);
			} catch (NoSuchAlgorithmException exp) {
				System.out.println("NoSuchAlgorithmException exception occurred " + exp.getMessage());
			} catch (CertificateException exp) {
				System.out.println("CertificateException exception occurred " + exp.getMessage());
			} catch (IOException exp) {
				System.out.println("CertificateException exception occurred " + exp.getMessage());
			}
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException exp) {
					System.out.println("IOException exception occurred " + exp.getMessage());
				}
		}

		System.out.println("[initializeSSLContext] KMF keystorepw loaded.");

		KeyManagerFactory kmf = null;
		try {
			kmf = KeyManagerFactory.getInstance(keymanageralgorithm);
		} catch (NoSuchAlgorithmException exp) {
			System.out.println("IOException exception occurred " + exp.getMessage());
		}
		try {
			kmf.init(ks, keyPw);
		} catch (UnrecoverableKeyException exp) {
			System.out.println("UnrecoverableKeyException exception occurred " + exp.getMessage());
		} catch (KeyStoreException exp) {
			System.out.println("KeyStoreException exception occurred " + exp.getMessage());
		} catch (NoSuchAlgorithmException exp) {
			System.out.println("NoSuchAlgorithmException exception occurred " + exp.getMessage());
		}

		System.out.println("[initializeSSLContext] KMF init done.");

		KeyStore ts = null;
		try {
			ts = KeyStore.getInstance("JKS");
		} catch (KeyStoreException exp) {
			System.out.println("NoSuchAlgorithmException exception occurred " + exp.getMessage());
		}
		FileInputStream tfis = null;
		SSLContext sslContext = null;
		try {
			tfis = new FileInputStream(trustStorePath);
			ts.load(tfis, trustStorePw);
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(keymanageralgorithm);
			tmf.init(ts);
			System.out.println("[initializeSSLContext] Truststore initialized");
			sslContext = SSLContext.getInstance("TLS");

			if (trustall)
				sslContext.init(kmf.getKeyManagers(), trustAllCerts, secureRandom);
			else
				sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), secureRandom);

		} catch (NoSuchAlgorithmException exp) {
			System.out.println("NoSuchAlgorithmException exception occurred " + exp.getMessage());
		} catch (CertificateException exp) {
			System.out.println("NoSuchAlgorithmException exception occurred " + exp.getMessage());
		} catch (IOException exp) {
			System.out.println("NoSuchAlgorithmException exception occurred " + exp.getMessage());
		} catch (KeyStoreException exp) {
			System.out.println("NoSuchAlgorithmException exception occurred " + exp.getMessage());
		} catch (KeyManagementException exp) {
			System.out.println("NoSuchAlgorithmException exception occurred " + exp.getMessage());
		} finally {
			if (tfis != null)
				try {
					tfis.close();
				} catch (IOException exp) {
					System.out.println("NoSuchAlgorithmException exception occurred " + exp.getMessage());
				}
		}

		if ((sslContext == null)) {
			System.out.println("[initializeSSLContext] sslContext is null");
			System.exit(-1);
		}
		return sslContext;
	}

	/**
	 * 
	 * @param keyStorePath
	 * @param pwKeyStore
	 * @param trustStorePath
	 * @param pwTrustStore
	 * @param keyPass
	 * @return
	 * @throws Exception
	 */
	private static SSLContext initializeSSLContextP12Cert(final String keyStorePath, final String pwKeyStore,
			final String trustStorePath, final String pwTrustStore, final String keyPass, final boolean trustall) {
		System.out.println("In initializeSSLContextP12Cert");
		SSLContext sslContext = null;
		String keystore = keyStorePath;
		String keystorepass = pwKeyStore;
		String truststore = trustStorePath;
		String truststorepass = pwTrustStore;

		try {
			KeyStore clientStore = KeyStore.getInstance("PKCS12");
			clientStore.load(new FileInputStream(keystore), keystorepass.toCharArray());

			KeyManagerFactory kmf = KeyManagerFactory.getInstance(keymanageralgorithm);
			kmf.init(clientStore, keystorepass.toCharArray());
			KeyManager[] kms = kmf.getKeyManagers();

			KeyStore trustStore = KeyStore.getInstance("JKS");
			trustStore.load(new FileInputStream(truststore), truststorepass.toCharArray());

			TrustManagerFactory tmf = TrustManagerFactory.getInstance(keymanageralgorithm);
			tmf.init(trustStore);
			TrustManager[] tms = tmf.getTrustManagers();
			sslContext = SSLContext.getInstance("TLS");

			if (trustall)
				sslContext.init(kms, trustAllCerts, new SecureRandom());
			else
				sslContext.init(kms, tms, new SecureRandom());

		} catch (NoSuchAlgorithmException exp) {
			System.out.println("NoSuchAlgorithmException exception occurred " + exp.getMessage());
		} catch (CertificateException exp) {
			System.out.println("CertificateException exception occurred " + exp.getMessage());
		} catch (IOException exp) {
			System.out.println("IOException occurred while reading the key file  " + exp.getMessage());
		} catch (KeyStoreException exp) {
			System.out.println("KeyStoreException exception occurred " + exp.getMessage());
		} catch (KeyManagementException exp) {
			System.out.println("KeyManagementException exception occurred " + exp.getMessage());
		} catch (UnrecoverableKeyException exp) {
			System.out.println("UnrecoverableKeyException exception occurred " + exp.getMessage());
		}

		if ((sslContext == null)) {
			System.out.println("[initializeSSLContext] sslContext is null");
			System.out.println("[initializeSSLContext] verify ssl config");
			System.out.println("MyREST application exit with status code -1");
		}
		System.out.println("[initializeSSLContextP12Cert] Truststore and KeyStore initialized");
		return sslContext;
	}

}
