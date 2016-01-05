package httpnetty.statistics;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;

public class PerChannelStatisticHandler extends ChannelTrafficShapingHandler {

    private StatService statService;
    private String ip;
    private String uri;
    private long startTime;

    public PerChannelStatisticHandler(long checkInterval, StatService statistics) {
        super(checkInterval);
        this.statService = statistics;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        long bytesSent = trafficCounter.cumulativeWrittenBytes();
        long bytesReceived = trafficCounter.cumulativeReadBytes();
        long elapsed = System.currentTimeMillis() - trafficCounter.lastCumulativeTime();
        if (elapsed == 0) elapsed = 1;
        long speed = (bytesSent * 1000l + bytesReceived * 1000l )/ elapsed;

        statService.addDataForProcessing(ip, uri, startTime, bytesSent, bytesReceived, speed);
        statService.addDataToRequestsPerIp();
        statService.addDataToProcessedRequestsStats();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        trafficCounter.resetCumulativeTime();
        super.channelRead(ctx, msg);
    }


    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
