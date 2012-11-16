package eugene.zhukov.providers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import eugene.zhukov.SCIMException;

/**
 * Catches SaxParseExceptions when unparseable xml input is provided.
 *
 */
@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

	@Override
	public Response toResponse(WebApplicationException exception) {

		if (exception.getCause() != null
				&& exception.getCause().getCause() instanceof org.xml.sax.SAXParseException) {
			return SCIMException.constructErrorResponse(
					Response.Status.BAD_REQUEST, "input:invalid", SCIMException.BAD_REQUEST);
		}
		return exception.getResponse();
	}
}