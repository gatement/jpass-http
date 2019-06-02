package net.johnsonlau.jproxy;

import net.johnsonlau.jproxy.impl.MyProxyLog;
import net.johnsonlau.jproxy.lib.ProxyMain;
import net.johnsonlau.jproxy.lib.conf.ProxySettings;

public class App {
	public static void main(String[] args) {
		ProxySettings settings = new ProxySettings();
		settings.setServerAddr("");
		settings.setServerPort(22);
		settings.setUsername("root");
		settings.setPassword("");
		settings.setProxyPort(8119);

		new ProxyMain(settings, new MyProxyLog()).run();
	}
}
