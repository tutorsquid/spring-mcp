package com.example.mcpserver.service;

import org.springframework.ai.mcp.server.McpTool;
import org.springframework.ai.mcp.server.McpToolParam;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * MCP Tools service providing calculator, echo, time, and random number utilities.
 * Uses Spring AI MCP annotations for automatic tool registration.
 */
@Service
public class McpToolsService {

    @McpTool(name = "add", description = "Add two numbers together")
    public double add(
            @McpToolParam(description = "First number", required = true) double a,
            @McpToolParam(description = "Second number", required = true) double b) {
        return a + b;
    }

    @McpTool(name = "subtract", description = "Subtract second number from first number")
    public double subtract(
            @McpToolParam(description = "First number", required = true) double a,
            @McpToolParam(description = "Second number", required = true) double b) {
        return a - b;
    }

    @McpTool(name = "multiply", description = "Multiply two numbers")
    public double multiply(
            @McpToolParam(description = "First number", required = true) double a,
            @McpToolParam(description = "Second number", required = true) double b) {
        return a * b;
    }

    @McpTool(name = "divide", description = "Divide first number by second number")
    public double divide(
            @McpToolParam(description = "Numerator", required = true) double a,
            @McpToolParam(description = "Denominator (must not be zero)", required = true) double b) {
        if (b == 0) {
            throw new IllegalArgumentException("Division by zero is not allowed");
        }
        return a / b;
    }

    @McpTool(name = "echo", description = "Echo back the provided message")
    public String echo(
            @McpToolParam(description = "The message to echo back", required = true) String message) {
        return "Echo: " + message;
    }

    @McpTool(name = "get_current_time", description = "Returns the current date and time")
    public String getCurrentTime(
            @McpToolParam(description = "Timezone (optional, defaults to system timezone)", required = false) String timezone) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = now.format(formatter);

        if (timezone != null && !timezone.isEmpty()) {
            return String.format("Current time (requested timezone: %s): %s", timezone, formattedTime);
        }
        return "Current time: " + formattedTime;
    }

    @McpTool(name = "random_number", description = "Generate a random number between min and max (inclusive)")
    public int randomNumber(
            @McpToolParam(description = "Minimum value (inclusive)", required = true) int min,
            @McpToolParam(description = "Maximum value (inclusive)", required = true) int max) {
        if (min > max) {
            throw new IllegalArgumentException("min must be less than or equal to max");
        }
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
}
