package eugene.zhukov.providers;

import java.io.IOException;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.type.TypeReference;

import scim.schemas.v1.Address;
import scim.schemas.v1.AuthenticationScheme;
import scim.schemas.v1.Error;
import scim.schemas.v1.MultiValuedAttribute;
import scim.schemas.v1.ObjectFactory;
import scim.schemas.v1.Response;
import scim.schemas.v1.Schema.Attributes;
import scim.schemas.v1.SchemaAttribute;
import scim.schemas.v1.ServiceProviderConfig.AuthenticationSchemes;
import scim.schemas.v1.User.Addresses;
import scim.schemas.v1.User.Emails;
import scim.schemas.v1.User.Entitlements;
import scim.schemas.v1.User.Groups;
import scim.schemas.v1.User.Ims;
import scim.schemas.v1.User.PhoneNumbers;
import scim.schemas.v1.User.Photos;
import scim.schemas.v1.User.Roles;
import scim.schemas.v1.User.X509Certificates;

public class SCIMSimpleModule {

	private SimpleModule module;

	SCIMSimpleModule() {

		final ObjectFactory objectFactory = new ObjectFactory();

		module = new SimpleModule("SCIM", new Version(1, 0, 0, null))
				.addSerializer(Attributes.class, new JsonSerializer<Attributes>() {

					@Override
					public void serialize(Attributes attributes, JsonGenerator jsonGenerator,
							SerializerProvider provider) throws IOException, JsonProcessingException {
						jsonGenerator.writeStartArray();

						for (SchemaAttribute attribute : attributes.getAttribute()) {
							jsonGenerator.writeObject(attribute);
						}
						jsonGenerator.writeEndArray();
					}
				})
				.addDeserializer(Emails.class, new JsonDeserializer<Emails>() {

					@Override
					public Emails deserialize(JsonParser jsonParser,
							DeserializationContext context) throws IOException, JsonProcessingException {
						List<MultiValuedAttribute> uem = jsonParser.readValueAs(
								new TypeReference<List<MultiValuedAttribute>>() { });
						Emails ue = objectFactory.createUserEmails();
						ue.getEmail().addAll(uem);

						return ue;
					}
				})
				.addSerializer(Emails.class, new JsonSerializer<Emails>() {

					@Override
					public void serialize(Emails emails, JsonGenerator jsonGenerator,
							SerializerProvider provider) throws IOException, JsonProcessingException {
						jsonGenerator.writeStartArray();

						for (MultiValuedAttribute attribute : emails.getEmail()) {
							jsonGenerator.writeObject(attribute);
						}
						jsonGenerator.writeEndArray();
					}
				})
				.addDeserializer(PhoneNumbers.class, new JsonDeserializer<PhoneNumbers>() {

					@Override
					public PhoneNumbers deserialize(JsonParser jsonParser,
							DeserializationContext context) throws IOException, JsonProcessingException {
						List<MultiValuedAttribute> uem = jsonParser.readValueAs(
								new TypeReference<List<MultiValuedAttribute>>() { });
						PhoneNumbers ue = objectFactory.createUserPhoneNumbers();
						ue.getPhoneNumber().addAll(uem);

						return ue;
					}
				})
				.addSerializer(PhoneNumbers.class, new JsonSerializer<PhoneNumbers>() {

					@Override
					public void serialize(PhoneNumbers numbers, JsonGenerator jsonGenerator,
							SerializerProvider provider) throws IOException, JsonProcessingException {
						jsonGenerator.writeStartArray();

						for (MultiValuedAttribute attribute : numbers.getPhoneNumber()) {
							jsonGenerator.writeObject(attribute);
						}
						jsonGenerator.writeEndArray();
					}
				})
				.addDeserializer(Ims.class, new JsonDeserializer<Ims>() {

					@Override
					public Ims deserialize(JsonParser jsonParser,
							DeserializationContext context) throws IOException, JsonProcessingException {
						List<MultiValuedAttribute> uem = jsonParser.readValueAs(
								new TypeReference<List<MultiValuedAttribute>>() { });
						Ims ue = objectFactory.createUserIms();
						ue.getIm().addAll(uem);

						return ue;
					}
				})
				.addSerializer(Ims.class, new JsonSerializer<Ims>() {

					@Override
					public void serialize(Ims ims, JsonGenerator jsonGenerator,
							SerializerProvider provider) throws IOException, JsonProcessingException {
						jsonGenerator.writeStartArray();

						for (MultiValuedAttribute attribute : ims.getIm()) {
							jsonGenerator.writeObject(attribute);
						}
						jsonGenerator.writeEndArray();
					}
				})
				.addDeserializer(Photos.class, new JsonDeserializer<Photos>() {

					@Override
					public Photos deserialize(JsonParser jsonParser,
							DeserializationContext context) throws IOException, JsonProcessingException {
						List<MultiValuedAttribute> uem = jsonParser.readValueAs(
								new TypeReference<List<MultiValuedAttribute>>() { });
						Photos ue = objectFactory.createUserPhotos();
						ue.getPhoto().addAll(uem);

						return ue;
					}
				})
				.addSerializer(Photos.class, new JsonSerializer<Photos>() {

					@Override
					public void serialize(Photos photos, JsonGenerator jsonGenerator,
							SerializerProvider provider) throws IOException, JsonProcessingException {
						jsonGenerator.writeStartArray();

						for (MultiValuedAttribute attribute : photos.getPhoto()) {
							jsonGenerator.writeObject(attribute);
						}
						jsonGenerator.writeEndArray();
					}
				})
				.addDeserializer(Groups.class, new JsonDeserializer<Groups>() {

					@Override
					public Groups deserialize(JsonParser jsonParser,
							DeserializationContext context) throws IOException, JsonProcessingException {
						List<MultiValuedAttribute> uem = jsonParser.readValueAs(
								new TypeReference<List<MultiValuedAttribute>>() { });
						Groups ue = objectFactory.createUserGroups();
						ue.getGroup().addAll(uem);

						return ue;
					}
				})
				.addSerializer(Groups.class, new JsonSerializer<Groups>() {

					@Override
					public void serialize(Groups groups, JsonGenerator jsonGenerator,
							SerializerProvider provider) throws IOException, JsonProcessingException {
						jsonGenerator.writeStartArray();

						for (MultiValuedAttribute attribute : groups.getGroup()) {
							jsonGenerator.writeObject(attribute);
						}
						jsonGenerator.writeEndArray();
					}
				})
				.addDeserializer(Entitlements.class, new JsonDeserializer<Entitlements>() {

					@Override
					public Entitlements deserialize(JsonParser jsonParser,
							DeserializationContext context) throws IOException, JsonProcessingException {
						List<MultiValuedAttribute> uem = jsonParser.readValueAs(
								new TypeReference<List<MultiValuedAttribute>>() { });
						Entitlements ue = objectFactory.createUserEntitlements();
						ue.getEntitlement().addAll(uem);

						return ue;
					}
				})
				.addSerializer(Entitlements.class, new JsonSerializer<Entitlements>() {

					@Override
					public void serialize(Entitlements entitlements, JsonGenerator jsonGenerator,
							SerializerProvider provider) throws IOException, JsonProcessingException {
						jsonGenerator.writeStartArray();

						for (MultiValuedAttribute attribute : entitlements.getEntitlement()) {
							jsonGenerator.writeObject(attribute);
						}
						jsonGenerator.writeEndArray();
					}
				})
				.addDeserializer(Roles.class, new JsonDeserializer<Roles>() {

					@Override
					public Roles deserialize(JsonParser jsonParser,
							DeserializationContext context) throws IOException, JsonProcessingException {
						List<MultiValuedAttribute> uem = jsonParser.readValueAs(
								new TypeReference<List<MultiValuedAttribute>>() { });
						Roles ue = objectFactory.createUserRoles();
						ue.getRole().addAll(uem);

						return ue;
					}
				})
				.addSerializer(Roles.class, new JsonSerializer<Roles>() {

					@Override
					public void serialize(Roles roles, JsonGenerator jsonGenerator,
							SerializerProvider provider) throws IOException, JsonProcessingException {
						jsonGenerator.writeStartArray();

						for (MultiValuedAttribute attribute : roles.getRole()) {
							jsonGenerator.writeObject(attribute);
						}
						jsonGenerator.writeEndArray();
					}
				})
				.addDeserializer(X509Certificates.class, new JsonDeserializer<X509Certificates>() {

					@Override
					public X509Certificates deserialize(JsonParser jsonParser,
							DeserializationContext context) throws IOException, JsonProcessingException {
						List<MultiValuedAttribute> uem = jsonParser.readValueAs(
								new TypeReference<List<MultiValuedAttribute>>() { });
						X509Certificates ue = objectFactory.createUserX509Certificates();
						ue.getX509Certificate().addAll(uem);

						return ue;
					}
				})
				.addSerializer(X509Certificates.class, new JsonSerializer<X509Certificates>() {

					@Override
					public void serialize(X509Certificates x509Certificates, JsonGenerator jsonGenerator,
							SerializerProvider provider) throws IOException, JsonProcessingException {
						jsonGenerator.writeStartArray();

						for (MultiValuedAttribute attribute : x509Certificates.getX509Certificate()) {
							jsonGenerator.writeObject(attribute);
						}
						jsonGenerator.writeEndArray();
					}
				})
				.addDeserializer(Addresses.class, new JsonDeserializer<Addresses>() {

					@Override
					public Addresses deserialize(JsonParser jsonParser,
							DeserializationContext context) throws IOException, JsonProcessingException {
						List<Address> ua = jsonParser.readValueAs(
								new TypeReference<List<Address>>() { });
						Addresses uas = objectFactory.createUserAddresses();
						uas.getAddress().addAll(ua);

						return uas;
					}
				})
				.addSerializer(Addresses.class, new JsonSerializer<Addresses>() {

					@Override
					public void serialize(Addresses addresses, JsonGenerator jsonGenerator,
							SerializerProvider provider) throws IOException, JsonProcessingException {
						jsonGenerator.writeStartArray();

						for (Address attribute : addresses.getAddress()) {
							jsonGenerator.writeObject(attribute);
						}
						jsonGenerator.writeEndArray();
					}
				})
				.addSerializer(XMLGregorianCalendar.class, new JsonSerializer<XMLGregorianCalendar>() {

					@Override
					public void serialize(XMLGregorianCalendar calendar, JsonGenerator jsonGenerator,
							SerializerProvider provider) throws IOException, JsonProcessingException {
						jsonGenerator.writeString(calendar.toString());
					}
				})
				.addSerializer(AuthenticationSchemes.class,	new JsonSerializer<AuthenticationSchemes>() {

					@Override
					public void serialize(AuthenticationSchemes schemes, JsonGenerator jsonGenerator,
							SerializerProvider provider) throws IOException, JsonProcessingException {
						jsonGenerator.writeStartArray();

						for (AuthenticationScheme attribute : schemes.getAuthenticationScheme()) {
							jsonGenerator.writeObject(attribute);
						}
						jsonGenerator.writeEndArray();
					}
				})
				.addSerializer(Response.class, new JsonSerializer<Response>() {

					@Override
					public void serialize(Response response, JsonGenerator jsonGenerator,
							SerializerProvider provider) throws IOException, JsonProcessingException {

						if (response.getErrors() != null) {
							jsonGenerator.writeStartObject();
							jsonGenerator.writeFieldName("Errors");
							jsonGenerator.writeStartArray();

							for (Error attribute : response.getErrors().getError()) {
								jsonGenerator.writeObject(attribute);
							}
							jsonGenerator.writeEndArray();
							jsonGenerator.writeEndObject();

						} else if (response.getResource() != null) {
							response.getResource().getSchemas().add("urn:scim:schemas:core:1.0");
							response.getResource().getSchemas().add("urn:scim:schemas:extension:enterprise:1.0");
							jsonGenerator.writeObject(response.getResource());

						} else if (response.getResources() != null) {
							jsonGenerator.writeObject(response.getResources());
						}
					}
				});
	}

	public SimpleModule getModule() {
		return module;
	}
}
