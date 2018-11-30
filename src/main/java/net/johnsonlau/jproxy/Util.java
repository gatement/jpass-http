package net.johnsonlau.jproxy;

public class Util {
	public static void printBytes(byte[] input) {
		for (int i = 0; i < input.length; i++) {
			if (i % 16 == 0) {
				System.out.printf("0x%04x: ", i);
			}
			System.out.printf("%02x", input[i]);
			if (i % 2 == 1) {
				System.out.printf(" ");
			}
			if ((i + 1) % 16 == 0 && (i + 1) < input.length) {
				System.out.printf("\n");
			}
		}
		System.out.println();
	}
}
