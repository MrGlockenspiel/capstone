import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Io {
    private static final String URI = "http://memory_service:8080";
    private static final int PAGE_SIZE = 0x1000;

    // maintain 4KB pages and track writes (god what am i doing)
    private static final Map<Cpu, CpuMemory> cpuCache = new HashMap<>();

    // BIOS data
    private static final byte[] BIOS = {
        (byte)0x31, (byte)0xfe, (byte)0xff, (byte)0xaf, (byte)0x21, (byte)0xff, (byte)0x9f, (byte)0x32, 
        (byte)0xcb, (byte)0x7c, (byte)0x20, (byte)0xfb, (byte)0x21, (byte)0x26, (byte)0xff, (byte)0x0e, 
        (byte)0x11, (byte)0x3e, (byte)0x80, (byte)0x32, (byte)0xe2, (byte)0x0c, (byte)0x3e, (byte)0xf3,
        (byte)0xe2, (byte)0x32, (byte)0x3e, (byte)0x77, (byte)0x77, (byte)0x3e, (byte)0xfc, (byte)0xe0, 
        (byte)0x47, (byte)0x11, (byte)0x04, (byte)0x01, (byte)0x21, (byte)0x10, (byte)0x80, (byte)0x1a, 
        (byte)0xcd, (byte)0x95, (byte)0x00, (byte)0xcd, (byte)0x96, (byte)0x00, (byte)0x13, (byte)0x7b,
        (byte)0xfe, (byte)0x34, (byte)0x20, (byte)0xf3, (byte)0x11, (byte)0xd8, (byte)0x00, (byte)0x06, 
        (byte)0x08, (byte)0x1a, (byte)0x13, (byte)0x22, (byte)0x23, (byte)0x05, (byte)0x20, (byte)0xf9, 
        (byte)0x3e, (byte)0x19, (byte)0xea, (byte)0x10, (byte)0x99, (byte)0x21, (byte)0x2f, (byte)0x99,
        (byte)0x0e, (byte)0x0c, (byte)0x3d, (byte)0x28, (byte)0x08, (byte)0x32, (byte)0x0d, (byte)0x20, 
        (byte)0xf9, (byte)0x2e, (byte)0x0f, (byte)0x18, (byte)0xf3, (byte)0x67, (byte)0x3e, (byte)0x64, 
        (byte)0x57, (byte)0xe0, (byte)0x42, (byte)0x3e, (byte)0x91, (byte)0xe0, (byte)0x40, (byte)0x04,
        (byte)0x1e, (byte)0x02, (byte)0x0e, (byte)0x0c, (byte)0xf0, (byte)0x44, (byte)0xfe, (byte)0x90, 
        (byte)0x20, (byte)0xfa, (byte)0x0d, (byte)0x20, (byte)0xf7, (byte)0x1d, (byte)0x20, (byte)0xf2, 
        (byte)0x0e, (byte)0x13, (byte)0x24, (byte)0x7c, (byte)0x1e, (byte)0x83, (byte)0xfe, (byte)0x62,
        (byte)0x28, (byte)0x06, (byte)0x1e, (byte)0xc1, (byte)0xfe, (byte)0x64, (byte)0x20, (byte)0x06, 
        (byte)0x7b, (byte)0xe2, (byte)0x0c, (byte)0x3e, (byte)0x87, (byte)0xe2, (byte)0xf0, (byte)0x42, 
        (byte)0x90, (byte)0xe0, (byte)0x42, (byte)0x15, (byte)0x20, (byte)0xd2, (byte)0x05, (byte)0x20,
        (byte)0x4f, (byte)0x16, (byte)0x20, (byte)0x18, (byte)0xcb, (byte)0x4f, (byte)0x06, (byte)0x04, 
        (byte)0xc5, (byte)0xcb, (byte)0x11, (byte)0x17, (byte)0xc1, (byte)0xcb, (byte)0x11, (byte)0x17, 
        (byte)0x05, (byte)0x20, (byte)0xf5, (byte)0x22, (byte)0x23, (byte)0x22, (byte)0x23, (byte)0xc9,
        (byte)0xce, (byte)0xed, (byte)0x66, (byte)0x66, (byte)0xcc, (byte)0x0d, (byte)0x00, (byte)0x0b, 
        (byte)0x03, (byte)0x73, (byte)0x00, (byte)0x83, (byte)0x00, (byte)0x0c, (byte)0x00, (byte)0x0d, 
        (byte)0x00, (byte)0x08, (byte)0x11, (byte)0x1f, (byte)0x88, (byte)0x89, (byte)0x00, (byte)0x0e,
        (byte)0xdc, (byte)0xcc, (byte)0x6e, (byte)0xe6, (byte)0xdd, (byte)0xdd, (byte)0xd9, (byte)0x99, 
        (byte)0xbb, (byte)0xbb, (byte)0x67, (byte)0x63, (byte)0x6e, (byte)0x0e, (byte)0xec, (byte)0xcc, 
        (byte)0xdd, (byte)0xdc, (byte)0x99, (byte)0x9f, (byte)0xbb, (byte)0xb9, (byte)0x33, (byte)0x3e,
        (byte)0x3c, (byte)0x42, (byte)0xb9, (byte)0xa5, (byte)0xb9, (byte)0xa5, (byte)0x42, (byte)0x3c, 
        (byte)0x21, (byte)0x04, (byte)0x01, (byte)0x11, (byte)0xa8, (byte)0x00, (byte)0x1a, (byte)0x13, 
        (byte)0xbe, (byte)0x20, (byte)0xfe, (byte)0x23, (byte)0x7d, (byte)0xfe, (byte)0x34, (byte)0x20,
        (byte)0xf5, (byte)0x06, (byte)0x19, (byte)0x78, (byte)0x86, (byte)0x23, (byte)0x05, (byte)0x20, 
        (byte)0xfb, (byte)0x86, (byte)0x20, (byte)0xfe, (byte)0x3e, (byte)0x01, (byte)0xe0, (byte)0x50,
    };

    // get or create per-CPU memory
    private static CpuMemory getCpuMemory(Cpu cpu) {
        return cpuCache.computeIfAbsent(cpu, c -> new CpuMemory());
    }

    // read a single byte
    public static byte read8(Cpu cpu, int addr) throws IOException {
        CpuMemory mem = getCpuMemory(cpu);

        if ((addr & 0xFFFF) >= 0xFF00) {
            //System.out.printf("reading from IO register %d (0x%04X), got %d (0x%02X), PC = %d (0x%04X)\n", addr & 0xFFFF, addr & 0xFFFF, readIORegister(cpu, addr, mem) & 0xFF, readIORegister(cpu, addr, mem) & 0xFF, cpu.getPC() & 0xFFFF, cpu.getPC() & 0xFFFF);
            return readIORegister(cpu, addr, mem);
        }

        int pageIndex = (addr & 0xFFFF) / PAGE_SIZE;
        int offset = (addr & 0xFFFF) % PAGE_SIZE;

        // BIOS check: read FF50 directly from pages
        int pageIndexFF50 = 0xFF50 / PAGE_SIZE;
        int offsetFF50 = 0xFF50 % PAGE_SIZE;
        byte ff50Value = (mem.pages[pageIndexFF50] != null) ? mem.pages[pageIndexFF50][offsetFF50] : 0;

        if (ff50Value == 0 && (addr & 0xFFFF) < 0x100) {
            return BIOS[addr & 0xFF];
        }

        // load the page if not present
        if (mem.pages[pageIndex] == null) {
            mem.loadPage(cpu, pageIndex);
        }

        return mem.pages[pageIndex][offset];
    }

    // write a single byte
    public static void write8(Cpu cpu, int addr, byte value) throws IOException {
        CpuMemory mem = getCpuMemory(cpu);

        if ((addr & 0xFFFF) >= 0xFF00) {
            //System.out.printf("writing to IO register %d (0x%04X), got %d (0x%02X), PC = %d (0x%04X)\n", addr & 0xFFFF, addr & 0xFFFF, readIORegister(cpu, addr, mem) & 0xFF, readIORegister(cpu, addr, mem) & 0xFF, cpu.getPC() & 0xFFFF, cpu.getPC() & 0xFFFF);
        }

        if ((addr & 0xFFFF) == 0xFF50) {
            System.out.printf("Writing %d to BIOS map register\n", value & 0xFF);
        }

        if ((addr & 0xFFFF) >= 0x0000 & (addr & 0xFFFF) < 0x7FFF) {
            System.out.printf("Illegal write at address 0x%04X\n", addr & 0xFFFF);
            return;
        }

        int pageIndex = (addr & 0xFFFF) / PAGE_SIZE;
        int offset = (addr & 0xFFFF) % PAGE_SIZE;

        if (mem.pages[pageIndex] == null) {
            mem.loadPage(cpu, pageIndex);
        }

        mem.pages[pageIndex][offset] = value;
        if (!mem.dirty[pageIndex]) {
            mem.dirty[pageIndex] = true;
            mem.commitPage(cpu, pageIndex);
        }
    }

    // dynamically calculate IO registers based on cpu cycles
    private static byte readIORegister(Cpu cpu, int addr, CpuMemory mem) throws IOException {
        int cycles = cpu.getCycles();
        int cyclesInFrame = cycles % 70224; // number of cycles in a frame
        int ly = cyclesInFrame / 456;       // current line (0-153)

        int pageIndex = (addr & 0xFFFF) / PAGE_SIZE;
        int offset = (addr & 0xFFFF) % PAGE_SIZE;

        switch (addr & 0xFFFF) {
            case 0xFF0F:
            case 0xFFFF:
                if (mem.pages[pageIndex] == null) mem.loadPage(cpu, pageIndex);
                return mem.pages[pageIndex][offset];
            // DIV
            case 0xFF04:
                int div = (cycles / 256) & 0xFF;
                return (byte)div;
            // LCD
            case 0xFF41: // STAT
                int cycleInLine = cyclesInFrame % 456;
                int mode;
                if (ly >= 144) {
                    mode = 1; // V-Blank
                } else if (cycleInLine < 80) {
                    mode = 2; // OAM
                } else if (cycleInLine < 80 + 172) {
                    mode = 3; // Pixel Transfer
                } else {
                    mode = 0; // H-Blank
                }

                // read stored STAT bits
                byte statStored = (mem.pages[pageIndex] != null) ? mem.pages[pageIndex][offset] : 0;
                // top 5 bits are static, bottom 3 bits are dynamic (mode + coincidence)
                statStored &= 0xF8; // clear bits 0-2
                statStored |= mode;  // set mode bits 0-1

                // LYC == LY coincidence flag (bit 2)
                int lyc = (mem.pages[(0xFF45) / PAGE_SIZE] != null) ? mem.pages[(0xFF45) / PAGE_SIZE][0xFF45 % PAGE_SIZE] & 0xFF : 0;
                if (ly == lyc) {
                    statStored |= (1 << 2);
                }
                return statStored;

            case 0xFF44: // LY
                return (byte) (ly & 0xFF);

            case 0xFF45: // LYC
            case 0xFF40: // LCDC
            case 0xFF42: // SCY
            case 0xFF43: // SCX
            case 0xFF47: // BGP
            case 0xFF48: // OBP0
            case 0xFF49: // OBP1
            case 0xFF4A: // WY
            case 0xFF4B: // WX
                // Return stored values for all other LCD registers
                if (mem.pages[pageIndex] == null) mem.loadPage(cpu, pageIndex);
                return mem.pages[pageIndex][offset];

            default:
                // For any other address > 0xFF00 just read from memory
                if (mem.pages[pageIndex] == null) mem.loadPage(cpu, pageIndex);
                return mem.pages[pageIndex][offset];
        }
    }

    // commit all pages synchronously
    public static void commitAll(Cpu cpu) throws IOException {
        CpuMemory mem = getCpuMemory(cpu);
        for (int i = 0; i < mem.pages.length; i++) {
            if (mem.dirty[i] && mem.pages[i] != null) {
                mem.commitPage(cpu, i);
            }
        }
    }

    // per-CPU memory container
    private static class CpuMemory {
        private final byte[][] pages = new byte[0x10000 / PAGE_SIZE][];
        private final boolean[] dirty = new boolean[0x10000 / PAGE_SIZE];

        void loadPage(Cpu cpu, int pageIndex) throws IOException {
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

        void commitPage(Cpu cpu, int pageIndex) throws IOException {
            if (!dirty[pageIndex] || pages[pageIndex] == null) return;

            byte[] buffer = Arrays.copyOf(pages[pageIndex], PAGE_SIZE);
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
    }
}
