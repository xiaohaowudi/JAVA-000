package gateway.client;

/*
代理客户端收到的响应的处理接口
 */

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

// 代理的HTTP响应消费者接口
public interface ProxyRspConsumer {
    public void processProxyRsp(byte[] rsp, FullHttpRequest orig_req, ChannelHandlerContext ctx);
}
