package com.crush.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.ByteInput;

import java.io.IOException;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/19
 * @version 0.0.1
 */
public class ChannelBufferByteInput implements ByteInput {

  private final ByteBuf buf;

  public ChannelBufferByteInput(ByteBuf buf) {
    this.buf = buf;
  }

  public ByteBuf getBuf() {
    return buf;
  }

  @Override
  public int read() throws IOException {

    if (buf.isReadable()) {
      return buf.readByte() & 0xff;
    }

    return -1;
  }

  @Override
  public int read(byte[] bytes) throws IOException {
    return read(bytes, 0, bytes.length);
  }

  @Override
  public int read(byte[] bytes, int i, int i1) throws IOException {

    int available = available();
    if (available == 0) {
      return -1;
    }

    i1 = Math.min(i1, available);
    buf.readBytes(bytes, i, i1);

    return i1;
  }

  @Override
  public int available() throws IOException {
    return buf.readableBytes();
  }

  @Override
  public long skip(long bytes) throws IOException {
    int readable = buf.readableBytes();
    if (readable < bytes) {
      bytes = readable;
    }

    buf.readerIndex((int) (buf.readerIndex() + bytes));

    return bytes;
  }

  @Override
  public void close() throws IOException {

  }
}
