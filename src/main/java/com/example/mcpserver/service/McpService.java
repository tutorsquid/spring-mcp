package com.example.mcpserver.service;

import com.example.mcpserver.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class McpService {

    private static final Logger logger = LoggerFactory.getLogger(McpService.class);
    private final ObjectMapper objectMapper;
    private final ToolService toolService;

    public McpService(ObjectMapper objectMapper, ToolService toolService) {
        this.objectMapper = objectMapper;
        this.toolService = toolService;
    }

    public JsonRpcResponse handleRequest(JsonRpcRequest request) {
        String method = request.getMethod();

        return switch (method) {
            case "initialize" -> handleInitialize(request);
            case "tools/list" -> handleToolsList(request);
            case "tools/call" -> handleToolsCall(request);
            case "resources/list" -> handleResourcesList(request);
            case "resources/read" -> handleResourcesRead(request);
            default -> JsonRpcResponse.error(-32601, "Method not found: " + method, request.getId());
        };
    }

    private JsonRpcResponse handleInitialize(JsonRpcRequest request) {
        ServerInfo serverInfo = ServerInfo.builder()
                .name("Spring MCP Server")
                .version("1.0.0")
                .build();

        Map<String, Object> capabilities = new HashMap<>();
        capabilities.put("tools", Map.of("listChanged", false));
        capabilities.put("resources", Map.of("subscribe", false, "listChanged", false));

        InitializeResult result = InitializeResult.builder()
                .protocolVersion("2024-11-05")
                .serverInfo(serverInfo)
                .capabilities(capabilities)
                .build();

        return JsonRpcResponse.success(result, request.getId());
    }

    private JsonRpcResponse handleToolsList(JsonRpcRequest request) {
        List<Tool> tools = toolService.getAvailableTools();
        Map<String, Object> result = Map.of("tools", tools);
        return JsonRpcResponse.success(result, request.getId());
    }

    @SuppressWarnings("unchecked")
    private JsonRpcResponse handleToolsCall(JsonRpcRequest request) {
        try {
            Map<String, Object> params = objectMapper.convertValue(request.getParams(), Map.class);
            String toolName = (String) params.get("name");
            Map<String, Object> arguments = (Map<String, Object>) params.get("arguments");

            Object result = toolService.executeTool(toolName, arguments);

            Map<String, Object> response = new HashMap<>();
            response.put("content", List.of(
                Map.of(
                    "type", "text",
                    "text", result.toString()
                )
            ));

            return JsonRpcResponse.success(response, request.getId());
        } catch (Exception e) {
            logger.error("Error executing tool", e);
            return JsonRpcResponse.error(-32603, "Tool execution error: " + e.getMessage(), request.getId());
        }
    }

    private JsonRpcResponse handleResourcesList(JsonRpcRequest request) {
        List<Resource> resources = List.of(
            Resource.builder()
                .uri("file:///example.txt")
                .name("Example Resource")
                .description("An example resource")
                .mimeType("text/plain")
                .build()
        );
        Map<String, Object> result = Map.of("resources", resources);
        return JsonRpcResponse.success(result, request.getId());
    }

    @SuppressWarnings("unchecked")
    private JsonRpcResponse handleResourcesRead(JsonRpcRequest request) {
        try {
            Map<String, Object> params = objectMapper.convertValue(request.getParams(), Map.class);
            String uri = (String) params.get("uri");

            Map<String, Object> result = new HashMap<>();
            result.put("contents", List.of(
                Map.of(
                    "uri", uri,
                    "mimeType", "text/plain",
                    "text", "This is example content for: " + uri
                )
            ));

            return JsonRpcResponse.success(result, request.getId());
        } catch (Exception e) {
            logger.error("Error reading resource", e);
            return JsonRpcResponse.error(-32603, "Resource read error: " + e.getMessage(), request.getId());
        }
    }
}
