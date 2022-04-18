/**
 * <p>
 *   Copyright (c) 2021, songchengen. All rights reserved.
 * </p>
 *
 * <p>
 *   MessagePack 序列化框架在netty包中的使用，包括对粘包和拆包的处理。
 * </p>
 *
 * <p>
 *   实现Messagepack解码器{@link com.crush.msgpack.codec.MsgpackDecode} 和
 *   编码器{@link com.crush.msgpack.codec.MsgpackDecode}，
 * </p>
 *
 * <p>
 *   客户端生成指定长度的{@link com.crush.msgpack.pojo.UserInfo}对象数组，
 *   并向Echo服务器{@link com.crush.msgpack.EchoServer}发送该数组，
 *   同时客户端将会增加粘包和拆包的支持：使用{@link  io.netty.handler.codec.LengthFieldBasedFrameDecoder} 和
 *   {@link io.netty.handler.codec.LengthFieldPrepender} 两个编解码器。
 *  </p>
 *
 *  <p>
 *    服务端的处理大致和客户端相同，收到数据时打印出来，并原封返回给客户端
 *  </p>
 *
 *
 *
 * @author songchengen
 * @date 2022/4/18
 * @version 0.0.1
 */
package com.crush.msgpack;

