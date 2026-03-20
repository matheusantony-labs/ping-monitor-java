import java.time.LocalDateTime;

public class HostStatus {

    private MonitoringStatus currentStatus;
    private MonitoringStatus previousStatus;
    private LocalDateTime lastStatusChange;
    private int consecutiveFailures;

    public MonitoringStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(MonitoringStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public MonitoringStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(MonitoringStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public LocalDateTime getLastStatusChange() {
        return lastStatusChange;
    }

    public void setLastStatusChange(LocalDateTime lastStatusChange) {
        this.lastStatusChange = lastStatusChange;
    }

    public int getConsecutiveFailures() {
        return consecutiveFailures;
    }

    public void resetFailures() {
        consecutiveFailures = 0;
    }

    public void incrementFailures() {
        consecutiveFailures++;
    }
}
