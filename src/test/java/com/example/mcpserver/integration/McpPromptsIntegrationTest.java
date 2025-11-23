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
 * Integration tests for MCP Prompts using Spring AI MCP Client.
 * Tests all prompt templates exposed by the MCP server.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class McpPromptsIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private McpClient mcpClient;

    @Test
    void testListPrompts() {
        StepVerifier.create(mcpClient.listPrompts())
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.prompts()).isNotEmpty();

                // Verify all expected prompts are present
                List<String> promptNames = response.prompts().stream()
                    .map(McpSchema.Prompt::name)
                    .toList();

                assertThat(promptNames).containsExactlyInAnyOrder(
                    "greeting",
                    "code-review",
                    "analyze-data",
                    "meeting-summary",
                    "debug-helper",
                    "generate-docs",
                    "sql-helper"
                );
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testGreetingPrompt() {
        var request = McpSchema.GetPromptRequest.builder()
            .params(McpSchema.GetPromptRequestParams.builder()
                .name("greeting")
                .arguments(Map.of("name", "Alice"))
                .build())
            .build();

        StepVerifier.create(mcpClient.getPrompt(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.messages()).isNotEmpty();

                var message = response.messages().get(0);
                assertThat(message.content()).isInstanceOf(McpSchema.TextContent.class);

                McpSchema.TextContent content = (McpSchema.TextContent) message.content();
                assertThat(content.text()).contains("Alice");
                assertThat(content.text()).contains("How can I assist you today?");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testGreetingPromptWithTimeOfDay() {
        var request = McpSchema.GetPromptRequest.builder()
            .params(McpSchema.GetPromptRequestParams.builder()
                .name("greeting")
                .arguments(Map.of("name", "Bob", "timeOfDay", "morning"))
                .build())
            .build();

        StepVerifier.create(mcpClient.getPrompt(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.messages()).isNotEmpty();

                var message = response.messages().get(0);
                assertThat(message.content()).isInstanceOf(McpSchema.TextContent.class);

                McpSchema.TextContent content = (McpSchema.TextContent) message.content();
                assertThat(content.text()).contains("Good morning");
                assertThat(content.text()).contains("Bob");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testCodeReviewPrompt() {
        var request = McpSchema.GetPromptRequest.builder()
            .params(McpSchema.GetPromptRequestParams.builder()
                .name("code-review")
                .arguments(Map.of("language", "Java"))
                .build())
            .build();

        StepVerifier.create(mcpClient.getPrompt(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.messages()).isNotEmpty();

                var message = response.messages().get(0);
                assertThat(message.content()).isInstanceOf(McpSchema.TextContent.class);

                McpSchema.TextContent content = (McpSchema.TextContent) message.content();
                assertThat(content.text()).contains("Java code");
                assertThat(content.text()).contains("Code quality and readability");
                assertThat(content.text()).contains("Best practices");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testCodeReviewPromptWithFocusArea() {
        var request = McpSchema.GetPromptRequest.builder()
            .params(McpSchema.GetPromptRequestParams.builder()
                .name("code-review")
                .arguments(Map.of("language", "Python", "focusArea", "security"))
                .build())
            .build();

        StepVerifier.create(mcpClient.getPrompt(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.messages()).isNotEmpty();

                var message = response.messages().get(0);
                assertThat(message.content()).isInstanceOf(McpSchema.TextContent.class);

                McpSchema.TextContent content = (McpSchema.TextContent) message.content();
                assertThat(content.text()).contains("Python code");
                assertThat(content.text()).contains("Special focus: security");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testAnalyzeDataPrompt() {
        var request = McpSchema.GetPromptRequest.builder()
            .params(McpSchema.GetPromptRequestParams.builder()
                .name("analyze-data")
                .arguments(Map.of(
                    "dataType", "sales",
                    "goal", "Identify trends in quarterly sales"
                ))
                .build())
            .build();

        StepVerifier.create(mcpClient.getPrompt(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.messages()).isNotEmpty();

                var message = response.messages().get(0);
                assertThat(message.content()).isInstanceOf(McpSchema.TextContent.class);

                McpSchema.TextContent content = (McpSchema.TextContent) message.content();
                assertThat(content.text()).contains("sales data");
                assertThat(content.text()).contains("Identify trends in quarterly sales");
                assertThat(content.text()).contains("Summary statistics");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testAnalyzeDataPromptWithContext() {
        var request = McpSchema.GetPromptRequest.builder()
            .params(McpSchema.GetPromptRequestParams.builder()
                .name("analyze-data")
                .arguments(Map.of(
                    "dataType", "customer feedback",
                    "goal", "Identify common complaints",
                    "context", "E-commerce platform with 10k users"
                ))
                .build())
            .build();

        StepVerifier.create(mcpClient.getPrompt(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.messages()).isNotEmpty();

                var message = response.messages().get(0);
                assertThat(message.content()).isInstanceOf(McpSchema.TextContent.class);

                McpSchema.TextContent content = (McpSchema.TextContent) message.content();
                assertThat(content.text()).contains("customer feedback data");
                assertThat(content.text()).contains("E-commerce platform with 10k users");
                assertThat(content.text()).contains("Identify common complaints");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testMeetingSummaryPrompt() {
        var request = McpSchema.GetPromptRequest.builder()
            .params(McpSchema.GetPromptRequestParams.builder()
                .name("meeting-summary")
                .arguments(Map.of("topic", "Q1 Planning Meeting"))
                .build())
            .build();

        StepVerifier.create(mcpClient.getPrompt(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.messages()).isNotEmpty();

                var message = response.messages().get(0);
                assertThat(message.content()).isInstanceOf(McpSchema.TextContent.class);

                McpSchema.TextContent content = (McpSchema.TextContent) message.content();
                assertThat(content.text()).contains("Q1 Planning Meeting");
                assertThat(content.text()).contains("Meeting Overview");
                assertThat(content.text()).contains("Action Items");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testMeetingSummaryPromptWithParticipants() {
        var request = McpSchema.GetPromptRequest.builder()
            .params(McpSchema.GetPromptRequestParams.builder()
                .name("meeting-summary")
                .arguments(Map.of(
                    "topic", "Sprint Review",
                    "participants", "Alice, Bob, Carol, Dave"
                ))
                .build())
            .build();

        StepVerifier.create(mcpClient.getPrompt(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.messages()).isNotEmpty();

                var message = response.messages().get(0);
                assertThat(message.content()).isInstanceOf(McpSchema.TextContent.class);

                McpSchema.TextContent content = (McpSchema.TextContent) message.content();
                assertThat(content.text()).contains("Sprint Review");
                assertThat(content.text()).contains("Participants: Alice, Bob, Carol, Dave");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testDebugHelperPrompt() {
        var request = McpSchema.GetPromptRequest.builder()
            .params(McpSchema.GetPromptRequestParams.builder()
                .name("debug-helper")
                .arguments(Map.of(
                    "issue", "NullPointerException in UserService",
                    "stack", "Spring Boot, Java 17, PostgreSQL"
                ))
                .build())
            .build();

        StepVerifier.create(mcpClient.getPrompt(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.messages()).isNotEmpty();

                var message = response.messages().get(0);
                assertThat(message.content()).isInstanceOf(McpSchema.TextContent.class);

                McpSchema.TextContent content = (McpSchema.TextContent) message.content();
                assertThat(content.text()).contains("NullPointerException in UserService");
                assertThat(content.text()).contains("Spring Boot, Java 17, PostgreSQL");
                assertThat(content.text()).contains("Possible root causes");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testDebugHelperPromptWithAttemptedSolutions() {
        var request = McpSchema.GetPromptRequest.builder()
            .params(McpSchema.GetPromptRequestParams.builder()
                .name("debug-helper")
                .arguments(Map.of(
                    "issue", "Memory leak in cache",
                    "stack", "Redis, Spring Cache",
                    "attemptedSolutions", "Cleared cache, restarted Redis"
                ))
                .build())
            .build();

        StepVerifier.create(mcpClient.getPrompt(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.messages()).isNotEmpty();

                var message = response.messages().get(0);
                assertThat(message.content()).isInstanceOf(McpSchema.TextContent.class);

                McpSchema.TextContent content = (McpSchema.TextContent) message.content();
                assertThat(content.text()).contains("Already Tried: Cleared cache, restarted Redis");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testGenerateDocsPrompt() {
        var request = McpSchema.GetPromptRequest.builder()
            .params(McpSchema.GetPromptRequestParams.builder()
                .name("generate-docs")
                .arguments(Map.of(
                    "component", "UserAuthentication API",
                    "audience", "developers"
                ))
                .build())
            .build();

        StepVerifier.create(mcpClient.getPrompt(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.messages()).isNotEmpty();

                var message = response.messages().get(0);
                assertThat(message.content()).isInstanceOf(McpSchema.TextContent.class);

                McpSchema.TextContent content = (McpSchema.TextContent) message.content();
                assertThat(content.text()).contains("UserAuthentication API");
                assertThat(content.text()).contains("developers");
                assertThat(content.text()).contains("markdown");
                assertThat(content.text()).contains("Getting Started");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testGenerateDocsPromptWithFormat() {
        var request = McpSchema.GetPromptRequest.builder()
            .params(McpSchema.GetPromptRequestParams.builder()
                .name("generate-docs")
                .arguments(Map.of(
                    "component", "Payment Gateway",
                    "audience", "users",
                    "format", "html"
                ))
                .build())
            .build();

        StepVerifier.create(mcpClient.getPrompt(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.messages()).isNotEmpty();

                var message = response.messages().get(0);
                assertThat(message.content()).isInstanceOf(McpSchema.TextContent.class);

                McpSchema.TextContent content = (McpSchema.TextContent) message.content();
                assertThat(content.text()).contains("Payment Gateway");
                assertThat(content.text()).contains("Format: html");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testSqlHelperPrompt() {
        var request = McpSchema.GetPromptRequest.builder()
            .params(McpSchema.GetPromptRequestParams.builder()
                .name("sql-helper")
                .arguments(Map.of(
                    "requirement", "Get all users who made purchases in the last 30 days"
                ))
                .build())
            .build();

        StepVerifier.create(mcpClient.getPrompt(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.messages()).isNotEmpty();

                var message = response.messages().get(0);
                assertThat(message.content()).isInstanceOf(McpSchema.TextContent.class);

                McpSchema.TextContent content = (McpSchema.TextContent) message.content();
                assertThat(content.text()).contains("Get all users who made purchases in the last 30 days");
                assertThat(content.text()).contains("SQL query");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testSqlHelperPromptWithDbType() {
        var request = McpSchema.GetPromptRequest.builder()
            .params(McpSchema.GetPromptRequestParams.builder()
                .name("sql-helper")
                .arguments(Map.of(
                    "requirement", "Calculate average order value by category",
                    "dbType", "PostgreSQL",
                    "performance", "High volume table"
                ))
                .build())
            .build();

        StepVerifier.create(mcpClient.getPrompt(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.messages()).isNotEmpty();

                var message = response.messages().get(0);
                assertThat(message.content()).isInstanceOf(McpSchema.TextContent.class);

                McpSchema.TextContent content = (McpSchema.TextContent) message.content();
                assertThat(content.text()).contains("PostgreSQL");
                assertThat(content.text()).contains("Performance optimization tips");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testPromptMetadata() {
        StepVerifier.create(mcpClient.listPrompts())
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.prompts()).isNotEmpty();

                // Find the greeting prompt and verify its metadata
                var greetingPrompt = response.prompts().stream()
                    .filter(prompt -> "greeting".equals(prompt.name()))
                    .findFirst()
                    .orElseThrow();

                assertThat(greetingPrompt.description()).isEqualTo("Generate a personalized greeting message");
                assertThat(greetingPrompt.arguments()).isNotNull();
                assertThat(greetingPrompt.arguments()).isNotEmpty();
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }
}
