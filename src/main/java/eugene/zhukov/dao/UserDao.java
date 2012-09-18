package eugene.zhukov.dao;

import scim.schemas.v1.User;

public interface UserDao {

	User persistUser(User user);
	
	User retrieveUser(long userId);
}
