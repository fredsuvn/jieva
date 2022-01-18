package test.java.xyz.srclab.common.net.http;

import com.google.common.net.HttpHeaders;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BString;
import xyz.srclab.common.collect.BMap;
import xyz.srclab.common.io.BIO;
import xyz.srclab.common.net.http.BHttp;
import xyz.srclab.common.net.http.HttpConnect;
import xyz.srclab.common.net.http.HttpReq;
import xyz.srclab.common.net.http.HttpResp;
import xyz.srclab.common.net.socket.BSocket;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class HttpTest {

    private static final String TEST_RESP_EMPTY_BODY = "TEST_RESP_EMPTY_BODY";
    private static final String TEST_RESP_BODY = "TEST_RESP_BODY";
    private static final String TEST_REQ_BODY = "TEST_REQ_BODY";

    @Test
    public void testUrl() throws Exception {
        String url = BHttp.newUrl(BHttp.HTTP_PROTOCOL, "localhost");
        BLog.info(url);
        Assert.assertEquals(url, "http://localhost");

        url = BHttp.newUrl(BHttp.HTTP_PROTOCOL, "localhost", "test/ttt");
        BLog.info(url);
        Assert.assertEquals(url, "http://localhost/test/ttt");

        url = BHttp.newUrl(BHttp.HTTP_PROTOCOL, "localhost", "test/ttt", BMap.newMap("a", "1", "中+ +文", "简+ +体"));
        BLog.info(url);
        Assert.assertEquals(url, "http://localhost/test/ttt?a=1&%E4%B8%AD%2B%20%2B%E6%96%87=%E7%AE%80%2B%20%2B%E4%BD%93");

        url = BHttp.newUrl(BHttp.HTTP_PROTOCOL, "localhost", "test/ttt", Collections.emptyMap(), "aaa");
        BLog.info(url);
        Assert.assertEquals(url, "http://localhost/test/ttt#aaa");

        url = BHttp.newUrl(BHttp.HTTP_PROTOCOL, "localhost", 666, "test/ttt", Collections.emptyMap(), "aaa");
        BLog.info(url);
        Assert.assertEquals(url, "http://localhost:666/test/ttt#aaa");

        url = BHttp.newUrlWithAuth(BHttp.HTTP_PROTOCOL, "user", null, "localhost", 666, "test/ttt");
        BLog.info(url);
        Assert.assertEquals(url, "http://user@localhost:666/test/ttt");

        url = BHttp.newUrlWithAuth(BHttp.HTTPS_PROTOCOL, "user", "pass", "localhost", 666, "test/ttt");
        BLog.info(url);
        Assert.assertEquals(url, "https://user:pass@localhost:666/test/ttt");
    }

    @Test
    public void testRequest() throws Exception {
        int port = BSocket.availableSocketPort();
        TestServer testServer = new TestServer();
        testServer.start(port);

        HttpResp resp = BHttp.request("http://localhost:" + port + "/empty");
        String body = resp.bodyAsString();
        BLog.info("resp: {}", body);
        Assert.assertEquals(body, TEST_RESP_EMPTY_BODY);

        HttpConnect connect = BHttp.connect("http://localhost:" + port + "/empty");
        resp = connect.getResponse(true);
        body = resp.bodyAsString();
        BLog.info("resp: {}", body);
        Assert.assertEquals(body, TEST_RESP_EMPTY_BODY);


        HttpReq req = new HttpReq();
        req.setUrl("http://localhost:" + port + "/body");
        req.setMethod(BHttp.HTTP_POST_METHOD);
        req.setContentLength(TEST_REQ_BODY.length());
        req.setBody(BIO.asInputStream(TEST_REQ_BODY.getBytes(StandardCharsets.UTF_8)));
        resp = BHttp.request(req);
        body = resp.bodyAsString();
        BLog.info("resp: {}", body);
        Assert.assertEquals(body, TEST_RESP_BODY);

        connect = BHttp.connect(req);
        req.setBody(BIO.asInputStream(TEST_REQ_BODY.getBytes(StandardCharsets.UTF_8)));
        resp = connect.getResponse(true);
        body = resp.bodyAsString();
        BLog.info("resp: {}", body);
        Assert.assertEquals(body, TEST_RESP_BODY);

        testServer.close();
    }

    private static class TestServer {

        private static EventLoopGroup bossGroup;
        private static EventLoopGroup workerGroup;

        public void start(int port) throws Exception {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)
                        throws Exception {
                        // server端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
                        ch.pipeline().addLast(
                            new HttpResponseEncoder());
                        // server端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
                        ch.pipeline().addLast(
                            new HttpRequestDecoder());
                        ch.pipeline().addLast(
                            new HttpServerHandler());
                    }
                }).option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = b.bind(port).channel().closeFuture();
            //f.sync();
        }

        public void close() {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private static class HttpServerHandler extends ChannelInboundHandlerAdapter {

        private boolean isEmpty = false;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
            if (msg instanceof LastHttpContent) {
                return;
            }
            if (msg instanceof HttpRequest) {
                String uri = ((HttpRequest) msg).uri();
                BLog.info("req uri: {}", uri);
                if ("/empty".equals(uri)) {
                    isEmpty = true;
                    sendMessage(ctx, TEST_RESP_EMPTY_BODY);
                }
                if ("/body".equals(uri)) {
                    isEmpty = false;
                    sendMessage(ctx, TEST_RESP_BODY);
                }
            }
            if (msg instanceof HttpContent) {
                if (!isEmpty) {
                    String body = ((HttpContent) msg).content().toString(BString.DEFAULT_CHARSET);
                    BLog.info("req body: {}", body);
                    Assert.assertEquals(body, TEST_REQ_BODY);
                }
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
            ctx.close();
        }

        private void sendMessage(ChannelHandlerContext ctx, String data) {
            byte[] bytes = data.getBytes(BString.DEFAULT_CHARSET);
            FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(bytes));
            response.headers().set(HttpHeaders.CONTENT_TYPE, "text/plain;charset=utf-8");
            response.headers().set(HttpHeaders.CONTENT_LENGTH, bytes.length);
            response.headers().set(HttpHeaders.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            ctx.write(response);
            ctx.flush();
        }
    }
}
