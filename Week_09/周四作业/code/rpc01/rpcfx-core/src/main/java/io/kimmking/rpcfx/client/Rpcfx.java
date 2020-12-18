package io.kimmking.rpcfx.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import io.kimmking.rpcfx.api.RpcfxException;
import io.kimmking.rpcfx.api.RpcfxRequest;
import io.kimmking.rpcfx.api.RpcfxResponse;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Arrays;

@Component
public final class Rpcfx implements ApplicationContextAware {
    static {
        ParserConfig.getGlobalInstance().addAccept("io.kimmking");
    }

    public static final MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");
    private ApplicationContext context;

    // 提供对客户端stub的aop切面增强
    public static Object rpcInvoke(ProceedingJoinPoint pjp) throws Throwable {
        Object target = pjp.getTarget();
        if (!(target instanceof HttpClientStub)) {
            throw new RuntimeException("target is not a ClientStub instance");
        }

        RpcfxRequest request = new RpcfxRequest();
        Object[] paramTypes = Arrays.stream(pjp.getArgs()).map(obj -> obj.getClass().toString()).toArray();
        request.setServiceClass(((HttpClientStub) target).getServiceName());
        request.setMethod(pjp.getSignature().getName());
        request.setParams(pjp.getArgs());
        request.setParamsTypes(paramTypes);

        //RpcfxResponse response = postRequestOkHttp(req, ((HttpClientStub) target).getRemoteUrl());
        RpcfxResponse response = postRequestNetty(request, ((HttpClientStub) target).getRemoteHost(), ((HttpClientStub) target).getRemotePort(), ((HttpClientStub) target).getRemoteUri());

        // 异常处理, 将响应中的异常取出，抛给业务层处理
        if (!response.getStatus()) {
            RpcfxException e = response.getException();
            if (e == null) {
                throw new RuntimeException("unknown remote exception");
            } else {
                throw e;
            }
        }

        return JSON.parse(response.getResult().toString());
    }

    // Netty 作为客户端发送POST请求
    private static RpcfxResponse postRequestNetty(RpcfxRequest req, String host, Integer port, String uri) throws Exception {
        return new NettyHttpClient().postRequest(req, host, port, uri);
    }

    // OkHttp 作为客户端发送POST请求
    private static RpcfxResponse postRequestOkHttp(RpcfxRequest req, String url) throws Exception {

        String reqJson = JSON.toJSONString(req);
        System.out.println("req json: "+reqJson);

        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSONTYPE, reqJson))
                .build();
        String respJson = client.newCall(request).execute().body().string();
        System.out.println("resp json: "+respJson);
        return JSON.parseObject(respJson, RpcfxResponse.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public <T> T create(final Class<T> serviceClass) {
        // 直接从容器中获取客户端的桩
        return context.getBean(serviceClass);
    }
}
