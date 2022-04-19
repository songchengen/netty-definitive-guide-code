package com.crush.websocket.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;
import java.util.Date;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/19
 * @version 0.0.1
 */
public class WebsocketServerHandler extends SimpleChannelInboundHandler<Object> {

  private WebSocketServerHandshaker handshaker;

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (msg instanceof FullHttpRequest) {
      handleHttpRequest(ctx, (FullHttpRequest) msg);
    } else if (msg instanceof WebSocketFrame) {
      handleWebSocketFrame(ctx, (WebSocketFrame) msg);
    }
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

  private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {

    // 如果HTTP解码失败，返回HTTP异常
    if (
        !req.decoderResult().isSuccess() ||
            (!"websocket".equals(req.headers().get("Upgrade")))
    ) {
      sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
      return;
    }

    WebSocketServerHandshakerFactory wsFactory =
        new WebSocketServerHandshakerFactory("ws://localhost:8088/websocket",
            null, false);
    handshaker = wsFactory.newHandshaker(req);
    if (handshaker == null) {
      WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
    } else {
      handshaker.handshake(ctx.channel(), req);
    }
  }


  private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
    if (frame instanceof CloseWebSocketFrame) {
      handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
      return;
    }
    if (frame instanceof PingWebSocketFrame) {
      ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
      return;
    }

    if (!(frame instanceof TextWebSocketFrame)) {
      throw new UnsupportedOperationException(
          String.format("%s frame types not supported", frame.getClass().getName())
      );
    }

    String request = ((TextWebSocketFrame) frame).text();
    System.out.println("request: " + request);
    ctx.channel().write(new TextWebSocketFrame(
        request + "， 欢迎使用Netty WebSocket服务器， Now time: " +
            new Date()
    ));
  }

  private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
    if (res.status().code() != 200) {
      ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
      res.content().writeBytes(buf);
      buf.release();
      HttpUtil.setContentLength(res, res.content().readableBytes());
    }

    ChannelFuture f = ctx.channel().writeAndFlush(res);
    if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
      f.addListener(ChannelFutureListener.CLOSE);
    }
  }
}
