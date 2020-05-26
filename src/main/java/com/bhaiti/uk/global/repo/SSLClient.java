package com.bhaiti.uk.global.repo;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;


public class SSLClient {
	
	static SSLClient _sslClient = null;
	int _responseCode = -1;
	URL url_ = null;
	HttpsURLConnection connection_ = null;
	static  SSLSocketFactory  sslSocketFactory = null;
	
	private SSLClient() {		
		sslSocketFactory = SSLUtil.getClientSSLSocketFactory();
	}
	
	public static SSLClient getSSLClient() {
		
		if (_sslClient == null) {
			_sslClient = new SSLClient();
		}
		return _sslClient;
	}
		
	private boolean setSSLConnection(URL url, String method, String msgtype) {
		HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
		try {
			connection_ = (HttpsURLConnection) url.openConnection();
			connection_.setSSLSocketFactory(sslSocketFactory);
			if(method == "POST")
				connection_.setRequestMethod(method);
			connection_.setDoOutput(true );
			connection_.setRequestProperty("Content-Type", msgtype /*"text/xml" */ );
			connection_.connect();
			return true;
		} catch (Exception e) {
			System.out.println("Exception occurred while establishing connection to SSL server. Error :"
					+ e.getMessage());
			connection_.disconnect();
			e.printStackTrace();
			connection_ = null;
			return false;
		}
	}
	
	public void releaseConnection() {
		connection_.disconnect();
		connection_ = null;
	}
	
	/**
	 * 
	 * @param url
	 * @param method
	 * @param message
	 * @param msgtype json or xml
	 * @return
	 */
	
	public String sendRequest(URL url, String method, String message, String msgtype) {
		
		String response = null;
		
		if (setSSLConnection(url,method,msgtype)) {
		
			try{
				//Sending the request to Remote server
				OutputStreamWriter writer = new OutputStreamWriter(connection_.getOutputStream());
				writer.write(message);
				writer.flush();
				writer.close();			
				_responseCode = connection_.getResponseCode();			
				System.out.println("Response Code :" + _responseCode);			
				// reading the response
				InputStreamReader reader = new InputStreamReader(connection_.getInputStream());
				StringBuilder buf = new StringBuilder();
				char[] cbuf = new char[ 2048 ];
				int num;
				while ( -1 != (num = reader.read( cbuf )))
				{
					buf.append(cbuf, 0, num );
				}
			response = buf.toString();
			
		  }catch(Exception e){
			response = "<EXCEPTION>Exception occurred while sending message</EXCEPTION>";
			e.printStackTrace();
		 }

	  }
		releaseConnection();
		return response;
	}
}
