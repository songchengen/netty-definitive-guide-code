package com.crush.netty.handler.server;

import com.crush.netty.struct.Header;
import com.crush.netty.struct.MessageType;
import com.crush.netty.struct.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/20
 * @version 0.0.1
 */
public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {

  private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<>();

  private final String[] whiteList = {"127.0.0.1", "localhost", "172.20.150.18"};

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    NettyMessage message = (NettyMessage) msg;
    if (
        message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_REQ.value()
    ) {
      String nodeIndex = ctx.channel().remoteAddress().toString();
      NettyMessage resp;
      // 防重复登录
      if (nodeCheck.containsKey(nodeIndex)) {
        resp = buildByBody((byte) -1);
      } else {
        // 白名单验证
        boolean isOk = false;
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        String ip = address.getAddress().getHostAddress();

        for (String white : whiteList) {
          if (white.equals(ip)) {
            isOk = true;
            break;
          }
        }

        resp = isOk ? buildByBody((byte) 0) : buildByBody((byte) -1);

        if (isOk) {
          nodeCheck.put(nodeIndex, true);
        }

        System.out.println("The client ip is " + ip
            + ", The login response body is " + resp.getBody());
        ctx.writeAndFlush(resp);

      }
    } else {
      ctx.fireChannelRead(msg);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    // 删除缓存
    nodeCheck.remove(ctx.channel().remoteAddress().toString());
    ctx.close();
    ctx.fireExceptionCaught(cause);
  }

  private NettyMessage buildByBody(byte body) {
    NettyMessage message = new NettyMessage();

    Header header = new Header();
    header.setType(MessageType.LOGIN_RESP.value());
    message.setHeader(header);
    message.setBody(body);
    return message;
  }
}
