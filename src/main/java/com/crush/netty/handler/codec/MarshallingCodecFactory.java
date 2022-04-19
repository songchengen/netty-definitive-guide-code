package com.crush.netty.handler.codec;

import org.jboss.marshalling.*;

import java.io.IOException;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/19
 * @version 0.0.1
 */
public class MarshallingCodecFactory {

  protected static Marshaller buildMarshalling() throws IOException {
    final MarshallerFactory marshallerFactory = Marshalling
        .getProvidedMarshallerFactory("serial");
    final MarshallingConfiguration configuration = new MarshallingConfiguration();
    configuration.setVersion(5);
    return marshallerFactory
        .createMarshaller(configuration);
  }

  protected static Unmarshaller buildUnMarshalling() throws IOException {
    final MarshallerFactory marshallerFactory = Marshalling
        .getProvidedMarshallerFactory("serial");
    final MarshallingConfiguration configuration = new MarshallingConfiguration();
    configuration.setVersion(5);
    return marshallerFactory
        .createUnmarshaller(configuration);
  }
}
