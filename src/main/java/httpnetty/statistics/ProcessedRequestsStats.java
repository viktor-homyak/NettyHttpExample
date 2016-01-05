package httpnetty.statistics;

import java.util.Date;


public class ProcessedRequestsStats {

    private String ip;
    private String uri;
    private Date StartTime;
    private long bytesSent;
    private long bytesReceived;
    private long speed;

    public ProcessedRequestsStats(String ip, String uri, Date StartTime, long bytesSent, long bytesReceived, long speed) {
        this.ip = ip;
        this.uri = uri;
        this.StartTime = StartTime;
        this.bytesSent = bytesSent;
        this.bytesReceived = bytesReceived;
        this.speed = speed;
    }

    public String getIp() {
        return ip;
    }

    public String getUri() {
        return uri;
    }

    public Date getStartTime() {
        return StartTime;
    }

    public long getBytesSent() {
        return bytesSent;
    }

    public long getBytesReceived() {
        return bytesReceived;
    }
    public long getSpeed() {
        return speed;
    }
}
