package com.hj.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 消息实体类定义【必须实现序列化】
 */
@Data
public class DeliveryInfoEntity implements Serializable {

    /**
     * 发送消息内容
     */
    private String msg;

    /**
     * 队列名称
     */
    private String queueName;
    /**
     * true 生产者投递消息
     * false 消费者获取消息
     */
    private Boolean connType;

    public DeliveryInfoEntity(String msg, String queueName, Boolean connType) {
        this.msg = msg;
        this.queueName = queueName;
        this.connType = connType;
    }
}
