package com.aws.treinamento.service;

import com.aws.treinamento.dto.HealthResponse;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    public HealthResponse checkHealth() {
        return new HealthResponse("UP", System.currentTimeMillis());
    }

}
