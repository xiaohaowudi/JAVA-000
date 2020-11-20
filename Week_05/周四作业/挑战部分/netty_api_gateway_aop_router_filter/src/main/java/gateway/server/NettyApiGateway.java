/*
API 网关主类
*/


package gateway.server;

import gateway.router.ProxyRouter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.*;

public class NettyApiGateway {

    public static void main(String[] args) {
        String localPort = System.getProperty("localPort", "8888");

        ApplicationContext context = null;
        try {
            context = new ClassPathXmlApplicationContext("spring-api-geteway-conf.xml");
            System.err.println(Arrays.toString(context.getBeanDefinitionNames()));

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }


        int port = Integer.parseInt(localPort);
        System.out.println("Api Gateway Running (proxy server running, local port : " + port + ")");

        try {
            // 注册3个服务器权重
            Map<String, Integer> server2weight = new HashMap<String, Integer>();
            server2weight.put("http://192.168.3.100:8808", 100);
            //server2weight.put("http://192.168.3.200:8808", 200);
            //server2weight.put("http://192.168.3.144:8808", 300);

            // 注册服务的API信息
            Map<String, Set<String>> server2uris = new HashMap<String, Set<String>>();
            Set<String> uris = new HashSet<String>();
            uris.add("/api/test");
            server2uris.put("http://192.168.3.100:8808", uris);

            /*
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
             */


            new ProxyRunner(port).run(context, ProxyRouter.ProxyRouterType.WEIGHT, server2uris, server2weight, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
