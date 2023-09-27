package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fs404.annotations.Nullable;
import xyz.fs404.common.base.Fs;
import xyz.fs404.common.base.FsLogger;
import xyz.fs404.common.base.FsString;
import xyz.fs404.common.collect.FsCollect;
import xyz.fs404.common.data.FsData;
import xyz.fs404.common.io.FsIO;
import xyz.fs404.common.net.FsNetException;
import xyz.fs404.common.net.FsNetServerException;
import xyz.fs404.common.net.http.FsHttp;
import xyz.fs404.common.net.http.FsHttpResponse;
import xyz.fs404.common.net.tcp.*;
import xyz.fs404.common.net.udp.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class NetTest {

    @Test
    public void testTcp() {
        testTcp0(6, 5, 10);
        testTcp0(1024, 8, 20);
        testTcp0(22, 8, 50);
    }

    @Test
    public void testUdp() {
        testUdp0(60, 5, 10);
        testUdp0(1024, 8, 20);
        testUdp0(22, 8, 50);
    }

    @Test
    public void testHttp() throws Exception {
        testHttp0();
    }

    private void testTcp0(int bufferSize, int serverThreads, int clientThreads) {
        Map<String, AtomicInteger> data = new ConcurrentHashMap<>();

        //server: hlo
        //client: hlo
        //client: abc * 10
        //server: qwe * 10
        //client: bye
        //server bye

        FsTcpServer server = FsTcpServer.newBuilder()
            .channelBufferSize(bufferSize)
            .executor(Executors.newFixedThreadPool(serverThreads))
            .serverHandler(new FsTcpServerHandler() {
                @Override
                public void onException(FsNetServerException exception) {
                    FsLogger.defaultLogger().info("server.onException: ", exception);
                }

                @Override
                public void onOpen(FsTcpChannel channel) {
                    TestUtil.count("server-onOpen", data);
                    channel.sendAndFlush(FsData.wrap(buildServerData("hlo")));
                    TestUtil.count("hlo", data);
                }

                @Override
                public void onClose(FsTcpChannel channel, ByteBuffer buffer) {
                    TestUtil.count("server-onClose", data);
                }

                @Override
                public void onException(FsTcpChannel channel, Throwable throwable, ByteBuffer buffer) {
                    TestUtil.count("server-channel.onException", data);
                    FsLogger.defaultLogger().info("server-channel.onException: ", throwable);
                }
            })
            .addChannelHandler(new LengthBasedTcpChannelHandler(3))
            .addChannelHandler(new FsTcpChannelHandler<List<ByteBuffer>>() {
                @Override
                public @Nullable Object onMessage(FsTcpChannel channel, List<ByteBuffer> message) {
                    for (ByteBuffer buffer : message) {
                        String str = FsIO.getString(buffer);
                        TestUtil.count(str, data);
                        System.out.println("TCP receive (" + channel.getRemoteSocketAddress() + "): " + str);
                        switch (str) {
                            case "abc": {
                                channel.sendAndFlush(FsData.wrap(buildServerData("qwe")));
                                break;
                            }
                            case "bye": {
                                channel.sendAndFlush(FsData.wrap(buildServerData("bye")));
                                //channel.flush();
                                channel.closeNow();
                                break;
                            }
                        }
                    }
                    //channel.flush();
                    return null;
                }
            })
            .build();
        server.start(false);
        server.closeNow();
        server = server.toBuilder().executor(Executors.newFixedThreadPool(serverThreads)).build();
        CountDownLatch latch = new CountDownLatch(clientThreads);
        FsTcpClient client = FsTcpClient.newBuilder()
            .channelBufferSize(bufferSize)
            .clientHandler(new FsTcpClientHandler() {
                @Override
                public void onOpen(FsTcpChannel channel) {
                    TestUtil.count("client-onOpen", data);
                }

                @Override
                public void onClose(FsTcpChannel channel, ByteBuffer buffer) {
                    TestUtil.count("client-onClose", data);
                }

                @Override
                public void onException(FsTcpChannel channel, Throwable throwable, ByteBuffer buffer) {
                    TestUtil.count("client-channel.onException", data);
                    FsLogger.defaultLogger().info("client-channel.onException: ", throwable);
                }
            })
            .addChannelHandler(new LengthBasedTcpChannelHandler(1, 2))
            .addChannelHandler(new FsTcpChannelHandler<List<ByteBuffer>>() {
                @Override
                public @Nullable Object onMessage(FsTcpChannel channel, List<ByteBuffer> message) {
                    for (ByteBuffer buffer : message) {
                        String str = parseServerData(buffer);
                        TestUtil.count(str, data);
                        switch (str) {
                            case "hlo": {
                                new Thread(() -> {
                                    channel.sendAndFlush(FsData.wrap("a"));
                                    Fs.sleep(200);
                                    channel.sendAndFlush(FsData.wrap("bc"));
                                    Fs.sleep(200);
                                    channel.sendAndFlush(FsData.wrap("abc"));
                                    Fs.sleep(200);
                                    channel.sendAndFlush(FsData.wrap("ab"));
                                    Fs.sleep(200);
                                    channel.sendAndFlush(FsData.wrap("ca"));
                                    Fs.sleep(200);
                                    channel.sendAndFlush(FsData.wrap("bca"));
                                    Fs.sleep(200);
                                    channel.sendAndFlush(FsData.wrap("bc"));
                                    Fs.sleep(200);
                                    channel.sendAndFlush(FsData.wrap("abcabcabcabcabc"));
                                    Fs.sleep(500);
                                    channel.sendAndFlush(FsData.wrap("bye"));
                                }).start();
                                break;
                            }
                            case "bye": {
                                //channel.flush();
                                channel.closeNow();
                                break;
                            }
                        }
                    }
                    channel.flush();
                    return null;
                }
            })
            .build();
        List<FsTcpClient> clients = new LinkedList<>();
        clients.add(client);
        for (int i = 0; i < clientThreads - 1; i++) {
            clients.add(client.toBuilder().build());
        }
        FsTcpServer tcpServer = server;
        tcpServer.start(false);
        for (FsTcpClient c : clients) {
            new Thread(() -> {
                try {
                    c.start("localhost", tcpServer.getPort());
                } catch (Exception e) {
                    System.out.println(e);
                }
                latch.countDown();
            }).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        tcpServer.close();
        //server: hlo
        //client: hlo
        //client: abc * 10
        //server: qwe * 10
        //client: bye
        //server bye
        Assert.assertEquals(data.get("server-onOpen").get(), clientThreads);
        Assert.assertEquals(data.get("client-onOpen").get(), clientThreads);
        Assert.assertEquals(data.get("server-onClose").get(), clientThreads);
        Assert.assertEquals(data.get("client-onClose").get(), clientThreads);
        Assert.assertEquals(data.get("hlo").get(), clientThreads * 2);
        Assert.assertEquals(data.get("bye").get(), clientThreads * 2);
        Assert.assertEquals(data.get("abc").get(), clientThreads * 10);
        Assert.assertEquals(data.get("qwe").get(), clientThreads * 10);
    }

    private byte[] buildServerData(String data) {
        byte[] dataBytes = data.getBytes(FsString.CHARSET);
        byte[] bytes = new byte[6];
        bytes[0] = 0;
        bytes[1] = 0;
        bytes[2] = 6;
        bytes[3] = dataBytes[0];
        bytes[4] = dataBytes[1];
        bytes[5] = dataBytes[2];
        return bytes;
    }

    private String parseServerData(ByteBuffer buffer) {
        byte[] bytes = FsIO.getBytes(buffer);
        if (bytes.length != 6) {
            throw new FsNetException("bytes.length != 6");
        }
        return new String(bytes, 3, 3, FsString.CHARSET);
    }

    private void testUdp0(int bufferSize, int serverThreads, int clientThreads) {
        Map<String, AtomicInteger> data = new ConcurrentHashMap<>();

        //client: udp-client * 10

        FsUdpServer server = FsUdpServer.newBuilder()
            .executor(Executors.newFixedThreadPool(serverThreads))
            .serverHandler(new FsUdpServerHandler() {
                @Override
                public void onException(FsNetServerException exception) {
                    FsLogger.defaultLogger().info("server.onException: ", exception);
                }

                @Override
                public void onException(FsUdpHeader header, FsUdpClient client, Throwable throwable, ByteBuffer buffer) {
                    TestUtil.count("server-header.onException", data);
                    FsLogger.defaultLogger().info("server-header.onException: ", throwable);
                }
            })
            .addPacketHandler(new FsUdpPacketHandler<ByteBuffer>() {
                @Override
                public @Nullable Object onPacket(FsUdpHeader header, FsUdpClient client, ByteBuffer buffer) {
                    String str = FsIO.getString(buffer);
                    TestUtil.count(str, data);
                    System.out.println("UDP receive (" + header.getInetSocketAddress() + "): " + str);
                    return null;
                }
            })
            .build();
        server.start(false);
        server.closeNow();
        server = server.toBuilder().executor(Executors.newFixedThreadPool(serverThreads)).build();
        CountDownLatch latch = new CountDownLatch(clientThreads);
        FsUdpClient client = FsUdpClient.newBuilder().build();
        List<FsUdpClient> clients = new LinkedList<>();
        clients.add(client);
        for (int i = 0; i < clientThreads - 1; i++) {
            clients.add(client.toBuilder().build());
        }
        FsUdpServer udpServer = server;
        udpServer.start(false);
        for (FsUdpClient c : clients) {
            new Thread(() -> {
                try {
                    for (int i = 0; i < 10; i++) {
                        c.send(FsUdpPacket.of(
                            ByteBuffer.wrap("udp-client".getBytes(FsString.CHARSET)),
                            new InetSocketAddress("localhost", udpServer.getPort()))
                        );
                        Fs.sleep(50);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
                latch.countDown();
            }).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        udpServer.close();
        //client: udp-client * 10
        Assert.assertEquals(data.get("udp-client").get(), clientThreads * 10);
    }

    private void testHttp0() throws Exception {
        ByteArrayOutputStream received = new ByteArrayOutputStream();
        FsTcpServer server = FsTcpServer.newBuilder()
            .executor(Executors.newFixedThreadPool(5))
            .addChannelHandler(new FsTcpChannelHandler<ByteBuffer>() {
                @Override
                public @Nullable Object onMessage(FsTcpChannel channel, ByteBuffer message) {
                    FsIO.readBytesTo(FsIO.toInputStream(message), received);
                    String response = "HTTP/1.1 200 OK\r\n" +
                        "Content-Length: 9\r\n" +
                        "Content-Length2: 9\r\n" +
                        "Content-Length2: 9\r\n" +
                        "\r\n" +
                        "Fuck Off!";
                    channel.sendAndFlush(response.getBytes(StandardCharsets.ISO_8859_1));
                    channel.close();
                    return null;
                }
            })
            .build();
        server.start(false);
        FsHttpResponse response = FsHttp.get(
            "http://localhost:" + server.getPort(),
            FsCollect.linkedHashMap(
                "Connection", "keep-alive"
            )
        );
        System.out.println("Server received:");
        System.out.println(new String(received.toByteArray(), FsString.CHARSET));
        System.out.println("Client response header:");
        Map<String, Object> headers = response.getHeaders();
        System.out.println(headers);
        InputStream body = response.getBody();
        System.out.println("Client response body:");
        String bodyString = FsIO.readString(body);
        System.out.println(bodyString);
        body.close();
        server.close();
        Assert.assertEquals(headers.get("Content-Length"), "9");
        Assert.assertEquals(headers.get("Content-Length2"), Arrays.asList("9", "9", "9", "9"));
        Assert.assertEquals(bodyString, "Fuck Off!");
    }
}
