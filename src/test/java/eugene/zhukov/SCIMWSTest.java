package eugene.zhukov;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SCIMWSTest {
//	private static final String URL_PATTERN_LOCAL = "http://ee.dy.fi/scim/Users";
	private static final String URL_PATTERN_LOCAL = "http://localhost:8080/Users";
	
	public static void main(String[] args) throws MalformedURLException, IOException {
		create();
		retrieve();
		update();
	}

	private static void create() throws MalformedURLException, IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(URL_PATTERN_LOCAL).openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/xml");
		connection.setRequestProperty("Accept", "application/xml");
		connection
				.setRequestProperty(
						"Authorization",
						"Bearer GHOTqBHdpZklMYki7t1KqlXpPW7pJLs7AkpJYdEaB7wmXBb0nte65hNVnqvOiUT89pOYnrxmWqsjLEZP1yQfVHz2K/mcKiZd5XndIFoh2YIx+szMgCxCZdbd681d426adJhkVrKP1VA1xsYoIF5TN3ny/JNFqhExiEjcDtPB7cg=");

		long nanoTime = System.nanoTime();
		makeCall(connection, "<User xmlns=\"urn:scim:schemas:core:1.0\" xmlns:enterprise=\"urn:scim:schemas:extension:enterprise:1.0\">" +
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

	private static void retrieve() throws UnsupportedEncodingException, IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(URL_PATTERN_LOCAL + "/5c3f2127-d826-4843-9153-6258c3f35555").openConnection();
		connection.setDoOutput(true);
//		connection.setRequestProperty("Content-Type", "application/xml");
		connection.setRequestProperty("Accept", "application/xml");
		connection.setRequestProperty("X-HTTP-Method-Override", "GET");
		connection
				.setRequestProperty(
						"Authorization",
						"Bearer fSOsufDQ+oV6cgF2nSFGFLOo6k403+nacG3NPNHWxAp8JD/fe0bR/KX3mUy5PE+qR8bCkWkp68uzoFGMhmNGIlm9czJZmmSekiUaqfnea5dJT/ShgdyTBleh/ALCs72/mmu7tHEsGdcGLM1pMBxxeoYoGTQnBYWcV/K7istXHEg=");
		makeCall(connection, "");
	}

	private static void update() throws MalformedURLException, IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(URL_PATTERN_LOCAL + "/5c3f2127-d826-4843-9153-6258c3f35555").openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/xml");
		connection.setRequestProperty("Accept", "application/xml");
		connection.setRequestProperty("X-HTTP-Method-Override", "PUT");
		connection
				.setRequestProperty(
						"Authorization",
						"Bearer GHOTqBHdpZklMYki7t1KqlXpPW7pJLs7AkpJYdEaB7wmXBb0nte65hNVnqvOiUT89pOYnrxmWqsjLEZP1yQfVHz2K/mcKiZd5XndIFoh2YIx+szMgCxCZdbd681d426adJhkVrKP1VA1xsYoIF5TN3ny/JNFqhExiEjcDtPB7cg=");

		long nanoTime = System.nanoTime();
		makeCall(connection, "<User xmlns=\"urn:scim:schemas:core:1.0\" xmlns:enterprise=\"urn:scim:schemas:extension:enterprise:1.0\">" +
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
