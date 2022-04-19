package com.crush.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/19
 * @version 0.0.1
 */
public class NettyMessageDecode extends LengthFieldBasedFrameDecoder {

  private final MarshallingDecoder decoder;

  public NettyMessageDecode(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) throws IOException {
    super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    decoder = new MarshallingDecoder();
  }

  @Override
  protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
    ByteBuf frame = (ByteBuf) super.decode(ctx, in);

    if (frame == null) {
      return null;
    }

    NettyMessage message = new NettyMessage();
    Header header = new Header();
    header.setCrcCode(frame.readInt());
    header.setLength(frame.readInt());
    header.setSessionID(frame.readLong());
    header.setType(frame.readByte());
    header.setPriority(frame.readByte());

    int attachmentSize = frame.readInt();

    if (attachmentSize > 0) {
      Map<String, Object> attachment = new HashMap<>(attachmentSize);
      int keySize;
      String key;
      byte[] keyArray;
      while (attachmentSize --> 0) {
        keySize = frame.readInt();
        keyArray = new byte[keySize];
        frame.readBytes(keyArray);
        key = new String(keyArray, "UTF-8");
        attachment.put(key, decoder.decode(frame));
      }
    }

    message.setHeader(header);
    // 剩下前四个字节表示body的长度
    if (frame.readableBytes() > 4) {
      message.setBody(decoder.decode(frame));
    }

    return message;
  }
}
