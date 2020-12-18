package io.kimmking.rpcfx.client;

public interface HttpClientStub {
    String getRemoteUrl();          // 获取完整的远端服务URL
    String getServiceName();        // 获取远端的服务名
    String getRemoteHost();         // 获取远端的主机名
    String getRemoteUri();          // 获取远端的uri
    Integer getRemotePort();        // 获取远端的端口

}
