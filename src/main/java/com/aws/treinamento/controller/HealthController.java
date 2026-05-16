package com.aws.treinamento.controller;

import com.aws.treinamento.dto.HealthResponse;
import com.aws.treinamento.service.HealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health", description = "Endpoints para verificar saúde da aplicação")
public class HealthController {

    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);
    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/health")
    @Operation(
            summary = "Verificar saúde da aplicação",
            description = "Retorna o status UP da aplicação com timestamp"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Aplicação está saudável",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = HealthResponse.class)
            )
    )
    public HealthResponse health() {
        logger.info("[HealthController] === GET /health ===");
        logger.info("[HealthController] Nenhum parâmetro de entrada");
        
        HealthResponse response = healthService.checkHealth();
        
        logger.info("[HealthController] Resposta: status={}, timestamp={}", response.getStatus(), response.getTimestamp());
        logger.info("[HealthController] === Fim do processamento ===");
        
        return response;
    }

}
