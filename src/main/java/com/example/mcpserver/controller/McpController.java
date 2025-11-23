package com.example.mcpserver.controller;

import com.example.mcpserver.model.JsonRpcRequest;
import com.example.mcpserver.model.JsonRpcResponse;
import com.example.mcpserver.service.McpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mcp")
public class McpController {

    private static final Logger logger = LoggerFactory.getLogger(McpController.class);
    private final McpService mcpService;

    public McpController(McpService mcpService) {
        this.mcpService = mcpService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonRpcResponse handleRequest(@RequestBody JsonRpcRequest request) {
        logger.info("Received MCP request: method={}, id={}", request.getMethod(), request.getId());

        try {
            return mcpService.handleRequest(request);
        } catch (Exception e) {
            logger.error("Error handling MCP request", e);
            return JsonRpcResponse.error(-32603, "Internal error: " + e.getMessage(), request.getId());
        }
    }
}
