package gateway.server;


import gateway.client.ProxyClient;
import gateway.filter.ProxyRequestFilter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;
import gateway.router.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;


public class HttpInboundHandler extends ChannelInboundHandlerAdapter {
    HttpOutboundHandler outboundHandler;
    List<ProxyRequestFilter> filters;

    public void setRouter(ProxyRouter router) {
        outboundHandler.setRouter(router);
    }

    public void setOutboundHandler(HttpOutboundHandler outboundHandler) {
        this.outboundHandler = outboundHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            FullHttpRequest req = (FullHttpRequest) msg;

            // 顺序调用所有Filter处理req
            for (ProxyRequestFilter filter : this.filters) {
                filter.filter(req, ctx);
            }

            outboundHandler.handle((FullHttpRequest) msg, ctx);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public void setFilters(List filters) {
        this.filters = filters;
    }
}
