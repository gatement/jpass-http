package net.johnsonlau.jproxy;

import net.johnsonlau.jproxy.lib.conf.ProxySettings;

public class MyProxySettings extends ProxySettings {

	@Override
	public String getSshHost() {
		return "lqahk";
	}

	@Override
	public int getSshPort() {
		return 9812;
	}

	@Override
	public String getSshUser() {
		return "johnson";
	}

	@Override
	public String getSshPwd() {
		return "KAwIc4SSJqbpUju9";
	}
}
