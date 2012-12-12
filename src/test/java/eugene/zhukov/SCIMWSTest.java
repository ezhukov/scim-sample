package eugene.zhukov;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import eugene.zhukov.util.S;
import eugene.zhukov.util.TestRSA;

public class SCIMWSTest {
	private static final String URL_PATTERN_LOCAL = "https://ee.dy.fi/v1/";
//	private static final String URL_PATTERN_LOCAL = "http://localhost:8080/scim-1.0/v1/";

	public static void main(String[] args) throws Exception {
		create("application/json");
		retrieve();
		retrieveNotModified();
		update();
		changePasswd();
		delete();
		groups();
	}

	private static String[] create(String type) throws Exception {
		HttpURLConnection connection = (HttpURLConnection) new URL(URL_PATTERN_LOCAL + "Users").openConnection();
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
		String id = extractValue(response, "id");
		return new String[] {id, connection.getHeaderField("ETag")};
	}

	private static void retrieve() throws Exception {
		String[] created = create("application/xml");
		HttpURLConnection connection = (HttpURLConnection) new URL(URL_PATTERN_LOCAL
				+ "Users/" + created[0]).openConnection();
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

	private static void retrieveNotModified() throws Exception {
		String[] created = create("application/xml");
		HttpURLConnection connection = (HttpURLConnection) new URL(URL_PATTERN_LOCAL
				+ "Users/" + created[0]).openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Accept", "application/xml");
		connection.setRequestProperty("X-HTTP-Method-Override", "GET");
		connection.setRequestProperty("If-None-Match", created[1]);
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
		String[] created = create("application/xml");
		HttpURLConnection connection = (HttpURLConnection) new URL(URL_PATTERN_LOCAL
				+ "Users/" + created[0]).openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/xml");
		connection.setRequestProperty("Accept", "application/json");
		connection.setRequestProperty("X-HTTP-Method-Override", "PUT");
		connection.setRequestProperty("If-Match", created[1]);

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
		String[] created = create("application/xml");
		HttpURLConnection connection = (HttpURLConnection) new URL(URL_PATTERN_LOCAL
				+ "Users/" + created[0] + "/password").openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/xml");
		connection.setRequestProperty("Accept", "application/json");
		connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
		connection.setRequestProperty("If-Match", created[1]);

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
		String[] created = create("application/xml");
		HttpURLConnection connection = (HttpURLConnection) new URL(URL_PATTERN_LOCAL
				+ "Users/" + created[0]).openConnection();
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

	private static void groups() throws Exception {
		HttpURLConnection connection = (HttpURLConnection) new URL(
				URL_PATTERN_LOCAL + "Groups").openConnection();
		connection.setDoOutput(true);
		S token = new S();
	    token.setTimestamp(System.currentTimeMillis());
		String encrypted = TestRSA.encrypt(token);
		connection
				.setRequestProperty(
						"Authorization",
						"Bearer " + encrypted);
		makeCall(connection, "");
	}

	private static String makeCall(HttpURLConnection connection, String input) throws UnsupportedEncodingException, IOException {
		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
		out.write(input);
		out.flush();
		BufferedReader in = null;

		try {
			in = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));
		} catch (java.io.IOException e) {

			if (connection.getErrorStream() != null) {
				in = new BufferedReader(
						new InputStreamReader(connection.getErrorStream()));
			}
		}
		StringBuilder response = new StringBuilder();
		
		if (in != null) {
			for (String s = in.readLine(); s != null; s = in.readLine()) {
				response.append(s);
				System.out.println(s);
			}
			in.close();
		}
		out.close();
		System.out.println(connection.getHeaderFields());
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
