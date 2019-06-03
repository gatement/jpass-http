package net.johnsonlau.jproxy;

import net.johnsonlau.jproxy.impl.MyProxyLog;
import net.johnsonlau.jproxy.lib.ProxyMain;
import net.johnsonlau.jproxy.lib.conf.ProxySettings;

public class App {
	public static void main(String[] args) {
		ProxySettings settings = new ProxySettings();
		settings.setServerAddr(System.getProperty("serverAddr", ""));
		settings.setServerPort(Integer.parseInt(System.getProperty("serverPort", "22")));
		settings.setUsername(System.getProperty("username", "root"));
		settings.setPassword(System.getProperty("password", ""));
		settings.setProxyPort(Integer.parseInt(System.getProperty("proxyPort", "8119")));
		
		new ProxyMain(settings, new MyProxyLog()).run();
	}
}
