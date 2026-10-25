# Car Park API

A REST API for managing a car park. It lets you:

- check how many spaces are available and occupied
- park a vehicle, which allocates a space and records its arrival time
- bill a vehicle on exit, which calculates the charge and frees its space

## Prerequisites
- Java 21 (JDK)

## Running the application
The application starts on http://localhost:8080.

### Mac / Linux
```sh
./gradlew bootRun
```

### Windows
```bat
gradlew bootRun
```

## Testing
```sh
./gradlew test
```

## Assumptions
- The car park has 50 spaces.
- A registration is 1 to 7 characters. Spaces count as characters.
- Registrations are case-sensitive: `aaa` and `AAA` are two different vehicles.
- Making the bill request means the vehicle has left the car park, so its space is freed.
- Charges are in pounds.
- Times are in UTC.

## Next steps
I have left the application in its current state. My next immediate steps would be:

- **Swagger documentation** so the API can be explored in a browser.
- **Clearer error responses.** `404` and `409` responses currently return Spring's default body without the reason (for example "Car park is full"). I'd return a consistent body for every error.
- **Logging** of key events expanded