package com.hj.core;

import com.hj.dto.DeliveryInfoEntity;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.*;

/**
 * server消息处理器
 */
public class ServerHandler extends SimpleChannelInboundHandler<DeliveryInfoEntity> {

    /**
     * 客户端列表
     */
    private static Map<String, List<ChannelHandlerContext>> ctxs = new HashMap<String, List<ChannelHandlerContext>>();
    /**
     * 消息列表
     */
    private static Map<String, Queue> queueMap = new HashMap<String, Queue>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DeliveryInfoEntity mqEntity) throws Exception {
        String queueName = mqEntity.getQueueName();
        if (queueName == null || "".equals(queueName)) {
            return;
        }
        boolean isProducer = mqEntity.getConnType();
        if (isProducer) {
            producer(mqEntity);
            return;
        }
        consumer(ctx, mqEntity);
    }

    /**
     * 发送给消费者具体操作
     * 1、保存客户端队列信息
     * 2、如果队列中有值，则主动拉取
     */
    private void consumer(ChannelHandlerContext ctx, DeliveryInfoEntity mqEntity) {
        String queueName = mqEntity.getQueueName();
        Queue queue = queueMap.get(queueName);
        List<ChannelHandlerContext> channelHandlerContexts = ctxs.get(queueName);
        if (channelHandlerContexts == null) {
            channelHandlerContexts = new ArrayList<ChannelHandlerContext>();
        }
        channelHandlerContexts.add(ctx);
        ctxs.put(queueName, channelHandlerContexts);
        if (queue == null || queue.isEmpty()) {
            System.out.println("当前队列没有数据，直接返回");
            return;
        }
        ctx.writeAndFlush(queue.poll());
    }

    /**
     * 生产者具体操作
     * 1、如果队列不存在的话创建队列并加入数据
     * 2、设置队列map
     * 3、如果已经存在了客户端列表，则主动推送
     */
    private void producer(DeliveryInfoEntity mqEntity) {
        String queueName = mqEntity.getQueueName();
        Queue queue = queueMap.get(queueName);
        if (queue == null) {
            queue = new LinkedList();
        }
        queue.offer(mqEntity);
        queueMap.put(queueName, queue);
        List<ChannelHandlerContext> channelHandlerContexts = ctxs.get(queueName);
        if (channelHandlerContexts != null && channelHandlerContexts.size() > 0) {
            for (ChannelHandlerContext ctx : channelHandlerContexts) {
                ctx.writeAndFlush(queue.poll());
            }
        }
    }
}