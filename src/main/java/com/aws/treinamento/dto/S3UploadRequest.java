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
@Schema(description = "Requisição para upload de arquivo no S3")
public class S3UploadRequest {
    
    @Schema(description = "Nome do arquivo", example = "documento.pdf")
    private String fileName;
    
    @Schema(description = "Tipo de conteúdo do arquivo", example = "application/pdf")
    private String contentType;
    
    @Schema(description = "Tamanho do arquivo em bytes", example = "1024000")
    private long contentLength;
}
