package eugene.zhukov;

import static eugene.zhukov.EndpointConstants.API_VERSION;
import static eugene.zhukov.EndpointConstants.ENDPOINT_ERRORS;
import static eugene.zhukov.EndpointConstants.ENDPOINT_GROUPS;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(ENDPOINT_ERRORS)
public class ErrorHandler {

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Consumes({ MediaType.WILDCARD })
	public Response get(@Context HttpServletRequest request) {
		Integer statusCode = (Integer) request
				.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

		if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
			return SCIMException.constructErrorResponse(
					Response.Status.fromStatusCode(statusCode),
					"unauthorized", SCIMException.UNAUTHORIZED);
		}
		String errorUri = (String) request
				.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

		if (errorUri != null && statusCode == HttpServletResponse.SC_NOT_FOUND
				&& errorUri.endsWith(API_VERSION.concat(ENDPOINT_GROUPS))) {
			return SCIMException.constructErrorResponse(HttpServletResponse.SC_NOT_IMPLEMENTED);
		}

		return SCIMException.constructErrorResponse(statusCode);
	}

	@POST
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Consumes({ MediaType.WILDCARD })
	public Response post(@Context HttpServletRequest request) {
		return get(request);
	}
}
