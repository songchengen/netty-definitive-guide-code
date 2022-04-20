package com.crush.netty.handler.server;

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
public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    NettyMessage message = (NettyMessage) msg;

    if (
        message.getHeader() != null &&
        message.getHeader().getType() == MessageType.HEARTBEAT_REQ.value()
    ) {
      // 接受心跳请求
      System.out.println("receive client heart beat message: ----->" + message);
      NettyMessage heartBeat = buildHeartBeatRespMessage();
      System.out.println("send heart beat message to client: ----->" + heartBeat);
      ctx.writeAndFlush(heartBeat);
    } else {
      ctx.fireChannelRead(msg);
    }
  }

  private NettyMessage buildHeartBeatRespMessage() {
    NettyMessage message = new NettyMessage();
    Header header = new Header();
    header.setType(MessageType.HEARTBEAT_RESP.value());
    message.setHeader(header);
    return message;
  }
}
