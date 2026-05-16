package com.aws.treinamento.service;

import com.aws.treinamento.dto.EnderecoRequest;
import com.aws.treinamento.dto.EnderecoResponse;
import com.aws.treinamento.dto.NominatimResponse;
import com.aws.treinamento.exception.EnderecoException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EnderecoService {

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";

    private final RestTemplate restTemplate;

    public EnderecoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public EnderecoResponse buscarPorEndereco(String endereco) {
        validateEndereco(endereco);

        try {
            String url = UriComponentsBuilder.fromUriString(NOMINATIM_URL)
                    .queryParam("q", endereco)
                    .queryParam("format", "json")
                    .queryParam("limit", 1)
                    .queryParam("accept-language", "pt-BR")
                    .build()
                    .toUriString();

            NominatimResponse[] responses = restTemplate.getForObject(url, NominatimResponse[].class);

            if (responses == null || responses.length == 0) {
                throw new EnderecoException("Endereço não encontrado: " + endereco);
            }

            NominatimResponse response = responses[0];

            return new EnderecoResponse(
                    Double.parseDouble(response.getLat()),
                    Double.parseDouble(response.getLon()),
                    response.getDisplay_name(),
                    response.getType()
            );

        } catch (EnderecoException e) {
            throw e;
        } catch (Exception e) {
            throw new EnderecoException("Erro ao consultar endereço na API Nominatim", e);
        }
    }

    public EnderecoResponse buscarPorEndereoEstruturado(EnderecoRequest request) {
        validateRequest(request);

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(NOMINATIM_URL)
                    .queryParam("format", "json")
                    .queryParam("limit", 1)
                    .queryParam("accept-language", "pt-BR");

            // Parâmetros obrigatórios
            if (request.getStreet() != null && !request.getStreet().isBlank()) {
                builder.queryParam("street", request.getStreet());
            }
            if (request.getCity() != null && !request.getCity().isBlank()) {
                builder.queryParam("city", request.getCity());
            }
            if (request.getCountry() != null && !request.getCountry().isBlank()) {
                builder.queryParam("country", request.getCountry());
            }

            // Parâmetros opcionais
            if (request.getAmenity() != null && !request.getAmenity().isBlank()) {
                builder.queryParam("amenity", request.getAmenity());
            }
            if (request.getCounty() != null && !request.getCounty().isBlank()) {
                builder.queryParam("county", request.getCounty());
            }
            if (request.getState() != null && !request.getState().isBlank()) {
                builder.queryParam("state", request.getState());
            }
            if (request.getPostalcode() != null && !request.getPostalcode().isBlank()) {
                builder.queryParam("postalcode", request.getPostalcode());
            }

            String url = builder.build().toUriString();

            NominatimResponse[] responses = restTemplate.getForObject(url, NominatimResponse[].class);

            if (responses == null || responses.length == 0) {
                throw new EnderecoException("Endereço não encontrado para os parâmetros informados");
            }

            NominatimResponse response = responses[0];

            return new EnderecoResponse(
                    Double.parseDouble(response.getLat()),
                    Double.parseDouble(response.getLon()),
                    response.getDisplay_name(),
                    response.getType()
            );

        } catch (EnderecoException e) {
            throw e;
        } catch (Exception e) {
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
