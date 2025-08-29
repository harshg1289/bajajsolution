package com.bajaj.challenge.service;

import com.bajaj.challenge.model.WebhookRequest;
import com.bajaj.challenge.model.WebhookResponse;
import com.bajaj.challenge.model.SolutionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StartupService implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(StartupService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final String WEBHOOK_GENERATE_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    private static final String WEBHOOK_SUBMIT_URL = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";
    
    @Override
    public void run(String... args) throws Exception {
        try {
            logger.info("Starting Bajaj Finserv Health Challenge...");
            
            // Step 1: Generate webhook
            WebhookResponse webhookResponse = generateWebhook();
            
            // Step 2: Solve SQL problem (Question 1 - regNo ends with 7, which is odd)
            String sqlQuery = solveSQLProblem();
            
            // Step 3: Submit solution
            submitSolution(webhookResponse.getWebhook(), webhookResponse.getAccessToken(), sqlQuery);
            
            logger.info("Challenge completed successfully!");
            
        } catch (Exception e) {
            logger.error("Error during challenge execution: ", e);
        }
    }
    
    private WebhookResponse generateWebhook() {
        try {
            logger.info("Generating webhook...");
            
            WebhookRequest request = new WebhookRequest();
            request.setName("John Doe");
            request.setRegNo("REG12347");
            request.setEmail("john@example.com");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<WebhookResponse> response = restTemplate.postForEntity(
                WEBHOOK_GENERATE_URL, entity, WebhookResponse.class);
            
            WebhookResponse webhookResponse = response.getBody();
            logger.info("Webhook generated successfully. URL: {}", webhookResponse.getWebhook());
            
            return webhookResponse;
            
        } catch (Exception e) {
            logger.error("Failed to generate webhook: ", e);
            throw new RuntimeException("Webhook generation failed", e);
        }
    }
    
    private String solveSQLProblem() {
        logger.info("Solving SQL Problem 1...");
        
        // SQL Query to find highest salary not credited on 1st day of month
        // with employee details (name, age, department)
        String sqlQuery = """
            SELECT 
                p.AMOUNT AS SALARY,
                CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME,
                TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE,
                d.DEPARTMENT_NAME
            FROM PAYMENTS p
            INNER JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID
            INNER JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID
            WHERE DAY(p.PAYMENT_TIME) != 1
            ORDER BY p.AMOUNT DESC
            LIMIT 1
            """;
        
        logger.info("SQL Query solved: {}", sqlQuery);
        return sqlQuery.trim();
    }
    
    private void submitSolution(String webhookUrl, String accessToken, String sqlQuery) {
        try {
            logger.info("Submitting solution to webhook: {}", webhookUrl);
            
            SolutionRequest solutionRequest = new SolutionRequest();
            solutionRequest.setFinalQuery(sqlQuery);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken);
            
            HttpEntity<SolutionRequest> entity = new HttpEntity<>(solutionRequest, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                WEBHOOK_SUBMIT_URL, entity, String.class);
            
            logger.info("Solution submitted successfully. Response: {}", response.getBody());
            
        } catch (Exception e) {
            logger.error("Failed to submit solution: ", e);
            throw new RuntimeException("Solution submission failed", e);
        }
    }
}