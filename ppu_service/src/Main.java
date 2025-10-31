import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;

public class Main {

    private static final int SCREEN_WIDTH = 160;
    private static final int SCREEN_HEIGHT = 144;

    // Game Boy memory regions
    private static final int VRAM_START = 0x8000;
    private static final int VRAM_SIZE = 0x2000;
    private static final int OAM_START = 0xFE00;
    private static final int OAM_SIZE = 0xA0;

    private static final int PORT = 8080;

    // palette mapping (DMG shades)
    private static final int[] PALETTE = new int[]{
            0xFFFFFFFF, // white
            0xFFAAAAAA, // light gray
            0xFF555555, // dark gray
            0xFF000000  // black
    };

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new FramebufferHandler());
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
        System.out.println("PPU service running on port " + PORT);
    }

    static class FramebufferHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String path = exchange.getRequestURI().getPath(); // /{id}/framebuffer
            String[] parts = path.split("/");
            if (parts.length < 3 || !"framebuffer".equals(parts[2])) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }

            String consoleId = parts[1];

            try {
                byte[] framebuffer = renderFrame(consoleId);
                exchange.getResponseHeaders().set("Content-Type", "application/octet-stream");
                exchange.sendResponseHeaders(200, framebuffer.length);
                OutputStream os = exchange.getResponseBody();
                os.write(framebuffer);
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        }
    }

    private static byte[] renderFrame(String consoleId) throws IOException {
        byte[] framebuffer = new byte[SCREEN_WIDTH * SCREEN_HEIGHT * 4];

        // fetch VRAM and OAM
        byte[] vram = fetchMemory(consoleId, VRAM_START, VRAM_SIZE);
        byte[] oam = fetchMemory(consoleId, OAM_START, OAM_SIZE);

        // render background (INACCURATE: full 32x32 tiles, no scrolling/window)
        for (int tileY = 0; tileY < 18; tileY++) { // 144/8
            for (int tileX = 0; tileX < 20; tileX++) { // 160/8
                int tileIndex = vram[0x1800 + tileY * 32 + tileX] & 0xFF; // BG map at 0x9800 -> offset 0x1800 in VRAM
                renderTile(vram, tileIndex, tileX * 8, tileY * 8, framebuffer);
            }
        }

        // render sprites
        for (int i = 0; i < 40; i++) {
            int spriteBase = i * 4;
            int yPos = (oam[spriteBase] & 0xFF) - 16;
            int xPos = (oam[spriteBase + 1] & 0xFF) - 8;
            int tileIndex = oam[spriteBase + 2] & 0xFF;
            int attributes = oam[spriteBase + 3] & 0xFF;
            boolean xFlip = (attributes & 0x20) != 0;
            boolean yFlip = (attributes & 0x40) != 0;

            renderTile(vram, tileIndex, xPos, yPos, framebuffer, xFlip, yFlip, true);
        }

        return framebuffer;
    }

    private static void renderTile(byte[] vram, int tileIndex, int startX, int startY, byte[] framebuffer) {
        renderTile(vram, tileIndex, startX, startY, framebuffer, false, false, false);
    }

    // completely guessing, untested
    private static void renderTile(byte[] vram, int tileIndex, int startX, int startY, byte[] framebuffer,
                                   boolean xFlip, boolean yFlip, boolean isSprite) {
        int tileAddr = 0x0000 + tileIndex * 16; // 16 bytes per tile
        for (int row = 0; row < 8; row++) {
            int srcRow = yFlip ? 7 - row : row;
            byte b1 = vram[tileAddr + srcRow * 2];
            byte b2 = vram[tileAddr + srcRow * 2 + 1];

            for (int col = 0; col < 8; col++) {
                int srcCol = xFlip ? col : 7 - col;
                int colorBit = ((b1 >> srcCol) & 1) | (((b2 >> srcCol) & 1) << 1);
                if (colorBit == 0 && isSprite) {
                    continue; // transparent sprite pixel
                }

                int pixelX = startX + col;
                int pixelY = startY + row;
                if (pixelX < 0 || pixelX >= SCREEN_WIDTH || pixelY < 0 || pixelY >= SCREEN_HEIGHT) continue;

                int offset = (pixelY * SCREEN_WIDTH + pixelX) * 4;
                int color = PALETTE[colorBit];
                framebuffer[offset] = (byte) ((color >> 24) & 0xFF);
                framebuffer[offset + 1] = (byte) ((color >> 16) & 0xFF);
                framebuffer[offset + 2] = (byte) ((color >> 8) & 0xFF);
                framebuffer[offset + 3] = (byte) (color & 0xFF);
            }
        }
    }

    private static byte[] fetchMemory(String consoleId, int address, int len) throws IOException {
        String urlStr = String.format("http://memory_service:8080/%s/%d?len=%d", consoleId, address, len);
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (InputStream in = conn.getInputStream();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            byte[] tmp = new byte[4096];
            int read;
            while ((read = in.read(tmp)) != -1) {
                buffer.write(tmp, 0, read);
            }
            return buffer.toByteArray();
        }
    }
}
