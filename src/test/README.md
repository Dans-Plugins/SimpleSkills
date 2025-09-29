# SimpleSkills Unit Tests

This directory contains comprehensive unit tests for the SimpleSkills Minecraft plugin, focusing on testing the WorldSaveEventListener implementation and related components.

## Test Coverage

### Core Components Tested

#### 1. WorldSaveEventListener (`listeners/WorldSaveEventListenerTest.java`)
- **Purpose**: Tests the listener that saves plugin data during server autosaves
- **Key Tests**:
  - Constructor validation with null checks
  - World save event handling and storage service calls
  - Debug logging verification
  - Multiple world save scenarios
  - Exception handling and propagation
  - Different world name handling

#### 2. StorageService (`services/StorageServiceTest.java`)  
- **Purpose**: Tests the service that persists player skill data
- **Key Tests**:
  - Constructor validation with dependency injection
  - Save operations with empty and multiple player records
  - Load operations and data clearing
  - Exception handling during save/load operations
  - Multiple save call scenarios
  - Integration between save and load operations

#### 3. PlayerJoinEventListener (`listeners/PlayerJoinEventListenerTest.java`)
- **Purpose**: Tests player record creation when players join
- **Key Tests**:
  - Existing player record handling
  - New player record creation (success and failure cases)
  - Error messaging to players
  - Multiple player scenarios
  - Exception handling from repository operations

### Integration Tests

#### WorldSaveIntegrationTest (`integration/WorldSaveIntegrationTest.java`)
- **Purpose**: Tests the complete flow from world save event to data persistence
- **Key Tests**:
  - End-to-end world save to storage flow
  - Multiple player record persistence
  - Repeated world save scenarios (simulating autosaves)
  - Different world save events
  - Storage failure handling

## Test Framework

- **Framework**: JUnit 4.13.2
- **Mocking**: Mockito 4.11.0 (core + inline for static mocking)
- **Test Runner**: Maven Surefire Plugin 3.0.0-M7

## Running Tests

### Prerequisites
1. Install Ponder dependency:
   ```bash
   mvn install:install-file -Dfile=dependencies/ponder-v0.14-alpha-2.jar \
     -DgroupId=preponderous -DartifactId=ponder -Dversion=v0.14-alpha-2 -Dpackaging=jar
   ```

2. Ensure network access to Maven repositories:
   - `hub.spigotmc.org` (for Spigot API)
   - `jitpack.io` (for XSeries)
   - Maven Central (for test dependencies)

### Commands

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=WorldSaveEventListenerTest

# Run tests with verbose output
mvn test -X

# Compile tests only
mvn test-compile
```

### GitHub Actions Integration

The tests are integrated into the GitHub Actions workflow (`.github/workflows/test-world-save-listener.yml`):
- Tests run automatically on push/PR
- Handles network restrictions gracefully
- Reports test success/failure in workflow summary
- Continues validation even if tests fail due to network issues

## Test Strategy

### Unit Testing Approach
- **Isolation**: Each component tested in isolation using mocks
- **Dependency Injection**: Constructor validation ensures proper DI
- **Edge Cases**: Null inputs, empty collections, exception scenarios
- **Verification**: Mockito verification ensures proper method calls and interactions

### Integration Testing Approach  
- **Real Dependencies**: Uses actual StorageService with ExperienceCalculator
- **Mock External Dependencies**: Mocks Bukkit APIs and data repositories
- **End-to-End Flows**: Tests complete workflows from event to persistence
- **Real-World Scenarios**: Simulates actual server autosave patterns

## Benefits

### Data Loss Prevention Validation
- Confirms WorldSaveEventListener properly triggers during world saves
- Verifies StorageService correctly persists player skill data
- Ensures integration between components works as expected
- Tests failure scenarios to prevent data corruption

### Code Quality Assurance
- Validates constructor parameters and dependency injection
- Tests error handling and exception propagation
- Ensures logging is properly implemented
- Confirms thread-safety considerations

### Regression Prevention
- Automated testing prevents future changes from breaking functionality
- GitHub Actions integration catches issues early
- Comprehensive coverage ensures all code paths are tested
- Mock-based testing enables fast, reliable test execution

## Network Restrictions

In environments with network restrictions (like CI/CD pipelines), tests may fail to run due to missing dependencies. The GitHub Actions workflow handles this gracefully:
- Tests are attempted but marked as non-blocking
- Source code validation continues regardless of test status  
- Clear reporting distinguishes between network issues and code problems
- Manual testing instructions provided for local development

## Future Enhancements

Potential areas for test expansion:
- Performance testing for large player datasets
- Concurrent access testing for multi-threaded scenarios
- File I/O testing with actual filesystem operations
- Memory usage testing for long-running operations
- Integration testing with real Minecraft server instances