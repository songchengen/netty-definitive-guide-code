package com.crush.msgpack.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/18
 * @version 0.0.1
 */
@ChannelHandler.Sharable
public class MsgpackEncode extends MessageToByteEncoder<Object> {
  @Override
  protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
    MessagePack msgPack = new MessagePack();
    byte[] raw = msgPack.write(msg);
    out.writeBytes(raw);
  }
}
