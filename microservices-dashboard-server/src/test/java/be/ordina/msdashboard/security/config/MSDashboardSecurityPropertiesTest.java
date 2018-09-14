package be.ordina.msdashboard.security.config;

import org.junit.After;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Andreas Evers
 */
public class MSDashboardSecurityPropertiesTest {

    private MSDashboardSecurityProperties properties = new MSDashboardSecurityProperties();
    private Map<String, List<String>> strategies = new HashMap<>();

    @After
    public void onTearDown() {
        properties = new MSDashboardSecurityProperties();
        strategies = new HashMap<>();
    }

    @Test
    public void returnsStrategiesByAggregator() throws Exception {
        fillUpStrategy("key1", "value1");
        fillUpStrategy("key2", "value2");
        fillUpStrategy("key3", "value3");
        fillUpStrategy("key4", "");
        fillUpStrategy("key5", "value4");
        injectStrategies();
        Map<String, String> strategiesByAggregator = properties.getStrategiesByAggregator();
        assertThat(strategiesByAggregator).containsEntry("value1", "key1");
        assertThat(strategiesByAggregator).containsEntry("value2", "key2");
        assertThat(strategiesByAggregator).containsEntry("value3", "key3");
        assertThat(strategiesByAggregator).containsEntry("", "key4");
        assertThat(strategiesByAggregator).containsEntry("value4", "key5");
    }

    @Test(expected = IllegalStateException.class)
    public void throwsExceptionOnDuplicateValue() throws Exception {
        fillUpStrategy("key1", "value1");
        fillUpStrategy("key2", "value1");
        injectStrategies();
        properties.getStrategiesByAggregator();
    }

    @Test
    public void returnsEmtpyStrategiesByAggregator() throws Exception {
        injectStrategies();
        Map<String, String> strategiesByAggregator = properties.getStrategiesByAggregator();
        assertThat(strategiesByAggregator).isEmpty();
    }

    private void fillUpStrategy(String key, String... values) {
        strategies.put(key, Arrays.asList(values));
    }

    private void injectStrategies() throws NoSuchFieldException, IllegalAccessException {
        Field f = properties.getClass().getDeclaredField("strategies");
        f.setAccessible(true);
        f.set(properties, strategies);
    }

}
