package httpnetty.http.handlers;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;


import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.router.RouteResult;
import io.netty.util.CharsetUtil;

import java.util.List;

import httpnetty.server.HttpHandler;
import httpnetty.statistics.StatService;

import static io.netty.handler.codec.http.HttpHeaders.Names.LOCATION;
import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by Viktor on 29.12.2015.
 */
public class RedirectHandler implements HttpHandler {


    @Override
    public void processRequest(ChannelHandlerContext ctx, HttpRequest request, RouteResult<HttpHandler> routeResult) throws Exception {

        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
        List<String> list = decoder.parameters().get("url");
        String targetUri = list.get(0);

        if (targetUri == null)
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
        else {
            System.out.println("target uri: " + targetUri);
            sendRedirect(ctx, targetUri);
        }
    }

    private static void sendRedirect(ChannelHandlerContext ctx, String newUri) {
        StatService.addRedirect(newUri);
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
        response.headers().set(LOCATION, newUri);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}

