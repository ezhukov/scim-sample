package eugene.zhukov.api;

import java.text.ParseException;
import java.util.HashMap;

import javax.ws.rs.core.MediaType;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

import eugene.zhukov.ApplicationContextProvider;
import eugene.zhukov.SCIMFilter;

public class AccountManagementTest extends JerseyTest {

	private static final String RESOURCE = "/Users";

	 private static final AppDescriptor APP_DESCRIPTOR
	 		= new WebAppDescriptor.Builder("eugene.zhukov").addFilter(SCIMFilter.class, "f").build();

	public AccountManagementTest() {
		super(APP_DESCRIPTOR);
	}

	@BeforeClass
	public static void initAppContext() throws Exception {
		new ApplicationContextProvider().setApplicationContext(
				new ClassPathXmlApplicationContext("springConfiguration.xml"));
	}

	@Test
	public void testRegisterUserRawXML() {
		String username = "" + System.currentTimeMillis();
		
		String input = "<User xmlns=\"urn:scim:schemas:core:1.0\" " +
				"xmlns:enterprise=\"urn:scim:schemas:extension:enterprise:1.0\">" +
				"<userName>" + username + "</userName>" +
				"<preferredLanguage>en_GB</preferredLanguage>" +
				"<password>123456</password>" +
				"<emails>" +
				"<email>" +
				"<value>" + username + "@test.com</value>" +
				"<primary>true</primary>" +
				"</email>" +
				"<email>" +
				"<value>" + username + "2@test.com</value>" +
//				"<primary>false</primary>" +
				"</email>" +
				"</emails>" +
				"<addresses><address><country>FI</country></address></addresses>" +
				"<enterprise:gender>female</enterprise:gender>" +
				"</User>";
		
		ClientResponse cr = resource().path(RESOURCE)
				.header(
						"Authorization",
						"Bearer Mnxabms6rYiy+mb1uOzeMjSuf0hhzYvWeZKjsaqMh+A6SkP5oOH5neORSkQOXsbXOZFfwT6v9UM6sltOWWYT6umfGvrsKJHLMtTzSMs5GrAfeai/ilNYrjgd49QV0QJrimQXsdCkcJqNNCm8eyVP5W7GD+jMk6CVN1mvExngAVk=")
				.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML)
				.post(ClientResponse.class, input);
		
		String response = cr.getEntity(String.class);
//		System.out.println(response);
		Assert.assertTrue("Expected string from response not found.", response.indexOf("<userName>" + username + "</userName>") > 1);
		Assert.assertEquals("Http status code 201 expected.", 201, cr.getStatus());

		cr = resource().path(RESOURCE + "/" + extractValue(response, "id"))
				.header(
						"Authorization",
						"Bearer Mnxabms6rYiy+mb1uOzeMjSuf0hhzYvWeZKjsaqMh+A6SkP5oOH5neORSkQOXsbXOZFfwT6v9UM6sltOWWYT6umfGvrsKJHLMtTzSMs5GrAfeai/ilNYrjgd49QV0QJrimQXsdCkcJqNNCm8eyVP5W7GD+jMk6CVN1mvExngAVk=")
				.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);

		response = cr.getEntity(String.class);
//		System.out.println(response);
		Assert.assertEquals("Http status code 200 expected.", 200, cr.getStatus());
	}
	
	@Test
	public void testRegisterAndUpdateUserRawJson() throws ParseException {
		long nanoTime = System.nanoTime();
		
		String input = "{ \"schemas\":[\"urn:scim:schemas:core:1.0\", \"urn:scim:schemas:extension:enterprise:1.0\"], "
				+ "\"userName\":\"S" + nanoTime + "\","
				+ "\"preferredLanguage\":\"en_FI\","
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
				.header(
						"Authorization",
						"Bearer Mnxabms6rYiy+mb1uOzeMjSuf0hhzYvWeZKjsaqMh+A6SkP5oOH5neORSkQOXsbXOZFfwT6v9UM6sltOWWYT6umfGvrsKJHLMtTzSMs5GrAfeai/ilNYrjgd49QV0QJrimQXsdCkcJqNNCm8eyVP5W7GD+jMk6CVN1mvExngAVk=")
				.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_XML)
				.post(ClientResponse.class, input);
		
		Assert.assertEquals(201, response.getStatus());
		String responseContent = response.getEntity(String.class);
		String id = extractValue(responseContent, "id");
		
		response = resource().path(RESOURCE + "/" + id)
				.header(
						"Authorization",
						"Bearer Mnxabms6rYiy+mb1uOzeMjSuf0hhzYvWeZKjsaqMh+A6SkP5oOH5neORSkQOXsbXOZFfwT6v9UM6sltOWWYT6umfGvrsKJHLMtTzSMs5GrAfeai/ilNYrjgd49QV0QJrimQXsdCkcJqNNCm8eyVP5W7GD+jMk6CVN1mvExngAVk=")
				.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML)
				.get(ClientResponse.class);
		Assert.assertEquals(200, response.getStatus());
		responseContent = response.getEntity(String.class);
		
		HashMap<String, String> values = new HashMap<String, String>();
		values.put("familyName", "Zhukov");
		responseContent = substituteValues(values, responseContent);
		
		response = resource().path(RESOURCE + "/" + id)
				.header(
						"Authorization",
						"Bearer Mnxabms6rYiy+mb1uOzeMjSuf0hhzYvWeZKjsaqMh+A6SkP5oOH5neORSkQOXsbXOZFfwT6v9UM6sltOWWYT6umfGvrsKJHLMtTzSMs5GrAfeai/ilNYrjgd49QV0QJrimQXsdCkcJqNNCm8eyVP5W7GD+jMk6CVN1mvExngAVk=")
				.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML)
				.put(ClientResponse.class, responseContent);
		
		Assert.assertEquals(200, response.getStatus());
		responseContent = response.getEntity(String.class);
		Assert.assertEquals("Zhukov", extractValue(responseContent, "familyName"));
//		System.out.println(responseContent);
//		Assert.assertEquals(retrievedProfile.getAccountId(), JsonPath.read(responseContent, "$.id"));
//		Assert.assertEquals("S" + nanoTime + "@example.com", JsonPath.read(responseContent, "$.emails[0].value"));
//		Assert.assertEquals("C" + nanoTime + "@example.com", JsonPath.read(responseContent, "$.emails[1].value"));
	}
	
//	@Test
	public void testRegisterUserError() {
		long nanoTime = System.nanoTime();
		String input = "{ \"schemas\":[\"urn:scim:schemas:core:1.0\"], "
				+ "\"userName\":\"S" + nanoTime + "\","
				+ "\"preferredLanguage\":\"en_SE\","
				+ "\"password\":\"123456\","
				+ "\"name\":{"
				+ "\"familyName\":\"Jensen\","
				+ "\"givenName\":\"Barbara\" }"
				+ ","
				+ "\"phoneNumbers\": [ { \"value\": \"555-555-5555\", \"type\": \"work\" }, { \"value\": \"555-555-4444\", \"primary\": \"true\" } ]"
				+ ",\"addresses\":[{\"country\":\"FI\"}]"
				+ "}";
		
		ClientResponse responseMsg = resource().path(RESOURCE)
				.header(
						"Authorization",
						"Bearer Mnxabms6rYiy+mb1uOzeMjSuf0hhzYvWeZKjsaqMh+A6SkP5oOH5neORSkQOXsbXOZFfwT6v9UM6sltOWWYT6umfGvrsKJHLMtTzSMs5GrAfeai/ilNYrjgd49QV0QJrimQXsdCkcJqNNCm8eyVP5W7GD+jMk6CVN1mvExngAVk=")
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

	@Test
	public void testRegisterUserRawXML2() {
		long nanoTime = System.nanoTime();
		
		String input = "<SCIM xmlns=\"urn:scim:schemas:core:1.0\" " +
				"xmlns:enterprise=\"urn:scim:schemas:extension:enterprise:1.0\">" +
				"<userName>S" + nanoTime + "</userName>" +
				"<preferredLanguage>en_US</preferredLanguage>" +
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
				.header(
						"Authorization",
						"Bearer Mnxabms6rYiy+mb1uOzeMjSuf0hhzYvWeZKjsaqMh+A6SkP5oOH5neORSkQOXsbXOZFfwT6v9UM6sltOWWYT6umfGvrsKJHLMtTzSMs5GrAfeai/ilNYrjgd49QV0QJrimQXsdCkcJqNNCm8eyVP5W7GD+jMk6CVN1mvExngAVk=")
				.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML)
				.post(ClientResponse.class, input);
		
//		String responseContent = cr.getEntity(String.class);
		Assert.assertEquals(201, cr.getStatus());
	}
	
	@Test
	public void testRegisterUserReservedUsername() {
		String input = "{ \"schemas\":[\"urn:scim:schemas:core:1.0\"], "
				+ "\"userName\":\"existing\","
				+ "\"preferredLanguage\":\"en_US\","
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
				.header(
						"Authorization",
						"Bearer Mnxabms6rYiy+mb1uOzeMjSuf0hhzYvWeZKjsaqMh+A6SkP5oOH5neORSkQOXsbXOZFfwT6v9UM6sltOWWYT6umfGvrsKJHLMtTzSMs5GrAfeai/ilNYrjgd49QV0QJrimQXsdCkcJqNNCm8eyVP5W7GD+jMk6CVN1mvExngAVk=")
				.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, input);
//		String responseContent = responseMsg.getEntity(String.class);
//		System.out.println(responseContent);
		Assert.assertEquals(409, responseMsg.getStatus());
	}
	
	@Test
	public void testRegisterUserReservedEmail() {
		long nanoTime = System.nanoTime();
		
		String input = "{ \"schemas\":[\"urn:scim:schemas:core:1.0\"], "
				+ "\"userName\":\"S" + nanoTime + "\","
				+ "\"preferredLanguage\":\"en_GB\","
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
				.header(
						"Authorization",
						"Bearer Mnxabms6rYiy+mb1uOzeMjSuf0hhzYvWeZKjsaqMh+A6SkP5oOH5neORSkQOXsbXOZFfwT6v9UM6sltOWWYT6umfGvrsKJHLMtTzSMs5GrAfeai/ilNYrjgd49QV0QJrimQXsdCkcJqNNCm8eyVP5W7GD+jMk6CVN1mvExngAVk=")
				.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, input);

		Assert.assertEquals(409, responseMsg.getStatus());
	}
	
	@Test
	public void testInvalidInput() {
		String username = "" + System.currentTimeMillis();
		
		String input = "<SCIM xmlns=\"urn:scim:schemas:core:1.0\">" +
				"<userName>" + username + "</userName>" +
				"<preferredLanguage>en_FI</preferredLanguage>" +
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
				.header(
						"Authorization",
						"Bearer Mnxabms6rYiy+mb1uOzeMjSuf0hhzYvWeZKjsaqMh+A6SkP5oOH5neORSkQOXsbXOZFfwT6v9UM6sltOWWYT6umfGvrsKJHLMtTzSMs5GrAfeai/ilNYrjgd49QV0QJrimQXsdCkcJqNNCm8eyVP5W7GD+jMk6CVN1mvExngAVk=")
				.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML)
				.post(ClientResponse.class, input);
		
		String response = cr.getEntity(String.class);
		
		Assert.assertEquals("Http status code 400 expected.", 400, cr.getStatus());
		Assert.assertTrue("Expexted string from response not found.", response.indexOf("Invalid, syntactically incorrect or unparseable input provided") > 1);
		Assert.assertTrue("Expexted string from response not found.", response.indexOf("urn:eugene.zhukov:scim:errors:1.0:input:invalid") > 1);
	}
	
//	@Test
	public void testRegisterUserRawTwoPrimaryAddresses() {
		String username = "" + System.currentTimeMillis();
		
		String input = "<SCIM xmlns=\"urn:scim:schemas:core:1.0\">" +
				"<userName>" + username + "</userName>" +
				"<preferredLanguage>en_SE</preferredLanguage>" +
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
				.header(
						"Authorization",
						"Bearer Mnxabms6rYiy+mb1uOzeMjSuf0hhzYvWeZKjsaqMh+A6SkP5oOH5neORSkQOXsbXOZFfwT6v9UM6sltOWWYT6umfGvrsKJHLMtTzSMs5GrAfeai/ilNYrjgd49QV0QJrimQXsdCkcJqNNCm8eyVP5W7GD+jMk6CVN1mvExngAVk=")
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

	private static String substituteValues(HashMap<String, String> valuesIn, String xml) {
		
		if (valuesIn == null) {
			return xml;
		}

		for (String attr : valuesIn.keySet()) {

			if (xml.contains("</" + attr + ">")) {
				xml = xml.replace(extractValue(xml, attr), valuesIn.get(attr));
			}
		}
		return xml;
	}
}