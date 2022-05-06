package com.ibm.bcbdepecflow.config;

import com.ibm.bcbdepecflow.config.util.SwaggerPageable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

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
                .title("Desafio Public Sector Net Debt IBM")
                .description("Uma aplicação que consome qualquer json das séries do Banco Central do Brasil - Departamento Econômico, faz onboarding dos dados e metadados das séries e disponibiliza para o usuário via REST.")
                .contact(new Contact("Ricardo de Souza Gava", "https://www.linkedin.com/in/ricardogava/", "ricardogava@ibm.com"))
                .version("1.0.0")
                .build();
    }

}
