package com.edupredict.controller;

import com.edupredict.model.Student;
import com.edupredict.repository.StudentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

 
    private String getGeminiUrl() {
        String apiKey = System.getenv("GEMINI_API_KEY");

        if (apiKey == null || apiKey.isEmpty()) {
            
            apiKey = "AIzaSyDRImHVJR_vWU_oL3Up--XeZ_wi0Q0AQPs";
        }

        return "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="  + apiKey;
    }

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping
    public Map<String, String> chatWithBot(@RequestBody Map<String, String> payload) {
        String userMessage = payload.get("message");
        String botResponse;

        try {
            botResponse = callGeminiAI(userMessage);
        } catch (Exception e) {
            e.printStackTrace();
            botResponse = "System Error: " + e.getMessage();
        }

        Map<String, String> response = new HashMap<>();
        response.put("response", botResponse);
        return response;
    }


    private String callGeminiAI(String text) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String studentData = "No student data available.";

        if (studentRepository.count() > 0) {
            java.util.List<Student> allStudents = studentRepository.findAll();

            StringBuilder sb = new StringBuilder();
            int count = 0;

            for (Student s : allStudents) {
                if (count >= 100)
                    break;

                sb.append(String.format("[%s: GPA=%.1f, Risk=%s], ",
                        s.getName(),
                        s.getCurrentGpa(),
                        s.getRiskLevel()));

                count++;
            }
            studentData = sb.toString();
        }

        String systemContext = "You are EduBot. Use this student database to answer: " + studentData
                + ". Keep it short.";
        String fullPrompt = systemContext + "\nUser: " + text;

        Map<String, Object> part = Map.of("text", fullPrompt);
        Map<String, Object> content = Map.of("parts", new Object[] { part });
        Map<String, Object> requestBody = Map.of("contents", new Object[] { content });

        String jsonBody = mapper.writeValueAsString(requestBody);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getGeminiUrl()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode rootNode = mapper.readTree(response.body());

        if (rootNode.has("error")) {
            return "API Error: " + rootNode.get("error").get("message").asText();
        }

        if (rootNode.has("candidates") && rootNode.get("candidates").isArray()) {
            JsonNode candidate = rootNode.get("candidates").get(0);
            if (candidate.has("content")) {
                return candidate.get("content").get("parts").get(0).get("text").asText();
            }
        }

        return "I couldn't analyze the data.";
    }
}
