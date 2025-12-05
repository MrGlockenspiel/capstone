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
        int entryPoint = (rom[0x100] << 24) | (rom[0x101] << 16) | (rom[0x102] << 8) | (rom[0x103]); 
        
        byte[] nintendoLogo = new byte[48];
        for (int i = 0; i < 48; i++) {
            nintendoLogo[i] = rom[0x104 + i];
        }

        String title = new String(rom, 0x134, 11, java.nio.charset.StandardCharsets.US_ASCII);
        String manufactererCode = new String(rom, 0x13F, 4, java.nio.charset.StandardCharsets.US_ASCII);

        byte cgbFlag = rom[0x143];
        
        short newLicenseeCode = (short) ((rom[0x144] & 0xFF) << 8 | (rom[0x145] & 0xFF));

        byte sgbFlag = rom[0x146];

        byte cartType = rom[0x147];

        byte romSizeByte = rom[0x148];
        byte ramSizeByte = rom[0x149];

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
        
        byte destinationCode = rom[0x14A];
        byte oldLicenseeCode = rom[0x14B];
        byte version = rom[0x14C];
        byte headerChecksum = rom[0x14D];
        
        short globalChecksum = (short) ((rom[0x14E] & 0xFF) << 8 | (rom[0x14F] & 0xFF));

        // load ROM banks into memory
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

        System.out.println("Loaded ROM:");
        System.out.println("\tEntry Point: " + String.format("0x%04X", entryPoint));

        System.out.print("\tNintendo Logo: ");
        for (byte b : nintendoLogo) {
            System.out.print(String.format("%02X ", b & 0xFF));
        }
        System.out.println();

        System.out.println("\tTitle: " + title.trim());
        System.out.println("\tManufacturer Code: " + manufactererCode.trim());
        System.out.println("\tCGB Flag: " + String.format("0x%02X", cgbFlag & 0xFF));
        System.out.println("\tNew Licensee Code: " + String.format("0x%04X", newLicenseeCode));
        System.out.println("\tSGB Flag: " + String.format("0x%02X", sgbFlag & 0xFF));
        System.out.println("\tCartridge Type: " + String.format("0x%02X", cartType & 0xFF));
        System.out.println("\tROM Size Byte: " + String.format("0x%02X", romSizeByte));
        System.out.println("\tCalculated ROM Size: " + romSize + " bytes");
        System.out.println("\tRAM Size Byte: " + String.format("0x%02X", ramSizeByte));
        System.out.println("\tCalculated RAM Size: " + ramSize + " bytes");
        System.out.println("\tDestination Code: " + String.format("0x%02X", destinationCode & 0xFF));
        System.out.println("\tOld Licensee Code: " + String.format("0x%02X", oldLicenseeCode & 0xFF));
        System.out.println("\tVersion: " + String.format("0x%02X", version & 0xFF));
        System.out.println("\tHeader Checksum: " + String.format("0x%02X", headerChecksum & 0xFF));
        System.out.println("\tGlobal Checksum: " + String.format("0x%04X", globalChecksum & 0xFFFF));
        
    }

    private static void writeMemory(String consoleId, int address, byte[] data, int offset, int length) throws IOException {
        URL url = new URL(String.format("http://memory_service:8080/%s/%d?len=%d", consoleId, address, length));
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
