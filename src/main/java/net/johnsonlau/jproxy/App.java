package net.johnsonlau.jproxy;

import net.johnsonlau.jproxy.impl.MyProxyLog;
import net.johnsonlau.jproxy.lib.ProxyServer;
import net.johnsonlau.jproxy.lib.conf.ProxySettings;

public class App {
	public static void main(String[] args) {
		ProxySettings settings = new ProxySettings();
		settings.setProxyPort(Integer.parseInt(System.getProperty("proxyPort", "8118")));
		settings.setLocalListening(Boolean.parseBoolean(System.getProperty("localListening", "true")));

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
