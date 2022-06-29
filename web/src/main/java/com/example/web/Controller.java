package com.example.web;

import com.hj.client.NettyMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
public class Controller {

    @Autowired
    NettyMQProducer producer;

    @PostConstruct
    public void init(){
        producer.init();
    }

    @GetMapping("/test")
    public void test() {
        producer.sendMsg("111");
    }

}
