package com.crush.timer.ch1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/18
 * @version 0.0.1
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {
  final static int QUERY_TIMES = 100;

  private int counter;
  private byte[] bytes;

  public TimeClientHandler() {
    bytes = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes();
  }


  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    ByteBuf buf;
    for (int i = 0; i < QUERY_TIMES; ++i) {
      buf = Unpooled.buffer(bytes.length);
      buf.writeBytes(bytes);
      ctx.writeAndFlush(buf);
    }
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    String body = (String) msg;

    System.out.println("Now is : " + body + " ; the counter is : " + ++counter);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }
}
