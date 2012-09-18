package eugene.zhukov;

import java.text.ParseException;

import javax.ws.rs.core.MediaType;

import org.junit.Assert;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;

public class AccountCreationTest extends IntegrationJerseyTestCase {
	
	private static final String RESOURCE = "v1/Users";

	@Test
	public void testRegisterUserRawXML() {
		String username = "" + System.currentTimeMillis();
		
		String input = "<User xmlns=\"urn:scim:schemas:core:1.0\" " +
				"xmlns:enterprise=\"urn:scim:schemas:extension:enterprise:1.0\">" +
				"<userName>" + username + "</userName>" +
				"<preferredLanguage>en</preferredLanguage>" +
				"<password>123456</password>" +
				"<emails>" +
				"<email>" +
				"<value>" + username + "@test.com</value>" +
				"<primary>true</primary>" +
				"</email>" +
				"</emails>" +
				"<addresses><address><country>FI</country></address></addresses>" +
				"<enterprise:gender>female</enterprise:gender>" +
				"</User>";
		
		ClientResponse cr = resource().path(RESOURCE)
				.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML)
				.post(ClientResponse.class, input);
		
		Assert.assertEquals("Http status code 201 expected.", 201, cr.getStatus());
		String response = cr.getEntity(String.class);
		cr = resource().path(RESOURCE + "/" + extractValue(response, "id"))
				.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML)
				.get(ClientResponse.class);
		Assert.assertTrue("Expexted string from response not found.", response.indexOf("<userName>" + username + "</userName>") > 1);
		response = cr.getEntity(String.class);
		Assert.assertEquals("Http status code 200 expected.", 200, cr.getStatus());
	}
	
	@Test
	public void testRegisterUserRawJson() throws ParseException {
		long nanoTime = System.nanoTime();
		
		String input = "{ \"schemas\":[\"urn:scim:schemas:core:1.0\", \"urn:scim:schemas:extension:enterprise:1.0\"], "
				+ "\"userName\":\"S" + nanoTime + "\","
				+ "\"preferredLanguage\":\"en\","
				+ "\"password\":\"123456\","
				+ "\"name\":{"
				+ "\"familyName\":\"Jensen\","
				+ "\"givenName\":\"Barbara\" }"
				+ ","
				+ "\"emails\": ["
				+ "{"
				+ "\"value\": \"S" + nanoTime + "@example.com\","
				+ "\"primary\": true"
				+ "},"
				+ "{"
				+ "\"value\": \"C" + nanoTime + "@example.com\","
				+ "\"primary\": false"
				+ "}]"
				+ ",\"addresses\": [{\"country\": \"FI\", \"primary\": \"true\"},{\"country\": \"SE\", \"primary\": \"false\"}],"
//				+ "\"urn:scim:schemas:extension:enterprise:1.0\": {"
				+ "\"gender\":\"male\""
//				+ "}"
				+ "}";
		
		ClientResponse response = resource().path(RESOURCE)
				.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, input);
		
//		String responseContent = response.getEntity(String.class);
		Assert.assertEquals(201, response.getStatus());
//		System.out.println(responseContent);
//		Assert.assertEquals(retrievedProfile.getAccountId(), JsonPath.read(responseContent, "$.id"));
//		Assert.assertEquals("S" + nanoTime + "@example.com", JsonPath.read(responseContent, "$.emails[0].value"));
//		Assert.assertEquals("C" + nanoTime + "@example.com", JsonPath.read(responseContent, "$.emails[1].value"));
	}
	
	@Test
	public void testRegisterUserError() {
		long nanoTime = System.nanoTime();
		String input = "{ \"schemas\":[\"urn:scim:schemas:core:1.0\"], "
				+ "\"userName\":\"S" + nanoTime + "\","
				+ "\"preferredLanguage\":\"en\","
				+ "\"password\":\"123456\","
				+ "\"name\":{"
				+ "\"familyName\":\"Jensen\","
				+ "\"givenName\":\"Barbara\" }"
				+ ","
				+ "\"phoneNumbers\": [ { \"value\": \"555-555-5555\", \"type\": \"work\" }, { \"value\": \"555-555-4444\", \"primary\": \"true\" } ]"
				+ ",\"addresses\":[{\"country\":\"FI\"}]"
				+ "}";
		
		ClientResponse responseMsg = resource().path(RESOURCE)
				.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, input);
		
		String expected = "{\"Errors\":[{\"description\":\"555-555-5555Providedmobilenumberisinvalid.\",\"code\":\"400\","
				+ "\"uri\":\"urn:info:scim:errors:1.0:phoneNumbers:phoneNumber:invalid\"},"
				+ "{\"description\":\"555-555-4444Providedmobilenumberisinvalid.\",\"code\":\"400\","
				+ "\"uri\":\"urn:info:scim:errors:1.0:phoneNumbers:phoneNumber:invalid\"}]}";
		
		Assert.assertEquals("Http status code 400 expected.", 400, responseMsg.getStatus());
		
		String response = responseMsg.getEntity(String.class);
		
		Assert.assertEquals("Expexted string from response not found.", expected, response.replaceAll("\n", "").replaceAll(" ", ""));
	}
	
//	@Test
	public void testRegisterUserRawJsonError() {
		String input = "{ \"schemas\":[\"urn:scim:schemas:core:1.0\"], "
				+ "\"userName\":\"testSCIMUser\","
				+ "\"name\":{"
				+ "\"familyName\":\"Jensen\","
				+ "\"givenName\":\"Barbara\" }"
				+ "}";
		
		ClientResponse responseMsg = resource().path(RESOURCE)
//				.header(
//				"Authorization",
//				"Bearer APssMFxxoGq2r9joK9Id0dTsiGvwmXu8X2Wm5_4sCMSRpkBRUpdKjtisdLK7S1rIqJIUIgach7o!")
				.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, input);
		
		Assert.assertEquals(400, responseMsg.getStatus());
	}
	
	@Test
	public void testRegisterUserRawXML2() {
		long nanoTime = System.nanoTime();
		
		String input = "<SCIM xmlns=\"urn:scim:schemas:core:1.0\" " +
				"xmlns:enterprise=\"urn:scim:schemas:extension:enterprise:1.0\">" +
				"<userName>S" + nanoTime + "</userName>" +
				"<preferredLanguage>en</preferredLanguage>" +
				"<name>" +
				"<givenName>Keimo</givenName>" +
				"</name>" +
				"<password>foobar</password>" +
				"<emails>" +
				"<email>" +
				"<value>S" + nanoTime + "@test.com</value>" +
				"<primary>true</primary>" +
				"</email>" +
				"</emails>" +
				"<phoneNumbers>" +
				"<phoneNumber>" +
				"<value>+" + nanoTime + "</value>" +
				"<primary>true</primary>" +
				"</phoneNumber>" +
				"</phoneNumbers>" +
				"<addresses><address><country>FI</country></address></addresses>" +
				"</SCIM>";
		
		ClientResponse cr = resource().path(RESOURCE)
				.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML)
				.post(ClientResponse.class, input);
		
//		String responseContent = cr.getEntity(String.class);
		Assert.assertEquals(201, cr.getStatus());
	}
	
	@Test
	public void testRegisterUserReservedUsername() {
		String input = "{ \"schemas\":[\"urn:scim:schemas:core:1.0\"], "
				+ "\"userName\":\"existing\","
				+ "\"preferredLanguage\":\"en\","
				+ "\"name\":{"
				+ "\"familyName\":\"Jensen\","
				+ "\"givenName\":\"Barbara\"},"
				+ "\"password\":\"foobar\""
				+ ","
				+ "\"emails\": ["
				+ "{"
				+ "\"value\": \"bjensen@example.com\","
				+ "\"primary\": true"
				+ "},"
				+ "{"
				+ "\"value\": \"bjensen2@example.com\","
				+ "\"primary\": false"
				+ "}]"
				+ ",\"addresses\": [{\"country\": \"FI\"}]"
				+ "}";
		
		ClientResponse responseMsg = resource().path(RESOURCE)
				.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, input);
		
		Assert.assertEquals(403, responseMsg.getStatus());
	}
	
	@Test
	public void testRegisterUserReservedEmail() {
		String input = "{ \"schemas\":[\"urn:scim:schemas:core:1.0\"], "
				+ "\"userName\":\"testSCIMUser\","
				+ "\"preferredLanguage\":\"en\","
				+ "\"name\":{"
				+ "\"familyName\":\"Jensen\","
				+ "\"givenName\":\"Barbara\"},"
				+ "\"password\":\"foobar\""
				+ ","
				+ "\"emails\": ["
				+ "{"
				+ "\"value\": \"reserved@noksu.com\","
				+ "\"primary\": true"
				+ "}"
				+ "]"
				+ ",\"addresses\": [{\"country\": \"FI\"}]"
				+ "}";
		
		ClientResponse responseMsg = resource().path(RESOURCE)
				.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, input);
		
		Assert.assertEquals(403, responseMsg.getStatus());
	}
	
	@Test
	public void testInvalidInput() {
		String username = "" + System.currentTimeMillis();
		
		String input = "<SCIM xmlns=\"urn:scim:schemas:core:1.0\">" +
				"<userName>" + username + "</userName>" +
				"<preferredLanguage>en</preferredLanguage>" +
				"<password>123456</password>" +
				"<emails>" +
				"<email>" +
				"<value>" + username + "@test.com</value>" +
				"<primary>true</primary>" +
				"</email>" +
				"</emails>" +
				"<addresses><address><country>FI</country></address></addresses>" +
				"</XXX>>";
		
		ClientResponse cr = resource().path(RESOURCE)
				.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML)
				.post(ClientResponse.class, input);
		
		String response = cr.getEntity(String.class);
		
		Assert.assertEquals("Http status code 400 expected.", 400, cr.getStatus());
		Assert.assertTrue("Expexted string from response not found.", response.indexOf("Invalid, syntactically incorrect or unparseable input provided") > 1);
		Assert.assertTrue("Expexted string from response not found.", response.indexOf("urn:info:scim:errors:1.0:input:invalid") > 1);
	}
	
	@Test
	public void testRegisterUserRawTwoPrimaryAddresses() {
		String username = "" + System.currentTimeMillis();
		
		String input = "<SCIM xmlns=\"urn:scim:schemas:core:1.0\">" +
				"<userName>" + username + "</userName>" +
				"<preferredLanguage>en</preferredLanguage>" +
				"<password>123456</password>" +
				"<emails>" +
				"<email>" +
				"<value>" + username + "@test.com</value>" +
				"<primary>true</primary>" +
				"</email>" +
				"</emails>" +
				"<addresses><address><country>FI</country><primary>true</primary></address><address><country>SE</country><primary>true</primary></address></addresses>" +
				"</SCIM>";
		
		ClientResponse cr = resource().path(RESOURCE)
				.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML)
				.post(ClientResponse.class, input);
		
		String response = cr.getEntity(String.class);
		Assert.assertEquals("Http status code 400 expected.", 400, cr.getStatus());
		Assert.assertTrue("Expexted string from response not found.", response.indexOf("Multiple primary addresses provided.") > 1);
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