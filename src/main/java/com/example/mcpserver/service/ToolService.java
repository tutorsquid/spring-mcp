package com.example.mcpserver.service;

import com.example.mcpserver.model.Tool;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ToolService {

    public List<Tool> getAvailableTools() {
        return List.of(
            createCalculatorTool(),
            createEchoTool(),
            createTimeTool(),
            createRandomNumberTool()
        );
    }

    public Object executeTool(String toolName, Map<String, Object> arguments) {
        return switch (toolName) {
            case "calculator" -> executeCalculator(arguments);
            case "echo" -> executeEcho(arguments);
            case "get_current_time" -> executeGetCurrentTime(arguments);
            case "random_number" -> executeRandomNumber(arguments);
            default -> throw new IllegalArgumentException("Unknown tool: " + toolName);
        };
    }

    private Tool createCalculatorTool() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("operation", Map.of(
            "type", "string",
            "enum", List.of("add", "subtract", "multiply", "divide"),
            "description", "The operation to perform"
        ));
        properties.put("a", Map.of(
            "type", "number",
            "description", "First number"
        ));
        properties.put("b", Map.of(
            "type", "number",
            "description", "Second number"
        ));

        Map<String, Object> inputSchema = new HashMap<>();
        inputSchema.put("type", "object");
        inputSchema.put("properties", properties);
        inputSchema.put("required", List.of("operation", "a", "b"));

        return Tool.builder()
                .name("calculator")
                .description("Performs basic arithmetic operations")
                .inputSchema(inputSchema)
                .build();
    }

    private Tool createEchoTool() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("message", Map.of(
            "type", "string",
            "description", "The message to echo back"
        ));

        Map<String, Object> inputSchema = new HashMap<>();
        inputSchema.put("type", "object");
        inputSchema.put("properties", properties);
        inputSchema.put("required", List.of("message"));

        return Tool.builder()
                .name("echo")
                .description("Echoes back the provided message")
                .inputSchema(inputSchema)
                .build();
    }

    private Tool createTimeTool() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("timezone", Map.of(
            "type", "string",
            "description", "Timezone (optional, defaults to system timezone)"
        ));

        Map<String, Object> inputSchema = new HashMap<>();
        inputSchema.put("type", "object");
        inputSchema.put("properties", properties);

        return Tool.builder()
                .name("get_current_time")
                .description("Returns the current date and time")
                .inputSchema(inputSchema)
                .build();
    }

    private Tool createRandomNumberTool() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("min", Map.of(
            "type", "number",
            "description", "Minimum value (inclusive)"
        ));
        properties.put("max", Map.of(
            "type", "number",
            "description", "Maximum value (inclusive)"
        ));

        Map<String, Object> inputSchema = new HashMap<>();
        inputSchema.put("type", "object");
        inputSchema.put("properties", properties);
        inputSchema.put("required", List.of("min", "max"));

        return Tool.builder()
                .name("random_number")
                .description("Generates a random number between min and max")
                .inputSchema(inputSchema)
                .build();
    }

    private Object executeCalculator(Map<String, Object> arguments) {
        String operation = (String) arguments.get("operation");
        Number aNum = (Number) arguments.get("a");
        Number bNum = (Number) arguments.get("b");

        double a = aNum.doubleValue();
        double b = bNum.doubleValue();

        double result = switch (operation) {
            case "add" -> a + b;
            case "subtract" -> a - b;
            case "multiply" -> a * b;
            case "divide" -> {
                if (b == 0) {
                    throw new IllegalArgumentException("Division by zero");
                }
                yield a / b;
            }
            default -> throw new IllegalArgumentException("Unknown operation: " + operation);
        };

        return String.format("Result: %.2f %s %.2f = %.2f", a, getOperatorSymbol(operation), b, result);
    }

    private String getOperatorSymbol(String operation) {
        return switch (operation) {
            case "add" -> "+";
            case "subtract" -> "-";
            case "multiply" -> "×";
            case "divide" -> "÷";
            default -> operation;
        };
    }

    private Object executeEcho(Map<String, Object> arguments) {
        String message = (String) arguments.get("message");
        return "Echo: " + message;
    }

    private Object executeGetCurrentTime(Map<String, Object> arguments) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "Current time: " + now.format(formatter);
    }

    private Object executeRandomNumber(Map<String, Object> arguments) {
        Number minNum = (Number) arguments.get("min");
        Number maxNum = (Number) arguments.get("max");

        int min = minNum.intValue();
        int max = maxNum.intValue();

        if (min > max) {
            throw new IllegalArgumentException("min must be less than or equal to max");
        }

        Random random = new Random();
        int randomNumber = random.nextInt(max - min + 1) + min;

        return String.format("Random number between %d and %d: %d", min, max, randomNumber);
    }
}
