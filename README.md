# MirageDB

**MirageDB** is a developer tool that reads your data models (Java, TypeScript, C#, or SQL) and uses Gemini AI to generate realistic, relational SQL mock data with perfect foreign-key integrity.

## Features
- **Language Agnostic**: Paste your raw entity classes or DDL scripts; the AI figures out the schema.
- **Smart Relations**: Automatically maps and respects primary/foreign key constraints across multiple tables.
- **Context-Aware Data**: Generates realistic mock data based on your variable names (e.g., `user_email` actually looks like an email).
- **Instant SQL**: Output is raw, ready-to-execute SQL insert statements.

## Tech Stack
- **Backend**: Java 21, Spring Boot 3, Google Gemini 2.5 Flash API
- **Frontend**: React, Tailwind CSS (Rapidly prototyped using Creao AI)

## Quick Start

### 1. Backend (Spring Boot)
**Requirements**: Java 21 and Maven. You also need a Google Gemini API Key.

```bash
git clone https://github.com/mahmoud-40/MirageDB.git
cd MirageDB/backend
```

Open `src/main/resources/application.properties` and add your key:
```
gemini.api.key=YOUR_API_KEY
```

Run the server:
```bash
mvn spring-boot:run
```

### 2. Frontend (React)
**Requirements**: Node.js and npm.

```bash
# Open a new terminal
cd MirageDB/frontend
npm install
npm run dev
```

Open [http://localhost:3000](http://localhost:3000) (or the port shown in your terminal).

## How to Use
1. Paste your data models (e.g., a Java `User` class and `Post` class) into the left pane.
2. Select how many rows you want.
3. Click **Generate SQL**.
4. Copy the generated relational inserts from the right pane and paste them into your database.

---

**Built by**: Mahmoud Abdulmawlaa