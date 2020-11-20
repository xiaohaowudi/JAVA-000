package gateway.router;


import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

// 路由信息类
public class RouteInfo {
    private final Map<String, Set<String>> api2servers;

    public RouteInfo() {
        api2servers = new ConcurrentHashMap<String, Set<String>>();
    }

    void addRoute(String targetServer, String apiUri) {
        if (!api2servers.containsKey(apiUri)) {
            api2servers.put(apiUri, new ConcurrentSkipListSet<String>());
        }

        Set<String> servers = api2servers.get(apiUri);
        servers.add(targetServer);
    }

    String[] getServers(String apiUri) {
        if (!api2servers.containsKey(apiUri)) {
            return null;
        }

        Set<String> servers = api2servers.get(apiUri);
        if (servers.size() == 0) {
            return null;
        }

        String[] serverList = new String[servers.size()];
        int idx = 0;
        for (String s : servers) {
            serverList[idx++] = s;
        }

        return serverList;
    }
}
