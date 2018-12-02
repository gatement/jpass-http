package net.johnsonlau.jproxy.lib.conf;

public abstract class ProxySettings {
	public abstract String getSshHost();

	public abstract String getSshUser();

	public abstract String getSshPwd();

	public int getSshPort() {
		return 22;
	}

	public int getProxyPort() {
		return 8119;
	}

	public int getSshAliveMaxCount() {
		return 3;
	}

	public int getSshAliveIntervalMs() {
		return 60000;
	}

	public int getSshChannelOpenTimeoutMs() {
		return 10000;
	}
}
