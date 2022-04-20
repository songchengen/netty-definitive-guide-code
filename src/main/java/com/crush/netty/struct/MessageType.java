package com.crush.netty.struct;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/20
 * @version 0.0.1
 */
public enum MessageType {

  /**
   * 业务请求消息
   */
  SERVICE_REQ((byte) 0),

  /**
   * 业务响应消息
   */
  SERVICE_RESP((byte) 1),

  /**
   * 业务 ONE WAY 消息
   */
  ONE_WAY((byte) 2),

  /**
   * 登录请求
   */
  LOGIN_REQ((byte) 3),

  /**
   * 登录响应
   */
  LOGIN_RESP((byte) 4),

  /**
   * 心跳请求
   */
  HEARTBEAT_REQ((byte) 5),

  /**
   * 心跳响应
   */
  HEARTBEAT_RESP((byte) 6),
  ;

  private final byte value;

  private MessageType(byte value) {
    this.value = value;
  }

  public byte value() {
    return value;
  }
}
