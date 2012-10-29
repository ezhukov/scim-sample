package eugene.zhukov.api;

import static eugene.zhukov.EndpointConstants.API_VERSION;
import static eugene.zhukov.EndpointConstants.ENDPOINT_GROUPS;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import eugene.zhukov.api.annotation.PATCH;

@Path(API_VERSION + ENDPOINT_GROUPS)
public class GroupsEndpoint {

	@GET
	@Path("{id}")
	public javax.ws.rs.core.Response get(@Context HttpServletResponse response) {
		response.setContentLength(0);
		return javax.ws.rs.core.Response.status(501).build();
	}

	@POST
	public javax.ws.rs.core.Response post(@Context HttpServletResponse response) {
		response.setContentLength(0);
		return javax.ws.rs.core.Response.status(501).build();
	}

	@PUT
	@Path("{id}")
	public javax.ws.rs.core.Response put(@Context HttpServletResponse response) {
		response.setContentLength(0);
		return javax.ws.rs.core.Response.status(501).build();
	}

	@PATCH
	@Path("{id}")
	public javax.ws.rs.core.Response patch(@Context HttpServletResponse response) {
		response.setContentLength(0);
		return javax.ws.rs.core.Response.status(501).build();
	}

	@DELETE
	@Path("{id}")
	public javax.ws.rs.core.Response delete(@Context HttpServletResponse response) {
		response.setContentLength(0);
		return javax.ws.rs.core.Response.status(501).build();
	}
}
