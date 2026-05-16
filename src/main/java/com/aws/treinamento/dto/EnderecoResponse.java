package com.aws.treinamento.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnderecoResponse {

    private Double latitude;
    private Double longitude;
    private String endereco;
    private String tipo;

}
