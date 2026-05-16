package com.aws.treinamento.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NominatimResponse {

    private String place_id;
    private String osm_type;
    private String osm_id;
    private String name;
    private String lat;
    private String lon;
    private String display_name;
    private Double importance;
    private String place_rank;
    private String address_rank;
    private String type;

}
