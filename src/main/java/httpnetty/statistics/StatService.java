package httpnetty.statistics;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class StatService {

    private String ip;
    private String uri;
    private long startTime;
    private long bytesSent;
    private long bytesReceived;
    private long speed;

    public static final AtomicInteger currentlyOpenedConnections = new AtomicInteger();
    public static final AtomicInteger totalRequestCount = new AtomicInteger();

    static final List<RequestsPerIp> requestsPerIp = Collections.synchronizedList(new LinkedList<>());
    static final List<ProcessedRequestsStats> processedRequestsStats = Collections.synchronizedList(new LinkedList<>());
    static final Map<String, Integer> redirects = Collections.synchronizedMap(new HashMap<>());


    public void addDataForProcessing(String ip, String uri, long startTime, long bytesSent, long bytesReceived, long speed) {
        this.ip = ip;
        this.uri = uri;
        this.startTime = startTime;
        this.bytesSent = bytesSent;
        this.bytesReceived = bytesReceived;
        this.speed = speed;

    }


    public void addDataToRequestsPerIp() {
        totalRequestCount.incrementAndGet();
        synchronized (requestsPerIp) {
            for (RequestsPerIp r : requestsPerIp) {
                if (r.getIp().equals(ip)) {
                    r.setNRequests(r.getNRequests() + 1l);
                    r.setRequestStartTime(new Date(startTime));
                    return;
                }
            }

            requestsPerIp.add(new RequestsPerIp(ip, 1, new Date(startTime)));
        }
    }

    public void addDataToProcessedRequestsStats() {
        synchronized (processedRequestsStats) {
            processedRequestsStats.add(new ProcessedRequestsStats(ip, uri, new Date(startTime), bytesSent, bytesReceived, speed));

            while (processedRequestsStats.size() > 16) {

                processedRequestsStats.remove(0);
            }
        }
    }


    public synchronized static void addRedirect(String url) {
        Integer found = redirects.get(url);
        if (found == null) {
            redirects.put(url, 1);
        } else {
            redirects.put(url, found + 1);
        }
    }


    public static Iterable<Iterable> getRedirects() {
        LinkedList<Iterable> answer = new LinkedList<>();

        for (Map.Entry<String, Integer> redirect : redirects.entrySet()) {
            LinkedList<Object> subanswer = new LinkedList<>();
            subanswer.add(redirect.getKey());
            subanswer.add(redirect.getValue());
            answer.add(subanswer);
        }
        return answer;
    }

    public static List<RequestsPerIp> getRequestsPerIp() {
        return requestsPerIp;
    }


    public static int getQueriesNumberByIp() {
        return requestsPerIp.size();
    }

    public static List<ProcessedRequestsStats> getProcessedRequestsStats() {
        return processedRequestsStats;
    }

    public long getBytesSent() {
        return bytesSent;
    }

    public long getBytesReceived() {
        return bytesReceived;
    }

    public long getLastWriteThroughput() {
        return speed;
    }


}



