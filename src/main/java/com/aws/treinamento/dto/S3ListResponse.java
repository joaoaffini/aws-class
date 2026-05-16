package com.aws.treinamento.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta com lista de arquivos do S3")
public class S3ListResponse {
    
    @Schema(description = "Nome do bucket", example = "s3-aws-class-4")
    private String bucketName;
    
    @Schema(description = "Total de arquivos", example = "10")
    private int totalFiles;
    
    @Schema(description = "Lista de arquivos")
    private List<S3FileResponse> files;
}
