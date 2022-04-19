package com.crush.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.Marshaller;

import java.io.IOException;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/19
 * @version 0.0.1
 */
public class MarshallingEncode {
  private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

  Marshaller marshaller;

  public MarshallingEncode() throws IOException {
    this.marshaller = MarshallingCodecFactory.buildMarshalling();
  }

  protected void encode(Object msg, ByteBuf out) throws IOException {
    try {
      // 保存NettyMessage起始的位置，同时也是存储消息长度的位置
      int lengthPos = out.writerIndex();

      // 保存消息长度的占位符
      // 消息长度编码之后才能确认
      out.writeBytes(LENGTH_PLACEHOLDER);
      ChannelBufferByteOutput output = new ChannelBufferByteOutput(out);
      marshaller.start(output);
      marshaller.writeObject(msg);
      marshaller.finish();
      // 设置消息长度
      out.setInt(lengthPos, out.writerIndex() - lengthPos - 4);
    } finally {
      marshaller.close();
    }

  }
}
