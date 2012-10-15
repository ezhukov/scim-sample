package eugene.zhukov;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import eugene.zhukov.util.S;
import eugene.zhukov.util.TestRSA;

public class SCIMWSTest {
	private static final String URL_PATTERN_LOCAL = "https://ee.dy.fi/scim/v1/";
//	private static final String URL_PATTERN_LOCAL = "https://localhost:8181/scim/v1/";

//	static {
//		System.getProperties().put("javax.net.ssl.keyStore", "/home/eugene/Downloads/cert/eugene.jks");
//		System.getProperties().put("javax.net.ssl.keyStorePassword", "changeit");
//		
//		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
//			    new javax.net.ssl.HostnameVerifier(){
//
//			        public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
//			        	return hostname.equals("localhost");
//			        }
//			    });
//	}

	public static void main(String[] args) throws Exception {
		create("application/json");
		retrieve();
		update();
		changePasswd();
		delete();
		serviceProviderConfig();
	}

	private static String create(String type) throws Exception {
		HttpsURLConnection connection = (HttpsURLConnection) new URL(URL_PATTERN_LOCAL + "Users").openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/xml");
		connection.setRequestProperty("Accept", type);

		S token = new S();
	    token.setTimestamp(System.currentTimeMillis());
		String encrypted = TestRSA.encrypt(token);
		connection
				.setRequestProperty(
						"Authorization",
						"Bearer " + encrypted);

		long nanoTime = System.nanoTime();
		String response = makeCall(connection, "<User xmlns=\"urn:scim:schemas:core:1.0\" " +
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
		return response;
	}

	private static void retrieve() throws Exception {
		String id = extractValue(create("application/xml"), "id");
		HttpsURLConnection connection = (HttpsURLConnection) new URL(URL_PATTERN_LOCAL
				+ "Users/" + id).openConnection();
		connection.setDoOutput(true);
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
		String id = extractValue(create("application/xml"), "id");
		HttpsURLConnection connection = (HttpsURLConnection) new URL(URL_PATTERN_LOCAL
				+ "Users/" + id).openConnection();
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

	private static void changePasswd() throws Exception {
		String id = extractValue(create("application/xml"), "id");
		HttpsURLConnection connection = (HttpsURLConnection) new URL(URL_PATTERN_LOCAL
				+ "Users/" + id + "/password").openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/xml");
		connection.setRequestProperty("Accept", "application/xml");
		connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");

		S token = new S();
	    token.setTimestamp(System.currentTimeMillis());
	    token.setPassword("foobar");
		String encrypted = TestRSA.encrypt(token);
		connection
				.setRequestProperty(
						"Authorization",
						"Bearer " + encrypted);

		makeCall(connection, "<User xmlns=\"urn:scim:schemas:core:1.0\" " +
				"xmlns:enterprise=\"urn:scim:schemas:extension:enterprise:1.0\">" +
				"<password>foobar2</password>" +
				"</User>");
	}

	private static void delete() throws Exception {
		String id = extractValue(create("application/xml"), "id");
		HttpsURLConnection connection = (HttpsURLConnection) new URL(URL_PATTERN_LOCAL
				+ "Users/" + id).openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("X-HTTP-Method-Override", "DELETE");
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

	private static void serviceProviderConfig() throws MalformedURLException, IOException {
		HttpsURLConnection connection = (HttpsURLConnection) new URL(
				URL_PATTERN_LOCAL + "ServiceProviderConfigs").openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("X-HTTP-Method-Override", "GET");
		makeCall(connection, "");
	}

	private static String makeCall(HttpsURLConnection connection, String input) throws UnsupportedEncodingException, IOException {
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
		StringBuilder response = new StringBuilder();
		for (String s = in.readLine(); s != null; s = in.readLine()) {
			response.append(s);
			System.out.println(s);
		}
		System.out.println("----------------Response code: " + connection.getResponseCode() + "----------------");
		
		out.close();
		in.close();
		return response.toString();
	}

	private static String extractValue(String row, String attr) {
		
		if (row.indexOf(attr) < 0) {
			return null;
		}

		int start = row.indexOf(attr) + attr.length() + 1;
		int end = row.indexOf("</" + attr);

		if (end < 0) {
			return null;
		}
		return row.substring(start, end);
	}
}
