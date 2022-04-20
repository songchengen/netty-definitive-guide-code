package com.crush.netty.handler.client;

import com.crush.netty.struct.Header;
import com.crush.netty.struct.MessageType;
import com.crush.netty.struct.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/20
 * @version 0.0.1
 */
public class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {

  private volatile ScheduledFuture<?> heartBeat;

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    NettyMessage message = (NettyMessage) msg;


    if (
        message.getHeader() != null &&
        message.getHeader().getType() == MessageType.LOGIN_RESP.value()
    ) {
      // 登录之后，启动心跳定时器
      heartBeat = ctx.executor()
          .scheduleAtFixedRate(new HeadBeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);
    } else if (
        message.getHeader() != null &&
        message.getHeader().getType() == MessageType.HEARTBEAT_RESP.value()
    ) {
      // 收到心跳响应
      System.out.println("Client receive server heart beat message: ---->" + message);
    } else {
      ctx.fireChannelRead(msg);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    cause.printStackTrace();
    if (heartBeat != null) {
      heartBeat.cancel(true);
      heartBeat = null;
    }
    ctx.fireExceptionCaught(cause);
  }

  private static class HeadBeatTask implements Runnable {

    private final ChannelHandlerContext ctx;
    private final NettyMessage message;

    public HeadBeatTask(ChannelHandlerContext ctx) {
      this.ctx = ctx;
      this.message = buildHeartBeatRespMessage();
    }

    @Override
    public void run() {
      System.out.println("Client send heart beat message: ---->" + message);
      ctx.writeAndFlush(message);
    }

    private NettyMessage buildHeartBeatRespMessage() {
      NettyMessage message = new NettyMessage();
      Header header = new Header();
      header.setType(MessageType.HEARTBEAT_REQ.value());
      message.setHeader(header);
      return message;
    }
  }
}
