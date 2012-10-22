package eugene.zhukov.api;

import static eugene.zhukov.ApplicationContextProvider.USER_DAO;
import static eugene.zhukov.SCIMFilter.API_VERSION;
import static eugene.zhukov.SCIMFilter.ENDPOINT_USERS;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.NOT_MODIFIED;

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
import javax.xml.ws.BindingProvider;

import scim.schemas.v1.Response;
import scim.schemas.v1.User;
import eugene.zhukov.ApplicationContextProvider;
import eugene.zhukov.SCIMException;
import eugene.zhukov.api.annotation.PATCH;
import eugene.zhukov.dao.UserDao;
import eugene.zhukov.util.Utils;

@Path(API_VERSION + ENDPOINT_USERS)
public class UsersEndpoint {

	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public javax.ws.rs.core.Response create(User user) {
		UserDao dao = (UserDao) ApplicationContextProvider.getContext().getBean(USER_DAO);
		validateUser(user);
		UUID id = dao.persistUser(user);

		Response response = new Response();
		User newUser = dao.retrieveUser(id);
		response.setResource(newUser);
		return javax.ws.rs.core.Response.status(CREATED)
				.entity(response).tag(stripQuotes(newUser.getMeta().getVersion())).build();
	}

	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public javax.ws.rs.core.Response retrieve(
			@PathParam("id") UUID id, @Context HttpServletRequest request) {
		UserDao dao = (UserDao) ApplicationContextProvider.getContext().getBean(USER_DAO);
		checkPassword(dao.retrievePasswd(id), request);
		String eTag = Utils.trimOrNull(request.getHeader(HttpHeaders.IF_NONE_MATCH));

		if (eTag != null && eTag.equalsIgnoreCase(dao.retrieveETag(id))) {
			return javax.ws.rs.core.Response.status(NOT_MODIFIED).build();
		}

		Response response = new Response();
		User user = dao.retrieveUser(id);
		response.setResource(user);
		return javax.ws.rs.core.Response.status(OK)
				.entity(response).tag(stripQuotes(user.getMeta().getVersion())).build();
	}

	@PUT
	@Path("{id}")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public javax.ws.rs.core.Response update(
			User user, @PathParam("id") UUID id, @Context HttpServletRequest request) {
		UserDao dao = (UserDao) ApplicationContextProvider.getContext().getBean(USER_DAO);
		checkPassword(dao.retrievePasswd(id), request);
		validateUser(user);
		user.setId(id.toString());
		dao.updateUser(user, checkETag(request));

		Response response = new Response();
		User newUser = dao.retrieveUser(id);
		response.setResource(newUser);
		return javax.ws.rs.core.Response.status(OK)
				.entity(response).tag(stripQuotes(newUser.getMeta().getVersion())).build();
	}

	@PATCH
	@Path("{id}/password")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public javax.ws.rs.core.Response passwordChange(
			@PathParam("id") UUID id, User user, @Context HttpServletRequest request) {
		UserDao dao = (UserDao) ApplicationContextProvider.getContext().getBean(USER_DAO);
		checkPassword(dao.retrievePasswd(id), request);
		String eTag = dao.updatePasswd(id, Utils.trimOrNull(user.getPassword()), checkETag(request));

		return javax.ws.rs.core.Response.status(NO_CONTENT).tag(eTag).build();
	}

	@DELETE
	@Path("{id}")
	public javax.ws.rs.core.Response remove(@PathParam("id") UUID id, @Context HttpServletRequest request) {
		UserDao dao = (UserDao) ApplicationContextProvider.getContext().getBean(USER_DAO);
		checkPassword(dao.retrievePasswd(id), request);
		dao.deleteUser(id);

		return javax.ws.rs.core.Response.status(OK).build();
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

	private static String stripQuotes(String version) {
		return version.substring(1, version.length() - 1);
	}
}
