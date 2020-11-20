/*
代理端处理请求类，进行Channel初始化
 */

package gateway.server;

import gateway.filter.AddDevelopInfoFilter;
import gateway.filter.AddNameFilter;
import gateway.filter.AddVersionFilter;
import gateway.router.ProxyRouter;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.springframework.context.ApplicationContext;

public class ProxyReqHandler extends ChannelInitializer<SocketChannel> {
    private final ApplicationContext appContext;

    public ProxyReqHandler(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipe = ch.pipeline();
        pipe.addLast(new HttpServerCodec());
        pipe.addLast(new HttpObjectAggregator(1024*1024));

        HttpInboundHandler inboundHandler = (HttpInboundHandler) appContext.getBean("inBoundHandler");
        pipe.addLast(inboundHandler);
    }
}
