package httpnetty.http.handlers;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import httpnetty.server.HttpHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.router.RouteResult;
import io.netty.util.CharsetUtil;


public class NotFoundHandler implements HttpHandler {

    @Override
    public void processRequest(ChannelHandlerContext ctx, HttpRequest reqest, RouteResult<HttpHandler> routeResult) throws Exception {
        sendError(ctx, HttpResponseStatus.NOT_FOUND);
    }

    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}

