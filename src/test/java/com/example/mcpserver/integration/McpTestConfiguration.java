package com.example.mcpserver.integration;

import org.springframework.ai.mcp.client.McpClient;
import org.springframework.ai.mcp.client.McpSyncClient;
import org.springframework.ai.mcp.client.transport.SseClientTransport;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

/**
 * Test configuration for MCP integration tests.
 * Provides MCP client bean configured to connect to the test server.
 */
@TestConfiguration
@Profile("test")
public class McpTestConfiguration {

    @LocalServerPort
    private int port;

    @Bean
    public McpClient mcpClient() {
        // Configure MCP client to connect to the local test server using SSE transport
        String serverUrl = "http://localhost:" + port + "/mcp";
        SseClientTransport transport = new SseClientTransport(serverUrl);

        return new McpSyncClient(transport);
    }
}
