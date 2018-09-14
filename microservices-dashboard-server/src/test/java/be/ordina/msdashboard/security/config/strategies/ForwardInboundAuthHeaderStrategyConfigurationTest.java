package be.ordina.msdashboard.security.config.strategies;

import org.junit.Test;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Andreas Evers
 */
public class ForwardInboundAuthHeaderStrategyConfigurationTest {

	@Test
	public void neither() throws Exception {

		final AnnotationConfigApplicationContext context = load(Config.class);
		assertThat(context.containsBean("myBean")).isFalse();
		context.close();
	}

	@Test
	public void propertiesStyledProperty() throws Exception {

		final AnnotationConfigApplicationContext context = load(Config.class, "msdashboard.security.strategies.forward-inbound-auth-header:mappings,pacts");
		assertThat(context.containsBean("myBean")).isTrue();
		context.close();
	}

	@Test
	public void yamlStyledProperty() throws Exception {

		final AnnotationConfigApplicationContext context = load(Config.class, "msdashboard.security.strategies.forward-inbound-auth-header[0]:mappings");
		assertThat(context.containsBean("myBean")).isTrue();
		context.close();
	}

	@Configuration
	// @Conditional(Condition.class)
	@ConditionalOnProperty({
			"msdashboard.security.strategies.forward-inbound-auth-header",
			"msdashboard.security.strategies.forward-inbound-auth-header[0]"
	})
	public static class Config {

		@Bean
		public String myBean() {

			return "myBean";
		}

	}

	private AnnotationConfigApplicationContext load(final Class<?> config, final String... env) {

		final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		EnvironmentTestUtils.addEnvironment(context, env);
		context.register(config);
		context.refresh();
		return context;
	}
}
