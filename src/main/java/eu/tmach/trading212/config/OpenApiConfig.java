package eu.tmach.trading212.config;

import eu.tmach.trading212.exception.ApiErrorResponse;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fifolio")
                        .version("1.0")
                        .description("API pro správu a synchronizaci investičního portfolia z Trading212"))
                .components(new Components());
        // Necháme prázdné, schémata tam dotlačíme v Customizeru, aby byla jistota existence
    }

    @Bean
    public OpenApiCustomizer customerGlobalHeaderOpenApiCustomiser() {
        return openApi -> {
            // 1. Získáme schémata pomocí ModelConverters
            var apiErrorSchema = ModelConverters.getInstance()
                    .resolveAsResolvedSchema(new AnnotatedType(ApiErrorResponse.class)).schema;

            // 2. Ručně je přidáme do komponent (pokud tam už nejsou)
            if (openApi.getComponents().getSchemas() == null) {
                openApi.getComponents().setSchemas(new java.util.HashMap<>());
            }
            openApi.getComponents().addSchemas("ApiErrorResponse", apiErrorSchema);

            // 3. Aplikujeme na všechny endpointy
            openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
                var responses = operation.getResponses();

                // Globální 500
                if (!responses.containsKey("500")) {
                    responses.addApiResponse("500", new ApiResponse()
                            .description("Interní chyba serveru")
                            .content(new Content().addMediaType("application/json",
                                    new MediaType().schema(new Schema<>().$ref("#/components/schemas/ApiErrorResponse")))));
                }

            }));
        };
    }
}