package com.flashquest.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AIGenerator {

    private static String apiKey;
    
    // Using your preferred model
    private static final String AI_MODEL = "gemini-2.5-flash"; 

    public static void loadApiKey() {
        try {
            apiKey = Files.readString(Paths.get("C:\\dev\\flashquest_key.txt")).trim();
            System.out.println("✅ AI Generator: API Key loaded successfully.");
        } catch (IOException e) {
            System.err.println("Failed to load API key. Please create 'C:\\dev\\flashquest_key.txt'");
            apiKey = null;
        }
    }

    public static String generateQuestions(String topic, int numQuestions, String difficulty, String quizType) {
        if (apiKey == null) {
            System.err.println("AI Generator: Cannot run, API Key is missing.");
            return null; 
        }

        System.out.println("AI Generator: Generating " + numQuestions + " " + difficulty + " " + quizType + " questions for topic: " + topic);
        String prompt = buildPrompt(topic, numQuestions, difficulty, quizType, null);
        String jsonBody = "{" +
                "  \"contents\": [{" +
                "    \"parts\": [{" +
                "      \"text\": \"" + prompt + "\"" +
                "    }]" +
                "  }]" +
                "}";

        return sendRequestAndParseResponse(jsonBody);
    }
    
    public static String generateQuestionsFromText(String fileText, int numQuestions, String difficulty, String quizType) {
        if (apiKey == null) {
            System.err.println("AI Generator: Cannot run, API Key is missing.");
            return null; 
        }

        System.out.println("AI Generator: Generating " + numQuestions + " " + difficulty + " " + quizType + " questions from file text.");
        String truncatedText = fileText;
        if (fileText.length() > 2000) {
            truncatedText = fileText.substring(0, 2000);
        }
        
        String escapedText = truncatedText
            .replace("\\", "\\\\") 
            .replace("\"", "\\\"") 
            .replace("\n", "\\n"); 

        String prompt = buildPrompt(null, numQuestions, difficulty, quizType, escapedText);
        String jsonBody = "{" +
                "  \"contents\": [{" +
                "    \"parts\": [{" +
                "      \"text\": \"" + prompt + "\"" +
                "    }]" +
                "  }]" +
                "}"; // The 'H' typo is also fixed here

        return sendRequestAndParseResponse(jsonBody);
    }
    
    // --- THIS IS THE FINAL 'buildPrompt' METHOD ---
    private static String buildPrompt(String topic, int numQuestions, String difficulty, String quizType, String fileText) {
        
        String basePrompt = "You are a quiz generation bot. Generate " + numQuestions + " " + difficulty + " " + quizType + " questions";
        String context = "";
        String requestedTopic; // This will be either the topic or "FileQuiz"
        
        if (fileText != null) {
            // Context for file upload
            context = " *based ONLY on the content of the following text*:\n\n" +
                      "--- BEGIN TEXT --- \n" + fileText + "\n--- END TEXT ---\n\n";
            requestedTopic = "FileQuiz";
        } else {
            // Context for topic
            context = " about " + topic + ". ";
            requestedTopic = topic;
        }

        String formatInstructions = "";
        String example = "";
        
        // --- THIS IS THE FIX ---
        // The format instructions AND the example now BOTH use the 'requestedTopic'
        // This removes all confusion for the AI.
        switch (quizType) {
            case "True/False":
                formatInstructions = "For each question, provide the correct answer as 'True' or 'False'. " +
                                     "Format the response ONLY as a single string with this exact format, using ~ as a separator: " +
                                     requestedTopic + "|Question1~Answer1|Question2~Answer2";
                example = "\n\nHERE IS AN EXAMPLE:\n" + requestedTopic + "|The Eiffel Tower is in France.~True|The sky is green.~False";
                break;
                
            case "Identification":
                formatInstructions = "For each question, provide the correct answer as a single word or short phrase. " +
                                     "Format the response ONLY as a single string with this exact format, using ~ as a separator: " +
                                     requestedTopic + "|Question1~Answer1|Question2~Answer2";
                example = "\n\nHERE IS AN EXAMPLE:\n" + requestedTopic + "|What is the chemical symbol for water?~H2O|What is the closest planet to the Sun?~Mercury";
                break;
                
            default: // "Multiple-Choice"
                formatInstructions = "For each question, provide 4 options (A, B, C, D) and the correct answer index (0, 1, 2, or 3). " +
                                     "Format the response ONLY as a single string with this exact format, using ~ as a separator: " +
                                     requestedTopic + "|Question1~OptionA~OptionB~OptionC~OptionD~CorrectIndex|Question2~OptionA~OptionB~OptionC~OptionD~CorrectIndex";
                example = "\n\nHERE IS AN EXAMPLE:\n" + requestedTopic + "|What sound does a cat make?~Meow~Woof~Moo~Oink~0|What do cats like to chase?~Mice~Cars~Clouds~Rocks~0";
                break;
        }
        
        String finalPrompt = basePrompt + context + formatInstructions + example;
        return finalPrompt;
    }
    
    // --- This method has ALL the cleaning fixes ---
    private static String sendRequestAndParseResponse(String jsonBody) {
        String response = HttpUtils.sendPostRequest(apiKey, AI_MODEL, jsonBody);
        
        if (response == null) {
            return null; 
        }
        
        try {
            String textStart = "\"text\": \"";
            int start = response.indexOf(textStart) + textStart.length();
            int end = response.indexOf("\"", start);
            String aiText = response.substring(start, end);
            
            // 1. Replace "\\n"
            String cleanedText = aiText.replace("\\n", "");
            // 2. Replace actual newlines
            cleanedText = cleanedText.replaceAll("\\r\\n|\\r|\\n", "");
            // 3. Replace escaped pipes '\|' (from log image_d9c5dd.png)
            cleanedText = cleanedText.replace("\\|", "|");
            // 4. Replace escaped tildes '\~'
            cleanedText = cleanedText.replace("\\~", "~");
            
            System.out.println("AI Generator: Successfully generated quiz.");
            return cleanedText; 
            
        } catch (Exception e) {
            System.err.println("AI Generator: Failed to parse AI response: " + response);
            e.printStackTrace();
            return null;
        }
    }
}