package net.johnsonlau.jproxy.lib;

import java.io.IOException;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SshClient {
	private static JSch sshClient;
	public static Session sshSession;

	public static void connect() {
		try {
			closeSession();

			sshClient = new JSch();
			sshSession = sshClient.getSession(
					ProxyMain.settings.getUsername(), 
					ProxyMain.settings.getServerAddr(),
					ProxyMain.settings.getServerPort());
			sshSession.setPassword(ProxyMain.settings.getPassword());
			sshSession.setConfig("StrictHostKeyChecking", "no"); // ask | yes | no
			sshSession.setServerAliveCountMax(ProxyMain.settings.getSshAliveMaxCount());
			sshSession.setServerAliveInterval(ProxyMain.settings.getSshAliveIntervalMs());
			sshSession.setDaemonThread(true);
			sshSession.connect();

			ProxyMain.log.info("SSH client ver: " + sshSession.getClientVersion() + ", server ver: "
					+ sshSession.getServerVersion());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Channel getStreamForwarder(String targetHost, int targetPort, boolean retrying)
			throws JSchException, IOException {
		try {
			Channel channel = SshClient.sshSession.getStreamForwarder(targetHost, targetPort);
			channel.connect(ProxyMain.settings.getSshChannelOpenTimeoutMs());
			return channel;
		} catch (JSchException ex) {
			if (!retrying && "session is down".equals(ex.getMessage())) {
				ProxyMain.log.info("Reconnecting SSH Tunnel");
				connect();
				return getStreamForwarder(targetHost, targetPort, true);
			} else {
				throw ex;
			}
		}
	}

	private static void closeSession() {
		if (sshSession != null) {
			try {
				sshSession.disconnect();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
