package gateway.router;

import java.util.HashMap;
import java.util.Map;


// 打印分发信息装饰器
public class PrintDispatchInfoWrapper implements ProxyRouter {
    private Map<String, Integer> server2disp_cnt = new HashMap<String, Integer>();
    private final ProxyRouter innerRouter;

    public PrintDispatchInfoWrapper(ProxyRouter router) {
        this.innerRouter = router;
    }

    @Override
    public void registerMapInfo(String targetServer, String apiUri) {
        this.innerRouter.registerMapInfo(targetServer, apiUri);
    }

    @Override
    public synchronized String getRoutedServiceUrl(String apiUri) {
        String server = innerRouter.getRoutedServiceUrl(apiUri);
        if (!server2disp_cnt.containsKey(server)) {
            server2disp_cnt.put(server, 0);
        }

        int val = server2disp_cnt.get(server);
        server2disp_cnt.put(server, val+1);
        return server;
    }

    // 打印分发信息
    public void printDispatchInfo() {
        for (Map.Entry<String, Integer> entry : server2disp_cnt.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}
