/*
请求处理类，调用异步客户端的GET请求接口，获取到目标服务器数据之后，将数据转发回请求方
*/

package gateway.server;

import gateway.client.NettyHttpClientAsyncGet;
import gateway.client.OkHttpClientAsyncGet;
import gateway.client.ProxyRspConsumer;
import gateway.router.PrintDispatchInfoWrapper;
import gateway.router.ProxyRouter;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import gateway.client.ProxyClient;
import java.util.concurrent.*;

import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpOutboundHandler implements ProxyRspConsumer {
    private ProxyClient httpClient;
    private ExecutorService threadPool;
    private OutboundRouteResolver resolver;     // 路由解析类实例

    public void handle(final FullHttpRequest fullReq, final ChannelHandlerContext ctx) {
        final ProxyRspConsumer consumer = this;

        final String targetServer = resolver.resolveTargetUrl(fullReq.uri());
        if (targetServer == null) {
            processProxyRsp(null, fullReq, ctx);
            return;
        }

        // 从服务器信息字符串中解析出ip和端口
        int idx1 = targetServer.indexOf("://");
        int idx2 = targetServer.indexOf(":", idx1+3);
        final String ip = targetServer.substring(idx1+3, idx2);
        final int port = Integer.parseInt(targetServer.substring(idx2+1));

        threadPool.execute(new Runnable() {
            public void run() {
                httpClient.getRequest(ip, port, targetServer + fullReq.uri(), fullReq, consumer, ctx);
            }
        });
    }

    // 处理代理客户端接收到的远端服务器的响应
    public void processProxyRsp(byte[] rsp, FullHttpRequest origReq, ChannelHandlerContext ctx) {
        FullHttpResponse response = null;
        try {
            if (rsp != null) {
                response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(rsp));
            } else {
                response = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
            }

            response.headers().set("Content-Type", "application/json");
            response.headers().setInt("Content-Length", response.content().readableBytes());

            if (!HttpUtil.isKeepAlive(origReq)) {
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(CONNECTION, KEEP_ALIVE);
                ctx.writeAndFlush(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setHttpClient(ProxyClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setThreadPool(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    public void setResolver(OutboundRouteResolver resolver) {
        this.resolver = resolver;
    }
}
