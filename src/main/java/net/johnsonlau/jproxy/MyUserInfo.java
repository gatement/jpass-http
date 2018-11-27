package net.johnsonlau.jproxy;

import com.jcraft.jsch.UserInfo;

public class MyUserInfo implements UserInfo {

	public String getPassphrase() {
		System.out.println("getPassphrase");
		return null;
	}

	public String getPassword() {
		System.out.println("getPassword");
		return "";
	}

	public boolean promptPassword(String message) {
		System.out.println("promptPassword: " + message);
		return true;
	}

	public boolean promptPassphrase(String message) {
		System.out.println("promptPassphrase: " + message);
		return true;
	}

	public boolean promptYesNo(String message) {
		System.out.println("promptYesNo: " + message);
		return true;
	}

	public void showMessage(String message) {
		System.out.println("showMessage: " + message);
	}
}