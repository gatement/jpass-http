package net.johnsonlau.jproxy;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.johnsonlau.jproxy.lib.conf.ProxyLog;

public class MyProxyLog extends ProxyLog {

	@Override
	public void info(String msg) {
		String dateString = "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "] ";
		System.out.println(dateString + msg);
	}
}
