package com.example.mcpserver.integration;

import org.springframework.ai.mcp.client.McpClient;
import org.springframework.ai.mcp.client.transport.ServerParameters;
import org.springframework.ai.mcp.client.transport.StdioClientTransport;
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
        // Configure MCP client to connect to the local test server
        ServerParameters serverParams = ServerParameters.builder("spring-mcp-server-test")
            .url("http://localhost:" + port + "/mcp")
            .build();

        StdioClientTransport transport = new StdioClientTransport(serverParams);

        return McpClient.sync(transport)
            .serverInfo()
            .name("spring-mcp-test-client")
            .version("1.0.0")
            .build();
    }
}
