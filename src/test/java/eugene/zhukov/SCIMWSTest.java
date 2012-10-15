package eugene.zhukov;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import eugene.zhukov.util.S;
import eugene.zhukov.util.TestRSA;

public class SCIMWSTest {
	private static final String URL_PATTERN_LOCAL = "https://ee.dy.fi/scim/v1/";
//	private static final String URL_PATTERN_LOCAL = "http://localhost:8080/scim/v1/";
	
//	static {
//		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
//			    new javax.net.ssl.HostnameVerifier(){
//
//			        public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
//			        	return hostname.equals("ee.dy.fi");
//			        }
//			    });
//	}

	public static void main(String[] args) throws Exception {
		create();
		retrieve();
		update();
		serviceProviderConfig();
	}

	private static void create() throws Exception {
		HttpsURLConnection connection = (HttpsURLConnection) new URL(URL_PATTERN_LOCAL + "Users").openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/xml");
		connection.setRequestProperty("Accept", "application/json");

		S token = new S();
	    token.setTimestamp(System.currentTimeMillis());
		String encrypted = TestRSA.encrypt(token);
		connection
				.setRequestProperty(
						"Authorization",
						"Bearer " + encrypted);

		long nanoTime = System.nanoTime();
		makeCall(connection, "<User xmlns=\"urn:scim:schemas:core:1.0\" " +
				"xmlns:enterprise=\"urn:scim:schemas:extension:enterprise:1.0\">" +
				"<userName>" + nanoTime + "</userName>" +
				"<password>foobar</password>" +
				"<preferredLanguage>en_US</preferredLanguage>" +
				"<emails>" +
					"<email>" +
						"<value>a" + nanoTime + "@test.com</value>" +
						"<primary>true</primary>" +
					"</email>" +
				"</emails>" +
				"<addresses><address><country>FI</country></address></addresses>" +
				"<enterprise:gender>male</enterprise:gender>" +
				"</User>");
	}

	private static void retrieve() throws Exception {
		HttpsURLConnection connection = (HttpsURLConnection) new URL(URL_PATTERN_LOCAL
				+ "Users/c0c4594a-19d1-496a-9da6-af6eac2d3286").openConnection();
		connection.setDoOutput(true);
//		connection.setRequestProperty("Content-Type", "application/xml");
		connection.setRequestProperty("Accept", "application/xml");
		connection.setRequestProperty("X-HTTP-Method-Override", "GET");
		S token = new S();
	    token.setTimestamp(System.currentTimeMillis());
	    token.setPassword("foobar");
		String encrypted = TestRSA.encrypt(token);
		connection
				.setRequestProperty(
						"Authorization",
						"Bearer " + encrypted);
		makeCall(connection, "");
	}

	private static void update() throws Exception {
		HttpsURLConnection connection = (HttpsURLConnection) new URL(URL_PATTERN_LOCAL
				+ "Users/5c3f2127-d826-4843-9153-6258c3f35555").openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/xml");
		connection.setRequestProperty("Accept", "application/xml");
		connection.setRequestProperty("X-HTTP-Method-Override", "PUT");

		S token = new S();
	    token.setTimestamp(System.currentTimeMillis());
	    token.setPassword("foobar");
		String encrypted = TestRSA.encrypt(token);
		connection
				.setRequestProperty(
						"Authorization",
						"Bearer " + encrypted);

		long nanoTime = System.nanoTime();
		makeCall(connection, "<User xmlns=\"urn:scim:schemas:core:1.0\" " +
				"xmlns:enterprise=\"urn:scim:schemas:extension:enterprise:1.0\">" +
				"<userName>" + nanoTime + "</userName>" +
				"<id>bb1d0a94-445a-468f-ad27-5d98b7be890c</id>" +
				"<password>foobar</password>" +
				"<preferredLanguage>en_US</preferredLanguage>" +
				"<emails>" +
					"<email>" +
						"<value>a" + nanoTime + "u@test.com</value>" +
						"<primary>true</primary>" +
					"</email>" +
				"</emails>" +
				"<addresses><address><country>FI</country></address></addresses>" +
				"<enterprise:gender>male</enterprise:gender>" +
				"</User>");
	}

	private static void serviceProviderConfig() throws MalformedURLException, IOException {
		HttpsURLConnection connection = (HttpsURLConnection) new URL(
				URL_PATTERN_LOCAL + "ServiceProviderConfigs").openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("X-HTTP-Method-Override", "GET");
		makeCall(connection, "");
	}

	private static void makeCall(HttpURLConnection connection, String input) throws UnsupportedEncodingException, IOException {
		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
		out.write(input);
		out.flush();
		BufferedReader in;
		try {
			in = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));
		} catch (java.io.IOException e) {
			in = new BufferedReader(
					new InputStreamReader(connection.getErrorStream()));
		}
		for (String s = in.readLine(); s != null; s = in.readLine()) {
			System.out.println(s);
		}
		System.out.println("----------------Response code: " + connection.getResponseCode() + "----------------");
		
		out.close();
		in.close();
	}
}
