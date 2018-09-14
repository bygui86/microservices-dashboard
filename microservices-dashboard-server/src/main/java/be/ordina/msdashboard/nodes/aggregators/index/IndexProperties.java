package be.ordina.msdashboard.nodes.aggregators.index;

import be.ordina.msdashboard.security.outbound.SecurityStrategyFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static be.ordina.msdashboard.nodes.aggregators.Constants.*;

/**
 * @author Andreas Evers
 * @author Kevin van Houtte
 */
public class IndexProperties {

	private Map<String, String> requestHeaders = new HashMap<>();
	private List<String> filteredServices =
			Arrays.asList(HYSTRIX, TURBINE, CONFIG_SERVER, DISCOVERY, ZUUL);

	private String security = SecurityStrategyFactory.NONE;

	public void setSecurity(String security) {
		this.security = security;
	}

	public String getSecurity() {
		return security;
	}

	public Map<String, String> getRequestHeaders() {
		return requestHeaders;
	}

	public List<String> getFilteredServices() {
		return filteredServices;
	}
}
