import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Command-line entry point: java -jar DWASearch.jar input.txt output.txt */
public final class DWASearch {
    private DWASearch() { }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar DWASearch.jar input.txt output.txt");
            System.exit(2);
        }
        try {
            List<String> commands = Files.readAllLines(Path.of(args[0]), StandardCharsets.UTF_8);
            if (!commands.isEmpty() && commands.get(0).startsWith("\uFEFF")) {
                commands.set(0, commands.get(0).substring(1));
            }
            CoreModule core = CoreModule.fromBundledData();
            Files.writeString(Path.of(args[1]), execute(commands, core), StandardCharsets.UTF_8);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("DWASearch: " + e.getMessage());
            System.exit(1);
        }
    }

    static String execute(List<String> commands, CoreModule core) {
        StringBuilder output = new StringBuilder();
        for (String command : commands) output.append(executeOne(command, core));
        return output.toString();
    }

    static String executeOne(String command, CoreModule core) {
        if (command.equals("players")) return core.displayAllPlayersInfo();
        if (!command.equals("result") && !command.startsWith("result ")) {
            return Lib.block("Error");
        }
        String event = command.equals("result") ? "" : command.substring("result ".length());
        boolean detail = event.endsWith(" detail");
        if (detail) event = event.substring(0, event.length() - " detail".length());
        if (!core.supportsEvent(event)) return Lib.block("N/A");
        return core.displayResultsForEachEvent(event, detail);
    }
}
