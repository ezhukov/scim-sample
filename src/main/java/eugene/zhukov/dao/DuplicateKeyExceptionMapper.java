package eugene.zhukov.dao;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.dao.DuplicateKeyException;

import eugene.zhukov.SCIMException;

/**
 * Catches Spring DuplicateKeyExceptions thrown by PostgreSQL JDBC driver.
 * Checks unique constraint name and sets appropriate JSON/XML response. 
 *
 */
@Provider
public class DuplicateKeyExceptionMapper implements ExceptionMapper<DuplicateKeyException> {

	@Override
	public Response toResponse(DuplicateKeyException exception) {
		String msg = exception.getRootCause().getMessage();

		if (msg.contains("emails_value_key")) {
			return SCIMException.constructErrorResponse(
					Response.Status.CONFLICT, "email:reserved", null);

		} else if (msg.contains("users_username_key")) {
			return SCIMException.constructErrorResponse(
					Response.Status.CONFLICT, "username:reserved", null);
		}

		throw exception;
	}
}
