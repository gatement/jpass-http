package net.johnsonlau.jproxy;

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
			while (true) {
				output.write(input.read());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}