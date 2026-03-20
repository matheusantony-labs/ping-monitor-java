import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HostMonitorService {

    private final int timeout;
    private final int failureThreshold;
    private final LogService logService;

    public HostMonitorService(int timeout, int failureThreshold, LogService logService) {
        this.timeout = timeout;
        this.failureThreshold = failureThreshold;
        this.logService = logService;
    }

    public Map<String, HostStatus> initializeStatuses(List<HostEntry> hosts) {
        Map<String, HostStatus> statuses = new LinkedHashMap<>();
        for (HostEntry host : hosts) {
            statuses.put(host.address(), new HostStatus());
        }
        return statuses;
    }

    public void checkHost(HostEntry host, HostStatus hostStatus) {
        LocalDateTime now = LocalDateTime.now();
        boolean reachable = isReachable(host.address());

        if (reachable) {
            handleSuccess(host, hostStatus, now);
            return;
        }

        handleFailure(host, hostStatus, now);
    }

    private boolean isReachable(String address) {
        try {
            InetAddress inet = InetAddress.getByName(address);
            return inet.isReachable(timeout);
        } catch (IOException e) {
            return false;
        }
    }

    private void handleSuccess(HostEntry host, HostStatus hostStatus, LocalDateTime now) {
        hostStatus.resetFailures();

        if (hostStatus.getCurrentStatus() == null) {
            updateStatus(hostStatus, MonitoringStatus.ONLINE, now);
            logService.printCheck(host, now, "ONLINE");
            return;
        }

        if (hostStatus.getCurrentStatus() == MonitoringStatus.OFFLINE) {
            long downtimeSeconds = Duration.between(hostStatus.getLastStatusChange(), now).getSeconds();
            updateStatus(hostStatus, MonitoringStatus.ONLINE, now);
            logService.logUp(host, now, downtimeSeconds);
            logService.printCheck(host, now, "ONLINE");
            return;
        }

        logService.printCheck(host, now, "ONLINE");
    }

    private void handleFailure(HostEntry host, HostStatus hostStatus, LocalDateTime now) {
        hostStatus.incrementFailures();

        if (hostStatus.getConsecutiveFailures() < failureThreshold) {
            logService.printCheck(host, now, "FAILURE " + hostStatus.getConsecutiveFailures() + "/" + failureThreshold);
            return;
        }

        if (hostStatus.getCurrentStatus() != MonitoringStatus.OFFLINE) {
            updateStatus(hostStatus, MonitoringStatus.OFFLINE, now);
            logService.logDown(host, now);
            logService.printCheck(host, now, "OFFLINE");
            return;
        }

        logService.printCheck(host, now, "OFFLINE");
    }

    private void updateStatus(HostStatus hostStatus, MonitoringStatus newStatus, LocalDateTime now) {
        hostStatus.setPreviousStatus(hostStatus.getCurrentStatus());
        hostStatus.setCurrentStatus(newStatus);
        hostStatus.setLastStatusChange(now);
    }
}
