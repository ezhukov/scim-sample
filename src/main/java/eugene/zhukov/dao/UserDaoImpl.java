package eugene.zhukov.dao;

import org.springframework.jdbc.core.JdbcTemplate;

public class UserDaoImpl {

	private JdbcTemplate jdbcTemplate;
	
	public void storeUser() {
		System.out.println("AAAAAAAAAAAAAAAAAA " + jdbcTemplate.queryForObject("select * from user;", String.class));
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
}
