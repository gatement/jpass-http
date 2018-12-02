package net.johnsonlau.jproxy.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProxyStreamingThread extends Thread {

	private InputStream input;
	private OutputStream output;

	public ProxyStreamingThread(InputStream input, OutputStream output) {
		this.input = input;
		this.output = output;
	}

	@Override
	public void run() {
		try {
			int data = input.read();
			while (data != -1) {
				output.write(data);
				output.flush();
				data = input.read();
			}
		} catch (IOException ex) {
			// Peer closed the connection
			// ex.printStackTrace();
		}
	}
}