package eugene.zhukov;

import static eugene.zhukov.EndpointConstants.ENDPOINT_ERRORS;
import static eugene.zhukov.EndpointConstants.PROVIDER_CONFIGS_PATH;
import static eugene.zhukov.EndpointConstants.SCHEMAS_USERS_PATH;
import static eugene.zhukov.EndpointConstants.SCHEMAS_GROUPS_PATH;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import javax.xml.ws.BindingProvider;

import eugene.zhukov.util.ConfigProperties;
import eugene.zhukov.util.SecureToken;
import eugene.zhukov.util.Utils;

public class SCIMFilter implements Filter {

	private static Logger logger = Logger.getLogger(SCIMFilter.class.getName());

	private static final String METHOD_OVERRIDE = "X-HTTP-Method-Override";
	private static final String BEARER_PREFIX = "Bearer ";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		req = new FilteredRequest(req, req.getHeader(METHOD_OVERRIDE));
		HttpServletResponse resp = (HttpServletResponse) response;

		ConfigProperties securityConfig = (ConfigProperties) ApplicationContextProvider
				.getContext().getBean(ApplicationContextProvider.CONFIG);

		if (!isAccessGranted(req, resp, securityConfig.getTokenValidityTime())) {
			req = new FilteredRequest(req, HttpMethod.GET);
			req.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, HttpServletResponse.SC_UNAUTHORIZED);
			req.getRequestDispatcher(ENDPOINT_ERRORS).forward(req, resp);
			return;
		}

		chain.doFilter(req, resp);
	}

	private static boolean isAccessGranted(
			HttpServletRequest request, HttpServletResponse response, long tokenValidityTime) {

		if (HttpMethod.GET.equalsIgnoreCase(request.getMethod())
				&& (PROVIDER_CONFIGS_PATH.equals(request.getPathInfo())
				|| SCHEMAS_USERS_PATH.equals(request.getPathInfo())
				|| SCHEMAS_GROUPS_PATH.equals(request.getPathInfo()))) {
			return true;
		}
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
			response.setHeader(HttpHeaders.WWW_AUTHENTICATE, BEARER_PREFIX);
			return false;
		}
		SecureToken token = Utils.decryptToken(
				authorizationHeader.substring(BEARER_PREFIX.length(), authorizationHeader.length()));

		if (token == null) {
			response.setHeader(HttpHeaders.WWW_AUTHENTICATE,
					BEARER_PREFIX.concat("error=\"invalid_token\""));
			return false;
		}
		long currentTimeMillis = System.currentTimeMillis();

		if (token.getTimestamp() > currentTimeMillis + tokenValidityTime) {
			logger.fine("Timestamp: " + token.getTimestamp() + ", current timestamp: " + currentTimeMillis);
			response.setHeader(HttpHeaders.WWW_AUTHENTICATE, BEARER_PREFIX.concat(
					"error=\"invalid_token\", error_description=\"Token has invalid timestamp\""));
			return false;
		}

		if (token.getTimestamp() + tokenValidityTime < currentTimeMillis) {
			logger.fine("Timestamp: " + token.getTimestamp() + ", current timestamp: " + currentTimeMillis);
			response.setHeader(HttpHeaders.WWW_AUTHENTICATE, BEARER_PREFIX.concat(
					"error=\"invalid_token\", error_description=\"Token expired\""));
			return false;
		}

		request.setAttribute(BindingProvider.PASSWORD_PROPERTY, token.getPassword());
		return true;
	}

	private static class FilteredRequest extends HttpServletRequestWrapper {

		private HttpServletRequest request;
		private String method;

		public FilteredRequest(HttpServletRequest request, String method) {
			super(request);
			this.request = request;
			this.method = method;
		}

		@Override
		public String getMethod() {
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
