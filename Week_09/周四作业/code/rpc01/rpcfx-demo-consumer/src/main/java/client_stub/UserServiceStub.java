package client_stub;


import io.kimmking.rpcfx.client.HttpClientStub;
import io.kimmking.rpcfx.client.RemoteService;
import io.kimmking.rpcfx.demo.api.User;
import io.kimmking.rpcfx.demo.api.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceStub implements UserService, HttpClientStub {
    @Override
    public String getRemoteUrl() {
        return "http://localhost:8080/";
    }

    @Override
    public String getServiceName() {
        return UserService.class.getName();
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
    public User findById(Integer id) {
        return null;
    }
}
