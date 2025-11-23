package com.example.mcpserver.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonRpcResponse {
    private String jsonrpc = "2.0";
    private Object result;
    private JsonRpcError error;
    private Object id;

    public static JsonRpcResponse success(Object result, Object id) {
        JsonRpcResponse response = new JsonRpcResponse();
        response.setJsonrpc("2.0");
        response.setResult(result);
        response.setId(id);
        return response;
    }

    public static JsonRpcResponse error(int code, String message, Object id) {
        JsonRpcResponse response = new JsonRpcResponse();
        response.setJsonrpc("2.0");
        response.setError(new JsonRpcError(code, message, null));
        response.setId(id);
        return response;
    }
}
