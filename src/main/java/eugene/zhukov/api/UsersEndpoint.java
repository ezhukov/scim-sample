package eugene.zhukov.api;

import static eugene.zhukov.ApplicationContextProvider.USER_DAO;
import static eugene.zhukov.EndpointConstants.API_VERSION;
import static eugene.zhukov.EndpointConstants.ENDPOINT_USERS;
import static eugene.zhukov.EndpointConstants.HOST;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_MODIFIED;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;

import java.net.URI;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.xml.ws.BindingProvider;

import scim.schemas.v1.Meta;
import scim.schemas.v1.Response;
import scim.schemas.v1.User;
import eugene.zhukov.ApplicationContextProvider;
import eugene.zhukov.SCIMException;
import eugene.zhukov.api.annotation.PATCH;
import eugene.zhukov.dao.UserDao;
import eugene.zhukov.util.Utils;

@Path(API_VERSION + ENDPOINT_USERS)
public class UsersEndpoint {

	private UserDao userDao = (UserDao) ApplicationContextProvider.getContext().getBean(USER_DAO);

	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public javax.ws.rs.core.Response create(User user) {
		validateUser(user);
		UUID id = userDao.persistUser(user);

		Response response = new Response();
		User newUser = userDao.retrieveUser(id);
		response.setResource(newUser);
		return toResponse(CREATED, response, newUser.getMeta());
	}

	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public javax.ws.rs.core.Response retrieve(
			@PathParam("id") UUID id, @Context HttpServletRequest request) {
		checkPassword(userDao.retrievePasswd(id), request);
		String eTag = Utils.trimOrNull(request.getHeader(HttpHeaders.IF_NONE_MATCH));

		if (eTag != null && eTag.equalsIgnoreCase(userDao.retrieveETag(id))) {
			return javax.ws.rs.core.Response.status(NOT_MODIFIED).build();
		}

		Response response = new Response();
		User user = userDao.retrieveUser(id);
		response.setResource(user);
		return toResponse(OK, response, user.getMeta());
	}

	@PUT
	@Path("{id}")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public javax.ws.rs.core.Response update(
			User user, @PathParam("id") UUID id, @Context HttpServletRequest request) {
		checkPassword(userDao.retrievePasswd(id), request);
		validateUser(user);
		user.setId(id.toString());
		userDao.updateUser(user, checkETag(request));

		Response response = new Response();
		User newUser = userDao.retrieveUser(id);
		response.setResource(newUser);
		return toResponse(OK, response, newUser.getMeta());
	}

	@PATCH
	@Path("{id}/password")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public javax.ws.rs.core.Response passwordChange(
			@PathParam("id") UUID id, User user, @Context HttpServletRequest request) {
		checkPassword(userDao.retrievePasswd(id), request);
		String eTag = userDao.updatePasswd(id, Utils.trimOrNull(user.getPassword()), checkETag(request));

		return javax.ws.rs.core.Response.status(NO_CONTENT).tag(eTag)
				.location(URI.create(HOST + API_VERSION + ENDPOINT_USERS + "/" + id)).build();
	}

	@DELETE
	@Path("{id}")
	public javax.ws.rs.core.Response remove(@PathParam("id") UUID id, @Context HttpServletRequest request) {
		checkPassword(userDao.retrievePasswd(id), request);
		userDao.deleteUser(id);

		return javax.ws.rs.core.Response.status(OK).build();
	}

	private javax.ws.rs.core.Response toResponse(Status status, Response response, Meta meta) {
		return javax.ws.rs.core.Response.status(status).entity(response)
				.tag(meta.getVersion().substring(1, meta.getVersion().length() - 1))
				.location(URI.create(meta.getLocation())).build();
	}

	private void validateUser(User user) {

		if (user.getPreferredLanguage() != null && !Utils.isLocaleValid(user.getPreferredLanguage())) {
			throw new SCIMException(BAD_REQUEST, "preferredLanguage:invalid");
		}

		if (user.getLocale() != null && !Utils.isLocaleValid(user.getLocale())) {
			throw new SCIMException(BAD_REQUEST, "locale:invalid");
		}
	}

	private void checkPassword(String dbPassword, HttpServletRequest request) {
		if (dbPassword != null && !dbPassword.equalsIgnoreCase(
				(String) request.getAttribute(BindingProvider.PASSWORD_PROPERTY))) {
			throw new SCIMException(BAD_REQUEST, "password:invalid");
		}
	}

	private String checkETag(HttpServletRequest request) {
		String eTag = Utils.trimOrNull(request.getHeader(HttpHeaders.IF_MATCH));

		if (eTag == null) {
			throw new SCIMException(BAD_REQUEST, null, HttpHeaders.IF_MATCH
					+ " header with ETag is requred for this request");
		}
		return eTag;
	}
}
