package com.ricardo.takehome.devices.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;

import java.util.List;

public class OpenAPIConfig {
    @Bean
    public OpenAPI deviceApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Device API")
                        .description("REST API for device management")
                        .version("1.0.0")
                ).servers(List.of(
                        new Server().url("/").description("Default server")));
    }
}
