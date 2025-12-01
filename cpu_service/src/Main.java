import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class Main {

    // Map of CPU instances by ID
    private static final Map<Integer, Cpu> cpuMap = new ConcurrentHashMap<>();

    // -----------------------------------
    // Handler for CPU step
    // -----------------------------------
    static class CPUHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            String[] parts = path.split("/");
            if (parts.length < 3 || !"step".equalsIgnoreCase(parts[2])) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            try {
                int cpuId = Integer.parseInt(parts[1]);
                Cpu cpu = cpuMap.computeIfAbsent(cpuId, id -> new Cpu(cpuId));

                cpu.step(); // execute one instruction

                String response = "OK";
                exchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }

            } catch (NumberFormatException e) {
                exchange.sendResponseHeaders(400, -1);
                e.printStackTrace();
            } catch (Exception e) {
                exchange.sendResponseHeaders(500, -1);
                e.printStackTrace();
            }
        }
    }

    // debug info
    static class DebugHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length < 3) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            try {
                int cpuId = Integer.parseInt(parts[2]);
                Cpu cpu = cpuMap.get(cpuId);
                if (cpu == null) {
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }

                // return registers as JSON
                String json = String.format(
                    "{ \"A\": %d, \"F\": %d, \"B\": %d, \"C\": %d, \"D\": %d, \"E\": %d, \"H\": %d, \"L\": %d, \"SP\": %d, \"PC\": %d }",
                    cpu.getA() & 0xFF, cpu.getF() & 0xFF,
                    cpu.getB() & 0xFF, cpu.getC() & 0xFF,
                    cpu.getD() & 0xFF, cpu.getE() & 0xFF,
                    cpu.getH() & 0xFF, cpu.getL() & 0xFF,
                    cpu.getSP() & 0xFFFF, cpu.getPC() & 0xFFFF
                );

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                byte[] bytes = json.getBytes();
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }

            } catch (NumberFormatException e) {
                exchange.sendResponseHeaders(400, -1);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        System.out.println("CPU service started on port " + port);

        // step CPU
        server.createContext("/", new CPUHandler());
        
        // return registers
        server.createContext("/debug", new DebugHandler());

        server.setExecutor(null); // default executor
        server.start();
    }
}
