package eugene.zhukov.dao;

import java.util.UUID;

import scim.schemas.v1.User;

public interface UserDao {

	User persistUser(User user);
	
	User retrieveUser(UUID userId);
}
