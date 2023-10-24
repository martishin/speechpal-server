# SpeechPal Server

<a href="https://www.speechpal.co/"><img src="./speechpal-logo.png" width="250"/></a>

This repository hosts the backend code for the [speechpal.co](https://www.speechpal.co/) service, responsible the telegram bot and report functionalities.

## 📌 Features
- **Telegram Bot**: Interact with the SpeechPal service directly through Telegram. Check the bot [here](https://t.me/SpeechPalBot).
- **Reports**: Access comprehensive reports about speech analysis. See a [sample report](https://www.speechpal.co/reports/AgAEOQAC0ZJASw).

## 🚀 Running Locally

### Prerequisites
1. **Telegram Bot Token**: Obtain a token for your Telegram bot. See the [instructions](https://help.openai.com/en/articles/4936850-where-do-i-find-my-secret-api-key).
2. **OpenAI API Key**: You'll need this key to access OpenAI functionalities. [Here's how](https://help.openai.com/en/articles/4936850-where-do-i-find-my-secret-api-key) to get it.
3. **Databases**: Make sure you have PostgreSQL and MongoDB running locally. You can use Docker or any other method to start these services.

### Starting the server
1. In the root of the project, create a `.env` file with the following content:
   ```
   export BOT_TOKEN={YOUR_BOT_TOKEN}
   export OPENAI_API_KEY={YOUR_OPENAI_API_KEY}
   export OPENAI_ORG={YOUR_OPENAI_ORG}
   export MONGO_DB_CONNECTION={YOUR_MONGO_DB_CONNECTION}
   export MONGO_DB_DATABASE={YOUR_MONGO_DB_DATABASE}
   export POSTGRESQL_DB_CONNECTION={YOUR_POSTGRESQL_DB_CONNECTION}
   export POSTGRESQL_DB_USERNAME={YOUR_POSTGRESQL_DB_USERNAME}
   export POSTGRESQL_DB_PASSWORD={YOUR_POSTGRESQL_DB_PASSWORD}
   ```
   Replace placeholders (like {YOUR_BOT_TOKEN}) with your actual values.
2. Load your environment variables:
   `source .env`
3. To start the server, run:
  `./gradlew bootRun`
