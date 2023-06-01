package com.generation.blogpessoal.configuration;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

@Configuration // indica que a Classe é do tipo configuração
public class SwaggerConfig {

	@Bean // indica ao Spring que ele deve invocar aquele Método e gerenciar o objeto retornado por ele
	OpenAPI springBlogPessoalOpenAPI() {
		return new OpenAPI()
				.info(new Info().title("projeto blog pessoal").description("projeto blog pessoal - gereration brasil")
						.version("v0.0.1")
						.license(new License().name("Generation brasil").url("Https://brazil.generation.org/"))
						.contact(new Contact().name("Generation brasil").url("Https://brazil.generation.org/")
								.email("contatogeneration@generation.org")))
				.externalDocs(new ExternalDocumentation().description("github")
						.url("https://github.com/conteudoGeneration/"));
	}

	@Bean
	OpenApiCustomizer customerGlobalHeaderOperApiCustomizer() {
		return openApi -> {
			openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations()
					.forEach(operation -> {

				ApiResponses apiResponses = operation.getResponses();
						
				apiResponses.addApiResponse("200", createApiResponse("Sucesso!"));
				apiResponses.addApiResponse("201", createApiResponse("objeto Persistido!"));
				apiResponses.addApiResponse("204", createApiResponse("objeto Excluído"));
				apiResponses.addApiResponse("400", createApiResponse("erro na Requisição"));
				apiResponses.addApiResponse("401", createApiResponse("acesso não Autorizado!"));
				apiResponses.addApiResponse("403", createApiResponse("acesso Proibido!"));
				apiResponses.addApiResponse("404", createApiResponse("objeto não Encontrado!"));
				apiResponses.addApiResponse("500", createApiResponse("erro na Aplicação!"));
			}));
		};
	}

	private ApiResponse createApiResponse(String message) {
		return new ApiResponse().description(message);
	}
}
