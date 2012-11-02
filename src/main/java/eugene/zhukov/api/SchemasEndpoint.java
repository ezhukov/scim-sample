package eugene.zhukov.api;

import static eugene.zhukov.EndpointConstants.API_VERSION;
import static eugene.zhukov.EndpointConstants.ENDPOINT_USERS;
import static eugene.zhukov.EndpointConstants.ENDPOINT_SCHEMAS;
import static javax.ws.rs.core.Response.Status.OK;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import scim.schemas.v1.Response;
import scim.schemas.v1.Schema;

@Path(API_VERSION + ENDPOINT_SCHEMAS)
public class SchemasEndpoint {

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public javax.ws.rs.core.Response retrieve() {
		Response response = new Response();
		Schema schema = new Schema();
		schema.setId("urn:scim:schemas:core:1.0:User");
		schema.setName("User");
		schema.setDescription("http://tools.ietf.org/html/draft-ietf-scim-core-schema-00#section-11.6");
		schema.setEndpoint(ENDPOINT_USERS);
		response.setResource(schema);
		return javax.ws.rs.core.Response.status(OK).entity(response).build();
	}
}
