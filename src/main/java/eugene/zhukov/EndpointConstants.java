package eugene.zhukov;

import eugene.zhukov.util.ConfigProperties;

public class EndpointConstants {

	public static final String API_VERSION = "/v1";
	public static final String ENDPOINT_SERVICE_PROVIDER_CONFIGS = "/ServiceProviderConfigs";
	public static final String ENDPOINT_USERS = "/Users";
	public static final String ENDPOINT_GROUPS = "/Groups";
	public static final String ENDPOINT_SCHEMAS = "/Schemas";
	public static final String ENDPOINT_ERRORS = "/Errors";

	public static final String HOST = ((ConfigProperties) ApplicationContextProvider
			.getContext().getBean(ApplicationContextProvider.CONFIG)).getHost();
}
