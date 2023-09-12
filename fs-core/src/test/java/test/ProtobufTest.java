package test;

import org.testng.annotations.Test;
import xyz.srclab.common.bean.FsBeanResolver;
import xyz.srclab.common.bean.handlers.ProtobufResolveHandler;

import java.text.ParseException;

public class ProtobufTest {

    @Test
    public void testBean() {
        FsBeanResolver resolver = FsBeanResolver.defaultResolver().withHandler(ProtobufResolveHandler.INSTANCE);
        resolver.resolve(Object.class);
    }
}
