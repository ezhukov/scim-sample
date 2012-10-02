package eugene.zhukov.util;
public class S implements java.io.Externalizable {

	private static final long serialVersionUID = 48346345L;

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

	@Override
	public String toString() {
		return "SecureToken [password=" + password + ", timestamp=" + timestamp
				+ "]";
	}

	@Override
	public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException {
		out.writeObject(password);
//		out.writeUTF(password);
		out.writeLong(timestamp);
	}

	@Override
	public void readExternal(java.io.ObjectInput in) throws java.io.IOException, ClassNotFoundException {
		password = (String) in.readObject();
//		password = in.readUTF();
		timestamp = in.readLong();
	}
}
