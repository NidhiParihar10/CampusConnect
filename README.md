
# CampusConnect

A Java-based campus peer help exchange system where students can request help, share skills, and collaborate with other students.

## Features

- Student registration and login
- Password hashing and authentication
- Student skill management
- Create, update, delete, and search help requests
- Skill-based helper matching
- Helper assignment, acceptance, and rejection
- Request status tracking
- Student dashboard and helper leaderboard
- Advanced search and filtering
- Input validation and exception handling
- Automated unit testing with JUnit 5

## Technologies Used

- Java
- JDBC
- SQLite
- SQL
- JUnit 5
- Git and GitHub

## Request Workflow

OPEN → ASSIGNED → IN_PROGRESS → RESOLVED

If a helper rejects a pending assignment, the request returns to OPEN.

## Project Structure

- CampusConnect.java — Main console application
- Student.java — Student model
- StudentDAO.java — Student database operations
- HelpRequest.java — Help request model
- HelpRequestDAO.java — Help request database operations
- AuthDAO.java — Authentication
- SkillDAO.java — Skill persistence
- StudentDashboardDAO.java — Student statistics
- HelperLeaderboardDAO.java — Helper ranking
- InputValidator.java — Input validation
- *Test.java — JUnit tests

## Running Locally

Requirements:
- Java JDK compatible with the project's source code
- SQLite JDBC driver and its required dependencies

Compile (Windows PowerShell):

    javac -cp ".;*" *.java

Run:

    java -cp ".;*" CampusConnect

Note: This project currently uses SQLite and requires database tables to be initialized before first use. Downloaded JAR dependencies and the local database are excluded from Git.

## Testing

JUnit 5 is used for automated unit tests covering student information, help request state transitions, and input validation.

## Future Improvements

- Spring Boot REST API
- React.js frontend
- Web-based authentication
- Improved API-level authorization
- Deployment

## Project Status

Console application completed. Web application development planned.
