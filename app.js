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
  let systemPrompt = "You are a disaster management assistant specifically for India. Only answer questions related to disaster preparedness, response, recovery, and safety in the Indian context. Always provide India-specific emergency numbers, protocols, and advice. Use Indian emergency services like 112 (National Emergency Number), 100 (Police), 101 (Fire), 102 (Ambulance), 108 (Emergency Response), NDRF, SDRF, and local Indian authorities. Mention Indian government schemes, Indian Red Cross, and India-specific disaster management protocols. If a question is not about disaster management, politely refuse.";
  if (lang === 'hinglish') {
    systemPrompt += " Reply in Hinglish (mix of Hindi and English) using Indian context and terminology.";
  } else {
    systemPrompt += " Reply in English but with Indian context, emergency numbers, and protocols.";
  }
  if (location) {
    systemPrompt += ` The user's location is: ${location}, India. Provide location-specific advice for this Indian state/city.`;
  } else {
    systemPrompt += " Assume the user is in India and provide India-specific advice.";
  }

  // Call Gemini API (or mock for now)
  let reply = "";
  try {
    if (process.env.GEMINI_API_KEY) {
      const geminiRes = await axios.post(
        `https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=${process.env.GEMINI_API_KEY}`,
        {
          contents: [{
            parts: [{
              text: `${systemPrompt}\n\nUser: ${message}`
            }]
          }]
        },
        {
          headers: {
            'Content-Type': 'application/json'
          }
        }
      );
      reply = geminiRes.data.candidates[0].content.parts[0].text.trim();
    } else if (process.env.OPENAI_API_KEY) {
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
      // Demo/mock reply for local dev - India specific
      if (message.toLowerCase().includes('earthquake')) {
        reply = lang === 'hinglish'
          ? "Bhai, earthquake ke time Drop-Cover-Hold karo! 112 pe call karo emergency ke liye. NDRF aur local authorities ko inform karo."
          : "During an earthquake: Drop, Cover, and Hold! Call 112 for emergencies. Contact NDRF and local Indian authorities. Stay away from buildings and go to open areas.";
      } else if (message.toLowerCase().includes('flood')) {
        reply = lang === 'hinglish'
          ? "Flood mein paani se door raho bhai! 112 ya 108 call karo. SDRF aur local disaster management team ko contact karo. High ground pe jao."
          : "During floods: Stay away from flood water! Call 112 or 108. Contact SDRF and local disaster management teams. Move to higher ground immediately.";
      } else if (message.toLowerCase().includes('fire')) {
        reply = lang === 'hinglish'
          ? "Fire emergency mein 101 call karo! Fire brigade ko inform karo. Smoke se bachne ke liye neeche crawl karo."
          : "Fire emergency: Call 101 for Fire Services! Inform local fire brigade. Crawl low to avoid smoke inhalation.";
      } else if (message.toLowerCase().includes('cyclone') || message.toLowerCase().includes('storm')) {
        reply = lang === 'hinglish'
          ? "Cyclone alert mein IMD ki warnings suno! 112 call karo. Ghar ke andar raho, windows band karo."
          : "Cyclone alert: Follow IMD (Indian Meteorological Department) warnings! Call 112. Stay indoors, close all windows and doors.";
      } else {
        reply = lang === 'hinglish'
          ? "Main India ke disaster management ke baare mein help kar sakta hu! 112 - National Emergency, 100 - Police, 101 - Fire, 102 - Ambulance. Kuch bhi pooch sakte ho!"
          : "I can help with Indian disaster management! Emergency numbers: 112 - National Emergency, 100 - Police, 101 - Fire, 102 - Ambulance, 108 - Emergency Response. Ask me anything about disaster preparedness in India!";
      }
    }

  } catch (err) {
    console.error('Error processing request:', err.message);
    console.error('Error details:', err.response?.data);
    
    // Check if the error is from API
    if (err.response?.status === 401) {
      reply = "Error: Invalid API key. Please check the key is set up correctly in Render environment variables.";
    } else if (err.response?.status === 404) {
      reply = "Error: API endpoint not found. Please check the API key and deployment settings.";
    } else if (err.response?.status === 400) {
      reply = "Error: Bad request to API. Please check the request format.";
    } else {
      reply = "Sorry, there was an error processing your request. Please try again later.";
    }
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
