package eugene.zhukov.api;

import static eugene.zhukov.SCIMFilter.API_VERSION;
import static eugene.zhukov.SCIMFilter.ENDPOINT_GROUPS;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import eugene.zhukov.api.annotation.PATCH;

@Path(API_VERSION + ENDPOINT_GROUPS)
public class GroupsEndpoint {

	@GET
	@Path("{id}")
	public javax.ws.rs.core.Response get() {
		return javax.ws.rs.core.Response.status(501).build();
	}

	@POST
	public javax.ws.rs.core.Response post() {
		return javax.ws.rs.core.Response.status(501).build();
	}

	@PUT
	@Path("{id}")
	public javax.ws.rs.core.Response put() {
		return javax.ws.rs.core.Response.status(501).build();
	}

	@PATCH
	@Path("{id}")
	public javax.ws.rs.core.Response patch() {
		return javax.ws.rs.core.Response.status(501).build();
	}

	@DELETE
	@Path("{id}")
	public javax.ws.rs.core.Response delete() {
		return javax.ws.rs.core.Response.status(501).build();
	}
}
