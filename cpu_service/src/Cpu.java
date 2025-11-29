public class Cpu {
    private short AF; // A, Flags
    private short BC; // B, C
    private short DE; // D, E
    private short HL; // H, L
    private short SP;
    private short PC;
    
    private static final int FLAG_Z = 0x80;
    private static final int FLAG_N = 0x40;
    private static final int FLAG_H = 0x20;
    private static final int FLAG_C = 0x10;
    
    private int id;

    private Instruction[] table = new Instruction[256];
    private Instruction[] cbTable = new Instruction[256];
    
    private int cycles = 0;

    public Cpu(int id) {
        this.id = id;

        this.AF = 0;
        this.BC = 0;
        this.DE = 0;
        this.HL = 0;
        this.SP = 0;
        this.PC = 0;
        
        InstructionDecoder.buildInstructionTable(this);
        InstructionDecoder.buildCbInstructionTable(this);
    }

    public void step() throws Exception {
        // Print BEFORE state
        System.out.println("=== BEFORE ===");
        printState();

        int opcode = read8(PC) & 0xFF;
        System.out.println(String.format("PC: 0x%04X  Opcode: 0x%02X", PC, opcode));

        PC++;

        Instruction ins;

        if (opcode == 0xCB) {
            int cb = read8(PC) & 0xFF;
            PC++;
            ins = getCBIns(cb);
            if (ins == null) {
                throw new RuntimeException(String.format("Unimplemented CB opcode: CB 0x%02X", cb));
            }
        } else {
            ins = getIns(opcode);
            if (ins == null) {
                throw new RuntimeException(String.format("Unimplemented opcode: 0x%02X", opcode));
            }
        }

        // Execute instruction
        ins.execute(this);
        cycles += ins.cycles();

        // Print AFTER state
        System.out.println("=== AFTER ===");
        printState();
        System.out.println("====================\n");
    }

    // Helper method to print CPU registers & flags
    public void printState() {
        System.out.printf(
            "A: 0x%02X  F: 0x%02X  B: 0x%02X  C: 0x%02X  D: 0x%02X  E: 0x%02X  H: 0x%02X  L: 0x%02X\n",
            getA() & 0xFF, getF() & 0xFF,
            getB() & 0xFF, getC() & 0xFF,
            getD() & 0xFF, getE() & 0xFF,
            getH() & 0xFF, getL() & 0xFF
        );
        System.out.printf("SP: 0x%04X  PC: 0x%04X  Cycles: %d\n", getSP() & 0xFFFF, getPC() & 0xFFFF, cycles);
    }

    
    public int getId() {
        return this.id;
    }

    private static byte hi(short reg) {
        return (byte)((reg >>> 8) & 0xFF);
    }

    private static byte lo(short reg) {
        return (byte)(reg & 0xFF);
    }

    private static short combine(byte hi, byte lo) {
        return (short)(((hi & 0xFF) << 8) | (lo & 0xFF));
    }

    // AF pair
    public byte getA() {
        return hi(AF);
    }

    public void setA(byte v) {
        byte f = getF();
        AF = combine(v, f);
    }

    // BC pair
    public byte getB() {
        return hi(BC);
    }

    public void setB(byte v) {
        BC = combine(v, getC());
    }

    public byte getC() {
        return lo(BC);
    }

    public void setC(byte v) {
        BC = combine(getB(), v);
    }

    // DE pair
    public byte getD() {
        return hi(DE);
    }

    public void setD(byte v) {
        DE = combine(v, getE());
    }

    public byte getE() {
        return lo(DE);
    }

    public void setE(byte v) {
        DE = combine(getD(), v);
    }

    // HL pair
    public byte getH() {
        return hi(HL);
    }

    public void setH(byte v) {
        HL = combine(v, getL());
    }

    public byte getL() {
        return lo(HL);
    }

    public void setL(byte v) {
        HL = combine(getH(), v);
    }

    // SP, PC (full 16-bit)
    public short getSP() {
        return SP;
    }

    public void setSP(short sp) {
        this.SP = sp;
    }

    public short getPC() {
        return PC;
    }

    public void setPC(short pc) {
        this.PC = pc;
    }

    // flags
    public byte getF() {
        return (byte)(AF & 0xF0);
    }

    private void setF(byte v) {
        v &= 0xF0; // bottom 4 bits always zero on Game Boy
        AF = (short)(((getA() & 0xFF) << 8) | (v & 0xF0));
    }

    private void setFlag(int mask) {
        setF((byte)(getF() | mask));
    }

    private void clearFlag(int mask) {
        setF((byte)(getF() & ~mask));
    }

    private boolean isFlagSet(int mask) {
        return (getF() & mask) != 0;
    }

    // Z flag
    public boolean isZ() {
        return isFlagSet(FLAG_Z);
    }

    public void setZ() {
        setFlag(FLAG_Z);
    }

    public void clearZ() {
        clearFlag(FLAG_Z);
    }

    public void updateZ(boolean set) {
        if (set) {
            setZ(); 
        } else { 
            clearZ();
        }
    }

    // N flag
    public boolean isN() {
        return isFlagSet(FLAG_N);
    }

    public void setN() {
        setFlag(FLAG_N);
    }

    public void clearN() {
        clearFlag(FLAG_N);
    }

    public void updateN(boolean set) {
        if (set) {
            setN(); 
        } else {
            clearN();
        }
    }

    // H flag
    public boolean isH() {
        return isFlagSet(FLAG_H);
    }

    public void setH() {
        setFlag(FLAG_H);
    }

    public void clearH() {
        clearFlag(FLAG_H);
    }

    public void updateH(boolean set) {
        if (set) {
            setH(); 
        } else {
            clearH();
        }
    }

    // C flag
    public boolean isC() {
        return isFlagSet(FLAG_C);
    }

    public void setCFlag() {
        setFlag(FLAG_C);
    }

    public void clearCFlag() {
        clearFlag(FLAG_C);
    }

    public void updateC(boolean set) {
        if (set) {
            setCFlag(); 
        } else {
            clearCFlag();
        }
    }
    
    // dispatch table
    public void setIns(int opcode, Instruction ins) {
        table[opcode & 0xFF] = ins;
    }

    public void setCBIns(int opcode, Instruction ins) {
        cbTable[opcode & 0xFF] = ins;
    }

    public Instruction getIns(int opcode) {
        return table[opcode & 0xFF];
    }

    public Instruction getCBIns(int opcode) {
        return cbTable[opcode & 0xFF];
    }
    
    // IO
    public byte read8(int addr) {
        try {
            return Io.read8(this, addr);
        } catch (Exception e) {
            System.out.println(e);
            return 0;
        }
    }

    public void write8(int addr) {
        try {
            Io.read8(this, addr);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}