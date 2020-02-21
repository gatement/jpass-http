package net.johnsonlau.jpass.lib.conf;

public class PassSettings {
	private int proxyPort = 8118;
	private boolean localListening = true;

	public PassSettings() {
	}

	public PassSettings(int proxyPort, boolean localListening) {
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
