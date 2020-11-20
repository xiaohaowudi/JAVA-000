package gateway.router;

import java.util.Random;

public class RandomRouter implements ProxyRouter {
    private final RouteInfo routeInfo = new RouteInfo();

    @Override
    public void registerMapInfo(String targetServer, String apiUri) {
        routeInfo.addRoute(targetServer, apiUri);
    }

    @Override
    public String getRoutedServiceUrl(String apiUri) {
        String[] servers = routeInfo.getServers(apiUri);
        return servers == null ? null : servers[new Random().nextInt(servers.length)];
    }
}
