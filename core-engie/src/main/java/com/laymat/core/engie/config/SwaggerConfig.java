package com.laymat.core.engie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.laymat.core.engie.controller"))
                //.paths(PathSelectors.regex("/admin/.*"))
                .build();
    }

    private ApiInfo apiInfo() {
        var contact = new Contact("haide api web","url","email");
        return new ApiInfoBuilder()
                .title("haide Restful API")
                .description("haide Restful API")
                .termsOfServiceUrl("http://api.haide.com/")
                .contact(contact)
                .version("1.0")
                .build();
    }
}
