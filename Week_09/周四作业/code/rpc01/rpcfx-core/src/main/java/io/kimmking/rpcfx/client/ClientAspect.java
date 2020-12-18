package io.kimmking.rpcfx.client;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class ClientAspect {

    // 拦截所有带了RemoteService注解的方法调用
    @Pointcut("@annotation(io.kimmking.rpcfx.client.RemoteService)")
    public void pointCut() {

    }

    @Around("io.kimmking.rpcfx.client.ClientAspect.pointCut()")
    public Object rpcInvoke(ProceedingJoinPoint pjp) throws Throwable {
        return Rpcfx.rpcInvoke(pjp);
    }
}
