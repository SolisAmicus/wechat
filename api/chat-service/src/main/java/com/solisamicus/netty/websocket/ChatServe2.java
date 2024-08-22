package com.solisamicus.netty.websocket;

import com.solisamicus.netty.rabbitmq.RabbitMQConnectUtils;
import com.solisamicus.netty.zookeeper.ZookeeperRegister;
import com.solisamicus.utils.IPUtils;
import com.solisamicus.utils.JedisPoolUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ChatServe2 {
    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        String ip = IPUtils.getIP();
        Integer port = JedisPoolUtils.selectPort();
        ZookeeperRegister.register(ip, port);
        String queueName = "netty_queue_" + port;
        RabbitMQConnectUtils rabbitMQConnectUtils = new RabbitMQConnectUtils();
        rabbitMQConnectUtils.listen(queueName, "fanout_exchange");
        ServerBootstrap server = new ServerBootstrap();
        server.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new WebSocketChannelInitializer());
        try {
            ChannelFuture channelFuture = server.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
