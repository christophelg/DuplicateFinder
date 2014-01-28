package com.github.christophelg.duplicatefinder.codec;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigInteger;


public class HashFileCodec {

	private static final String SEPARATOR = "\t";

	public void encode(BufferedWriter output, long checksum, String file) throws IOException {
		output.write(String.valueOf(checksum));
		output.write(SEPARATOR);
		output.write(file);
		output.newLine();
	}

	public void encode(BufferedWriter output, byte[] hash, String file) throws IOException {
		output.write(new BigInteger(1, hash).toString(16));
		output.write(SEPARATOR);
		output.write(file);
		output.newLine();
	}

	public void encode(BufferedWriter output, HashFileEntry entry) throws IOException {
		output.write(entry.getHash());
		output.write(SEPARATOR);
		output.write(entry.getFile());
		output.newLine();
	}

	public HashFileEntry decode(String input) {
		String[] tokens = input.split(SEPARATOR);
		if (tokens.length != 2) {
			throw new IllegalArgumentException("Unable to parse:" + input);
		}
		return new HashFileEntry(tokens[0], tokens[1]);
	}
}
