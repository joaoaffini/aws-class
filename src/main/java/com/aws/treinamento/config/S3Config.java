package com.aws.treinamento.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Configuração do cliente S3 da AWS.
 * 
 * IMPORTANTE: Esta configuração usa automaticamente as credenciais da IAM Role
 * associada à EC2 (Instance Metadata Service). Não é necessário configurar
 * AWS_ACCESS_KEY_ID ou AWS_SECRET_ACCESS_KEY.
 */
@Configuration
public class S3Config {
    
    @Bean
    public S3Client s3Client() {
        // O SDK do AWS busca credenciais automaticamente na seguinte ordem:
        // 1. Environment variables
        // 2. Credentials file
        // 3. Instance Metadata Service (EC2) ← Será usado em produção
        return S3Client.builder()
                .region(software.amazon.awssdk.regions.Region.SA_EAST_1)
                .build();
    }
}
