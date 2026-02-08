package br.gov.sus.telemedicina.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoomMeetingResponse {

    private String id;

    @JsonProperty("host_id")
    private String hostId;

    private String topic;

    private Integer type;

    @JsonProperty("start_time")
    private String startTime;

    private Integer duration;

    private String timezone;

    @JsonProperty("join_url")
    private String joinUrl;

    @JsonProperty("start_url")
    private String startUrl;

    private String password;

    @JsonProperty("h323_password")
    private String h323Password;
}

