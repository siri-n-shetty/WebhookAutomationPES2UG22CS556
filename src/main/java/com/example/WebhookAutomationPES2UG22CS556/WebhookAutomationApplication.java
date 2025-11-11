package com.example.WebhookAutomationPES2UG22CS556;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

@SpringBootApplication
public class WebhookAutomationApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(WebhookAutomationApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String generateWebhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

            JSONObject requestBody = new JSONObject();
            requestBody.put("name", "John Doe");
            requestBody.put("regNo", "REG12347");
            requestBody.put("email", "john@example.com");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(generateWebhookUrl, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject jsonResponse = new JSONObject(response.getBody());
                String webhookUrl = jsonResponse.getString("webhook");
                String accessToken = jsonResponse.getString("accessToken");

                System.out.println("Webhook URL: " + webhookUrl);
                System.out.println("Access Token: " + accessToken);

                String finalQuery =
                        "SELECT e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME, " +
                        "COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT " +
                        "FROM EMPLOYEE e1 " +
                        "JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID " +
                        "LEFT JOIN EMPLOYEE e2 ON e1.DEPARTMENT = e2.DEPARTMENT AND e2.DOB > e1.DOB " +
                        "GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME " +
                        "ORDER BY e1.EMP_ID DESC;";

                HttpHeaders answerHeaders = new HttpHeaders();
                answerHeaders.setContentType(MediaType.APPLICATION_JSON);
                answerHeaders.set("Authorization", accessToken);

                JSONObject answerBody = new JSONObject();
                answerBody.put("finalQuery", finalQuery);

                HttpEntity<String> answerRequest = new HttpEntity<>(answerBody.toString(), answerHeaders);

                ResponseEntity<String> answerResponse =
                        restTemplate.postForEntity(webhookUrl, answerRequest, String.class);

                System.out.println("Submission Response: " + answerResponse.getBody());
            } else {
                System.out.println("Failed to generate webhook. Response: " + response.getBody());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
