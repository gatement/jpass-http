package net.johnsonlau.jproxy;

public class App {
	public static void main(String[] args) throws Exception {
		System.out.println("Hello World!");

		int port = 8119;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		}

		new DiscardServer(port).run();
	}
}
