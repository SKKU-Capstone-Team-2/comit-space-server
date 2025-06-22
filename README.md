# Backend Server Project

This document provides a quick overview of the backend server application, including how to set it up, run it, and understand its basic file structure.

---

## 1. Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK) 17 or higher**: The project is built with Spring Boot, which typically requires a modern JDK.
- **Gradle 8.x**: The project uses Gradle as its build automation tool.

---

## 2. Getting Started

Follow these steps to get the backend server up and running:

### Clone the Repository

```bash
git clone <your-repository-url>
cd comit-space-server  # Or your project's root directory name
```

### Build and Run the Application (using Gradle Wrapper)

Navigate to the project's root directory (where `build.gradle` is located) in your terminal.

#### Run directly (for development)

```bash
./gradlew bootRun
```

This command compiles the project and starts the Spring Boot application.

#### Build a JAR file (for deployment)

```bash
./gradlew build
```

This command will create an executable JAR file in the `build/libs` directory (e.g., `comitserver-0.0.1-SNAPSHOT.jar`).

#### Run the built JAR file

```bash
java -jar build/libs/comitserver-0.0.1-SNAPSHOT.jar
```

The application will typically start on `http://localhost:8080` (or the port configured in `src/main/resources/application.properties`).

---

## 3. Environment Configuration

To securely manage database credentials and connection settings, this project uses a `.env` file.

### 📄 `.env` File

Create a `.env` file in the root directory with the following content (replace with your own credentials):

```env
DATABASE_URL="jdbc:mysql://<your-host>:<port>/<your-database>"
DATABASE_USERNAME="<your-username>"
DATABASE_PASSWORD="<your-password>"
```

> 🔒 **Note**:  
> The `.env` file is **excluded from version control** via `.gitignore`, so you must **create it manually** on every development or deployment machine.

---

## 4. Project Structure

The project follows a standard Spring Boot application structure. Below is a simplified overview of the key directories and their purposes:

```
.
├── .git/                      # Git version control files
├── .gradle/                   # Gradle's build cache and wrapper files
├── .idea/                     # IntelliJ IDEA project files (can be ignored)
├── build/                     # Output directory for build artifacts (compiled classes, JARs)
├── gradle/
│   └── wrapper/               # Gradle Wrapper files
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/comitserver/
│   │   │       ├── config/              # Spring configuration classes
│   │   │       ├── controller/          # REST API endpoints
│   │   │       ├── dto/                 # DTOs for requests/responses
│   │   │       ├── entity/              # JPA Entities and enums
│   │   │       ├── exception/           # Custom exception classes
│   │   │       ├── jwt/                 # JWT filter/util classes
│   │   │       ├── repository/          # Spring Data JPA repositories
│   │   │       ├── service/             # Business logic layer
│   │   │       └── utils/               # Utility classes
│   │   └── resources/
│   │       ├── application.properties   # App config (DB, port, etc.)
│   │       └── data.sql                 # Optional seed data script
│   └── test/                  # Unit and integration tests
├── .gitignore                 # Specifies files to be ignored by Git
├── .env                       # Environment variables (⚠️ not tracked by Git)
├── build.gradle               # Gradle build script
├── gradlew                    # Gradle Wrapper (Unix)
├── gradlew.bat                # Gradle Wrapper (Windows)
└── settings.gradle            # Gradle project settings
```

This structure aligns with the **MVC (Model-View-Controller)** pattern and Spring Boot's best practices.