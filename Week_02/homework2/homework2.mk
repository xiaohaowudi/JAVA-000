本作业用netty框架构建简单的HTTP服务端，在8808端口启动服务，服务端功能将字符串"hello, this is netty server, my name is gongruohao"封装到HTTP报文body中并发送给请求端，
用OkHttp框架构造异步Get方式获取服务器端数据的额客户端，获取到服务器端的数据之后转为字符串打印，验证HTTP通信

服务器端代码：

NettyServerApplication.java
```java
public class NettyServerApplication {

    public static void main(String[] args) {
        HttpServer server = new HttpServer(false,8808);
        try {
            System.out.println("server running!!!");
            server.run();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}

```

