package net.johnsonlau.jproxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
			String targetHost = "";
			int targetPort = 80;
			String line;
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientInput));
			StringBuilder headStr = new StringBuilder();
			while (null != (line = bufferedReader.readLine())) {
				// System.out.println(line);
				headStr.append(line + "\r\n");

				if (line.length() == 0) {
					// Finish HTTP header receiving
					break;
				} else {
					// Extract host name and port
					// Example1: "Host: www.example.com"
					// Example2: "Host: www.example.com:443"
					String[] temp = line.split(" ");
					if ("Host:".equalsIgnoreCase(temp[0])) {
						String[] hostStr = temp[1].split(":");
						targetHost = hostStr[0];
						if (hostStr.length > 1) {
							targetPort = Integer.valueOf(hostStr[1]);
						}
					}
				}
			}
			String type = headStr.substring(0, headStr.indexOf(" "));

			// Connect target server
			System.out.println("Connect target " + targetHost + ":" + String.valueOf(targetPort));
			// proxySocket = new Socket(targetHost, targetPort);
			// proxyInput = proxySocket.getInputStream();
			// proxyOutput = proxySocket.getOutputStream();
			Channel sshChannel = SshClient.sshClient.getStreamForwarder(targetHost, targetPort);
			proxyInput = sshChannel.getInputStream();
			proxyOutput = sshChannel.getOutputStream();
			sshChannel.connect(5000);

			if ("CONNECT".equalsIgnoreCase(type)) {
				// For HTTPS request, consume the HTTP header and send back response
				clientOutput.write("HTTP/1.1 200 Connection Established\r\n\r\n".getBytes());
				clientOutput.flush();
			} else {
				// For HTTP request, send the HTTP header
				proxyOutput.write(headStr.toString().getBytes());
			}

			// New thread continue sending data to target server
			new ProxyStreamingThread(clientInput, proxyOutput).start();

			// Receive target response
			System.out.println("about to read: isClosed=" + String.valueOf(sshChannel.isClosed()) + ", isConnected="
					+ String.valueOf(sshChannel.isConnected()) + ", isEOF=" + String.valueOf(sshChannel.isEOF()));
			int data = proxyInput.read();
			System.out.println(String.valueOf(data));
			while (data != -1) {
				System.out.print("i");
				clientOutput.write(data);
				data = proxyInput.read();
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
