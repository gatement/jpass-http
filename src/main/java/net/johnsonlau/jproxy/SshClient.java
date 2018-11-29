package net.johnsonlau.jproxy;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class SshClient {
	public static Session sshClient;

	public static void connect() throws JSchException {
		JSch jsch = new JSch();
		sshClient = jsch.getSession(Settings.SSH_USER, Settings.SSH_HOST, Settings.SSH_PORT);
		UserInfo userInfo = new SshUserInfo();
		sshClient.setUserInfo(userInfo);
		sshClient.connect();
	}

	public static Channel initChannel(String targetHost, int targetPort) throws JSchException {
		Channel channel = sshClient.getStreamForwarder(targetHost, targetPort);
		channel.connect(10000);
		return channel;
	}

}
