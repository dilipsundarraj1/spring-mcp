# MCP in SpringBoot 

## How does the Transport Work under the hood?

### HttpClientSseClientTransport

-  There is a **HttpClientSseClientTransport** responsible for sending the request to the server and receiving the response.
- sendMessage - Sends a JSON-RPC message to the server.

####  Call to retrieve the tools

**Endpoint** - /mcp/messages?sessionId=f79df0c4-cf0d-4fd5-ae87-8b08b028bada

**RequestBody:** 
```json
{
  "jsonrpc": "2.0",
  "method": "initialize",
  "id": "e20b0b2e-0",
  "params": {
    "protocolVersion": "2024-11-05",
    "capabilities": {},
    "clientInfo": {
      "name": "spring-ai-mcp-client - person-mcp-server",
      "version": "1.0.0"
    }
  }
}

```
#### Tool call to retrieve the data

```
{
  "jsonrpc": "2.0",
  "method": "tools/call",
  "id": "deacff79-2",
  "params": {
    "name": "getPersonsByNationality",
    "arguments": {
      "nationality": "India"
    }
  }
}
```

