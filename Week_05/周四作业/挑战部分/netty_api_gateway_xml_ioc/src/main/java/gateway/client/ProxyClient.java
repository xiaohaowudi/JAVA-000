package gateway.client;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

enum ProxyClientType
{
    OKHTTP,     // 客户端使用OkHttp实现
    NETTY       // 客户端是用Netty实现
}

// 代理HTTP客户端接口
public interface ProxyClient {

    public static enum ProxyClientType
    {
        OKHTTP,     // 客户端使用OkHttp实现
        NETTY       // 客户端是用Netty实现
    }

    public void getRequest(String ip, int port, String url, FullHttpRequest origReq, ProxyRspConsumer consumer, ChannelHandlerContext ctx);
}
