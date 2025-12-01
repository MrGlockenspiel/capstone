public class InstructionDecoder {

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
                    cpu.setPC((short)(cpu.getPC() + (byte)offset));
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



        // 0xAF - XOR A, A
        cpu.setIns(0xAF, new Instruction() {
            @Override
            public void execute(Cpu cpu) throws Exception {
                cpu.setA((byte)((cpu.getA() ^ cpu.getA()) & 0xFF));
                
                cpu.setZ();
                cpu.incCycles(4);
                
            }

            @Override
            public String name() {
                return "JR NZ, r8";
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
                
                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "RL C";
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
                
                cpu.incCycles(8);
            }

            @Override
            public String name() {
                return "BIT 7, H";
            }
        });

        // TODO: MORE!!!!!!!!!!!!!!!!!!!!!!1
    }
}