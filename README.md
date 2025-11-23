# Spring AI MCP Server

A Model Context Protocol (MCP) server implementation using **Spring AI** and Spring Boot. This server provides AI assistants with standardized access to tools and resources through the MCP protocol with minimal custom development.

## Overview

The Model Context Protocol (MCP) is an open protocol that enables AI assistants to securely access external data sources and tools. This implementation leverages **Spring AI's MCP Boot Starter** to create a production-ready MCP server with automatic tool registration, SSE transport support, and zero boilerplate code.

## Features

- **Spring AI MCP Integration**: Built on `spring-ai-mcp-server-webflux-spring-boot-starter`
- **Annotation-Driven Tools**: Use `@McpTool` annotations for automatic tool registration
- **WebFlux SSE Transport**: Server-Sent Events (SSE) support with reactive Spring WebFlux
- **Zero Boilerplate**: No manual JSON-RPC handling or callback registration
- **Built-in Tools**: Calculator (add, subtract, multiply, divide), echo, time, and random number
- **Spring Boot Actuator**: Production-ready health checks and monitoring endpoints
- **Auto-Configuration**: Spring AI handles all MCP protocol details automatically

## Architecture

```
┌─────────────────────────────────────────┐
│   Spring Boot Application               │
│  ┌────────────────────────────────────┐ │
│  │  @McpTool Annotated Methods        │ │
│  │  (McpToolsService)                 │ │
│  └────────────────────────────────────┘ │
│              ↓                           │
│  ┌────────────────────────────────────┐ │
│  │  Spring AI MCP Server              │ │
│  │  (Auto-configured)                 │ │
│  └────────────────────────────────────┘ │
│              ↓                           │
│  ┌────────────────────────────────────┐ │
│  │  WebFlux SSE Endpoint (/mcp)       │ │
│  └────────────────────────────────────┘ │
└─────────────────────────────────────────┘
              ↓ HTTP SSE
      ┌──────────────────┐
      │   MCP Client     │
      │   (Claude, etc)  │
      └──────────────────┘
```

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

The server will start on `http://localhost:8080` with:
- MCP SSE endpoint at `/mcp`
- Actuator health endpoint at `/actuator/health`

### 3. Test the server

Check server health:
```bash
curl http://localhost:8080/actuator/health
```

Response:
```json
{
  "status": "UP"
}
```

## Available Tools

The server automatically exposes all methods annotated with `@McpTool`:

### 1. Add
Adds two numbers together.

**Parameters:**
- `a`: First number (required)
- `b`: Second number (required)

### 2. Subtract
Subtracts second number from first number.

**Parameters:**
- `a`: First number (required)
- `b`: Second number (required)

### 3. Multiply
Multiplies two numbers.

**Parameters:**
- `a`: First number (required)
- `b`: Second number (required)

### 4. Divide
Divides first number by second number.

**Parameters:**
- `a`: Numerator (required)
- `b`: Denominator, must not be zero (required)

### 5. Echo
Echoes back the provided message.

**Parameters:**
- `message`: The message to echo back (required)

### 6. Get Current Time
Returns the current date and time.

**Parameters:**
- `timezone`: Timezone (optional, defaults to system timezone)

### 7. Random Number
Generates a random number between min and max (inclusive).

**Parameters:**
- `min`: Minimum value (required)
- `max`: Maximum value (required)

## Project Structure

```
src/
├── main/
│   ├── java/com/example/mcpserver/
│   │   ├── McpServerApplication.java        # Main application
│   │   └── service/
│   │       └── McpToolsService.java         # Tools with @McpTool annotations
│   └── resources/
│       └── application.properties           # Configuration
└── test/
    └── java/com/example/mcpserver/          # Tests
```

## Adding New Tools

Adding new tools is incredibly simple with Spring AI's `@McpTool` annotation. No need to manually register callbacks or handle JSON-RPC!

### Step 1: Create a method and annotate it

```java
@Service
public class MyToolsService {

    @McpTool(
        name = "weather",
        description = "Get current weather for a city"
    )
    public String getWeather(
        @McpToolParam(description = "City name", required = true)
        String city,

        @McpToolParam(description = "Temperature unit (C or F)", required = false)
        String unit
    ) {
        // Your implementation
        return "Weather in " + city + ": 72°" + (unit != null ? unit : "F");
    }
}
```

### Step 2: That's it!

Spring AI automatically:
- Discovers your `@McpTool` annotated methods
- Registers them with the MCP server
- Generates the JSON schema from `@McpToolParam` annotations
- Handles all protocol communication

### Annotation Details

**@McpTool**
- `name`: Tool name (shown to AI clients)
- `description`: What the tool does

**@McpToolParam**
- `description`: Parameter description
- `required`: Whether parameter is required (true/false)

## Configuration

Edit `src/main/resources/application.properties`:

```properties
# Server port
server.port=8080

# Spring AI MCP Server Configuration
spring.ai.mcp.server.name=spring-mcp-server
spring.ai.mcp.server.version=1.0.0
spring.ai.mcp.server.type=ASYNC
spring.ai.mcp.server.protocol=STREAMABLE

# Actuator endpoints
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized

# Logging
logging.level.org.springframework.ai.mcp=DEBUG
logging.level.com.example.mcpserver=DEBUG
```

### Configuration Properties

| Property | Description | Default |
|----------|-------------|---------|
| `spring.ai.mcp.server.name` | Server name | - |
| `spring.ai.mcp.server.version` | Server version | - |
| `spring.ai.mcp.server.type` | Server type (ASYNC or SYNC) | SYNC |
| `spring.ai.mcp.server.protocol` | Protocol (STREAMABLE or STANDARD) | STANDARD |

## MCP Endpoints

### SSE Endpoint

The Spring AI MCP server automatically exposes an SSE endpoint at:

```
http://localhost:8080/mcp
```

This endpoint implements the Model Context Protocol over Server-Sent Events, allowing AI clients to:
- List available tools
- Call tools with parameters
- Receive results

## Actuator Endpoints

Spring Boot Actuator provides production-ready monitoring:

### Health Endpoint

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

## Using with Claude Desktop

To use this MCP server with Claude Desktop, add to your Claude configuration file:

**MacOS:** `~/Library/Application Support/Claude/claude_desktop_config.json`

**Windows:** `%APPDATA%\Claude\claude_desktop_config.json`

```json
{
  "mcpServers": {
    "spring-mcp-server": {
      "url": "http://localhost:8080/mcp"
    }
  }
}
```

Restart Claude Desktop and the tools will be available.

## Development

### Running Tests

```bash
mvn test
```

### Code Style

This project uses standard Java code conventions and Spring Boot best practices.

## Technologies

- **Spring Boot 3.4.0** - Application framework
- **Spring AI 1.0.0-M6** - MCP server implementation
- **Spring WebFlux** - Reactive web framework for SSE
- **Spring Boot Actuator** - Production monitoring

## Why Spring AI MCP?

### Before (Custom Implementation)
- Manual JSON-RPC request/response handling
- Custom controller and service layers
- Manual tool schema definition
- Manual callback registration
- 15+ source files

### After (Spring AI)
- Annotation-driven (`@McpTool`)
- Auto-configuration
- Auto-discovery and registration
- Zero boilerplate
- 2 source files

**Result:** 85% less code, production-ready MCP server in minutes!

## License

MIT License

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Resources

- [Model Context Protocol Documentation](https://modelcontextprotocol.io)
- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [Spring AI MCP Server Documentation](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-server-boot-starter-docs.html)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [MCP Specification](https://spec.modelcontextprotocol.io)
