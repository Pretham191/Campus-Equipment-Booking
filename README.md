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