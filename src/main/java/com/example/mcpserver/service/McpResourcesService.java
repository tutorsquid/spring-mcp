package com.example.mcpserver.service;

import org.springframework.ai.mcp.server.McpResource;
import org.springframework.ai.mcp.server.McpResourceParam;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * MCP Resources service providing various resource types.
 * Resources are read-only data or content that clients can access.
 * Uses Spring AI MCP annotations for automatic resource registration.
 */
@Service
public class McpResourcesService {

    /**
     * Example 1: Simple static text resource
     * URI: resource://welcome
     */
    @McpResource(
        uri = "resource://welcome",
        name = "Welcome Message",
        description = "A welcome message for new users",
        mimeType = "text/plain"
    )
    public String getWelcomeMessage() {
        return "Welcome to the Spring MCP Server! This server provides tools, resources, and prompts via the Model Context Protocol.";
    }

    /**
     * Example 2: Dynamic resource with current system information
     * URI: resource://system/info
     */
    @McpResource(
        uri = "resource://system/info",
        name = "System Information",
        description = "Current system information including time and runtime details",
        mimeType = "application/json"
    )
    public Map<String, Object> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();

        info.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        info.put("serverName", "Spring MCP Server");
        info.put("version", "1.0.0");
        info.put("javaVersion", System.getProperty("java.version"));
        info.put("osName", System.getProperty("os.name"));
        info.put("osVersion", System.getProperty("os.version"));
        info.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        info.put("freeMemory", Runtime.getRuntime().freeMemory());
        info.put("totalMemory", Runtime.getRuntime().totalMemory());

        return info;
    }

    /**
     * Example 3: Configuration resource
     * URI: resource://config/server
     */
    @McpResource(
        uri = "resource://config/server",
        name = "Server Configuration",
        description = "Current server configuration and capabilities",
        mimeType = "application/json"
    )
    public Map<String, Object> getServerConfig() {
        Map<String, Object> config = new HashMap<>();

        config.put("name", "spring-mcp-server");
        config.put("protocol", "STATELESS");
        config.put("capabilities", Map.of(
            "tools", true,
            "resources", true,
            "prompts", true,
            "logging", true
        ));
        config.put("endpoints", Map.of(
            "mcp", "/mcp",
            "health", "/actuator/health"
        ));

        return config;
    }

    /**
     * Example 4: Parameterized resource - Documentation for a specific topic
     * URI: resource://docs/{topic}
     */
    @McpResource(
        uri = "resource://docs/{topic}",
        name = "Documentation",
        description = "Documentation for various topics. Available topics: tools, resources, prompts, getting-started",
        mimeType = "text/markdown"
    )
    public String getDocumentation(
            @McpResourceParam(description = "The documentation topic to retrieve") String topic) {

        return switch (topic.toLowerCase()) {
            case "tools" -> """
                # MCP Tools

                Tools are executable functions that clients can invoke through the MCP protocol.

                ## Available Tools
                - Calculator operations (add, subtract, multiply, divide)
                - Utility functions (echo, get_current_time, random_number)

                ## Usage
                Tools can be called by MCP clients with the appropriate parameters.
                """;

            case "resources" -> """
                # MCP Resources

                Resources are read-only data or content that clients can access.

                ## Available Resources
                - resource://welcome - Welcome message
                - resource://system/info - System information
                - resource://config/server - Server configuration
                - resource://docs/{topic} - Documentation

                ## Usage
                Resources can be read by MCP clients using the resource URI.
                """;

            case "prompts" -> """
                # MCP Prompts

                Prompts are reusable templates that help structure interactions with language models.

                ## Available Prompts
                Check the prompts service for available prompt templates.

                ## Usage
                Prompts can be retrieved and used with parameters by MCP clients.
                """;

            case "getting-started" -> """
                # Getting Started with Spring MCP Server

                ## Overview
                This is a Spring Boot application that implements the Model Context Protocol (MCP) server.

                ## Components
                1. **Tools**: Executable functions (McpToolsService)
                2. **Resources**: Read-only data (McpResourcesService)
                3. **Prompts**: Reusable templates (McpPromptsService)

                ## Endpoints
                - MCP Server: http://localhost:8080/mcp
                - Health Check: http://localhost:8080/actuator/health

                ## Building
                ```bash
                mvn clean install
                ```

                ## Running
                ```bash
                mvn spring-boot:run
                ```
                """;

            default -> String.format("Documentation topic '%s' not found. Available topics: tools, resources, prompts, getting-started", topic);
        };
    }

    /**
     * Example 5: API Reference resource
     * URI: resource://api/reference
     */
    @McpResource(
        uri = "resource://api/reference",
        name = "API Reference",
        description = "Quick reference guide for all available MCP tools",
        mimeType = "text/plain"
    )
    public String getApiReference() {
        return """
            === Spring MCP Server API Reference ===

            TOOLS:
            - add(a, b): Add two numbers
            - subtract(a, b): Subtract b from a
            - multiply(a, b): Multiply two numbers
            - divide(a, b): Divide a by b
            - echo(message): Echo back a message
            - get_current_time(timezone): Get current date/time
            - random_number(min, max): Generate random number

            RESOURCES:
            - resource://welcome: Welcome message
            - resource://system/info: System information (JSON)
            - resource://config/server: Server configuration (JSON)
            - resource://docs/{topic}: Documentation by topic
            - resource://api/reference: This API reference

            PROMPTS:
            - See prompts service for available templates

            Server Version: 1.0.0
            Protocol: MCP (Model Context Protocol)
            """;
    }
}
