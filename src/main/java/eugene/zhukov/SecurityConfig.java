package eugene.zhukov;

public class SecurityConfig {

	private String unauthorizedXML;
	private String unauthorizedJSON;

	public String getUnauthorizedXML() {
		return unauthorizedXML;
	}
	public void setUnauthorizedXML(String unauthorizedXML) {
		this.unauthorizedXML = unauthorizedXML;
	}
	public String getUnauthorizedJSON() {
		return unauthorizedJSON;
	}
	public void setUnauthorizedJSON(String unauthorizedJSON) {
		this.unauthorizedJSON = unauthorizedJSON;
	}

}
