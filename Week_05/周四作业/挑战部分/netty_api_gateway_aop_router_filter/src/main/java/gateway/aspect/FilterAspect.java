package gateway.aspect;

import gateway.filter.ProxyRequestFilter;
import gateway.server.HttpOutboundHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class FilterAspect {
    // 默认使用所有的Filter
    @Autowired
    List<ProxyRequestFilter> filters;

    // ilter 相关的环绕拦截器实现，在HttpInboundHandler的ChannelRead函数调用之前先用Filter进行处理
    public Object aroundFilter(ProceedingJoinPoint pjp, ChannelHandlerContext ctx, Object msg, HttpOutboundHandler outboundHandler) {
        FullHttpRequest req = (FullHttpRequest) msg;

        // 顺序调用所有Filter处理req
        for (ProxyRequestFilter filter : filters) {
            filter.filter(req, ctx);
        }

        Object ret = null;
        try {
            ret = pjp.proceed();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return ret;
    }
}
