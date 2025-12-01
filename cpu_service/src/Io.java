import java.io.*;
import java.net.*;
import java.util.Arrays;

public class Io {
    private static final String URI = "http://memory_service:8080";
    private static final int PAGE_SIZE = 0x1000;

    // maintain 4KB pages and track writes (god what am i doing)
    private static byte[][] pages = new byte[0x10000 / PAGE_SIZE][];
    private static boolean[] dirty = new boolean[0x10000 / PAGE_SIZE];

    // read a single byte
    public static byte read8(Cpu cpu, int addr) throws IOException {
        int pageIndex = (addr & 0xFFFF) / PAGE_SIZE;
        int offset = (addr & 0xFFFF) % PAGE_SIZE;

        if (pages[pageIndex] == null) {
            loadPage(cpu, pageIndex);
        }
        return pages[pageIndex][offset];
    }

    // write a single byte
    public static void write8(Cpu cpu, int addr, byte value) throws IOException {
        int pageIndex = (addr & 0xFFFF) / PAGE_SIZE;
        int offset = (addr & 0xFFFF) % PAGE_SIZE;

        if (pages[pageIndex] == null) {
            loadPage(cpu, pageIndex);
        }
        pages[pageIndex][offset] = value;
        if (!dirty[pageIndex]) {
            dirty[pageIndex] = true;
            commitPage(cpu, pageIndex);
        }
    }

    private static void loadPage(Cpu cpu, int pageIndex) throws IOException {
        int startAddr = pageIndex * PAGE_SIZE;

        URL url = new URL(String.format("%s/%d/%d?len=%d", URI, cpu.getId(), startAddr, PAGE_SIZE));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            throw new IOException("Failed to load page: HTTP " + conn.getResponseCode());
        }

        byte[] buffer = new byte[PAGE_SIZE];
        try (InputStream is = conn.getInputStream()) {
            int read = 0;
            while (read < PAGE_SIZE) {
                int n = is.read(buffer, read, PAGE_SIZE - read);
                if (n == -1) break;
                read += n;
            }
        }

        pages[pageIndex] = buffer;
        dirty[pageIndex] = false;
    }

    private static void commitPage(Cpu cpu, int pageIndex) throws IOException {
        byte[] buffer;
        
        if (!dirty[pageIndex] || pages[pageIndex] == null) {
            return;
        }
        // copy to avoid race
        buffer = Arrays.copyOf(pages[pageIndex], PAGE_SIZE); 
        dirty[pageIndex] = false;

        int startAddr = pageIndex * PAGE_SIZE;

        URL url = new URL(String.format("%s/%d/%d?len=%d", URI, cpu.getId(), startAddr, PAGE_SIZE));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/octet-stream");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(buffer);
        }

        if (conn.getResponseCode() != 200) {
            throw new IOException("Failed to write page: HTTP " + conn.getResponseCode());
        }
    }

    // commit all pages synchronously
    public static void commitAll(Cpu cpu) throws IOException {
        for (int i = 0; i < pages.length; i++) {
            if (dirty[i] && pages[i] != null) {
                commitPage(cpu, i);
            }
        }
    }
}
