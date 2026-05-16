package com.aws.treinamento.controller;

import com.aws.treinamento.dto.EnderecoRequest;
import com.aws.treinamento.dto.EnderecoResponse;
import com.aws.treinamento.service.EnderecoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Endereço", description = "Endpoints para buscar latitude e longitude de endereços (Nominatim)")
public class EnderecoController {

    private static final Logger logger = LoggerFactory.getLogger(EnderecoController.class);
    private final EnderecoService enderecoService;

    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    @GetMapping("/endereco")
    @Operation(
            summary = "Buscar endereço por texto livre",
            description = "Retorna latitude e longitude baseado em busca de texto livre"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Endereço encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EnderecoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Endereço inválido ou muito curto"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Endereço não encontrado"
            )
    })
    public ResponseEntity<EnderecoResponse> buscarEndereco(
            @RequestParam
            @Parameter(
                    name = "q",
                    description = "Endereço em texto livre (ex: Avenida Paulista, São Paulo)",
                    example = "Avenida Paulista, São Paulo"
            )
            String q) {
        logger.info("[EnderecoController] === GET /endereco ===");
        logger.info("[EnderecoController] Parâmetro de entrada: q={}", q);
        
        EnderecoResponse response = enderecoService.buscarPorEndereco(q);
        
        logger.info("[EnderecoController] Resposta: latitude={}, longitude={}, endereco={}", 
                response.getLatitude(), response.getLongitude(), response.getEndereco());
        logger.info("[EnderecoController] === Fim do processamento ===");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/endereco/buscar")
    @Operation(
            summary = "Buscar endereço estruturado",
            description = "Retorna latitude e longitude baseado em parâmetros estruturados (rua, cidade, país)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Endereço encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EnderecoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parâmetros obrigatórios não informados"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Endereço não encontrado"
            )
    })
    public ResponseEntity<EnderecoResponse> buscarEnderecoEstruturado(
            @RequestParam
            @Parameter(
                    name = "street",
                    description = "Rua (obrigatório)",
                    example = "Avenida Paulista"
            )
            String street,

            @RequestParam
            @Parameter(
                    name = "city",
                    description = "Cidade (obrigatório)",
                    example = "São Paulo"
            )
            String city,

            @RequestParam
            @Parameter(
                    name = "country",
                    description = "País (obrigatório)",
                    example = "Brazil"
            )
            String country,

            @RequestParam(required = false)
            @Parameter(
                    name = "amenity",
                    description = "Nome/tipo de POI (opcional)",
                    example = "restaurant"
            )
            String amenity,

            @RequestParam(required = false)
            @Parameter(
                    name = "county",
                    description = "County (opcional)",
                    example = "São Paulo"
            )
            String county,

            @RequestParam(required = false)
            @Parameter(
                    name = "state",
                    description = "Estado (opcional)",
                    example = "SP"
            )
            String state,

            @RequestParam(required = false)
            @Parameter(
                    name = "postalcode",
                    description = "CEP/Postal code (opcional)",
                    example = "01310100"
            )
            String postalcode) {

        logger.info("[EnderecoController] === GET /endereco/buscar ===");
        logger.info("[EnderecoController] Parâmetros de entrada:");
        logger.info("[EnderecoController]   - street: {}", street);
        logger.info("[EnderecoController]   - city: {}", city);
        logger.info("[EnderecoController]   - country: {}", country);
        logger.info("[EnderecoController]   - amenity: {}", amenity);
        logger.info("[EnderecoController]   - county: {}", county);
        logger.info("[EnderecoController]   - state: {}", state);
        logger.info("[EnderecoController]   - postalcode: {}", postalcode);
        
        EnderecoRequest request = new EnderecoRequest(street, city, country, amenity, county, state, postalcode);
        EnderecoResponse response = enderecoService.buscarPorEndereoEstruturado(request);
        
        logger.info("[EnderecoController] Resposta: latitude={}, longitude={}, endereco={}, tipo={}", 
                response.getLatitude(), response.getLongitude(), response.getEndereco(), response.getTipo());
        logger.info("[EnderecoController] === Fim do processamento ===");
        
        return ResponseEntity.ok(response);
    }

}
