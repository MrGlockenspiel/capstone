public class InstructionDecoder {
    public static byte BYTE(int n) {
        return (byte) (n & 0xFF);
    }

    public static short SHORT(int n) {
        return (short) (n & 0xFFFF);
    }

    public static void buildInstructionTable(Cpu cpu) {
        // 0x00 - NOP
        cpu.setIns(0x00, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                // NOP
                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "NOP";
            }
        });
        
        // 0x01 - LD BC, d16
        cpu.setIns(0x01, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte low = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));
                byte high = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));

                short value = (short)(((high & 0xFF) << 8) | (low & 0xFF));
                cpu.setBC(value);
                
                
                cpu.incCycles(12);
            }

            @Override
            public String name() { 
                return "LD BC, d16"; 
            }
        });

        // 0x02 - LD (BC), A
        cpu.setIns(0x01, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte low = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));
                byte high = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));

                short value = (short)(((high & 0xFF) << 8) | (low & 0xFF));
                cpu.setBC(value);

                cpu.incCycles(12);
            }

            @Override
            public String name() { 
                return "LD BC, d16"; 
            }
        });

        // 0x03 - INC BC
        cpu.setIns(0x03, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setBC((short)(cpu.getBC() + 1));

                cpu.incCycles(8);
            }

            @Override
            public String name() { 
                return "INC BC";
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
                
                cpu.incCycles(4);
            }
            
            @Override
            public String name() {
                return "INC B";
            }
        });

        // 0x05 - DEC B
        cpu.setIns(0x05, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte old = cpu.getB();
                byte result = (byte)((old - 1) & 0xFF);

                cpu.setB(result);

                cpu.updateZ(result == 0);
                cpu.updateN(true);
                cpu.updateH((old & 0x0F) == 0x00);
                
                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "DEC B";
            }
        });

        // 0x06 - LD B, d8
        cpu.setIns(0x06, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte value = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));
                cpu.setB(value);
                
                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD B, d8";
            }
        });

        // 0x07 - RLCA
        cpu.setIns(0x07, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte a = cpu.getA();
                boolean newCarry = (a & 0x80) != 0;

                byte result = (byte)(((a << 1) & 0xFF) | (newCarry ? 1 : 0));
                cpu.setA(result);

                cpu.updateZ(false);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);
                
                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "RLCA";
            }
        });

        // 0x08 - LD (a16), SP
        cpu.setIns(0x08, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte low = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));
                byte high = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));

                short addr = (short)(((high & 0xFF) << 8) | (low & 0xFF));

                cpu.write16(addr, cpu.getSP());
                
                cpu.incCycles(20);
            }

            @Override
            public String name() {
                return "LD (a16), SP";
            }
        });

        // 0x09 - ADD HL, BC
        cpu.setIns(0x09, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int hl = cpu.getHL() & 0xFFFF;
                int bc = cpu.getBC() & 0xFFFF;

                int result = hl + bc;

                cpu.updateN(false);
                cpu.updateH(((hl & 0x0FFF) + (bc & 0x0FFF)) > 0x0FFF);
                cpu.updateC(result > 0xFFFF);

                cpu.setHL((short)(result & 0xFFFF));
                
                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "ADD HL, BC";
            }
        });

        // 0x0A - LD A, (BC)
        cpu.setIns(0x0A, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.setA(cpu.read8(cpu.getBC()));
                
                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD A, (BC)";
            }
        });

        // 0x0B - DEC BC
        cpu.setIns(0x0B, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setBC((short)(cpu.getBC() - 1));
                
                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "DEC BC";
            }
        });

        // 0x0C - INC C
        cpu.setIns(0x0C, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte old = cpu.getC();
                byte result = (byte)((old + 1) & 0xFF);

                cpu.setC(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH((old & 0x0F) == 0x0F);
                
                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "INC C";
            }
        });

        // 0x0D - DEC C
        cpu.setIns(0x0D, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte old = cpu.getC();
                byte result = (byte)((old - 1) & 0xFF);

                cpu.setC(result);

                cpu.updateZ(result == 0);
                cpu.updateN(true);
                cpu.updateH((old & 0x0F) == 0x00);
                
                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "DEC C";
            }
        });

        // 0x0E - LD C, d8
        cpu.setIns(0x0E, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte value = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));
                cpu.setC(value);
                
                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD C, d8";
            }
        });
        
        // 0x0F - RRCA
        cpu.setIns(0x0F, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte a = cpu.getA();
                boolean newCarry = (a & 0x01) != 0;

                byte result = (byte)(((a >> 1) & 0xFF) | (newCarry ? 0x80 : 0));
                cpu.setA(result);

                cpu.updateZ(false);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);
                
                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "RRCA";
            }
        });

        // 0x10 - STOP d8
        cpu.setIns(0x10, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                // GB enters low power state uhhhhhh
                byte value = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));
                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "STOP d8";
            }
        });
        
        // 0x11 - LD DE, d16
        cpu.setIns(0x11, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte low = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));

                byte high = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));

                short value = (short)(((high & 0xFF) << 8) | (low & 0xFF));
                cpu.setDE(value);

                cpu.incCycles(12);
            }

            @Override
            public String name() {
                return "LD DE, d16";
            }
        });

        // 0x12 - LD (DE), A
        cpu.setIns(0x12, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.write8(cpu.getDE(), cpu.getA());

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD (DE), A";
            }
        });

        // 0x13 - INC DE
        cpu.setIns(0x13, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setDE((short)(cpu.getDE() + 1));

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "INC DE";
            }
        });

        // 0x14 - INC D
        cpu.setIns(0x14, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte old = cpu.getD();
                byte result = (byte)((old + 1) & 0xFF);

                cpu.setD(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH((old & 0x0F) == 0x0F);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "INC D";
            }
        });

        // 0x15 - DEC D
        cpu.setIns(0x15, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte old = cpu.getD();
                byte result = (byte)((old - 1) & 0xFF);

                cpu.setD(result);

                cpu.updateZ(result == 0);
                cpu.updateN(true);
                cpu.updateH((old & 0x0F) == 0x00);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "DEC D";
            }
        });

        // 0x16 - LD D, d8
        cpu.setIns(0x16, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte value = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));

                cpu.setD(value);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD D, d8";
            }
        });

        // 0x17 - RLA
        cpu.setIns(0x17, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte a = cpu.getA();
                boolean oldCarry = cpu.isC();
                boolean newCarry = (a & 0x80) != 0;

                byte result = (byte)(((a << 1) & 0xFF) | (oldCarry ? 1 : 0));
                cpu.setA(result);

                cpu.updateZ(false);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "RLA";
            }
        });

        // 0x18 - JR r8
        cpu.setIns(0x18, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte offset = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));

                cpu.setPC((short)(cpu.getPC() + (byte)offset));

                cpu.incCycles(12);
            }

            @Override
            public String name() {
                return "JR r8";
            }
        });

        // 0x19 - ADD HL, DE
        cpu.setIns(0x19, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int hl = cpu.getHL() & 0xFFFF;
                int de = cpu.getDE() & 0xFFFF;

                int result = hl + de;

                cpu.updateN(false);
                cpu.updateH(((hl & 0x0FFF) + (de & 0x0FFF)) > 0x0FFF);
                cpu.updateC(result > 0xFFFF);

                cpu.setHL((short)(result & 0xFFFF));

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "ADD HL, DE";
            }
        });

        // 0x1A - LD A, (DE)
        cpu.setIns(0x1A, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.setA(cpu.read8(cpu.getDE()));

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD A, (DE)";
            }
        });
        
        // 0x1B - DEC DE
        cpu.setIns(0x1B, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setDE((short)(cpu.getDE() - 1));

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "DEC DE";
            }
        });

        // 0x1C - INC E
        cpu.setIns(0x1C, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte old = cpu.getE();
                byte result = (byte)((old + 1) & 0xFF);

                cpu.setE(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH((old & 0x0F) == 0x0F);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "INC E";
            }
        });

        // 0x1D - DEC E
        cpu.setIns(0x1D, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte old = cpu.getE();
                byte result = (byte)((old - 1) & 0xFF);

                cpu.setE(result);

                cpu.updateZ(result == 0);
                cpu.updateN(true);
                cpu.updateH((old & 0x0F) == 0x00);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "DEC E";
            }
        });

        // 0x1E - LD E, d8
        cpu.setIns(0x1E, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte value = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));

                cpu.setE(value);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD E, d8";
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

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "RRA";
            }
        });

        // 0x20 - JR NZ, r8
        cpu.setIns(0x20, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte offset = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));

                if (!cpu.isZ()) {
                    //System.out.printf("Z == 1, branching to %d (old PC = %d)\n", SHORT((int)cpu.getPC() + (int)offset), cpu.getPC() & 0xFFFF);
                    cpu.setPC(SHORT((int)cpu.getPC() + (int)offset));
                    cpu.incCycles(12);
                } else {
                    cpu.incCycles(8);
                }
            }

            @Override
            public String name() {
                return "JR NZ, r8";
            }
        });
        
        // 0x21 - LD HL, d16
        cpu.setIns(0x21, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte low = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));

                byte high = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));

                short value = (short)(((high & 0xFF) << 8) | (low & 0xFF));
                cpu.setHL(value);

                cpu.incCycles(12);
            }

            @Override
            public String name() {
                return "LD HL, d16";
            }
        });
        
        // 0x22 - LD (HL+), A
        cpu.setIns(0x22, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.write8(cpu.getHL(), cpu.getA());
                cpu.setHL((short)(cpu.getHL() + 1));

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD (HL+), A";
            }
        });

        // 0x23 - INC HL
        cpu.setIns(0x23, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setHL((short)(cpu.getHL() + 1));

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "INC HL";
            }
        });

        // 0x24 - INC H
        cpu.setIns(0x24, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte old = cpu.getH();
                byte result = (byte)((old + 1) & 0xFF);

                cpu.setH(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH((old & 0x0F) == 0x0F);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "INC H";
            }
        });

        // 0x25 - DEC H
        cpu.setIns(0x25, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte old = cpu.getH();
                byte result = (byte)((old - 1) & 0xFF);

                cpu.setH(result);

                cpu.updateZ(result == 0);
                cpu.updateN(true);
                cpu.updateH((old & 0x0F) == 0x00);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "DEC H";
            }
        });

        // 0x26 - LD H, d8
        cpu.setIns(0x26, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte value = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));

                cpu.setH(value);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD H, d8";
            }
        });

        // 0x27 - DAA
        cpu.setIns(0x27, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int correction = 0;
                boolean carry = cpu.isC();

                if (!cpu.isN()) {
                    if (cpu.isH() || (a & 0x0F) > 9) {
                        correction |= 0x06;
                    }
                    if (carry || a > 0x99) {
                        correction |= 0x60;
                        carry = true;
                    }
                    a += correction;
                } else {
                    if (cpu.isH()) {
                        correction |= 0x06;
                    }
                    if (carry) {
                        correction |= 0x60;
                    }
                    a -= correction;
                }

                a &= 0xFF;
                cpu.setA((byte)a);

                cpu.updateZ(a == 0);
                cpu.updateH(false);
                cpu.updateC(carry);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "DAA";
            }
        });

        // 0x28 - JR Z, r8
        cpu.setIns(0x28, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte offset = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));

                if (cpu.isZ()) {
                    cpu.setPC((short)(cpu.getPC() + (byte)offset));
                    cpu.incCycles(12);
                } else {
                    cpu.incCycles(8);
                }
            }

            @Override
            public String name() {
                return "JR Z, r8";
            }
        });

        // 0x29 - ADD HL, HL
        cpu.setIns(0x29, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int hl = cpu.getHL() & 0xFFFF;

                int result = hl + hl;

                cpu.updateN(false);
                cpu.updateH(((hl & 0x0FFF) + (hl & 0x0FFF)) > 0x0FFF);
                cpu.updateC(result > 0xFFFF);

                cpu.setHL((short)(result & 0xFFFF));

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "ADD HL, HL";
            }
        });


        // 0x2A - LD A, (HL+)
        cpu.setIns(0x2A, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.setA(cpu.read8(cpu.getHL()));
                cpu.setHL((short)(cpu.getHL() + 1));

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD A, (HL+)";
            }
        });

        // 0x2B - DEC HL
        cpu.setIns(0x2B, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setHL((short)(cpu.getHL() - 1));

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "DEC HL";
            }
        });

        // 0x2C - INC L
        cpu.setIns(0x2C, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte old = cpu.getL();
                byte result = (byte)((old + 1) & 0xFF);

                cpu.setL(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH((old & 0x0F) == 0x0F);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "INC L";
            }
        });

        // 0x2D - DEC L
        cpu.setIns(0x2D, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte old = cpu.getL();
                byte result = (byte)((old - 1) & 0xFF);

                cpu.setL(result);

                cpu.updateZ(result == 0);
                cpu.updateN(true);
                cpu.updateH((old & 0x0F) == 0x00);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "DEC L";
            }
        });

        // 0x2E - LD L, d8
        cpu.setIns(0x2E, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte value = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));

                cpu.setL(value);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD L, d8";
            }
        });

        // 0x2F - CPL
        cpu.setIns(0x2F, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setA((byte)(~cpu.getA()));

                cpu.updateN(true);
                cpu.updateH(true);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "CPL";
            }
        });

        // 0x30 - JR NC, r8
        cpu.setIns(0x30, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte offset = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));

                if (!cpu.isC()) {
                    cpu.setPC((short)(cpu.getPC() + (byte)offset));
                    cpu.incCycles(12);
                } else {
                    cpu.incCycles(8);
                }
            }

            @Override
            public String name() {
                return "JR NC, r8";
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
                
                cpu.setSP(value);
                
                cpu.incCycles(12);
            }

            @Override
            public String name() {
                return "LD SP, d16";
            }
        });
        
        // 0x32 - LD (HL-), A
        cpu.setIns(0x32, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                
                cpu.write8(cpu.getHL(), cpu.getA());
                cpu.setHL((short)(cpu.getHL() - 1));

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD (HL-), A";
            }
        });

        // 0x33 - INC SP
        cpu.setIns(0x33, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setSP((short)(cpu.getSP() + 1));

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "INC SP";
            }
        });

        // 0x34 - INC (HL)
        cpu.setIns(0x34, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte old = cpu.read8(cpu.getHL());
                byte result = (byte)((old + 1) & 0xFF);

                cpu.write8(cpu.getHL(), result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH((old & 0x0F) == 0x0F);

                cpu.incCycles(12);
            }

            @Override
            public String name() {
                return "INC (HL)";
            }
        });

        // 0x35 - DEC (HL)
        cpu.setIns(0x35, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte old = cpu.read8(cpu.getHL());
                byte result = (byte)((old - 1) & 0xFF);

                cpu.write8(cpu.getHL(), result);

                cpu.updateZ(result == 0);
                cpu.updateN(true);
                cpu.updateH((old & 0x0F) == 0x00);

                cpu.incCycles(12);
            }

            @Override
            public String name() {
                return "DEC (HL)";
            }
        });

        // 0x36 - LD (HL), d8
        cpu.setIns(0x36, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte value = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));

                cpu.write8(cpu.getHL(), value);

                cpu.incCycles(12);
            }

            @Override
            public String name() {
                return "LD (HL), d8";
            }
        });

        // 0x37 - SCF
        cpu.setIns(0x37, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(true);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "SCF";
            }
        });

        // 0x38 - JR C, r8
        cpu.setIns(0x38, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte offset = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));

                if (cpu.isC()) {
                    cpu.setPC((short)(cpu.getPC() + (byte)offset));
                    cpu.incCycles(12);
                } else {
                    cpu.incCycles(8);
                }
            }

            @Override
            public String name() {
                return "JR C, r8";
            }
        });

        // 0x39 - ADD HL, SP
        cpu.setIns(0x39, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int hl = cpu.getHL() & 0xFFFF;
                int sp = cpu.getSP() & 0xFFFF;

                int result = hl + sp;

                cpu.updateN(false);
                cpu.updateH(((hl & 0x0FFF) + (sp & 0x0FFF)) > 0x0FFF);
                cpu.updateC(result > 0xFFFF);

                cpu.setHL((short)(result & 0xFFFF));

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "ADD HL, SP";
            }
        });

        // 0x3A - LD A, (HL-)
        cpu.setIns(0x3A, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.setA(cpu.read8(cpu.getHL()));
                cpu.setHL((short)(cpu.getHL() - 1));

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD A, (HL-)";
            }
        });

        // 0x3B - DEC SP
        cpu.setIns(0x3B, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setSP((short)(cpu.getSP() - 1));

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "DEC SP";
            }
        });

        // 0x3C - INC A
        cpu.setIns(0x3C, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte old = cpu.getA();
                byte result = (byte)((old + 1) & 0xFF);

                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH((old & 0x0F) == 0x0F);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "INC A";
            }
        });

        // 0x3D - DEC A
        cpu.setIns(0x3D, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte old = cpu.getA();
                byte result = (byte)((old - 1) & 0xFF);

                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(true);
                cpu.updateH((old & 0x0F) == 0x00);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "DEC A";
            }
        });

        // 0x3E - LD A, d8
        cpu.setIns(0x3E, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte value = cpu.read8(cpu.getPC());
                cpu.setPC((short)(cpu.getPC() + 1));

                cpu.setA(value);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD A, d8";
            }
        });

        // 0x3F - CCF
        cpu.setIns(0x3F, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(!cpu.isC());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "CCF";
            }
        });

        // 0x40 - LD B, B
        cpu.setIns(0x40, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setB(cpu.getB());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD B, B";
            }
        });
        
        // 0x41 - LD B, C
        cpu.setIns(0x41, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setB(cpu.getC());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD B, C";
            }
        });

        // 0x42 - LD B, D
        cpu.setIns(0x42, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setB(cpu.getD());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD B, D";
            }
        });

        // 0x43 - LD B, E
        cpu.setIns(0x43, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setB(cpu.getE());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD B, E";
            }
        });

        // 0x44 - LD B, H
        cpu.setIns(0x44, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setB(cpu.getH());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD B, H";
            }
        });

        // 0x45 - LD B, L
        cpu.setIns(0x45, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setB(cpu.getL());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD B, L";
            }
        });

        // 0x46 - LD B, (HL)
        cpu.setIns(0x46, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.setB(cpu.read8(cpu.getHL()));

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD B, (HL)";
            }
        });

        // 0x47 - LD B, A
        cpu.setIns(0x47, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setB(cpu.getA());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD B, A";
            }
        });

        // 0x48 - LD C, B
        cpu.setIns(0x48, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setC(cpu.getB());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD C, B";
            }
        });

        // 0x49 - LD C, C
        cpu.setIns(0x49, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setC(cpu.getC());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD C, C";
            }
        });

        // 0x4A - LD C, D
        cpu.setIns(0x4A, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setC(cpu.getD());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD C, D";
            }
        });

        // 0x4B - LD C, E
        cpu.setIns(0x4B, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setC(cpu.getE());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD C, E";
            }
        });

        // 0x4C - LD C, H
        cpu.setIns(0x4C, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setC(cpu.getH());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD C, H";
            }
        });

        // 0x4D - LD C, L
        cpu.setIns(0x4D, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setC(cpu.getL());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD C, L";
            }
        });

        // 0x4E - LD C, (HL)
        cpu.setIns(0x4E, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.setC(cpu.read8(cpu.getHL()));

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD C, (HL)";
            }
        });
        
        // 0x4F - LD C, A
        cpu.setIns(0x4F, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setC(cpu.getA());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD C, A";
            }
        });

        // 0x50 - LD D, B
        cpu.setIns(0x50, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setD(cpu.getB());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD D, B";
            }
        });

        // 0x51 - LD D, C
        cpu.setIns(0x51, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setD(cpu.getC());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD D, C";
            }
        });


        // 0x52 - LD D, D
        cpu.setIns(0x52, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setD(cpu.getD());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD D, D";
            }
        });

        // 0x53 - LD D, E
        cpu.setIns(0x53, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setD(cpu.getE());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD D, E";
            }
        });

        // 0x54 - LD D, H
        cpu.setIns(0x54, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setD(cpu.getH());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD D, H";
            }
        });

        // 0x55 - LD D, L
        cpu.setIns(0x55, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setD(cpu.getL());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD D, L";
            }
        });

        // 0x56 - LD D, (HL)
        cpu.setIns(0x56, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.setD(cpu.read8(cpu.getHL()));

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD D, (HL)";
            }
        });

        // 0x57 - LD D, A
        cpu.setIns(0x57, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setD(cpu.getA());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD D, A";
            }
        });

        // 0x58 - LD E, B
        cpu.setIns(0x58, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setE(cpu.getB());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD E, B";
            }
        });

        // 0x59 - LD E, C
        cpu.setIns(0x59, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setE(cpu.getC());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD E, C";
            }
        });

        // 0x5A - LD E, D
        cpu.setIns(0x5A, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setE(cpu.getD());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD E, D";
            }
        });

        // 0x5B - LD E, E
        cpu.setIns(0x5B, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setE(cpu.getE());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD E, E";
            }
        });

        // 0x5C - LD E, H
        cpu.setIns(0x5C, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setE(cpu.getH());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD E, H";
            }
        });

        // 0x5D - LD E, L
        cpu.setIns(0x5D, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setE(cpu.getL());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD E, L";
            }
        });

        // 0x5E - LD E, (HL)
        cpu.setIns(0x5E, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.setE(cpu.read8(cpu.getHL()));

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD E, (HL)";
            }
        });

        // 0x5F - LD E, A
        cpu.setIns(0x5F, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setE(cpu.getA());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD E, A";
            }
        });

        // 0x60 - LD H, B
        cpu.setIns(0x60, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setH(cpu.getB());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD H, B";
            }
        });

        // 0x61 - LD H, C
        cpu.setIns(0x61, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setH(cpu.getC());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD H, C";
            }
        });

        // 0x62 - LD H, D
        cpu.setIns(0x62, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setH(cpu.getD());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD H, D";
            }
        });

        // 0x63 - LD H, E
        cpu.setIns(0x63, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setH(cpu.getE());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD H, E";
            }
        });

        // 0x64 - LD H, H
        cpu.setIns(0x64, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setH(cpu.getH());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD H, H";
            }
        });

        // 0x65 - LD H, L
        cpu.setIns(0x65, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setH(cpu.getL());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD H, L";
            }
        });

        // 0x66 - LD H, (HL)
        cpu.setIns(0x66, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.setH(cpu.read8(cpu.getHL()));

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD H, (HL)";
            }
        });

        // 0x67 - LD H, A
        cpu.setIns(0x67, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setH(cpu.getA());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD H, A";
            }
        });

        // 0x68 - LD L, B
        cpu.setIns(0x68, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setL(cpu.getB());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD L, B";
            }
        });

        // 0x69 - LD L, C
        cpu.setIns(0x69, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setL(cpu.getC());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD L, C";
            }
        });

        // 0x6A - LD L, D
        cpu.setIns(0x6A, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setL(cpu.getD());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD L, D";
            }
        });

        // 0x6B - LD L, E
        cpu.setIns(0x6B, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setL(cpu.getE());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD L, E";
            }
        });

        // 0x6C - LD L, H
        cpu.setIns(0x6C, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setL(cpu.getH());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD L, H";
            }
        });

        // 0x6D - LD L, L
        cpu.setIns(0x6D, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setL(cpu.getL());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD L, L";
            }
        });

        // 0x6E - LD L, (HL)
        cpu.setIns(0x6E, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.setL(cpu.read8(cpu.getHL()));

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD L, (HL)";
            }
        });

        // 0x6F - LD L, A
        cpu.setIns(0x6F, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setL(cpu.getA());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD L, A";
            }
        });

        // 0x70 - LD (HL), B
        cpu.setIns(0x70, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.write8(cpu.getHL(), cpu.getB());

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD (HL), B";
            }
        });

        // 0x71 - LD (HL), C
        cpu.setIns(0x71, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.write8(cpu.getHL(), cpu.getC());

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD (HL), C";
            }
        });

        // 0x72 - LD (HL), D
        cpu.setIns(0x72, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.write8(cpu.getHL(), cpu.getD());

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD (HL), D";
            }
        });

        // 0x73 - LD (HL), E
        cpu.setIns(0x73, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.write8(cpu.getHL(), cpu.getE());

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD (HL), E";
            }
        });

        // 0x74 - LD (HL), H
        cpu.setIns(0x74, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.write8(cpu.getHL(), cpu.getH());

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD (HL), H";
            }
        });

        // 0x75 - LD (HL), L
        cpu.setIns(0x75, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.write8(cpu.getHL(), cpu.getL());

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD (HL), L";
            }
        });

        // 0x76 - HALT
        cpu.setIns(0x76, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.halt();
                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "HALT";
            }
        });

        // 0x77 - LD (HL), A
        cpu.setIns(0x77, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.write8(cpu.getHL(), cpu.getA());

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD (HL), A";
            }
        });

        // 0x78 - LD A, B
        cpu.setIns(0x78, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setA(cpu.getB());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD A, B";
            }
        });

        // 0x79 - LD A, C
        cpu.setIns(0x79, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setA(cpu.getC());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD A, C";
            }
        });

        // 0x7A - LD A, D
        cpu.setIns(0x7A, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setA(cpu.getD());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD A, D";
            }
        });

        // 0x7B - LD A, E
        cpu.setIns(0x7B, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setA(cpu.getE());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD A, E";
            }
        });

        // 0x7C - LD A, H
        cpu.setIns(0x7C, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setA(cpu.getH());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD A, H";
            }
        });

        // 0x7D - LD A, L
        cpu.setIns(0x7D, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setA(cpu.getL());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD A, L";
            }
        });

        // 0x7E - LD A, (HL)
        cpu.setIns(0x7E, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.setA(cpu.read8(cpu.getHL()));

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD A, (HL)";
            }
        });

        // 0x7F - LD A, A
        cpu.setIns(0x7F, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setA(cpu.getA());

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "LD A, A";
            }
        });

        // 0x80 - ADD A, B
        cpu.setIns(0x80, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int b = cpu.getB() & 0xFF;

                int result = a + b;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(false);
                cpu.updateH(((a & 0x0F) + (b & 0x0F)) > 0x0F);
                cpu.updateC(result > 0xFF);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "ADD A, B";
            }
        });
        
        // 0x81 - ADD A, C
        cpu.setIns(0x81, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int c = cpu.getC() & 0xFF;

                int result = a + c;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(false);
                cpu.updateH(((a & 0x0F) + (c & 0x0F)) > 0x0F);
                cpu.updateC(result > 0xFF);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "ADD A, C";
            }
        });

        // 0x82 - ADD A, D
        cpu.setIns(0x82, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int d = cpu.getD() & 0xFF;

                int result = a + d;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(false);
                cpu.updateH(((a & 0x0F) + (d & 0x0F)) > 0x0F);
                cpu.updateC(result > 0xFF);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "ADD A, D";
            }
        });

        // 0x83 - ADD A, E
        cpu.setIns(0x83, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int e = cpu.getE() & 0xFF;

                int result = a + e;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(false);
                cpu.updateH(((a & 0x0F) + (e & 0x0F)) > 0x0F);
                cpu.updateC(result > 0xFF);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "ADD A, E";
            }
        });

        // 0x84 - ADD A, H
        cpu.setIns(0x84, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int h = cpu.getH() & 0xFF;

                int result = a + h;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(false);
                cpu.updateH(((a & 0x0F) + (h & 0x0F)) > 0x0F);
                cpu.updateC(result > 0xFF);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "ADD A, H";
            }
        });

        // 0x85 - ADD A, L
        cpu.setIns(0x85, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int l = cpu.getL() & 0xFF;

                int result = a + l;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(false);
                cpu.updateH(((a & 0x0F) + (l & 0x0F)) > 0x0F);
                cpu.updateC(result > 0xFF);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "ADD A, L";
            }
        });

        // 0x86 - ADD A, (HL)
        cpu.setIns(0x86, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                int a = cpu.getA() & 0xFF;
                int hl = cpu.read8(cpu.getHL()) & 0xFF;

                int result = a + hl;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(false);
                cpu.updateH(((a & 0x0F) + (hl & 0x0F)) > 0x0F);
                cpu.updateC(result > 0xFF);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "ADD A, (HL)";
            }
        });

        // 0x87 - ADD A, A
        cpu.setIns(0x87, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;

                int result = a + a;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(false);
                cpu.updateH(((a & 0x0F) + (a & 0x0F)) > 0x0F);
                cpu.updateC(result > 0xFF);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "ADD A, A";
            }
        });

        // 0x88 - ADC A, B
        cpu.setIns(0x88, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int b = cpu.getB() & 0xFF;
                int carry = cpu.isC() ? 1 : 0;

                int result = a + b + carry;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(false);
                cpu.updateH(((a & 0x0F) + (b & 0x0F) + carry) > 0x0F);
                cpu.updateC(result > 0xFF);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "ADC A, B";
            }
        });

        // 0x89 - ADC A, C
        cpu.setIns(0x89, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int c = cpu.getC() & 0xFF;
                int carry = cpu.isC() ? 1 : 0;

                int result = a + c + carry;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(false);
                cpu.updateH(((a & 0x0F) + (c & 0x0F) + carry) > 0x0F);
                cpu.updateC(result > 0xFF);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "ADC A, C";
            }
        });

        // 0x8A - ADC A, D
        cpu.setIns(0x8A, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int d = cpu.getD() & 0xFF;
                int carry = cpu.isC() ? 1 : 0;

                int result = a + d + carry;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(false);
                cpu.updateH(((a & 0x0F) + (d & 0x0F) + carry) > 0x0F);
                cpu.updateC(result > 0xFF);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "ADC A, D";
            }
        });

        // 0x8B - ADC A, E
        cpu.setIns(0x8B, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int e = cpu.getE() & 0xFF;
                int carry = cpu.isC() ? 1 : 0;

                int result = a + e + carry;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(false);
                cpu.updateH(((a & 0x0F) + (e & 0x0F) + carry) > 0x0F);
                cpu.updateC(result > 0xFF);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "ADC A, E";
            }
        });

        // 0x8C - ADC A, H
        cpu.setIns(0x8C, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int h = cpu.getH() & 0xFF;
                int carry = cpu.isC() ? 1 : 0;

                int result = a + h + carry;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(false);
                cpu.updateH(((a & 0x0F) + (h & 0x0F) + carry) > 0x0F);
                cpu.updateC(result > 0xFF);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "ADC A, H";
            }
        });

        // 0x8D - ADC A, L
        cpu.setIns(0x8D, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int l = cpu.getL() & 0xFF;
                int carry = cpu.isC() ? 1 : 0;

                int result = a + l + carry;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(false);
                cpu.updateH(((a & 0x0F) + (l & 0x0F) + carry) > 0x0F);
                cpu.updateC(result > 0xFF);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "ADC A, L";
            }
        });

        // 0x8E - ADC A, (HL)
        cpu.setIns(0x8E, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                int a = cpu.getA() & 0xFF;
                int hlVal = cpu.read8(cpu.getHL()) & 0xFF;
                int carry = cpu.isC() ? 1 : 0;

                int result = a + hlVal + carry;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(false);
                cpu.updateH(((a & 0x0F) + (hlVal & 0x0F) + carry) > 0x0F);
                cpu.updateC(result > 0xFF);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "ADC A, (HL)";
            }
        });

        // 0x8F - ADC A, A
        cpu.setIns(0x8F, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int carry = cpu.isC() ? 1 : 0;

                int result = a + a + carry;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(false);
                cpu.updateH(((a & 0x0F) + (a & 0x0F) + carry) > 0x0F);
                cpu.updateC(result > 0xFF);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "ADC A, A";
            }
        });

        // 0x90 - SUB B
        cpu.setIns(0x90, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int b = cpu.getB() & 0xFF;

                int result = a - b;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(true);
                cpu.updateH((a & 0x0F) < (b & 0x0F));
                cpu.updateC(a < b);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "SUB B";
            }
        });

        // 0x91 - SUB C
        cpu.setIns(0x91, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int c = cpu.getC() & 0xFF;

                int result = a - c;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(true);
                cpu.updateH((a & 0x0F) < (c & 0x0F));
                cpu.updateC(a < c);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "SUB C";
            }
        });

        // 0x92 - SUB D
        cpu.setIns(0x92, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int d = cpu.getD() & 0xFF;

                int result = a - d;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(true);
                cpu.updateH((a & 0x0F) < (d & 0x0F));
                cpu.updateC(a < d);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "SUB D";
            }
        });

        // 0x93 - SUB E
        cpu.setIns(0x93, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int e = cpu.getE() & 0xFF;

                int result = a - e;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(true);
                cpu.updateH((a & 0x0F) < (e & 0x0F));
                cpu.updateC(a < e);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "SUB E";
            }
        });

        // 0x94 - SUB H
        cpu.setIns(0x94, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int h = cpu.getH() & 0xFF;

                int result = a - h;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(true);
                cpu.updateH((a & 0x0F) < (h & 0x0F));
                cpu.updateC(a < h);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "SUB H";
            }
        });

        // 0x95 - SUB L
        cpu.setIns(0x95, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int l = cpu.getL() & 0xFF;

                int result = a - l;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(true);
                cpu.updateH((a & 0x0F) < (l & 0x0F));
                cpu.updateC(a < l);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "SUB L";
            }
        });

        // 0x96 - SUB (HL)
        cpu.setIns(0x96, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                int a = cpu.getA() & 0xFF;
                int hl = cpu.read8(cpu.getHL()) & 0xFF;

                int result = a - hl;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(true);
                cpu.updateH((a & 0x0F) < (hl & 0x0F));
                cpu.updateC(a < hl);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SUB (HL)";
            }
        });

        // 0x97 - SUB A
        cpu.setIns(0x97, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;

                int result = a - a;

                cpu.setA((byte)result);

                cpu.updateZ(true);
                cpu.updateN(true);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "SUB A";
            }
        });

        // 0x98 - SBC A, B
        cpu.setIns(0x98, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int b = cpu.getB() & 0xFF;
                int carry = cpu.isC() ? 1 : 0;

                int result = a - b - carry;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(true);
                cpu.updateH((a & 0x0F) < ((b & 0x0F) + carry));
                cpu.updateC(a < (b + carry));

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "SBC A, B";
            }
        });

        // 0x99 - SBC A, C
        cpu.setIns(0x99, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int c = cpu.getC() & 0xFF;
                int carry = cpu.isC() ? 1 : 0;

                int result = a - c - carry;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(true);
                cpu.updateH((a & 0x0F) < ((c & 0x0F) + carry));
                cpu.updateC(a < (c + carry));

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "SBC A, C";
            }
        });

        // 0x9A - SBC A, D
        cpu.setIns(0x9A, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int d = cpu.getD() & 0xFF;
                int carry = cpu.isC() ? 1 : 0;

                int result = a - d - carry;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(true);
                cpu.updateH((a & 0x0F) < ((d & 0x0F) + carry));
                cpu.updateC(a < (d + carry));

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "SBC A, D";
            }
        });

        // 0x9B - SBC A, E
        cpu.setIns(0x9B, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int e = cpu.getE() & 0xFF;
                int carry = cpu.isC() ? 1 : 0;

                int result = a - e - carry;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(true);
                cpu.updateH((a & 0x0F) < ((e & 0x0F) + carry));
                cpu.updateC(a < (e + carry));

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "SBC A, E";
            }
        });

        // 0x9C - SBC A, H
        cpu.setIns(0x9C, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int h = cpu.getH() & 0xFF;
                int carry = cpu.isC() ? 1 : 0;

                int result = a - h - carry;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(true);
                cpu.updateH((a & 0x0F) < ((h & 0x0F) + carry));
                cpu.updateC(a < (h + carry));

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "SBC A, H";
            }
        });

        // 0x9D - SBC A, L
        cpu.setIns(0x9D, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int l = cpu.getL() & 0xFF;
                int carry = cpu.isC() ? 1 : 0;

                int result = a - l - carry;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(true);
                cpu.updateH((a & 0x0F) < ((l & 0x0F) + carry));
                cpu.updateC(a < (l + carry));

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "SBC A, L";
            }
        });

        // 0x9E - SBC A, (HL)
        cpu.setIns(0x9E, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                int a = cpu.getA() & 0xFF;
                int hlVal = cpu.read8(cpu.getHL()) & 0xFF;
                int carry = cpu.isC() ? 1 : 0;

                int result = a - hlVal - carry;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(true);
                cpu.updateH((a & 0x0F) < ((hlVal & 0x0F) + carry));
                cpu.updateC(a < (hlVal + carry));

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SBC A, (HL)";
            }
        });

        // 0x9F - SBC A, A
        cpu.setIns(0x9F, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int carry = cpu.isC() ? 1 : 0;

                int result = a - a - carry;

                cpu.setA((byte)result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(true);
                cpu.updateH(carry > 0); // borrow from lower nibble if carry=1
                cpu.updateC(carry == 1);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "SBC A, A";
            }
        });

        // 0xA0 - AND B
        cpu.setIns(0xA0, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte result = (byte)(cpu.getA() & cpu.getB());

                cpu.setA(result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(false);
                cpu.updateH(true);
                cpu.updateC(false);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "AND B";
            }
        });

        // 0xA1 - AND C
        cpu.setIns(0xA1, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte result = (byte)(cpu.getA() & cpu.getC());

                cpu.setA(result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(false);
                cpu.updateH(true);
                cpu.updateC(false);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "AND C";
            }
        });

        // 0xA2 - AND D
        cpu.setIns(0xA2, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte result = (byte)(cpu.getA() & cpu.getD());

                cpu.setA(result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(false);
                cpu.updateH(true);
                cpu.updateC(false);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "AND D";
            }
        });

        // 0xA3 - AND E
        cpu.setIns(0xA3, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte result = (byte)(cpu.getA() & cpu.getE());

                cpu.setA(result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(false);
                cpu.updateH(true);
                cpu.updateC(false);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "AND E";
            }
        });

        // 0xA4 - AND H
        cpu.setIns(0xA4, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte result = (byte)(cpu.getA() & cpu.getH());

                cpu.setA(result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(false);
                cpu.updateH(true);
                cpu.updateC(false);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "AND H";
            }
        });

        // 0xA5 - AND L
        cpu.setIns(0xA5, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte result = (byte)(cpu.getA() & cpu.getL());

                cpu.setA(result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(false);
                cpu.updateH(true);
                cpu.updateC(false);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "AND L";
            }
        });

        // 0xA6 - AND (HL)
        cpu.setIns(0xA6, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte value = cpu.read8(cpu.getHL());
                byte result = (byte)(cpu.getA() & value);

                cpu.setA(result);

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(false);
                cpu.updateH(true);
                cpu.updateC(false);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "AND (HL)";
            }
        });

        // 0xA7 - AND A
        cpu.setIns(0xA7, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte a = cpu.getA();

                cpu.setA(a);

                cpu.updateZ(a == 0);
                cpu.updateN(false);
                cpu.updateH(true);
                cpu.updateC(false);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "AND A";
            }
        });

        // 0xA8 - XOR B
        cpu.setIns(0xA8, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte result = (byte)(cpu.getA() ^ cpu.getB());
                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "XOR B";
            }
        });

        // 0xA9 - XOR C
        cpu.setIns(0xA9, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte result = (byte)(cpu.getA() ^ cpu.getC());
                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "XOR C";
            }
        });

        // 0xAA - XOR D
        cpu.setIns(0xAA, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte result = (byte)(cpu.getA() ^ cpu.getD());
                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "XOR D";
            }
        });

        // 0xAB - XOR E
        cpu.setIns(0xAB, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte result = (byte)(cpu.getA() ^ cpu.getE());
                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "XOR E";
            }
        });

        // 0xAC - XOR H
        cpu.setIns(0xAC, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte result = (byte)(cpu.getA() ^ cpu.getH());
                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "XOR H";
            }
        });

        // 0xAD - XOR L
        cpu.setIns(0xAD, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte result = (byte)(cpu.getA() ^ cpu.getL());
                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "XOR L";
            }
        });

        // 0xAE - XOR (HL)
        cpu.setIns(0xAE, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte value = cpu.read8(cpu.getHL());
                byte result = (byte)(cpu.getA() ^ value);

                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "XOR (HL)";
            }
        });

        // 0xAF - XOR A
        cpu.setIns(0xAF, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setA((byte)0);

                cpu.updateZ(true);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "XOR A";
            }
        });

        // 0xB0 - OR B
        cpu.setIns(0xB0, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte result = (byte)(cpu.getA() | cpu.getB());
                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "OR B";
            }
        });
        
        // 0xB1 - OR C
        cpu.setIns(0xB1, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte result = (byte)(cpu.getA() | cpu.getC());
                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "OR C";
            }
        });

        // 0xB2 - OR D
        cpu.setIns(0xB2, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte result = (byte)(cpu.getA() | cpu.getD());
                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "OR D";
            }
        });

        // 0xB3 - OR E
        cpu.setIns(0xB3, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte result = (byte)(cpu.getA() | cpu.getE());
                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "OR E";
            }
        });

        // 0xB4 - OR H
        cpu.setIns(0xB4, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte result = (byte)(cpu.getA() | cpu.getH());
                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "OR H";
            }
        });

        // 0xB5 - OR L
        cpu.setIns(0xB5, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte result = (byte)(cpu.getA() | cpu.getL());
                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "OR L";
            }
        });

        // 0xB6 - OR (HL)
        cpu.setIns(0xB6, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte value = cpu.read8(cpu.getHL());
                byte result = (byte)(cpu.getA() | value);

                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "OR (HL)";
            }
        });

        // 0xB7 - OR A
        cpu.setIns(0xB7, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte a = cpu.getA();

                cpu.setA(a);

                cpu.updateZ(a == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "OR A";
            }
        });

        // 0xB8 - CP B
        cpu.setIns(0xB8, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int b = cpu.getB() & 0xFF;
                int result = a - b;

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(true);
                cpu.updateH(((a & 0x0F) - (b & 0x0F)) < 0);
                cpu.updateC(a < b);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "CP B";
            }
        });

        // 0xB9 - CP C
        cpu.setIns(0xB9, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int c = cpu.getC() & 0xFF;
                int result = a - c;

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(true);
                cpu.updateH(((a & 0x0F) - (c & 0x0F)) < 0);
                cpu.updateC(a < c);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "CP C";
            }
        });

        // 0xBA - CP D
        cpu.setIns(0xBA, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int d = cpu.getD() & 0xFF;
                int result = a - d;

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(true);
                cpu.updateH(((a & 0x0F) - (d & 0x0F)) < 0);
                cpu.updateC(a < d);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "CP D";
            }
        });

        // 0xBB - CP E
        cpu.setIns(0xBB, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int e = cpu.getE() & 0xFF;
                int result = a - e;

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(true);
                cpu.updateH(((a & 0x0F) - (e & 0x0F)) < 0);
                cpu.updateC(a < e);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "CP E";
            }
        });

        // 0xBC - CP H
        cpu.setIns(0xBC, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int h = cpu.getH() & 0xFF;
                int result = a - h;

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(true);
                cpu.updateH(((a & 0x0F) - (h & 0x0F)) < 0);
                cpu.updateC(a < h);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "CP H";
            }
        });

        // 0xBD - CP L
        cpu.setIns(0xBD, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int a = cpu.getA() & 0xFF;
                int l = cpu.getL() & 0xFF;
                int result = a - l;

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(true);
                cpu.updateH(((a & 0x0F) - (l & 0x0F)) < 0);
                cpu.updateC(a < l);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "CP L";
            }
        });

        // 0xBE - CP (HL)
        cpu.setIns(0xBE, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                int a = cpu.getA() & 0xFF;
                int value = cpu.read8(cpu.getHL()) & 0xFF;
                int result = a - value;

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(true);
                cpu.updateH(((a & 0x0F) - (value & 0x0F)) < 0);
                cpu.updateC(a < value);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "CP (HL)";
            }
        });

        // 0xBF - CP A
        cpu.setIns(0xBF, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.updateZ(true);
                cpu.updateN(true);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "CP A";
            }
        });

        // 0xC0 - RET NZ
        cpu.setIns(0xC0, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                if (!cpu.isZ()) {
                    int lo = cpu.read8(cpu.getSP()) & 0xFF;
                    cpu.setSP(SHORT((cpu.getSP() + 1)));
                    int hi = cpu.read8(cpu.getSP()) & 0xFF;
                    cpu.setSP(SHORT(cpu.getSP() + 1));

                    cpu.setPC(SHORT(SHORT(hi << 8) | lo));

                    cpu.incCycles(20);
                } else {
                    cpu.incCycles(8);
                }
            }

            @Override
            public String name() {
                return "RET NZ";
            }
        });

        // 0xC1 - POP BC
        cpu.setIns(0xC1, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                int lo = cpu.read8(cpu.getSP()) & 0xFF;
                cpu.setSP(SHORT(cpu.getSP() + 1));

                int hi = cpu.read8(cpu.getSP()) & 0xFF;
                cpu.setSP(SHORT(cpu.getSP() + 1));

                cpu.setBC(SHORT((hi << 8) | lo));

                cpu.incCycles(12);
            }

            @Override
            public String name() {
                return "POP BC";
            }
        });

        // 0xC2 - JP NZ, a16
        cpu.setIns(0xC2, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                int lo = cpu.read8(cpu.getPC()) & 0xFF;
                cpu.setPC(SHORT(cpu.getPC() + 1));

                int hi = cpu.read8(cpu.getPC()) & 0xFF;
                cpu.setPC(SHORT(cpu.getPC() + 1));

                int addr = (hi << 8) | lo;

                if (!cpu.isZ()) {
                    cpu.setPC(SHORT(addr));
                    cpu.incCycles(16);
                } else {
                    cpu.incCycles(12);
                }
            }

            @Override
            public String name() {
                return "JP NZ, a16";
            }
        });

        // 0xC3 - JP a16
        cpu.setIns(0xC3, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                short oldPC = cpu.getPC();

                int lo = cpu.read8(cpu.getPC()) & 0xFF;
                cpu.setPC(SHORT(cpu.getPC() + 1));

                int hi = cpu.read8(cpu.getPC()) & 0xFF;
                cpu.setPC(SHORT(cpu.getPC() + 1));

                cpu.setPC(SHORT((hi << 8) | lo));

                cpu.incCycles(16);
                
                //System.out.printf("INT RELATED :: JP 0x%04X from PC = 0x%04X\n", SHORT((hi << 8) | lo), oldPC & 0xFFFF);
            }

            @Override
            public String name() {
                return "JP a16";
            }
        });

        // 0xC4 - CALL NZ, a16
        cpu.setIns(0xC4, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                int lo = cpu.read8(cpu.getPC()) & 0xFF;
                cpu.setPC(SHORT(cpu.getPC() + 1));

                int hi = cpu.read8(cpu.getPC()) & 0xFF;
                cpu.setPC(SHORT(cpu.getPC() + 1));

                int addr = (hi << 8) | lo;

                if (!cpu.isZ()) {
                    int pc = cpu.getPC();
                    cpu.setSP(SHORT(cpu.getSP() - 1));
                    cpu.write8(cpu.getSP(), (byte)((pc >> 8) & 0xFF));
                    cpu.setSP(SHORT(cpu.getSP() - 1));
                    cpu.write8(cpu.getSP(), (byte)(pc & 0xFF));

                    cpu.setPC(SHORT(addr));

                    cpu.incCycles(24);
                } else {
                    cpu.incCycles(12);
                }
            }

            @Override
            public String name() {
                return "CALL NZ, a16";
            }
        });

        // 0xC5 - PUSH BC
        cpu.setIns(0xC5, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                int bc = cpu.getBC();

                cpu.setSP(SHORT(cpu.getSP() - 1));
                cpu.write8(cpu.getSP(), (byte)((bc >> 8) & 0xFF));

                cpu.setSP(SHORT(cpu.getSP() - 1));
                cpu.write8(cpu.getSP(), (byte)(bc & 0xFF));

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "PUSH BC";
            }
        });

        // 0xC6 - ADD A, d8
        cpu.setIns(0xC6, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                int a = cpu.getA() & 0xFF;

                int value = cpu.read8(cpu.getPC()) & 0xFF;
                cpu.setPC(SHORT(cpu.getPC() + 1));

                int result = a + value;

                cpu.setA((byte)(result & 0xFF));

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(false);
                cpu.updateH(((a & 0x0F) + (value & 0x0F)) > 0x0F);
                cpu.updateC(result > 0xFF);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "ADD A, d8";
            }
        });

        // 0xC7 - RST 00H
        cpu.setIns(0xC7, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                int pc = cpu.getPC();

                cpu.setSP(SHORT(cpu.getSP() - 1));
                cpu.write8(cpu.getSP(), (byte)((pc >> 8) & 0xFF));

                cpu.setSP(SHORT(cpu.getSP() - 1));
                cpu.write8(cpu.getSP(), (byte)(pc & 0xFF));

                cpu.setPC(SHORT(0x0000));

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "RST 00H";
            }
        });

        // 0xC8 - RET Z
        cpu.setIns(0xC8, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                if (cpu.isZ()) {
                    int lo = cpu.read8(cpu.getSP()) & 0xFF;
                    cpu.setSP(SHORT(cpu.getSP() + 1));

                    int hi = cpu.read8(cpu.getSP()) & 0xFF;
                    cpu.setSP(SHORT(cpu.getSP() + 1));

                    cpu.setPC(SHORT((hi << 8) | lo));

                    cpu.incCycles(20);
                } else {
                    cpu.incCycles(8);
                }
            }

            @Override
            public String name() {
                return "RET Z";
            }
        });

        // 0xC9 - RET
        cpu.setIns(0xC9, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                int lo = cpu.read8(cpu.getSP()) & 0xFF;
                cpu.setSP(SHORT(cpu.getSP() + 1));

                int hi = cpu.read8(cpu.getSP()) & 0xFF;
                cpu.setSP(SHORT(cpu.getSP() + 1));

                cpu.setPC(SHORT((hi << 8) | lo));

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "RET";
            }
        });

        // 0xCA - JP Z, a16
        cpu.setIns(0xCA, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                int lo = cpu.read8(cpu.getPC()) & 0xFF;
                cpu.setPC(SHORT(cpu.getPC() + 1));

                int hi = cpu.read8(cpu.getPC()) & 0xFF;
                cpu.setPC(SHORT(cpu.getPC() + 1));

                int addr = (hi << 8) | lo;

                if (cpu.isZ()) {
                    cpu.setPC(SHORT(addr));
                    cpu.incCycles(16);
                } else {
                    cpu.incCycles(12);
                }
            }

            @Override
            public String name() {
                return "JP Z, a16";
            }
        });

        // not really an instrucion, should never be executed
        // 0xCB - PREFIX CB
        cpu.setIns(0xCB, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.incCycles(4);
                throw new RuntimeException("something has gone very wrong, executed 0xCB");
            }

            @Override
            public String name() {
                return "PREFIX CB";
            }
        });

        // 0xCC - CALL Z, a16
        cpu.setIns(0xCC, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                int lo = cpu.read8(cpu.getPC()) & 0xFF;
                cpu.setPC(SHORT(cpu.getPC() + 1));

                int hi = cpu.read8(cpu.getPC()) & 0xFF;
                cpu.setPC(SHORT(cpu.getPC() + 1));

                int addr = (hi << 8) | lo;

                if (cpu.isZ()) {
                    int pc = cpu.getPC();

                    cpu.setSP(SHORT(cpu.getSP() - 1));
                    cpu.write8(cpu.getSP(), (byte)((pc >> 8) & 0xFF));

                    cpu.setSP(SHORT(cpu.getSP() - 1));
                    cpu.write8(cpu.getSP(), (byte)(pc & 0xFF));

                    cpu.setPC(SHORT(addr));

                    cpu.incCycles(24);
                } else {
                    cpu.incCycles(12);
                }
            }

            @Override
            public String name() {
                return "CALL Z, a16";
            }
        });

        // 0xCD - CALL a16
        cpu.setIns(0xCD, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                int lo = cpu.read8(cpu.getPC()) & 0xFF;
                cpu.setPC(SHORT(cpu.getPC() + 1));

                int hi = cpu.read8(cpu.getPC()) & 0xFF;
                cpu.setPC(SHORT(cpu.getPC() + 1));
                int pc = cpu.getPC();

                cpu.setSP(SHORT(cpu.getSP() - 1));
                cpu.write8(cpu.getSP(), (byte)((pc >> 8) & 0xFF));

                cpu.setSP(SHORT(cpu.getSP() - 1));
                cpu.write8(cpu.getSP(), (byte)(pc & 0xFF));

                cpu.setPC(SHORT((hi << 8) | lo));

                cpu.incCycles(24);
            }

            @Override
            public String name() {
                return "CALL a16";
            }
        });

        // 0xCE - ADC A, d8
        cpu.setIns(0xCE, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                int a = cpu.getA() & 0xFF;
                int value = cpu.read8(cpu.getPC()) & 0xFF;
                cpu.setPC(SHORT(cpu.getPC() + 1));

                int carry = (cpu.getC() == 1) ? 1 : 0;

                int result = a + value + carry;

                cpu.setA((byte)(result & 0xFF));

                cpu.updateZ((result & 0xFF) == 0);
                cpu.updateN(false);
                cpu.updateH(((a & 0x0F) + (value & 0x0F) + carry) > 0x0F);
                cpu.updateC(result > 0xFF);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "ADC A, d8";
            }
        });

        // 0xCF - RST 08H
        cpu.setIns(0xCF, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                int pc = cpu.getPC();

                cpu.setSP(SHORT(cpu.getSP() - 1));
                cpu.write8(cpu.getSP(), (byte)((pc >> 8) & 0xFF));

                cpu.setSP(SHORT(cpu.getSP() - 1));
                cpu.write8(cpu.getSP(), (byte)(pc & 0xFF));

                cpu.setPC(SHORT(0x0008));

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "RST 08H";
            }
        });

        // 0xD0 - RET NC
        cpu.setIns(0xD0, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                if (!cpu.isC()) {
                    int lo = cpu.read8(cpu.getSP()) & 0xFF;
                    cpu.setSP(SHORT(cpu.getSP() + 1));

                    int hi = cpu.read8(cpu.getSP()) & 0xFF;
                    cpu.setSP(SHORT(cpu.getSP() + 1));

                    cpu.setPC(SHORT((hi << 8) | lo));

                    cpu.incCycles(20);
                } else {
                    cpu.incCycles(8);
                }
            }

            @Override
            public String name() {
                return "RET NC";
            }
        });

        // 0xD1 - POP DE
        cpu.setIns(0xD1, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte low = cpu.pop8();
                byte high = cpu.pop8();

                short value = SHORT((high << 8) | (low));
                cpu.setDE(value);

                cpu.incCycles(12);
            }

            @Override
            public String name() {
                return "POP DE";
            }
        });

        // 0xD2 - JP NC, d16
        cpu.setIns(0xD2, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte low = cpu.read8(cpu.getPC());
                cpu.setPC(SHORT(cpu.getPC() + 1));

                byte high = cpu.read8(cpu.getPC());
                cpu.setPC(SHORT(cpu.getPC() + 1));

                short addr = SHORT((high << 8) | (low));

                if (!cpu.isC()) {
                    cpu.setPC(addr);
                    cpu.incCycles(16);
                } else {
                    cpu.incCycles(12);
                }
            }

            @Override
            public String name() {
                return "JP NC, d16";
            }
        });

        // 0xD3 - UNUSED
        cpu.setIns(0xD3, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                throw new RuntimeException("Unused opcode 0xD3");
            }

            @Override
            public String name() {
                return "UNUSED";
            }
        });

        // 0xD4 - CALL NC, d16
        cpu.setIns(0xD4, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte low = cpu.read8(cpu.getPC());
                cpu.setPC(SHORT(cpu.getPC() + 1));

                byte high = cpu.read8(cpu.getPC());
                cpu.setPC(SHORT(cpu.getPC() + 1));

                short addr = SHORT((high << 8) | (low));

                if (!cpu.isC()) {
                    cpu.push16(cpu.getPC());
                    cpu.setPC(addr);
                    cpu.incCycles(24);
                } else {
                    cpu.incCycles(12);
                }
            }

            @Override
            public String name() {
                return "CALL NC, d16";
            }
        });

        // 0xD5 - PUSH DE
        cpu.setIns(0xD5, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                short de = cpu.getDE();

                cpu.push8(BYTE(de >> 8));
                cpu.push8(BYTE(de));

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "PUSH DE";
            }
        });

        // 0xD6 - SUB d8
        cpu.setIns(0xD6, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte value = cpu.read8(cpu.getPC());
                cpu.setPC(SHORT(cpu.getPC() + 1));

                int a = cpu.getA() & 0xFF;
                int v = value & 0xFF;

                int result = a - v;

                cpu.setA(BYTE(result));

                cpu.updateZ(BYTE(result) == 0);
                cpu.updateN(true);
                cpu.updateH(((a & 0x0F) - (v & 0x0F)) < 0);
                cpu.updateC(result < 0);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SUB d8";
            }
        });

        // 0xD7 - RST 10h
        cpu.setIns(0xD7, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.push16(cpu.getPC());
                cpu.setPC(SHORT(0x10));

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "RST 10h";
            }
        });

        // 0xD8 - RET C
        cpu.setIns(0xD8, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                if (cpu.isC()) {
                    byte low = cpu.pop8();
                    byte high = cpu.pop8();

                    short addr = SHORT((high << 8) | (low));
                    cpu.setPC(addr);

                    cpu.incCycles(20);
                } else {
                    cpu.incCycles(8);
                }
            }

            @Override
            public String name() {
                return "RET C";
            }
        });

        // 0xD9 - RETI
        cpu.setIns(0xD9, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte low = cpu.pop8();
                byte high = cpu.pop8();

                short addr = SHORT((high << 8) | (low));
                cpu.setPC(addr);

                cpu.enableInterrupts();

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "RETI";
            }
        });

        // 0xDA - JP C, d16
        cpu.setIns(0xDA, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte low = cpu.read8(cpu.getPC());
                cpu.setPC(SHORT(cpu.getPC() + 1));

                byte high = cpu.read8(cpu.getPC());
                cpu.setPC(SHORT(cpu.getPC() + 1));

                short addr = SHORT((high << 8) | (low));

                if (cpu.isC()) {
                    cpu.setPC(addr);
                    cpu.incCycles(16);
                } else {
                    cpu.incCycles(12);
                }
            }

            @Override
            public String name() {
                return "JP C, d16";
            }
        });

        // 0xDB - UNUSED
        cpu.setIns(0xDB, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                throw new RuntimeException("Unused opcode 0xDB");
            }

            @Override
            public String name() {
                return "UNUSED";
            }
        });

        // 0xDC - CALL C, d16
        cpu.setIns(0xDC, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte low = cpu.read8(cpu.getPC());
                cpu.setPC(SHORT(cpu.getPC() + 1));

                byte high = cpu.read8(cpu.getPC());
                cpu.setPC(SHORT(cpu.getPC() + 1));

                short addr = SHORT((high << 8) | (low));

                if (cpu.isC()) {
                    cpu.push16(cpu.getPC());
                    cpu.setPC(addr);
                    cpu.incCycles(24);
                } else {
                    cpu.incCycles(12);
                }
            }

            @Override
            public String name() {
                return "CALL C, d16";
            }
        });

        // 0xDD - UNUSED
        cpu.setIns(0xDD, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                throw new RuntimeException("Unused opcode 0xDD");
            }

            @Override
            public String name() {
                return "UNUSED";
            }
        });

        // 0xDE - SBC d8
        cpu.setIns(0xDE, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte value = cpu.read8(cpu.getPC());
                cpu.setPC(SHORT(cpu.getPC() + 1));

                int a = cpu.getA() & 0xFF;
                int v = value & 0xFF;
                int carry = cpu.isC() ? 1 : 0;

                int result = a - v - carry;

                cpu.setA(BYTE(result));

                cpu.updateZ(BYTE(result) == 0);
                cpu.updateN(true);
                cpu.updateH(((a & 0x0F) - (v & 0x0F) - carry) < 0);
                cpu.updateC(result < 0);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SBC d8";
            }
        });

        // 0xDF - RST 18h
        cpu.setIns(0xDF, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.push16(cpu.getPC());
                cpu.setPC(SHORT(0x18));

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "RST 18h";
            }
        });
        
        // 0xE0 - LDH (n), A
        cpu.setIns(0xE0, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte offset = cpu.read8(cpu.getPC());
                cpu.setPC(SHORT(cpu.getPC() + 1));

                short addr = SHORT(0xFF00 | BYTE(offset));

                System.out.printf("writing %d to %d (0x%04X) (PC: %d)\n", cpu.getA() & 0xFF, addr & 0xFFFF, addr & 0xFFFF, cpu.getPC() & 0xFFFF);
                cpu.write8(addr, cpu.getA());

                cpu.incCycles(12);
            }

            @Override
            public String name() {
                return "LDH (n), A";
            }
        });

        // 0xE1 - POP HL
        cpu.setIns(0xE1, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte low = cpu.pop8();
                byte high = cpu.pop8();

                cpu.setHL(SHORT((high << 8) | low));

                cpu.incCycles(12);
            }

            @Override
            public String name() {
                return "POP HL";
            }
        });

        // 0xE2 - LD (C), A
        cpu.setIns(0xE2, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                short addr = SHORT(0xFF00 | (cpu.getC() & 0xFF));
                cpu.write8(addr, cpu.getA());

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD (C), A";
            }
        });

        // 0xE3 - UNUSED
        cpu.setIns(0xE3, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                throw new RuntimeException("Unused opcode 0xE3");
            }

            @Override
            public String name() {
                return "UNUSED";
            }
        });

        // 0xE4 - UNUSED
        cpu.setIns(0xE4, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                throw new RuntimeException("Unused opcode 0xE4");
            }

            @Override
            public String name() {
                return "UNUSED";
            }
        });

        // 0xE5 - PUSH HL
        cpu.setIns(0xE5, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                short hl = cpu.getHL();

                cpu.push8(BYTE(hl >> 8));
                cpu.push8(BYTE(hl));

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "PUSH HL";
            }
        });

        // 0xE6 - AND d8
        cpu.setIns(0xE6, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte value = cpu.read8(cpu.getPC());
                cpu.setPC(SHORT(cpu.getPC() + 1));

                byte result = BYTE(cpu.getA() & value);
                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(true);
                cpu.updateC(false);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "AND d8";
            }
        });

        // 0xE7 - RST 20h
        cpu.setIns(0xE7, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.push16(cpu.getPC());
                cpu.setPC(SHORT(0x20));

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "RST 20h";
            }
        });

        // 0xE8 - ADD SP, r8
        cpu.setIns(0xE8, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte r8 = cpu.read8(cpu.getPC());
                cpu.setPC(SHORT(cpu.getPC() + 1));

                int sp = cpu.getSP() & 0xFFFF;
                int value = r8;

                int result = sp + value;

                cpu.updateZ(false);
                cpu.updateN(false);
                cpu.updateH(((sp & 0xF) + (value & 0xF)) > 0xF);
                cpu.updateC(((sp & 0xFF) + (value & 0xFF)) > 0xFF);

                cpu.setSP(SHORT(result));

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "ADD SP, r8";
            }
        });

        // 0xE9 - JP HL
        cpu.setIns(0xE9, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setPC(cpu.getHL());
                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "JP HL";
            }
        });

        // 0xEA - LD (a16), A
        cpu.setIns(0xEA, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte low = cpu.read8(cpu.getPC());
                cpu.setPC(SHORT(cpu.getPC() + 1));

                byte high = cpu.read8(cpu.getPC());
                cpu.setPC(SHORT(cpu.getPC() + 1));

                short addr = SHORT((high << 8) | low);
                cpu.write8(addr, cpu.getA());

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "LD (a16), A";
            }
        });

        // 0xEB - UNUSED
        cpu.setIns(0xEB, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                throw new RuntimeException("Unused opcode 0xEB");
            }

            @Override
            public String name() {
                return "UNUSED";
            }
        });

        // 0xEC - UNUSED
        cpu.setIns(0xEC, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                throw new RuntimeException("Unused opcode 0xEC");
            }

            @Override
            public String name() {
                return "UNUSED";
            }
        });

        // 0xED - UNUSED
        cpu.setIns(0xED, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                throw new RuntimeException("Unused opcode 0xED");
            }

            @Override
            public String name() {
                return "UNUSED";
            }
        });

        // 0xEE - XOR d8
        cpu.setIns(0xEE, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte value = cpu.read8(cpu.getPC());
                cpu.setPC(SHORT(cpu.getPC() + 1));

                byte result = BYTE(cpu.getA() ^ value);
                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "XOR d8";
            }
        });

        // 0xEF - RST 28h
        cpu.setIns(0xEF, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.push16(cpu.getPC());
                cpu.setPC(SHORT(0x28));

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "RST 28h";
            }
        });
        
       // 0xF0 - LDH A, (n)
        cpu.setIns(0xF0, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte offset = cpu.read8(cpu.getPC());
                cpu.setPC(SHORT(cpu.getPC() + 1));

                short addr = SHORT(0xFF00 | BYTE(offset));
                cpu.setA(cpu.read8(addr));
                //System.out.println(((int)cpu.getA() & 0xFF));
                
                cpu.incCycles(12);
            }

            @Override
            public String name() {
                return "LDH A, (n)";
            }
        });

        // 0xF1 - POP AF
        cpu.setIns(0xF1, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte low = cpu.pop8();
                byte high = cpu.pop8();

                cpu.setAF(SHORT((high << 8) | low));

                cpu.incCycles(12);
            }

            @Override
            public String name() {
                return "POP AF";
            }
        });

        // 0xF2 - LD A, (C)
        cpu.setIns(0xF2, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                short addr = SHORT(0xFF00 | BYTE(cpu.getC()));
                cpu.setA(cpu.read8(addr));

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD A, (C)";
            }
        });

        // 0xF3 - DI
        cpu.setIns(0xF3, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.disableInterrupts();
                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "DI";
            }
        });

        // 0xF4 - UNUSED
        cpu.setIns(0xF4, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                throw new RuntimeException("Unused opcode 0xF4");
            }

            @Override
            public String name() {
                return "UNUSED";
            }
        });

        // 0xF5 - PUSH AF
        cpu.setIns(0xF5, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                short af = cpu.getAF();

                cpu.push8(BYTE(af >> 8));
                cpu.push8(BYTE(af));

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "PUSH AF";
            }
        });

        // 0xF6 - OR d8
        cpu.setIns(0xF6, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte value = cpu.read8(cpu.getPC());
                cpu.setPC(SHORT(cpu.getPC() + 1));

                byte result = BYTE(cpu.getA() | value);
                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "OR d8";
            }
        });

        // 0xF7 - RST 30h
        cpu.setIns(0xF7, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.push16(cpu.getPC());
                cpu.setPC(SHORT(0x30));

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "RST 30h";
            }
        });

        // 0xF8 - LD HL, SP+r8
        cpu.setIns(0xF8, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte r8 = cpu.read8(cpu.getPC());
                cpu.setPC(SHORT(cpu.getPC() + 1));

                int sp = cpu.getSP() & 0xFFFF;
                int result = sp + r8;

                cpu.updateZ(false);
                cpu.updateN(false);
                cpu.updateH(((sp & 0xF) + (r8 & 0xF)) > 0xF);
                cpu.updateC(((sp & 0xFF) + (r8 & 0xFF)) > 0xFF);

                cpu.setHL(SHORT(result));

                cpu.incCycles(12);
            }

            @Override
            public String name() {
                return "LD HL, SP+r8";
            }
        });

        // 0xF9 - LD SP, HL
        cpu.setIns(0xF9, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.setSP(cpu.getHL());
                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "LD SP, HL";
            }
        });

        // 0xFA - LD A, (a16)
        cpu.setIns(0xFA, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte low = cpu.read8(cpu.getPC());
                cpu.setPC(SHORT(cpu.getPC() + 1));

                byte high = cpu.read8(cpu.getPC());
                cpu.setPC(SHORT(cpu.getPC() + 1));

                short addr = SHORT((high << 8) | low);
                cpu.setA(cpu.read8(addr));

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "LD A, (a16)";
            }
        });

        // 0xFB - EI
        cpu.setIns(0xFB, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                cpu.enableInterrupts();
                cpu.incCycles(4);
            }

            @Override
            public String name() {
                return "EI";
            }
        });

        // 0xFC - UNUSED
        cpu.setIns(0xFC, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                throw new RuntimeException("Unused opcode 0xFC");
            }

            @Override
            public String name() {
                return "UNUSED";
            }
        });

        // 0xFD - UNUSED
        cpu.setIns(0xFD, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                throw new RuntimeException("Unused opcode 0xFD");
            }

            @Override
            public String name() {
                return "UNUSED";
            }
        });

        // 0xFE - CP d8
        cpu.setIns(0xFE, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                byte value = cpu.read8(cpu.getPC());
                cpu.setPC(SHORT(cpu.getPC() + 1));

                byte a = cpu.getA();
                int result = (a & 0xFF) - (value & 0xFF);
                

                cpu.updateZ(BYTE(result) == 0);
                cpu.updateN(true);
                cpu.updateH((a & 0x0F) < (value & 0x0F));
                cpu.updateC((result & 0x100) != 0);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "CP d8";
            }
        });

        // 0xFF - RST 38h
        cpu.setIns(0xFF, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.push16(cpu.getPC());
                cpu.setPC(SHORT(0x38));

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "RST 38h";
            }
        });
    }

    public static void buildCbInstructionTable(Cpu cpu) {
        // CB 0x00 - RLC B
        cpu.setCBIns(0x00, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte b = cpu.getB();
                boolean newCarry = (b & 0x80) != 0;
                byte result = BYTE(((b << 1) | (newCarry ? 1 : 0)));

                cpu.setB(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RLC B";
            }
        });

        // CB 0x01 - RLC C
        cpu.setCBIns(0x01, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte c = cpu.getC();
                boolean newCarry = (c & 0x80) != 0;
                byte result = BYTE(((c << 1) | (newCarry ? 1 : 0)));

                cpu.setC(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RLC C";
            }
        });

        // CB 0x02 - RLC D
        cpu.setCBIns(0x02, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte d = cpu.getD();
                boolean newCarry = (d & 0x80) != 0;
                byte result = BYTE(((d << 1) | (newCarry ? 1 : 0)));

                cpu.setD(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RLC D";
            }
        });

        // CB 0x03 - RLC E
        cpu.setCBIns(0x03, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte e = cpu.getE();
                boolean newCarry = (e & 0x80) != 0;
                byte result = BYTE(((e << 1) | (newCarry ? 1 : 0)));

                cpu.setE(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RLC E";
            }
        });

        // CB 0x04 - RLC H
        cpu.setCBIns(0x04, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte h = cpu.getH();
                boolean newCarry = (h & 0x80) != 0;
                byte result = BYTE(((h << 1) | (newCarry ? 1 : 0)));

                cpu.setH(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RLC H";
            }
        });

        // CB 0x05 - RLC L
        cpu.setCBIns(0x05, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte l = cpu.getL();
                boolean newCarry = (l & 0x80) != 0;
                byte result = BYTE(((l << 1) | (newCarry ? 1 : 0)));

                cpu.setL(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RLC L";
            }
        });

        // CB 0x06 - RLC (HL)
        cpu.setCBIns(0x06, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                short addr = cpu.getHL();
                byte v = cpu.read8(addr);
                boolean newCarry = (v & 0x80) != 0;
                byte result = BYTE(((v << 1) | (newCarry ? 1 : 0)));

                cpu.write8(addr, result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "RLC (HL)";
            }
        });

        // CB 0x07 - RLC A
        cpu.setCBIns(0x07, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte a = cpu.getA();
                boolean newCarry = (a & 0x80) != 0;
                byte result = BYTE(((a << 1) | (newCarry ? 1 : 0)));

                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RLC A";
            }
        });

        // CB 0x08 - RRC B
        cpu.setCBIns(0x08, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte b = cpu.getB();
                boolean newCarry = (b & 0x01) != 0;
                byte result = BYTE(((b >> 1) | (newCarry ? 0x80 : 0)));

                cpu.setB(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RRC B";
            }
        });

        // CB 0x09 - RRC C
        cpu.setCBIns(0x09, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte c = cpu.getC();
                boolean newCarry = (c & 0x01) != 0;
                byte result = BYTE(((c >> 1) | (newCarry ? 0x80 : 0)));

                cpu.setC(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RRC C";
            }
        });

        // CB 0x0A - RRC D
        cpu.setCBIns(0x0A, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte d = cpu.getD();
                boolean newCarry = (d & 0x01) != 0;
                byte result = BYTE(((d >> 1) | (newCarry ? 0x80 : 0)));

                cpu.setD(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RRC D";
            }
        });

        // CB 0x0B - RRC E
        cpu.setCBIns(0x0B, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte e = cpu.getE();
                boolean newCarry = (e & 0x01) != 0;
                byte result = BYTE(((e >> 1) | (newCarry ? 0x80 : 0)));

                cpu.setE(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RRC E";
            }
        });

        // CB 0x0C - RRC H
        cpu.setCBIns(0x0C, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte h = cpu.getH();
                boolean newCarry = (h & 0x01) != 0;
                byte result = BYTE(((h >> 1) | (newCarry ? 0x80 : 0)));

                cpu.setH(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RRC H";
            }
        });

        // CB 0x0D - RRC L
        cpu.setCBIns(0x0D, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte l = cpu.getL();
                boolean newCarry = (l & 0x01) != 0;
                byte result = BYTE(((l >> 1) | (newCarry ? 0x80 : 0)));

                cpu.setL(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RRC L";
            }
        });

        // CB 0x0E - RRC (HL)
        cpu.setCBIns(0x0E, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                short addr = cpu.getHL();
                byte v = cpu.read8(addr);
                boolean newCarry = (v & 0x01) != 0;
                byte result = BYTE(((v >> 1) | (newCarry ? 0x80 : 0)));

                cpu.write8(addr, result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "RRC (HL)";
            }
        });

        // CB 0x0F - RRC A
        cpu.setCBIns(0x0F, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte a = cpu.getA();
                boolean newCarry = (a & 0x01) != 0;
                byte result = BYTE(((a >> 1) | (newCarry ? 0x80 : 0)));

                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RRC A";
            }
        });

        // CB 0x10 - RL B
        cpu.setCBIns(0x10, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte b = cpu.getB();
                boolean oldCarry = cpu.isC();
                boolean newCarry = (b & 0x80) != 0;

                byte result = BYTE(((b << 1) | (oldCarry ? 1 : 0)));

                cpu.setB(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RL B";
            }
        });

        // CB 0x11 - RL C
        cpu.setCBIns(0x11, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte c = cpu.getC();
                boolean oldCarry = cpu.isC();
                boolean newCarry = (c & 0x80) != 0;

                byte result = BYTE(((c << 1) | (oldCarry ? 1 : 0)));

                cpu.setC(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RL C";
            }
        });

        // CB 0x12 - RL D
        cpu.setCBIns(0x12, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte d = cpu.getD();
                boolean oldCarry = cpu.isC();
                boolean newCarry = (d & 0x80) != 0;

                byte result = BYTE(((d << 1) | (oldCarry ? 1 : 0)));

                cpu.setD(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RL D";
            }
        });

        // CB 0x13 - RL E
        cpu.setCBIns(0x13, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte e = cpu.getE();
                boolean oldCarry = cpu.isC();
                boolean newCarry = (e & 0x80) != 0;

                byte result = BYTE(((e << 1) | (oldCarry ? 1 : 0)));

                cpu.setE(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RL E";
            }
        });

        // CB 0x14 - RL H
        cpu.setCBIns(0x14, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte h = cpu.getH();
                boolean oldCarry = cpu.isC();
                boolean newCarry = (h & 0x80) != 0;

                byte result = BYTE(((h << 1) | (oldCarry ? 1 : 0)));

                cpu.setH(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RL H";
            }
        });

        // CB 0x15 - RL L
        cpu.setCBIns(0x15, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte l = cpu.getL();
                boolean oldCarry = cpu.isC();
                boolean newCarry = (l & 0x80) != 0;

                byte result = BYTE(((l << 1) | (oldCarry ? 1 : 0)));

                cpu.setL(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RL L";
            }
        });

        // CB 0x16 - RL (HL)
        cpu.setCBIns(0x16, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                short addr = cpu.getHL();
                byte v = cpu.read8(addr);
                boolean oldCarry = cpu.isC();
                boolean newCarry = (v & 0x80) != 0;

                byte result = BYTE(((v << 1) | (oldCarry ? 1 : 0)));

                cpu.write8(addr, result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "RL (HL)";
            }
        });

        // CB 0x17 - RL A
        cpu.setCBIns(0x17, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte a = cpu.getA();
                boolean oldCarry = cpu.isC();
                boolean newCarry = (a & 0x80) != 0;

                byte result = BYTE(((a << 1) | (oldCarry ? 1 : 0)));

                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RL A";
            }
        });

        // CB 0x18 - RR B
        cpu.setCBIns(0x18, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte b = cpu.getB();
                boolean oldCarry = cpu.isC();
                boolean newCarry = (b & 0x01) != 0;

                byte result = BYTE(((b >> 1) | (oldCarry ? 0x80 : 0)));

                cpu.setB(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RR B";
            }
        });

        // CB 0x19 - RR C
        cpu.setCBIns(0x19, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte c = cpu.getC();
                boolean oldCarry = cpu.isC();
                boolean newCarry = (c & 0x01) != 0;

                byte result = BYTE(((c >> 1) | (oldCarry ? 0x80 : 0)));

                cpu.setC(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RR C";
            }
        });

        // CB 0x1A - RR D
        cpu.setCBIns(0x1A, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte d = cpu.getD();
                boolean oldCarry = cpu.isC();
                boolean newCarry = (d & 0x01) != 0;

                byte result = BYTE(((d >> 1) | (oldCarry ? 0x80 : 0)));

                cpu.setD(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RR D";
            }
        });

        // CB 0x1B - RR E
        cpu.setCBIns(0x1B, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte e = cpu.getE();
                boolean oldCarry = cpu.isC();
                boolean newCarry = (e & 0x01) != 0;

                byte result = BYTE(((e >> 1) | (oldCarry ? 0x80 : 0)));

                cpu.setE(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RR E";
            }
        });

        // CB 0x1C - RR H
        cpu.setCBIns(0x1C, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte h = cpu.getH();
                boolean oldCarry = cpu.isC();
                boolean newCarry = (h & 0x01) != 0;

                byte result = BYTE(((h >> 1) | (oldCarry ? 0x80 : 0)));

                cpu.setH(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RR H";
            }
        });

        // CB 0x1D - RR L
        cpu.setCBIns(0x1D, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte l = cpu.getL();
                boolean oldCarry = cpu.isC();
                boolean newCarry = (l & 0x01) != 0;

                byte result = BYTE(((l >> 1) | (oldCarry ? 0x80 : 0)));

                cpu.setL(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RR L";
            }
        });

        // CB 0x1E - RR (HL)
        cpu.setCBIns(0x1E, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                short addr = cpu.getHL();
                byte v = cpu.read8(addr);
                boolean oldCarry = cpu.isC();
                boolean newCarry = (v & 0x01) != 0;

                byte result = BYTE(((v >> 1) | (oldCarry ? 0x80 : 0)));

                cpu.write8(addr, result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "RR (HL)";
            }
        });

        // CB 0x1F - RR A
        cpu.setCBIns(0x1F, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte a = cpu.getA();
                boolean oldCarry = cpu.isC();
                boolean newCarry = (a & 0x01) != 0;

                byte result = BYTE(((a >> 1) | (oldCarry ? 0x80 : 0)));

                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RR A";
            }
        });
        
        // CB 0x20 - SLA B
        cpu.setCBIns(0x20, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte b = cpu.getB();
                boolean newCarry = (b & 0x80) != 0;

                byte result = BYTE(b << 1);

                cpu.setB(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SLA B";
            }
        });

        // CB 0x21 - SLA C
        cpu.setCBIns(0x21, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte c = cpu.getC();
                boolean newCarry = (c & 0x80) != 0;

                byte result = BYTE(c << 1);

                cpu.setC(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SLA C";
            }
        });

        // CB 0x22 - SLA D
        cpu.setCBIns(0x22, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte d = cpu.getD();
                boolean newCarry = (d & 0x80) != 0;

                byte result = BYTE(d << 1);

                cpu.setD(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SLA D";
            }
        });

        // CB 0x23 - SLA E
        cpu.setCBIns(0x23, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte e = cpu.getE();
                boolean newCarry = (e & 0x80) != 0;

                byte result = BYTE(e << 1);

                cpu.setE(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SLA E";
            }
        });

        // CB 0x24 - SLA H
        cpu.setCBIns(0x24, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte h = cpu.getH();
                boolean newCarry = (h & 0x80) != 0;

                byte result = BYTE(h << 1);

                cpu.setH(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SLA H";
            }
        });

        // CB 0x25 - SLA L
        cpu.setCBIns(0x25, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte l = cpu.getL();
                boolean newCarry = (l & 0x80) != 0;

                byte result = BYTE(l << 1);

                cpu.setL(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SLA L";
            }
        });

        // CB 0x26 - SLA (HL)
        cpu.setCBIns(0x26, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                short addr = cpu.getHL();
                byte v = cpu.read8(addr);
                boolean newCarry = (v & 0x80) != 0;

                byte result = BYTE(v << 1);

                cpu.write8(addr, result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "SLA (HL)";
            }
        });

        // CB 0x27 - SLA A
        cpu.setCBIns(0x27, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte a = cpu.getA();
                boolean newCarry = (a & 0x80) != 0;

                byte result = BYTE(a << 1);

                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SLA A";
            }
        });

        // CB 0x28 - SRA B
        cpu.setCBIns(0x28, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte b = cpu.getB();
                boolean newCarry = (b & 0x01) != 0;
                int msb = b & 0x80;

                byte result = BYTE((b >> 1) | msb);

                cpu.setB(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SRA B";
            }
        });

        // CB 0x29 - SRA C
        cpu.setCBIns(0x29, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte c = cpu.getC();
                boolean newCarry = (c & 0x01) != 0;
                int msb = c & 0x80;

                byte result = BYTE((c >> 1) | msb);

                cpu.setC(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SRA C";
            }
        });

        // CB 0x2A - SRA D
        cpu.setCBIns(0x2A, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte d = cpu.getD();
                boolean newCarry = (d & 0x01) != 0;
                int msb = d & 0x80;

                byte result = BYTE((d >> 1) | msb);

                cpu.setD(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SRA D";
            }
        });

        // CB 0x2B - SRA E
        cpu.setCBIns(0x2B, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte e = cpu.getE();
                boolean newCarry = (e & 0x01) != 0;
                int msb = e & 0x80;

                byte result = BYTE((e >> 1) | msb);

                cpu.setE(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SRA E";
            }
        });

        // CB 0x2C - SRA H
        cpu.setCBIns(0x2C, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte h = cpu.getH();
                boolean newCarry = (h & 0x01) != 0;
                int msb = h & 0x80;

                byte result = BYTE((h >> 1) | msb);

                cpu.setH(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SRA H";
            }
        });

        // CB 0x2D - SRA L
        cpu.setCBIns(0x2D, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte l = cpu.getL();
                boolean newCarry = (l & 0x01) != 0;
                int msb = l & 0x80;

                byte result = BYTE((l >> 1) | msb);

                cpu.setL(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SRA L";
            }
        });

        // CB 0x2E - SRA (HL)
        cpu.setCBIns(0x2E, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                short addr = cpu.getHL();
                byte v = cpu.read8(addr);
                boolean newCarry = (v & 0x01) != 0;
                int msb = v & 0x80;

                byte result = BYTE((v >> 1) | msb);

                cpu.write8(addr, result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "SRA (HL)";
            }
        });

        // CB 0x2F - SRA A
        cpu.setCBIns(0x2F, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte a = cpu.getA();
                boolean newCarry = (a & 0x01) != 0;
                int msb = a & 0x80;

                byte result = BYTE((a >> 1) | msb);

                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SRA A";
            }
        });

        // CB 0x30 - SWAP B
        cpu.setCBIns(0x30, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte b = cpu.getB();
                byte result = BYTE(((b & 0x0F) << 4) | ((b & 0xF0) >> 4));

                cpu.setB(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SWAP B";
            }
        });

        // CB 0x31 - SWAP C
        cpu.setCBIns(0x31, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte c = cpu.getC();
                byte result = BYTE(((c & 0x0F) << 4) | ((c & 0xF0) >> 4));

                cpu.setC(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SWAP C";
            }
        });

        // CB 0x32 - SWAP D
        cpu.setCBIns(0x32, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte d = cpu.getD();
                byte result = BYTE(((d & 0x0F) << 4) | ((d & 0xF0) >> 4));

                cpu.setD(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SWAP D";
            }
        });

        // CB 0x33 - SWAP E
        cpu.setCBIns(0x33, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte e = cpu.getE();
                byte result = BYTE(((e & 0x0F) << 4) | ((e & 0xF0) >> 4));

                cpu.setE(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SWAP E";
            }
        });

        // CB 0x34 - SWAP H
        cpu.setCBIns(0x34, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte h = cpu.getH();
                byte result = BYTE(((h & 0x0F) << 4) | ((h & 0xF0) >> 4));

                cpu.setH(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SWAP H";
            }
        });

        // CB 0x35 - SWAP L
        cpu.setCBIns(0x35, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte l = cpu.getL();
                byte result = BYTE(((l & 0x0F) << 4) | ((l & 0xF0) >> 4));

                cpu.setL(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SWAP L";
            }
        });

        // CB 0x36 - SWAP (HL)
        cpu.setCBIns(0x36, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                short addr = cpu.getHL();
                byte v = cpu.read8(addr);

                byte result = BYTE(((v & 0x0F) << 4) | ((v & 0xF0) >> 4));

                cpu.write8(addr, result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "SWAP (HL)";
            }
        });

        // CB 0x37 - SWAP A
        cpu.setCBIns(0x37, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte a = cpu.getA();
                byte result = BYTE(((a & 0x0F) << 4) | ((a & 0xF0) >> 4));

                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(false);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SWAP A";
            }
        });

        // CB 0x38 - SRL B
        cpu.setCBIns(0x38, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte b = cpu.getB();
                boolean newCarry = (b & 0x01) != 0;

                byte result = BYTE(b >> 1);

                cpu.setB(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SRL B";
            }
        });

        // CB 0x39 - SRL C
        cpu.setCBIns(0x39, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte c = cpu.getC();
                boolean newCarry = (c & 0x01) != 0;

                byte result = BYTE(c >> 1);

                cpu.setC(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SRL C";
            }
        });

        // CB 0x3A - SRL D
        cpu.setCBIns(0x3A, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte d = cpu.getD();
                boolean newCarry = (d & 0x01) != 0;

                byte result = BYTE(d >> 1);

                cpu.setD(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SRL D";
            }
        });

        // CB 0x3B - SRL E
        cpu.setCBIns(0x3B, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte e = cpu.getE();
                boolean newCarry = (e & 0x01) != 0;

                byte result = BYTE(e >> 1);

                cpu.setE(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SRL E";
            }
        });

        // CB 0x3C - SRL H
        cpu.setCBIns(0x3C, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte h = cpu.getH();
                boolean newCarry = (h & 0x01) != 0;

                byte result = BYTE(h >> 1);

                cpu.setH(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SRL H";
            }
        });

        // CB 0x3D - SRL L
        cpu.setCBIns(0x3D, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte l = cpu.getL();
                boolean newCarry = (l & 0x01) != 0;

                byte result = BYTE(l >> 1);

                cpu.setL(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SRL L";
            }
        });

        // CB 0x3E - SRL (HL)
        cpu.setCBIns(0x3E, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                short addr = cpu.getHL();
                byte v = cpu.read8(addr);
                boolean newCarry = (v & 0x01) != 0;

                byte result = BYTE(v >> 1);

                cpu.write8(addr, result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "SRL (HL)";
            }
        });

        // CB 0x3F - SRL A
        cpu.setCBIns(0x3F, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte a = cpu.getA();
                boolean newCarry = (a & 0x01) != 0;

                byte result = BYTE(a >> 1);

                cpu.setA(result);

                cpu.updateZ(result == 0);
                cpu.updateN(false);
                cpu.updateH(false);
                cpu.updateC(newCarry);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "SRL A";
            }
        });
        
        // 0x40 - BIT 0, B
        cpu.setCBIns(0x40, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte b = cpu.getB();
                boolean zero = ((b >> 0) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 0, B";
            }
        });

        // 0x41 - BIT 0, C
        cpu.setCBIns(0x41, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte c = cpu.getC();
                boolean zero = ((c >> 0) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 0, C";
            }
        });

        // 0x42 - BIT 0, D
        cpu.setCBIns(0x42, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte d = cpu.getD();
                boolean zero = ((d >> 0) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 0, D";
            }
        });

        // 0x43 - BIT 0, E
        cpu.setCBIns(0x43, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte e = cpu.getE();
                boolean zero = ((e >> 0) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 0, E";
            }
        });

        // 0x44 - BIT 0, H
        cpu.setCBIns(0x44, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte h = cpu.getH();
                boolean zero = ((h >> 0) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 0, H";
            }
        });

        // 0x45 - BIT 0, L
        cpu.setCBIns(0x45, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte l = cpu.getL();
                boolean zero = ((l >> 0) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 0, L";
            }
        });

        // 0x46 - BIT 0, (HL)
        cpu.setCBIns(0x46, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int hl = cpu.getHL();
                byte v = cpu.read8(hl);
                boolean zero = ((v >> 0) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(12);
            }

            @Override
            public String name() {
                return "BIT 0, (HL)";
            }
        });

        // 0x47 - BIT 0, A
        cpu.setCBIns(0x47, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte a = cpu.getA();
                boolean zero = ((a >> 0) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 0, A";
            }
        });

        // 0x48 - BIT 1, B
        cpu.setCBIns(0x48, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte b = cpu.getB();
                boolean zero = ((b >> 1) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 1, B";
            }
        });

        // 0x49 - BIT 1, C
        cpu.setCBIns(0x49, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte c = cpu.getC();
                boolean zero = ((c >> 1) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 1, C";
            }
        });

        // 0x4A - BIT 1, D
        cpu.setCBIns(0x4A, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte d = cpu.getD();
                boolean zero = ((d >> 1) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 1, D";
            }
        });

        // 0x4B - BIT 1, E
        cpu.setCBIns(0x4B, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte e = cpu.getE();
                boolean zero = ((e >> 1) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 1, E";
            }
        });

        // 0x4C - BIT 1, H
        cpu.setCBIns(0x4C, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte h = cpu.getH();
                boolean zero = ((h >> 1) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 1, H";
            }
        });

        // 0x4D - BIT 1, L
        cpu.setCBIns(0x4D, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte l = cpu.getL();
                boolean zero = ((l >> 1) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 1, L";
            }
        });

        // 0x4E - BIT 1, (HL)
        cpu.setCBIns(0x4E, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int hl = cpu.getHL();
                byte v = cpu.read8(hl);
                boolean zero = ((v >> 1) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(12);
            }

            @Override
            public String name() {
                return "BIT 1, (HL)";
            }
        });

        // 0x4F - BIT 1, A
        cpu.setCBIns(0x4F, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte a = cpu.getA();
                boolean zero = ((a >> 1) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 1, A";
            }
        });

        // 0x50 - BIT 2, B
        cpu.setCBIns(0x50, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte b = cpu.getB();
                boolean zero = ((b >> 2) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 2, B";
            }
        });

        // 0x51 - BIT 2, C
        cpu.setCBIns(0x51, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte c = cpu.getC();
                boolean zero = ((c >> 2) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 2, C";
            }
        });

        // 0x52 - BIT 2, D
        cpu.setCBIns(0x52, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte d = cpu.getD();
                boolean zero = ((d >> 2) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 2, D";
            }
        });

        // 0x53 - BIT 2, E
        cpu.setCBIns(0x53, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte e = cpu.getE();
                boolean zero = ((e >> 2) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 2, E";
            }
        });

        // 0x54 - BIT 2, H
        cpu.setCBIns(0x54, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte h = cpu.getH();
                boolean zero = ((h >> 2) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 2, H";
            }
        });

        // 0x55 - BIT 2, L
        cpu.setCBIns(0x55, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte l = cpu.getL();
                boolean zero = ((l >> 2) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 2, L";
            }
        });

        // 0x56 - BIT 2, (HL)
        cpu.setCBIns(0x56, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int hl = cpu.getHL();
                byte v = cpu.read8(hl);
                boolean zero = ((v >> 2) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(12);
            }

            @Override
            public String name() {
                return "BIT 2, (HL)";
            }
        });

        // 0x57 - BIT 2, A
        cpu.setCBIns(0x57, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte a = cpu.getA();
                boolean zero = ((a >> 2) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 2, A";
            }
        });

        // 0x58 - BIT 3, B
        cpu.setCBIns(0x58, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte b = cpu.getB();
                boolean zero = ((b >> 3) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 3, B";
            }
        });

        // 0x59 - BIT 3, C
        cpu.setCBIns(0x59, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte c = cpu.getC();
                boolean zero = ((c >> 3) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 3, C";
            }
        });

        // 0x5A - BIT 3, D
        cpu.setCBIns(0x5A, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte d = cpu.getD();
                boolean zero = ((d >> 3) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 3, D";
            }
        });

        // 0x5B - BIT 3, E
        cpu.setCBIns(0x5B, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte e = cpu.getE();
                boolean zero = ((e >> 3) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 3, E";
            }
        });

        // 0x5C - BIT 3, H
        cpu.setCBIns(0x5C, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte h = cpu.getH();
                boolean zero = ((h >> 3) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 3, H";
            }
        });

        // 0x5D - BIT 3, L
        cpu.setCBIns(0x5D, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte l = cpu.getL();
                boolean zero = ((l >> 3) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 3, L";
            }
        });

        // 0x5E - BIT 3, (HL)
        cpu.setCBIns(0x5E, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int hl = cpu.getHL();
                byte v = cpu.read8(hl);
                boolean zero = ((v >> 3) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(12);
            }

            @Override
            public String name() {
                return "BIT 3, (HL)";
            }
        });

        // 0x5F - BIT 3, A
        cpu.setCBIns(0x5F, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte a = cpu.getA();
                boolean zero = ((a >> 3) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 3, A";
            }
        });
        
        // 0x60 - BIT 4, B
        cpu.setCBIns(0x60, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte b = cpu.getB();
                boolean zero = ((b >> 4) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 4, B";
            }
        });

        // 0x61 - BIT 4, C
        cpu.setCBIns(0x61, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte c = cpu.getC();
                boolean zero = ((c >> 4) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 4, C";
            }
        });

        // 0x62 - BIT 4, D
        cpu.setCBIns(0x62, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte d = cpu.getD();
                boolean zero = ((d >> 4) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 4, D";
            }
        });

        // 0x63 - BIT 4, E
        cpu.setCBIns(0x63, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte e = cpu.getE();
                boolean zero = ((e >> 4) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 4, E";
            }
        });

        // 0x64 - BIT 4, H
        cpu.setCBIns(0x64, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte h = cpu.getH();
                boolean zero = ((h >> 4) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 4, H";
            }
        });

        // 0x65 - BIT 4, L
        cpu.setCBIns(0x65, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte l = cpu.getL();
                boolean zero = ((l >> 4) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 4, L";
            }
        });

        // 0x66 - BIT 4, (HL)
        cpu.setCBIns(0x66, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int hl = cpu.getHL();
                byte v = cpu.read8(hl);
                boolean zero = ((v >> 4) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(12);
            }

            @Override
            public String name() {
                return "BIT 4, (HL)";
            }
        });

        // 0x67 - BIT 4, A
        cpu.setCBIns(0x67, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte a = cpu.getA();
                boolean zero = ((a >> 4) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 4, A";
            }
        });

        // 0x68 - BIT 5, B
        cpu.setCBIns(0x68, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte b = cpu.getB();
                boolean zero = ((b >> 5) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 5, B";
            }
        });

        // 0x69 - BIT 5, C
        cpu.setCBIns(0x69, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte c = cpu.getC();
                boolean zero = ((c >> 5) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 5, C";
            }
        });

        // 0x6A - BIT 5, D
        cpu.setCBIns(0x6A, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte d = cpu.getD();
                boolean zero = ((d >> 5) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 5, D";
            }
        });

        // 0x6B - BIT 5, E
        cpu.setCBIns(0x6B, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte e = cpu.getE();
                boolean zero = ((e >> 5) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 5, E";
            }
        });

        // 0x6C - BIT 5, H
        cpu.setCBIns(0x6C, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte h = cpu.getH();
                boolean zero = ((h >> 5) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 5, H";
            }
        });

        // 0x6D - BIT 5, L
        cpu.setCBIns(0x6D, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte l = cpu.getL();
                boolean zero = ((l >> 5) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 5, L";
            }
        });

        // 0x6E - BIT 5, (HL)
        cpu.setCBIns(0x6E, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int hl = cpu.getHL();
                byte v = cpu.read8(hl);
                boolean zero = ((v >> 5) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(12);
            }

            @Override
            public String name() {
                return "BIT 5, (HL)";
            }
        });

        // 0x6F - BIT 5, A
        cpu.setCBIns(0x6F, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte a = cpu.getA();
                boolean zero = ((a >> 5) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 5, A";
            }
        });

        // 0x70 - BIT 6, B
        cpu.setCBIns(0x70, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte b = cpu.getB();
                boolean zero = ((b >> 6) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 6, B";
            }
        });

        // 0x71 - BIT 6, C
        cpu.setCBIns(0x71, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte c = cpu.getC();
                boolean zero = ((c >> 6) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 6, C";
            }
        });

        // 0x72 - BIT 6, D
        cpu.setCBIns(0x72, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte d = cpu.getD();
                boolean zero = ((d >> 6) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 6, D";
            }
        });

        // 0x73 - BIT 6, E
        cpu.setCBIns(0x73, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte e = cpu.getE();
                boolean zero = ((e >> 6) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 6, E";
            }
        });

        // 0x74 - BIT 6, H
        cpu.setCBIns(0x74, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte h = cpu.getH();
                boolean zero = ((h >> 6) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 6, H";
            }
        });

        // 0x75 - BIT 6, L
        cpu.setCBIns(0x75, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte l = cpu.getL();
                boolean zero = ((l >> 6) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 6, L";
            }
        });

        // 0x76 - BIT 6, (HL)
        cpu.setCBIns(0x76, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int hl = cpu.getHL();
                byte v = cpu.read8(hl);
                boolean zero = ((v >> 6) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(12);
            }

            @Override
            public String name() {
                return "BIT 6, (HL)";
            }
        });

        // 0x77 - BIT 6, A
        cpu.setCBIns(0x77, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte a = cpu.getA();
                boolean zero = ((a >> 6) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 6, A";
            }
        });

        // 0x78 - BIT 7, B
        cpu.setCBIns(0x78, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte b = cpu.getB();
                boolean zero = ((b >> 7) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 7, B";
            }
        });

        // 0x79 - BIT 7, C
        cpu.setCBIns(0x79, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte c = cpu.getC();
                boolean zero = ((c >> 7) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 7, C";
            }
        });

        // 0x7A - BIT 7, D
        cpu.setCBIns(0x7A, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte d = cpu.getD();
                boolean zero = ((d >> 7) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 7, D";
            }
        });

        // 0x7B - BIT 7, E
        cpu.setCBIns(0x7B, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte e = cpu.getE();
                boolean zero = ((e >> 7) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 7, E";
            }
        });

        // 0x7C - BIT 7, H
        cpu.setCBIns(0x7C, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte h = cpu.getH();
                boolean zero = ((h >> 7) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 7, H";
            }
        });

        // 0x7D - BIT 7, L
        cpu.setCBIns(0x7D, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte l = cpu.getL();
                boolean zero = ((l >> 7) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 7, L";
            }
        });

        // 0x7E - BIT 7, (HL)
        cpu.setCBIns(0x7E, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int hl = cpu.getHL();
                byte v = cpu.read8(hl);
                boolean zero = ((v >> 7) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(12);
            }

            @Override
            public String name() {
                return "BIT 7, (HL)";
            }
        });

        // 0x7F - BIT 7, A
        cpu.setCBIns(0x7F, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte a = cpu.getA();
                boolean zero = ((a >> 7) & 1) == 0;

                cpu.updateZ(zero);
                cpu.updateN(false);
                cpu.updateH(true);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 7, A";
            }
        });
        
        // 0x80 - RES 0, B
        cpu.setCBIns(0x80, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte b = cpu.getB();
                b = (byte)(b & ~(1 << 0));
                cpu.setB(b);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RES 0, B";
            }
        });

        // 0x81 - RES 0, C
        cpu.setCBIns(0x81, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte c = cpu.getC();
                c = (byte)(c & ~(1 << 0));
                cpu.setC(c);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RES 0, C";
            }
        });

        // 0x82 - RES 0, D
        cpu.setCBIns(0x82, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte d = cpu.getD();
                d = (byte)(d & ~(1 << 0));
                cpu.setD(d);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RES 0, D";
            }
        });

        // 0x83 - RES 0, E
        cpu.setCBIns(0x83, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte e = cpu.getE();
                e = (byte)(e & ~(1 << 0));
                cpu.setE(e);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RES 0, E";
            }
        });

        // 0x84 - RES 0, H
        cpu.setCBIns(0x84, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte h = cpu.getH();
                h = (byte)(h & ~(1 << 0));
                cpu.setH(h);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RES 0, H";
            }
        });

        // 0x85 - RES 0, L
        cpu.setCBIns(0x85, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte l = cpu.getL();
                l = (byte)(l & ~(1 << 0));
                cpu.setL(l);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RES 0, L";
            }
        });

        // 0x86 - RES 0, (HL)
        cpu.setCBIns(0x86, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int hl = cpu.getHL();
                byte v = cpu.read8(hl);
                v = (byte)(v & ~(1 << 0));
                cpu.write8(hl, v);

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "RES 0, (HL)";
            }
        });

        // 0x87 - RES 0, A
        cpu.setCBIns(0x87, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte a = cpu.getA();
                a = (byte)(a & ~(1 << 0));
                cpu.setA(a);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RES 0, A";
            }
        });

        // 0x88 - RES 1, B
        cpu.setCBIns(0x88, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte b = cpu.getB();
                b = (byte)(b & ~(1 << 1));
                cpu.setB(b);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RES 1, B";
            }
        });

        // 0x89 - RES 1, C
        cpu.setCBIns(0x89, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte c = cpu.getC();
                c = (byte)(c & ~(1 << 1));
                cpu.setC(c);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RES 1, C";
            }
        });

        // 0x8A - RES 1, D
        cpu.setCBIns(0x8A, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte d = cpu.getD();
                d = (byte)(d & ~(1 << 1));
                cpu.setD(d);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RES 1, D";
            }
        });

        // 0x8B - RES 1, E
        cpu.setCBIns(0x8B, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte e = cpu.getE();
                e = (byte)(e & ~(1 << 1));
                cpu.setE(e);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RES 1, E";
            }
        });

        // 0x8C - RES 1, H
        cpu.setCBIns(0x8C, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte h = cpu.getH();
                h = (byte)(h & ~(1 << 1));
                cpu.setH(h);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RES 1, H";
            }
        });

        // 0x8D - RES 1, L
        cpu.setCBIns(0x8D, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte l = cpu.getL();
                l = (byte)(l & ~(1 << 1));
                cpu.setL(l);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RES 1, L";
            }
        });

        // 0x8E - RES 1, (HL)
        cpu.setCBIns(0x8E, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                int hl = cpu.getHL();
                byte v = cpu.read8(hl);
                v = (byte)(v & ~(1 << 1));
                cpu.write8(hl, v);

                cpu.incCycles(16);
            }

            @Override
            public String name() {
                return "RES 1, (HL)";
            }
        });

        // 0x8F - RES 1, A
        cpu.setCBIns(0x8F, new Instruction() {
            @Override
            public void execute(Cpu cpu) {
                byte a = cpu.getA();
                a = (byte)(a & ~(1 << 1));
                cpu.setA(a);

                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RES 1, A";
            }
        });



        // TODO: MORE!!!!!!!!!!!!!!!!!!!!!!1
    }
}