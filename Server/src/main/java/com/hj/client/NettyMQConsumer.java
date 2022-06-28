package com.hj.client;

import com.hj.core.MarshallingCodeCFactory;
import com.hj.dto.DeliveryInfoEntity;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class NettyMQConsumer {
    private static final String host = "127.0.0.1";
    private static final int port = 900;

    private static String queueName = "mayikt";

    public static void sendMsg(String msg) {
        //创建nioEventLoopGroup
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress(host, port))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                        ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                        ch.pipeline().addLast(new NettyMQConsumerHandler());
                    }
                });
        try {
            // 发起同步连接
            ChannelFuture sync = bootstrap.connect().sync();
            DeliveryInfoEntity deliveryInfoEntity = new DeliveryInfoEntity(null, queueName,
                    false);
            sync.channel().writeAndFlush(deliveryInfoEntity);
            sync.channel().closeFuture().sync();
        } catch (Exception e) {

        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args){
        sendMsg(null);
    };
}
