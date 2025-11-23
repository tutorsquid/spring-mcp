package com.example.mcpserver.integration;

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
 * Integration tests for MCP Tools using Spring AI MCP Client.
 * Tests all calculator and utility tools exposed by the MCP server.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class McpToolsIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private McpClient mcpClient;

    @Test
    void testListTools() {
        StepVerifier.create(mcpClient.listTools())
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.tools()).isNotEmpty();

                // Verify all expected tools are present
                List<String> toolNames = response.tools().stream()
                    .map(McpSchema.Tool::name)
                    .toList();

                assertThat(toolNames).containsExactlyInAnyOrder(
                    "add",
                    "subtract",
                    "multiply",
                    "divide",
                    "echo",
                    "get_current_time",
                    "random_number"
                );
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testAddTool() {
        var request = McpSchema.CallToolRequest.builder()
            .params(McpSchema.CallToolRequestParams.builder()
                .name("add")
                .arguments(Map.of("a", 5.0, "b", 3.0))
                .build())
            .build();

        StepVerifier.create(mcpClient.callTool(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.content()).isNotEmpty();

                var content = response.content().get(0);
                assertThat(content).isInstanceOf(McpSchema.TextContent.class);

                McpSchema.TextContent textContent = (McpSchema.TextContent) content;
                assertThat(textContent.text()).isEqualTo("8.0");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testSubtractTool() {
        var request = McpSchema.CallToolRequest.builder()
            .params(McpSchema.CallToolRequestParams.builder()
                .name("subtract")
                .arguments(Map.of("a", 10.0, "b", 4.0))
                .build())
            .build();

        StepVerifier.create(mcpClient.callTool(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.content()).isNotEmpty();

                var content = response.content().get(0);
                assertThat(content).isInstanceOf(McpSchema.TextContent.class);

                McpSchema.TextContent textContent = (McpSchema.TextContent) content;
                assertThat(textContent.text()).isEqualTo("6.0");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testMultiplyTool() {
        var request = McpSchema.CallToolRequest.builder()
            .params(McpSchema.CallToolRequestParams.builder()
                .name("multiply")
                .arguments(Map.of("a", 7.0, "b", 6.0))
                .build())
            .build();

        StepVerifier.create(mcpClient.callTool(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.content()).isNotEmpty();

                var content = response.content().get(0);
                assertThat(content).isInstanceOf(McpSchema.TextContent.class);

                McpSchema.TextContent textContent = (McpSchema.TextContent) content;
                assertThat(textContent.text()).isEqualTo("42.0");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testDivideTool() {
        var request = McpSchema.CallToolRequest.builder()
            .params(McpSchema.CallToolRequestParams.builder()
                .name("divide")
                .arguments(Map.of("a", 15.0, "b", 3.0))
                .build())
            .build();

        StepVerifier.create(mcpClient.callTool(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.content()).isNotEmpty();

                var content = response.content().get(0);
                assertThat(content).isInstanceOf(McpSchema.TextContent.class);

                McpSchema.TextContent textContent = (McpSchema.TextContent) content;
                assertThat(textContent.text()).isEqualTo("5.0");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testDivideByZero() {
        var request = McpSchema.CallToolRequest.builder()
            .params(McpSchema.CallToolRequestParams.builder()
                .name("divide")
                .arguments(Map.of("a", 10.0, "b", 0.0))
                .build())
            .build();

        StepVerifier.create(mcpClient.callTool(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                // Should contain error information
                assertThat(response.isError()).isTrue();
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testEchoTool() {
        var request = McpSchema.CallToolRequest.builder()
            .params(McpSchema.CallToolRequestParams.builder()
                .name("echo")
                .arguments(Map.of("message", "Hello MCP!"))
                .build())
            .build();

        StepVerifier.create(mcpClient.callTool(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.content()).isNotEmpty();

                var content = response.content().get(0);
                assertThat(content).isInstanceOf(McpSchema.TextContent.class);

                McpSchema.TextContent textContent = (McpSchema.TextContent) content;
                assertThat(textContent.text()).isEqualTo("Echo: Hello MCP!");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testGetCurrentTimeTool() {
        var request = McpSchema.CallToolRequest.builder()
            .params(McpSchema.CallToolRequestParams.builder()
                .name("get_current_time")
                .arguments(Map.of())
                .build())
            .build();

        StepVerifier.create(mcpClient.callTool(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.content()).isNotEmpty();

                var content = response.content().get(0);
                assertThat(content).isInstanceOf(McpSchema.TextContent.class);

                McpSchema.TextContent textContent = (McpSchema.TextContent) content;
                assertThat(textContent.text()).contains("Current time:");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testGetCurrentTimeWithTimezone() {
        var request = McpSchema.CallToolRequest.builder()
            .params(McpSchema.CallToolRequestParams.builder()
                .name("get_current_time")
                .arguments(Map.of("timezone", "UTC"))
                .build())
            .build();

        StepVerifier.create(mcpClient.callTool(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.content()).isNotEmpty();

                var content = response.content().get(0);
                assertThat(content).isInstanceOf(McpSchema.TextContent.class);

                McpSchema.TextContent textContent = (McpSchema.TextContent) content;
                assertThat(textContent.text()).contains("requested timezone: UTC");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testRandomNumberTool() {
        var request = McpSchema.CallToolRequest.builder()
            .params(McpSchema.CallToolRequestParams.builder()
                .name("random_number")
                .arguments(Map.of("min", 1, "max", 10))
                .build())
            .build();

        StepVerifier.create(mcpClient.callTool(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.content()).isNotEmpty();

                var content = response.content().get(0);
                assertThat(content).isInstanceOf(McpSchema.TextContent.class);

                McpSchema.TextContent textContent = (McpSchema.TextContent) content;
                int randomValue = Integer.parseInt(textContent.text());
                assertThat(randomValue).isBetween(1, 10);
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testRandomNumberInvalidRange() {
        var request = McpSchema.CallToolRequest.builder()
            .params(McpSchema.CallToolRequestParams.builder()
                .name("random_number")
                .arguments(Map.of("min", 10, "max", 1))
                .build())
            .build();

        StepVerifier.create(mcpClient.callTool(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                // Should contain error information
                assertThat(response.isError()).isTrue();
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testToolMetadata() {
        StepVerifier.create(mcpClient.listTools())
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.tools()).isNotEmpty();

                // Find the add tool and verify its metadata
                var addTool = response.tools().stream()
                    .filter(tool -> "add".equals(tool.name()))
                    .findFirst()
                    .orElseThrow();

                assertThat(addTool.description()).isEqualTo("Add two numbers together");
                assertThat(addTool.inputSchema()).isNotNull();
                assertThat(addTool.inputSchema().properties()).containsKeys("a", "b");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }
}
