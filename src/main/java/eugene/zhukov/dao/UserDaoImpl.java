package eugene.zhukov.dao;

import static eugene.zhukov.EndpointConstants.API_VERSION;
import static eugene.zhukov.EndpointConstants.ENDPOINT_USERS;
import static eugene.zhukov.EndpointConstants.HOST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.PRECONDITION_FAILED;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import scim.schemas.v1.Address;
import scim.schemas.v1.Meta;
import scim.schemas.v1.MultiValuedAttribute;
import scim.schemas.v1.Name;
import scim.schemas.v1.User;
import scim.schemas.v1.User.Addresses;
import scim.schemas.v1.User.Emails;
import scim.schemas.v1.User.Entitlements;
import scim.schemas.v1.User.Groups;
import scim.schemas.v1.User.Ims;
import scim.schemas.v1.User.PhoneNumbers;
import scim.schemas.v1.User.Photos;
import scim.schemas.v1.User.Roles;
import scim.schemas.v1.User.X509Certificates;
import eugene.zhukov.SCIMException;
import eugene.zhukov.util.Utils;

public class UserDaoImpl implements UserDao {

	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	@Transactional
	public UUID persistUser(User user) {
		java.util.Date dateTime = new java.util.Date();
		StringBuilder sql = new StringBuilder();
		Name name = user.getName() == null ? new Name() : user.getName();
		UUID userId = UUID.randomUUID();

		jdbcTemplate.update(sql.append("insert into users (")
				.append("id,")
				.append("username,")
				.append("formattedName,")
				.append("familyName,")
				.append("givenName,")
				.append("middleName,")
				.append("honorificPrefix,")
				.append("honorificSuffix,")
				.append("displayName,")
				.append("nickname,")
				.append("profileURL,")
				.append("title,")
				.append("userType,")
				.append("preferredLanguage,")
				.append("locale,")
				.append("timezone,")
				.append("active,")
				.append("password,")
				.append("created,")
				.append("lastModified,")
				.append("location,")
				.append("version,")
				.append("gender")
				.append(") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)").toString(),
				userId,
				Utils.trimOrNull(user.getUserName()),
				Utils.trimOrNull(name.getFormatted()),
				Utils.trimOrNull(name.getFamilyName()),
				Utils.trimOrNull(name.getGivenName()),
				Utils.trimOrNull(name.getMiddleName()),
				Utils.trimOrNull(name.getHonorificPrefix()),
				Utils.trimOrNull(name.getHonorificSuffix()),
				Utils.trimOrNull(user.getDisplayName()),
				Utils.trimOrNull(user.getNickName()),
				user.getProfileUrl(),
				user.getTitle(),
				user.getUserType(),
				user.getPreferredLanguage(),
				user.getLocale(),
				user.getTimezone(),
				true,
				Utils.hashPassword(user.getPassword()),
				dateTime,
				dateTime,
				HOST + API_VERSION + ENDPOINT_USERS + "/" + userId,
				Utils.createVersion(dateTime),
				user.getGender());

		insertAttrs(user, userId);

		return userId;
	}

	@Override
	@Transactional(readOnly = true)
	public String retrievePasswd(UUID userId) {
		try {
			return jdbcTemplate.queryForObject("select password from users where id = ?", String.class, userId);

		} catch (EmptyResultDataAccessException e) {
			throw new SCIMException(NOT_FOUND, null, "Resource " + userId + " not found");
		}
	}

	@Override
	@Transactional
	public String updatePasswd(UUID userId, String password, String eTag) {

		if (!eTag.equalsIgnoreCase(retrieveETag(userId))) {
			throw new SCIMException(PRECONDITION_FAILED, null,
					"Failed to update as Resource " + userId
					+ " changed on the server last retrieved");
		}
		java.util.Date dateTime = new java.util.Date();
		String newEtag = Utils.toSHA1(dateTime);
		jdbcTemplate.update(
				"update users set (password, lastModified, version) = (?,?,?) where id = ?",
				Utils.hashPassword(password), dateTime, "\"" + newEtag + "\"", userId);
		return newEtag;
	}

	@Override
	@Transactional(readOnly = true)
	public User retrieveUser(UUID userId) {
		User user = jdbcTemplate.queryForObject("select * from users where id =?", new RowMapper<User>() {

			@Override
			public User mapRow(ResultSet resultSet, int arg1) throws SQLException {
				User user = new User();
				user.setId(resultSet.getString("id"));
				user.setUserName(resultSet.getString("username"));

				Name name = new Name();
				name.setFormatted(resultSet.getString("formattedName"));
				name.setFamilyName(resultSet.getString("familyName"));
				name.setGivenName(resultSet.getString("givenName"));
				name.setHonorificPrefix(resultSet.getString("honorificPrefix"));
				name.setHonorificSuffix(resultSet.getString("honorificSuffix"));
				name.setMiddleName(resultSet.getString("middleName"));

				if (name.getFormatted() != null
						|| name.getFamilyName() != null
						|| name.getGivenName() != null
						|| name.getHonorificPrefix() != null
						|| name.getHonorificSuffix() != null
						|| name.getMiddleName() != null) {
					user.setName(name);
				}
				user.setDisplayName(resultSet.getString("displayName"));
				user.setNickName(resultSet.getString("nickname"));
				user.setProfileUrl(resultSet.getString("profileURL"));
				user.setTitle(resultSet.getString("title"));
				user.setUserType(resultSet.getString("userType"));
				user.setPreferredLanguage(resultSet.getString("preferredLanguage"));
				user.setLocale(resultSet.getString("locale"));
				user.setTimezone(resultSet.getString("timezone"));
				user.setActive(resultSet.getBoolean("active"));
				user.setGender(resultSet.getString("gender"));

				Meta meta = new Meta();
				meta.setCreated(Utils.asXMLGregorianCalendar(resultSet.getTimestamp("created")));
				meta.setLastModified(Utils.asXMLGregorianCalendar(resultSet.getTimestamp("lastModified")));
				meta.setLocation(resultSet.getString("location"));
				meta.setVersion(resultSet.getString("version"));
				user.setMeta(meta);

				return user;
			}

		}, userId);

		java.util.List<MultiValuedAttribute> attrs = retrieveMultiValuedAttrs("emails", userId);

		if (!attrs.isEmpty()) {
			Emails emails = new Emails();
			emails.getEmail().addAll(attrs);
			user.setEmails(emails);
		}

		attrs = retrieveMultiValuedAttrs("phoneNumbers", userId);

		if (!attrs.isEmpty()) {
			PhoneNumbers phoneNumbers = new PhoneNumbers();
			phoneNumbers.getPhoneNumber().addAll(attrs);
			user.setPhoneNumbers(phoneNumbers);
		}

		attrs = retrieveMultiValuedAttrs("ims", userId);

		if (!attrs.isEmpty()) {
			Ims ims = new Ims();
			ims.getIm().addAll(attrs);
			user.setIms(ims);
		}

		attrs = retrieveMultiValuedAttrs("photos", userId);

		if (!attrs.isEmpty()) {
			Photos photos = new Photos();
			photos.getPhoto().addAll(attrs);
			user.setPhotos(photos);
		}

		attrs = retrieveMultiValuedAttrs("groups", userId);

		if (!attrs.isEmpty()) {
			Groups proups = new Groups();
			proups.getGroup().addAll(attrs);
			user.setGroups(proups);
		}

		attrs = retrieveMultiValuedAttrs("entitlements", userId);

		if (!attrs.isEmpty()) {
			Entitlements entitlements = new Entitlements();
			entitlements.getEntitlement().addAll(attrs);
			user.setEntitlements(entitlements);
		}

		attrs = retrieveMultiValuedAttrs("roles", userId);

		if (!attrs.isEmpty()) {
			Roles roles = new Roles();
			roles.getRole().addAll(attrs);
			user.setRoles(roles);
		}

		attrs = retrieveMultiValuedAttrs("x509Certificates", userId);

		if (!attrs.isEmpty()) {
			X509Certificates x509Certificates = new X509Certificates();
			x509Certificates.getX509Certificate().addAll(attrs);
			user.setX509Certificates(x509Certificates);
		}

		java.util.List<Address> addressList = retrieveAddresses(userId);

		if (!addressList.isEmpty()) {
			Addresses addresses = new Addresses();
			addresses.getAddress().addAll(addressList);
			user.setAddresses(addresses);
		}

		return user;
	}

	@Override
	@Transactional
	public void updateUser(User user, String eTag) {
		UUID id = UUID.fromString(user.getId());

		if (!eTag.equalsIgnoreCase(retrieveETag(id))) {
			throw new SCIMException(PRECONDITION_FAILED, null,
					"Failed to update as Resource " + user.getId()
					+ " changed on the server last retrieved");
		}
		java.util.Date dateTime = new java.util.Date();
		Name name = user.getName() == null ? new Name() : user.getName();
		StringBuilder sql = new StringBuilder();

		jdbcTemplate.update(
				sql.append("update users set (")
				.append("username,")
				.append("formattedName,")
				.append("familyName,")
				.append("givenName,")
				.append("middleName,")
				.append("honorificPrefix,")
				.append("honorificSuffix,")
				.append("displayName,")
				.append("nickname,")
				.append("profileURL,")
				.append("title,")
				.append("userType,")
				.append("preferredLanguage,")
				.append("locale,")
				.append("timezone,")
				.append("active,")
				.append("lastModified,")
				.append("version,")
				.append("gender")
				.append(") = (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) where id = ?")
				.toString(),
				Utils.trimOrNull(user.getUserName()),
				Utils.trimOrNull(name.getFormatted()),
				Utils.trimOrNull(name.getFamilyName()),
				Utils.trimOrNull(name.getGivenName()),
				Utils.trimOrNull(name.getMiddleName()),
				Utils.trimOrNull(name.getHonorificPrefix()),
				Utils.trimOrNull(name.getHonorificSuffix()),
				Utils.trimOrNull(user.getDisplayName()),
				Utils.trimOrNull(user.getNickName()),
				user.getProfileUrl(),
				user.getTitle(),
				user.getUserType(),
				user.getPreferredLanguage(),
				user.getLocale(),
				user.getTimezone(),
				user.isActive(),
				dateTime,
				Utils.createVersion(dateTime),
				user.getGender(),
				id);

		deleteFromTable(id,
				"emails", "phoneNumbers", "ims", "photos", "groups",
				"entitlements", "roles", "x509Certificates", "addresses");

		insertAttrs(user, id);
	}

	@Override
	@Transactional
	public void deleteUser(UUID userId) {
		jdbcTemplate.update("delete from users where id = ?", userId);
		deleteFromTable(userId,
				"emails", "phoneNumbers", "ims", "photos", "groups",
				"entitlements", "roles", "x509Certificates", "addresses");
	}

	@Override
	@Transactional(readOnly = true)
	public String retrieveETag(UUID userId) {
		return jdbcTemplate.queryForObject("select version from users where id = ?", String.class, userId);
	}

	private void insertAttrs(User user, UUID userId) {

		if (user.getEmails() != null) {
			insertMultiValuedAttrs(user.getEmails().getEmail(), "emails", userId);
		}

		if (user.getPhoneNumbers() != null) {
			insertMultiValuedAttrs(user.getPhoneNumbers().getPhoneNumber(), "phoneNumbers", userId);
		}

		if (user.getIms() != null) {
			insertMultiValuedAttrs(user.getIms().getIm(), "ims", userId);
		}

		if (user.getPhotos() != null) {
			insertMultiValuedAttrs(user.getPhotos().getPhoto(), "photos", userId);
		}

		if (user.getGroups() != null) {
			insertMultiValuedAttrs(user.getGroups().getGroup(), "groups", userId);
		}

		if (user.getEntitlements() != null) {
			insertMultiValuedAttrs(user.getEntitlements().getEntitlement(), "entitlements", userId);
		}

		if (user.getRoles() != null) {
			insertMultiValuedAttrs(user.getRoles().getRole(), "roles", userId);
		}

		if (user.getX509Certificates() != null) {
			insertMultiValuedAttrs(user.getX509Certificates().getX509Certificate(), "x509Certificates", userId);
		}

		if (user.getAddresses() != null) {
			insertAddresses(user.getAddresses().getAddress(), userId);
		}
	}

	private void deleteFromTable(UUID userId, String...tables) {

		for (String table : tables) {
			jdbcTemplate.update("delete from ".concat(table).concat(" where userId = ?"), userId);
		}
	}

	private java.util.List<MultiValuedAttribute> retrieveMultiValuedAttrs(String table, UUID userId) {
		return jdbcTemplate.query("select * from " + table + " where userId =?", new RowMapper<MultiValuedAttribute>() {

			@Override
			public MultiValuedAttribute mapRow(ResultSet resultSet, int arg1) throws SQLException {
				MultiValuedAttribute attr = new MultiValuedAttribute();
				attr.setDisplay(resultSet.getString("display"));
				attr.setOperation(resultSet.getString("operation"));
				attr.setPrimary(resultSet.getBoolean("isPrimary"));
				attr.setType(resultSet.getString("type"));
				attr.setValue(resultSet.getString("value"));
				return attr;
			}

		}, userId);
	}
	
	private java.util.List<Address> retrieveAddresses(UUID userId) {
		return jdbcTemplate.query("select * from addresses where userId =?", new RowMapper<Address>() {

			@Override
			public Address mapRow(ResultSet resultSet, int arg1) throws SQLException {
				Address attr = new Address();
				attr.setDisplay(resultSet.getString("display"));
				attr.setOperation(resultSet.getString("operation"));
				attr.setPrimary(resultSet.getBoolean("isPrimary"));
				attr.setType(resultSet.getString("type"));
				attr.setValue(resultSet.getString("value"));
				attr.setCountry(resultSet.getString("country"));
				attr.setFormatted(resultSet.getString("formatted"));
				attr.setLocality(resultSet.getString("locality"));
				attr.setPostalCode(resultSet.getString("postalCode"));
				attr.setRegion(resultSet.getString("region"));
				attr.setStreetAddress(resultSet.getString("streetAddress"));
				return attr;
			}
			
		}, userId);
	}

	private void insertMultiValuedAttrs(java.util.List<MultiValuedAttribute> values, String table, UUID userId) {
		StringBuilder sql = null;

		for (MultiValuedAttribute attr : values) {
			sql = new StringBuilder();
			jdbcTemplate.update(sql.append("insert into ").append(table).append(" (")
					.append("value,")
					.append("display,")
					.append("isPrimary,")
					.append("type,")
					.append("operation,")
					.append("userId")
					.append(") values (?,?,?,?,?,?)").toString(),
					attr.getValue(),
					attr.getDisplay(),
					attr.isPrimary(),
					attr.getType(),
					attr.getOperation(),
					userId);
		}
	}

	private void insertAddresses(java.util.List<Address> addresses, UUID userId) {
		StringBuilder sql = null;

		for (Address address : addresses) {
			sql = new StringBuilder();
			jdbcTemplate.update(sql.append("insert into addresses (")
					.append("value,")
					.append("display,")
					.append("isPrimary,")
					.append("type,")
					.append("operation,")
					.append("formatted,")
					.append("streetAddress,")
					.append("locality,")
					.append("region,")
					.append("postalCode,")
					.append("country,")
					.append("userId")
					.append(") values (?,?,?,?,?,?,?,?,?,?,?,?)").toString(),
					address.getValue(),
					address.getDisplay(),
					address.isPrimary(),
					address.getType(),
					address.getOperation(),
					address.getFormatted(),
					address.getStreetAddress(),
					address.getLocality(),
					address.getRegion(),
					address.getPostalCode(),
					address.getCountry(),
					userId);
		}
	}
}
