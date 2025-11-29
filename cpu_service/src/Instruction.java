public interface Instruction {
    void execute(Cpu cpu) throws Exception;

    default int cycles() {
        return 4; 
    }

    default String name() {
        return "UNKNOWN";
    }
}