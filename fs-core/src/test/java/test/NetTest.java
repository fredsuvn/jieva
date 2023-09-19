package test;

import org.testng.annotations.Test;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.base.FsLogger;
import xyz.srclab.common.data.FsData;
import xyz.srclab.common.io.FsIO;
import xyz.srclab.common.net.*;
import xyz.srclab.common.net.handlers.FixedLengthTcpChannelHandler;

import java.nio.ByteBuffer;
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
        testTcp0(2, 5, 10);
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
            .bufferSize(bufferSize)
            .executor(Executors.newFixedThreadPool(serverThreads))
            .serverHandler(new FsTcpServerHandler() {
                @Override
                public void onException(FsNetServerException exception) {
                    FsLogger.defaultLogger().info("server.onException: ", exception);
                }

                @Override
                public void onOpen(FsTcpChannel channel) {
                    channel.send(FsData.wrap("hlo"));
                    TestUtil.count("server-onOpen", data);
                }

                @Override
                public void onClose(FsTcpChannel channel) {
                    FsLogger.defaultLogger().info("server-channel.onClose: ");
                    TestUtil.count("server-onClose", data);
                }

                @Override
                public void onException(FsTcpChannel channel, Throwable throwable) {
                    FsLogger.defaultLogger().info("server-channel.onException: ", throwable);
                }
            })
            .addChannelHandler(new FixedLengthTcpChannelHandler(3))
            .addChannelHandler(new FsTcpChannelHandler<List<ByteBuffer>>() {
                @Override
                public @Nullable Object onMessage(FsTcpChannel channel, List<ByteBuffer> message) {
                    for (ByteBuffer buffer : message) {
                        String str = FsIO.getString(buffer);
                        TestUtil.count(str, data);
                        switch (str) {
                            case "abc": {
                                channel.send(FsData.wrap("qwe"));
                                break;
                            }
                            case "bye": {
                                channel.send(FsData.wrap("bye"));
                                channel.flush();
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
        CountDownLatch latch = new CountDownLatch(clientThreads);
        List<FsTcpClient> clients = new LinkedList<>();
        for (int i = 0; i < clientThreads; i++) {
            FsTcpClient client = FsTcpClient.newBuilder()
                .bufferSize(bufferSize)
                .clientHandler(new FsTcpClientHandler() {
                    @Override
                    public void onOpen(FsTcpChannel channel) {
                        TestUtil.count("client-onOpen", data);
                    }

                    @Override
                    public void onClose(FsTcpChannel channel) {
                        TestUtil.count("client-onClose", data);
                    }

                    @Override
                    public void onException(FsTcpChannel channel, Throwable throwable) {
                        FsLogger.defaultLogger().info("client-channel.onException: ", throwable);
                    }
                })
                .addChannelHandler(new FixedLengthTcpChannelHandler(3))
                .addChannelHandler(new FsTcpChannelHandler<List<ByteBuffer>>() {
                    @Override
                    public @Nullable Object onMessage(FsTcpChannel channel, List<ByteBuffer> message) {
                        for (ByteBuffer buffer : message) {
                            String str = FsIO.getString(buffer);
                            TestUtil.count(str, data);
                            switch (str) {
                                case "hlo": {
                                    new Thread(() -> {
                                        channel.send(FsData.wrap("a"));
                                        Fs.sleep(200);
                                        channel.send(FsData.wrap("bc"));
                                        Fs.sleep(200);
                                        channel.send(FsData.wrap("abc"));
                                        Fs.sleep(200);
                                        channel.send(FsData.wrap("ab"));
                                        Fs.sleep(200);
                                        channel.send(FsData.wrap("ca"));
                                        Fs.sleep(200);
                                        channel.send(FsData.wrap("bca"));
                                        Fs.sleep(200);
                                        channel.send(FsData.wrap("bc"));
                                        Fs.sleep(200);
                                        channel.send(FsData.wrap("abcabcabcabcabc"));
                                        channel.flush();
                                    }).start();
                                    break;
                                }
                                case "bye": {
                                    channel.flush();
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
            clients.add(client);
        }
        server.start(false);
        for (FsTcpClient client : clients) {
            new Thread(() -> {
                try {
                    client.start();
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
        System.out.println(data);
    }
}
