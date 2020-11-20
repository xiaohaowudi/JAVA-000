package gateway.filter;


import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

// Filter接口
public interface ProxyRequestFilter {
    public void filter(FullHttpRequest req, ChannelHandlerContext ctx);
}
