package net.johnsonlau.jproxy;

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
		Util.log("Creating connection, connection count up to = "
				+ Integer.valueOf(Util.connectionCount.incrementAndGet()));
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
					// Util.log(headStr.toString());
					// Util.printBytes(headStr.toString().getBytes());

					// Extract HTTP method and target server
					// Example1: CONNECT www.example.com:443 HTTP/1.1
					// Example2: POST http://www.example.com/a/b/c HTTP/1.1
					String[] firstLine = headStr.toString().split("\r\n")[0].split(" ");
					String httpMethod = firstLine[0];

					String hostLine = firstLine[1];
					String targetHost = "";
					int targetPort = 80;
					if (hostLine.startsWith("http")) {
						Util.log(hostLine);
						String[] host = hostLine.split("://")[1].split("/")[0].split(":");
						targetHost = host[0];
						targetPort = 80;
						if (host.length > 1) {
							targetPort = Integer.valueOf(host[1]);
						}
					} else {
						String[] host = hostLine.split(":");
						targetHost = host[0];
						targetPort = 80;
						if (host.length > 1) {
							targetPort = Integer.valueOf(host[1]);
						}
					}

					// Connect target server
					Util.log("Connect target " + targetHost + ":" + String.valueOf(targetPort));
					// proxySocket = new Socket(targetHost, targetPort);
					// proxyInput = proxySocket.getInputStream();
					// proxyOutput = proxySocket.getOutputStream();
					sshChannel = SshClient.getStreamForwarder(targetHost, targetPort, false);
					proxyInput = sshChannel.getInputStream();
					proxyOutput = sshChannel.getOutputStream();

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

			if (sshChannel != null) {
				// New thread continue sending data to target server
				new ProxyStreamingThread(clientInput, proxyOutput).start();

				// Receive target response
				int outputByte = proxyInput.read();
				while (outputByte != -1) {
					clientOutput.write(outputByte);
					clientOutput.flush();
					outputByte = proxyInput.read();
				}
			}
		} catch (SocketException ex) {
			// peer closed the socket
		} catch (Exception ex) {
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

			Util.log("Closed connection, connection count down to = "
					+ Integer.valueOf(Util.connectionCount.decrementAndGet()));
		}
	}
}
