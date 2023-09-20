// package xyz.srclab.common.net;
//
// import xyz.srclab.annotations.concurrent.ThreadSafe;
//
// import java.io.InputStream;
// import java.nio.ByteBuffer;
//
// /**
//  * Byte buffer of network channel to read and consume.
//  * <p>
//  * This is a limited version of {@link ByteBuffer} which opens methods about
//  * <ul>
//  *     <li>
//  *         <b>get</b> methods to read bytes;
//  *     </li>
//  *     <li>
//  *         <b>position and slice</b> methods to control read bounds.
//  *     </li>
//  * </ul>
//  *
//  * @author fredsuv
//  */
// @ThreadSafe
// public interface FsNetBuffer {
//
//     /**
//      * Returns current position, same with {@link ByteBuffer#position()}.
//      */
//     int position();
//
//     /**
//      * Sets current position, same with {@link ByteBuffer#position(int)}.
//      */
//     void position(int position);
//
//     /**
//      * Returns limit of this buffer, same with {@link ByteBuffer#limit()}.
//      */
//     int limit();
//
//     /**
//      * Returns whether this buffer has remaining bytes, same with {@link ByteBuffer#hasRemaining()}.
//      */
//     boolean hasRemaining();
//
//     /**
//      * Returns remaining byte number of this buffer, same with {@link ByteBuffer#remaining()}.
//      */
//     int remaining();
//
//     /**
//      * Reads bytes into dest array from current position, same with {@link ByteBuffer#get(byte[])}.
//      *
//      * @param dest dest array
//      */
//     void get(byte[] dest);
//
//     /**
//      * Reads bytes into dest array from current position, same with {@link ByteBuffer#get(byte[], int, int)}.
//      *
//      * @param dest   dest array
//      * @param offset to read into
//      * @param length length of read bytes
//      */
//     void get(byte[] dest, int offset, int length);
//
//     /**
//      * Reads bytes into dest buffer from current position, same with inverted of {@link ByteBuffer#put(ByteBuffer)}.
//      *
//      * @param dest dest buffer
//      */
//     void get(ByteBuffer dest);
//
//     /**
//      * Returns byte at current position, same with {@link ByteBuffer#get()}.
//      */
//     byte getByte();
//
//     /**
//      * Returns next 2 bytes as short at current position, same with {@link ByteBuffer#getShort()}.
//      */
//     short getShort();
//
//     /**
//      * Returns next 2 bytes as char at current position, same with {@link ByteBuffer#getChar()}.
//      */
//     char getChar();
//
//     /**
//      * Returns next 4 bytes as int at current position, same with {@link ByteBuffer#getInt()}.
//      */
//     int getInt();
//
//     /**
//      * Returns next 8 bytes as long at current position, same with {@link ByteBuffer#getLong()}.
//      */
//     long getLong();
//
//     /**
//      * Returns next 4 bytes as float at current position, same with {@link ByteBuffer#getFloat()}.
//      */
//     float getFloat();
//
//     /**
//      * Returns next 8 bytes as double at current position, same with {@link ByteBuffer#getDouble()}.
//      */
//     double getDouble();
//
//     /**
//      * Marks current position, same with {@link ByteBuffer#mark()}.
//      */
//     void mark();
//
//     /**
//      * Resets current position, same with {@link ByteBuffer#reset()}.
//      */
//     void reset();
//
//     /**
//      * Returns a new reference of which content is shared with this buffer, same with {@link ByteBuffer#slice()}.
//      */
//     FsNetBuffer slice();
//
//     /**
//      * Returns a new reference of which content is shared with this buffer, same with {@link ByteBuffer#slice()}.
//      * And set new buffer's limit to given limit
//      *
//      * @param limit given limit
//      */
//     FsNetBuffer slice(int limit);
//
//     /**
//      * Returns an input stream of which content comes from this buffer,
//      * and the read operations of the stream will equally move the position of this buffer.
//      */
//     InputStream toInputStream();
// }
