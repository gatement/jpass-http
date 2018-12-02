package net.johnsonlau.jproxy.lib;

import java.util.concurrent.atomic.AtomicInteger;

import net.johnsonlau.jproxy.lib.conf.ProxyLog;
import net.johnsonlau.jproxy.lib.conf.ProxySettings;

public class ProxyMain {
	public static ProxySettings settings;
	public static ProxyLog log;

	public static AtomicInteger connectionCount = new AtomicInteger(0);

	public ProxyMain(ProxySettings settings, ProxyLog log) {
		ProxyMain.settings = settings;
		ProxyMain.log = log;
	}

	public void run() {
		try {
			SshClient.connect();
			log.info("SSH connected, start http proxy at port: " + String.valueOf(settings.getProxyPort()));
			new ProxyServer(settings.getProxyPort()).run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}