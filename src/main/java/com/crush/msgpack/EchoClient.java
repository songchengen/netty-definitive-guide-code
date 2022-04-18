package com.crush.msgpack;

import com.crush.msgpack.codec.MsgpackDecode;
import com.crush.msgpack.codec.MsgpackEncode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/18
 * @version 0.0.1
 */
public class EchoClient {

  static int SEND_TIMES = 100;

  public void connect(String host, int port) throws InterruptedException {
    EventLoopGroup group = new NioEventLoopGroup();

    try {
      Bootstrap bootstrap = new Bootstrap();
      bootstrap.group(group)
          .channel(NioSocketChannel.class)
          .option(ChannelOption.TCP_NODELAY, true)
          .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              ch.pipeline().addLast("frame decoder ",
                  new LengthFieldBasedFrameDecoder(1024,
                      0, 2, 0, 2)
              );
              ch.pipeline().addLast("msg decoder", new MsgpackDecode());
              ch.pipeline().addLast("frame encoder", new LengthFieldPrepender(2));
              ch.pipeline().addLast("msg encoder", new MsgpackEncode());
              ch.pipeline().addLast(new EchoClientHandler(SEND_TIMES));
            }
          });
      bootstrap.connect(host, port).sync().channel().closeFuture().sync();
    } finally {
      group.shutdownGracefully();
    }
  }

  public static void main(String[] args) {
    try {
      new EchoClient().connect("127.0.0.1", 8088);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
