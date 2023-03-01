package md.util;

import java.io.*;

public class CharArray {
	private final static int DEFAULT_BUFFER_SIZE = 4096;
	
	/**
	 * @uml.property  name="size"
	 */
	private int size = 0;
	/**
	 * @uml.property  name="buf_size"
	 */
	private int buf_size = 0;
	/**
	 * @uml.property  name="buf" multiplicity="(0 -1)" dimension="1"
	 */
	private char[] buf = null;

	public CharArray() {
		this(DEFAULT_BUFFER_SIZE);
	}

	public CharArray(int buf_size) {
		buf = new char[buf_size];
		this.buf_size = buf_size;
		this.size = buf_size;
	}

	public CharArray(InputStreamReader in, int max_size) throws Exception {
		this();
		this.size = 0;
		while(true) {
			int _c = in.read();
			if(_c == -1)
				break;
			if(size == max_size) // 최대 크기를 넘어서는건 처리하지 않는다.
				break;
			if(size == buf_size) {
				// Reallocate..
				char[] tmp = new char[buf_size*2];
				System.arraycopy(buf, 0, tmp, 0, buf_size);
				buf = tmp;
				buf_size *= 2;
				tmp = null;
			}
			buf[size] = (char)_c;
			++size;
		}
	}

	public int length() {
		return size;
	}

	public char get(int idx) {
		return buf[idx];
	}
	
	public void put(int idx, char c) {
		buf[idx] = c;
	}
	
	public void Destroy() {
		buf = null;
	}
}
