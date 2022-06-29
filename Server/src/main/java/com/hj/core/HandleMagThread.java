package com.hj.core;

import io.netty.channel.ChannelHandlerContext;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Map;
import java.util.Queue;

public class HandleMagThread extends Thread {



    @SneakyThrows
    @Override
    public void run() {

        for (;;){
            Thread.sleep(500);
            ServerHandler.consumerMap.keySet().stream().forEach(item->{
                if (ServerHandler.msgMap.containsKey(item)) {
                    Queue queue = ServerHandler.msgMap.get(item);
                    if (queue != null&&queue.size()>0) {
                        ServerHandler.consumerMap.get(item).forEach(it->it.writeAndFlush(queue.poll()));
                    }
                }
            });
        }



    }
}
