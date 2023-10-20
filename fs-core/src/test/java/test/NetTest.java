package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.base.FsChars;
import xyz.fsgek.common.base.FsLogger;
import xyz.fsgek.common.base.FsString;
import xyz.fsgek.common.base.FsThread;
import xyz.fsgek.common.collect.FsCollect;
import xyz.fsgek.common.data.FsData;
import xyz.fsgek.common.io.FsBuffer;
import xyz.fsgek.common.io.FsIO;
import xyz.fsgek.common.net.FsNetException;
import xyz.fsgek.common.net.FsNetServerException;
import xyz.fsgek.common.net.http.FsHttp;
import xyz.fsgek.common.net.http.FsHttpHeaders;
import xyz.fsgek.common.net.http.FsHttpResponse;
import xyz.fsgek.common.net.tcp.*;
import xyz.fsgek.common.net.udp.*;
import xyz.fsgek.common.net.tcp.handlers.DelimiterBasedTcpChannelHandler;
import xyz.fsgek.common.net.tcp.handlers.LengthBasedTcpChannelHandler;

import java.io.ByteArrayInputStream;
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
        testTcpLengthBased(6, 5, 10);
        testTcpLengthBased(1024, 8, 20);
        testTcpLengthBased(22, 8, 50);
        testTcpDelimiterBased(6, 5, 10);
        testTcpDelimiterBased(1024, 8, 20);
        testTcpDelimiterBased(22, 8, 50);
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

    private void testTcpLengthBased(int bufferSize, int serverThreads, int clientThreads) {
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
                        String str = FsBuffer.getString(buffer);
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
                                    FsThread.sleep(200);
                                    channel.sendAndFlush(FsData.wrap("bc"));
                                    FsThread.sleep(200);
                                    channel.sendAndFlush(FsData.wrap("abc"));
                                    FsThread.sleep(200);
                                    channel.sendAndFlush(FsData.wrap("ab"));
                                    FsThread.sleep(200);
                                    channel.sendAndFlush(FsData.wrap("ca"));
                                    FsThread.sleep(200);
                                    channel.sendAndFlush(FsData.wrap("bca"));
                                    FsThread.sleep(200);
                                    channel.sendAndFlush(FsData.wrap("bc"));
                                    FsThread.sleep(200);
                                    channel.sendAndFlush(FsData.wrap("abcabcabcabcabc"));
                                    FsThread.sleep(500);
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
        byte[] dataBytes = data.getBytes(FsChars.defaultCharset());
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
        byte[] bytes = FsBuffer.getBytes(buffer);
        if (bytes.length != 6) {
            throw new FsNetException("bytes.length != 6");
        }
        return new String(bytes, 3, 3, FsChars.defaultCharset());
    }

    private void testTcpDelimiterBased(int bufferSize, int serverThreads, int clientThreads) {
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
                    channel.sendAndFlush(FsData.wrap("hlo|"));
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
            .addChannelHandler(new DelimiterBasedTcpChannelHandler((byte) '|'))
            .addChannelHandler(new FsTcpChannelHandler<List<ByteBuffer>>() {
                @Override
                public @Nullable Object onMessage(FsTcpChannel channel, List<ByteBuffer> message) {
                    for (ByteBuffer buffer : message) {
                        String str = FsBuffer.getString(buffer);
                        TestUtil.count(str, data);
                        System.out.println("TCP receive (" + channel.getRemoteSocketAddress() + "): " + str);
                        switch (str) {
                            case "abc": {
                                channel.sendAndFlush(FsData.wrap("qwe|"));
                                break;
                            }
                            case "bye": {
                                channel.sendAndFlush(FsData.wrap("bye|"));
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
            .addChannelHandler(new DelimiterBasedTcpChannelHandler((byte) '|'))
            .addChannelHandler(new FsTcpChannelHandler<List<ByteBuffer>>() {
                @Override
                public @Nullable Object onMessage(FsTcpChannel channel, List<ByteBuffer> message) {
                    for (ByteBuffer buffer : message) {
                        String str = FsBuffer.getString(buffer);
                        TestUtil.count(str, data);
                        switch (str) {
                            case "hlo": {
                                new Thread(() -> {
                                    channel.sendAndFlush(FsData.wrap("a"));
                                    FsThread.sleep(200);
                                    channel.sendAndFlush(FsData.wrap("bc|"));
                                    FsThread.sleep(200);
                                    channel.sendAndFlush(FsData.wrap("abc|"));
                                    FsThread.sleep(200);
                                    channel.sendAndFlush(FsData.wrap("ab"));
                                    FsThread.sleep(200);
                                    channel.sendAndFlush(FsData.wrap("c|a"));
                                    FsThread.sleep(200);
                                    channel.sendAndFlush(FsData.wrap("bc|a"));
                                    FsThread.sleep(200);
                                    channel.sendAndFlush(FsData.wrap("bc"));
                                    FsThread.sleep(200);
                                    channel.sendAndFlush(FsData.wrap("|abc|abc|abc|abc|abc"));
                                    FsThread.sleep(500);
                                    channel.sendAndFlush(FsData.wrap("|bye|"));
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
                    String str = FsBuffer.getString(buffer);
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
                            ByteBuffer.wrap("udp-client".getBytes(FsChars.defaultCharset())),
                            new InetSocketAddress("localhost", udpServer.getPort()))
                        );
                        FsThread.sleep(50);
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
        FsHttpResponse response1 = FsHttp.get(
            "http://localhost:" + server.getPort(),
            FsHttpHeaders.of(
                "Connection", "keep-alive"
            )
        );
        System.out.println("response1 Server received:");
        System.out.println(new String(received.toByteArray(), FsChars.defaultCharset()));
        System.out.println("response1 Client response header:");
        FsHttpHeaders headers = response1.getHeaders();
        System.out.println(headers);
        InputStream body = response1.getBody();
        System.out.println("response1 Client response body:");
        String bodyString = FsIO.readString(body);
        System.out.println(bodyString);
        body.close();
        Assert.assertEquals(headers.getHeaderFirst("Content-Length"), "9");
        Assert.assertEquals(headers.getHeader("Content-Length2"), Arrays.asList("9", "9", "9"));
        Assert.assertEquals(bodyString, "Fuck Off!");
        received.reset();
        System.out.println("----------------------------------");
        FsHttpResponse response2 = FsHttp.get(
            "http://localhost:" + server.getPort(),
            FsCollect.hashMap("hello", "hello world")
        );
        System.out.println("response2 Server received:");
        System.out.println(new String(received.toByteArray(), FsChars.defaultCharset()));
        System.out.println("response2 Client response header:");
        headers = response2.getHeaders();
        System.out.println(headers);
        body = response2.getBody();
        System.out.println("response2 Client response body:");
        bodyString = FsIO.readString(body);
        System.out.println(bodyString);
        body.close();
        Assert.assertEquals(headers.getHeaderFirst("Content-Length"), "9");
        Assert.assertEquals(headers.getHeader("Content-Length2"), Arrays.asList("9", "9", "9"));
        Assert.assertEquals(bodyString, "Fuck Off!");
        received.reset();
        System.out.println("----------------------------------");
        FsHttpResponse response3 = FsHttp.post(
            "http://localhost:" + server.getPort(),
            new ByteArrayInputStream(FsString.encode("request3"))
        );
        System.out.println("response3 Server received:");
        System.out.println(new String(received.toByteArray(), FsChars.defaultCharset()));
        System.out.println("response3 Client response header:");
        headers = response3.getHeaders();
        System.out.println(headers);
        body = response3.getBody();
        System.out.println("response3 Client response body:");
        bodyString = FsIO.readString(body);
        System.out.println(bodyString);
        body.close();
        Assert.assertEquals(headers.getHeaderFirst("Content-Length"), "9");
        Assert.assertEquals(headers.getHeader("Content-Length2"), Arrays.asList("9", "9", "9"));
        Assert.assertEquals(bodyString, "Fuck Off!");
        received.reset();
        System.out.println("----------------------------------");
        FsHttpResponse response4 = FsHttp.post(
            "http://localhost:" + server.getPort(),
            ByteBuffer.wrap(FsString.encode("request4"))
        );
        System.out.println("response4 Server received:");
        System.out.println(new String(received.toByteArray(), FsChars.defaultCharset()));
        System.out.println("response4 Client response header:");
        headers = response4.getHeaders();
        System.out.println(headers);
        body = response4.getBody();
        System.out.println("response4 Client response body:");
        bodyString = FsIO.readString(body);
        System.out.println(bodyString);
        body.close();
        Assert.assertEquals(headers.getHeaderFirst("Content-Length"), "9");
        Assert.assertEquals(headers.getHeader("Content-Length2"), Arrays.asList("9", "9", "9"));
        Assert.assertEquals(bodyString, "Fuck Off!");
        received.reset();
        System.out.println("----------------------------------");

        server.close();
    }
}
