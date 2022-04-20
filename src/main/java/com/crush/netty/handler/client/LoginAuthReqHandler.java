package com.crush.netty.handler.client;

import com.crush.netty.struct.Header;
import com.crush.netty.struct.MessageType;
import com.crush.netty.struct.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/20
 * @version 0.0.1
 */
public class LoginAuthReqHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    ctx.writeAndFlush(buildLoginReq(ctx));
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    NettyMessage message = (NettyMessage) msg;
    if (
        message.getHeader() != null &&
        message.getHeader().getType() == MessageType.LOGIN_RESP.value()
    ) {
      byte body = (byte) message.getBody();
      if (body != (byte) 0) {
        System.out.println("登录失败");
        ctx.close();
      } else {
        System.out.println("login is success: " + message);
        ctx.fireChannelRead(msg);
      }
    } else {
      ctx.fireChannelRead(msg);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    ctx.fireExceptionCaught(cause);
  }

  private NettyMessage buildLoginReq(ChannelHandlerContext ctx) {
    NettyMessage message = new NettyMessage();
    Header header = new Header();
    header.setType(MessageType.LOGIN_REQ.value());
    message.setHeader(header);
    return message;
  }


}
