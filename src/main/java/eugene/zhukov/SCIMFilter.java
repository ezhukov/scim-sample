package eugene.zhukov;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import eugene.zhukov.util.SecureToken;
import eugene.zhukov.util.SecurityConfig;
import eugene.zhukov.util.Utils;

public class SCIMFilter implements Filter {

	public static final String API_VERSION = "/v1";
	public static final String ENDPOINT_SERVICE_PROVIDER_CONFIGS = "/ServiceProviderConfigs";
	public static final String ENDPOINT_USERS = "/Users";

	private static final String ACCEPT_HEADER = "Accept";
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String METHOD_OVERRIDE = "X-HTTP-Method-Override";
	private static final String BEARER_PREFIX = "Bearer ";
	private static final long TOKEN_VALIDITY_TIME_IN_MILLIS = 5 * 60000; // five minutes

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = new FilteredRequest((HttpServletRequest) request);
		HttpServletResponse resp = (HttpServletResponse) response;

		SecurityConfig securityConfig = (SecurityConfig) ApplicationContextProvider
				.getContext().getBean(ApplicationContextProvider.SECURITY_CONFIG);

		if (!isAccessGranted(req)) {
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			PrintWriter out = new PrintWriter(response.getWriter(), true);
			out.println(MediaType.APPLICATION_XML.equals(req.getHeader(ACCEPT_HEADER))
					? securityConfig.getUnauthorizedXML()
					: securityConfig.getUnauthorizedJSON());
			out.close();
			return;
		}

		chain.doFilter(req, resp);
	}
	
	private static boolean isAccessGranted(HttpServletRequest request) {

		if ("GET".equalsIgnoreCase(request.getMethod())
				&& API_VERSION.concat(ENDPOINT_SERVICE_PROVIDER_CONFIGS).equals(request.getPathInfo())) {
			return true;
		}
		String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

		if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
			return false;
		}
		SecureToken token = Utils.decryptToken(
				authorizationHeader.substring(BEARER_PREFIX.length(), authorizationHeader.length()));
		long currentTimeMillis = System.currentTimeMillis();

		if (token == null
				|| token.getTimestamp() > currentTimeMillis
				|| token.getTimestamp() + TOKEN_VALIDITY_TIME_IN_MILLIS < currentTimeMillis) {
			return false;
		}
		request.setAttribute("password", token.getPassword());
		return true;
	}

	private static class FilteredRequest extends HttpServletRequestWrapper {

		private HttpServletRequest request;

		public FilteredRequest(HttpServletRequest request) {
			super(request);
			this.request = request;
		}

		@Override
		public String getMethod() {
			String method = request.getHeader(METHOD_OVERRIDE);
			return method == null ? request.getMethod() : method;
		}
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
}
