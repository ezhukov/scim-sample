package eugene.zhukov.dao;

import java.util.UUID;

import scim.schemas.v1.User;

public interface UserDao {

	UUID persistUser(User user);

	User retrieveUser(UUID userId);

	void updateUser(User user, String eTag);

	void deleteUser(UUID userId);

	String retrievePasswd(UUID userId);

	String updatePasswd(UUID userId, String password, String eTag);

	String retrieveETag(UUID userId);
}
