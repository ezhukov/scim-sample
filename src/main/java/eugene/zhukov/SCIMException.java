package eugene.zhukov;

import javax.ws.rs.WebApplicationException;

import scim.schemas.v1.Response;
import scim.schemas.v1.Error;
import javax.ws.rs.core.Response.Status;

public class SCIMException extends WebApplicationException {

	private static final long serialVersionUID = -69032204675769L;
	public static final String BAD_REQUEST = "Invalid, syntactically incorrect or unparseable input provided";
	public static final String SCIM_ERRORS = "urn:eugene.zhukov:scim:errors:1.0:";

	public SCIMException(Status code, String fieldAndReason, String description) {
		super(constructErrorResponse(code, fieldAndReason, description));
	}

	public SCIMException(Status code, String fieldAndReason) {
		super(constructErrorResponse(code, fieldAndReason, null));
	}

	public static javax.ws.rs.core.Response constructErrorResponse(Status code, String fieldAndReason, String description) {
		Response response = new Response();
		Response.Errors errors = new Response.Errors();
		Error error = new Error();
		error.setCode(String.valueOf(code.getStatusCode()));
		error.setUri(SCIM_ERRORS + fieldAndReason);
		error.setDescription(description);
		errors.getError().add(error);
		response.setErrors(errors);

		return javax.ws.rs.core.Response.status(code).entity(response).build();
	}
}