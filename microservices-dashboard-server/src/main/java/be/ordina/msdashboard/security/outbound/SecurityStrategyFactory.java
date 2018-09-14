/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package be.ordina.msdashboard.security.outbound;

import be.ordina.msdashboard.security.config.DefaultStrategyBeanProvider;
import be.ordina.msdashboard.security.config.MSDashboardSecurityProperties;
import be.ordina.msdashboard.security.config.StrategyBeanProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class to create SecurityStrategy annotated classes
 *
 * @author Kevin Van Houtte
 * @author Andreas Evers
 */
public class SecurityStrategyFactory {

	private static final Logger logger = LoggerFactory.getLogger(SecurityStrategyFactory.class);
	public static final String NONE = "none";

	private ApplicationContext applicationContext;
	private MSDashboardSecurityProperties msDashboardSecurityProperties;

	private Map<String, StrategyBeanProvider> strategies = new HashMap<>();

	public SecurityStrategyFactory(ApplicationContext applicationContext, MSDashboardSecurityProperties msDashboardSecurityProperties) {
		this.applicationContext = applicationContext;
		this.msDashboardSecurityProperties = msDashboardSecurityProperties;
	}

	@PostConstruct
	public void init() {
		Map<String, StrategyBeanProvider> strategyBeanProviderClasses = applicationContext.getBeansOfType(StrategyBeanProvider.class);

		for (Object bean : strategyBeanProviderClasses.values()) {
			PropertyDescriptor typeProperty = null;
			try {
				typeProperty = BeanUtils.findPropertyForMethod(bean.getClass().getMethod("getType"));
			} catch (NoSuchMethodException e) {
				throw new IllegalArgumentException("Strategy with type " + bean.getClass() + " did not implement the getType method", e);
			}
			try {
				strategies.put(typeProperty.getReadMethod().invoke(bean).toString(), (StrategyBeanProvider) bean);
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException("Strategy with type " + bean.getClass() + " did not implement the getType method", e);
			} catch (InvocationTargetException e) {
				throw new IllegalArgumentException("Strategy with type " + bean.getClass() + " did not implement the getType method", e);
			}
		}
	}

	public StrategyBeanProvider getStrategy(String aggregatorKey) {
		final String strategyKey = msDashboardSecurityProperties.getStrategiesByAggregator().get(aggregatorKey);
		StrategyBeanProvider strategyBeanProvider = strategies.get(strategyKey);
		logger.debug("Strategies found: " + strategies);
		if (strategyBeanProvider == null) {
			logger.info("No StrategyBeanProvider found for aggregator with key '" + aggregatorKey + "', return DefaultStrategyBeanProvider");
			return new DefaultStrategyBeanProvider();
		}
		return strategyBeanProvider;
	}
}
