package com.aws.treinamento.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnderecoRequest {

    private String street;
    private String city;
    private String country;

    // Campos opcionais
    private String amenity;
    private String county;
    private String state;
    private String postalcode;

}
