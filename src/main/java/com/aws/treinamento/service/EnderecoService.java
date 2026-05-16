package com.aws.treinamento.service;

import com.aws.treinamento.dto.EnderecoRequest;
import com.aws.treinamento.dto.EnderecoResponse;
import com.aws.treinamento.dto.NominatimResponse;
import com.aws.treinamento.exception.EnderecoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
public class EnderecoService {

    private static final Logger logger = LoggerFactory.getLogger(EnderecoService.class);
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";

    private final RestTemplate restTemplate;

    public EnderecoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public EnderecoResponse buscarPorEndereco(String endereco) {
        validateEndereco(endereco);
        
        logger.info("[EnderecoService] === BUSCA LIVRE ===" );
        logger.info("[EnderecoService] Parâmetro recebido: q={}", endereco);
        
        // Decodificar em caso de URL encoding
        String decodedEndereco = decodeParameter(endereco);
        logger.info("[EnderecoService] Parâmetro após decodificação: q={}", decodedEndereco);

        try {
            String url = UriComponentsBuilder.fromUriString(NOMINATIM_URL)
                    .queryParam("q", decodedEndereco)
                    .queryParam("format", "json")
                    .queryParam("limit", 1)
                    .queryParam("accept-language", "pt-BR")
                    .build()
                    .toUriString();

            logger.info("[EnderecoService] URL: {}", url);
            
            NominatimResponse[] responses = restTemplate.getForObject(url, NominatimResponse[].class);

            if (responses == null || responses.length == 0) {
                logger.error("[EnderecoService] Nenhum resultado encontrado");
                throw new EnderecoException("Endereço não encontrado: " + endereco);
            }

            NominatimResponse response = responses[0];
            
            logger.info("[EnderecoService] Resposta: lat={}, lon={}, display_name={}", 
                    response.getLat(), response.getLon(), response.getDisplay_name());

            return new EnderecoResponse(
                    Double.parseDouble(response.getLat()),
                    Double.parseDouble(response.getLon()),
                    response.getDisplay_name(),
                    response.getType()
            );

        } catch (EnderecoException e) {
            logger.error("[EnderecoService] EnderecoException: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            throw new EnderecoException("Erro ao consultar endereço na API Nominatim", e);
        }
    }

    public EnderecoResponse buscarPorEndereoEstruturado(EnderecoRequest request) {
        validateRequest(request);
        
        logger.info("[EnderecoService] === BUSCA ESTRUTURADA ===" );
        logger.info("[EnderecoService] Parâmetros recebidos (com possível URL encoding):");
        logger.info("[EnderecoService]   - street: {}", request.getStreet());
        logger.info("[EnderecoService]   - city: {}", request.getCity());
        logger.info("[EnderecoService]   - country: {}", request.getCountry());
        logger.info("[EnderecoService]   - state: {}", request.getState());

        try {
            // Decodificar parâmetros que possam vir com URL encoding
            String decodedStreet = decodeParameter(request.getStreet());
            String decodedCity = decodeParameter(request.getCity());
            String decodedCountry = decodeParameter(request.getCountry());
            String decodedAmenity = decodeParameter(request.getAmenity());
            String decodedCounty = decodeParameter(request.getCounty());
            String decodedState = decodeParameter(request.getState());
            String decodedPostalcode = decodeParameter(request.getPostalcode());
            
            logger.info("[EnderecoService] Parâmetros decodificados (após URL decode):");
            logger.info("[EnderecoService]   - street: {}", decodedStreet);
            logger.info("[EnderecoService]   - city: {}", decodedCity);
            logger.info("[EnderecoService]   - country: {}", decodedCountry);
            logger.info("[EnderecoService]   - state: {}", decodedState);
            
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(NOMINATIM_URL)
                    .queryParam("format", "json")
                    .queryParam("limit", 1)
                    .queryParam("accept-language", "pt-BR");

            // Parâmetros obrigatórios
            if (decodedStreet != null && !decodedStreet.isBlank()) {
                builder.queryParam("street", decodedStreet);
            }
            if (decodedCity != null && !decodedCity.isBlank()) {
                builder.queryParam("city", decodedCity);
            }
            if (decodedCountry != null && !decodedCountry.isBlank()) {
                builder.queryParam("country", decodedCountry);
            }

            // Parâmetros opcionais
            if (decodedAmenity != null && !decodedAmenity.isBlank()) {
                builder.queryParam("amenity", decodedAmenity);
            }
            if (decodedCounty != null && !decodedCounty.isBlank()) {
                builder.queryParam("county", decodedCounty);
            }
            if (decodedState != null && !decodedState.isBlank()) {
                builder.queryParam("state", decodedState);
            }
            if (decodedPostalcode != null && !decodedPostalcode.isBlank()) {
                builder.queryParam("postalcode", decodedPostalcode);
            }

            String url = builder.build().toUriString();
            logger.info("[EnderecoService] URL final: {}", url);

            NominatimResponse[] responses = restTemplate.getForObject(url, NominatimResponse[].class);

            if (responses == null || responses.length == 0) {
                logger.error("[EnderecoService] Nenhum resultado encontrado");
                throw new EnderecoException("Endereço não encontrado para os parâmetros informados");
            }

            NominatimResponse response = responses[0];
            
            logger.info("[EnderecoService] Resposta: lat={}, lon={}, display_name={}", 
                    response.getLat(), response.getLon(), response.getDisplay_name());

            return new EnderecoResponse(
                    Double.parseDouble(response.getLat()),
                    Double.parseDouble(response.getLon()),
                    response.getDisplay_name(),
                    response.getType()
            );

        } catch (EnderecoException e) {
            logger.error("[EnderecoService] EnderecoException: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("[EnderecoService] Erro ao consultar Nominatim: {}", e.getMessage(), e);
            throw new EnderecoException("Erro ao consultar endereço na API Nominatim", e);
        }
    }

    private void validateEndereco(String endereco) {
        if (endereco == null || endereco.trim().isEmpty()) {
            throw new EnderecoException("Endereço não pode ser vazio");
        }

        if (endereco.length() < 3) {
            throw new EnderecoException("Endereço deve conter no mínimo 3 caracteres");
        }

        if (endereco.length() > 255) {
            throw new EnderecoException("Endereço não pode exceder 255 caracteres");
        }
    }

    /**
     * Decodifica um parâmetro que pode vir com URL encoding
     * Ex: "Avenida%20Conde" → "Avenida Conde"
     */
    private String decodeParameter(String param) {
        if (param == null || param.isBlank()) {
            return param;
        }
        try {
            return URLDecoder.decode(param, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Se falhar a decodificação, retorna o parâmetro original
            return param;
        }
    }

    private void validateRequest(EnderecoRequest request) {
        if (request == null) {
            throw new EnderecoException("Dados do endereço não podem ser nulos");
        }

        if (request.getStreet() == null || request.getStreet().isBlank()) {
            throw new EnderecoException("Rua é obrigatória");
        }

        if (request.getCity() == null || request.getCity().isBlank()) {
            throw new EnderecoException("Cidade é obrigatória");
        }

        if (request.getCountry() == null || request.getCountry().isBlank()) {
            throw new EnderecoException("País é obrigatório");
        }
    }

}
