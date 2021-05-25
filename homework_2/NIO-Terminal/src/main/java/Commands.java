import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class Commands {
    private static Map<String, Command> commandMap = new HashMap<>();
    static {
        commandMap.put("help", new Command() {
            @Override
            public String execute(String... args) {
                return "\r\nls -> list directories and files." +
                        "\r\ncat filename -> show file content." +
                        "\r\nhelp -> list of available commands." +
                        "\r\n";
            }
        });
        commandMap.put("ls", new Command() {
            @Override
            public String execute(String... args) {
                String result = null;
                try {
                    result = Files.walk(Paths.get("Path-Files"))
                            .map(path -> {
                                return Files.isDirectory(path) ? path.toString() + "\\" : path.toString();
                            })
                            .collect(Collectors.joining("\r\n")) + "\r\n";
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
                return result;
            }
        });
        commandMap.put("cat", new Command() {
            @Override
            public String execute(String... args) {
                String result = null;
                if (args.length == 2) {
                    try {
                        result = new String(Files.readAllBytes(Paths.get("Path-Files/" + args[1])), StandardCharsets.UTF_8) + "\r\n";
                        result = String.format("content of %s: \r\n%s\r\n", args[1], result);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                } else {
                    result = "invalid parameters\r\n";
                }
                return result;
            }
        });
    }
    public static String execute(String cmd){
        String[] args = cmd.trim().toLowerCase().split(" ");
        if (args[0].isEmpty()) {
            return null;
        }
        Command c = commandMap.get(args[0]);
        return c != null ? c.execute(args) : "invalid command: " + cmd;
    }
}
