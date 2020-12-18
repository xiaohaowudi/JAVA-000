package io.kimmking.rpcfx.client;

import java.net.URI;

import com.alibaba.fastjson.JSON;
import io.kimmking.rpcfx.api.RpcfxRequest;
import io.kimmking.rpcfx.api.RpcfxResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


class ByteBufToBytes {
    private ByteBuf temp;
    private boolean end = true;
    public ByteBufToBytes(int length) {
        temp = Unpooled.buffer(length);
    }
    public void reading(ByteBuf datas) {
        datas.readBytes(temp, datas.readableBytes());
        if (this.temp.writableBytes() != 0) {
            end = false;
        } else {
            end = true;
        }
    }
    public boolean isEnd() {
        return end;
    }
    public byte[] readFull() {
        if (end) {
            byte[] contentByte = new byte[this.temp.readableBytes()];
            this.temp.readBytes(contentByte);
            this.temp.release();
            return contentByte;
        } else {
            return null;
        }
    }
    public byte[] read(ByteBuf datas) {
        byte[] bytes = new byte[datas.readableBytes()];
        datas.readBytes(bytes);
        return bytes;
    }
}

public class NettyHttpClient {

    static class ByteBufToBytes {
        private ByteBuf temp;
        private boolean end = true;
        public ByteBufToBytes(int length) {
            temp = Unpooled.buffer(length);
        }
        public void reading(ByteBuf datas) {
            datas.readBytes(temp, datas.readableBytes());
            if (this.temp.writableBytes() != 0) {
                end = false;
            } else {
                end = true;
            }
        }
        public boolean isEnd() {
            return end;
        }
        public byte[] readFull() {
            if (end) {
                byte[] contentByte = new byte[this.temp.readableBytes()];
                this.temp.readBytes(contentByte);
                this.temp.release();
                return contentByte;
            } else {
                return null;
            }
        }
        public byte[] read(ByteBuf datas) {
            byte[] bytes = new byte[datas.readableBytes()];
            datas.readBytes(bytes);
            return bytes;
        }
    }

    static class NettyHttpClientHandler extends ChannelInboundHandlerAdapter {
        private ByteBufToBytes reader;
        private RpcfxResponse rpcResp;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg)
                throws Exception {
            if (msg instanceof HttpResponse) {
                FullHttpResponse response = (FullHttpResponse) msg;

                ByteBuf buf = response.content();
                byte[] rspBuf = new byte[buf.readableBytes()];
                buf.readBytes(rspBuf);
                String jsonStr = new String(rspBuf, "UTF-8");
                rpcResp = JSON.parseObject(jsonStr, RpcfxResponse.class);
            }

            ctx.close();
        }

        public RpcfxResponse getRpcResp() {
            return rpcResp;
        }
    }


    public RpcfxResponse postRequest(RpcfxRequest req, String host, int port, String uri) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        RpcfxResponse rpcRsp = null;

        try {
            Bootstrap b = new Bootstrap();
            NettyHttpClientHandler handler = new NettyHttpClientHandler();

            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new HttpClientCodec());
                    ch.pipeline().addLast(new HttpObjectAggregator(1024 * 10 * 1024));
                    ch.pipeline().addLast(new HttpContentDecompressor());
                    ch.pipeline().addLast(handler);
                }

            });
            ChannelFuture f = b.connect(host, port).sync();
            String msg = JSON.toJSONString(req);
            DefaultFullHttpRequest request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.POST, new URI(uri).toASCIIString(),
                    Unpooled.wrappedBuffer(msg.getBytes()));

            // 构建http请求
            request.headers().set("Content-Type", "application/json; charset=utf-8");
            request.headers().set("Content-Length", request.content().readableBytes());
            request.headers().set("Host", host + ":" + port);
            request.headers().set("Connection", "Keep-Alive");
            request.headers().set("Accept-Encoding", "gzip");
            request.headers().set("User-Agent", "NettyHttpClient");

            // 发送http请求
            f.channel().write(request);
            f.channel().flush();
            f.channel().closeFuture().sync();

            rpcRsp = handler.getRpcResp();
        } finally {
            workerGroup.shutdownGracefully();
        }

        return rpcRsp;
    }

    public static void main(String[] args) throws Exception {
        NettyHttpClient client = new NettyHttpClient();
        RpcfxRequest req = new RpcfxRequest();
        req.setMethod("findById");
        req.setServiceClass("io.kimmking.rpcfx.demo.api.UserService");

        Object[] params = new Object[] {new Integer(1)};
        Object[] paramTypes = new Object[] {new String("class java.lang.Integer")};

        req.setParams(params);
        req.setParamsTypes(paramTypes);

        RpcfxResponse resp = client.postRequest(req, "127.0.0.1", 8080, "/");
        System.out.println(resp.getResult());
        //client.connect("127.0.0.1", 8000);
    }
}
