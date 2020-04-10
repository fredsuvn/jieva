//package xyz.srclab.sample.lang;
//
//import xyz.srclab.common.bean.BeanHelper;
//import xyz.srclab.common.lang.Computed;
//import xyz.srclab.common.lang.Ref;
//import xyz.srclab.common.lang.TypeRef;
//import xyz.srclab.test.asserts.AssertHelper;
//
//import java.util.Map;
//
//public class LangSample {
//
//    public void showComputed() throws Exception {
//        int[] ia = {0};
//        Computed<Integer> computed1 = Computed.with(() -> {
//            // Very large processing...
//            return ia[0]++;
//        });
//        AssertHelper.printAssert(computed1.get(), 0);
//        Computed<Integer> computed2 = Computed.with(1, () -> {
//            // Very large processing...
//            return ia[0]++;
//        });
//        AssertHelper.printAssert(computed2.get(), 1);
//        Thread.sleep(2000);
//        AssertHelper.printAssert(computed2.get(), 2);
//    }
//
//    public void showRef() {
//        Ref<String> mutable = Ref.with("some");
//        new Thread(() -> {
//            mutable.set("another");
//        }).start();
//    }
//
//    public void showTypeRef() {
//        Map<? super String, ? extends String> map =
//                BeanHelper.convert("any", new TypeRef<Map<? super String, ? extends String>>() {
//                });
//    }
//}
