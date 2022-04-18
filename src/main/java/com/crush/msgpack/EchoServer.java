package com.crush.msgpack;

import com.crush.msgpack.codec.MsgpackDecode;
import com.crush.msgpack.codec.MsgpackEncode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

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

              ch.pipeline().addLast("frame decoder ",
                  new LengthFieldBasedFrameDecoder(1024,
                      0, 2, 0, 2)
              );
              ch.pipeline().addLast("msg decoder", new MsgpackDecode());
              ch.pipeline().addLast("frame encoder", new LengthFieldPrepender(2));
              ch.pipeline().addLast("msg encoder", new MsgpackEncode());
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
