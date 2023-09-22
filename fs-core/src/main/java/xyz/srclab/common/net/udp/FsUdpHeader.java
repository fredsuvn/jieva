package xyz.srclab.common.net.udp;

import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Represents header info of UDP datagram packet, such as address and port.
 *
 * @author fredsuvn
 */
public interface FsUdpHeader {

    /**
     * Returns UDP header of which content comes from given datagram packet.
     *
     * @param packet given packet
     */
    static FsUdpHeader from(DatagramPacket packet) {
        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        return new FsUdpHeader() {

            @Override
            public InetAddress getAddress() {
                return address;
            }

            @Override
            public int getPort() {
                return port;
            }
        };
    }

    /**
     * Returns address of the datagram packet.
     */
    InetAddress getAddress();

    /**
     * Returns port of the datagram packet.
     */
    int getPort();
}
