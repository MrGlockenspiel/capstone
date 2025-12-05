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
            0xAAAAAAFF, // light gray
            0x555555FF, // dark gray
            0x000000FF  // black
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

        // fetch VRAM, OAM, and LCD registers
        byte[] vram = fetchMemory(consoleId, VRAM_START, VRAM_SIZE);
        byte[] oam = fetchMemory(consoleId, OAM_START, OAM_SIZE);
        byte[] lcdRegs = fetchMemory(consoleId, 0xFF40, 0x10); // FF40-FF4F
        
        byte[] palettes = fetchMemory(consoleId, 0xFF47, 3); // BGP, OBP0, OBP1
        int bgPalette = palettes[0] & 0xFF;
        int obp0 = palettes[1] & 0xFF;
        int obp1 = palettes[2] & 0xFF;
        
        int[] colorMapping = new int[4];
        colorMapping[0] = bgPalette & 0x03;
        colorMapping[1] = (bgPalette >> 2) & 0x03;
        colorMapping[2] = (bgPalette >> 4) & 0x03;
        colorMapping[3] = (bgPalette >> 4) & 0x03;

        int scx = lcdRegs[3] & 0xFF; // FF43
        int scy = lcdRegs[2] & 0xFF; // FF42
        boolean bgTileDataSelect = (lcdRegs[0] & 0x10) != 0; // FF40 bit 4
        int bgMapSelect = (lcdRegs[0] & 0x08) != 0 ? 0x1C00 : 0x1800; // FF40 bit 3

        // render background
        for (int screenY = 0; screenY < SCREEN_HEIGHT; screenY++) {
            int bgY = (screenY + scy) & 0xFF; // wrap around 256
            int tileY = bgY / 8;
            int pixelY = bgY % 8;

            for (int screenX = 0; screenX < SCREEN_WIDTH; screenX++) {
                int bgX = (screenX + scx) & 0xFF; // wrap around 256
                int tileX = bgX / 8;
                int pixelX = bgX % 8;

                // choose BG map
                int tileIndex = vram[bgMapSelect + tileY * 32 + tileX] & 0xFF;

                int tileAddr;
                if (bgTileDataSelect) {
                    // $8000 mode
                    tileAddr = tileIndex * 16;
                } else {
                    // $9000 mode, signed index
                    tileIndex = (byte) tileIndex; // sign extension (sext lmaio)
                    tileAddr = 0x1000 + tileIndex * 16; // $9000 in VRAM
                }

                byte b1 = vram[tileAddr + pixelY * 2];
                byte b2 = vram[tileAddr + pixelY * 2 + 1];

                int colorBit = ((b2 >> (7 - pixelX)) & 1) << 1 | ((b1 >> (7 - pixelX)) & 1);

                int offset = (screenY * SCREEN_WIDTH + screenX) * 4;
                
                int color = PALETTE[colorMapping[colorBit]]; // still using fixed palette
                
                framebuffer[offset] = (byte) ((color >> 24) & 0xFF);
                framebuffer[offset + 1] = (byte) ((color >> 16) & 0xFF);
                framebuffer[offset + 2] = (byte) ((color >> 8) & 0xFF);
                framebuffer[offset + 3] = (byte) (color & 0xFF);
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

            renderTile(vram, tileIndex, xPos, yPos, framebuffer, xFlip, yFlip, true, bgPalette, obp0, obp1);
        }

        return framebuffer;
    }

    private static void renderTile(byte[] vram, int tileIndex, int startX, int startY, byte[] framebuffer,
                                boolean xFlip, boolean yFlip, boolean isSprite,
                                int bgPalette, int obp0, int obp1) {

        int tileAddr = tileIndex * 16; // 16 bytes per tile

        for (int row = 0; row < 8; row++) {
            int srcRow = yFlip ? 7 - row : row;
            byte b1 = vram[tileAddr + srcRow * 2];
            byte b2 = vram[tileAddr + srcRow * 2 + 1];

            for (int col = 0; col < 8; col++) {
                int srcCol = xFlip ? 7 - col : col;

                int colorBit = ((b2 >> (7 - srcCol)) & 1) << 1 | ((b1 >> (7 - srcCol)) & 1);
                if (colorBit == 0 && isSprite) {
                    continue; // transparent pixel
                }

                int pixelX = startX + col;
                int pixelY = startY + row;
                if (pixelX < 0 || pixelX >= SCREEN_WIDTH || pixelY < 0 || pixelY >= SCREEN_HEIGHT) continue;

                int offset = (pixelY * SCREEN_WIDTH + pixelX) * 4;

                int color;
                if (isSprite) {
                    boolean useObp1 = ((colorBit & 2) != 0);
                    color = PALETTE[(useObp1 ? obp1 : obp0) & 0x03];
                } else {
                    color = PALETTE[bgPalette & 0x03];
                }

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
