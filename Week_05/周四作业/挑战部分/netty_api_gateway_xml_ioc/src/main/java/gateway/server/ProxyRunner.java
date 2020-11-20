package gateway.server;

import gateway.router.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.Set;

public class ProxyRunner {
    private final int local_port;

    public ProxyRunner(int local_port) {
        this.local_port = local_port;
    }

    public void run(ApplicationContext appContext, ProxyRouter.ProxyRouterType routerType, Map<String, Set<String>> server2uris, Map<String, Integer> server2weight, boolean debug) {
        EventLoopGroup bossGrp = new NioEventLoopGroup(1);
        EventLoopGroup workerGrp = new NioEventLoopGroup(16);

        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.SO_RCVBUF, 32 * 1024)
                    .option(ChannelOption.SO_SNDBUF, 32 * 1024)
                    .option(EpollChannelOption.SO_REUSEPORT, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            // 构造Router
            ProxyRouter router = null;
            if (routerType == ProxyRouter.ProxyRouterType.WEIGHT) {
                WeightBasedRouter weightRouter = (WeightBasedRouter)appContext.getBean("weightRouter");

                if (server2weight != null) {
                    for (Map.Entry<String, Integer> entry : server2weight.entrySet()) {
                        weightRouter.registerServerWeight(entry.getKey(), entry.getValue());
                    }
                }
                router = debug ? new PrintDispatchInfoWrapper(weightRouter) : weightRouter;

            } else {
                RandomRouter randomRouter = (RandomRouter) appContext.getBean("randomRouter");
                router = debug ? new PrintDispatchInfoWrapper(randomRouter) : randomRouter;
            }

            for (Map.Entry<String, Set<String>> entry : server2uris.entrySet()) {
                String server = entry.getKey();
                Set<String> uris = entry.getValue();

                for (String uri : uris) {
                    router.registerMapInfo(server, uri);
                }
            }

            ProxyReqHandler handler = new ProxyReqHandler(router, appContext);
            sb.group(bossGrp, workerGrp).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO)).childHandler(handler);
            sb.bind(local_port).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            workerGrp.shutdownGracefully();
            bossGrp.shutdownGracefully();
        }
    }
}
