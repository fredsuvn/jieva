package test.java.xyz.srclab.common.net.http;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.collect.BMap;
import xyz.srclab.common.net.http.BHttp;
import xyz.srclab.common.net.http.HttpResp;

import java.util.Collections;

public class HttpTest {

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
    public void testRequest() {
        HttpResp resp = BHttp.request("https://www.baidu.com");
        BLog.info("resp: {}", resp.bodyAsString());
    }
}
