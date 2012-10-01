package eugene.zhukov.providers;

import eugene.zhukov.SCIMException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SCIMExceptionMapper implements ExceptionMapper<javax.ws.rs.WebApplicationException> {

	@Override
	public Response toResponse(javax.ws.rs.WebApplicationException exception) {

		if (exception.getCause() != null
				&& exception.getCause().getCause() instanceof org.xml.sax.SAXParseException) {
			return SCIMException.constructErrorResponse(
					Response.Status.BAD_REQUEST, "input:invalid", SCIMException.BAD_REQUEST);
		}
		return exception.getResponse();
	}
}