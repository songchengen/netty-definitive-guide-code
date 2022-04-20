package com.crush.netty.handler.codec;

import com.crush.netty.struct.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/19
 * @version 0.0.1
 */
public class NettyMessageEncode extends MessageToByteEncoder<NettyMessage> {

  private final MarshallingEncode marshallingEncode;

  public NettyMessageEncode() throws IOException {
    this.marshallingEncode = new MarshallingEncode();
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, NettyMessage msg, ByteBuf sendBuf) throws Exception {
    if (msg == null || msg.getHeader() == null) {
      throw  new Exception("the encode message is null");
    }
    // 1. 消息的校验码
    sendBuf.writeInt(msg.getHeader().getCrcCode());
    // 2. 消息的长度
    // 将在所有消息体编码后重新设置，此处占位
    sendBuf.writeInt(msg.getHeader().getLength());
    // 3. 全局ID
    sendBuf.writeLong(msg.getHeader().getSessionID());
    // 4. 消息类型
    sendBuf.writeByte(msg.getHeader().getType());
    // 5. 消息的优先级
    sendBuf.writeByte(msg.getHeader().getPriority());
    // 6. 消息体的长度
    sendBuf.writeInt(msg.getHeader().getAttachment().size());

    String key;
    byte[] keyBytes;
    Object value;
    for (Map.Entry<String, Object> param: msg.getHeader().getAttachment().entrySet()) {
      key = param.getKey();
      keyBytes = key.getBytes("UTF-8");
      sendBuf.writeInt(keyBytes.length);
      sendBuf.writeBytes(keyBytes);
      value = param.getValue();
      marshallingEncode.encode(value, sendBuf);
    }

    key = null;
    keyBytes = null;
    value = null;

    if (msg.getBody() != null) {
      marshallingEncode.encode(msg.getBody(), sendBuf);
    } else {
      sendBuf.writeInt(0);
    }
    int length = sendBuf.readableBytes();
    sendBuf.setInt(4, length - 8);

  }
}
