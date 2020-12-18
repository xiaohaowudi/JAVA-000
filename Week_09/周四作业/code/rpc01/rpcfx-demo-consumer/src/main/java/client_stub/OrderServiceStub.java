package client_stub;

import io.kimmking.rpcfx.client.HttpClientStub;
import io.kimmking.rpcfx.client.RemoteService;
import io.kimmking.rpcfx.demo.api.Order;
import io.kimmking.rpcfx.demo.api.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceStub implements OrderService, HttpClientStub {

    @Override
    public String getRemoteUrl() {
        return "http://localhost:8080/";
    }

    @Override
    public String getRemoteHost() {
        return "127.0.0.1";
    }

    @Override
    public Integer getRemotePort() {
        return 8080;
    }

    @Override
    public String getRemoteUri() {
        return "/";
    }

    @Override
    @RemoteService
    public Order findOrderById(Integer id) {
        return null;
    }

    @Override
    public String getServiceName() {
        return OrderService.class.getName();
    }
}
