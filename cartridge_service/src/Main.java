import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;

public class Main {
    private static final int PORT = 8080;

    // TODO: Add MBC1/2/3/5 bank switching

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new CartridgeHandler());
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
        System.out.println("Cartridge service running on port " + PORT);
    }

    static class CartridgeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath(); // /{id}/load
            String[] parts = path.split("/");
            if (parts.length < 3 || !"load".equals(parts[2])) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }

            String consoleId = parts[1];

            if ("POST".equals(method)) {
                try (InputStream is = exchange.getRequestBody()) {
                    byte[] rom = readAllBytes(is);
                    loadROM(consoleId, rom);
                    exchange.sendResponseHeaders(200, -1);
                } catch (Exception e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, -1);
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // Method not allowed
            }
        }
    }

    private static byte[] readAllBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] tmp = new byte[4096];
        int read;
        while ((read = is.read(tmp)) != -1) {
            buffer.write(tmp, 0, read);
        }
        return buffer.toByteArray();
    }

    private static void loadROM(String consoleId, byte[] rom) throws IOException {
        if (rom.length < 0x8000) {
            throw new IllegalArgumentException("ROM too small to load");
        }

        // parse ROM header
        int romSizeByte = rom[0x148] & 0xFF;
        int ramSizeByte = rom[0x149] & 0xFF;

        int romSize = 0x8000; // minimum 32KB
        if (romSizeByte < 8) {
            romSize = 0x8000 << romSizeByte;
        } else {
            // special cases for large ROMs
            romSize = (32 * 1024) * (1 << (romSizeByte - 1));
        }

        int ramSize;
        switch (ramSizeByte) {
            case 0: 
                ramSize = 0; 
                break;
            case 1: 
                ramSize = 2 * 1024; 
                break;
            case 2: 
                ramSize = 8 * 1024; 
                break;
            case 3: 
                ramSize = 32 * 1024; 
                break;
            case 4: 
                ramSize = 128 * 1024;
                break;
            default: 
                ramSize = 0;
                break;
        }

        // Load ROM banks into memory
        int address = 0x0000;
        int offset = 0;
        while (offset < rom.length && address < 0x8000) {
            int len = Math.min(0x4000, rom.length - offset); // 16KB bank
            writeMemory(consoleId, address, rom, offset, len);
            address += len;
            offset += len;
        }

        // optionally clear cartridge RAM area if needed
        if (ramSize > 0) {
            byte[] emptyRam = new byte[ramSize];
            writeMemory(consoleId, 0xA000, emptyRam, 0, ramSize);
        }

        System.out.println("ROM loaded for console " + consoleId + ", ROM size: " + rom.length + " bytes");
    }

    private static void writeMemory(String consoleId, int address, byte[] data, int offset, int length) throws IOException {
        URL url = new URL(String.format("http://memory_service:8080/%s/%d", consoleId, address));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/octet-stream");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(data, offset, length);
        }

        int response = conn.getResponseCode();
        if (response != 200) {
            throw new IOException("Failed to write memory: HTTP " + response);
        }
    }
}
