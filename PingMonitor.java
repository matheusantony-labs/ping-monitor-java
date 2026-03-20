import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PingMonitor {

    private static final int TIMEOUT = 3000;
    private static final int INTERVAL_MS = 5000;
    private static final int FAILURE_THRESHOLD = 2;
    private static final Path HOSTS_FILE = Path.of("hosts.txt");
    private static final Path LOG_FILE = Path.of("ping-monitor.log");

    public static void main(String[] args) {
        List<HostEntry> hosts = loadHosts();

        if (hosts.isEmpty()) {
            System.out.println("Nenhum host encontrado em " + HOSTS_FILE + ".");
            return;
        }

        LogService logService = new LogService(LOG_FILE);
        HostMonitorService monitorService = new HostMonitorService(TIMEOUT, FAILURE_THRESHOLD, logService);
        Map<String, HostStatus> hostStatuses = monitorService.initializeStatuses(hosts);

        System.out.println("Monitorando " + hosts.size() + " host(s) de " + HOSTS_FILE + ".");
        System.out.println("Logs de eventos em " + LOG_FILE + ".");

        while (true) {
            for (HostEntry host : hosts) {
                monitorService.checkHost(host, hostStatuses.get(host.address()));
            }

            try {
                Thread.sleep(INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Monitoramento interrompido.");
                break;
            }
        }
    }

    private static List<HostEntry> loadHosts() {
        try {
            return Files.readAllLines(HOSTS_FILE).stream()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .filter(line -> !line.startsWith("#"))
                    .map(PingMonitor::parseHostLine)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Nao foi possivel ler " + HOSTS_FILE + ": " + e.getMessage());
            return List.of();
        }
    }

    private static HostEntry parseHostLine(String line) {
        String[] parts = line.split(";", 2);
        if (parts.length == 2) {
            String name = parts[0].trim();
            String address = parts[1].trim();
            if (!name.isEmpty() && !address.isEmpty()) {
                return new HostEntry(name, address);
            }
        }

        return new HostEntry(line, line);
    }
}
