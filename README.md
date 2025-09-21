# Code-Red Chatbot Backend (REST API + Hinglish Support)

This is a REST API backend for the Code-Red disaster management chatbot, designed for in-app integration (no WhatsApp required). It supports both English and Hinglish (Hindi-English mix) responses.

---

## Features

- **/api/message** endpoint for chat integration
- Hinglish and English support (auto-detect or explicit)
- Ready for deployment on Render, Heroku, etc.
- Easy to extend and maintain
- Clear documentation and code comments

---

## Quick Start

1. **Install dependencies:**
   ```bash
   npm install
   ```

2. **Set your OpenAI API key (optional, for real AI replies):**
   - Create a `.env` file:
     ```
     OPENAI_API_KEY=your_openai_key_here
     ```
   - If not set, the bot will reply with a demo message.

3. **Run the server:**
   ```bash
   npm start
   ```

4. **Test the API:**
   ```bash
   curl -X POST http://localhost:3000/api/message \
     -H 'Content-Type: application/json' \
     -d '{"message":"Bhai flood aa gaya, kya karu?","location":"Delhi","language":"hinglish"}'
   ```

---

## API Usage

### POST `/api/message`

**Request body:**
```json
{
  "message": "Your question here",
  "location": "Optional location",
  "language": "english" | "hinglish" // optional, auto-detected if not provided
}
```

**Response:**
```json
{
  "reply": "Bot's answer here"
}
```

---

## Deployment

- Deploy to [Render](https://render.com/), Heroku, or any NodeJS host.
- Set your `OPENAI_API_KEY` as an environment variable for real AI-powered answers.

---

## For Developers & AI Assistants

- All main logic is in `app.js`.
- To add more languages, extend the `isHinglish` function and prompt logic.
- For disaster data integration, add modules or API calls as needed.

---

## License

MIT

---

**Maintained by:** umesh + AI assistant
