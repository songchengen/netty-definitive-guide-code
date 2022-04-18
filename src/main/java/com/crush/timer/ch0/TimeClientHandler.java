package com.crush.timer.ch0;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/18
 * @version 0.0.1
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {

  private final ByteBuf message;

  public TimeClientHandler() {
    byte[] req = "QUERY TIME ORDER".getBytes();
    message = Unpooled.buffer(req.length);
    message.writeBytes(req);
  }


  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    ctx.writeAndFlush(message);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf buf = (ByteBuf) msg;
    byte[] req = new byte[buf.readableBytes()];

    buf.readBytes(req);

    String body = new String(req, StandardCharsets.UTF_8);
    System.out.println("Now is : " + body);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    System.err.println("Unexpected exception from downstream : " + cause.getMessage());

    ctx.close();
  }
}
