# 🚀 Deployment Guide for Code-Red Chatbot Backend

## 📱 APK Integration Ready!

Your chatbot backend is perfectly designed for integration with your disaster management APK. Here's everything you need:

---

## 🌐 Render Deployment

### Step 1: Deploy to Render

1. **Push your code to GitHub** (if not already done)
2. **Go to [Render.com](https://render.com)** and sign up/login
3. **Create a new Web Service:**
   - Connect your GitHub repository
   - Choose "Web Service"
   - Use these settings:
     - **Build Command:** `npm install`
     - **Start Command:** `npm start`
     - **Environment:** `Node`
     - **Plan:** Free (or paid for production)

4. **Set Environment Variables:**
   - `OPENAI_API_KEY`: Your OpenAI API key (optional)
   - `NODE_ENV`: `production`

5. **Deploy!** Render will give you a URL like: `https://your-app-name.onrender.com`

---

## 📱 APK Integration Guide

### API Endpoints for Your APK

**Base URL:** `https://your-app-name.onrender.com`

#### 1. Health Check
```http
GET https://your-app-name.onrender.com/
```
**Response:** `"Code-Red Chatbot Backend is running!"`

#### 2. Chat Message
```http
POST https://your-app-name.onrender.com/api/message
Content-Type: application/json

{
  "message": "What should I do during an earthquake?",
  "location": "Delhi",
  "language": "english"
}
```

**Response:**
```json
{
  "reply": "Bot's response here"
}
```

### Android Integration Code Examples

#### Java/Kotlin (Android)
```java
// Using Retrofit or OkHttp
public class ChatbotService {
    private static final String BASE_URL = "https://your-app-name.onrender.com/";
    
    public void sendMessage(String message, String location, String language, Callback<ChatResponse> callback) {
        ChatRequest request = new ChatRequest(message, location, language);
        // Make HTTP POST request to BASE_URL + "api/message"
    }
}

class ChatRequest {
    String message;
    String location;
    String language;
    
    public ChatRequest(String message, String location, String language) {
        this.message = message;
        this.location = location;
        this.language = language;
    }
}

class ChatResponse {
    String reply;
}
```

#### Flutter/Dart
```dart
import 'package:http/http.dart' as http;
import 'dart:convert';

class ChatbotService {
  static const String baseUrl = 'https://your-app-name.onrender.com';
  
  static Future<String> sendMessage(String message, {String? location, String? language}) async {
    final response = await http.post(
      Uri.parse('$baseUrl/api/message'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'message': message,
        'location': location,
        'language': language,
      }),
    );
    
    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return data['reply'];
    } else {
      throw Exception('Failed to get response');
    }
  }
}
```

---

## 🔧 Features Your APK Can Use

### 1. **Multi-Language Support**
- **English**: `"language": "english"`
- **Hinglish**: `"language": "hinglish"`
- **Auto-detect**: Don't specify language, bot will auto-detect

### 2. **Location-Aware Responses**
- Include user's location for localized disaster advice
- Example: `"location": "Mumbai"`

### 3. **Disaster Management Focus**
- Bot only responds to disaster-related queries
- Refuses non-disaster questions politely

### 4. **Error Handling**
- Proper HTTP status codes
- JSON error responses
- Graceful fallbacks

---

## 🛠️ Testing Your Deployment

### Test Commands
```bash
# Health check
curl https://your-app-name.onrender.com/

# English message
curl -X POST https://your-app-name.onrender.com/api/message \
  -H 'Content-Type: application/json' \
  -d '{"message":"What should I do during an earthquake?","location":"Delhi","language":"english"}'

# Hinglish message
curl -X POST https://your-app-name.onrender.com/api/message \
  -H 'Content-Type: application/json' \
  -d '{"message":"Bhai flood aa gaya, kya karu?","location":"Delhi","language":"hinglish"}'
```

---

## 🔐 Security & Best Practices

### For Production APK:
1. **Use HTTPS** (Render provides this automatically)
2. **Add API rate limiting** (consider upgrading Render plan)
3. **Implement user authentication** if needed
4. **Add request validation** in your APK
5. **Handle network errors** gracefully

### Environment Variables:
- Keep your OpenAI API key secure
- Use Render's environment variable system
- Never commit API keys to code

---

## 📊 Monitoring & Analytics

### Render Dashboard:
- Monitor uptime and performance
- View logs and errors
- Track usage metrics

### For Your APK:
- Track chatbot usage
- Monitor response times
- Log user interactions (privacy-compliant)

---

## 🚀 Ready to Deploy!

Your chatbot backend is:
- ✅ **Production-ready**
- ✅ **APK integration-friendly**
- ✅ **Multi-language supported**
- ✅ **Disaster management focused**
- ✅ **Render deployment configured**

**Next Steps:**
1. Deploy to Render using the guide above
2. Update your APK with the new API endpoint
3. Test thoroughly with your disaster management scenarios
4. Monitor and iterate based on user feedback

---

**Need help?** The chatbot is designed to be simple and reliable. All the hard work is done - just deploy and integrate! 🎉
