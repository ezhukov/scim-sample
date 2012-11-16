package eugene.zhukov.providers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.dao.DataIntegrityViolationException;

import eugene.zhukov.SCIMException;

/**
 * Catches Spring DataIntegrityViolationExceptions thrown by PostgreSQL JDBC driver.
 * Checks error message and returns appropriate JSON/XML response. 
 *
 */
@Provider
public class DataIntegrityExceptionMapper implements ExceptionMapper<DataIntegrityViolationException> {

	@Override
	public Response toResponse(DataIntegrityViolationException exception) {
		String msg = exception.getRootCause().getMessage();

		if (msg.endsWith("violates not-null constraint")) {
			return SCIMException.constructErrorResponse(
					Response.Status.BAD_REQUEST, "input:required", msg);

		} else if (msg.startsWith("ERROR: value too long for type")) {
			return SCIMException.constructErrorResponse(
					Response.Status.BAD_REQUEST, "input:invalid", msg);
		}

		throw exception;
	}
}
