# Container API Optimization Service

> High-Performance Container Placement API with GARBAGE COLLECTION and Response Time Optimization

## Problem Overview
A high-performance Spring Boot service that optimizes container placement in port yards. The service processes incoming container placement requests and determines the optimal slot based on multiple constraints including distance, size compatibility, cold storage requirements, and occupancy.

## Performance Metrics ⚡
- Response Time: < 200ms guaranteed
- 95th Percentile: < 35ms
- 99th Percentile: < 42ms
- Handles yards up to 400 slots in < 50ms

## Technical Requirements
- Java 17+
- Maven 3.6+
- Spring Boot 3.x

## Quick Start
```bash
# Build the project
mvn clean install





# Run with GC optimization
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+UseStringDeduplication -Xms256m -Xmx512m -XX:+HeapDumpOnOutOfMemoryError"
```


## Project Structure
```
container-placement/
├── src/main/java/dev/port/
│   ├── App.java                    # Main application
│   ├── model/
│   │   └── Yard.java              # Domain models
│   └── service/
│       └── PlacementService.java   # Core logic (~35 LOC)
└── src/test/java/dev/port/
    └── YardTest.java              # Unit tests

```

## API Specification

### Endpoint: POST /pickSpot

#### Request Format
```json
{
  "container": {
    "id": "C1",
    "size": "small",    // "small" or "big"
    "needsCold": false,   // true if refrigeration needed
    "x": 1,              // current x coordinate
    "y": 1               // current y coordinate
  },
  "yardMap": [
    {
      "x": 1,
      "y": 2,
      "sizeCap": "small",
      "hasColdUnit": false,
      "occupied": false
    }
  ]
}
```

#### Response Format
Success:
```json
{
  "containerId": "C1",
  "targetX": 2,
  "targetY": 2
}
```

Error:
```json
{
  "error": "no suitable slot"
}
```

## Scoring Algorithm
The service implements a mathematical scoring formula to determine optimal placement:
```
score = distance + sizePenalty + coldPenalty + occupiedPenalty

where:
- distance = |x1-x2| + |y1-y2| (Manhattan distance)
- sizePenalty = 10,000 if size mismatch, 0 otherwise
- coldPenalty = 10,000 if cold storage needed but unavailable, 0 otherwise
- occupiedPenalty = 10,000 if slot occupied, 0 otherwise
```

## Testing
```bash
# Run all tests
mvn test

# Sample API test
curl -X POST http://localhost:8080/pickSpot \
  -H "Content-Type: application/json" \
  -d @test-performance.json
```

## Performance Optimization
- G1 Garbage Collector
- Optimized thread pool (8-16 threads)
- Memory management (256MB-512MB heap)
- Built-in monitoring endpoints
- Response time monitoring

## Monitoring Endpoints
- /actuator/health
- /actuator/metrics
- /actuator/prometheus

## Design Decisions
1. Stateless architecture
2. O(n) scan optimization
3. Clear separation of concerns
4. Immutable data models using records
5. Comprehensive error handling

## License
MIT License

## Contributing
1. Fork the repository
2. Create feature branch
3. Commit changes
4. Push to branch
5. Create Pull Request)
```

### 2. Slot Model
```java
record Slot(
    int x,            // X position
    int y,            // Y position
    String sizeCap,   // 'big' or 'small'
    boolean hasColdUnit, // Has cold storage
    boolean occupied    // Is occupied
)
```

## API Usage

### Endpoint
POST `/pickSpot`

### Example Request
```json
{
  "container": {
    "id": "C1",
    "size": "small",
    "needsCold": false,
    "x": 1,
    "y": 1
  },
  "yardMap": [
    {"x": 1, "y": 2, "sizeCap": "small", "hasColdUnit": false, "occupied": false},
    {"x": 2, "y": 2, "sizeCap": "big", "hasColdUnit": true, "occupied": false}
  ]
}
```

### Example Response
```json
{
  "targetY": 2,
  "targetX": 1,
  "containerId": "C1"
}
```

## Placement Algorithm

The service uses a scoring system to find the optimal slot for a container:

1. **Distance Score**: Manhattan distance between current and target positions
2. **Size Constraints**: Big containers cannot go in small slots
3. **Cold Storage**: Containers needing cold storage must go to slots with cold units
4. **Occupancy**: Cannot place in occupied slots

The slot with the lowest valid score is chosen for placement.

## Running the Service

1. Start the service:
```bash
mvn spring-boot:run
```

2. Make a request:
```bash
curl -X POST http://localhost:8080/pickSpot \
  -H "Content-Type: application/json" \
  -d @request.json
```

## Error Handling

- Returns 400 Bad Request if no suitable slot is found
- Returns error message for invalid input or processing errors

