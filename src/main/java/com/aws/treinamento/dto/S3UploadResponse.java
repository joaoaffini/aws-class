package com.aws.treinamento.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta após upload de arquivo no S3")
public class S3UploadResponse {
    
    @Schema(description = "Mensagem de sucesso", example = "Arquivo enviado com sucesso")
    private String message;
    
    @Schema(description = "Nome do arquivo enviado", example = "documento.pdf")
    private String fileName;
    
    @Schema(description = "URL do arquivo no S3", example = "s3://bucket-name/documento.pdf")
    private String s3Url;
    
    @Schema(description = "Timestamp do upload", example = "2026-05-16T10:30:00Z")
    private String uploadedAt;
}
