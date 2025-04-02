# CodeSpark - Backend

The backend service for **CodeSpark**, a gamified Java learning platform for students.  
This Spring Boot application handles user authentication, lesson management, AI-powered evaluations, badge achievements, and more.

## 🚀 Features

- 📚 Lesson system with multiple types
- 🤖 AI-based answer evaluation using OpenAI
- 🧠 Progress tracking and lesson skipping
- 🏆 Badge system for gamification
- 🔐 JWT-based authentication

## 📦 Tech Stack

- Java 21 + Spring Boot
- PostgreSQL
- OpenAI
- Cloudinary
- Docker

## 🛠️ Setup

### Prerequisites

- Java 21
- Maven
- PostgreSQL setup
- Cloudinary account
- SMTP credentials

### Environment Variables

These must be provided via environment variables:

| Key                             | Description                       |
|---------------------------------|-----------------------------------|
| `DATABASE_URL`                  | JDBC connection string            |
| `DATABASE_USERNAME`             | DB username                       |
| `DATABASE_PASSWORD`             | DB password                       |
| `JWT_SECRET_KEY`                | Secret for token signing          |
| `SMTP_ACCOUNT_EMAIL`            | SMTP account email                |
| `SMTP_ACCOUNT_PASSWORD`         | SMTP password                     |
| `OPENAI_API_KEY`                | OpenAI API Key                    |
| `OPENAI_API_PROJECT_ID`         | OpenAI Project ID                 |
| `OPENAI_API_ORGANIZATION_ID`    | OpenAI Org ID                     |
| `CLOUDINARY_URL`                | Cloudinary API URL                |

## 🧪 Running Tests

Use the `test` Spring profile:

```bash
mvn test -Dspring.profiles.active=test
```

## 🤝 Related Repositories

* [Frontend](https://github.com/CodeSparkApp/Frontend) — Vaadin-based frontend
* [Organization Overview](https://github.com/CodeSparkApp)

## 📜 License

Distributed under the `Attribution-NonCommercial-ShareAlike 4.0 International`. See `LICENSE` for more information.
