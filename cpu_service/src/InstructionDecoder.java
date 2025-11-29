public class InstructionDecoder {

    public static void buildInstructionTable(Cpu cpu) {
        // 0x00 - NOP
        cpu.setIns(0x00, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                // NOP
            }

            @Override
            public String name() {
                return "NOP";
            }

            @Override
            public int cycles() {
                return 4;
            }
        });

        // 0x04 - INC B
        cpu.setIns(0x04, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte old = cpu.getB();
                byte result = (byte)((old + 1) & 0xFF);

                cpu.setB(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH((old & 0x0F) == 0x0F);
            }
            
            @Override
            public String name() {
                return "INC B";
            }

            @Override
            public int cycles() {
                return 4;
            }
        });

        // 0x06 - LD B, d8
        cpu.setIns(0x06, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte value = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));
                cpu.setB(value);
            }

            @Override
            public String name() {
                return "LD B, d8";
            }

            @Override
            public int cycles() {
                return 8;
            }
        });

        // 0x1F - RRA
        cpu.setIns(0x1F, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte a = cpu.getA();
                boolean oldCarry = cpu.isC();   // current carry flag
                boolean newCarry = (a & 0x01) != 0; // bit 0 of A

                // rotate right through carry
                byte result = (byte)(((oldCarry ? 0x80 : 0x00) | ((a & 0xFF) >> 1)) & 0xFF);
                cpu.setA(result);

                // update flags
                cpu.updateZ(false);      // Z flag always 0
                cpu.updateN(false);      // N flag reset
                cpu.updateH(false);      // H flag reset
                cpu.updateC(newCarry);   // C flag = old bit 0
            }

            @Override
            public String name() {
                return "RRA";
            }

            @Override
            public int cycles() {
                return 4;
            }
        });
        
        // 0x31 - LD SP, n16
        cpu.setIns(0x31, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                // read 16-bit immediate (LE)
                byte low = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));

                byte high = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));

                short value = (short)(((high & 0xFF) << 8) | (low & 0xFF));
                
                System.out.println("hi: " + high + ", lo: " + low + ", value: " + value);
                cpu.setSP(value);
            }

            @Override
            public String name() {
                return "LD SP, d16";
            }

            @Override
            public int cycles() {
                return 12;
            }
        });

        // TODO: MORE!!!!!!!
    }

    public static void buildCbInstructionTable(Cpu cpu) {
        // 0x11 - RL C (rotate left through carry)
        cpu.setCBIns(0x11, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte c = cpu.getC();
                int carry = cpu.isC() ? 1 : 0;
                int newC = ((c << 1) & 0xFF) | carry;

                cpu.setC((byte)newC);

                cpu.updateZ((byte)newC == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC((c & 0x80) != 0);
            }

            @Override
            public String name() {
                return "RL C";
            }

            @Override
            public int cycles() {
                return 8;
            }
        });

        // 0x7C - BIT 7, H (test bit 7 of H)
        cpu.setCBIns(0x7C, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte h = cpu.getH();
                cpu.updateZ((h & 0x80) == 0);
                cpu.updateN(false);
                cpu.updateH(true);
            }

            @Override
            public String name() {
                return "BIT 7,H";
            }

            @Override
            public int cycles() {
                return 8;
            }
        });

        // TODO: MORE!!!!!!!!!!!!!!!!!!!!!!1
    }
}