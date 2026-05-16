package com.aws.treinamento.controller;

import com.aws.treinamento.dto.S3ListResponse;
import com.aws.treinamento.dto.S3UploadResponse;
import com.aws.treinamento.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/s3")
@Tag(name = "S3", description = "Operações de gerenciamento de arquivos no Amazon S3")
public class S3Controller {

    private static final Logger logger = LoggerFactory.getLogger(S3Controller.class);

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Fazer upload de arquivo", description = "Faz upload de um arquivo para o bucket S3")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Arquivo enviado com sucesso",
            content = @Content(schema = @Schema(implementation = S3UploadResponse.class))),
        @ApiResponse(responseCode = "400", description = "Requisição inválida"),
        @ApiResponse(responseCode = "500", description = "Erro no servidor")
    })
    public ResponseEntity<S3UploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file) {

        logger.info("[S3Controller] Requisição de upload recebida. Nome do arquivo: {}, Tamanho: {} bytes",
                file.getOriginalFilename(), file.getSize());

        try {
            byte[] fileContent = file.getBytes();
            String fileName = file.getOriginalFilename();
            String contentType = file.getContentType();

            S3UploadResponse response = s3Service.uploadFile(fileName, fileContent, contentType);

            logger.info("[S3Controller] Upload concluído com sucesso para: {}", fileName);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            logger.error("[S3Controller] Erro ao processar upload", e);
            throw new RuntimeException("Erro ao processar o arquivo: " + e.getMessage(), e);
        }
    }

    @GetMapping("/list")
    @Operation(summary = "Listar arquivos", description = "Lista todos os arquivos do bucket S3")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de arquivos recuperada com sucesso",
            content = @Content(schema = @Schema(implementation = S3ListResponse.class))),
        @ApiResponse(responseCode = "500", description = "Erro no servidor")
    })
    public ResponseEntity<S3ListResponse> listFiles() {

        logger.info("[S3Controller] Requisição para listar arquivos do S3");

        S3ListResponse response = s3Service.listFiles();

        logger.info("[S3Controller] Lista retornada com {} arquivo(s)", response.getTotalFiles());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{fileName}")
    @Operation(summary = "Baixar arquivo", description = "Baixa um arquivo específico do bucket S3")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Arquivo baixado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Arquivo não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro no servidor")
    })
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable String fileName) {

        logger.info("[S3Controller] Requisição para baixar arquivo: {}", fileName);

        byte[] fileContent = s3Service.downloadFile(fileName);

        logger.info("[S3Controller] Arquivo enviado para download: {}", fileName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body(fileContent);
    }
}
