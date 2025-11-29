import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;

public class Main {
    private static final int PORT = 8080;

    // TODO: Add MBC1/2/3/5 bank switching
    
    /*
    private static final byte[] BIOS = {
        // jp $100 lmaio
        (byte)0xC3, (byte)0x00, (byte)0x01,
    };
    */

    private static final byte[] BIOS = {
        (byte)0x31, (byte)0xfe, (byte)0xff, (byte)0xaf, (byte)0x21, (byte)0xff, (byte)0x9f, (byte)0x32, (byte)0xcb, (byte)0x7c, (byte)0x20, (byte)0xfb,
        (byte)0x21, (byte)0x26, (byte)0xff, (byte)0x0e, (byte)0x11, (byte)0x3e, (byte)0x80, (byte)0x32, (byte)0xe2, (byte)0x0c, (byte)0x3e, (byte)0xf3,
        (byte)0xe2, (byte)0x32, (byte)0x3e, (byte)0x77, (byte)0x77, (byte)0x3e, (byte)0xfc, (byte)0xe0, (byte)0x47, (byte)0x11, (byte)0x04, (byte)0x01,
        (byte)0x21, (byte)0x10, (byte)0x80, (byte)0x1a, (byte)0xcd, (byte)0x95, (byte)0x00, (byte)0xcd, (byte)0x96, (byte)0x00, (byte)0x13, (byte)0x7b,
        (byte)0xfe, (byte)0x34, (byte)0x20, (byte)0xf3, (byte)0x11, (byte)0xd8, (byte)0x00, (byte)0x06, (byte)0x08, (byte)0x1a, (byte)0x13, (byte)0x22,
        (byte)0x23, (byte)0x05, (byte)0x20, (byte)0xf9, (byte)0x3e, (byte)0x19, (byte)0xea, (byte)0x10, (byte)0x99, (byte)0x21, (byte)0x2f, (byte)0x99,
        (byte)0x0e, (byte)0x0c, (byte)0x3d, (byte)0x28, (byte)0x08, (byte)0x32, (byte)0x0d, (byte)0x20, (byte)0xf9, (byte)0x2e, (byte)0x0f, (byte)0x18,
        (byte)0xf3, (byte)0x67, (byte)0x3e, (byte)0x64, (byte)0x57, (byte)0xe0, (byte)0x42, (byte)0x3e, (byte)0x91, (byte)0xe0, (byte)0x40, (byte)0x04,
        (byte)0x1e, (byte)0x02, (byte)0x0e, (byte)0x0c, (byte)0xf0, (byte)0x44, (byte)0xfe, (byte)0x90, (byte)0x20, (byte)0xfa, (byte)0x0d, (byte)0x20,
        (byte)0xf7, (byte)0x1d, (byte)0x20, (byte)0xf2, (byte)0x0e, (byte)0x13, (byte)0x24, (byte)0x7c, (byte)0x1e, (byte)0x83, (byte)0xfe, (byte)0x62,
        (byte)0x28, (byte)0x06, (byte)0x1e, (byte)0xc1, (byte)0xfe, (byte)0x64, (byte)0x20, (byte)0x06, (byte)0x7b, (byte)0xe2, (byte)0x0c, (byte)0x3e,
        (byte)0x87, (byte)0xe2, (byte)0xf0, (byte)0x42, (byte)0x90, (byte)0xe0, (byte)0x42, (byte)0x15, (byte)0x20, (byte)0xd2, (byte)0x05, (byte)0x20,
        (byte)0x4f, (byte)0x16, (byte)0x20, (byte)0x18, (byte)0xcb, (byte)0x4f, (byte)0x06, (byte)0x04, (byte)0xc5, (byte)0xcb, (byte)0x11, (byte)0x17,
        (byte)0xc1, (byte)0xcb, (byte)0x11, (byte)0x17, (byte)0x05, (byte)0x20, (byte)0xf5, (byte)0x22, (byte)0x23, (byte)0x22, (byte)0x23, (byte)0xc9,
        (byte)0xce, (byte)0xed, (byte)0x66, (byte)0x66, (byte)0xcc, (byte)0x0d, (byte)0x00, (byte)0x0b, (byte)0x03, (byte)0x73, (byte)0x00, (byte)0x83,
        (byte)0x00, (byte)0x0c, (byte)0x00, (byte)0x0d, (byte)0x00, (byte)0x08, (byte)0x11, (byte)0x1f, (byte)0x88, (byte)0x89, (byte)0x00, (byte)0x0e,
        (byte)0xdc, (byte)0xcc, (byte)0x6e, (byte)0xe6, (byte)0xdd, (byte)0xdd, (byte)0xd9, (byte)0x99, (byte)0xbb, (byte)0xbb, (byte)0x67, (byte)0x63,
        (byte)0x6e, (byte)0x0e, (byte)0xec, (byte)0xcc, (byte)0xdd, (byte)0xdc, (byte)0x99, (byte)0x9f, (byte)0xbb, (byte)0xb9, (byte)0x33, (byte)0x3e,
        (byte)0x3c, (byte)0x42, (byte)0xb9, (byte)0xa5, (byte)0xb9, (byte)0xa5, (byte)0x42, (byte)0x3c, (byte)0x21, (byte)0x04, (byte)0x01, (byte)0x11,
        (byte)0xa8, (byte)0x00, (byte)0x1a, (byte)0x13, (byte)0xbe, (byte)0x20, (byte)0xfe, (byte)0x23, (byte)0x7d, (byte)0xfe, (byte)0x34, (byte)0x20,
        (byte)0xf5, (byte)0x06, (byte)0x19, (byte)0x78, (byte)0x86, (byte)0x23, (byte)0x05, (byte)0x20, (byte)0xfb, (byte)0x86, (byte)0x20, (byte)0xfe,
        (byte)0x3e, (byte)0x01, (byte)0xe0, (byte)0x50,
    };

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
        
        // write BIOS to 0x0
        writeMemory(consoleId, 0x0, BIOS, 0, BIOS.length);
        System.out.println("Wrote BIOS");

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
