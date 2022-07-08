package test.java.xyz.srclab.common.base;

public class ValVarTest {

    //@Test
    //public void testVal() {
    //    Val<String> v = Val.of(null);
    //    Assert.assertEquals(v.getOrDefault("null"), "null");
    //    Assert.assertFalse(v.isPresent());
    //    v.set("123");
    //    Assert.assertEquals(v.getOrDefault("null"), "123");
    //    Assert.assertTrue(v.isPresent());
    //}
    //
    //@Test
    //public void testChainOps() {
    //    String dateString = "2222-12-22 22:22:22";
    //    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    //    Date date = Ref.of(dateString)
    //        .accept(BLog::info)
    //        .map(d -> LocalDateTime.parse(d, formatter))
    //        .map(d -> d.atZone(ZoneId.systemDefault()))
    //        .map(ChronoZonedDateTime::toInstant)
    //        .map(Date::from)
    //        .get();
    //    BLog.info("date: {}", date);
    //}
}
