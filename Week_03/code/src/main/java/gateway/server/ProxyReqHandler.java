/*
代理端处理请求类，进行Channel初始化
 */

package gateway.server;

import gateway.filter.AddDevelopInfoFilter;
import gateway.filter.AddNameFilter;
import gateway.filter.AddVersionFilter;
import gateway.router.ProxyRouter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class ProxyReqHandler extends ChannelInitializer<SocketChannel> {
    private final ProxyRouter router;

    public ProxyReqHandler(ProxyRouter router) {
        this.router = router;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipe = ch.pipeline();
        pipe.addLast(new HttpServerCodec());
        pipe.addLast(new HttpObjectAggregator(1024*1024));

        // 注册3个Filter
        HttpInboundHandler inboundHandler = new HttpInboundHandler(router);
        inboundHandler.registerFilter(new AddDevelopInfoFilter());
        inboundHandler.registerFilter(new AddNameFilter());
        inboundHandler.registerFilter(new AddVersionFilter());

        pipe.addLast(inboundHandler);
    }
}
