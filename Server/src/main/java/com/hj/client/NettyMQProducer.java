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
    private static final String host = "127.0.0.1";
    private static final int port = 900;

    private static String queueName = "mayikt";
    private static Channel channel;

    public void init(){
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
                    }
                });
        try {
            // 发起同步连接
            ChannelFuture sync = bootstrap.connect().sync();
            channel = sync.channel();
            sync.channel().closeFuture().sync();
        } catch (Exception e) {

        } finally {
            group.shutdownGracefully();
        }
    }


    public  void sendMsg(String msg) {
        DeliveryInfoEntity deliveryInfoEntity = new DeliveryInfoEntity(msg, queueName,
                true);
        channel.writeAndFlush(deliveryInfoEntity);
    }

    public static void main(String[] args) throws InterruptedException {
        NettyMQProducer mqProducer = new NettyMQProducer();

        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                Thread.sleep(2000);
                mqProducer.sendMsg("每特教育第六期平均突破3万月薪1");
                Thread.sleep(20000);
                mqProducer.sendMsg("每特教育第六期平均突破3万月薪2");
            }
        }).start();
        mqProducer.init();


    }
}
