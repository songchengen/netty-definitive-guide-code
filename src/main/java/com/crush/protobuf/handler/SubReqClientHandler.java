package com.crush.protobuf.handler;

import com.crush.protobuf.pojo.SubscribeReqProto;
import com.crush.protobuf.pojo.SubscribeRespProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/18
 * @version 0.0.1
 */
public class SubReqClientHandler extends ChannelInboundHandlerAdapter {

  static final int REQ_TIMES = 100;

  private SubscribeReqProto.SubscribeReq req(int subReqId) {
    SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
    builder.setSubReqId(subReqId);
    builder.setUserName("songchengen");
    builder.setProductName("Netty Book");
    List<String> address = new ArrayList<>(2);
    address.add("NanJing");
    address.add("BeiJing");
    builder.addAllAddress(address);
    return builder.build();
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    for (int i = 0; i < REQ_TIMES; ++i) {
      ctx.writeAndFlush(req(i));
    }
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    SubscribeRespProto.SubscribeResp resp = (SubscribeRespProto.SubscribeResp) msg;

    System.out.println("Client Receive subreq id: " + resp.getSubReqId());
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
