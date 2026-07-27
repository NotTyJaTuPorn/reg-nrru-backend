# AGENTS.md — Onboarding for AI coding agents

Checklist for the agent (do these steps before editing code):
- Read `src/main/java/com/nrru/registration` package root to learn naming conventions
- Inspect `application.yml` and `src/main/resources/db/migration/V1__init_schema.sql` for DB expectations
- Run local build and tests: `./gradlew bootRun` / `gradlew.bat bootRun` (Windows PowerShell) and `gradlew.bat test`

Quick architecture summary
- Spring Boot application (main class: `RegNrruBackendApplication`). Package root: `com.nrru.registration`.
- Typical layers: controller -> service -> repository -> entity. DTOs live in `dto` and shared ApiResponse objects are used for results.
- Security: JWT-based auth implemented in `config/JwtUtil.java`, `config/JwtAuthenticationFilter.java`, and wired by `config/SecurityConfig.java`.

Key patterns and where to look (concrete examples)
- Controllers: `controller/*` — REST endpoints under `/api/*`. Examples: `AuthController` (`/api/auth`) and `CourseController` (`/api/courses`).
- Services: `service/*` — business logic and transactions. Example: `EnrollmentService.enrollStudent(...)` uses `@Transactional` and performs multi-step checks (student lookup, prerequisite check, locking).
- Repositories: `repository/*` extend `JpaRepository`. Look for custom queries and locking annotations. Example: `CourseRepository.findByIdWithLock(...)` uses `@Lock(LockModeType.PESSIMISTIC_WRITE)` to prevent concurrent over-enrollment.
- Entities: `entity/*` use Lombok (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`). Expect typical JPA mappings and table names (e.g., `@Table(name = "courses")`).

Important integration points
- Database: Postgres configured in `src/main/resources/application.yml` (jdbc:postgresql://localhost:5432/registration_db). DB migrations live in `src/main/resources/db/migration/V1__init_schema.sql`.
- Authentication: JWT created/verified in `JwtUtil` and applied per-request by `JwtAuthenticationFilter` (extends `OncePerRequestFilter`). Agents modifying auth should update both util and filter.
- Concurrency: Enrollment flow uses DB-level pessimistic locking and `@Transactional` service methods. When changing enrollment logic, preserve locking patterns to avoid race conditions.

Developer workflows and commands
- Build & run (Windows PowerShell):
  - ./gradlew.bat build
  - ./gradlew.bat bootRun
  - java -jar build/libs/reg-nrru-backend-0.0.1-SNAPSHOT.jar
- Test: `./gradlew.bat test` (uses standard JUnit tests found under `src/test/java`).
- Debug: set run configuration to `RegNrruBackendApplication` main class in IDE; pass env vars or `--spring.profiles.active` as needed.

Project-specific conventions
- DTO and response pattern: use `dto/ApiResponse` for controller responses and `dto/EnrollRequest` for enrollment input.
- Services follow `XxxService` names and are constructor-injected. Prefer calling service methods from controllers rather than repos directly.
- Repositories: custom queries are placed in repository interfaces (e.g., `CoursePrerequisiteRepository.findPrerequisiteCourseIds(...)`). Don't duplicate query logic into services; call repository methods.
- Localization / comments: some inline comments are in Thai (e.g., `EnrollmentService`), so expect bilingual context in code comments.

Where to confidently make changes
- Non-security business logic: `service/*` and `controller/*` are safe places to add endpoints and orchestration. Respect transactions and repository contracts.
- DB schema changes: update `src/main/resources/db/migration/V1__init_schema.sql` and application.yml DB config together; tests rely on schema.

Risks and red flags for agents
- Changing authentication: updating token format requires coordinated changes to `JwtUtil`, filter, and `AuthController`.
- Concurrency and enrollment: removing `@Lock` or `@Transactional` wrapping from enrollment flow will introduce race conditions.
- Lombok usage: entities rely on Lombok annotations; generated getters/setters are assumed elsewhere.

Pointers for common tasks
- Add a new REST endpoint: create controller under `controller/`, add service in `service/`, persist via `repository/` and entity in `entity/`.
- Add DB migration: update `src/main/resources/db/migration` with a new versioned SQL file and ensure `application.yml` points to the expected DB.
- Change security scopes: modify `SecurityConfig.java` and test with real token generation via `AuthController`.

Useful files (short list)
- Main: `src/main/java/com/nrru/registration/RegNrruBackendApplication.java`
- Config: `src/main/java/com/nrru/registration/config/{AppConfig,JwtUtil,JwtAuthenticationFilter,SecurityConfig}.java`
- Example service logic: `src/main/java/com/nrru/registration/service/EnrollmentService.java`
- Repository with locking: `src/main/java/com/nrru/registration/repository/CourseRepository.java`
- DB migration: `src/main/resources/db/migration/V1__init_schema.sql`
- Application config: `src/main/resources/application.yml`

If you (the agent) need to make a change, include in the PR description: which files changed, migration version added (if DB changed), and a short justification for any concurrency/security adjustments.

---
This file was generated by an automated codebase scan. For clarification or to expand this guide, open an issue or ask for a deeper walkthrough of a specific subsystem (auth, enrollment, or DB migrations).

