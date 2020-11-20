package gateway.server;

// 目标路由解析器类
public class OutboundRouteResolver {

    // 根据请求uri获取目标服务的url, 默认返回本次服务地址，通过Router相关的切面做功能增强
    public String resolveTargetUrl(String uri) {
        return "http://localhost:8808";
    }

}
