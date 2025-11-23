# MCP Server Test Suite

This directory contains a comprehensive test suite for the Spring MCP Server using Spring AI MCP Client.

## Test Structure

The test suite is organized into four main test classes:

### 1. McpToolsIntegrationTest
Tests all MCP tools exposed by the server:
- **Calculator Tools**: add, subtract, multiply, divide
- **Utility Tools**: echo, get_current_time, random_number
- **Error Handling**: Division by zero, invalid parameters
- **Metadata Verification**: Tool descriptions and input schemas

**Test Coverage:**
- ✅ List all available tools
- ✅ Execute each tool with valid inputs
- ✅ Test error scenarios (division by zero, invalid ranges)
- ✅ Verify tool metadata and schemas
- ✅ Test optional parameters

### 2. McpResourcesIntegrationTest
Tests all MCP resources exposed by the server:
- **Static Resources**: welcome message, API reference
- **Dynamic Resources**: system info, server config
- **Parameterized Resources**: documentation by topic
- **Content Types**: text/plain, application/json, text/markdown

**Test Coverage:**
- ✅ List all available resources
- ✅ Read static resources
- ✅ Read dynamic resources
- ✅ Test parameterized resources with different topics
- ✅ Verify resource metadata (URI, name, description, mimeType)
- ✅ Validate all resources are readable

### 3. McpPromptsIntegrationTest
Tests all MCP prompts exposed by the server:
- **Simple Prompts**: greeting
- **Complex Prompts**: code-review, analyze-data, meeting-summary
- **Specialized Prompts**: debug-helper, generate-docs, sql-helper
- **Parameter Handling**: Required and optional parameters

**Test Coverage:**
- ✅ List all available prompts
- ✅ Get prompts with required parameters only
- ✅ Get prompts with all parameters (required + optional)
- ✅ Verify prompt metadata and argument schemas
- ✅ Test all 7 prompt templates

### 4. McpServerComprehensiveTest
End-to-end integration tests covering complete workflows:
- **Server Capabilities**: Verify all three MCP capabilities are exposed
- **Complex Workflows**: Multi-step operations combining tools, resources, and prompts
- **Performance**: Rapid sequential calls
- **Error Handling**: Graceful error handling across all capabilities
- **Consistency**: Metadata consistency and stateless behavior
- **Completeness**: Full API coverage verification

**Test Coverage:**
- ✅ Server exposes tools, resources, and prompts
- ✅ Complex multi-step workflows
- ✅ Rapid sequential tool calls
- ✅ Error handling across all capabilities
- ✅ Metadata consistency validation
- ✅ Parameterized resource handling
- ✅ Optional parameter handling in prompts
- ✅ Stateless protocol behavior
- ✅ Complete calculator operations
- ✅ API reference completeness

## Test Configuration

### McpTestConfiguration
Provides test-specific bean configuration:
- Configures MCP client to connect to test server
- Uses random port for test isolation
- Enables test profile

### Test Properties
Located in `src/test/resources/application-test.properties`:
- Random server port (0) for test isolation
- Debug logging for troubleshooting
- Test-specific MCP server configuration

## Running the Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
# Tools tests only
mvn test -Dtest=McpToolsIntegrationTest

# Resources tests only
mvn test -Dtest=McpResourcesIntegrationTest

# Prompts tests only
mvn test -Dtest=McpPromptsIntegrationTest

# Comprehensive tests only
mvn test -Dtest=McpServerComprehensiveTest
```

### Run Tests with Detailed Output
```bash
mvn test -X
```

### Run Tests and Generate Report
```bash
mvn test
mvn surefire-report:report
```

## Test Dependencies

The test suite uses the following dependencies:

1. **Spring Boot Test**: Core testing framework
2. **Spring AI MCP Client**: MCP client for testing server
3. **Reactor Test**: Reactive testing with StepVerifier
4. **JUnit 5**: Test framework
5. **AssertJ**: Fluent assertions

## Test Execution Flow

1. **Spring Boot Test** starts the MCP server on a random port
2. **McpClient** is configured to connect to the test server
3. **Tests execute** MCP operations via the client
4. **Assertions verify** responses match expected behavior
5. **StepVerifier** ensures reactive streams complete correctly

## Key Test Patterns

### Reactive Testing with StepVerifier
```java
StepVerifier.create(mcpClient.callTool(request))
    .assertNext(response -> {
        assertThat(response).isNotNull();
        // Additional assertions
    })
    .expectComplete()
    .verify(Duration.ofSeconds(5));
```

### Testing Tools
```java
var request = McpSchema.CallToolRequest.builder()
    .params(McpSchema.CallToolRequestParams.builder()
        .name("add")
        .arguments(Map.of("a", 5.0, "b", 3.0))
        .build())
    .build();
```

### Testing Resources
```java
var request = McpSchema.ReadResourceRequest.builder()
    .params(McpSchema.ReadResourceRequestParams.builder()
        .uri("resource://welcome")
        .build())
    .build();
```

### Testing Prompts
```java
var request = McpSchema.GetPromptRequest.builder()
    .params(McpSchema.GetPromptRequestParams.builder()
        .name("greeting")
        .arguments(Map.of("name", "Alice"))
        .build())
    .build();
```

## Test Metrics

- **Total Test Classes**: 4
- **Total Test Methods**: ~60
- **Code Coverage**: Tools (100%), Resources (100%), Prompts (100%)
- **Test Execution Time**: ~5-10 seconds

## Continuous Integration

These tests are designed to run in CI/CD pipelines:
- No external dependencies required
- Uses embedded server on random port
- Isolated test execution
- Comprehensive error handling

## Troubleshooting

### Tests Fail to Connect
- Verify server starts successfully
- Check port is not in use
- Review application-test.properties

### Timeout Errors
- Increase timeout duration in StepVerifier
- Check server performance
- Review logs for slow operations

### Assertion Failures
- Verify server implementation matches test expectations
- Check for recent changes in service classes
- Review test data and expected values

## Future Enhancements

Potential additions to the test suite:
- Performance benchmarking tests
- Stress testing with concurrent clients
- Schema validation tests
- Security and authentication tests
- WebSocket transport tests (if applicable)
- Error recovery and retry logic tests
