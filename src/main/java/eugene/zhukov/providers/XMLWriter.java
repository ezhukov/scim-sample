package eugene.zhukov.providers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import scim.schemas.v1.Resource;
import scim.schemas.v1.Response;
import scim.schemas.v1.Response.Errors;
import scim.schemas.v1.Response.Resources;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

@Provider
@Produces({ MediaType.APPLICATION_XML })
public class XMLWriter implements ContextResolver<JAXBContext>, MessageBodyWriter<Response> {
	
	private final JAXBContext context;
	private final static String ENTITY_PACKAGE = "scim.schemas.v1";
	private final static String CORE_NAMESPACE = "urn:scim:schemas:core:1.0";
	private static final String ENTERPRISE_NAMESPACE = "urn:scim:schemas:extension:enterprise:1.0";
	private final static String ENTERPRISE_PREFIX = "enterprise";
	
	public XMLWriter() throws JAXBException {
		context = JAXBContext.newInstance(ENTITY_PACKAGE);
	}
	
	@Override
	public long getSize(Response target, Class<?> clazz, Type type, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}
	
	@Override
	public boolean isWriteable(Class<?> clazz, Type type, Annotation[] annotations, MediaType mediaType) {
		return mediaType.isCompatible(MediaType.APPLICATION_XML_TYPE);
	}
	
	@Override
	public void writeTo(Response target, Class<?> objectType, Type type, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> map, OutputStream outputStream) throws IOException, WebApplicationException {
		
		try {
			javax.xml.bind.Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FRAGMENT, true);
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NamespacePrefixMapper() {
				
				@Override
				public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
					return ENTERPRISE_NAMESPACE.equals(namespaceUri) ? ENTERPRISE_PREFIX : null;
				}
			});
			
			if (target.getErrors() != null && !target.getErrors().getError().isEmpty()) {
				marshaller.marshal(new JAXBElement<Errors>(
						new QName(CORE_NAMESPACE, "Errors"), Errors.class, target.getErrors()), outputStream);
				
			} else if (target.getResource() != null) {
				marshaller.marshal(new JAXBElement<Resource>(new QName(CORE_NAMESPACE,
						target.getResource().getClass().getSimpleName()), Resource.class, target.getResource()), outputStream);
				
			} else if (target.getResources() != null) {
				marshaller.marshal(new JAXBElement<Resources>(new QName(CORE_NAMESPACE,
						target.getResources().getClass().getSimpleName()), Resources.class, target.getResources()), outputStream);
			}

		} catch (JAXBException ex) {
			throw new WebApplicationException(ex);
		}
	}
	
	@Override
	public JAXBContext getContext(Class<?> objectType) {
		return objectType.getPackage().getName().contains(ENTITY_PACKAGE) ? context : null;
	}
}