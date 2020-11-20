package gateway.router;


// 路由功能接口
public interface ProxyRouter {
    public static enum ProxyRouterType {
        RANDOM,     // 随机路由
        WEIGHT      // 基于服务器权重路由
    }

    public void registerMapInfo(String targetServer, String apiUri);       // 注册目标服务器和服务URI
    public String getRoutedServiceUrl(String apiUri);                       // 获取路由之后的发送服务的URL
}
