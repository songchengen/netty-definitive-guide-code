package com.crush.timer.ch0;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/18
 * @version 0.0.1
 */
public class TimeClient {
  public void connect(String host, Integer port) throws InterruptedException {
    EventLoopGroup group = new NioEventLoopGroup();

    try {
      Bootstrap bootstrap = new Bootstrap();

      bootstrap.group(group)
          .option(ChannelOption.TCP_NODELAY, true)
          .channel(NioSocketChannel.class)
          .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              ch.pipeline().addLast(new TimeClientHandler());
            }
          });

      ChannelFuture f = bootstrap.connect(host, port).sync();
      f.channel().closeFuture().sync();
    } finally {
      group.shutdownGracefully();
    }
  }

  public static void main(String[] args) {
    try {
      new TimeClient().connect("127.0.0.1", 8088);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
