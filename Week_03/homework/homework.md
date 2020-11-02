# 作业要求

1)周四作业:整合你上次作业的httpclient/okhttp;

2)周四作业(可选):使用netty实现后端http访问(代替上一步骤);

3)周六作业:实现过滤器;

4)周六作业(可选):实现路由;


# 作业实现

## 代码结构简述(具体代码清参上库上code目录下文件)
```
src
├── main
│   ├── java
│   │   └── gateway
│   │       ├── client
│   │       │   ├── NettyHttpClientAsyncGet.java
│   │       │   ├── OkHttpClientAsyncGet.java
│   │       │   ├── ProxyClient.java
│   │       │   └── ProxyRspConsumer.java
│   │       ├── filter
│   │       │   ├── AddDevelopInfoFilter.java
│   │       │   ├── AddNameFilter.java
│   │       │   ├── AddVersionFilter.java
│   │       │   └── ProxyRequestFilter.java
│   │       ├── router
│   │       │   ├── PrintDispatchInfoWrapper.java
│   │       │   ├── ProxyRouter.java
│   │       │   ├── RandomRouter.java
│   │       │   ├── RouteInfo.java
│   │       │   ├── WeightBasedRouter.java
│   │       │   ├── WeightInfo.java
│   │       │   └── WeightRouter.java
│   │       └── server
│   │           ├── HttpInboundHandler.java
│   │           ├── HttpOutboundHandler.java
│   │           ├── NettyApiGateway.java
│   │           ├── ProxyReqHandler.java
│   │           └── ProxyRunner.java
│   └── resources
└── test
    └── java
```

整个工程实现一个简单的API网关，完成了要求的所有功能，实现HTTP请求和响应的转发，客户端通过HTTP访问网关上的服务，由网关实现路由，将HTTP请求转发给后端其他真正运行服务的服务器，然后将后端服务器的响应数据转发给一开始的请求发送客户端，后端服务器信息和其提供的服务信息均动态通过注册接口注册给网关，网关服务器代码总体使用Netty框架完成，通过OkHttp和Netty分别实现了两种代理客户端用于给后端服务器发送HTTP请求

**client目录**
client目录下为客户端相关代码实现，ProxyClient.java 和 ProxyRspConsumer.java 两个文件分别定义了客户端的顶层接口和代理客户端收到的服务端响应的消费者接口：
```java
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

```

```java
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
```

NettyHttpClientAsyncGet 和 OkHttpClientAsyncGet 类分别基于Netty框架和OkHttp框架实现了接口ProxyClient，都提供了异步发送Http请求，并且在接收到后端服务器响应之后，将其转换为字节流然后回调PProxyRspConsumer接口中processProxyRsp方法的功能，用于将收到的响应传送回API网关服务器

**Server目录**

基于Netty对API网关服务器功能进行实现，主要功能是接收客户端Http请求，然后顺序调用所有注册给网关服务器的Filter对请求进行过滤和修改，然后根据注册好的路由信息和不同的的策略调用Router的路由接口获取后端服务器地址，借助前面实现的网关Client将请求转发给后端服务器，在收到客户端以回调方式传送的响应之后，再统一发送给一开始发送请求的客户端

**filter目录**

filter目录下文件对filter进行实现，主要实现的三个filter功能都类似，在原始请求的请求头中添加不同的头信息，共同实现如下的ProxyRequestFilter接口：

```java
package gateway.filter;


import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

// Filter接口
public interface ProxyRequestFilter {
    public void filter(FullHttpRequest req, ChannelHandlerContext ctx);
}

```
AddDevelopInfoFilter 功能是在head中添加键值对Developer_Info", "XXX_Technology_Company"
AddNameFilter 功能是在head中添加键值对"Name", "Jamse_Bond"
AddVersionFilter 功能是在head中添加键值对"Software_Version", "12.1.3_Beta"

添加的键值对将在网关发送给后端服务器的请求报文中体现

**router目录**

router目录下是对路由器的实现，主要实现两种路由功能，WeightRouter实现基于服务器权重的分发功能，RandomRouter实现随机的分发功能，ProxyRouter是路由功能的公共接口

WeightRouter实现中根据注册的服务器权重将各个服务器映射成长度不同的区间，然后生成随机数，根据随机数落在区间的编号确定路由的目标服务器，保证了分发请求的比例根服务器的权重匹配，核心实现代码如下：
```java
@Override
public String getRoutedServiceUrl(String apiUri) {
    String[] servers = routeInfo.getServers(apiUri);
    if (servers == null) {
        return null;
    }

    // 按照权重比例随机值计算路由目标服务器
    TreeMap<Integer, String> bound2server = new TreeMap<Integer, String>();
    int bound = 0;
    for (String s : servers) {
        int w = weightInfo.getWeightInfo(s);
        if (w == 0) {
            continue;
        }

        bound += w;
        bound2server.put(bound, s);
    }

    if (bound == 0) {
        return servers[new Random().nextInt(servers.length)];
    }

    Random rand = new Random();
    int rand_val = rand.nextInt(bound);

    Integer key = bound2server.ceilingKey(rand_val);
    if (key == null) {
        return servers[new Random().nextInt(servers.length)];
    }

    return bound2server.get(key);
}
```

## 功能点验证
### 验证环境和方法
环境包含局域网中三台物理主机:
1. Mac系统主机IP为192.168.3.100，作为网关服务器服务端口8888，同时在8808端口运行后端真实的服务
2. Win10系统主机IP为192.168.3.200，在8808端口运行后端真实后端服务
3. Linux系统主机IP为192.168.3.144，同样在8808端口运行真实后端服务

主机权重和服务信息的注册代码如下：

```java
public class NettyApiGateway {

    public static void main(String[] args) {
        String localPort = System.getProperty("localPort", "8888");

        int port = Integer.parseInt(localPort);
        System.out.println("Api Gateway Running (proxy server running, local port : " + port + ")");

        try {
            // 注册3个服务器权重
            Map<String, Integer> server2weight = new HashMap<String, Integer>();
            server2weight.put("http://192.168.3.100:8808", 100);
            server2weight.put("http://192.168.3.200:8808", 200);
            server2weight.put("http://192.168.3.144:8808", 300);

            // 注册服务的API信息
            Map<String, Set<String>> server2uris = new HashMap<String, Set<String>>();
            Set<String> uris = new HashSet<String>();
            uris.add("/api/test");
            uris.add("/api/func1");
            uris.add("/api/func2");
            uris.add("/api/func3");
            uris.add("/api/func4");
            server2uris.put("http://192.168.3.100:8808", uris);

            uris = new HashSet<String>();
            uris.add("/api/test");
            uris.add("/api/func1");
            uris.add("/api/func2");
            server2uris.put("http://192.168.3.200:8808", uris);

            uris = new HashSet<String>();
            uris.add("/api/test");
            uris.add("/api/func3");
            uris.add("/api/func4");
            server2uris.put("http://192.168.3.144:8808", uris);

            new ProxyRunner(port).run(ProxyRouter.ProxyRouterType.WEIGHT, server2uris, server2weight, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

```


在win10系统主机上用压测工具sb压测主机1上的API网关服务，验证转发性能，并用Wireshark, Postman配合验证转发的请求和响应内容的正确性(为简化验证过程，验证时候网关服务器固定采用了OkHttp实现的客户端和基于权重的路由分发策略)

### 基本转发功能验证
Postman工具给 http://192.168.3.100:8888/api/test 发送请求，响应正常，验证了基本转发功能OK：

<img src="https://github.com/xiaohaowudi/JAVA-000/blob/main/Week_03/image/Postman%E6%88%AA%E5%9B%BE.png" width="100%" height="100%" />

Postman运行于192.168.3.100主机上，给http://192.168.3.100:8888/api/test 发送的请求报文截图如下：

<img src="https://github.com/xiaohaowudi/JAVA-000/blob/main/Week_03/image/%E8%AF%B7%E6%B1%82%E5%8C%851%E6%88%AA%E5%9B%BE.png" width="75%" height="75%" />

可见是由192.168.3.100主机自己发给自己，其目的端口为8888，此时请求中还没有filter添加的字段

转发出去的请求报文截图如下：

<img src="https://github.com/xiaohaowudi/JAVA-000/blob/main/Week_03/image/%E8%AF%B7%E6%B1%82%E5%8C%852%E6%88%AA%E5%9B%BE.png" width="75%" height="75%" />

可见其通过路由转发给了192.168.3.200主机，其目的端口变为为8808，且请求的http头中已经夹带了filter添加的三个字段，验证了filter功能的正确性

后端服务器回给网关客户端的响应截图如下：

<img src="https://github.com/xiaohaowudi/JAVA-000/blob/main/Week_03/image/%E5%93%8D%E5%BA%94%E5%8C%851%E6%88%AA%E5%9B%BE.png" width="75%" height="75%" />

可见该响应是由192.168.3.200主机回给192.168.3.100网关客户端，其原端口为8808

最后是API网关转发给Postman的报文截图：

<img src="https://github.com/xiaohaowudi/JAVA-000/blob/main/Week_03/image/%E5%93%8D%E5%BA%94%E5%8C%852%E6%88%AA%E5%9B%BE.png" width="75%" height="75%" />

可见最后响应是由192.168.3.100转发给192.168.3.100，其源端口是网关服务端口8888，从数据包内容和四个HTTP报文中的源目IP以及源目mac可以验证转发了流程的正确性

### 压测和路由功能
192.168.3.200主机上运行sb -u http://192.168.3.100:8888/api/test -c 30 -n 150000 命令进行压力测试，30并发量验证150000条请求的处理情况，并用Wireshark抓包验证请求是分发到了3台主机的

压测结果如下：
```
gongruohao@GRHWIN10 C:\Users\GongRuoHao\Desktop\PressureTestTool>sb -u http://192.168.3.100:8888/api/test -c 30 -n 150000
Starting at 11/3/2020 12:02:30 AM
[Press C to stop the test]
150000  (RPS: 4342.8)
---------------Finished!----------------
Finished at 11/3/2020 12:03:05 AM (took 00:00:34.5923289)
Status 200:    150000
RPS: 4221.2 (requests/second)
Max: 468ms
Min: 0ms
Avg: 3.3ms
  50%   below 2ms
  60%   below 2ms
  70%   below 3ms
  80%   below 5ms
  90%   below 7ms
  95%   below 8ms
  98%   below 12ms
  99%   below 18ms
99.9%   below 65ms
```
测试结果RPS 4300左右，150000条请求和响应全部正确转发

在网关服务所在主机192.168.3.100上网卡上抓包，可以获取到HTTP报文的收发情况如下

<img src="https://github.com/xiaohaowudi/JAVA-000/blob/main/Week_03/image/%E8%AF%B7%E6%B1%82%E6%8A%A5%E6%96%87%E5%88%86%E5%8F%91%E6%88%AA%E5%9B%BE.png" width="75%" height="75%" />

可见请求分发给了192.168.3.144和192.168.3.200 且 在回环网卡上能够抓到网关转发给自己的请求，证明了请求和响应确实是在3台主机之间交互，路由功能OK，为了验证路由分发比例的正确性，编写代码时候添加了调试功能，在Router进行分发时候会记录给每一个服务器分发请求的次数，通过给API网关发送/api/debug请求，会触发网关服务器在控制台打印每个服务器的分发次数

装饰器PrintDispatchInfoWrapper 包装了Router，实现了计数功能，实现代码如下：

```java
package gateway.router;

import java.util.HashMap;
import java.util.Map;


// 打印分发信息装饰器
public class PrintDispatchInfoWrapper implements ProxyRouter {
    private Map<String, Integer> server2disp_cnt = new HashMap<String, Integer>();
    private final ProxyRouter innerRouter;

    public PrintDispatchInfoWrapper(ProxyRouter router) {
        this.innerRouter = router;
    }

    @Override
    public void registerMapInfo(String targetServer, String apiUri) {
        this.innerRouter.registerMapInfo(targetServer, apiUri);
    }

    @Override
    public synchronized String getRoutedServiceUrl(String apiUri) {
        String server = innerRouter.getRoutedServiceUrl(apiUri);
        if (!server2disp_cnt.containsKey(server)) {
            server2disp_cnt.put(server, 0);
        }

        int val = server2disp_cnt.get(server);
        server2disp_cnt.put(server, val+1);
        return server;
    }

    // 打印分发信息
    public void printDispatchInfo() {
        for (Map.Entry<String, Integer> entry : server2disp_cnt.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}
```

在压测命令 sb -u http://192.168.3.100:8888/api/test -c 30 -n 150000 执行之后，利用Postman 给网关发送 http://192.168.3.100:8888/api/debug 请求，打印的计数信息如下

```
http://192.168.3.144:8808 : 74768
http://192.168.3.100:8808 : 25547
http://192.168.3.200:8808 : 49685
```
一开始注册的三个服务器权重分别为300 100 200，通过算法随机生成路由的目标服务器，从打印数值可见该算法能保证分发的比例是按照服务器权重进行。基于权重的路由算法功能OK



