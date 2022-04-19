package com.crush.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.Unmarshaller;

import java.io.IOException;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/19
 * @version 0.0.1
 */
public class MarshallingDecoder {
  private final Unmarshaller unmarshaller;

  public MarshallingDecoder() throws IOException {
    unmarshaller = MarshallingCodecFactory.buildUnMarshalling();
  }

  public Object decode(ByteBuf in) throws IOException, ClassNotFoundException {
    int objectSize = in.readInt();

    ByteBuf buf = in.slice(in.readerIndex(), objectSize);
    ChannelBufferByteInput input = new ChannelBufferByteInput(buf);

    try {
      unmarshaller.start(input);
      Object obj = unmarshaller.readObject();
      unmarshaller.finish();
      in.readerIndex(in.readerIndex() + objectSize);
      return obj;
    } finally {
      unmarshaller.close();
    }
  }
}
