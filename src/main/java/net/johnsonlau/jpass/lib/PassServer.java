package net.johnsonlau.jpass.lib;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import net.johnsonlau.jpass.lib.conf.PassLog;
import net.johnsonlau.jpass.lib.conf.PassSettings;

public class PassServer implements Runnable {
	public static PassSettings settings;
	public static PassLog log;
	public static AtomicInteger connectionCount = new AtomicInteger(0);

	public PassServer(PassSettings settings, PassLog log) {
		PassServer.settings = settings;
		PassServer.log = log;
	}

	public void run() {
	    ServerSocket serverSocket = null;
		try {
			InetAddress addr = settings.getLocalListening() ? InetAddress.getLoopbackAddress() : null;
			serverSocket = new ServerSocket(PassServer.settings.getProxyPort(), 50, addr);
			serverSocket.setSoTimeout(1000);
			log.info("==== Http proxy started at port: " + String.valueOf(settings.getProxyPort()));
			while (true) {
				try {
					Socket socket = serverSocket.accept();
					new PassSocketHandler(socket).start();
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
			log.info("==== Http proxy stopped.");
		}
	}
}
