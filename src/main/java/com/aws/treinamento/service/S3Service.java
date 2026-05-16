package com.aws.treinamento.service;

import com.aws.treinamento.dto.S3FileResponse;
import com.aws.treinamento.dto.S3ListResponse;
import com.aws.treinamento.dto.S3UploadResponse;
import com.aws.treinamento.exception.S3Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .withZone(ZoneId.of("UTC"));

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name:s3-aws-class-4}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public S3UploadResponse uploadFile(String fileName, byte[] fileContent, String contentType) {
        logger.info("[S3Service] Iniciando upload do arquivo: {}", fileName);
        
        validateFileName(fileName);
        validateFileContent(fileContent);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(contentType)
                    .contentLength((long) fileContent.length)
                    .build();

            PutObjectResponse putObjectResponse = s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(fileContent)
            );

            logger.info("[S3Service] Arquivo uploadado com sucesso. ETag: {}", putObjectResponse.eTag());

            String s3Url = String.format("s3://%s/%s", bucketName, fileName);
            String uploadedAt = DATE_FORMATTER.format(Instant.now());

            return S3UploadResponse.builder()
                    .message("Arquivo enviado com sucesso")
                    .fileName(fileName)
                    .s3Url(s3Url)
                    .uploadedAt(uploadedAt)
                    .build();

        } catch (Exception e) {
            logger.error("[S3Service] Erro ao fazer upload do arquivo: {}", fileName, e);
            throw new S3Exception("Erro ao fazer upload do arquivo: " + e.getMessage(), e);
        }
    }

    public S3ListResponse listFiles() {
        logger.info("[S3Service] ===== INICIANDO LISTAGEM DE ARQUIVOS =====");
        logger.info("[S3Service] Bucket: {}", bucketName);

        try {
            logger.info("[S3Service] Criando ListObjectsV2Request...");
            ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            logger.info("[S3Service] Chamando S3 para listar objetos...");
            ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);

            logger.info("[S3Service] Resposta recebida. Total de objetos: {}", 
                    listObjectsResponse.keyCount());

            List<S3FileResponse> files = listObjectsResponse.contents().stream()
                    .map(this::mapS3ObjectToFileResponse)
                    .collect(Collectors.toList());

            logger.info("[S3Service] Total de arquivos processados: {}", files.size());
            logger.info("[S3Service] ===== LISTAGEM CONCLUÍDA COM SUCESSO =====");

            return S3ListResponse.builder()
                    .bucketName(bucketName)
                    .totalFiles(files.size())
                    .files(files)
                    .build();

        } catch (Exception e) {
            logger.error("[S3Service] ===== ERRO AO LISTAR ARQUIVOS =====", e);
            logger.error("[S3Service] Tipo de exceção: {}", e.getClass().getName());
            logger.error("[S3Service] Mensagem: {}", e.getMessage());
            logger.error("[S3Service] Bucket tentado: {}", bucketName);
            throw new S3Exception("Erro ao listar arquivos: " + e.getMessage(), e);
        }
    }

    public byte[] downloadFile(String fileName) {
        logger.info("[S3Service] Baixando arquivo: {}", fileName);

        validateFileName(fileName);

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            byte[] fileContent = s3Client.getObject(getObjectRequest).readAllBytes();
            logger.info("[S3Service] Arquivo baixado com sucesso: {}", fileName);

            return fileContent;

        } catch (Exception e) {
            logger.error("[S3Service] Erro ao baixar arquivo: {}", fileName, e);
            throw new S3Exception("Erro ao baixar arquivo: " + e.getMessage(), e);
        }
    }

    private S3FileResponse mapS3ObjectToFileResponse(S3Object s3Object) {
        String s3Url = String.format("s3://%s/%s", bucketName, s3Object.key());
        String lastModified = DATE_FORMATTER.format(s3Object.lastModified());

        return S3FileResponse.builder()
                .fileName(s3Object.key())
                .size(s3Object.size())
                .lastModified(lastModified)
                .s3Url(s3Url)
                .etag(s3Object.eTag())
                .build();
    }

    private void validateFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new S3Exception("Nome do arquivo não pode estar vazio");
        }
        if (fileName.contains("..") || fileName.startsWith("/")) {
            throw new S3Exception("Nome de arquivo inválido");
        }
    }

    private void validateFileContent(byte[] fileContent) {
        if (fileContent == null || fileContent.length == 0) {
            throw new S3Exception("Arquivo não pode estar vazio");
        }
        if (fileContent.length > 5_000_000_000L) { // 5GB
            throw new S3Exception("Arquivo muito grande. Tamanho máximo: 5GB");
        }
    }
}
