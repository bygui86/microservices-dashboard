package com.rabbit.samples.configserver.configs;

import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.Configuration;


@Configuration("configServerConfig")
@EnableConfigServer
public class ConfigServerConfig {

	// no-op
}
