package net.johnsonlau.jproxy.conf;

import net.johnsonlau.jproxy.lib.conf.ProxySettings;

public class MyProxySettings extends ProxySettings {

	@Override
	public String getSshHost() {
		return "1.2.3.4";
	}

	@Override
	public int getSshPort() {
		return 22;
	}

	@Override
	public String getSshUser() {
		return "root";
	}

	@Override
	public String getSshPwd() {
		return "123456";
	}
	
	@Override
	public int getProxyPort() {
		return 8119;
	}
}
