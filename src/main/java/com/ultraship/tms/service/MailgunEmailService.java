package com.ultraship.tms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class MailgunEmailService implements EmailService {

    @Value("${mailgun.api.key}")
    private String apiKey;

    @Value("${mailgun.domain}")
    private String domain;

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

        String url = "https://api.mailgun.net/v3/" + domain + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String auth = "api:" + apiKey;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("from", "Ultraship <no-reply@" + domain + ">");
        body.add("to", to);
        body.add("subject", subject);
        body.add("html", htmlContent);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

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
