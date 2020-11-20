package gateway.router;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class WeightRouter implements WeightBasedRouter {
    private RouteInfo routeInfo = new RouteInfo();
    private WeightInfo weightInfo = new WeightInfo();

    @Override
    public void registerServerWeight(String targetServer, int weight) {
        weightInfo.addWeightInfo(targetServer, weight);
    }

    @Override
    public void registerMapInfo(String targetServer, String apiUri) {
        routeInfo.addRoute(targetServer, apiUri);
    }

    @Override
    public String getRoutedServiceUrl(String apiUri) {
        String[] servers = routeInfo.getServers(apiUri);
        if (servers == null) {
            return null;
        }

        // 按照权重比例随机值计算路由目标服务器
        TreeMap<Integer, String> bound2server = new TreeMap<Integer, String>();
        int bound = 0;
        for (String s : servers) {
            int w = weightInfo.getWeightInfo(s);
            if (w == 0) {
                continue;
            }

            bound += w;
            bound2server.put(bound, s);
        }

        if (bound == 0) {
            return servers[new Random().nextInt(servers.length)];
        }

        Random rand = new Random();
        int rand_val = rand.nextInt(bound);

        Integer key = bound2server.ceilingKey(rand_val);
        if (key == null) {
            return servers[new Random().nextInt(servers.length)];
        }

        return bound2server.get(key);
    }
}
