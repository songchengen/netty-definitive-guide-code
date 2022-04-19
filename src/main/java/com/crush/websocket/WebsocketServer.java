package com.crush.websocket;

import com.crush.websocket.handler.WebsocketServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/19
 * @version 0.0.1
 */
public class WebsocketServer {
  public void bind(int port) throws InterruptedException {
    EventLoopGroup boss = new NioEventLoopGroup(1);
    EventLoopGroup worker = new NioEventLoopGroup();
    try {
      ServerBootstrap bootstrap = new ServerBootstrap();
      bootstrap.group(boss, worker)
          .channel(NioServerSocketChannel.class)
          .option(ChannelOption.SO_BACKLOG, 100)
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              ch.pipeline().addLast("http-codec", new HttpServerCodec());
              ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
              ch.pipeline().addLast("http chunked", new ChunkedWriteHandler());

              ch.pipeline().addLast(new WebsocketServerHandler());
            }
          });

      ChannelFuture f = bootstrap.bind(port).sync();
      System.out.println("Websocket server started on port " + port);
      System.out.println("Open your web browser and navigate to http://localhost:" + port + "/");
      f.channel().closeFuture().sync();

    } finally {
      boss.shutdownGracefully();
      worker.shutdownGracefully();
    }
  }

  public static void main(String[] args) {
    try {
      new WebsocketServer().bind(8088);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
