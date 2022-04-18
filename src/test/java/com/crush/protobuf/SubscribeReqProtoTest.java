package com.crush.protobuf;

import com.crush.protobuf.pojo.SubscribeReqProto;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/18
 * @version 0.0.1
 */
class SubscribeReqProtoTest {
  private static byte[] encode(SubscribeReqProto.SubscribeReq req) {
    return req.toByteArray();
  }

  private static SubscribeReqProto.SubscribeReq decode(byte[] bytes) throws InvalidProtocolBufferException {
    return SubscribeReqProto.SubscribeReq.parseFrom(bytes);
  }

  private static SubscribeReqProto.SubscribeReq createSubscribeReq() {
    SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
    builder.setSubReqId(1);
    builder.setUserName("test");
    builder.setProductName("test");

    List<String> address = new ArrayList<>();
    address.add("Sichuan Chengdu");
    address.add("Beijing");
    address.add("Yunnan Kunming");
    builder.addAllAddress(address);

    return builder.build();
  }

  public static void main(String[] args) throws InvalidProtocolBufferException {
    SubscribeReqProto.SubscribeReq req = createSubscribeReq();

    System.out.println("before encode: " + req.toString());
    SubscribeReqProto.SubscribeReq req2 = decode(encode(req));
    System.out.println("after decode: " + req2.toString());

    System.out.println("Assert equals: --> " + req.equals(req2));

  }
}