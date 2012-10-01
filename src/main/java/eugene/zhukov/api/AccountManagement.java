package eugene.zhukov.api;

import static eugene.zhukov.ApplicationContextProvider.USER_DAO;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import scim.schemas.v1.Response;
import scim.schemas.v1.User;
import eugene.zhukov.ApplicationContextProvider;
import eugene.zhukov.SCIMException;
import eugene.zhukov.api.annotation.PATCH;
import eugene.zhukov.dao.UserDao;
import eugene.zhukov.util.Utils;

@Path("v1/Users")
public class AccountManagement {

	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public javax.ws.rs.core.Response create(User user) {
		UserDao dao = (UserDao) ApplicationContextProvider.getContext().getBean(USER_DAO);

		if (user.getPreferredLanguage() != null && !Utils.isLocaleValid(user.getPreferredLanguage())) {
			throw new SCIMException(BAD_REQUEST, "preferredLanguage:invalid");
		}

		if (user.getLocale() != null && !Utils.isLocaleValid(user.getLocale())) {
			throw new SCIMException(BAD_REQUEST, "locale:invalid");
		}
		UUID id = dao.persistUser(user);

		Response response = new Response();
		response.setResource(dao.retrieveUser(id));
		return javax.ws.rs.core.Response.status(CREATED).entity(response).build();
	}

	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public javax.ws.rs.core.Response retrieve(@Context HttpServletRequest request) {
		UserDao dao = (UserDao) ApplicationContextProvider.getContext().getBean(USER_DAO);
		try {
			User user = dao.retrieveUser(UUID.fromString(request.getPathInfo().substring(10)));
	
			Response response = new Response();
			response.setResource(user);
			return javax.ws.rs.core.Response.status(OK).entity(response).build();

		} catch (IllegalArgumentException e) {
			throw new SCIMException(BAD_REQUEST, "id:invalid");
		}
	}

	@PUT
	@Path("{id}")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public javax.ws.rs.core.Response update(User user) {
		UserDao dao = (UserDao) ApplicationContextProvider.getContext().getBean(USER_DAO);
		dao.updateUser(user);

		Response response = new Response();
		response.setResource(dao.retrieveUser(UUID.fromString(user.getId())));
		return javax.ws.rs.core.Response.status(OK).entity(response).build();
	}

	@PATCH
	@Path("{id}/password")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public javax.ws.rs.core.Response passwordChange(@Context HttpServletRequest request, User user) {
		return javax.ws.rs.core.Response.status(NO_CONTENT).build();
	}

	@DELETE
	@Path("{id}")
	public javax.ws.rs.core.Response remove() {
		return javax.ws.rs.core.Response.status(OK).build();
	}
}
