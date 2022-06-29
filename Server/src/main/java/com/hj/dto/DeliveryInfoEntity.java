package com.hj.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 消息实体类定义【必须实现序列化】
 */
@Data
@AllArgsConstructor
public class DeliveryInfoEntity implements Serializable {

    /**
     * 发送消息内容
     */
    private Object msg;

    /**
     * 队列名称
     */
    private String queueName;
    /**
     * true 生产者投递消息
     * false 消费者获取消息
     */
    private Boolean connType;
    

}
