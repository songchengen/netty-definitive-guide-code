package com.crush.msgpack.pojo;

import org.msgpack.annotation.Message;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author songchengen
 * @date 2022/4/18
 * @version 0.0.1
 */
@Message
public class UserInfo {
  private String userName;
  private int age;

  public UserInfo() {
  }

  public UserInfo(String userName, int age) {
    this.userName = userName;
    this.age = age;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  @Override
  public String toString() {
    return "[" + userName +
        "," + age +
        ']';
  }

  public static UserInfo build(String userName, int age) {
    return new UserInfo(userName, age);
  }
}
