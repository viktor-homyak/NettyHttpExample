package httpnetty.statistics;

import java.util.Date;


public class RequestsPerIp {

    private String ip;
    private long nRequests;
    private Date requestStartTime;

    public RequestsPerIp(String ip, int nRequests, Date requestStartTime) {
        this.ip = ip;
        this.nRequests = nRequests;
        this.requestStartTime = requestStartTime;
    }

    public String getIp() {
        return ip;
    }

    public void setNRequests(long nRequests) {
        this.nRequests = nRequests;
    }

    public void setRequestStartTime(Date requestStartTime) {
        this.requestStartTime = requestStartTime;
    }

    public long getNRequests() {
        return nRequests;
    }

    public Date getRequestStartTime() {
        return requestStartTime;
    }

}