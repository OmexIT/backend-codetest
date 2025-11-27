# Seed Bag Counts (Java Take‑Home)

This take‑home mirrors the JS coding challenge but in Java. You will build the bag counting logic and ensure all tests pass. The core logic is intentionally left as a stub for you to implement.

## What to implement
- Complete `GetBagCounts.getBagCounts` (currently throws `UnsupportedOperationException`) to calculate how many seed bags of each available size are needed per the provided requirements (see inline Javadoc).
- Keep the contract: larger bags preferred, include all sizes, support half bags, reject orders not in 0.5kg increments or that cannot be fulfilled with the provided bag sizes.
- Feel free to add small helper methods or classes if they improve clarity.

## Project layout
```
pom.xml
src/
  main/java/com/example/codetest/
    BagCount.java
    GetBagCounts.java
  test/java/com/example/codetest/
    GetBagCountsTest.java
```

## Prerequisites
- Java 17+
- Maven 3.8+ (or wrapper if you add one)

## Running tests
```bash
mvn test
```

## Expectations
- Keep code clear and small; no frameworks needed.
- Add/adjust tests if you change behavior or to cover edge cases you identify.
- Handle invalid inputs defensively with meaningful exceptions.

## Bonus ideas (optional)
- Extend tests with more scenarios (e.g., different bag sets, multiple clients, unfulfillable orders).
- Wrap the logic in a minimal REST API (Spring Boot) if you want to showcase API skills.
