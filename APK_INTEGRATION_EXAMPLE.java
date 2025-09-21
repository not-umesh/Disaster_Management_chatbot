// Example Android/Java integration for your disaster management APK
// Replace YOUR_RENDER_URL with your actual Render deployment URL

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DisasterChatbotService {
    
    // Replace with your Render deployment URL
    private static final String BASE_URL = "https://your-app-name.onrender.com";
    private static final String TAG = "DisasterChatbot";
    
    public interface ChatbotCallback {
        void onSuccess(String reply);
        void onError(String error);
    }
    
    public static void sendMessage(String message, String location, String language, ChatbotCallback callback) {
        new SendMessageTask(callback).execute(message, location, language);
    }
    
    private static class SendMessageTask extends AsyncTask<String, Void, String> {
        private ChatbotCallback callback;
        
        public SendMessageTask(ChatbotCallback callback) {
            this.callback = callback;
        }
        
        @Override
        protected String doInBackground(String... params) {
            String message = params[0];
            String location = params[1];
            String language = params[2];
            
            try {
                URL url = new URL(BASE_URL + "/api/message");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                
                // Set up the request
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                
                // Create JSON payload
                JSONObject jsonPayload = new JSONObject();
                jsonPayload.put("message", message);
                if (location != null && !location.isEmpty()) {
                    jsonPayload.put("location", location);
                }
                if (language != null && !language.isEmpty()) {
                    jsonPayload.put("language", language);
                }
                
                // Send the request
                OutputStream os = connection.getOutputStream();
                os.write(jsonPayload.toString().getBytes());
                os.flush();
                os.close();
                
                // Read the response
                int responseCode = connection.getResponseCode();
                BufferedReader br;
                if (responseCode == 200) {
                    br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                }
                
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();
                
                if (responseCode == 200) {
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    return jsonResponse.getString("reply");
                } else {
                    return "Error: " + responseCode + " - " + response.toString();
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error sending message", e);
                return "Network error: " + e.getMessage();
            }
        }
        
        @Override
        protected void onPostExecute(String result) {
            if (result.startsWith("Error:") || result.startsWith("Network error:")) {
                callback.onError(result);
            } else {
                callback.onSuccess(result);
            }
        }
    }
}

// Usage example in your Activity/Fragment:
/*
DisasterChatbotService.sendMessage(
    "What should I do during an earthquake?", 
    "Mumbai", 
    "english",
    new DisasterChatbotService.ChatbotCallback() {
        @Override
        public void onSuccess(String reply) {
            // Update your UI with the chatbot response
            textViewResponse.setText(reply);
        }
        
        @Override
        public void onError(String error) {
            // Handle error - show error message to user
            textViewResponse.setText("Sorry, chatbot is unavailable: " + error);
        }
    }
);
*/
