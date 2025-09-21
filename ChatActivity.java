// Complete Chat Activity for your disaster management APK
// This shows how to integrate the chatbot into your app's UI

package com.yourpackage.disastermanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    
    private EditText messageInput;
    private EditText locationInput;
    private Button sendButton;
    private TextView chatDisplay;
    private ProgressBar loadingBar;
    private Button englishButton;
    private Button hinglishButton;
    
    private List<String> chatHistory = new ArrayList<>();
    private String currentLanguage = "english";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        // Initialize views
        messageInput = findViewById(R.id.messageInput);
        locationInput = findViewById(R.id.locationInput);
        sendButton = findViewById(R.id.sendButton);
        chatDisplay = findViewById(R.id.chatDisplay);
        loadingBar = findViewById(R.id.loadingBar);
        englishButton = findViewById(R.id.englishButton);
        hinglishButton = findViewById(R.id.hinglishButton);
        
        // Set up click listeners
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        
        englishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage("english");
            }
        });
        
        hinglishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage("hinglish");
            }
        });
        
        // Check chatbot health on startup
        checkChatbotHealth();
        
        // Add welcome message
        addToChat("🤖 Disaster Management Chatbot", "system");
        addToChat("Ask me anything about disaster preparedness, response, or safety!", "bot");
    }
    
    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();
        
        if (message.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Add user message to chat
        addToChat(message, "user");
        
        // Clear input
        messageInput.setText("");
        
        // Show loading
        showLoading(true);
        
        // Send to chatbot
        DisasterChatbotService.sendMessage(message, location, currentLanguage, new DisasterChatbotService.ChatbotCallback() {
            @Override
            public void onSuccess(String reply) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showLoading(false);
                        addToChat(reply, "bot");
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showLoading(false);
                        addToChat("Sorry, I'm having trouble connecting. Please try again.", "error");
                        Toast.makeText(ChatActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    
    private void addToChat(String message, String sender) {
        String timestamp = java.text.DateFormat.getTimeInstance().format(new java.util.Date());
        String formattedMessage;
        
        switch (sender) {
            case "user":
                formattedMessage = "👤 You (" + timestamp + "):\n" + message;
                break;
            case "bot":
                formattedMessage = "🤖 Disaster Bot (" + timestamp + "):\n" + message;
                break;
            case "system":
                formattedMessage = "ℹ️ " + message;
                break;
            case "error":
                formattedMessage = "❌ " + message;
                break;
            default:
                formattedMessage = message;
        }
        
        chatHistory.add(formattedMessage);
        updateChatDisplay();
    }
    
    private void updateChatDisplay() {
        StringBuilder chatText = new StringBuilder();
        for (String message : chatHistory) {
            chatText.append(message).append("\n\n");
        }
        chatDisplay.setText(chatText.toString());
        
        // Scroll to bottom
        chatDisplay.post(new Runnable() {
            @Override
            public void run() {
                int scrollAmount = chatDisplay.getLayout().getLineTop(chatDisplay.getLineCount()) - chatDisplay.getHeight();
                if (scrollAmount > 0) {
                    chatDisplay.scrollTo(0, scrollAmount);
                } else {
                    chatDisplay.scrollTo(0, 0);
                }
            }
        });
    }
    
    private void showLoading(boolean show) {
        loadingBar.setVisibility(show ? View.VISIBLE : View.GONE);
        sendButton.setEnabled(!show);
    }
    
    private void setLanguage(String language) {
        currentLanguage = language;
        
        // Update button states
        if (language.equals("english")) {
            englishButton.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            hinglishButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            hinglishButton.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            englishButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        }
        
        Toast.makeText(this, "Language set to: " + language, Toast.LENGTH_SHORT).show();
    }
    
    private void checkChatbotHealth() {
        DisasterChatbotService.checkHealth(new DisasterChatbotService.HealthCallback() {
            @Override
            public void onHealthCheck(boolean isHealthy) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isHealthy) {
                            addToChat("✅ Chatbot is online and ready!", "system");
                        } else {
                            addToChat("⚠️ Chatbot is offline. Some features may not work.", "system");
                        }
                    }
                });
            }
        });
    }
    
    // Quick action buttons for common disaster scenarios
    public void onEarthquakeClick(View view) {
        messageInput.setText("What should I do during an earthquake?");
    }
    
    public void onFloodClick(View view) {
        messageInput.setText("Bhai flood aa gaya, kya karu?");
    }
    
    public void onFireClick(View view) {
        messageInput.setText("Fire safety tips please");
    }
    
    public void onEmergencyClick(View view) {
        messageInput.setText("Emergency! I need immediate help!");
    }
}
