# Employee Service

A Spring Boot RESTful web service for managing employee data.

## Table of Contents

- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Building the Application](#building-the-application)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Docker Deployment](#docker-deployment)
- [Kubernetes Deployment](#kubernetes-deployment)

## Overview

Employee Service is a simple REST API service built with Spring Boot that provides basic employee management functionality. The service allows you to retrieve a list of employees and query employee information by ID.

## Technology Stack

- **Java 21**
- **Spring Boot 4.1.0**
- **Maven**
- **Spring Web MVC**
- **Spring Boot Actuator**

## Prerequisites

Before running this application, ensure you have the following installed:

- JDK 21 or later
- Maven 3.6+
- Docker (optional, for containerized deployment)
- Kubernetes CLI (optional, for K8s deployment)

## Building the Application

Clone the repository and build the project using Maven:

```bash
# Build the project
./mvnw clean package

# Or using system Maven
mvn clean package
```

## Running the Application

### Using Maven

```bash
./mvnw spring-boot:run
```

### Using JAR file

```bash
java -jar target/employee-service-0.0.1-SNAPSHOT.jar
```

The application will start on port **8085**.

## API Endpoints

Once the application is running, you can access the following endpoints:

### Get All Employees

```http
GET http://localhost:8085/employees
```

**Response:**
```json
["Nikita", "Rahul", "Amit"]
```

### Get Employee by ID

```http
GET http://localhost:8085/employees/{id}
```

**Response:**
```
Employee ID : {id}
```

**Example:**
```bash
curl http://localhost:8085/employees/1
```

## Testing

Run the unit tests using:

```bash
./mvnw test
```

## Docker Deployment

Build and run the Docker container:

```bash
# Build Docker image
docker build -t employee-service:latest .

# Run Docker container
docker run -p 8085:8085 employee-service:latest
```

### Using Docker Compose

```bash
docker-compose up
```

## Kubernetes Deployment

Kubernetes configuration files are located in the `k8s/` directory:

```bash
# Apply all Kubernetes configurations
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

## CI/CD Pipeline

The project includes multiple Jenkinsfile configurations for different CI/CD scenarios:

- `Jenkinsfile` - Standard pipeline
- `Jenkinsfile_Junittest` - Pipeline with unit tests
- `Jenkinsfile_SonarQube` - Pipeline with SonarQube integration
- `Jenkinsfile_params` - Parameterized pipeline

