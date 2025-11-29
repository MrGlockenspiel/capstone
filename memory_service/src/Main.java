import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    private static final int MEMORY_SIZE = 0xFFFF;
    private static final Map<Integer, byte[]> memoryMap = new ConcurrentHashMap<>();

    static class MemoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();

            String[] parts = path.split("/");
            if (parts.length < 3) {
                System.out.println("Invalid request path: " + path);
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            try {
                int instance = Integer.parseInt(parts[1]);
                int address = Integer.parseInt(parts[2]);
                int len = 1;

                if (query != null && query.startsWith("len=")) {
                    len = Integer.parseInt(query.substring(4));
                }

                if (address < 0 || address + len > MEMORY_SIZE) {
                    System.out.printf("Invalid memory range: instance=%d, address=0x%04X, len=%d%n", instance, address, len);
                    exchange.sendResponseHeaders(400, -1);
                    return;
                }

                byte[] memory = memoryMap.computeIfAbsent(instance, k -> new byte[MEMORY_SIZE]);

                if ("GET".equalsIgnoreCase(method)) {
                    System.out.printf("[GET]  Instance %d, Address 0x%04X, Length %d%n", instance, address, len);
                    exchange.getResponseHeaders().set("Content-Type", "application/octet-stream");
                    exchange.sendResponseHeaders(200, len);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(memory, address, len);
                    }
                } else if ("POST".equalsIgnoreCase(method)) {
                    System.out.printf("[POST] Instance %d, Address 0x%04X, Length %d%n", instance, address, len);
                    InputStream is = exchange.getRequestBody();
                    int readTotal = 0;
                    while (readTotal < len) {
                        int read = is.read(memory, address + readTotal, len - readTotal);
                        if (read == -1) break;
                        readTotal += read;
                    }
                    exchange.sendResponseHeaders(200, 0); // no body
                    exchange.getResponseBody().close();
                } else {
                    System.out.println("Unsupported method: " + method);
                    exchange.sendResponseHeaders(405, -1);
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid number in path: " + path);
                exchange.sendResponseHeaders(400, -1);
            }
        }
    }


    public static void main(String[] args) throws IOException {
        final int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("Memory service started on port " + port);

        // define endpoints
        server.createContext("/", new MemoryHandler());

        // start server with a default thread pool
        server.setExecutor(null);
        server.start();
    }
}
