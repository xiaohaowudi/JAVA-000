package io.kimmking.rpcfx.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sun.jdi.InvocationException;
import io.kimmking.rpcfx.api.RpcfxException;
import io.kimmking.rpcfx.api.RpcfxRequest;
import io.kimmking.rpcfx.api.RpcfxResolver;
import io.kimmking.rpcfx.api.RpcfxResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class RpcfxInvoker {

    private RpcfxResolver resolver;

    public RpcfxInvoker(RpcfxResolver resolver){
        this.resolver = resolver;
    }

    public RpcfxResponse invoke(RpcfxRequest request) {
        RpcfxResponse response = new RpcfxResponse();
        String serviceClass = request.getServiceClass();

        try {
            Object service = resolver.resolve(serviceClass);
            Method method = resolveMethodByNameAndParamsTypes(service.getClass(), request.getMethod(), request.getParamsTypes());
            Object result = method.invoke(service, request.getParams());
            response.setResult(JSON.toJSONString(result, SerializerFeature.WriteClassName));
            response.setStatus(true);
            return response;
        } catch (Exception e) {

            Throwable actualException = e;
            if (e instanceof InvocationTargetException) {
                actualException = ((InvocationTargetException)e).getTargetException();
            }

            response.setResult(null);

            RpcfxException rpcException = new RpcfxException();
            rpcException.setCauseString(actualException.getLocalizedMessage());

            StackTraceElement[] stackElem = actualException.getStackTrace();
            if (stackElem != null && stackElem.length > 0) {
                rpcException.setLastStackFilenme(stackElem[0].getFileName());
                rpcException.setLastStackLineNum(stackElem[0].getLineNumber());
                rpcException.setLastMethodName(stackElem[0].getMethodName());
            }

            response.setException(rpcException);
            response.setStatus(false);
            return response;
        }
    }

    // 根据方法名和参数类型列表获取Method实例
    private Method resolveMethodByNameAndParamsTypes(Class<?> klass, String methodName, Object[] paramsTypes) {
        return Arrays.stream(klass.getMethods()).filter(
                m -> methodName.equals(m.getName()) && Arrays.equals(Arrays.stream(m.getParameterTypes()).map(Class::toString).toArray(), paramsTypes)
        ).findFirst().get();
    }

}
