package md.util;

import java.io.*;

public class ChunkedInputStream extends InputStream {
	/**
	 * @uml.property  name="in"
	 */
	private InputStream in = null;
	/**
	 * @uml.property  name="currentRemainSize"
	 */
	private int CurrentRemainSize = -1;
	/**
	 * @uml.property  name="isEndOfStream"
	 */
	private boolean isEndOfStream = false;

	private int readSize() throws IOException {
		int rtn = 0;
		try {
			String strSize = Util.readLine(in, "8859_1", 8);
			if(!strSize.endsWith("\r\n"))
				throw new IOException("Invalid ChunkSize(not end of new line: "+strSize+")");
			rtn = Integer.parseInt(strSize.trim(), 16);
			if(rtn < 0 || rtn > (1024*16)) {
				throw new Exception("Invalid ChunkSize("+strSize+")");
			}
		} catch(Exception e) {
			throw new IOException(e.toString());
		}
		return rtn;
	}

	public ChunkedInputStream(InputStream in) {
		this.in = in;
	}

	public int read() throws IOException {
		if(isEndOfStream)
			return -1;
		if(CurrentRemainSize == -1)
			CurrentRemainSize = readSize();
		if(CurrentRemainSize == 0) {
			isEndOfStream = true;
			return -1;
		}
		int rtn = in.read();
		--CurrentRemainSize;
		if(CurrentRemainSize == 0) {
			try{in.read();in.read();}catch(Exception e){}
			CurrentRemainSize = -1;
		}
		if(rtn == -1) {
			isEndOfStream = true;
		}
		return rtn;
	}

	public int available() throws IOException {
		return 1;
	}

	public int read(byte[] buf, int offset, int len) throws IOException {
		int size = 0;
		for(int i=offset;i<(len+offset);i++) {
			int rtn = read();
			if(rtn == -1) {
				break;
			}
			buf[i] = (byte)rtn;
			++size;
		}
		if(size == 0)
			return -1;
		return size;
	}

	public int read(byte[] buf) throws IOException {
		return read(buf, 0, buf.length);
	}

	public void close() throws IOException {
		isEndOfStream = true;
		try{in.close();}catch(Exception e){}
	}
}
