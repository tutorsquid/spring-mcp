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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for MCP Resources using Spring AI MCP Client.
 * Tests all resources exposed by the MCP server including static and dynamic resources.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class McpResourcesIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private McpClient mcpClient;

    @Test
    void testListResources() {
        StepVerifier.create(mcpClient.listResources())
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.resources()).isNotEmpty();

                // Verify all expected resources are present
                List<String> resourceUris = response.resources().stream()
                    .map(McpSchema.Resource::uri)
                    .toList();

                assertThat(resourceUris).containsExactlyInAnyOrder(
                    "resource://welcome",
                    "resource://system/info",
                    "resource://config/server",
                    "resource://docs/{topic}",
                    "resource://api/reference"
                );
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testWelcomeResource() {
        var request = McpSchema.ReadResourceRequest.builder()
            .params(McpSchema.ReadResourceRequestParams.builder()
                .uri("resource://welcome")
                .build())
            .build();

        StepVerifier.create(mcpClient.readResource(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.contents()).isNotEmpty();

                var content = response.contents().get(0);
                assertThat(content).isInstanceOf(McpSchema.TextResourceContents.class);

                McpSchema.TextResourceContents textContent = (McpSchema.TextResourceContents) content;
                assertThat(textContent.text()).contains("Welcome to the Spring MCP Server");
                assertThat(textContent.mimeType()).isEqualTo("text/plain");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testSystemInfoResource() {
        var request = McpSchema.ReadResourceRequest.builder()
            .params(McpSchema.ReadResourceRequestParams.builder()
                .uri("resource://system/info")
                .build())
            .build();

        StepVerifier.create(mcpClient.readResource(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.contents()).isNotEmpty();

                var content = response.contents().get(0);
                assertThat(content).isInstanceOf(McpSchema.TextResourceContents.class);

                McpSchema.TextResourceContents textContent = (McpSchema.TextResourceContents) content;
                assertThat(textContent.mimeType()).isEqualTo("application/json");

                // Verify the JSON contains expected system info fields
                String json = textContent.text();
                assertThat(json).contains("timestamp");
                assertThat(json).contains("serverName");
                assertThat(json).contains("version");
                assertThat(json).contains("javaVersion");
                assertThat(json).contains("osName");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testServerConfigResource() {
        var request = McpSchema.ReadResourceRequest.builder()
            .params(McpSchema.ReadResourceRequestParams.builder()
                .uri("resource://config/server")
                .build())
            .build();

        StepVerifier.create(mcpClient.readResource(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.contents()).isNotEmpty();

                var content = response.contents().get(0);
                assertThat(content).isInstanceOf(McpSchema.TextResourceContents.class);

                McpSchema.TextResourceContents textContent = (McpSchema.TextResourceContents) content;
                assertThat(textContent.mimeType()).isEqualTo("application/json");

                // Verify the JSON contains expected config fields
                String json = textContent.text();
                assertThat(json).contains("spring-mcp-server");
                assertThat(json).contains("STATELESS");
                assertThat(json).contains("capabilities");
                assertThat(json).contains("tools");
                assertThat(json).contains("resources");
                assertThat(json).contains("prompts");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testDocumentationResourceTools() {
        var request = McpSchema.ReadResourceRequest.builder()
            .params(McpSchema.ReadResourceRequestParams.builder()
                .uri("resource://docs/tools")
                .build())
            .build();

        StepVerifier.create(mcpClient.readResource(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.contents()).isNotEmpty();

                var content = response.contents().get(0);
                assertThat(content).isInstanceOf(McpSchema.TextResourceContents.class);

                McpSchema.TextResourceContents textContent = (McpSchema.TextResourceContents) content;
                assertThat(textContent.mimeType()).isEqualTo("text/markdown");
                assertThat(textContent.text()).contains("# MCP Tools");
                assertThat(textContent.text()).contains("Calculator operations");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testDocumentationResourceResources() {
        var request = McpSchema.ReadResourceRequest.builder()
            .params(McpSchema.ReadResourceRequestParams.builder()
                .uri("resource://docs/resources")
                .build())
            .build();

        StepVerifier.create(mcpClient.readResource(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.contents()).isNotEmpty();

                var content = response.contents().get(0);
                assertThat(content).isInstanceOf(McpSchema.TextResourceContents.class);

                McpSchema.TextResourceContents textContent = (McpSchema.TextResourceContents) content;
                assertThat(textContent.mimeType()).isEqualTo("text/markdown");
                assertThat(textContent.text()).contains("# MCP Resources");
                assertThat(textContent.text()).contains("resource://welcome");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testDocumentationResourcePrompts() {
        var request = McpSchema.ReadResourceRequest.builder()
            .params(McpSchema.ReadResourceRequestParams.builder()
                .uri("resource://docs/prompts")
                .build())
            .build();

        StepVerifier.create(mcpClient.readResource(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.contents()).isNotEmpty();

                var content = response.contents().get(0);
                assertThat(content).isInstanceOf(McpSchema.TextResourceContents.class);

                McpSchema.TextResourceContents textContent = (McpSchema.TextResourceContents) content;
                assertThat(textContent.mimeType()).isEqualTo("text/markdown");
                assertThat(textContent.text()).contains("# MCP Prompts");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testDocumentationResourceGettingStarted() {
        var request = McpSchema.ReadResourceRequest.builder()
            .params(McpSchema.ReadResourceRequestParams.builder()
                .uri("resource://docs/getting-started")
                .build())
            .build();

        StepVerifier.create(mcpClient.readResource(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.contents()).isNotEmpty();

                var content = response.contents().get(0);
                assertThat(content).isInstanceOf(McpSchema.TextResourceContents.class);

                McpSchema.TextResourceContents textContent = (McpSchema.TextResourceContents) content;
                assertThat(textContent.mimeType()).isEqualTo("text/markdown");
                assertThat(textContent.text()).contains("# Getting Started");
                assertThat(textContent.text()).contains("Spring Boot");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testDocumentationResourceInvalidTopic() {
        var request = McpSchema.ReadResourceRequest.builder()
            .params(McpSchema.ReadResourceRequestParams.builder()
                .uri("resource://docs/invalid-topic")
                .build())
            .build();

        StepVerifier.create(mcpClient.readResource(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.contents()).isNotEmpty();

                var content = response.contents().get(0);
                assertThat(content).isInstanceOf(McpSchema.TextResourceContents.class);

                McpSchema.TextResourceContents textContent = (McpSchema.TextResourceContents) content;
                assertThat(textContent.text()).contains("Documentation topic 'invalid-topic' not found");
                assertThat(textContent.text()).contains("Available topics:");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testApiReferenceResource() {
        var request = McpSchema.ReadResourceRequest.builder()
            .params(McpSchema.ReadResourceRequestParams.builder()
                .uri("resource://api/reference")
                .build())
            .build();

        StepVerifier.create(mcpClient.readResource(request))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.contents()).isNotEmpty();

                var content = response.contents().get(0);
                assertThat(content).isInstanceOf(McpSchema.TextResourceContents.class);

                McpSchema.TextResourceContents textContent = (McpSchema.TextResourceContents) content;
                assertThat(textContent.mimeType()).isEqualTo("text/plain");
                assertThat(textContent.text()).contains("Spring MCP Server API Reference");
                assertThat(textContent.text()).contains("TOOLS:");
                assertThat(textContent.text()).contains("RESOURCES:");
                assertThat(textContent.text()).contains("PROMPTS:");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testResourceMetadata() {
        StepVerifier.create(mcpClient.listResources())
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.resources()).isNotEmpty();

                // Find the welcome resource and verify its metadata
                var welcomeResource = response.resources().stream()
                    .filter(resource -> "resource://welcome".equals(resource.uri()))
                    .findFirst()
                    .orElseThrow();

                assertThat(welcomeResource.name()).isEqualTo("Welcome Message");
                assertThat(welcomeResource.description()).isEqualTo("A welcome message for new users");
                assertThat(welcomeResource.mimeType()).isEqualTo("text/plain");
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testAllResourcesReadable() {
        StepVerifier.create(mcpClient.listResources())
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.resources()).isNotEmpty();

                // Verify each resource can be read (except parameterized ones)
                response.resources().stream()
                    .filter(resource -> !resource.uri().contains("{"))
                    .forEach(resource -> {
                        var request = McpSchema.ReadResourceRequest.builder()
                            .params(McpSchema.ReadResourceRequestParams.builder()
                                .uri(resource.uri())
                                .build())
                            .build();

                        StepVerifier.create(mcpClient.readResource(request))
                            .assertNext(readResponse -> {
                                assertThat(readResponse).isNotNull();
                                assertThat(readResponse.contents()).isNotEmpty();
                            })
                            .expectComplete()
                            .verify(Duration.ofSeconds(5));
                    });
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }
}
