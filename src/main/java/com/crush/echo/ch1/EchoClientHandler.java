package com.crush.echo.ch1;

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
public class EchoClientHandler extends ChannelInboundHandlerAdapter {
  static final String ECHO_REQ = "Hi, Welcome to Netty.$_";
  static final int QUERY_TIMES = 100;

  private int counter;

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
   for (int i = 0; i < QUERY_TIMES; ++i) {
      ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQ.getBytes()));
   }
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    System.out.println("This is " + ++counter + " times receive client: [" + msg + "]");
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    ctx.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }
}
