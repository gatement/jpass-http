package net.johnsonlau.jproxy.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import com.jcraft.jsch.Channel;

public class ProxySocketHandler extends Thread {

	private Socket socket;

	public ProxySocketHandler(Socket socket) {
		this.socket = socket;
		ProxyServer.log.info("Creating connection, connection count up to = "
				+ Integer.valueOf(ProxyServer.connectionCount.incrementAndGet()));
	}

	@Override
	public void run() {
		OutputStream clientOutput = null;
		InputStream clientInput = null;
		Socket proxySocket = null;
		InputStream proxyInput = null;
		OutputStream proxyOutput = null;
		Channel sshChannel = null;
		try {
			clientInput = socket.getInputStream();
			clientOutput = socket.getOutputStream();
			StringBuilder headStr = new StringBuilder();

			int inputByte = clientInput.read();
			while (inputByte != -1) {
				headStr.append((char) inputByte);

				// Finish receiving HTTP headers
				if (headStr.length() > 4
						&& headStr.substring(headStr.length() - 4, headStr.length()).equals("\r\n\r\n")) {
					
					ProxyServer.log.info(headStr.toString());

					// Extract HTTP method and target server:
					//   Example1: CONNECT www.example.com:443 HTTP/1.1
					//   Example2: POST http://www.example.com/a/b/c HTTP/1.1
					String[] firstLine = headStr.toString().split("\r\n")[0].split(" ");
					
					// 1. get httpMethod
					String httpMethod = firstLine[0];

					// 2. get targetHost, targetPort
					String hostLine = firstLine[1];
					String targetHost = "";
					int targetPort = 80;
					if (hostLine.toLowerCase().startsWith("http")) {
						String[] host = hostLine.split("://")[1].split("/")[0].split(":");
						targetHost = host[0];
						if (host.length > 1) {
							targetPort = Integer.valueOf(host[1]);
						}
					} else {
						String[] host = hostLine.split(":");
						targetHost = host[0];
						if (host.length > 1) {
							targetPort = Integer.valueOf(host[1]);
						}
					}
					// Connect target server
					ProxyServer.log.info("Connect target " + targetHost + ":" + String.valueOf(targetPort));

					// 3. create proxy channel
					// Use SSH Tunnel to connect remote server
					sshChannel = SshClient.getStreamForwarder(targetHost, targetPort, false);
					proxyInput = sshChannel.getInputStream();
					proxyOutput = sshChannel.getOutputStream();
					// Optional: Connect remote server directly
					// proxySocket = new Socket(targetHost, targetPort);
					// proxyInput = proxySocket.getInputStream();
					// proxyOutput = proxySocket.getOutputStream();

					// 4. response CONNECT or transmit to targetHost
					// Process HTTP Method CONNECT
					if ("CONNECT".equalsIgnoreCase(httpMethod)) {
						// For HTTPS request, consume the initiative HTTP request and send back response
						clientOutput.write("HTTP/1.1 200 Connection Established\r\n\r\n".getBytes());
						clientOutput.flush();
					} else {
						// For HTTP request, transmit the initiative HTTP request
						proxyOutput.write(headStr.toString().getBytes());
						proxyOutput.flush();
					}

					break;
				}

				inputByte = clientInput.read();
			}

			// do the following transmission
			if (sshChannel != null) {
				// New thread continue sending data to target server
				new ProxyStreamingThread(clientInput, proxyOutput).start();

				// Receive target response
				byte[] data = new byte[65536]; // 64KB
				int readCount = proxyInput.read(data);
				while (readCount != -1) {
					clientOutput.write(data, 0, readCount);
					clientOutput.flush();
					readCount = proxyInput.read(data);
				}
			}
		} catch (SocketException ex) {
			// peer closed the socket
		} catch (Exception ex) {
			ProxyServer.log.info("exception: " + ex.getMessage());
			ex.printStackTrace();
		} finally {
			if (proxyInput != null) {
				try {
					proxyOutput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (proxyOutput != null) {
				try {
					proxyOutput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (proxySocket != null) {
				try {
					proxySocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (clientInput != null) {
				try {
					clientInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (clientOutput != null) {
				try {
					clientOutput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (sshChannel != null) {
				try {
					sshChannel.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			ProxyServer.log.info("Closed connection, connection count down to = "
					+ Integer.valueOf(ProxyServer.connectionCount.decrementAndGet()));
		}
	}
}
