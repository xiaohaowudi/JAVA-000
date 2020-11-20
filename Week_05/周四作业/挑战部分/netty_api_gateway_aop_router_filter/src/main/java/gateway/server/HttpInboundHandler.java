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
    private HttpOutboundHandler outboundHandler;
    private InboundMsgHandler inboundMsgHandler;

    public void setOutboundHandler(HttpOutboundHandler outboundHandler) {
        this.outboundHandler = outboundHandler;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            inboundMsgHandler.handleMsg(ctx, msg, outboundHandler);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public void setInboundMsgHandler(InboundMsgHandler inboundMsgHandler) {
        this.inboundMsgHandler = inboundMsgHandler;
    }
}
