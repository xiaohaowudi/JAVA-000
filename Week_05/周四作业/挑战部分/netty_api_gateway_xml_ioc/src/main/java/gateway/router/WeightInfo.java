package gateway.router;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

// 服务器权重信息
public class WeightInfo {
    private final Map<String, Integer> server2weight;

    public WeightInfo() {
        server2weight = new ConcurrentHashMap<String, Integer>();
    }

    public void addWeightInfo(String targetServer, int weight) {
        server2weight.put(targetServer, weight);
    }

    public int getWeightInfo(String targetServer) {
        return server2weight.containsKey(targetServer) ? server2weight.get(targetServer) : 0;
    }

}
