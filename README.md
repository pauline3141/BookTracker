Matrikelnummer: 1745652

# BookTracker

A full-stack web application for cataloging and managing personal book collections. Users can browse popular titles, search the Open Library catalog, organize books into custom shelves, track reading progress, and annotate individual entries with notes. Books form a shared catalog, while each user's shelves, reading progress, and notes are private and secured through JWT-based authentication.

### Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | React 19, TypeScript, Vite |
| Backend | Java 21, Spring Boot 4 |
| Database | H2 (development), PostgreSQL (Docker) |
| Authentication | Spring Security, JWT |
| API | REST + GraphQL |
| External API | Open Library (Search + Subjects API) |
| Build Tool | Maven |
| Containerization | Docker, Docker Compose |
| CI/CD | GitHub Actions |

## Architecture

The application follows a three-tier architecture. The React frontend communicates exclusively with the backend over its REST and GraphQL APIs. The Spring Boot backend holds the business logic, persists data in a relational database via Spring Data JPA, and enriches the application by calling the external Open Library API.

```
┌──────────┐      API     ┌──────────┐      API     ┌────────────────┐
│ FRONTEND │ ◄──────────► │ BACKEND  │ ◄──────────► │ Open Library   │
│ (React)  │              │ (Spring  │              │ (External API) │
└──────────┘              │  Boot)   │              └────────────────┘
                          │    │     │
                          │    ▼     │
                          │ ┌──────┐ │
                          │ │  DB  │ │
                          │ └──────┘ │
                          └──────────┘
```

The backend is organized in a strict layered architecture: controllers handle HTTP and GraphQL, services contain the business logic, and Spring Data repositories handle persistence. Request and response DTOs decouple the API from the JPA entities.

**Data model:** Books form a shared catalog. When a user adds a book, the backend reuses an existing catalog entry with the same ISBN instead of creating a duplicate. Shelves belong to individual users, and each shelf entry links a catalog book to a shelf together with that user's reading progress and notes. As a result, the same book can appear on many users' shelves without being duplicated, and books are never removed when a shelf is deleted. Full CRUD is provided for shelves, shelf entries, and notes; the book catalog is read-and-add only, so individual users cannot modify or delete catalog entries that others may be using.

## Features

- Public discover page showing popular books from a random subject, with a search bar to find any book (no login required)
- Search books via the Open Library API with pagination
- User registration and login secured with JWT
- A shared book catalog: adding a book reuses an existing entry with the same ISBN instead of creating a duplicate
- Create and manage private, user-scoped shelves, with a cover-image preview thumbnail per shelf
- Add books to shelves and track reading progress
- Move books between shelves
- Add and delete personal notes on book entries, with an optional page reference
- REST API documented with Swagger UI
- GraphQL endpoint as an alternative to REST, with queries and mutations

## Prerequisites

- Java 21
- Node.js 20+
- Maven
- Docker Desktop (for the Docker setup)

## Running Locally

### Option 1: Without Docker (H2 in-memory database)

**Backend:**
```bash
./mvnw spring-boot:run
```

**Frontend**:
```bash
cd frontend
npm install
npm run dev
```

- Frontend: http://localhost:5173
- Backend: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- GraphiQL: http://localhost:8080/graphiql
- H2 Console: http://localhost:8080/h2-console

The discover page and book search are available without an account. Navigate to `/register` to create an account before adding books to shelves. Shelves, reading progress, and notes are private to the logged-in user, while the book catalog is shared across all users.

### Option 2: With Docker (PostgreSQL database)

The backend reads its JWT signing secret from a `JWT_SECRET` environment variable. Before starting the stack, create a `.env` file next to `docker-compose.yml`:

```
JWT_SECRET=booktracker-secret-key-must-be-at-least-32-characters-long
POSTGRES_DB=booktracker
POSTGRES_USER=booktracker
POSTGRES_PASSWORD=booktracker
```

`JWT_SECRET` must be at least 32 characters long. Then start the stack:

```bash
docker compose up --build
```

- Frontend: http://localhost:80
- Backend: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

To stop without losing data:
```bash
docker compose down
```

To stop and delete all data:
```bash
docker compose down -v
```

## Authentication

The API is secured with JWT. After registering or logging in via `/api/auth/register` or `/api/auth/login`, the response contains a token that must be sent as a Bearer token in the Authorization header for all subsequent requests:

```
Authorization: Bearer <token>
```

A demo account is seeded automatically on first startup, so you can log in immediately without registering: username `demo`, password `demo123`.

The frontend handles this automatically once logged in. Two endpoints are intentionally public and do not require a token: `GET /api/books/search` and `GET /api/books/discover`, since they only read from the external Open Library API and never expose user data.

In the Swagger UI, use the **Authorize** button to paste a token and try the protected endpoints directly.

## GraphQL

In addition to the REST API, a GraphQL endpoint is available at `/graphql` (authenticated the same way as REST, via the Authorization header). It supports:

- **Queries:** `shelves`, `shelf(id)`, `searchBooks(query, offset)`
- **Mutations:** `createShelf(name, description)`

Example query:
```graphql
query {
  shelves {
    id
    name
    entries {
      book { title author }
      currentPage
      totalPages
    }
  }
}
```

GraphiQL is available at `/graphiql` for interactive testing.

## Running Tests

```bash
./mvnw test
```

The test suite includes 43 tests across unit tests (services, mocked with Mockito) and integration tests (controllers, tested with MockMvc), covering successful flows, validation failures, not-found and conflict cases, ISBN-based deduplication, user-scoped access control, and cascading delete behavior.

## External API

This project uses the [Open Library API](https://openlibrary.org/dev/docs/api).

- **Search endpoint:** `https://openlibrary.org/search.json`: used to search books by title, author, or ISBN; the `fields` parameter is used to request ISBNs so catalog deduplication can work
- **Subjects endpoint:** `https://openlibrary.org/subjects/{subject}.json`: used to power the discover page with popular books from a randomly chosen subject (fiction, fantasy, mystery, science, romance, history, biography, thriller, poetry, philosophy)
- **Authentication:** None required
- **Resilience:** Requests are retried automatically with exponential backoff (up to 3 attempts) for transient (5xx and network) errors, while 4xx client errors are not retried; failures are mapped to a 502 Bad Gateway response.

## Project Structure

```
BookTracker/
├── src/                          # Spring Boot backend
│   ├── main/java/.../
│   │   ├── config/                 # App config, DataSeeder, OpenAPI config
│   │   ├── controller/              # REST and GraphQL controllers
│   │   ├── dto/                     # Request/Response records
│   │   ├── exception/               # Custom exceptions, GlobalExceptionHandler
│   │   ├── mapper/                  # Entity to DTO mappers
│   │   ├── model/                   # JPA entities (Book, Shelf, ShelfEntry, BookNote, User)
│   │   ├── repository/              # Spring Data repositories
│   │   ├── security/                # JWT filter, JwtUtil, SecurityConfig
│   │   └── service/                 # Business logic
│   ├── main/resources/
│   │   ├── graphql/schema.graphqls  # GraphQL schema
│   │   └── application*.properties
│   └── test/                       # Unit and integration tests
├── frontend/                      # React frontend
│   ├── src/
│   │   ├── api/                  # Typed API layer (fetch-based)
│   │   ├── components/           # Reusable components
│   │   ├── pages/                # Page components (Discover, Search, Shelves, Login/Register)
│   │   └── types.ts              # Domain types
│   ├── Dockerfile
│   └── nginx.conf
├── Dockerfile                    # Backend Dockerfile
├── docker-compose.yml            # Full stack: backend + frontend + PostgreSQL
├── .github/workflows/ci.yml      # CI pipeline (build + test on push)
└── README.md
```

## Note on AI Assistance

AI tools were used to assist with the visual design of the frontend (CSS styling, layout, and component appearance).