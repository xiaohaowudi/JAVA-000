package gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class AddNameFilter implements ProxyRequestFilter {
    @Override
    public void filter(FullHttpRequest req, ChannelHandlerContext ctx) {
        // 添加一个测试的姓名信息
        req.headers().add("Name", "Jamse_Bond");
    }
}
