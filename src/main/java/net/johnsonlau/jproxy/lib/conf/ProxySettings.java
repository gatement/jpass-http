package net.johnsonlau.jproxy.lib.conf;

public class ProxySettings {
	private int proxyPort = 8118;
	private boolean localListening = true;

	public ProxySettings() {
	}

	public ProxySettings(int proxyPort, boolean localListening) {
		this.proxyPort = proxyPort;
		this.localListening = localListening;
	}

	public boolean getLocalListening() {
		return this.localListening;
	}

	public void setLocalListening(boolean localListening) {
		this.localListening = localListening;
	}

	public int getProxyPort() {
		return this.proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}
}
