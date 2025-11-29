import java.io.*;
import java.net.*;

public class Io {
    private static final String URI = "http://memory_service:8080";

    public static byte read8(Cpu cpu, int addr) throws IOException {
        URL url = new URL(String.format("%s/%d/%d", URI, cpu.getId(), addr));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int response = conn.getResponseCode();
        if (response != 200) {
            throw new IOException("Failed to read memory: HTTP " + response);
        }

        try (InputStream is = conn.getInputStream()) {
            int value = is.read();
            if (value == -1) {
                System.out.println("Memory read returned no data");
                throw new IOException("Memory read returned no data");
            }
            return (byte)(value & 0xFF);
        }
    }

    public static void write8(Cpu cpu, int addr, byte b) throws IOException {
        URL url = new URL(String.format("%s/%d/%d", URI, cpu.getId(), addr));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/octet-stream");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(new byte[]{b});
        }

        int response = conn.getResponseCode();
        if (response != 200) {
            System.out.println("Failed to write memory: HTTP " + response);
            throw new IOException("Failed to write memory: HTTP " + response);
        }
    }
}