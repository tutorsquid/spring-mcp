# Spring AI MCP Server

A Model Context Protocol (MCP) server implementation using **Spring AI** and Spring Boot. This server provides AI assistants with standardized access to tools and resources through the MCP protocol with minimal custom development.

## Overview

The Model Context Protocol (MCP) is an open protocol that enables AI assistants to securely access external data sources and tools. This implementation leverages **Spring AI's MCP Boot Starter** to create a production-ready MCP server with automatic tool registration, SSE transport support, and zero boilerplate code.

## Features

- **Spring AI MCP Integration**: Built on `spring-ai-mcp-server-webflux-spring-boot-starter`
- **Annotation-Driven Tools**: Use `@McpTool` annotations for automatic tool registration
- **Stateless Protocol**: Simple request/response protocol for reliable tool execution
- **WebFlux Transport**: Reactive Spring WebFlux for efficient request handling
- **Zero Boilerplate**: No manual JSON-RPC handling or callback registration
- **Built-in Tools**: Calculator (add, subtract, multiply, divide), echo, time, and random number
- **Built-in Resources**: System information, configuration, documentation, and API reference
- **Built-in Prompts**: Code review, data analysis, debugging, and documentation templates
- **Spring Boot Actuator**: Production-ready health checks and monitoring endpoints
- **Auto-Configuration**: Spring AI handles all MCP protocol details automatically

## Architecture

```
┌─────────────────────────────────────────┐
│   Spring Boot Application               │
│  ┌────────────────────────────────────┐ │
│  │  @McpTool Annotated Methods        │ │
│  │  (McpToolsService)                 │ │
│  ├────────────────────────────────────┤ │
│  │  @McpResource Annotated Methods    │ │
│  │  (McpResourcesService)             │ │
│  ├────────────────────────────────────┤ │
│  │  @McpPrompt Annotated Methods      │ │
│  │  (McpPromptsService)               │ │
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

## Available Resources

The server automatically exposes all methods annotated with `@McpResource`:

### 1. Welcome Message
URI: `resource://welcome`

A welcome message for new users introducing the Spring MCP Server.

### 2. System Information
URI: `resource://system/info`

Dynamic resource providing current system information including timestamp, Java version, OS details, and memory usage. Returns JSON format.

### 3. Server Configuration
URI: `resource://config/server`

Current server configuration and capabilities including protocol type, enabled features, and endpoints. Returns JSON format.

### 4. Documentation
URI: `resource://docs/{topic}`

Parameterized resource providing documentation for various topics. Available topics:
- `tools` - Documentation about MCP tools
- `resources` - Documentation about MCP resources
- `prompts` - Documentation about MCP prompts
- `getting-started` - Getting started guide

Returns Markdown format.

### 5. API Reference
URI: `resource://api/reference`

Quick reference guide listing all available tools, resources, and prompts in plain text format.

## Available Prompts

The server automatically exposes all methods annotated with `@McpPrompt`:

### 1. Greeting
Name: `greeting`

Generate a personalized greeting message.

**Parameters:**
- `name`: The name of the person to greet (required)
- `timeOfDay`: Time of day - morning, afternoon, evening (optional)

### 2. Code Review
Name: `code-review`

Generate a comprehensive code review prompt for analyzing code.

**Parameters:**
- `language`: The programming language (required)
- `focusArea`: Specific aspects to focus on like security, performance, readability (optional)

### 3. Data Analysis
Name: `analyze-data`

Generate a prompt for analyzing data or datasets.

**Parameters:**
- `dataType`: The type of data being analyzed (required)
- `goal`: The analysis goal or question (required)
- `context`: Additional context about the data (optional)

### 4. Meeting Summary
Name: `meeting-summary`

Generate a prompt for creating structured meeting summaries.

**Parameters:**
- `topic`: The meeting topic or title (required)
- `participants`: List of participants, comma-separated (optional)

### 5. Debug Helper
Name: `debug-helper`

Generate a systematic debugging prompt for troubleshooting issues.

**Parameters:**
- `issue`: The error message or issue description (required)
- `stack`: The technology stack or environment (required)
- `attemptedSolutions`: What has been tried already (optional)

### 6. Documentation Generator
Name: `generate-docs`

Generate a prompt for creating technical documentation.

**Parameters:**
- `component`: The component or feature to document (required)
- `audience`: Target audience - developers, users, admins (required)
- `format`: Documentation format - markdown, html, javadoc (optional)

### 7. SQL Helper
Name: `sql-helper`

Generate a prompt for building SQL queries based on requirements.

**Parameters:**
- `requirement`: Description of the data to retrieve or modify (required)
- `dbType`: The database type like MySQL, PostgreSQL (optional)
- `performance`: Performance considerations like indexes, optimization (optional)

## Project Structure

```
src/
├── main/
│   ├── java/com/example/mcpserver/
│   │   ├── McpServerApplication.java        # Main application
│   │   └── service/
│   │       ├── McpToolsService.java         # Tools with @McpTool annotations
│   │       ├── McpResourcesService.java     # Resources with @McpResource annotations
│   │       └── McpPromptsService.java       # Prompts with @McpPrompt annotations
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

## Adding New Resources

Resources are read-only data or content that clients can access. Adding resources is simple with Spring AI's `@McpResource` annotation.

### Step 1: Create a method and annotate it

```java
@Service
public class MyResourcesService {

    @McpResource(
        uri = "resource://company/info",
        name = "Company Information",
        description = "Information about the company",
        mimeType = "application/json"
    )
    public Map<String, Object> getCompanyInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Acme Corp");
        info.put("founded", 2020);
        info.put("industry", "Technology");
        return info;
    }
}
```

### Step 2: That's it!

Spring AI automatically:
- Discovers your `@McpResource` annotated methods
- Registers them with the MCP server
- Makes them accessible via the resource URI
- Handles all protocol communication

### Annotation Details

**@McpResource**
- `uri`: Resource URI (e.g., "resource://my/resource" or "resource://docs/{id}")
- `name`: Resource name (shown to AI clients)
- `description`: What the resource provides
- `mimeType`: Content type (e.g., "text/plain", "application/json", "text/markdown")

**@McpResourceParam** (for parameterized URIs)
- `description`: Parameter description

### Resource URI Patterns

Static resources:
```java
@McpResource(uri = "resource://config")
public String getConfig() { ... }
```

Parameterized resources:
```java
@McpResource(uri = "resource://user/{id}")
public String getUser(@McpResourceParam String id) { ... }
```

## Adding New Prompts

Prompts are reusable templates that help structure interactions with language models. Adding prompts is simple with Spring AI's `@McpPrompt` annotation.

### Step 1: Create a method and annotate it

```java
@Service
public class MyPromptsService {

    @McpPrompt(
        name = "explain-code",
        description = "Generate a prompt for explaining code functionality"
    )
    public String explainCode(
        @McpPromptParam(description = "Programming language", required = true)
        String language,

        @McpPromptParam(description = "Detail level (basic, intermediate, advanced)", required = false)
        String level
    ) {
        String detailLevel = level != null ? level : "intermediate";
        return String.format(
            "Please explain the following %s code at a %s level:\n\n" +
            "Include:\n" +
            "1. What the code does\n" +
            "2. Key concepts used\n" +
            "3. How it works step by step\n",
            language, detailLevel
        );
    }
}
```

### Step 2: That's it!

Spring AI automatically:
- Discovers your `@McpPrompt` annotated methods
- Registers them with the MCP server
- Generates parameter schemas from `@McpPromptParam` annotations
- Handles all protocol communication

### Annotation Details

**@McpPrompt**
- `name`: Prompt name (used by AI clients to invoke the prompt)
- `description`: What the prompt template does

**@McpPromptParam**
- `description`: Parameter description
- `required`: Whether parameter is required (true/false)

### Prompt Templates Best Practices

1. **Structure your prompts clearly**: Use sections, bullet points, and numbered lists
2. **Make them flexible**: Use parameters to customize the prompt for different scenarios
3. **Provide context**: Include relevant background information in the template
4. **Be specific**: Clear instructions lead to better results
5. **Use markdown**: Format your prompts for readability

## Configuration

Edit `src/main/resources/application.properties`:

```properties
# Server port
server.port=8080

# Spring AI MCP Server Configuration
spring.ai.mcp.server.name=spring-mcp-server
spring.ai.mcp.server.version=1.0.0
spring.ai.mcp.server.type=ASYNC
spring.ai.mcp.server.protocol=STATELESS

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
| `spring.ai.mcp.server.protocol` | Protocol (STATELESS or STREAMABLE) | STATELESS |

## MCP Endpoints

### MCP Endpoint

The Spring AI MCP server automatically exposes an MCP endpoint at:

```
http://localhost:8080/mcp
```

This endpoint implements the Model Context Protocol in stateless mode, allowing AI clients to:
- List available tools
- Call tools with parameters
- Receive results in simple request/response format

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
- Manual tool/resource/prompt schema definition
- Manual callback registration
- 15+ source files

### After (Spring AI)
- Annotation-driven (`@McpTool`, `@McpResource`, `@McpPrompt`)
- Auto-configuration
- Auto-discovery and registration
- Zero boilerplate
- 4 source files (1 app + 3 services)

**Result:** 85% less code, production-ready MCP server with tools, resources, and prompts in minutes!

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
