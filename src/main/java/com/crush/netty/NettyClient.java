package com.crush.netty;

import com.crush.netty.handler.client.HeartBeatReqHandler;
import com.crush.netty.handler.client.LoginAuthReqHandler;
import com.crush.netty.handler.codec.NettyMessageDecode;
import com.crush.netty.handler.codec.NettyMessageEncode;
import com.crush.netty.handler.server.LoginAuthRespHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/20
 * @version 0.0.1
 */
public class NettyClient {

  private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
  private EventLoopGroup group = new NioEventLoopGroup();

  public void connect(String host, int port) throws InterruptedException {
    try {
      Bootstrap bootstrap = new Bootstrap();
      bootstrap.group(group)
          .channel(NioSocketChannel.class)
          .option(ChannelOption.TCP_NODELAY, true)
          .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {

              // 消息解码器
              ch.pipeline().addLast(
                  new NettyMessageDecode(1024 * 1024, 4, 4));

              // 消息编码器
              ch.pipeline().addLast(new NettyMessageEncode());

              ch.pipeline().addLast(new ReadTimeoutHandler(50));

              // 发起登录
              ch.pipeline().addLast(new LoginAuthReqHandler());

              // 发起心跳
              ch.pipeline().addLast(new HeartBeatReqHandler());
            }
          });

      ChannelFuture f = bootstrap.connect(
          new InetSocketAddress(host, port),
          new InetSocketAddress(NettyConstant.LOCALIP, NettyConstant.LOCAL_PORT)
      ).sync();

      f.channel().closeFuture().sync();
    } finally {

      // 断线后重新连接
      executor.execute(() -> {
        try {
          TimeUnit.SECONDS.sleep(5);
          System.out.println("client reconnect");
          connect(NettyConstant.REMOTEIP, NettyConstant.PORT);

        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
    }
  }

  public static void main(String[] args) {
    try {
      new NettyClient().connect(NettyConstant.REMOTEIP, NettyConstant.PORT);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
