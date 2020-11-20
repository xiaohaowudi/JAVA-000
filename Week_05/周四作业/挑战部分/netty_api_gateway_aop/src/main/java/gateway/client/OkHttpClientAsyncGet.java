
/*
基于OkHttp 实现的Http客户端
 */

package gateway.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import okhttp3.*;
import java.io.IOException;
import java.util.Map;

public class OkHttpClientAsyncGet implements ProxyClient {
    final private OkHttpClient client;

    static class RespHandler {
        public void handle(byte[] rsp, ChannelHandlerContext ctx, ProxyRspConsumer consumer, FullHttpRequest orig_req) {
            consumer.processProxyRsp(rsp, orig_req, ctx);
        }
    }

    static class RespCallback implements Callback {
        private final RespHandler handler;
        private ChannelHandlerContext ctx;
        private ProxyRspConsumer consumer;
        private FullHttpRequest origReq;

        public RespCallback(RespHandler h, ChannelHandlerContext ctx, ProxyRspConsumer consumer, FullHttpRequest origReq) {
            this.handler = h;
            this.ctx = ctx;
            this.consumer = consumer;
            this.origReq = origReq;
        }

        public void onResponse(Call call, Response response) throws IOException {
            handler.handle(response.body().bytes(), ctx, consumer, origReq);
        }

        public void onFailure(Call call, IOException e) {
            e.printStackTrace();
            consumer.processProxyRsp(null, origReq, ctx);
        }
    }

    public OkHttpClientAsyncGet() {
        this.client = new OkHttpClient();
    }

    @Override
    public void getRequest(String ip, int port, String url, FullHttpRequest origReq, ProxyRspConsumer consumer, ChannelHandlerContext ctx) {
        Request.Builder builder = new Request.Builder();

        // 复制原来请求中所有的请求头
        for (Map.Entry<String, String> entry : origReq.headers()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }

        Request req = builder.url(url).build();
        client.newCall(req).enqueue(new RespCallback(new RespHandler(), ctx, consumer, origReq));
    }
}
