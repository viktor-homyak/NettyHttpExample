package httpnetty.http.handlers;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import httpnetty.server.HttpHandler;
import httpnetty.statistics.StatService;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.router.RouteResult;


public class StatusHandler implements HttpHandler {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONNECTION = "Connection";
    private static final String KEEP_ALIVE = "keep-alive";

    MustacheFactory mf = new DefaultMustacheFactory();
    Mustache mustache = mf.compile("stats.mustache");

    @Override
    public void processRequest(ChannelHandlerContext ctx, HttpRequest request, RouteResult<HttpHandler> routeResult) throws Exception {

        
        String html = generateHTML();
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(html.getBytes()));
        response.headers().set(CONTENT_TYPE, "text/html");
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

            response.headers().set(CONNECTION, KEEP_ALIVE);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private String generateHTML() {
        Writer writer = new StringWriter();

        Map<String, Object> context = new HashMap<>();
        context.put("totalRequests", StatService.totalRequestCount);
        context.put("openConnections", StatService.currentlyOpenedConnections);
        context.put("uniqueRequests", StatService.getQueriesNumberByIp());
        context.put("requests", StatService.getRequestsPerIp());
        context.put("redirects", StatService.getRedirects());
        context.put("connections", StatService.getProcessedRequestsStats());

        try {
            mustache.execute(writer, context).flush();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return writer.toString();
    }
}



