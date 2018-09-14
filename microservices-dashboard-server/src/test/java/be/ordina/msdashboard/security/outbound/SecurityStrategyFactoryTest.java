package be.ordina.msdashboard.security.outbound;

import be.ordina.msdashboard.security.config.MSDashboardSecurityProperties;
import be.ordina.msdashboard.security.config.StrategyBeanProvider;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Temporarily ignored just for synchronising work
 *
 * @author Andreas Evers
 */
@Ignore
@RunWith(MockitoJUnitRunner.class)
public class SecurityStrategyFactoryTest {

    @Mock
    private MSDashboardSecurityProperties msDashboardSecurityProperties;

    @Before
    public void onSetup() {
        Map<String, String> strats = new HashMap<>();
        strats.put("aggr1", "a");
        strats.put("aggr2", "b");
        strats.put("aggr3", "c");
        when(msDashboardSecurityProperties.getStrategiesByAggregator()).thenReturn(strats);
    }

    @Test
    public void getStrategy() throws Exception {
        AnnotationConfigApplicationContext context = load(SecurityStrategyFactoryTest.Config.class);
        SecurityStrategyFactory factory = new SecurityStrategyFactory(context, msDashboardSecurityProperties);
        assertThat(factory.getStrategy("aggr1").getType()).isEqualTo("a");
    }

    @Test
    public void getDefaultStrategy() throws Exception {
        AnnotationConfigApplicationContext context = load(SecurityStrategyFactoryTest.Config.class);
        SecurityStrategyFactory factory = new SecurityStrategyFactory(context, msDashboardSecurityProperties);
        assertThat(factory.getStrategy("unexisting").getType()).isEqualTo("default");
    }

    @Test
    public void neither() throws Exception {
        AnnotationConfigApplicationContext context = load(SecurityStrategyFactoryTest.Config.class);
        assertThat(context.containsBean("myBean")).isFalse();
        context.close();
    }

    @Test
    public void propertiesStyledProperty() throws Exception {
        AnnotationConfigApplicationContext context = load(SecurityStrategyFactoryTest.Config.class, "msdashboard.security.strategies.forward-inbound-auth-header:mappings,pacts");
        assertThat(context.containsBean("myBean")).isTrue();
        context.close();
    }

    @Test
    public void yamlStyledProperty() throws Exception {
        AnnotationConfigApplicationContext context = load(SecurityStrategyFactoryTest.Config.class, "msdashboard.security.strategies.forward-inbound-auth-header[0]:mappings");
        assertThat(context.containsBean("myBean")).isTrue();
        context.close();
    }

    @Configuration
    public static class Config {

        @Bean
        public MSDashboardSecurityProperties msDashboardSecurityProperties() {
            return new MSDashboardSecurityProperties();
        }

        @Bean
        public StrategyBeanProvider provider1() {
            return new StrategyBeanProvider() {
                @Override
                public String getType() {
                    return "a";
                }

                @Override
                public OutboundSecurityObjectProvider getOutboundSecurityObjectProvider() {
                    return null;
                }

                @Override
                public OutboundSecurityStrategy getOutboundSecurityStrategy() {
                    return null;
                }
            };
        }

    }

    private AnnotationConfigApplicationContext load(Class<?> config, String... env) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        EnvironmentTestUtils.addEnvironment(context, env);
        context.register(config);
        context.refresh();
        return context;
    }
}
