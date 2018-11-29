package net.johnsonlau.jproxy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class ProxyHandler extends ChannelInboundHandlerAdapter {
	StringBuilder headStr = new StringBuilder();

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		System.out.println(Thread.currentThread().getName() + ": handlerRemoved");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		ByteBuf in = (ByteBuf) msg;
		try {
			while (in.isReadable()) {
				headStr.append((char) in.readByte());

				// Finish receiving HTTP headers
				if (headStr.length() > 4
						&& headStr.substring(headStr.length() - 4, headStr.length()).equals("\r\n\r\n")) {
					System.out.println(Thread.currentThread().getName() + ": ");
					System.out.println(headStr.toString());

					// Extract host name and port
					for (String headLine : headStr.toString().split("\r\n")) {
						String headName = headLine.substring(0, headLine.indexOf(" "));
						// Example1: "Host: www.example.com"
						// Example2: "Host: www.example.com:443"
						if ("Host:".equals(headName)) {
							String[] host = headLine.split(" ")[1].split(":");
							String hostname = host[0];
							int port = 80;
							if (host.length > 1) {
								port = Integer.valueOf(host[1]);
							}
							System.out.println("hostname: " + hostname + ", port: " + String.valueOf(port));
						}
					}

					// Extract HTTP Method
					String type = headStr.substring(0, headStr.indexOf(" "));
					if ("CONNECT".equals(type)) {
						System.out.println("start connect");
					}
				}
			}
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}