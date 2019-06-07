package net.johnsonlau.jproxy.lib;

import java.io.IOException;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SshClient {
	public static Session sshSession;

	public static void connect() {
		try {
			disconnect();

			JSch sshClient = new JSch();
			sshSession = sshClient.getSession(
					ProxyServer.settings.getUsername(), 
					ProxyServer.settings.getServerAddr(),
					ProxyServer.settings.getServerPort());
			sshSession.setPassword(ProxyServer.settings.getPassword());
			sshSession.setConfig("StrictHostKeyChecking", "no"); // ask | yes | no
			sshSession.setServerAliveCountMax(ProxyServer.settings.getSshAliveMaxCount());
			sshSession.setServerAliveInterval(ProxyServer.settings.getSshAliveIntervalMs());
			sshSession.setDaemonThread(true);
			sshSession.connect();

			ProxyServer.log.info("==== SSH tunnel connected.");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Channel getStreamForwarder(String targetHost, int targetPort, boolean retrying)
			throws JSchException, IOException {
		try {
			Channel channel = sshSession.getStreamForwarder(targetHost, targetPort);
			channel.connect(ProxyServer.settings.getSshChannelOpenTimeoutMs());
			return channel;
		} catch (JSchException ex) {
			if (!retrying && "session is down".equals(ex.getMessage())) {
				ProxyServer.log.info("Reconnecting SSH tunnel");
				connect();
				return getStreamForwarder(targetHost, targetPort, true);
			} else {
				throw ex;
			}
		}
	}

	public static void disconnect() {
		if (sshSession != null) {
			try {
                sshSession.disconnect();
                ProxyServer.log.info("==== SSH tunnel disconnected.");
			} catch (Exception ex) {
				ProxyServer.log.info("exception: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

}
