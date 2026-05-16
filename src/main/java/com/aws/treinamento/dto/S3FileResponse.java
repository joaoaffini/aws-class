package com.aws.treinamento.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informações sobre um arquivo no S3")
public class S3FileResponse {
    
    @Schema(description = "Nome do arquivo", example = "documento.pdf")
    private String fileName;
    
    @Schema(description = "Tamanho do arquivo em bytes", example = "1024000")
    private long size;
    
    @Schema(description = "Data da última modificação", example = "2026-05-16T10:30:00Z")
    private String lastModified;
    
    @Schema(description = "URL do arquivo no S3", example = "s3://bucket-name/documento.pdf")
    private String s3Url;
    
    @Schema(description = "ETag do arquivo", example = "\"1234567890abcdef\"")
    private String etag;
}
