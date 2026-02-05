package br.gov.sus.sus.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Response da API Nominatim (OpenStreetMap) para geocoding
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NominatimResponse {

    @JsonProperty("place_id")
    private Long placeId;

    @JsonProperty("lat")
    private String lat;

    @JsonProperty("lon")
    private String lon;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private String type;

    public Double getLatitude() {
        if (lat == null || lat.isBlank()) {
            return null;
        }
        try {
            return Double.parseDouble(lat);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Double getLongitude() {
        if (lon == null || lon.isBlank()) {
            return null;
        }
        try {
            return Double.parseDouble(lon);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
