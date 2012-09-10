package eugene.zhukov.api;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import scim.schemas.v1.User;
import eugene.zhukov.api.annotation.PATCH;

@Path("v1/Users/{accountId}")
public class AccountManagement {
	
	@DELETE
	public Response remove() {
		return Response.status(Response.Status.OK).build();
	}
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response retrieve(@Context HttpServletRequest request) {
		return Response.status(Response.Status.OK).build();
	}
	
	@PATCH
	@Path("password")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response passwordChange(@Context HttpServletRequest request, User user) {
		return Response.status(Response.Status.NO_CONTENT).build();
	}
}
