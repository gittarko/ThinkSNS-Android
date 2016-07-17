package com.thinksns.sociax.unit;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FlushedInputStream extends FilterInputStream {
	public FlushedInputStream(InputStream in) {
		super(in);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FilterInputStream#skip(long)
	 */
	@Override
	public long skip(long byteCount) throws IOException {
		long totalBytesSkipped = 0L;

		while (totalBytesSkipped < byteCount) {
			long bytesSkipped = in.skip(byteCount - totalBytesSkipped);
			if (bytesSkipped == 0L) {
				int bytes = read();
				if (bytes < 0) {
					break;
				} else {
					bytesSkipped = 1;
				}
			}
			totalBytesSkipped += bytesSkipped;
		}

		return totalBytesSkipped;
	}

}
