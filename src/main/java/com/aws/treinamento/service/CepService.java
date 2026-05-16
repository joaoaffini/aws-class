package com.aws.treinamento.service;

import com.aws.treinamento.dto.CepResponse;
import com.aws.treinamento.exception.CepException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

@Service
public class CepService {

    private static final String VIACEP_URL = "https://viacep.com.br/ws/{cep}/json/";
    private static final int CEP_LENGTH = 8;

    private final RestTemplate restTemplate;

    public CepService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CepResponse findByCep(String cep) {
        validateCep(cep);

        try {
            CepResponse response = restTemplate.getForObject(
                    VIACEP_URL.replace("{cep}", cep),
                    CepResponse.class
            );

            if (response == null || response.getStreet() == null) {
                throw new CepException("CEP não encontrado: " + cep);
            }

            response.setCep(cep);
            return response;

        } catch (RestClientException e) {
            throw new CepException("Erro ao consultar CEP na API viacep", e);
        }
    }

    private void validateCep(String cep) {
        if (cep == null || cep.isEmpty()) {
            throw new CepException("CEP não pode ser vazio");
        }

        // Remove hífens se houver
        String cleanCep = cep.replaceAll("-", "");

        if (cleanCep.length() != CEP_LENGTH) {
            throw new CepException("CEP deve conter " + CEP_LENGTH + " dígitos, recebido: " + cleanCep.length());
        }

        if (!cleanCep.matches("\\d+")) {
            throw new CepException("CEP deve conter apenas dígitos");
        }
    }

}
