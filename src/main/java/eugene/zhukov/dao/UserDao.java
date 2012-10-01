package eugene.zhukov.dao;

import java.util.UUID;

import scim.schemas.v1.User;

public interface UserDao {

	UUID persistUser(User user);
	
	User retrieveUser(UUID userId);

	void updateUser(User user);

	boolean checkUserExists(UUID userId);
}
