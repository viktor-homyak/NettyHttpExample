package httpnetty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.router.RouteResult;

public interface HttpHandler {
  public void processRequest(ChannelHandlerContext ctx, HttpRequest request, RouteResult<HttpHandler> routeResult)
      throws Exception;
}
