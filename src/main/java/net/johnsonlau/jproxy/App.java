package net.johnsonlau.jproxy;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class App {
	public static void main(String[] args) throws Exception {
		System.out.println("Hello World!");

		int port = 8119;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		}

		new DiscardServer(port).run();
	}

	public static void main2(String[] args) {
		System.out.println("Hello World!");

		int port = 9812;
		String host = "johnson@lqahk";

		try {
			JSch jsch = new JSch();

			String user = host.substring(0, host.indexOf('@'));
			host = host.substring(host.indexOf('@') + 1);

			Session session = jsch.getSession(user, host, 9812);

			// username and password will be given via UserInfo interface.
			UserInfo ui = new MyUserInfo();
			session.setUserInfo(ui);
			session.connect();

			String foo = "118.178.232.166:18077";
			host = foo.substring(0, foo.indexOf(':'));
			port = Integer.parseInt(foo.substring(foo.indexOf(':') + 1));

			System.out.println("System.{in,out} will be forwarded to " + host + ":" + port + ".");
			Channel channel = session.getStreamForwarder(host, port);
			// InputStream in = channel.getInputStream();
			// OutpuStream out = channel.getOutputStream();
			channel.setInputStream(System.in);
			channel.setOutputStream(System.out);
			channel.connect(1000);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
