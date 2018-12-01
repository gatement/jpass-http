package net.johnsonlau.jproxy;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SshClient {
	public static Session sshClient;

	public static void connect() throws JSchException {
		try {
			JSch jsch = new JSch();
			sshClient = jsch.getSession(Settings.SSH_USER, Settings.SSH_HOST, Settings.SSH_PORT);
			sshClient.setPassword(Settings.SSH_PWD);
			sshClient.setConfig("StrictHostKeyChecking", "no"); // ask | yes | no
			sshClient.setServerAliveCountMax(3);
			sshClient.setServerAliveInterval(60000);
			sshClient.setDaemonThread(true);
			sshClient.connect();
			Util.log("SSH client version: " + sshClient.getClientVersion() + ", server version: "
					+ sshClient.getServerVersion());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
