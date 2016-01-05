package httpnetty.server;

import httpnetty.http.handlers.HelloWorldHandler;
import httpnetty.http.handlers.NotFoundHandler;
import httpnetty.http.handlers.RedirectHandler;
import httpnetty.http.handlers.StatusHandler;
import httpnetty.statistics.StatService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.router.Router;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;


public final class Server {

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }

        @SuppressWarnings("unchecked")
        Router<HttpHandler> router = new Router<HttpHandler>()
                .GET("/hello", new HelloWorldHandler())
                .GET("/redirect", new RedirectHandler())
                .GET("/status", new StatusHandler())
                .notFound(new NotFoundHandler());

        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                              .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ServerInitializer(router));

            Channel ch = b.bind(port).sync().channel();

            System.err.println("Open your web browser and navigate to " +
                    "http" + "://127.0.0.1:" + port + '/' + "hello");

            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}