// Code-Red Chatbot Backend (REST API + Hinglish support)
// To run: npm install && npm start

const express = require('express');
const cors = require('cors');
const axios = require('axios');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(express.json());

// Utility: Detect Hinglish (very basic, can be improved)
function isHinglish(text) {
  // If text contains common Hindi words in Latin script, treat as Hinglish
  const hindiWords = ['hai', 'kya', 'nahi', 'kar', 'par', 'ka', 'ki', 'se', 'mein', 'ho', 'raha', 'gaya', 'bhai', 'aap', 'kyun', 'kyunki', 'sab', 'toh', 'ab', 'tum', 'hum', 'yeh', 'woh'];
  return hindiWords.some(word => text.toLowerCase().includes(word));
}

// Main REST endpoint
app.post('/api/message', async (req, res) => {
  const { message, location, language } = req.body;

  if (!message) {
    return res.status(400).json({ error: 'Message is required.' });
  }

  // Determine language: use explicit param or auto-detect
  let lang = language;
  if (!lang) {
    lang = isHinglish(message) ? 'hinglish' : 'english';
  }

  // Compose prompt for LLM (OpenAI, etc.)
  let systemPrompt = "You are a disaster management assistant. Only answer questions related to disaster preparedness, response, recovery, and safety. If a question is not about disaster management, politely refuse.";
  if (lang === 'hinglish') {
    systemPrompt += " Reply in Hinglish (mix of Hindi and English).";
  } else {
    systemPrompt += " Reply in English.";
  }
  if (location) {
    systemPrompt += ` The user's location is: ${location}.`;
  }

  // Call OpenAI API (or mock for now)
  let reply = "Sorry, the chatbot backend is not connected to OpenAI yet.";
  try {
    if (process.env.OPENAI_API_KEY) {
      const openaiRes = await axios.post(
        'https://api.openai.com/v1/chat/completions',
        {
          model: 'gpt-3.5-turbo',
          messages: [
            { role: 'system', content: systemPrompt },
            { role: 'user', content: message }
          ],
          max_tokens: 256,
          temperature: 0.7
        },
        {
          headers: {
            'Authorization': `Bearer ${process.env.OPENAI_API_KEY}`,
            'Content-Type': 'application/json'
          }
        }
      );
      reply = openaiRes.data.choices[0].message.content.trim();
    } else {
      // Demo/mock reply for local dev
      reply = lang === 'hinglish'
        ? "Bhai, yeh ek demo reply hai. Disaster management se related kuch bhi pooch sakte ho!"
        : "This is a demo reply. You can ask anything related to disaster management!";
    }
  } catch (err) {
    reply = "Sorry, there was an error processing your request.";
    console.error(err);
  }

  res.json({ reply });
});

// Health check
app.get('/', (req, res) => {
  res.send('Code-Red Chatbot Backend is running!');
});

app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
