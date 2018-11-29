package net.johnsonlau.jproxy;

import com.jcraft.jsch.UserInfo;

public class SshUserInfo implements UserInfo {

	public String getPassphrase() {
		System.out.println("SSH getPassphrase");
		return null;
	}

	public String getPassword() {
		System.out.println("SSH getPassword");
		return Settings.SSH_PWD;
	}

	public boolean promptPassword(String message) {
		System.out.println("SSH promptPassword: " + message);
		return true;
	}

	public boolean promptPassphrase(String message) {
		System.out.println("SSH promptPassphrase: " + message);
		return true;
	}

	public boolean promptYesNo(String message) {
		System.out.println("SSH promptYesNo: " + message);
		return true;
	}

	public void showMessage(String message) {
		System.out.println("SSH: " + message);
	}
}