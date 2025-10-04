# Unit Tests for Auto-Save Feature

This directory contains unit tests for the auto-save functionality implemented to prevent data loss during server crashes.

## Test Files

### PlayerRecordAutoSaveTest.java
Tests the core auto-save functionality in the `PlayerRecord` class:

- **testPlayerRecordCreation**: Verifies PlayerRecord instances are created correctly
- **testSetExperienceTriggersAutoSave**: Validates that setting experience values triggers auto-save
- **testSetSkillLevelTriggersAutoSave**: Validates that setting skill levels triggers auto-save
- **testIncrementExperienceTriggersAutoSave**: Tests that incrementing experience triggers auto-save
- **testSetSkillLevelsTriggersAutoSave**: Tests bulk skill level updates trigger auto-save
- **testThrottlingMechanismExists**: Verifies the 5-second throttling mechanism is in place
- **testStorageServiceInjection**: Confirms StorageService is properly injected
- **testPlayerRecordWithNullStorageService**: Ensures graceful handling when StorageService is null
- **testMultipleDataModifications**: Tests multiple data changes are preserved
- **testSaveMethodExists**: Verifies the save() method works
- **testLoadMethodPreservesData**: Tests data persistence through save/load cycle

### PlayerRecordRepositoryAutoSaveTest.java
Tests the integration between `PlayerRecordRepository` and auto-save:

- **testRepositoryCreation**: Verifies repository initialization
- **testSetStorageServiceMethod**: Tests StorageService injection method
- **testStorageServiceInjection**: Validates StorageService is passed to PlayerRecord instances
- **testCreatePlayerRecordWithoutStorageService**: Tests graceful degradation without StorageService
- **testGetPlayerRecord**: Verifies player record retrieval
- **testGetNonExistentPlayerRecord**: Tests null handling for missing records

### StorageServiceAutoSaveTest.java
Tests the `StorageService` integration:

- **testStorageServiceCreation**: Verifies StorageService initialization
- **testSaveMethodExists**: Confirms save() method functionality
- **testLoadMethodExists**: Confirms load() method functionality

## Running Tests

### Via Maven
```bash
mvn test
```

### Via Maven with specific test
```bash
mvn test -Dtest=PlayerRecordAutoSaveTest
```

### Via GitHub Actions CI
Tests run automatically on:
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop` branches

See `.github/workflows/test.yml` for CI configuration.

## Test Coverage

The tests cover:
1. ✅ Auto-save triggers on all data modification methods
2. ✅ Throttling mechanism (5-second cooldown)
3. ✅ Dependency injection (StorageService into PlayerRecord)
4. ✅ Graceful error handling (null StorageService)
5. ✅ Data persistence through save/load cycle
6. ✅ Repository integration

## Note on Async Testing

Some tests verify the logic is in place but cannot fully test async save behavior due to Bukkit scheduler requirements. The actual async save functionality is tested through:
1. Integration tests with Docker (see main README)
2. Manual testing on live servers
3. The unit tests verify the mechanism is properly wired up

## Dependencies

- JUnit 5.9.3
- Mockito 4.11.0
- Maven Surefire Plugin 2.22.2
