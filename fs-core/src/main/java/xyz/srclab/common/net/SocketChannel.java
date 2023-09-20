package xyz.srclab.common.net;

import org.jetbrains.annotations.NotNull;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.data.FsData;
import xyz.srclab.common.io.FsIO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.*;
import java.time.Duration;

final class SocketChannel implements FsTcpChannel{

    private final Socket socket;
    private final InputStream in;
    private final OutputStream out;

    SocketChannel(Socket socket) {
        this.socket = socket;
        try {
            this.in = socket.getInputStream();
            this.out = socket.getOutputStream();
        } catch (IOException e) {
            throw new FsNetException(e);
        }
    }

    @Override
    public InetAddress getRemoteAddress() {
        return socket.getInetAddress();
    }

    @Override
    public int getRemotePort() {
        return socket.getPort();
    }

    @Override
    public InetAddress getLocalAddress() {
        return socket.getLocalAddress();
    }

    @Override
    public int getLocalPort() {
        return socket.getLocalPort();
    }

    @Override
    public boolean isOpened() {
        return socket.isConnected();
    }

    @Override
    public boolean isClosed() {
        return socket.isClosed();
    }

    @Override
    public void close(@Nullable Duration timeout) {
        closeNow();
    }

    @Override
    public synchronized void closeNow() {
        if (socket.isClosed()) {
            return;
        }
        try {
            socket.close();
        } catch (IOException e) {
            throw new FsNetException(e);
        }
    }

    @Override
    public synchronized void send(FsData data) {
        FsIO.readBytesTo(data.toInputStream(), out);
    }

    @Override
    public synchronized void flush() {
        try {
            out.flush();
        } catch (IOException e) {
            throw new FsNetException(e);
        }
    }

    @Override
    public Object getSource() {
        return socket;
    }
}
