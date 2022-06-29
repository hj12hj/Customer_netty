package com.hj.core;

import com.hj.dto.DeliveryInfoEntity;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * server消息处理器
 */
public class ServerHandler extends SimpleChannelInboundHandler<DeliveryInfoEntity> {

    /**
     * 监听列表   主题加客户端
     */
    public static Map<String, List<ChannelHandlerContext>> consumerMap = new ConcurrentHashMap<>();
    /**
     * 消息列表   主题加消息
     */
    public static Map<String, Queue> msgMap = new ConcurrentHashMap();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DeliveryInfoEntity mqEntity) throws Exception {
        String queueName = mqEntity.getQueueName();
        if (queueName == null || "".equals(queueName)) {
            return;
        }
        boolean isProducer = mqEntity.getConnType();
        if (isProducer) {
            producerMsg(mqEntity);
        } else {
            registerConsumer(ctx, mqEntity);
        }
    }

    /**
     * 注册消费者
     */
    private void registerConsumer(ChannelHandlerContext ctx, DeliveryInfoEntity mqEntity) {
        String queueName = mqEntity.getQueueName();
        consumerMap.putIfAbsent(mqEntity.getQueueName(), new ArrayList<>());
        consumerMap.get(mqEntity.getQueueName()).add(ctx);
    }

    /**
     * 存储推送的消息
     */
    private void producerMsg(DeliveryInfoEntity mqEntity) {
        String queueName = mqEntity.getQueueName();
        msgMap.putIfAbsent(mqEntity.getQueueName(), new LinkedList());
        msgMap.get(mqEntity.getQueueName()).add(mqEntity);
    }
}