# Campus Equipment Booking Service
Java 17 / Spring Boot REST API for a CI/CD lab assessment.

## Build and test
`mvn clean test`

## Run
`mvn spring-boot:run` — port 8080.

## Endpoints
- `GET /api/equipment`
- `GET /api/equipment/{id}`
- `POST /api/bookings`
- `GET /api/bookings/{id}`
- `POST /api/bookings/{id}/cancel`
- `GET /actuator/health`

Inventory is in memory and resets on restart. No database is required.

# Campus Equipment Booking

## Overview

Campus Equipment Booking is a Spring Boot REST API for managing equipment inventory and bookings. It supports equipment lookup, booking creation, booking retrieval, and cancellation.

This project addresses the Set G change request: preventing sequential booking requests from collectively reserving more units than the available inventory.

## Technology Stack

- Java 17
- Spring Boot
- Maven
- Git
- Jenkins
- Docker
- Kubernetes
- Minikube

## Build and Test

Run the automated tests:

```bash
mvn clean test
```

Package the application:

```bash
mvn clean package
```

The packaged application JAR is generated in the `target/` directory.

## Maven Build and Testing

### Prerequisites
- Java Development Kit (JDK) 17
- Apache Maven 3.6 or later

### Verify Installation

Check the installed Java and Maven versions:

```bash
java -version
javac -version
mvn -version
```

**Note:** The project requires Java 17. Ensure Maven uses a compatible JDK, particularly when running builds through Jenkins.

### Run Unit Tests

Execute the following command to clean the previous build outputs and run the unit tests:

```bash
mvn clean test
```

The tests validate application functionality, including:
- Successful equipment bookings.
- Rejection of zero or negative quantities.
- Rejection of bookings exceeding available inventory.
- Acceptance of bookings matching the available inventory.
- Inventory updates after successful bookings.
- Sequential booking validation to prevent overbooking.
- Inventory restoration when bookings are cancelled.

### Package the Application

Generate the application JAR:

```bash
mvn clean package
```

The generated JAR is available in the `target/` directory.

### Troubleshooting

**Error: `release version 17 not supported`**

This error occurs when Maven uses a Java compiler that does not support Java 17.

To resolve it:

1. Ensure JDK 17 is installed.
2. Verify the Java compiler version using `javac -version`.
3. Check the Java version Maven uses with `mvn -version`.
4. Configure Jenkins to use JDK 17 for pipeline builds.
5. Rerun the pipeline after correcting the Java configuration.

Jenkins must use a compatible JDK even if Java 17 works correctly in the local terminal.

## Docker

### Build the image

```bash
docker build -t campus-equipment-booking:set-g .
```

### Run the container

The following command maps host port 8081 to container port 8080. Use another available host port if necessary.

```bash
docker run -d --name campus-equipment-booking -p 8081:8080 campus-equipment-booking:set-g
```

### Verify application health

```bash
curl -i http://localhost:8081/actuator/health
```

A healthy application should return HTTP 200 with a response containing `"status":"UP"`.

## Kubernetes Deployment

Ensure Minikube is running:

```bash
minikube status
```

Make the Docker image available to Minikube:

```bash
minikube image load campus-equipment-booking:set-g
```

Apply the Kubernetes Deployment and Service:

```bash
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
```

Wait for the Deployment to become ready:

```bash
kubectl rollout status deployment/campus-equipment-booking
```

Inspect the Deployment, Pods, and Service:

```bash
kubectl get deployments
kubectl get pods -o wide
kubectl get services
```

### Access the health endpoint through Kubernetes

Start port forwarding:

```bash
kubectl port-forward service/campus-equipment-booking 8082:8080
```

In another terminal, check application health:

```bash
curl -i http://localhost:8082/actuator/health
```

The expected healthy response contains `"status":"UP"`.

### Scale to two replicas

```bash
kubectl scale deployment campus-equipment-booking --replicas=2
kubectl rollout status deployment/campus-equipment-booking
kubectl get deployments
kubectl get pods -o wide
```

Verify that the Deployment reports two ready replicas.

## Set G Change Request

### Defect

The original booking validation compared the requested quantity against the available inventory plus one. This off-by-one error allowed a request for one additional unit beyond the available inventory.

### Correction

The capacity validation was corrected so that a request is rejected whenever its quantity exceeds the currently available inventory. The requested quantity must also be positive.

### Automated Tests

The tests verify that:

1. A booking equal to the available inventory is accepted.
2. A request exceeding available inventory is rejected.
3. A rejected request does not change inventory or create a booking.
4. Multiple sequential bookings within the inventory limit are accepted.
5. A subsequent booking that exceeds the remaining inventory is rejected without changing the remaining quantity.

## Evidence Checklist

Capture or retain evidence of:

- Git branch, commit history, and final working-tree status.
- Maven test results.
- Docker image build and image tag.
- Running Docker container and health-check response.
- Kubernetes Deployment, Pods, and Service.
- Health-check response through the Kubernetes Service.
- Successful scaling to two replicas.