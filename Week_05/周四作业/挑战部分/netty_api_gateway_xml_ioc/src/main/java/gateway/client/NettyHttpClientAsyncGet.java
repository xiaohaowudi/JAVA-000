/*
基于Netty实现的HTTP客户端
*/

package gateway.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import java.util.Map;


public class NettyHttpClientAsyncGet implements ProxyClient {
    static class NettyClientHttpRespHandler extends ChannelInboundHandlerAdapter {
        private ChannelHandlerContext consumer_ctx;
        private ProxyRspConsumer consumer;
        private FullHttpRequest origReq;
        private String targetUrl;


        public NettyClientHttpRespHandler(ChannelHandlerContext ctx, ProxyRspConsumer consumer, FullHttpRequest origReq, String url) {
            this.consumer_ctx = ctx;
            this.consumer = consumer;
            this.origReq = origReq;
            this.targetUrl = url;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            try {
                FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, targetUrl);
                for (Map.Entry<String, String> entry : origReq.headers()) {
                    request.headers().add(entry.getKey(), entry.getValue());
                }

                ctx.writeAndFlush(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            try {
                FullHttpResponse rsp = (FullHttpResponse) msg;
                ByteBuf buf = rsp.content();
                byte[] rspBuf = new byte[buf.readableBytes()];
                buf.readBytes(rspBuf);
                consumer.processProxyRsp(rspBuf, origReq, consumer_ctx);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
            consumer.processProxyRsp(null, origReq, consumer_ctx);
        }
    }


    private EventLoopGroup workers = new NioEventLoopGroup();

    @Override
    public void getRequest(final String ip, final int port, final String url, final FullHttpRequest origReq, final ProxyRspConsumer consumer, final ChannelHandlerContext ctx) {
        Bootstrap bs = new Bootstrap();
        try {
            bs.group(workers).channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HttpClientCodec());
                            ch.pipeline().addLast(new HttpObjectAggregator(1024 * 10 * 1024));
                            ch.pipeline().addLast(new HttpContentDecompressor());
                            ch.pipeline().addLast(new NettyClientHttpRespHandler(ctx, consumer, origReq, url));
                        }
                    });

            bs.connect(ip, port).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
