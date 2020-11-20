package gateway.filter;

// 添加软件版本信息Filter

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class AddVersionFilter implements ProxyRequestFilter {

    @Override
    public void filter(FullHttpRequest req, ChannelHandlerContext ctx) {
        // 添加一个测试的版本信息
        req.headers().add("Software_Version", "12.1.3_Beta");
    }
}
