package xyz.fsgek.common.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.annotations.ThreadSafe;
import xyz.fsgek.common.base.GekCheck;
import xyz.fsgek.common.base.ref.BooleanRef;
import xyz.fsgek.common.base.ref.GekRef;
import xyz.fsgek.common.base.ref.LongRef;
import xyz.fsgek.common.cache.GekCache;
import xyz.fsgek.common.io.GekIO;

import java.io.*;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * File cache service, provides cached IO methods.
 *
 * @author fredsuvn
 * @see Builder
 */
@ThreadSafe
public interface GekFileCache {

    /**
     * Returns a new builder for {@link GekFileCache}.
     *
     * @return a new builder
     */
    static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Opens an input stream for file of given path at specified file position,
     * data of the stream comes from cache or underlying file (if cache not found or expired).
     *
     * @param path   given path
     * @param offset specified file position
     * @return an input stream for file of given path
     */
    InputStream getInputStream(Path path, long offset);

    /**
     * Opens an output stream for file of given path at specified file position,
     * and cache will be updated after writing.
     *
     * @param path   given path
     * @param offset specified file position
     * @return an output stream for file of given path
     */
    OutputStream getOutputStream(Path path, long offset);

    /**
     * Sets new file length for given path, truncated or extended.
     *
     * @param path      given path
     * @param newLength new length
     */
    void setFileLength(Path path, long newLength);

    /**
     * Returns cached file chunk count.
     *
     * @return cached file chunk count
     */
    int cachedChunkCount();

    /**
     * Generator for file chunk cache.
     */
    interface ChunkCacheGenerator {

        /**
         * Generates a new file chunk cache with given remove listener.
         *
         * @param removeListener given remove listener
         * @return new file chunk cache
         */
        ChunkCache generate(RemoveListener removeListener);

        /**
         * Chunk cache remove listener.
         */
        interface RemoveListener {

            /**
             * On cache remove. This method should be called after the entry was removed.
             *
             * @param key   key of the cache
             * @param cache cache itself
             */
            void onCacheRemove(ChunkIndex key, ChunkCache cache);
        }
    }

    /**
     * Cache interface used for {@link GekFileCache}, must support remove listener.
     */
    @ThreadSafe
    interface ChunkCache {

        /**
         * Gets cached file chunk value, if the value doesn't exist, create and put a new one with given function.
         * If the function is null, return null else return cached or new value.
         * If the function returns null, no value will be cached and return null.
         *
         * @param key      key of the value
         * @param function given function
         * @return cached file chunk value
         */
        @Nullable
        Chunk get(ChunkIndex key, @Nullable Function<ChunkIndex, @Nullable Chunk> function);

        /**
         * Puts new value of specified key
         *
         * @param key   specified key
         * @param value new value
         */
        void put(ChunkIndex key, Chunk value);

        /**
         * Removes the value associated with given key.
         *
         * @param key given key
         */
        void remove(ChunkIndex key);

        /**
         * Removes values of which key and value (first and second param) pass given predicate.
         *
         * @param predicate given predicate
         */
        void removeIf(BiPredicate<ChunkIndex, Chunk> predicate);

        /**
         * Returns cached size.
         *
         * @return cached size
         */
        int size();
    }

    /**
     * Generator for un-cached underlying file access.
     */
    interface FileAccessGenerator {

        /**
         * Generates a new {@link GekFile} to access un-cached underlying file.
         *
         * @param path file path
         * @return a new {@link GekFile} to access un-cached underlying file
         */
        GekFile generate(Path path);
    }

    /**
     * Listener for cache read.
     */
    interface CacheReadListener {

        /**
         * On cache read.
         *
         * @param path   file path
         * @param offset offset position
         * @param length read length
         */
        void onCacheRead(Path path, long offset, long length);
    }

    /**
     * Listener for cache write.
     */
    interface CacheWriteListener {

        /**
         * On cache write.
         *
         * @param file   file path
         * @param offset offset position
         * @param length written length
         */
        void onCacheWrite(Path file, long offset, long length);
    }

    /**
     * Listener for underlying file read.
     */
    interface FileReadListener {

        /**
         * On file read.
         *
         * @param file   file path
         * @param offset offset position
         * @param length read length
         */
        void onFileRead(Path file, long offset, long length);
    }

    /**
     * Listener for underlying file write.
     */
    interface FileWriteListener {

        /**
         * On file write.
         *
         * @param file   file path
         * @param offset offset position
         * @param length written length
         */
        void onFileWrite(Path file, long offset, long length);
    }

    /**
     * Key for chunk cache, holds file path and index of cached chunk data.
     */
    @Data
    @EqualsAndHashCode
    class ChunkIndex {
        /**
         * File path.
         */
        private final String path;
        /**
         * Start position of cached file data.
         */
        private final long position;
    }

    /**
     * Value for chunk cache, holds file chunk data and tail flag.
     */
    @AllArgsConstructor
    class Chunk {
        /**
         * File chunk data.
         */
        private final byte[] data;
        /**
         * Whether this chunk is end of the file
         */
        private final boolean eof;
    }

    /**
     * Builder for {@link GekFileCache}.
     */
    class Builder {

        private static final ChunkCacheGenerator DEFAULT_CACHE_GENERATOR = ChunkCacheImpl::new;
        private static final FileAccessGenerator DEFAULT_FILE_ACCESS_GENERATOR = GekFile::from;
        private int chunkSize = 1024 * 4;
        private int bufferSize = GekIO.IO_BUFFER_SIZE;
        private ChunkCacheGenerator chunkCacheGenerator = DEFAULT_CACHE_GENERATOR;
        private FileAccessGenerator fileAccessGenerator = DEFAULT_FILE_ACCESS_GENERATOR;
        private CacheReadListener cacheReadListener = null;
        private CacheWriteListener cacheWriteListener = null;
        private FileReadListener fileReadListener = null;
        private FileWriteListener fileWriteListener = null;

        /**
         * Sets file chunk size for caching, default is {@link GekIO#IO_BUFFER_SIZE}.
         *
         * @param chunkSize chunk size
         * @return this builder
         */
        public Builder chunkSize(int chunkSize) {
            GekCheck.checkArgument(chunkSize > 0, "chunkSize must > 0.");
            this.chunkSize = chunkSize;
            return this;
        }

        /**
         * Sets buffer size for IO operation, default is {@link GekIO#IO_BUFFER_SIZE}.
         *
         * @param bufferSize buffer size
         * @return this builder
         */
        public Builder bufferSize(int bufferSize) {
            GekCheck.checkArgument(bufferSize > 0, "bufferSize must > 0.");
            this.bufferSize = bufferSize;
            return this;
        }

        /**
         * Sets cache generator, default uses {@link GekCache#softCache(GekCache.RemoveListener)}.
         *
         * @param chunkCacheGenerator cache generator
         * @return this builder
         */
        public Builder cacheGenerator(ChunkCacheGenerator chunkCacheGenerator) {
            this.chunkCacheGenerator = chunkCacheGenerator;
            return this;
        }

        /**
         * Sets generator to generate un-cached underlying file access, default uses {@link GekFile#from(Path)}.
         *
         * @param fileAccessGenerator generator to generate un-cached underlying file access
         * @return this builder
         */
        public Builder fileAccessGenerator(FileAccessGenerator fileAccessGenerator) {
            this.fileAccessGenerator = fileAccessGenerator;
            return this;
        }

        /**
         * Sets cache read listener, default is null.
         *
         * @param cacheReadListener cache read listener
         * @return this builder
         */
        public Builder cacheReadListener(@Nullable GekFileCache.CacheReadListener cacheReadListener) {
            this.cacheReadListener = cacheReadListener;
            return this;
        }

        /**
         * Sets cache write listener, default is null.
         *
         * @param cacheWriteListener cache write listener
         * @return this builder
         */
        public Builder cacheWriteListener(@Nullable GekFileCache.CacheWriteListener cacheWriteListener) {
            this.cacheWriteListener = cacheWriteListener;
            return this;
        }

        /**
         * Sets underlying file read listener, default is null.
         *
         * @param fileReadListener file read listener
         * @return this builder
         */
        public Builder fileReadListener(@Nullable GekFileCache.FileReadListener fileReadListener) {
            this.fileReadListener = fileReadListener;
            return this;
        }

        /**
         * Sets underlying file write listener, default is null.
         *
         * @param fileWriteListener file write listener
         * @return this builder
         */
        public Builder fileWriteListener(@Nullable GekFileCache.FileWriteListener fileWriteListener) {
            this.fileWriteListener = fileWriteListener;
            return this;
        }

        /**
         * Builds {@link GekFileCache}.
         *
         * @return built {@link GekFileCache}
         */
        public GekFileCache build() {
            return new GekFileCacheImpl(
                chunkSize,
                bufferSize,
                chunkCacheGenerator,
                fileAccessGenerator,
                cacheReadListener,
                cacheWriteListener,
                fileReadListener,
                fileWriteListener
            );
        }

        private static final class ChunkCacheImpl implements ChunkCache {

            private final GekCache<ChunkIndex, Chunk> cache;

            private ChunkCacheImpl(ChunkCacheGenerator.RemoveListener removeListener) {
                this.cache = GekCache.softCache((cache, key) ->
                    removeListener.onCacheRemove(key, ChunkCacheImpl.this));
            }

            @Override
            public @Nullable Chunk get(ChunkIndex key, @Nullable Function<ChunkIndex, Chunk> function) {
                if (function == null) {
                    return cache.get(key);
                }
                return cache.get(key, function);
            }

            @Override
            public void put(ChunkIndex key, Chunk value) {
                cache.put(key, value);
            }

            @Override
            public void remove(ChunkIndex key) {
                cache.remove(key);
            }

            @Override
            public void removeIf(BiPredicate<ChunkIndex, Chunk> predicate) {
                cache.removeIf(predicate);
            }

            @Override
            public int size() {
                return cache.size();
            }
        }

        private static final class GekFileCacheImpl implements GekFileCache {

            private static final Chunk EOF = new Chunk(new byte[0], true);

            private final int chunkSize;
            private final int bufferSize;
            private final FileAccessGenerator fileAccessGenerator;
            private final CacheReadListener cacheReadListener;
            private final CacheWriteListener cacheWriteListener;
            private final FileReadListener fileReadListener;
            private final FileWriteListener fileWriteListener;
            private final ChunkCache chunkCache;

            private GekFileCacheImpl(
                int chunkSize,
                int bufferSize,
                ChunkCacheGenerator chunkCacheGenerator,
                FileAccessGenerator fileAccessGenerator,
                CacheReadListener cacheReadListener,
                CacheWriteListener cacheWriteListener,
                FileReadListener fileReadListener,
                FileWriteListener fileWriteListener
            ) {
                this.chunkSize = chunkSize;
                this.bufferSize = bufferSize;
                this.fileAccessGenerator = fileAccessGenerator;
                this.cacheReadListener = cacheReadListener;
                this.cacheWriteListener = cacheWriteListener;
                this.fileReadListener = fileReadListener;
                this.fileWriteListener = fileWriteListener;
                this.chunkCache = chunkCacheGenerator.generate(new ChunkCacheGenerator.RemoveListener() {
                    @Override
                    public void onCacheRemove(ChunkIndex key, ChunkCache cache) {
                        chunkCache.remove(key);
                    }
                });
            }

            @Override
            public InputStream getInputStream(Path path, long offset) {
                return new CacheInputStream(path, offset);
            }

            @Override
            public OutputStream getOutputStream(Path path, long offset) {
                return new CacheOutputStream(path, offset);
            }

            @Override
            public void setFileLength(Path path, long newLength) {
                try {
                    RandomAccessFile random = new RandomAccessFile(path.toFile(), "rws");
                    random.setLength(newLength);
                    random.close();
                    chunkCache.removeIf((c, v) -> Objects.equals(path.toString(), c.path));
                } catch (Exception e) {
                    throw new GekFileException(e);
                }
            }

            @Override
            public int cachedChunkCount() {
                return chunkCache.size();
            }

            private final class CacheInputStream extends InputStream {

                private final Path path;
                private final GekFile underlying;
                private long pos;
                private InputStream buffered;
                private long bufferedPos;

                CacheInputStream(Path path, long offset) {
                    try {
                        GekCheck.checkArgument(offset >= 0, "offset must >= 0.");
                        this.path = path;
                        this.underlying = fileAccessGenerator.generate(path);
                        this.pos = offset;
                    } catch (Exception e) {
                        throw new GekFileException(e);
                    }
                }

                @Override
                public synchronized int read(byte[] b, int off, int len) throws IOException {
                    try {
                        GekCheck.checkRangeInBounds(off, off + len, 0, b.length);
                        if (pos == -1) {
                            return -1;
                        }
                        if (len == 0) {
                            return 0;
                        }
                        int readSize = read0(b, off, len);
                        if (readSize == -1) {
                            pos = -1;
                            return -1;
                        } else {
                            pos += readSize;
                        }
                        return readSize;
                    } catch (IOException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new IOException(e);
                    }
                }

                private int read0(byte[] b, int off, int len) throws IOException {
                    int offset = off;
                    int remaining = len;
                    final LongRef chunkIndex = GekRef.ofLong(pos / chunkSize);
                    long chunkOffset = pos % chunkSize;
                    String pathString = path.toString();
                    ChunkIndex chunkIndexKey;
                    final BooleanRef cacheFlag = GekRef.ofBoolean(true);
                    while (true) {
                        chunkIndexKey = new ChunkIndex(pathString, chunkIndex.get());
                        long chunkPos = chunkIndex.get() * chunkSize;
                        Chunk chunk = chunkCache.get(chunkIndexKey, k -> {
                            cacheFlag.set(false);
                            if (!underlying.isOpened()) {
                                underlying.open("r");
                            }
                            if (buffered == null || bufferedPos != chunkPos) {
                                if (underlying.position() != chunkPos) {
                                    underlying.position(chunkPos);
                                }
                                buffered = new BufferedInputStream(underlying.bindInputStream(), bufferSize);
                            }
                            byte[] readBytes = GekIO.read(buffered, chunkSize);
                            if (readBytes == null) {
                                return EOF;
                            }
                            bufferedPos += readBytes.length;
                            Chunk newChunk = readBytes.length < chunkSize ?
                                new Chunk(readBytes, true) : new Chunk(readBytes, false);
                            if (cacheWriteListener != null) {
                                cacheWriteListener.onCacheWrite(path, chunkPos, readBytes.length);
                            }
                            return newChunk;
                        });
                        if (chunk.data.length > 0) {
                            int copySize = Math.min(remaining, chunk.data.length - (int) chunkOffset);
                            System.arraycopy(chunk.data, (int) chunkOffset, b, offset, copySize);
                            offset += copySize;
                            remaining -= copySize;
                            if (cacheFlag.get()) {
                                if (cacheReadListener != null) {
                                    cacheReadListener.onCacheRead(path, chunkPos + chunkOffset, copySize);
                                }
                            } else {
                                if (fileReadListener != null) {
                                    fileReadListener.onFileRead(path, chunkPos + chunkOffset, copySize);
                                }
                            }
                        }
                        if (remaining <= 0 || chunk.eof) {
                            break;
                        }
                        cacheFlag.set(true);
                        chunkIndex.incrementAndGet();
                        chunkOffset = 0;
                    }
                    if (remaining == len) {
                        return -1;
                    }
                    return len - remaining;
                }

                @Override
                public synchronized int read() throws IOException {
                    byte[] dest = new byte[1];
                    int read = read(dest, 0, 1);
                    if (read == -1) {
                        return -1;
                    }
                    return dest[0] & 0x000000ff;
                }

                @Override
                public synchronized int available() throws IOException {
                    if (underlying.isOpened()) {
                        long available = underlying.length() - pos;
                        if (available > Integer.MAX_VALUE) {
                            return Integer.MAX_VALUE;
                        }
                        return available <= 0 ? 0 : (int) available;
                    }
                    long posIndex = pos / chunkSize;
                    long posOffset = pos % chunkSize;
                    Chunk chunk = chunkCache.get(new ChunkIndex(path.toString(), posIndex), null);
                    if (chunk == null || posOffset >= chunk.data.length) {
                        return 0;
                    }
                    return chunk.data.length - (int) posIndex;
                }

                @Override
                public synchronized void close() throws IOException {
                    try {
                        underlying.close();
                    } catch (Exception e) {
                        throw new IOException(e);
                    }
                }
            }

            private final class CacheOutputStream extends OutputStream {

                private final Path path;
                private final GekFile underlying;
                private long pos;
                private OutputStream buffered;
                private long bufferedPos;

                CacheOutputStream(Path path, long offset) {
                    try {
                        GekCheck.checkArgument(offset >= 0, "offset must >= 0.");
                        this.path = path;
                        this.underlying = fileAccessGenerator.generate(path);
                        this.pos = offset;
                    } catch (Exception e) {
                        throw new GekFileException(e);
                    }
                }

                @Override
                public synchronized void write(byte[] b, int off, int len) throws IOException {
                    try {
                        GekCheck.checkRangeInBounds(off, off + len, 0, b.length);
                        if (len == 0) {
                            return;
                        }
                        writeUnderlying(b, off, len);
                        String pathString = path.toString();
                        chunkCache.removeIf((chunkPos, data) -> Objects.equals(chunkPos.getPath(), pathString));
                        pos += len;
                    } catch (IOException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new IOException(e);
                    }
                }

                private void writeUnderlying(byte[] b, int off, int len) throws IOException {
                    if (buffered != null && bufferedPos != pos) {
                        buffered.flush();
                    }
                    if (!underlying.isOpened()) {
                        underlying.open("rw");
                    }
                    if (buffered == null || bufferedPos != pos) {
                        buffered = new BufferedOutputStream(underlying.bindOutputStream(), bufferSize);
                        bufferedPos = pos;
                    }
                    if (underlying.position() != pos) {
                        underlying.position(pos);
                    }
                    buffered.write(b, off, len);
                    bufferedPos += len;
                    if (fileWriteListener != null) {
                        fileWriteListener.onFileWrite(path, pos, len);
                    }
                }

                @Override
                public synchronized void write(int b) throws IOException {
                    write(new byte[]{(byte) b});
                }

                @Override
                public synchronized void flush() throws IOException {
                    if (buffered != null && bufferedPos == pos) {
                        buffered.flush();
                    }
                    if (underlying.isOpened()) {
                        try {
                            underlying.sync();
                        } catch (Exception e) {
                            throw new IOException(e);
                        }
                    }
                }

                @Override
                public synchronized void close() throws IOException {
                    try {
                        underlying.close();
                    } catch (Exception e) {
                        throw new IOException(e);
                    }
                }
            }
        }
    }
}
