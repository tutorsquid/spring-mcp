# Spring MCP Server

A Model Context Protocol (MCP) server implementation using Spring Boot. This server provides AI assistants with standardized access to tools and resources through the MCP protocol.

## Overview

The Model Context Protocol (MCP) is an open protocol that enables AI assistants to securely access external data sources and tools. This implementation uses Spring Boot to create a robust, production-ready MCP server.

## Features

- **JSON-RPC 2.0 Protocol**: Full implementation of MCP using JSON-RPC 2.0
- **RESTful API**: HTTP endpoint for MCP communication
- **Built-in Tools**: Sample tools including calculator, echo, time, and random number generator
- **Resource Support**: Example resource implementation
- **Spring Boot Integration**: Leverages Spring Boot's dependency injection, configuration, and logging
- **Spring Boot Actuator**: Production-ready health checks and monitoring endpoints
- **Extensible Architecture**: Easy to add new tools and resources

## Prerequisites

- Java 17 or higher
- Maven 3.6+

## Quick Start

### 1. Build the project

```bash
mvn clean install
```

### 2. Run the server

```bash
mvn spring-boot:run
```

The server will start on `http://localhost:8080`

### 3. Test the server

Check server health:
```bash
curl http://localhost:8080/actuator/health
```

Initialize the MCP connection:
```bash
curl -X POST http://localhost:8080/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "initialize",
    "params": {
      "protocolVersion": "2024-11-05",
      "capabilities": {},
      "clientInfo": {
        "name": "test-client",
        "version": "1.0.0"
      }
    },
    "id": 1
  }'
```

List available tools:
```bash
curl -X POST http://localhost:8080/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "tools/list",
    "params": {},
    "id": 2
  }'
```

Call a tool:
```bash
curl -X POST http://localhost:8080/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "tools/call",
    "params": {
      "name": "calculator",
      "arguments": {
        "operation": "add",
        "a": 5,
        "b": 3
      }
    },
    "id": 3
  }'
```

## Available Tools

### 1. Calculator
Performs basic arithmetic operations (add, subtract, multiply, divide).

**Parameters:**
- `operation`: "add" | "subtract" | "multiply" | "divide"
- `a`: number
- `b`: number

### 2. Echo
Echoes back the provided message.

**Parameters:**
- `message`: string

### 3. Get Current Time
Returns the current date and time.

**Parameters:**
- `timezone`: string (optional)

### 4. Random Number
Generates a random number between min and max.

**Parameters:**
- `min`: number
- `max`: number

## Project Structure

```
src/
├── main/
│   ├── java/com/example/mcpserver/
│   │   ├── McpServerApplication.java        # Main application
│   │   ├── controller/
│   │   │   └── McpController.java           # REST controller
│   │   ├── service/
│   │   │   ├── McpService.java              # MCP protocol handler
│   │   │   └── ToolService.java             # Tool implementations
│   │   └── model/
│   │       ├── JsonRpcRequest.java          # Request model
│   │       ├── JsonRpcResponse.java         # Response model
│   │       ├── JsonRpcError.java            # Error model
│   │       ├── Tool.java                    # Tool definition
│   │       ├── Resource.java                # Resource definition
│   │       ├── ServerInfo.java              # Server info
│   │       └── InitializeResult.java        # Initialize result
│   └── resources/
│       └── application.properties           # Configuration
└── test/
    └── java/com/example/mcpserver/          # Tests
```

## Adding New Tools

To add a new tool, modify `ToolService.java`:

1. Create a tool definition method:
```java
private Tool createMyTool() {
    Map<String, Object> properties = new HashMap<>();
    properties.put("param1", Map.of(
        "type", "string",
        "description", "Parameter description"
    ));

    Map<String, Object> inputSchema = new HashMap<>();
    inputSchema.put("type", "object");
    inputSchema.put("properties", properties);
    inputSchema.put("required", List.of("param1"));

    return Tool.builder()
            .name("my_tool")
            .description("Tool description")
            .inputSchema(inputSchema)
            .build();
}
```

2. Add it to the available tools list in `getAvailableTools()`

3. Implement the execution logic:
```java
private Object executeMyTool(Map<String, Object> arguments) {
    String param1 = (String) arguments.get("param1");
    // Implementation logic
    return "Result";
}
```

4. Add the case to the switch statement in `executeTool()`

## Configuration

Edit `src/main/resources/application.properties`:

```properties
# Server port
server.port=8080

# Logging level
logging.level.com.example.mcpserver=DEBUG

# Actuator endpoints
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized
```

## MCP Protocol Methods

The server supports the following MCP methods:

- `initialize`: Initialize the MCP connection
- `tools/list`: List all available tools
- `tools/call`: Execute a tool
- `resources/list`: List all available resources
- `resources/read`: Read a resource

## Actuator Endpoints

Spring Boot Actuator provides production-ready monitoring and management endpoints:

### Health Endpoint

Check the application health status:
```bash
curl http://localhost:8080/actuator/health
```

Response:
```json
{
  "status": "UP"
}
```

### Available Actuator Endpoints

- `/actuator/health` - Application health information
- `/actuator/info` - Application information (if configured)

To expose additional endpoints, modify `application.properties`:
```properties
management.endpoints.web.exposure.include=health,info,metrics
```

## Building for Production

Create a production-ready JAR:

```bash
mvn clean package
```

Run the JAR:

```bash
java -jar target/spring-mcp-server-1.0.0.jar
```

## Docker Support

Create a `Dockerfile`:

```dockerfile
FROM eclipse-temurin:17-jre
COPY target/spring-mcp-server-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

Build and run:

```bash
docker build -t spring-mcp-server .
docker run -p 8080:8080 spring-mcp-server
```

## Development

### Running Tests

```bash
mvn test
```

### Code Style

This project uses standard Java code conventions and Spring Boot best practices.

## License

MIT License

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Resources

- [Model Context Protocol Documentation](https://modelcontextprotocol.io)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [MCP Specification](https://spec.modelcontextprotocol.io)