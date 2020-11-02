# 作业要求

1)周四作业:整合你上次作业的httpclient/okhttp;

2)周四作业(可选):使用netty实现后端http访问(代替上一步骤);

3)周六作业:实现过滤器;

4)周六作业(可选):实现路由;


# 作业实现

## 代码结构
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

整个工程实现一个简答的API网关，实现HTTP请求和响应的转发，客户端通过HTTP访问网关上的服务，由网关实现路由，将HTTP请求转发给后端其他真正运行服务的服务器，然后将后端服务器的响应数据转发给一开始的请求发送客户端，后端服务器信息和其提供的服务信息均动态通过注册接口注册给网关，网关服务器代码总体使用Netty框架完成，通过OkHttp和Netty分别实现了两种代理客户端用于给后端服务器发送HTTP请求



