package eugene.zhukov;

import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.grizzly.web.GrizzlyWebTestContainerFactory;

public class IntegrationJerseyTestCase extends JerseyTest {
	
	public IntegrationJerseyTestCase() {
		super(new WebAppDescriptor.Builder("eugene.zhukov")
				.contextPath("/scim")
//				.contextListenerClass(ApplicationInitMock.class)
//				.addFilter(SCIMFilter.class, "filter")
				.build());

		new ApplicationContextProvider().setApplicationContext(
				new FileSystemXmlApplicationContext("file:/home/eugene/workspace/scim-sample/src/main/webapp/WEB-INF/applicationContext.xml"));
	}
	
	@Override
	protected TestContainerFactory getTestContainerFactory() {
		return new GrizzlyWebTestContainerFactory();
	}
	
//	public static class ApplicationInitMock implements ServletContextListener {
//
//		@Override
//		public void contextInitialized(ServletContextEvent arg0) {
//
//		}
//		
//		@Override
//		public void contextDestroyed(ServletContextEvent arg0) {
//
//		}
//	}
}