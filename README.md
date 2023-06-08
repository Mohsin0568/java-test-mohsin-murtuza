# java-test-mohsin-murtuza

## Purpose
Effortlessly schedule conflict-free meetings with our intelligent scheduler service. Our platform integrates with your calendar, checks for conflicts, and considers out-of-office schedules. Say goodbye to double bookings and coordination headaches. Simplify meeting planning and maximize attendance with our user-friendly solution. Sign up now for stress-free scheduling!

## Requirements
- [JDK 11](https://www.oracle.com/uk/java/technologies/javase/jdk11-archive-downloads.html)


## Running the application locally

```
./mvnw spring-boot:run
```

## Running the test cases locally

```
./mvnw test
```

## Execute file processor service locally

```
curl -v -F file=@<file-name> http://localhost:8080/v1/meetings/process
```
