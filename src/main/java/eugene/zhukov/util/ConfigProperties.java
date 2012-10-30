package eugene.zhukov.util;

import org.springframework.core.io.Resource;

public class ConfigProperties {

	private Resource privateKey;
	private long tokenValidityTime;
	private String host;

	public Resource getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(Resource privateKey) {
		this.privateKey = privateKey;
	}
	public long getTokenValidityTime() {
		return tokenValidityTime;
	}
	public void setTokenValidityTime(long tokenValidityTime) {
		this.tokenValidityTime = tokenValidityTime;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
}
