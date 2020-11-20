package gateway.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class BeanConf {

    @Scope("prototype")
    @Bean
    public ExecutorService threadPool() {
        int cores = Runtime.getRuntime().availableProcessors() * 2;
        return Executors.newFixedThreadPool(cores);
    }
}
