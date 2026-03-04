package com.ultraship.tms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class MailjetEmailService implements EmailService {

    @Value("${mailjet.api-key}")
    private String apiKey;

    @Value("${mailjet.secret-key}")
    private String secretKey;

    @Value("${mailjet.from-email}")
    private String fromEmail;

    @Value("${mailjet.from-name}")
    private String fromName;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void sendVerificationEmail(String to, String token) {
        String link = frontendUrl + "/verify?token=" + token;
        sendEmail(to, "Verify Your Account",
                buildHtml("Verify Account", link));
    }

    @Override
    public void sendResetEmail(String to, String token) {
        String link = frontendUrl + "/reset-password?token=" + token;
        sendEmail(to, "Reset Your Password",
                buildHtml("Reset Password", link));
    }

    private void sendEmail(String to, String subject, String htmlContent) {

        String url = "https://api.mailjet.com/v3.1/send";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String auth = apiKey + ":" + secretKey;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);

        htmlContent = htmlContent
                .replace("\n", "")
                .replace("\r", "")
                .replace("\t", "");
        Map<String, Object> payload = Map.of(
                "Messages", List.of(
                        Map.of(
                                "From", Map.of(
                                        "Email", fromEmail,
                                        "Name", fromName
                                ),
                                "To", List.of(
                                        Map.of("Email", to)
                                ),
                                "Subject", subject,
                                "HTMLPart", htmlContent
                        )
                )
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(payload, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(url, request, String.class);
    }

    private String buildHtml(String buttonText, String link) {
        return """
            <div style="font-family:Arial;padding:20px">
                <h2>%s</h2>
                <p>Please click the button below:</p>
                <a href="%s"
                   style="padding:10px 20px;background:#2563eb;
                   color:white;text-decoration:none;border-radius:5px">
                   %s
                </a>
            </div>
        """.formatted(buttonText, link, buttonText);
    }
}
