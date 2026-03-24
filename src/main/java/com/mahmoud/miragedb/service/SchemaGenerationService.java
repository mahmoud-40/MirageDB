package com.mahmoud.miragedb.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SchemaGenerationService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private static final String GEMINI_API_URL_TEMPLATE = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=%s";
    private static final String SYSTEM_PROMPT = """
    You are a Universal Database Architect. Your job is to parse data structures from ANY format (Java, C#, TypeScript interfaces, Go structs, Python Dataclasses, or even raw SQL CREATE TABLE statements) and generate 10 rows of realistic SQL INSERT statements.
    
    CRITICAL INSTRUCTIONS:
    1. Identify the table name and column names regardless of the input language.
    2. OUTPUT ONLY RAW SQL. No markdown, no conversational text.
    3. MAINTAIN REFERENTIAL INTEGRITY across all generated inserts.
    4. Ensure the INSERT order respects foreign key dependencies.
    """;

    public String generateSqlFromEntities(String entityClasses) {
        try {
            String requestBody = buildRequestBody(entityClasses);
            HttpResponse<String> response = sendHttpRequest(requestBody);
            String rawSql = parseResponse(response.body());

            return cleanSqlOutput(rawSql);

        } catch (Exception e) {
            throw new RuntimeException("Failed to process SQL generation request", e);
        }
    }

    private String buildRequestBody(String entityClasses) throws JsonProcessingException {
        Map<String, Object> requestBodyMap = Map.of(
                "systemInstruction", Map.of("parts", Map.of("text", SYSTEM_PROMPT)),
                "contents", List.of(Map.of("parts", List.of(Map.of("text", entityClasses)))),
                "generationConfig", Map.of("temperature", 0.2)
        );
        return objectMapper.writeValueAsString(requestBodyMap);
    }

    private HttpResponse<String> sendHttpRequest(String requestBody) throws IOException, InterruptedException {
        String fullUrl = String.format(GEMINI_API_URL_TEMPLATE, apiKey);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String parseResponse(String jsonResponseBody) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(jsonResponseBody);

        if (rootNode.has("error")) {
            String errorMessage = rootNode.get("error").get("message").asText();
            throw new RuntimeException("Gemini API Error: " + errorMessage);
        }

        return rootNode.path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text").asText();
    }

    private String cleanSqlOutput(String rawSql) {
        return rawSql.replace("```sql", "")
                .replace("```", "")
                .trim();
    }
}