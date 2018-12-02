package net.johnsonlau.jproxy;

public class App {
	public static void main(String[] args) {
		try {
			SshClient.connect();

			Util.log("SSH connected, start http proxy at port: " + String.valueOf(Settings.PROXY_PORT));
			new ProxyServer(Settings.PROXY_PORT).run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
