package com.crush.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.ByteOutput;

import java.io.IOException;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/19
 * @version 0.0.1
 */
public class ChannelBufferByteOutput implements ByteOutput {


  private final ByteBuf byteBuf;

  public ChannelBufferByteOutput(ByteBuf byteBuf) {
    this.byteBuf = byteBuf;
  }

  public ByteBuf getByteBuf() {
    return byteBuf;
  }

  @Override
  public void write(int i) throws IOException {
    byteBuf.writeByte(i);
  }

  @Override
  public void write(byte[] bytes) throws IOException {
    byteBuf.writeBytes(bytes);
  }

  @Override
  public void write(byte[] bytes, int i, int i1) throws IOException {
    byteBuf.writeBytes(bytes, i, i1);
  }

  @Override
  public void close() throws IOException {

  }

  @Override
  public void flush() throws IOException {

  }
}
