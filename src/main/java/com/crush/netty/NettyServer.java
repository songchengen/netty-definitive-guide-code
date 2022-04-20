package com.crush.netty;

import com.crush.netty.handler.codec.NettyMessageDecode;
import com.crush.netty.handler.codec.NettyMessageEncode;
import com.crush.netty.handler.server.HeartBeatRespHandler;
import com.crush.netty.handler.server.LoginAuthRespHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/20
 * @version 0.0.1
 */
public class NettyServer {
  public void bind() throws InterruptedException {
    EventLoopGroup boss = new NioEventLoopGroup(1);
    EventLoopGroup worker = new NioEventLoopGroup();

    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(boss, worker)
        .channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_BACKLOG, 100)
        .handler(new LoggingHandler(LogLevel.INFO))
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new NettyMessageDecode(1024 * 1024, 4, 4));

            ch.pipeline().addLast(new NettyMessageEncode());

            ch.pipeline().addLast(new LoginAuthRespHandler());

            ch.pipeline().addLast(new HeartBeatRespHandler());
          }
        });

    ChannelFuture f = bootstrap.bind(NettyConstant.REMOTEIP, NettyConstant.PORT).sync();
    System.out.println("Netty server start ok: " + NettyConstant.REMOTEIP + ":" + NettyConstant.PORT);
  }


  public static void main(String[] args) {
    try {
      new NettyServer().bind();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
