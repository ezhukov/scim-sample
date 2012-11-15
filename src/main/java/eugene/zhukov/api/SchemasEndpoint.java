package eugene.zhukov.api;

import static eugene.zhukov.EndpointConstants.API_VERSION;
import static eugene.zhukov.EndpointConstants.ENDPOINT_SCHEMAS;
import static eugene.zhukov.EndpointConstants.ENDPOINT_USERS;
import static eugene.zhukov.EndpointConstants.ENDPOINT_GROUPS;
import static javax.ws.rs.core.Response.Status.OK;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import scim.schemas.v1.Response;
import scim.schemas.v1.Schema;
import scim.schemas.v1.SchemaAttribute;

@Path(API_VERSION + ENDPOINT_SCHEMAS)
public class SchemasEndpoint {

	@GET
	@Path(ENDPOINT_USERS)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public javax.ws.rs.core.Response getUsers() {
		Response response = new Response();
		Schema schema = new Schema();
		schema.setId("urn:scim:schemas:core:1.0:User");
		schema.setName("User");
		schema.setDescription("http://tools.ietf.org/html/draft-ietf-scim-core-schema-00#section-11.6");
		schema.setEndpoint(ENDPOINT_USERS);

		Schema.Attributes attributes = new Schema.Attributes();
		SchemaAttribute attribute = new SchemaAttribute();
		attribute.setCaseExact(false);
		attribute.setDescription("Unique identifier for the User resource");
		attribute.setMultiValued(false);
		attribute.setName("id");
		attribute.setReadOnly(true);
		attribute.setRequired(true);
		attribute.setSchema("urn:scim:schemas:core:1.0");
		attribute.setType("UUID");

		attributes.getAttribute().add(attribute);

		schema.setAttributes(attributes);
		response.setResource(schema);
		return javax.ws.rs.core.Response.status(OK).entity(response).build();
	}

	@GET
	@Path(ENDPOINT_GROUPS)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public javax.ws.rs.core.Response getGroups() {
		Response response = new Response();
		Schema schema = new Schema();
		schema.setId("urn:scim:schemas:core:1.0:Group");
		schema.setName("Group");
		schema.setEndpoint(ENDPOINT_GROUPS);

		Schema.Attributes attributes = new Schema.Attributes();
		SchemaAttribute attribute = new SchemaAttribute();
		attribute.setCaseExact(false);
		attribute.setMultiValued(false);
		attribute.setName("displayName");
		attribute.setReadOnly(true);
		attribute.setRequired(true);
		attribute.setSchema("urn:scim:schemas:core:1.0");
		attribute.setType("String");

		attributes.getAttribute().add(attribute);

		schema.setAttributes(attributes);
		response.setResource(schema);
		return javax.ws.rs.core.Response.status(OK).entity(response).build();
	}
}
