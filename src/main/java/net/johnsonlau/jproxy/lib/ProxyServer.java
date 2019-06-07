package net.johnsonlau.jproxy.lib;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import net.johnsonlau.jproxy.lib.conf.ProxyLog;
import net.johnsonlau.jproxy.lib.conf.ProxySettings;

public class ProxyServer implements Runnable {
	public static ProxySettings settings;
	public static ProxyLog log;
	public static AtomicInteger connectionCount = new AtomicInteger(0);

	public ProxyServer(ProxySettings settings, ProxyLog log) {
		ProxyServer.settings = settings;
		ProxyServer.log = log;
	}

	public void run() {
	    ServerSocket serverSocket = null;
		try {
			SshClient.connect();
			serverSocket = new ServerSocket(ProxyServer.settings.getProxyPort());
			serverSocket.setSoTimeout(1000);
			log.info("==== Http proxy started at port: " + String.valueOf(settings.getProxyPort()));
			while (true) {
				try {
					Socket socket = serverSocket.accept();
					new ProxySocketHandler(socket).start();
				} catch (SocketTimeoutException ex) {
				}
				
				Thread.sleep(1); // allow for interrupting
			}
		} catch (InterruptedException ex) {
		} catch (Exception ex) {
			log.info("exception: " + ex.getMessage());
			ex.printStackTrace();
		} finally {
			if (serverSocket != null) {
				try {
				    serverSocket.close();
				} catch (Exception ex) {
         			log.info("exception: " + ex.getMessage());
					ex.printStackTrace();
				}
			}
			SshClient.disconnect();
			log.info("==== Http proxy stopped.");
		}
	}
}
