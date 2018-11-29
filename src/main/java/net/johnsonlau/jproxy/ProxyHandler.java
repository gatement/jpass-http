package net.johnsonlau.jproxy;

import com.jcraft.jsch.Channel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class ProxyHandler extends ChannelInboundHandlerAdapter {
	StringBuilder headStr = new StringBuilder();
	Channel sshChannel = null;
	String targetHost = null;
	int targetPort = 80;

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		cleanUp();
		//System.out.println(Thread.currentThread().getName() + ": handlerRemoved");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf in = (ByteBuf) msg;
		try {
			while (in.isReadable()) {
				if (sshChannel == null) {
					headStr.append((char) in.readByte());

					// Finish receiving HTTP headers
					if (headStr.length() > 4
							&& headStr.substring(headStr.length() - 4, headStr.length()).equals("\r\n\r\n")) {
						//System.out.println(Thread.currentThread().getName() + ": ");
						//System.out.println(headStr.toString());

						// Extract host name and port
						for (String headLine : headStr.toString().split("\r\n")) {
							String headName = headLine.substring(0, headLine.indexOf(" "));
							// Example1: "Host: www.example.com"
							// Example2: "Host: www.example.com:443"
							if ("Host:".equals(headName)) {
								String[] host = headLine.split(" ")[1].split(":");
								targetHost = host[0];
								if (host.length > 1) {
									targetPort = Integer.valueOf(host[1]);
								}
							}
						}

						// Connect target host
						System.out.println("Connect target " + targetHost + ":" + String.valueOf(targetPort));
						sshChannel = SshClient.initChannel(targetHost, targetPort);

						// Extract HTTP Method
						String type = headStr.substring(0, headStr.indexOf(" "));
						if ("CONNECT".equals(type)) {
							// send back connect response
							byte[] connectResponse = "HTTP/1.1 200 Connection Established\r\n\r\n".getBytes();
							ctx.writeAndFlush(ctx.alloc().buffer(connectResponse.length).writeBytes(connectResponse));
						} else {
							// transmit the read bytes
							sshChannel.getOutputStream().write(headStr.toString().getBytes());
						}
					}

				} else {
					// Send remaining bytes to target
					sshChannel.getOutputStream().write(in.readByte());
				}
			}

			// Receive target response
			int b = sshChannel.getInputStream().read();
			while (b != -1) {
				ctx.writeAndFlush(ctx.alloc().buffer().writeByte(b));
				b = sshChannel.getInputStream().read();
			}
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
		cleanUp();
	}

	private void cleanUp() {
		try {
			if (sshChannel != null) {
				sshChannel.disconnect();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}