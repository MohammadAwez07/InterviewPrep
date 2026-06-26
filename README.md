# Interview Prep App

A full-stack interview preparation platform for backend engineers.

## Tech Stack

- **Backend**: Java 21 · Spring Boot 3.x · Spring Security (JWT) · JPA/Hibernate · PostgreSQL · Redis · Flyway
- **Frontend**: Next.js 14 · TypeScript · TanStack Query · Monaco Editor · ReactFlow · Tailwind CSS
- **Infrastructure**: Docker Compose (PostgreSQL 16, Redis 7)
- **AI**: OpenAI GPT-4o for mock interview code evaluation

## Features

| Feature | Status |
|---|---|
| DSA Problem Bank (50+ problems) | ✅ |
| Monaco Code Editor | ✅ |
| Flashcard System (SM-2 spaced repetition, 200+ cards) | ✅ |
| Mock Interview with AI feedback | ✅ |
| Study Planner (target-date algorithm) | ✅ |
| System Design Whiteboard (ReactFlow) | ✅ |
| Progress Dashboard (streak, heatmap) | ✅ |
| JWT Auth (register/login) | ✅ |

## Quick Start

### Prerequisites
- Java 21+, Maven 3.9+
- Node.js 20+, npm 10+
- Docker + Docker Compose

### 1. Environment
```bash
```

### 2. Start Infrastructure
```bash
docker-compose up -d postgres redis
```

### 3. Backend
```bash
cd backend
mvn clean install -DskipTests
mvn spring-boot:run
# API available at http://localhost:8080
```

### 4. Frontend
```bash
cd frontend
npm install
npm run dev
# App available at http://localhost:3000
```

### Or run everything with Docker
```bash
docker-compose up --build
```

## API Endpoints

| Method | Path | Description |
|---|---|---|
| POST | /api/auth/register | Register |
| POST | /api/auth/login | Login |
| GET | /api/problems | List problems (filter by topic/difficulty) |
| GET | /api/problems/:slug | Get problem detail |
| POST | /api/problems/:slug/submit | Submit attempt |
| GET | /api/flashcards/due | Get due flashcards (SM-2) |
| POST | /api/flashcards/:id/review | Review with rating 1-5 |
| GET | /api/progress/dashboard | Dashboard data |
| POST | /api/planner/generate | Generate study plan |
| GET | /api/planner/active | Get active plan |
| POST | /api/mock/sessions | Start mock interview |
| POST | /api/mock/sessions/:id/submit | Submit code for AI evaluation |
| GET/POST/PUT/DELETE | /api/design/sessions | System design sessions |

## Architecture

```
interview-prep-app/
├── backend/                  # Spring Boot 3 application
│   ├── src/main/java/com/interviewprep/
│   │   ├── auth/             # JWT authentication
│   │   ├── problems/         # DSA problem bank
│   │   ├── flashcards/       # SM-2 spaced repetition
│   │   ├── progress/         # Dashboard & streak
│   │   ├── planner/          # Study schedule generator
│   │   ├── design/           # System design sessions
│   │   └── mock/             # Mock interview + OpenAI
│   └── src/main/resources/
│       └── db/migration/     # Flyway SQL migrations
├── frontend/                 # Next.js 14 app
│   └── src/app/
│       ├── dashboard/
│       ├── problems/
│       ├── flashcards/
│       ├── planner/
│       ├── design/
│       └── mock/
└── docker-compose.yml
```
