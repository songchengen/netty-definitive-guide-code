package com.crush.echo.ch1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/18
 * @version 0.0.1
 */
public class EchoServer {
  public void bind(int port) throws InterruptedException {
    EventLoopGroup boss = new NioEventLoopGroup(1);
    EventLoopGroup worker = new NioEventLoopGroup();
    try {
      ServerBootstrap bootstrap = new ServerBootstrap();
      bootstrap.group(boss, worker)
          .channel(NioServerSocketChannel.class)
          .option(ChannelOption.SO_BACKLOG, 1024)
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {

              ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());

              ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
              ch.pipeline().addLast(new StringDecoder());
              ch.pipeline().addLast(new EchoServerHandler());
            }
          });

      bootstrap.bind(port).sync().channel().closeFuture().sync();
    } finally {
      boss.shutdownGracefully();
      worker.shutdownGracefully();
    }
  }

  public static void main(String[] args) {
    try {
      new EchoServer().bind(8088);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
