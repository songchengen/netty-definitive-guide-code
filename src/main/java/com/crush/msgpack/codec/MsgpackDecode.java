package com.crush.msgpack.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/18
 * @version 0.0.1
 */
@ChannelHandler.Sharable
public class MsgpackDecode extends MessageToMessageDecoder<ByteBuf> {
  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
    final byte[] bytes = new byte[msg.readableBytes()];
    msg.getBytes(msg.readerIndex(), bytes, 0, bytes.length);
    MessagePack messagePack = new MessagePack();
    out.add(messagePack.read(bytes));

  }
}
