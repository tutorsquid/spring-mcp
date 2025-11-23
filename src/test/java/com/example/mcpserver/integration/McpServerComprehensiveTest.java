package com.example.mcpserver.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.mcp.client.McpClient;
import org.springframework.ai.mcp.spec.McpSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive integration test suite for the Spring MCP Server.
 * Tests end-to-end functionality of tools, resources, and prompts.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("MCP Server Comprehensive Integration Tests")
class McpServerComprehensiveTest {

    @LocalServerPort
    private int port;

    @Autowired
    private McpClient mcpClient;

    @Test
    @DisplayName("Server should expose all three MCP capabilities: tools, resources, and prompts")
    void testServerCapabilities() {
        // Test tools capability
        StepVerifier.create(mcpClient.listTools())
            .assertNext(response -> assertThat(response.tools()).isNotEmpty())
            .expectComplete()
            .verify(Duration.ofSeconds(5));

        // Test resources capability
        StepVerifier.create(mcpClient.listResources())
            .assertNext(response -> assertThat(response.resources()).isNotEmpty())
            .expectComplete()
            .verify(Duration.ofSeconds(5));

        // Test prompts capability
        StepVerifier.create(mcpClient.listPrompts())
            .assertNext(response -> assertThat(response.prompts()).isNotEmpty())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    @DisplayName("Server should handle complex workflow: calculate, retrieve config, generate docs prompt")
    void testComplexWorkflow() {
        // Step 1: Use calculator tool to perform calculations
        var calcRequest = McpSchema.CallToolRequest.builder()
            .params(McpSchema.CallToolRequestParams.builder()
                .name("multiply")
                .arguments(Map.of("a", 6.0, "b", 7.0))
                .build())
            .build();

        StepVerifier.create(mcpClient.callTool(calcRequest))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.content()).isNotEmpty();
                McpSchema.TextContent content = (McpSchema.TextContent) response.content().get(0);
                assertThat(content.text()).isEqualTo("42.0");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));

        // Step 2: Read server configuration resource
        var configRequest = McpSchema.ReadResourceRequest.builder()
            .params(McpSchema.ReadResourceRequestParams.builder()
                .uri("resource://config/server")
                .build())
            .build();

        StepVerifier.create(mcpClient.readResource(configRequest))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.contents()).isNotEmpty();
                McpSchema.TextResourceContents content = (McpSchema.TextResourceContents) response.contents().get(0);
                assertThat(content.text()).contains("spring-mcp-server");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));

        // Step 3: Generate documentation prompt
        var promptRequest = McpSchema.GetPromptRequest.builder()
            .params(McpSchema.GetPromptRequestParams.builder()
                .name("generate-docs")
                .arguments(Map.of(
                    "component", "MCP Calculator Tools",
                    "audience", "developers"
                ))
                .build())
            .build();

        StepVerifier.create(mcpClient.getPrompt(promptRequest))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.messages()).isNotEmpty();
                var message = response.messages().get(0);
                McpSchema.TextContent content = (McpSchema.TextContent) message.content();
                assertThat(content.text()).contains("MCP Calculator Tools");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    @DisplayName("Server should handle rapid sequential tool calls")
    void testRapidToolCalls() {
        List<Map<String, Object>> calculations = List.of(
            Map.of("a", 1.0, "b", 1.0, "expected", "2.0"),
            Map.of("a", 5.0, "b", 3.0, "expected", "8.0"),
            Map.of("a", 10.0, "b", 20.0, "expected", "30.0")
        );

        for (Map<String, Object> calc : calculations) {
            var request = McpSchema.CallToolRequest.builder()
                .params(McpSchema.CallToolRequestParams.builder()
                    .name("add")
                    .arguments(Map.of(
                        "a", calc.get("a"),
                        "b", calc.get("b")
                    ))
                    .build())
                .build();

            StepVerifier.create(mcpClient.callTool(request))
                .assertNext(response -> {
                    assertThat(response).isNotNull();
                    McpSchema.TextContent content = (McpSchema.TextContent) response.content().get(0);
                    assertThat(content.text()).isEqualTo(calc.get("expected"));
                })
                .expectComplete()
                .verify(Duration.ofSeconds(5));
        }
    }

    @Test
    @DisplayName("Server should handle error scenarios gracefully")
    void testErrorHandling() {
        // Test 1: Division by zero
        var divideByZero = McpSchema.CallToolRequest.builder()
            .params(McpSchema.CallToolRequestParams.builder()
                .name("divide")
                .arguments(Map.of("a", 5.0, "b", 0.0))
                .build())
            .build();

        StepVerifier.create(mcpClient.callTool(divideByZero))
            .assertNext(response -> {
                assertThat(response.isError()).isTrue();
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));

        // Test 2: Invalid random number range
        var invalidRange = McpSchema.CallToolRequest.builder()
            .params(McpSchema.CallToolRequestParams.builder()
                .name("random_number")
                .arguments(Map.of("min", 100, "max", 1))
                .build())
            .build();

        StepVerifier.create(mcpClient.callTool(invalidRange))
            .assertNext(response -> {
                assertThat(response.isError()).isTrue();
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    @DisplayName("Server should provide consistent metadata for all capabilities")
    void testMetadataConsistency() {
        // Verify tools have proper metadata
        StepVerifier.create(mcpClient.listTools())
            .assertNext(response -> {
                assertThat(response.tools()).allSatisfy(tool -> {
                    assertThat(tool.name()).isNotBlank();
                    assertThat(tool.description()).isNotBlank();
                    assertThat(tool.inputSchema()).isNotNull();
                });
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));

        // Verify resources have proper metadata
        StepVerifier.create(mcpClient.listResources())
            .assertNext(response -> {
                assertThat(response.resources()).allSatisfy(resource -> {
                    assertThat(resource.uri()).isNotBlank();
                    assertThat(resource.name()).isNotBlank();
                    assertThat(resource.description()).isNotBlank();
                    assertThat(resource.mimeType()).isNotBlank();
                });
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));

        // Verify prompts have proper metadata
        StepVerifier.create(mcpClient.listPrompts())
            .assertNext(response -> {
                assertThat(response.prompts()).allSatisfy(prompt -> {
                    assertThat(prompt.name()).isNotBlank();
                    assertThat(prompt.description()).isNotBlank();
                });
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    @DisplayName("Server should handle parameterized resources correctly")
    void testParameterizedResources() {
        List<String> topics = List.of("tools", "resources", "prompts", "getting-started");

        for (String topic : topics) {
            var request = McpSchema.ReadResourceRequest.builder()
                .params(McpSchema.ReadResourceRequestParams.builder()
                    .uri("resource://docs/" + topic)
                    .build())
                .build();

            StepVerifier.create(mcpClient.readResource(request))
                .assertNext(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.contents()).isNotEmpty();
                    McpSchema.TextResourceContents content = (McpSchema.TextResourceContents) response.contents().get(0);
                    assertThat(content.text()).isNotEmpty();
                    assertThat(content.mimeType()).isEqualTo("text/markdown");
                })
                .expectComplete()
                .verify(Duration.ofSeconds(5));
        }
    }

    @Test
    @DisplayName("Server should handle prompts with optional parameters")
    void testPromptsWithOptionalParameters() {
        // Test prompt with only required parameters
        var minimalRequest = McpSchema.GetPromptRequest.builder()
            .params(McpSchema.GetPromptRequestParams.builder()
                .name("greeting")
                .arguments(Map.of("name", "Test User"))
                .build())
            .build();

        StepVerifier.create(mcpClient.getPrompt(minimalRequest))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.messages()).isNotEmpty();
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));

        // Test prompt with all parameters
        var fullRequest = McpSchema.GetPromptRequest.builder()
            .params(McpSchema.GetPromptRequestParams.builder()
                .name("greeting")
                .arguments(Map.of(
                    "name", "Test User",
                    "timeOfDay", "evening"
                ))
                .build())
            .build();

        StepVerifier.create(mcpClient.getPrompt(fullRequest))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.messages()).isNotEmpty();
                var message = response.messages().get(0);
                McpSchema.TextContent content = (McpSchema.TextContent) message.content();
                assertThat(content.text()).contains("Good evening");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    @DisplayName("Server should maintain stateless protocol behavior")
    void testStatelessBehavior() {
        // Make the same call twice and verify consistent results
        var request = McpSchema.CallToolRequest.builder()
            .params(McpSchema.CallToolRequestParams.builder()
                .name("add")
                .arguments(Map.of("a", 5.0, "b", 5.0))
                .build())
            .build();

        // First call
        StepVerifier.create(mcpClient.callTool(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                McpSchema.TextContent content = (McpSchema.TextContent) response.content().get(0);
                assertThat(content.text()).isEqualTo("10.0");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));

        // Second call - should produce identical result (stateless)
        StepVerifier.create(mcpClient.callTool(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                McpSchema.TextContent content = (McpSchema.TextContent) response.content().get(0);
                assertThat(content.text()).isEqualTo("10.0");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    @DisplayName("Server should handle all calculator operations correctly")
    void testCalculatorCompleteness() {
        // Test comprehensive calculator workflow
        Map<String, Map<String, Object>> operations = Map.of(
            "add", Map.of("a", 15.0, "b", 25.0, "expected", "40.0"),
            "subtract", Map.of("a", 50.0, "b", 20.0, "expected", "30.0"),
            "multiply", Map.of("a", 6.0, "b", 7.0, "expected", "42.0"),
            "divide", Map.of("a", 100.0, "b", 4.0, "expected", "25.0")
        );

        operations.forEach((operation, params) -> {
            var request = McpSchema.CallToolRequest.builder()
                .params(McpSchema.CallToolRequestParams.builder()
                    .name(operation)
                    .arguments(Map.of(
                        "a", params.get("a"),
                        "b", params.get("b")
                    ))
                    .build())
                .build();

            StepVerifier.create(mcpClient.callTool(request))
                .assertNext(response -> {
                    assertThat(response).isNotNull();
                    McpSchema.TextContent content = (McpSchema.TextContent) response.content().get(0);
                    assertThat(content.text()).isEqualTo(params.get("expected"));
                })
                .expectComplete()
                .verify(Duration.ofSeconds(5));
        });
    }

    @Test
    @DisplayName("Server should provide complete API reference through resources")
    void testApiReferenceCompleteness() {
        var request = McpSchema.ReadResourceRequest.builder()
            .params(McpSchema.ReadResourceRequestParams.builder()
                .uri("resource://api/reference")
                .build())
            .build();

        StepVerifier.create(mcpClient.readResource(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                McpSchema.TextResourceContents content = (McpSchema.TextResourceContents) response.contents().get(0);
                String apiRef = content.text();

                // Verify all tools are documented
                assertThat(apiRef).contains("add(a, b)");
                assertThat(apiRef).contains("subtract(a, b)");
                assertThat(apiRef).contains("multiply(a, b)");
                assertThat(apiRef).contains("divide(a, b)");
                assertThat(apiRef).contains("echo(message)");
                assertThat(apiRef).contains("get_current_time(timezone)");
                assertThat(apiRef).contains("random_number(min, max)");

                // Verify resources section exists
                assertThat(apiRef).contains("RESOURCES:");

                // Verify prompts section exists
                assertThat(apiRef).contains("PROMPTS:");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }
}
