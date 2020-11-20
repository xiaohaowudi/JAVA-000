package gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class AddDevelopInfoFilter implements ProxyRequestFilter {
    @Override
    public void filter(FullHttpRequest req, ChannelHandlerContext ctx) {
        // 添加一个测试的开发者信息
        req.headers().add("Developer_Info", "XXX_Technology_Company");
    }
}
