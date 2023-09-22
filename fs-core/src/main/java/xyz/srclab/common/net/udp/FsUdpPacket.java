package xyz.srclab.common.net.udp;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Represents UDP datagram packet, usually based on {@link DatagramPacket}.
 *
 * @author fredsuvn
 */
public interface FsUdpPacket {

    /**
     * Returns UDP packet of which content comes from given datagram packet.
     *
     * @param packet given packet
     */
    static FsUdpPacket from(DatagramPacket packet) {
        byte[] data = Arrays.copyOfRange(
            packet.getData(), packet.getOffset(), packet.getOffset() + packet.getLength());
        ByteBuffer buffer = ByteBuffer.wrap(data);
        FsUdpHeader header = FsUdpHeader.from(packet);
        return new FsUdpPacket() {

            @Override
            public FsUdpHeader getHeader() {
                return header;
            }

            @Override
            public ByteBuffer getData() {
                return buffer;
            }
        };
    }

    /**
     * Returns header info of this datagram packet.
     */
    FsUdpHeader getHeader();

    /**
     * Returns readonly data of this datagram packet.
     */
    ByteBuffer getData();
}
