package com.hj.client;

import com.hj.core.MarshallingCodeCFactory;
import com.hj.dto.DeliveryInfoEntity;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.SneakyThrows;

import java.net.InetSocketAddress;

public class NettyMQProducer {
    private final String host = "127.0.0.1";
    private final int port = 900;
    private String queueName = "mayikt";


    private NioEventLoopGroup group;
    private ChannelFuture channelFuture;

    public void init() {
        //创建nioEventLoopGroup
        group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress(host, port))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                        ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                    }
                });
        try {
            // 发起同步连接
            channelFuture = bootstrap.connect().sync();
        } catch (Exception e) {

        } finally {
        }
    }

    public void sendMsg(String msg) {
        DeliveryInfoEntity deliveryInfoEntity = new DeliveryInfoEntity(msg, queueName,
                true);
        channelFuture.channel().writeAndFlush(deliveryInfoEntity);
    }


    public void close() {
        group.shutdownGracefully();
    }


}
