package net.johnsonlau.jpass.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PassStreamingThread extends Thread {

	private InputStream input;
	private OutputStream output;

	public PassStreamingThread(InputStream input, OutputStream output) {
		this.input = input;
		this.output = output;
	}

	@Override
	public void run() {
		try {
			byte[] data = new byte[65536]; // 64KB
			int readCount = input.read(data);
			while (readCount != -1) {
				output.write(data, 0, readCount);
				output.flush();
				readCount = input.read(data);
			}

		} catch (IOException ex) {
			// Peer closed the connection
			// ex.printStackTrace();
		}
	}
}