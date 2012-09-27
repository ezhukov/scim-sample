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
import scim.schemas.v1.Error;
import scim.schemas.v1.MultiValuedAttribute;
import scim.schemas.v1.ObjectFactory;
import scim.schemas.v1.Response;
import scim.schemas.v1.User.Addresses;
import scim.schemas.v1.User.Emails;
import scim.schemas.v1.User.PhoneNumbers;

public class SCIMSimpleModule {

	private SimpleModule module;

	SCIMSimpleModule() {

		final ObjectFactory objectFactory = new ObjectFactory();

		module = new SimpleModule("SCIM", new Version(1, 0, 0, null))
				.addDeserializer(Emails.class, new JsonDeserializer<Emails>() {

					@Override
					public Emails deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JsonProcessingException {
						List<MultiValuedAttribute> uem = jsonParser.readValueAs(new TypeReference<List<MultiValuedAttribute>>() {
						});
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
					public PhoneNumbers deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JsonProcessingException {
						List<MultiValuedAttribute> uem = jsonParser.readValueAs(new TypeReference<List<MultiValuedAttribute>>() {
						});
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
				.addDeserializer(Addresses.class, new JsonDeserializer<Addresses>() {
					
					@Override
					public Addresses deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JsonProcessingException {
						List<Address> ua = jsonParser.readValueAs(new TypeReference<List<Address>>() {
						});
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
//						jsonGenerator.writeString(String.format("%tFT%<tTZ", calendar.toGregorianCalendar()));
						jsonGenerator.writeString(calendar.toString());
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
