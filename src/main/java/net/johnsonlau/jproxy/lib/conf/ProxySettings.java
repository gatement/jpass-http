package net.johnsonlau.jproxy.lib.conf;

public class ProxySettings {
	private int SSH_ALIVE_MAX_COUNT = 3;
	private int SSH_ALIVE_INTERVAL_MS = 60000;
	private int SSH_CHANNEL_OPEN_TIMEOUT_MS = 10000;

	private String serverAddr = "";
	private int serverPort = 22;
	private String username = "root";
	private String password = "";
	private int proxyPort = 8119;

	public ProxySettings() {
	}

	public ProxySettings(String serverAddr, int serverPort, String username, String password, int proxyPort) {
		this.serverAddr = serverAddr;
		this.serverPort = serverPort;
		this.username = username;
		this.password = password;
		this.proxyPort = proxyPort;
	}

	public String getServerAddr() {
		return serverAddr;
	}

	public void setServerAddr(String serverAddr) {
		this.serverAddr = serverAddr;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public int getSshAliveMaxCount() {
		return SSH_ALIVE_MAX_COUNT;
	}

	public int getSshAliveIntervalMs() {
		return SSH_ALIVE_INTERVAL_MS;
	}

	public int getSshChannelOpenTimeoutMs() {
		return SSH_CHANNEL_OPEN_TIMEOUT_MS;
	}
}
