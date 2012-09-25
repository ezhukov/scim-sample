package eugene.zhukov;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class SecureToken implements java.io.Externalizable {

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

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(password);
		out.writeLong(timestamp);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		password = (String) in.readObject();
		timestamp = in.readLong();
	}
}
