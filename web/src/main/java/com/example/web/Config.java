package com.example.web;

import com.hj.client.NettyMQProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    NettyMQProducer getNettyMQProducer(){
        return new NettyMQProducer();
    }

}
