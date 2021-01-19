package com.javacamp.demo;

import com.javacamp.demo.jms.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.Resource;
import java.util.List;

@SpringBootApplication
public class ActivemqDemoApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ActivemqDemoApplication.class, args);
    }

}
