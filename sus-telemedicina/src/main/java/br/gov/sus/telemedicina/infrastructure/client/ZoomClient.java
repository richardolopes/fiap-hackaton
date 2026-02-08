package br.gov.sus.telemedicina.infrastructure.client;

import br.gov.sus.telemedicina.infrastructure.client.dto.ZoomMeetingRequest;
import br.gov.sus.telemedicina.infrastructure.client.dto.ZoomMeetingResponse;
import br.gov.sus.telemedicina.infrastructure.client.dto.ZoomTokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Slf4j
@Service
public class ZoomClient {

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String accountId;
    private final String clientId;
    private final String clientSecret;

    private String cachedAccessToken;
    private LocalDateTime tokenExpiryTime;

    public ZoomClient(
            @Value("${zoom.api.base-url}") String baseUrl,
            @Value("${zoom.api.account-id}") String accountId,
            @Value("${zoom.api.client-id}") String clientId,
            @Value("${zoom.api.client-secret}") String clientSecret,
            ObjectMapper objectMapper
    ) {
        this.baseUrl = baseUrl;
        this.accountId = accountId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.objectMapper = objectMapper;
        this.httpClient = new OkHttpClient();
    }

    public ZoomMeetingResponse createMeeting(String topic, LocalDateTime startTime, Integer durationMinutes) {
        try {
            String accessToken = getAccessToken();

            ZoomMeetingRequest request = buildMeetingRequest(topic, startTime, durationMinutes);
            String requestBody = objectMapper.writeValueAsString(request);

            log.info("Creating Zoom meeting: {}", topic);

            Request httpRequest = new Request.Builder()
                    .url(baseUrl + "/users/me/meetings")
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                    .build();

            try (Response response = httpClient.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Failed to create Zoom meeting. Status: {}, Body: {}",
                            response.code(), response.body() != null ? response.body().string() : "null");
                    throw new RuntimeException("Failed to create Zoom meeting: " + response.code());
                }

                String responseBody = response.body().string();
                ZoomMeetingResponse meetingResponse = objectMapper.readValue(responseBody, ZoomMeetingResponse.class);

                log.info("Zoom meeting created successfully. Meeting ID: {}", meetingResponse.getId());
                return meetingResponse;
            }
        } catch (Exception e) {
            log.error("Error creating Zoom meeting", e);
            throw new RuntimeException("Error creating Zoom meeting", e);
        }
    }

    private String getAccessToken() throws IOException {
        // Check if we have a valid cached token
        if (cachedAccessToken != null && tokenExpiryTime != null
                && LocalDateTime.now().isBefore(tokenExpiryTime.minusMinutes(5))) {
            return cachedAccessToken;
        }

        log.info("Fetching new Zoom access token");

        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        RequestBody body = new FormBody.Builder()
                .add("grant_type", "account_credentials")
                .add("account_id", accountId)
                .build();

        Request request = new Request.Builder()
                .url("https://zoom.us/oauth/token")
                .header("Authorization", "Basic " + encodedCredentials)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get Zoom access token: " + response.code());
            }

            String responseBody = response.body().string();
            ZoomTokenResponse tokenResponse = objectMapper.readValue(responseBody, ZoomTokenResponse.class);

            cachedAccessToken = tokenResponse.getAccessToken();
            tokenExpiryTime = LocalDateTime.now().plusSeconds(tokenResponse.getExpiresIn());

            log.info("Access token fetched successfully");
            return cachedAccessToken;
        }
    }

    private ZoomMeetingRequest buildMeetingRequest(String topic, LocalDateTime startTime, Integer durationMinutes) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

        return ZoomMeetingRequest.builder()
                .topic(topic)
                .type(2) // Scheduled meeting
                .startTime(startTime.format(formatter))
                .duration(durationMinutes)
                .timezone("America/Sao_Paulo")
                .settings(ZoomMeetingRequest.Settings.builder()
                        .hostVideo(true)
                        .participantVideo(true)
                        .joinBeforeHost(false)
                        .muteUponEntry(true)
                        .waitingRoom(false)
                        .audio("both")
                        .autoRecording("none")
                        .build())
                .build();
    }
}

