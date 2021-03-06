package com.crush.http.file.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/**
 * Copyright (c) 2021, songchengen. All rights reserved.
 *
 * @author chengensong
 * @version 0.0.1
 * @date 2022/4/18
 */
public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

  private final String url;

  public HttpFileServerHandler(String url) {
    this.url = url;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

    // 解码不成功
    if (!request.decoderResult().isSuccess()) {
      sendError(ctx, HttpResponseStatus.BAD_REQUEST);
      return;
    }

    // 只接受 Get请求
    if (request.method() != HttpMethod.GET) {
      sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
      return;
    }

    String uri = request.uri();
    String path = sanitizeUri(uri);

    // 文件路径解析之后如果为空
    if (path == null) {
      sendError(ctx, HttpResponseStatus.FORBIDDEN);
      return;
    }

    File file = new File(path);

    // 文件不存在
    if (file.isHidden() || !file.exists()) {
      sendError(ctx, HttpResponseStatus.NOT_FOUND);
      return;
    }

    // 如果文件为目录
    if (file.isDirectory()) {
      if (uri.endsWith("/")) {
        sendList(ctx, file);
      } else {
        sendRedirect(ctx, uri + '/');
      }
      return;
    }

    // file必须为文件
    if (!file.isFile()) {
      sendError(ctx, HttpResponseStatus.FORBIDDEN);
      return;
    }

    RandomAccessFile randomAccessFile = null;

    try {
      randomAccessFile = new RandomAccessFile(file, "r");
    } catch (FileNotFoundException e) {
      sendError(ctx, HttpResponseStatus.NOT_FOUND);
      return;
    }

    long fileLength = randomAccessFile.length();
    HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);

    HttpUtil.setContentLength(response, fileLength);
    setContentTypeHeader(response, file);

    if (HttpUtil.isKeepAlive(request)) {
      response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
    }

    ctx.write(response);

    ChannelFuture sendFileFuture = ctx.write(new ChunkedFile(randomAccessFile, 0, fileLength, 8192),
        ctx.newProgressivePromise());

    sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
      @Override
      public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) throws Exception {
        if (total < 0) {
          System.err.println("Transfer progress: " + progress);
        } else {
          System.err.println("Transfer progress: " + progress + " / "
              + total);
        }
      }

      @Override
      public void operationComplete(ChannelProgressiveFuture future) throws Exception {
        System.out.println("Transfer complete.");
      }
    });

    ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

    if (!HttpUtil.isKeepAlive(request)) {
      lastContentFuture.addListener(ChannelFutureListener.CLOSE);
    }


  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();

    if (ctx.channel().isActive()) {
      sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private static final Pattern ALLOWED_FILE_NAME = Pattern
      .compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");

  private static void sendList(ChannelHandlerContext ctx, File dir) {
    FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);

    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
    StringBuilder buf = new StringBuilder();

    String dirPath = dir.getPath();
    buf.append("<!DOCTYPE html>\r\n");
    buf.append("<html><head><title>");
    buf.append(dirPath);
    buf.append(" 目录：");
    buf.append("</title></head><body>\r\n");
    buf.append("<h3>");
    buf.append(dirPath).append(" 目录：");
    buf.append("</h3>\r\n");
    buf.append("<ul>");
    buf.append("<li>链接：<a href=\"../\">..</a></li>\r\n");
    for (File f : dir.listFiles()) {
      if (f.isHidden() || !f.canRead()) {
        continue;
      }
      String name = f.getName();
      if (!ALLOWED_FILE_NAME.matcher(name).matches()) {
        continue;
      }
      buf.append("<li>链接：<a href=\"");
      buf.append(name);
      buf.append("\">");
      buf.append(name);
      buf.append("</a></li>\r\n");
    }
    buf.append("</ul></body></html>\r\n");
    ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
    response.content().writeBytes(buffer);
    buffer.release();
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

  }

  private static void sendRedirect(ChannelHandlerContext ctx, String newUri) {
    FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
    response.headers().set(HttpHeaderNames.LOCATION, newUri);
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
  }

  private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");

  private String sanitizeUri(String uri) {
    try {
      uri = URLDecoder.decode(uri, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      try {
        uri = URLDecoder.decode(uri, "ISO-8859-1");
      } catch (UnsupportedEncodingException ex) {
        throw new Error();
      }
    }

    if (!uri.startsWith(url)) {
      return null;
    }
    if (!uri.startsWith("/")) {
      return null;
    }

    uri = uri.replace('/', File.separatorChar);

    if (uri.contains(File.separator + '.')
        || uri.contains('.' + File.separator) || uri.startsWith(".")
        || uri.endsWith(".") || INSECURE_URI.matcher(uri).matches()) {
      return null;
    }

    return System.getProperty("user.dir") + File.separator + uri;
  }

  private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
    FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
        status,
        Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8)
        );

    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
  }

  private void setContentTypeHeader(HttpResponse response, File file) {
    Path path = Paths.get(file.getPath());
    try {
      response.headers().set(HttpHeaderNames.CONTENT_TYPE, Files.probeContentType(path));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
