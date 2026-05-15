package com.aws.treinamento.service;

import com.aws.treinamento.dto.HealthResponse;
import com.aws.treinamento.exception.HealthException;
import com.aws.treinamento.model.Health;
import com.aws.treinamento.repository.HealthRepository;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    private final HealthRepository healthRepository;

    public HealthService(HealthRepository healthRepository) {
        this.healthRepository = healthRepository;
    }

    public HealthResponse checkHealth() {
        try {
            Health health = new Health();
            health.setStatus("UP");
            health.setTimestamp(System.currentTimeMillis());

            Health savedHealth = healthRepository.save(health);

            return new HealthResponse(savedHealth.getStatus(), savedHealth.getTimestamp());
        } catch (Exception e) {
            throw new HealthException("Erro ao verificar saúde da aplicação", e);
        }
    }

    public HealthResponse getLatestHealth() {
        Health health = healthRepository.findLatest()
                .orElseThrow(() -> new HealthException("Nenhum registro de health encontrado"));

        return new HealthResponse(health.getStatus(), health.getTimestamp());
    }

}
