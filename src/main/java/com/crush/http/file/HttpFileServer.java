package com.crush.http.file;

import com.crush.http.file.handler.HttpFileServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author chengensong
 * @version 0.0.1
 * @date 2022/4/18
 */
public class HttpFileServer {

  static final String LOCAL_HOST = "127.0.0.1";
  private static final String DEFAULT_URL = "/src";

  public void bind(int port, String url) throws InterruptedException {
    EventLoopGroup boss = new NioEventLoopGroup();
    EventLoopGroup worker = new NioEventLoopGroup();

    try {
      ServerBootstrap bootstrap = new ServerBootstrap();

      bootstrap.group(boss, worker)
          .channel(NioServerSocketChannel.class)
          .option(ChannelOption.SO_BACKLOG, 100)
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
             ch.pipeline().addLast(new HttpRequestDecoder());
             ch.pipeline().addLast(new HttpObjectAggregator(65536));

             ch.pipeline().addLast(new HttpResponseEncoder());
             ch.pipeline().addLast(new ChunkedWriteHandler());

             ch.pipeline().addLast(new HttpFileServerHandler(url));
            }
          });

      ChannelFuture f = bootstrap.bind(port).sync();
      System.out.println("HTTP文件目录服务器启动，网址是 : " + LOCAL_HOST + ":"
          + port + url);

      f.channel().closeFuture().sync();
    } finally {
      boss.shutdownGracefully();
      worker.shutdownGracefully();
    }
  }

  public static void main(String[] args) {
    try {
      new HttpFileServer().bind(8088, DEFAULT_URL);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
