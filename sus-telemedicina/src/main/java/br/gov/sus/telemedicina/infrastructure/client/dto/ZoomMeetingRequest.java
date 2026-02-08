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
public class ZoomMeetingRequest {

    private String topic;

    private Integer type; // 1 = Instant, 2 = Scheduled

    @JsonProperty("start_time")
    private String startTime; // Format: yyyy-MM-dd'T'HH:mm:ss'Z'

    private Integer duration; // in minutes

    private String timezone;

    private Settings settings;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Settings {
        @JsonProperty("host_video")
        private Boolean hostVideo;

        @JsonProperty("participant_video")
        private Boolean participantVideo;

        @JsonProperty("join_before_host")
        private Boolean joinBeforeHost;

        @JsonProperty("mute_upon_entry")
        private Boolean muteUponEntry;

        @JsonProperty("waiting_room")
        private Boolean waitingRoom;

        @JsonProperty("audio")
        private String audio; // both, telephony, voip

        @JsonProperty("auto_recording")
        private String autoRecording; // local, cloud, none
    }
}

