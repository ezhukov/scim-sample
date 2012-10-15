package eugene.zhukov.api;

import static javax.ws.rs.core.Response.Status.OK;
import static eugene.zhukov.SCIMFilter.API_VERSION;
import static eugene.zhukov.SCIMFilter.ENDPOINT_SERVICE_PROVIDER_CONFIGS;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import scim.schemas.v1.AuthenticationScheme;
import scim.schemas.v1.Response;
import scim.schemas.v1.ServiceProviderConfig;

@Path(API_VERSION + ENDPOINT_SERVICE_PROVIDER_CONFIGS)
public class ServiceProviderConfiguration {

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public javax.ws.rs.core.Response retrieve() {
		Response response = new Response();
		ServiceProviderConfig config = new ServiceProviderConfig();
		config.setDocumentationUrl("http://ee.dy.fi/help/scim.html");

		ServiceProviderConfig.AuthenticationSchemes authenticationSchemes
				= new ServiceProviderConfig.AuthenticationSchemes();
		AuthenticationScheme authenticationScheme = new AuthenticationScheme();
		authenticationScheme.setDescription("Authentication Scheme using the OAuth Bearer Token Standard");
		authenticationScheme.setDocumentationUrl("http://ee.dy.fi/help/oauth.html");
		authenticationScheme.setName("OAuth Bearer Token");
		authenticationScheme.setSpecUrl("http://tools.ietf.org/html/draft-ietf-oauth-v2-bearer-01");
		authenticationSchemes.getAuthenticationScheme().add(authenticationScheme);
		config.setAuthenticationSchemes(authenticationSchemes);

		ServiceProviderConfig.Bulk bulk = new ServiceProviderConfig.Bulk();
		bulk.setSupported(false);
		config.setBulk(bulk);

		ServiceProviderConfig.ChangePassword changePassword = new ServiceProviderConfig.ChangePassword();
		changePassword.setSupported(true);
		config.setChangePassword(changePassword);

		ServiceProviderConfig.Etag etag = new ServiceProviderConfig.Etag();
		etag.setSupported(false);
		config.setEtag(etag);

		ServiceProviderConfig.Filter filter = new ServiceProviderConfig.Filter();
		filter.setSupported(false);
		config.setFilter(filter);

		ServiceProviderConfig.Patch patch = new ServiceProviderConfig.Patch();
		patch.setSupported(false);
		config.setPatch(patch);

		ServiceProviderConfig.Sort sort = new ServiceProviderConfig.Sort();
		sort.setSupported(false);
		config.setSort(sort);

		ServiceProviderConfig.XmlDataFormat xmlDataFormat = new ServiceProviderConfig.XmlDataFormat();
		xmlDataFormat.setSupported(true);
		config.setXmlDataFormat(xmlDataFormat);

		response.setResource(config);
		return javax.ws.rs.core.Response.status(OK).entity(response).build();
	}
}
