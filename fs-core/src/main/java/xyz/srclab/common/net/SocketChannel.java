package xyz.srclab.common.net;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.data.FsData;

import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.time.Duration;

final class SocketChannel implements FsTcpChannel{

    private final Socket socket;

    SocketChannel(Socket socket) {
        this.socket = socket;
    }

    @Override
    public InetAddress getRemoteAddress() {
        return null;
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public InetAddress getLocalAddress() {
        return null;
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

    @Override
    public boolean isOpened() {
        return false;
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void close(@Nullable Duration timeout) {

    }

    @Override
    public void closeNow() {

    }

    @Override
    public void send(FsData data) {

    }

    @Override
    public void flush() {

    }

    @Override
    public ByteBuffer getBuffer() {
        return null;
    }

    @Override
    public Object getSource() {
        return socket;
    }
}
