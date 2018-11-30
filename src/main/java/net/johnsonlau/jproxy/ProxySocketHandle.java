package net.johnsonlau.jproxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import com.jcraft.jsch.Channel;

public class ProxySocketHandle extends Thread {

	private Socket socket;

	public ProxySocketHandle(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		OutputStream clientOutput = null;
		InputStream clientInput = null;
		Socket proxySocket = null;
		InputStream proxyInput = null;
		OutputStream proxyOutput = null;
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
					// Util.printBytes(headStr.toString().getBytes());
					// Extract target server
					String targetHost = "";
					int targetPort = 80;
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
							break;
						}
					}

					// Connect target server
					System.out.println("Connect target " + targetHost + ":" + String.valueOf(targetPort));
					// proxySocket = new Socket(targetHost, targetPort);
					// proxyInput = proxySocket.getInputStream();
					// proxyOutput = proxySocket.getOutputStream();
					Channel sshChannel = SshClient.sshClient.getStreamForwarder(targetHost, targetPort);
					proxyInput = sshChannel.getInputStream();
					proxyOutput = sshChannel.getOutputStream();
					sshChannel.connect(5000);

					// Extract HTTP Method and process CONNECT
					String type = headStr.substring(0, headStr.indexOf(" "));
					if ("CONNECT".equalsIgnoreCase(type)) {
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

			// New thread continue sending data to target server
			new ProxyStreamingThread(clientInput, proxyOutput).start();

			// Receive target response
			int outputByte = proxyInput.read();
			while (outputByte != -1) {
				clientOutput.write(outputByte);
				clientOutput.flush();
				outputByte = proxyInput.read();
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
		}
	}
}
