package eugene.zhukov;

public class SecureToken implements java.io.Serializable {

	private static final long serialVersionUID = 4812224717220614882L;

	private String password;
	
	private long timestamp;

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
