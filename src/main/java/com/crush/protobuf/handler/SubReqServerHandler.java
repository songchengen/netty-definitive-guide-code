package com.crush.protobuf.handler;

import com.crush.protobuf.pojo.SubscribeReqProto;
import com.crush.protobuf.pojo.SubscribeRespProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/18
 * @version 0.0.1
 */
public class SubReqServerHandler extends ChannelInboundHandlerAdapter {

  private SubscribeRespProto.SubscribeResp resp(int subReqId) {
    SubscribeRespProto.SubscribeResp.Builder builder = SubscribeRespProto.SubscribeResp.newBuilder();
    builder.setSubReqId(subReqId);
    builder.setRespCode(0);
    builder.setDesc("Netty book order succeed, 3 days later, sent to the designated address");
    return builder.build();
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    SubscribeReqProto.SubscribeReq req = (SubscribeReqProto.SubscribeReq) msg;

    System.out.println("Server accept client subscribe req id: " + req.getSubReqId());

    ctx.writeAndFlush(resp(req.getSubReqId()));

  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }
}
