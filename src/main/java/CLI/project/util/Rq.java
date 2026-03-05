package CLI.project.util;

public class Rq {
    private String command;
    private String[] arguments;

    public Rq(String input) {
        // TODO: 명령어 파싱 로직 구현 (split, trim 등)
    }

    public String getCommand() {
        return command != null ? command : "";
    }

    public String[] getArguments() {
        return arguments;
    }
}