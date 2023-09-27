package xyz.fs404.common.io;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;

final class FileImpl implements FsFile {

    private final Path path;

    private volatile RandomAccessFile random;
    private volatile InputStream inputStream;
    private volatile OutputStream outputStream;

    FileImpl(Path path) {
        this.path = path;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public boolean isOpened() {
        return random != null;
    }

    @Override
    public synchronized void open(String mode) {
        if (isOpened()) {
            throw new FsIOException("The file has already opened.");
        }
        try {
            random = new RandomAccessFile(path.toFile(), mode);
        } catch (Exception e) {
            throw new FsIOException(e);
        }
    }

    @Override
    public synchronized void close() {
        if (!isOpened()) {
            return;
        }
        try {
            random.close();
            random = null;
            inputStream = null;
            outputStream = null;
        } catch (Exception e) {
            throw new FsIOException(e);
        }
    }

    @Override
    public synchronized long position() {
        checkOpen();
        try {
            return random.getFilePointer();
        } catch (Exception e) {
            throw new FsIOException(e);
        }
    }

    @Override
    public synchronized void position(long pos) {
        checkOpen();
        try {
            random.seek(pos);
        } catch (Exception e) {
            throw new FsIOException(e);
        }
    }

    @Override
    public synchronized long length() {
        checkOpen();
        try {
            return random.length();
        } catch (Exception e) {
            throw new FsIOException(e);
        }
    }

    @Override
    public synchronized void setFileLength(long newLength) {
        checkOpen();
        try {
            random.setLength(newLength);
        } catch (Exception e) {
            throw new FsIOException(e);
        }
    }

    @Override
    public synchronized FileChannel getChannel() {
        checkOpen();
        try {
            return random.getChannel();
        } catch (Exception e) {
            throw new FsIOException(e);
        }
    }

    @Override
    public synchronized FileDescriptor getDescriptor() {
        checkOpen();
        try {
            return random.getFD();
        } catch (Exception e) {
            throw new FsIOException(e);
        }
    }

    @Override
    public synchronized InputStream bindInputStream() {
        checkOpen();
        if (inputStream == null) {
            inputStream = new BindInputStream();
        }
        return inputStream;
    }

    @Override
    public synchronized OutputStream bindOutputStream() {
        checkOpen();
        if (outputStream == null) {
            outputStream = new BindOutputStream();
        }
        return outputStream;
    }

    private void checkOpen() {
        if (!isOpened()) {
            throw new FsIOException("The file is closed or not yet open.");
        }
    }

    private final class BindInputStream extends InputStream {

        @Override
        public synchronized int read(byte[] b) throws IOException {
            checkOpen();
            return random.read(b);
        }

        @Override
        public synchronized int read(byte[] b, int off, int len) throws IOException {
            checkOpen();
            return random.read(b, off, len);
        }

        @Override
        public synchronized int read() throws IOException {
            checkOpen();
            return random.read();
        }

        @Override
        public synchronized long skip(long n) throws IOException {
            checkOpen();
            if (n <= Integer.MAX_VALUE) {
                return random.skipBytes((int) n);
            }
            long remaining = n;
            while (true) {
                int skip = random.skipBytes(Integer.MAX_VALUE);
                if (skip <= 0) {
                    break;
                }
                remaining -= skip;
            }
            return n - remaining;
        }

        @Override
        public synchronized int available() throws IOException {
            checkOpen();
            long available = random.length() - random.getFilePointer();
            if (available > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }
            return (int) available;
        }
    }

    private final class BindOutputStream extends OutputStream {

        @Override
        public synchronized void write(byte[] b) throws IOException {
            checkOpen();
            long pos = random.getFilePointer();
            FileLock lock = random.getChannel().tryLock(pos, b.length, false);
            if (lock == null || !lock.isValid()) {
                throw new IOException("Failed to lock file at pos: " + pos + ", length: " + b.length + ".");
            }
            try {
                random.write(b);
            } finally {
                lock.close();
            }
        }

        @Override
        public synchronized void write(byte[] b, int off, int len) throws IOException {
            checkOpen();
            long pos = random.getFilePointer();
            FileLock lock = random.getChannel().tryLock(pos, len, false);
            if (lock == null || !lock.isValid()) {
                throw new IOException("Failed to lock file at pos: " + pos + ", length: " + len + ".");
            }
            try {
                random.write(b, off, len);
            } finally {
                lock.close();
            }
        }

        @Override
        public synchronized void write(int b) throws IOException {
            checkOpen();
            long pos = random.getFilePointer();
            FileLock lock = random.getChannel().tryLock(pos, 1, false);
            if (lock == null || !lock.isValid()) {
                throw new IOException("Failed to lock file at pos: " + pos + ", length: " + 1 + ".");
            }
            try {
                random.write(b);
            } finally {
                lock.close();
            }
        }

        @Override
        public synchronized void flush() throws IOException {
            checkOpen();
            random.getFD().sync();
        }
    }
}
