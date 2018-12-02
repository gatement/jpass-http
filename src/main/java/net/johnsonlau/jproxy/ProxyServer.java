package net.johnsonlau.jproxy;

import java.net.ServerSocket;

public class ProxyServer {

	private int port;
	private ServerSocket serverSocket;

	public ProxyServer(int port) {
		this.port = port;
	}

	public void run() throws Exception {
		try {
			serverSocket = new ServerSocket(port);
			while (true) {
				new ProxySocketHandler(serverSocket.accept()).start();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			serverSocket.close();
		}
	}
}
