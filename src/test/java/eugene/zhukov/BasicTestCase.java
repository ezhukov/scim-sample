package eugene.zhukov;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.grizzly.web.GrizzlyWebTestContainerFactory;

public class BasicTestCase extends JerseyTest {

	public BasicTestCase() {
		super(new WebAppDescriptor.Builder("eugene.zhukov")
				.contextPath("/scim")
				.addFilter(SCIMFilter.class, "filter")
				.build());

		new ApplicationContextProvider().setApplicationContext(
				new ClassPathXmlApplicationContext("springConfiguration.xml"));
	}

	@Override
	protected TestContainerFactory getTestContainerFactory() {
		return new GrizzlyWebTestContainerFactory();
	}
}