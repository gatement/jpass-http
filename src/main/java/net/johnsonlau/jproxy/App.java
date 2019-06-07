package net.johnsonlau.jproxy;

import net.johnsonlau.jproxy.impl.MyProxyLog;
import net.johnsonlau.jproxy.lib.ProxyServer;
import net.johnsonlau.jproxy.lib.SshClient;
import net.johnsonlau.jproxy.lib.conf.ProxySettings;

public class App {
	public static void main(String[] args) {
		ProxySettings settings = new ProxySettings();
		settings.setServerAddr(System.getProperty("serverAddr", ""));
		settings.setServerPort(Integer.parseInt(System.getProperty("serverPort", "22")));
		settings.setUsername(System.getProperty("username", "root"));
		settings.setPassword(System.getProperty("password", ""));
		settings.setProxyPort(Integer.parseInt(System.getProperty("proxyPort", "8119")));

		final Thread thread = new Thread(new ProxyServer(settings, new MyProxyLog()), "ProxyThread");
		thread.run();

		// to be stopped 
		//thread.start();
		//try {
		//	Thread.sleep(10000);
		//} catch (InterruptedException ex) {
		//}
		//thread.interrupt();
	}
}
