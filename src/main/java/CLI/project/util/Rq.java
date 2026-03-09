package CLI.project.util;

public class Rq {
    private String command;
    private String[] arguments;

    public Rq(String input) {
        if (input == null || input.isBlank()) {
            this.command = "";
            this.arguments = new String[0];
            return;
        }
        String[] parts = input.trim().split("\\s+");
        this.command = parts[0];
        if (parts.length == 1) {
            this.arguments = new String[0];
        } else {
            this.arguments = new String[parts.length - 1];
            System.arraycopy(parts, 1, this.arguments, 0, this.arguments.length);
        }
    }

    public String getCommand() {
        return command != null ? command : "";
    }

    public String[] getArguments() {
        return arguments;
    }
}