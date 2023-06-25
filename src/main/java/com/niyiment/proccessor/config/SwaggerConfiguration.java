package com.niyiment.proccessor.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfiguration {
    private static final String  SERVER_URL = "https://www.niyiment.com";
    @Value("${server.port}")
    private int port;


    @Bean
    public OpenAPI myOpenAPI() {
        String devUrl = "http://localhost:" + port;
        Server developmentServer = new Server();
        developmentServer.setUrl(devUrl);
        developmentServer.setDescription("Server URL in development environment");

        Server productionServer = new Server();
        productionServer.setUrl(SERVER_URL);
        productionServer.setDescription("Server URL in Production environment");

        Contact contact = new Contact();
        contact.setEmail("info@niyiment.com");
        contact.setName("Niyiment");
        contact.setUrl(SERVER_URL);

        Info info = new Info()
                .title("Data Processor")
                .version("1.0")
                .contact(contact)
                .description("Data Processor")
                .termsOfService(SERVER_URL + "/terms");

        return new OpenAPI().info(info).servers(List.of(developmentServer, productionServer));
    }
}
