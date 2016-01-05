package httpnetty.server;

import httpnetty.statistics.PerChannelStatisticHandler;
import httpnetty.statistics.StatService;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.router.Router;


public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    private Router<HttpHandler> router;

    public ServerInitializer(Router<HttpHandler> router) {
        this.router = router;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        StatService statService = new StatService();
        long startTime = System.currentTimeMillis();

        statService.currentlyOpenedConnections.incrementAndGet();
        String ip = ch.remoteAddress().getHostString();

        PerChannelStatisticHandler requestStatsHandler = new PerChannelStatisticHandler(0, statService);

        requestStatsHandler.setIp(ip);
        requestStatsHandler.setStartTime(startTime);

        ch.pipeline()
                .addLast(requestStatsHandler)
                .addLast(new HttpServerCodec())
                .addLast(new RouterHandler(router, statService, requestStatsHandler));
    }


}
