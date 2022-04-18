package com.crush.msgpack;

import com.crush.msgpack.pojo.UserInfo;
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
  private final int sendTimes;

  public EchoClientHandler(int sendTimes) {
    this.sendTimes = sendTimes;
  }

  private UserInfo[] userInfos() {
    UserInfo[] userInfos = new UserInfo[sendTimes];
    for (int i = 0; i < sendTimes; ++i) {
      userInfos[i] = UserInfo.build("ABCDEFG ---->" + i, i + 1);
    }

    return userInfos;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    UserInfo[] userInfos = userInfos();
    for (int i = 0; i < sendTimes; ++i) {
      ctx.write(userInfos[i]);
    }
    ctx.flush();
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    System.out.println("Client receive this msgpack message: " + msg);
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
