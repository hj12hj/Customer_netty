package com.example.web;

import com.hj.client.NettyMQProducer;
import com.hj.core.MqNettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

    @Autowired
    NettyMQProducer nettyMQProducer;

    @Override
    public void run(String... args) throws Exception {
        new MqNettyServer().start();
        Thread.sleep(1000);
        nettyMQProducer.init();
    }
}
