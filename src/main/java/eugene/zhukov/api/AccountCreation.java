package eugene.zhukov.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import scim.schemas.v1.Response;
import scim.schemas.v1.User;
import eugene.zhukov.ApplicationContextProvider;
import eugene.zhukov.dao.UserDaoImpl;

@Path("v1/Users")
public class AccountCreation {
	
	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public javax.ws.rs.core.Response create(User user) {

		UserDaoImpl dao = (UserDaoImpl) ApplicationContextProvider.getContext().getBean("userDao");
		dao.storeUser();
		Response response = new Response();
		response.setResource(user);
		return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.CREATED).entity(response).build();
	}
}