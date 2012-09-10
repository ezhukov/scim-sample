package eugene.zhukov;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextProvider implements ApplicationContextAware {
	
	public static final String POLICY_CONFIG = "policyConfig";
	
	private static ApplicationContext ctx;
	
	@Override
	public void setApplicationContext(ApplicationContext ctxt) {
		ApplicationContextProvider.ctx = ctxt;
	}
	
	/** 
	 * Get access to the Spring ApplicationContext from everywhere in the Application.
	 * 
	 * @return ApplicationContext
	 */
	public static ApplicationContext getContext() {
		return ctx;
	}
}
