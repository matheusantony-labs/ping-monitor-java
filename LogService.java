import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Path logFile;

    public LogService(Path logFile) {
        this.logFile = logFile;
    }

    public void logDown(HostEntry host, LocalDateTime timestamp) {
        writeLine(formatLine(timestamp, host.displayName(), "DOWN"));
    }

    public void logUp(HostEntry host, LocalDateTime timestamp, long downtimeSeconds) {
        writeLine(formatLine(timestamp, host.displayName(), "UP") + " | downtime=" + downtimeSeconds + "s");
    }

    public void printCheck(HostEntry host, LocalDateTime timestamp, String statusDetail) {
        System.out.println(formatLine(timestamp, host.displayName(), statusDetail));
    }

    private String formatLine(LocalDateTime timestamp, String host, String status) {
        return FORMATTER.format(timestamp) + " | " + host + " | " + status;
    }

    private void writeLine(String line) {
        System.out.println(line);
        try {
            Files.writeString(
                    logFile,
                    line + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Falha ao gravar log em " + logFile + ": " + e.getMessage());
        }
    }
}
