package eugene.zhukov.api;

import static eugene.zhukov.ApplicationContextProvider.USER_DAO;
import static javax.ws.rs.core.Response.Status.CREATED;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import scim.schemas.v1.Response;
import scim.schemas.v1.User;
import eugene.zhukov.ApplicationContextProvider;
import eugene.zhukov.dao.UserDao;

@Path("v1/Users")
public class AccountCreation {
	
	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public javax.ws.rs.core.Response create(User user) {
		UserDao dao = (UserDao) ApplicationContextProvider.getContext().getBean(USER_DAO);
		User persisted = dao.persistUser(user);

		Response response = new Response();
		response.setResource(persisted);
		return javax.ws.rs.core.Response.status(CREATED).entity(response).build();
	}
}