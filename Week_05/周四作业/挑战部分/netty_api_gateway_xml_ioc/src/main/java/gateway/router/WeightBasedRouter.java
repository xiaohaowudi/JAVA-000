package gateway.router;

public interface WeightBasedRouter extends ProxyRouter {
    public void registerServerWeight(String target_server, int weight);
}
