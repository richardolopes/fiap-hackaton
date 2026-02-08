package br.gov.sus.telemedicina.infrastructure.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;

@Slf4j
@Service
public class WhatsAppClient {

    private final OkHttpClient httpClient;
    private final String accountSid;
    private final String authToken;
    private final String fromNumber;
    private final String apiUrl;

    public WhatsAppClient(
            @Value("${whatsapp.twilio.account-sid}") String accountSid,
            @Value("${whatsapp.twilio.auth-token}") String authToken,
            @Value("${whatsapp.twilio.from-number}") String fromNumber,
            @Value("${whatsapp.twilio.api-url}") String apiUrl
    ) {
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.fromNumber = fromNumber;
        this.apiUrl = apiUrl;
        this.httpClient = new OkHttpClient();
    }

    public void sendMessage(String toPhoneNumber, String message) {
        try {
            log.info("Sending WhatsApp message to: {}", toPhoneNumber);

            String credentials = accountSid + ":" + authToken;
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

            // Ensure the phone number has the whatsapp: prefix
            String formattedTo = toPhoneNumber.startsWith("whatsapp:")
                    ? toPhoneNumber
                    : "whatsapp:" + toPhoneNumber;

            // Ensure the phone number has the whatsapp: prefix
            String formattedFrom = fromNumber.startsWith("whatsapp:")
                    ? fromNumber
                    : "whatsapp:" + fromNumber;

            RequestBody body = new FormBody.Builder()
                    .add("From", formattedFrom)
                    .add("To", formattedTo)
                    .add("Body", message)
                    .build();

            Request request = new Request.Builder()
                    .url(apiUrl + "/Accounts/" + accountSid + "/Messages.json")
                    .header("Authorization", "Basic " + encodedCredentials)
                    .post(body)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "null";
                    log.error("Failed to send WhatsApp message. Status: {}, Body: {}",
                            response.code(), responseBody);
                    throw new RuntimeException("Failed to send WhatsApp message: " + response.code());
                }

                log.info("WhatsApp message sent successfully to: {}", toPhoneNumber);
            }
        } catch (Exception e) {
            log.error("Error sending WhatsApp message", e);
            throw new RuntimeException("Error sending WhatsApp message", e);
        }
    }

    public void sendConsultationNotification(String toPhoneNumber, String patientName,
                                            String doctorName, String appointmentTime,
                                            String zoomLink) {
        String message = String.format("""
                🏥 *Lembrete de Consulta - SUS Telemedicina*
                
                Olá %s,
                
                Sua consulta por telemedicina está próxima!
                
                📋 *Detalhes da Consulta:*
                👨‍⚕️ Profissional: %s
                🕐 Horário: %s
                
                🔗 *Link da Reunião:*
                %s
                
                ⚠️ *Instruções:*
                1. Clique no link alguns minutos antes do horário
                2. Certifique-se de ter uma boa conexão de internet
                3. Tenha seus documentos e exames em mãos
                
                Em caso de dúvidas, entre em contato com a unidade de saúde.
                
                Atenciosamente,
                Sistema SUS Telemedicina
                """, patientName, doctorName, appointmentTime, zoomLink);

        sendMessage(toPhoneNumber, message);
    }
}

