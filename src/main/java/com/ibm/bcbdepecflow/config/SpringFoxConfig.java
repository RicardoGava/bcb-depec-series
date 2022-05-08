package com.ibm.bcbdepecflow.config;

import com.ibm.bcbdepecflow.config.util.SwaggerPageable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;

import java.net.URI;

import static org.springframework.web.servlet.function.RequestPredicates.GET;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@Configuration

public class SpringFoxConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build()
                .directModelSubstitute(Pageable.class, SwaggerPageable.class)
                .apiInfo(metaData());

    }

    private ApiInfo metaData() {
        return new ApiInfoBuilder()
                .title("Documentação dos endpoints da aplicação")
                .description("Essa aplicação consome as APIs das séries do Departamento Econômico do Banco Central do Brasil, faz onboarding dos dados e metadados das mesmas e disponibiliza via REST API. Nessa documentação será disponibilizado os endpoints e possíveis parâmetros dessa API.")
                .contact(new Contact("Ricardo de Souza Gava", "https://www.linkedin.com/in/ricardogava/", "ricardogava@ibm.com"))
                .version("1.0.0")
                .build();
    }

    // Remove models do swagger-ui
    @Bean
    UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder().defaultModelsExpandDepth(-1).build();
    }

    // Redireciona página /docs para /swagger-ui/#/
    @Bean
    RouterFunction<ServerResponse> routerFunction() {
        return route(GET("/docs"), req ->
                ServerResponse.temporaryRedirect(URI.create("swagger-ui/#/")).build());
    }

}
