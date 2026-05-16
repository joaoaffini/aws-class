package com.aws.treinamento.controller;

import com.aws.treinamento.dto.CepResponse;
import com.aws.treinamento.service.CepService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "CEP", description = "Endpoints para buscar informações por CEP")
public class CepController {

    private final CepService cepService;

    public CepController(CepService cepService) {
        this.cepService = cepService;
    }

    @GetMapping("/cep/{cep}")
    @Operation(
            summary = "Buscar endereço por CEP",
            description = "Retorna informações de endereço baseado no CEP informado (8 dígitos)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "CEP encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CepResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "CEP inválido (deve ter 8 dígitos)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "CEP não encontrado"
            )
    })
    public ResponseEntity<CepResponse> findByCep(
            @PathVariable
            @Parameter(
                    name = "cep",
                    description = "CEP com 8 dígitos (ex: 01310100)",
                    example = "01310100"
            )
            String cep) {
        CepResponse response = cepService.findByCep(cep);
        return ResponseEntity.ok(response);
    }

}
