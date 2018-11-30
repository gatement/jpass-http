package net.johnsonlau.jproxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

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
				System.out.print("o");
				output.write(data);
				data = input.read();
			}
		} catch (SocketException ex) {
			// Peer closed the connection
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}