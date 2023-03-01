package md.util;

import java.io.*;

public class ChunkedOutputStream {
	/**
	 * @uml.property  name="out"
	 */
	private OutputStream out = null;
	/**
	 * @uml.property  name="buf" multiplicity="(0 -1)" dimension="1"
	 */
	private byte[] buf = null;
	/**
	 * @uml.property  name="pointer"
	 */
	private int pointer = 0;

	public ChunkedOutputStream(OutputStream out, int buffer_size) {
		this.out = out;
		buf = new byte[buffer_size];
	}

	public void write(int i) throws IOException {
		buf[pointer] = (byte)i;
		++pointer;
		if(pointer == buf.length)
			flush();
	}

	public void write(byte[] buf) throws IOException {
		write(buf, 0, buf.length);
	}

	public void write(byte[] buf, int offset, int len) throws IOException {
		for(int i=offset; i<(offset+len);i++) {
			write(buf[i]);
		}
	}

	public void flush() throws IOException {
		if(pointer == 0)
			return;
		out.write((Integer.toHexString(pointer)+"\r\n").getBytes());
		out.write(buf, 0, pointer);
		out.write("\r\n".getBytes());
		pointer = 0;
	}

	public void close() throws IOException {
		flush();
		out.write(("0\r\n").getBytes());
	}
}
