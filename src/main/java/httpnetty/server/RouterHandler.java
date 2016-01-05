package httpnetty.server;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.router.RouteResult;
import io.netty.handler.codec.http.router.Router;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import httpnetty.statistics.PerChannelStatisticHandler;
import httpnetty.statistics.StatService;

@ChannelHandler.Sharable
public class RouterHandler extends ChannelInboundHandlerAdapter {

    private final Router<HttpHandler> router;
    private StatService statService;
    private PerChannelStatisticHandler perChannelStatistic;

    public RouterHandler(Router<HttpHandler> router, StatService statistics, PerChannelStatisticHandler perChannelStatistic) {
        this.router = router;
        this.statService = statistics;
        this.perChannelStatistic = perChannelStatistic;
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        statService.currentlyOpenedConnections.decrementAndGet();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;

            if (HttpHeaders.is100ContinueExpected(request)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }

            RouteResult<HttpHandler> routeResult = router.route(request.getMethod(), request.getUri());
            String uri = request.getUri();
            perChannelStatistic.setUri(uri);

            routeResult.target().processRequest(ctx, request, routeResult);

        }

    }



}


