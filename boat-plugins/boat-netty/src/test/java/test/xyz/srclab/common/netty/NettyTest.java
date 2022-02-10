package test.xyz.srclab.common.netty;

import com.google.common.net.HttpHeaders;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BDefault;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.collect.BList;
import xyz.srclab.common.io.BIO;
import xyz.srclab.common.net.http.BHttp;
import xyz.srclab.common.net.http.HttpConnect;
import xyz.srclab.common.net.http.HttpReq;
import xyz.srclab.common.net.http.HttpResp;
import xyz.srclab.common.netty.BNetty;
import xyz.srclab.common.netty.SimpleNettyClient;
import xyz.srclab.common.netty.SimpleNettyServer;
import xyz.srclab.common.run.RunLatch;

import java.nio.charset.StandardCharsets;

public class NettyTest {

    private static final String TEST_RESP_EMPTY_BODY = "TEST_RESP_EMPTY_BODY";
    private static final String TEST_RESP_BODY = "TEST_RESP_BODY";
    private static final String TEST_REQ_BODY = "TEST_REQ_BODY";

    private static final String SIMPLE_CLIENT_MESSAGE = "Hello, Server!";
    private static final String SIMPLE_SERVER_MESSAGE = "Hello, Client!";

    private static final RunLatch runLatch = RunLatch.newRunLatch();

    @Test
    public void testHttpServer() throws Exception {
        SimpleNettyServer testServer = new SimpleNettyServer(
            BList.newList(HttpResponseEncoder::new, HttpRequestDecoder::new, HttpServerHandler::new)
        );
        int port = testServer.getPort();
        testServer.start();

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

    @Test
    public void testSimple() {
        SimpleNettyServer simpleNettyServer = new SimpleNettyServer(
            BList.newList(SimpleServerHandler::new)
        );
        simpleNettyServer.start();
        SimpleNettyClient simpleNettyClient = new SimpleNettyClient(
            "localhost:" + simpleNettyServer.getPort(),
            BList.newList(SimpleClientHandler::new)
        );
        runLatch.close();
        simpleNettyClient.start();
        runLatch.await();
        simpleNettyClient.close();
        simpleNettyServer.close();
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
                    String body = ((HttpContent) msg).content().toString(BDefault.DEFAULT_CHARSET);
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
            byte[] bytes = data.getBytes(BDefault.DEFAULT_CHARSET);
            FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(bytes));
            response.headers().set(HttpHeaders.CONTENT_TYPE, "text/plain;charset=utf-8");
            response.headers().set(HttpHeaders.CONTENT_LENGTH, bytes.length);
            response.headers().set(HttpHeaders.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            ctx.write(response);
            ctx.flush();
        }
    }

    private class SimpleClientHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            BLog.info("Send simple client message");
            ctx.writeAndFlush(Unpooled.wrappedBuffer(SIMPLE_CLIENT_MESSAGE.getBytes()));
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            String message = msgToString(msg);
            BLog.info("Receive simple server message: {}", message);
            Assert.assertEquals(message, SIMPLE_SERVER_MESSAGE);
            runLatch.open();
        }
    }

    private class SimpleServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            String message = msgToString(msg);
            BLog.info("Receive simple client message: {}", message);
            Assert.assertEquals(message, SIMPLE_CLIENT_MESSAGE);
            ctx.writeAndFlush(Unpooled.wrappedBuffer(SIMPLE_SERVER_MESSAGE.getBytes()));
        }
    }

    private String msgToString(Object msg) {
        if (msg instanceof ByteBuf) {
            return BNetty.getString((ByteBuf) msg);
        }
        return String.valueOf(msg);
    }
}
