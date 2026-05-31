# SIT305 Task 8.1 Chatbot App

Native Android chatbot app for the SIT305 Task 8.1 assessment.

## Features

- Username login screen matching the supplied blue wireframe.
- Chat interface with user and chatbot message bubbles.
- Timestamp shown on every message bubble.
- Room/SQLite persistence for previous conversations per username.
- OpenAI-compatible LLM backend through `https://api.openai.com/v1/chat/completions`.
- Offline fallback replies when no API key is configured.

## LLM Setup

Copy `local.properties.example` to `local.properties`, then update the SDK path and API key:

```properties
OPENAI_API_KEY=your_api_key_here
OPENAI_MODEL=gpt-4o-mini
```

`OPENAI_MODEL` is optional. If no API key is supplied, the app still runs and stores chats, but chatbot replies explain that live AI needs a key. Do not commit `local.properties` because it contains machine-specific paths and secrets.

## Build

Open the folder in Android Studio and run the `app` configuration, or build from the command line with a local JDK/Gradle setup.

The verified debug APK is generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```
