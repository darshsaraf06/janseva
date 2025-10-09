# JanSeva - Civic Issue Reporting System

A Spring Boot application for reporting and tracking civic issues with AI-powered image classification.

## Features

- Report civic issues with photos and location
- AI-powered issue type detection (mock implementation)
- Interactive map with issue markers
- MySQL database integration
- RESTful API

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Google Maps API key (for frontend)

## Setup Instructions

### 1. Database Setup

1. Create MySQL database:
```sql
CREATE DATABASE janseva_db;
```

2. Run the SQL script to create tables:
```bash
mysql -u root -p janseva_db < janseva_db.sql
```

### 2. Configuration

1. Update `src/main/resources/application.properties` with your MySQL credentials:
```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

2. Update the Google Maps API key in `index.html`:
```javascript
const backendUrl = 'http://localhost:8080';
```

### 3. Running the Application

1. Compile and run the Spring Boot application:
```bash
mvn clean compile
mvn spring-boot:run
```

2. Open `index.html` in your web browser to access the frontend.

## API Endpoints

- `GET /api/complaints` - Get all complaints
- `POST /api/complaints` - Create a new complaint

## Project Structure

```
src/
├── main/
│   ├── java/com/janseva/
│   │   ├── controller/     # REST controllers
│   │   ├── model/         # JPA entities
│   │   ├── repository/    # Data repositories
│   │   ├── service/       # Business logic
│   │   └── dto/          # Data transfer objects
│   └── resources/
│       └── application.properties
├── index.html            # Frontend application
└── app.html             # Alternative frontend
```

## Technologies Used

- Spring Boot 3.2.0
- Spring Data JPA
- MySQL
- Google Maps API
- Bootstrap 5
- HTML5/JavaScript

## Notes

- The AI image classification is currently mocked with random issue types
- For production use, integrate with Google Cloud Vision API
- Add proper authentication and user management
- Implement file upload to cloud storage (AWS S3/Google Cloud Storage)
