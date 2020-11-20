package gateway.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class InboundMsgHandler {

    public void handleMsg(ChannelHandlerContext ctx, Object msg, HttpOutboundHandler outboundHandler) {
        outboundHandler.handle((FullHttpRequest) msg, ctx);
    }
}
