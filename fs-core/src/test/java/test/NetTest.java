package test;

import org.testng.annotations.Test;
import xyz.srclab.common.bean.FsBeanResolver;
import xyz.srclab.common.bean.handlers.ProtobufResolveHandler;

import java.text.ParseException;

public class NetTest {

    @Test
    public void testBean() throws ParseException {
        FsBeanResolver resolver = FsBeanResolver.defaultResolver().withHandler(ProtobufResolveHandler.INSTANCE);
    }
}
