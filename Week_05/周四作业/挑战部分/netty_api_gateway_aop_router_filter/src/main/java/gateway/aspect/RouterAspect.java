package gateway.aspect;

import gateway.router.ProxyRouter;
import org.aspectj.lang.ProceedingJoinPoint;

import javax.annotation.Resource;

public class RouterAspect {

    @Resource(name = "weightRouter")
    private ProxyRouter router;

    // 环绕拦截器进行路由解析
    public Object aroundRouter(ProceedingJoinPoint pjp, String uri) {
        System.err.println("aroundRoute called!");

        Object ret = router.getRoutedServiceUrl(uri);
        if (ret == null) {
            // router解析不了，再调用默认实现
            try {
                ret = pjp.proceed();
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        return ret;
    }

}
