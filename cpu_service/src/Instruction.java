public interface Instruction {
    void execute(Cpu cpu) throws Exception;

    default String name() {
        return "UNKNOWN";
    }
}