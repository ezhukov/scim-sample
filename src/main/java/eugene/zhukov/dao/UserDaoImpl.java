package eugene.zhukov.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import scim.schemas.v1.Address;
import scim.schemas.v1.Meta;
import scim.schemas.v1.MultiValuedAttribute;
import scim.schemas.v1.Name;
import scim.schemas.v1.User;
import scim.schemas.v1.User.Emails;
import eugene.zhukov.util.XMLGregorianCalendarConverter;

public class UserDaoImpl implements UserDao {

	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	@Transactional
	public User persistUser(User user) {
		java.util.Date dateTime = new java.util.Date();
		StringBuilder sql = new StringBuilder();
		Name name = user.getName() == null ? new Name() : user.getName();

		jdbcTemplate.update(sql.append("insert into users (")
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
				.append(") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)").toString(),
				user.getUserName(),
				name.getFormatted(),
				name.getFamilyName(),
				name.getGivenName(),
				name.getMiddleName(),
				name.getHonorificPrefix(),
				name.getHonorificSuffix(),
				user.getDisplayName(),
				user.getNickName(),
				user.getProfileUrl(),
				user.getTitle(),
				user.getUserType(),
				user.getPreferredLanguage(),
				user.getLocale(),
				user.getTimezone(),
				user.isActive(),
				user.getPassword(),
				dateTime,
				dateTime,
				"{server}/Users/@me",
				"v1",
				user.getGender());
		
		// TODO need to generate some random IDs instead of relaying on database generated values
		long userId = jdbcTemplate.queryForLong("select id from users where username = ?", user.getUserName());

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

		return retrieveUser(user.getUserName());
	}

	@Override
	public User retrieveUser(String username) {
		User user = jdbcTemplate.queryForObject("select * from users where username =?", new RowMapper<User>() {

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
				user.setName(name);
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
				meta.setCreated(XMLGregorianCalendarConverter.asXMLGregorianCalendar(resultSet.getDate("created")));
				meta.setLastModified(XMLGregorianCalendarConverter.asXMLGregorianCalendar(resultSet.getDate("lastModified")));
				meta.setLocation(resultSet.getString("location"));
				meta.setVersion(resultSet.getString("version"));
				user.setMeta(meta);

				return user;
			}

		}, username);

		Emails emails = new Emails();
		emails.getEmail().addAll(retrieveMultiValuedAttrs("emails", Long.parseLong(user.getId())));
		user.setEmails(emails);

		//TODO fetch addresses, phones, ims etc.
		
		return user;
	}

	private java.util.List<MultiValuedAttribute> retrieveMultiValuedAttrs(String table, long userId) {
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

	private void insertMultiValuedAttrs(java.util.List<MultiValuedAttribute> values, String table, long userId) {
		StringBuilder sql = null;

		for (MultiValuedAttribute email : values) {
			sql = new StringBuilder();
			jdbcTemplate.update(sql.append("insert into ").append(table).append(" (")
					.append("value,")
					.append("display,")
					.append("isPrimary,")
					.append("type,")
					.append("operation,")
					.append("userId")
					.append(") values (?,?,?,?,?,?)").toString(),
					email.getValue(),
					email.getDisplay(),
					email.isPrimary(),
					email.getType(),
					email.getOperation(),
					userId);
		}
	}
	
	private void insertAddresses(java.util.List<Address> addresses, long userId) {
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
