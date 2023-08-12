// package xyz.srclab.common.io;
//
// import xyz.srclab.annotations.Nullable;
// import xyz.srclab.common.cache.FsCache;
//
// import java.util.Map;
// import java.util.concurrent.ConcurrentHashMap;
// import java.util.concurrent.atomic.AtomicLong;
//
// /**
//  * @author fredsuvn
//  */
// public class FsFileCache {
//
//     private final int segmentSize;
//     private final Map<String, FsCache<BytesValue>> cache = new ConcurrentHashMap<>();
//     private final AtomicLong bytesCount = new AtomicLong(0);
//
//     public FsFileCache(int segmentSize) {
//         this.segmentSize = segmentSize;
//     }
//
//     @Nullable
//     private BytesValue getBytesValue(String path, BytesKey bytesKey) {
//         FsCache<BytesValue> fileCache = cache.get(path);
//         if (fileCache == null) {
//             return null;
//         }
//         return fileCache.get(bytesKey);
//     }
//
//     private FsCache<BytesValue> buildFileCache() {
//         return FsCache.newCache(key -> {
//             BytesKey bytesKey = (BytesKey) key;
//
//         });
//     }
//
//     private void putBytesValue(String path) {
//
//     }
//
//     private final class BytesKey {
//
//         private final long startPos;
//         private final long endPos;
//
//         private BytesKey(long startPos, long endPos) {
//             this.startPos = startPos;
//             this.endPos = endPos;
//         }
//     }
//
//     private static final class FileCache {
//
//         private final String path;
//         private final FsCache<BytesValue> cache = FsCache.newCache(key -> {
//             BytesKey bytesKey = (BytesKey) key;
//         });
//
//         private FileCache(String path) {
//             this.path = path;
//         }
//     }
//
//     private final class BytesValue {
//
//         private final String path;
//         private final byte[] data = new byte[segmentSize];
//         private int length;
//     }
// }
