package com.alibaba.dubbo.remoting.buffer;

import java.io.OutputStream;

/**
 * @author wuping
 * @date 2018/12/9
 * 通道 Buffer 输出流
 */

public class ChannelBufferOutputStream extends OutputStream {
    private final ChannelBuffer buffer;
    /**
     * 开始位置
     */
    private final int startIndex;

    public ChannelBufferOutputStream(ChannelBuffer buffer) {
        if (buffer == null) {
            throw new NullPointerException("buffer");
        }
        this.buffer = buffer;
        startIndex = buffer.writerIndex();
    }

    public int writtenBytes() {
        return buffer.writerIndex() - startIndex;
    }

    @Override
    public void write(byte[] b, int off, int len) {
        if (len == 0) {
            return;
        }
        buffer.writeBytes(b, off, len);
    }

    @Override
    public void write(byte[] b) {
        buffer.writeBytes(b);
    }

    @Override
    public void write(int b) {
        buffer.writeByte((byte) b);
    }

    public ChannelBuffer buffer() {
        return buffer;
    }
}
