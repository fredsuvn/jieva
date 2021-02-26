package sample.xyz.srclab.common.base;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.srclab.common.base.*;
import xyz.srclab.common.test.TestLogger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class BaseSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testLet() {
        int sum = Let.of("1,2,3,4,5,6,7,8,9,10")
                .then(s -> s.split(","))
                .then(Arrays::asList)
                .then(l -> l.stream().mapToInt(Integer::parseInt))
                .then(IntStream::sum)
                .get();
        //55
        logger.log("sum: {}", sum);
    }

    @Test
    public void testRef() {
        Ref<String> ref = Ref.of("1");
        List<String> list = Arrays.asList("-1", "-2", "-3");

        //here <String> should be final without Ref
        list.forEach(i -> ref.set(ref.get() + i));
        //1-1-2-3
        logger.log("result: {}", ref.get());
    }

    @Test
    public void testCurrent() {
        Current.set("1", "2");
        //2
        logger.log(Current.get("1"));
        //System.currentTimeMillis();
        logger.log(Current.millis());
    }

    @Test
    public void testDefault() {
        //UTF-8
        logger.log(Default.charset());
        //Locale.getDefault();
        logger.log(Default.locale());
    }

    @Test
    public void testEnvironment() {
        logger.log(Environment.getProperty(Environment.KEY_OS_ARCH));
        logger.log(Environment.availableProcessors());
        logger.log(Environment.osVersion());
        logger.log(Environment.isOsWindows());
    }

    @Test
    public void testFormat() {
        String byFast = Format.fastFormat("1, 2, {}", 3);
        String byMessage = Format.messageFormat("1, 2, {0}", 3);
        String byPrintf = Format.printfFormat("1, 2, %d", 3);
        //1, 2, 3
        logger.log("byFast: {}", byFast);
        logger.log("byMessage: {}", byMessage);
        logger.log("byPrintf: {}", byPrintf);
    }

    @Test
    public void testNamingCase() {
        String upperCamel = "UpperCamel";
        String lowerCamel = NamingCase.UPPER_CAMEL.convertTo(upperCamel, NamingCase.LOWER_CAMEL);
        //upperCamel
        logger.log("lowerCamel: {}", lowerCamel);
    }

    @Test
    public void testCounter() {
        Counter counter = Counter.startsAt(100);
        counter.getAndIncrementInt();
        counter.reset();
        Counter atomicCounter = Counter.startsAt(100, true);
        atomicCounter.incrementAndGetInt();
        atomicCounter.reset();
    }

    @Test
    public void testLazy() {
        Lazy<String> lazy = Lazy.of(() -> UUID.randomUUID().toString());
        String value1 = lazy.get();
        String value2 = lazy.get();
        lazy.refresh();
        String value3 = lazy.get();
        //value1 == value2
        //value2 != value3
        logger.log("value1: {}", value1);
        logger.log("value2: {}", value2);
        logger.log("value3: {}", value3);
    }

    @Test
    public void testLoaders() {
        Class<String[][][]> cls = Loaders.loadClass("[[[Ljava.lang.String;");
        //class [[[Ljava.lang.String;
        logger.log("cls: {}", cls);
    }

    @Test
    public void testSpecParser() {
        String s = SpecParser.parseFirstClassNameToInstance("java.lang.String");
        //an empty String
        logger.log("s: {}", s);
    }

    @Test
    public void testUtils() {

        //Anys examples:
        List<String>[] lists = Anys.as(new List[]{});
        int hash = Anys.anyOrArrayHash(Arrays.asList("", 1));
        boolean equals = Anys.anyOrArrayEquals(Arrays.asList("", 1), Arrays.asList("", 1));

        //Chars examples:
        byte[] bytes = "message10086".getBytes();
        String toChars = Chars.toChars(bytes);
        byte[] toBytes = Chars.toBytes(toChars);
        //message10086
        logger.log("toChars: {}", toChars);
        //[109, 101, 115, 115, 97, 103, 101, 49, 48, 48, 56, 54]
        logger.log("toBytes: {}", toBytes);

        //Nums examples:
        BigDecimal n = Nums.toBigDecimal("110");
        int i = Nums.toInt(new BigDecimal("2333"));
        //110
        logger.log("n: {}", n);
        //2333
        logger.log("i: {}", i);

        //Bools examples:
        boolean b = Bools.toBoolean("true");
        //true
        logger.log("b: {}", b);

        //Dates examples:
        String timestamp = Dates.timestamp();
        LocalDateTime localDateTime = Dates.toLocalDateTime("2011-12-03T10:15:30");
        //20210207144816045
        logger.log("timestamp: {}", timestamp);
        //2011-12-03T10:15:30
        logger.log("localDateTime: {}", localDateTime);

        //Randoms examples:
        //[10, 20]
        for (int j = 0; j < 10; j++) {
            logger.log("random[10, 20]: {}", Randoms.between(10, 21));
        }

        //Compares example:
        //99
        logger.log("inBounds: {}", Compares.inBounds(100, 0, 99));

        //Checks examples:
        try {
            Checks.checkArgument(1 == 2, "1 != 2");
        } catch (IllegalArgumentException e) {
            //java.lang.IllegalArgumentException: 1 != 2
            logger.log("e: {}", e);
        }

        //Requires examples:
        try {
            Object notNull = Requires.notNull(null, "null");
        } catch (NullPointerException e) {
            //java.lang.NullPointerException: null
            logger.log("e: {}", e);
        }
    }

    @Test
    public void testCachingBuilder() {

        class CachingBuilderSample extends CachingProductBuilder<String> {

            private String value = "null";

            public void setValue(String value) {
                this.value = value;
                this.commitChange();
            }

            @NotNull
            @Override
            protected String buildNew() {
                return value + UUID.randomUUID().toString();
            }
        }

        CachingBuilderSample cachingBuilderSample = new CachingBuilderSample();
        cachingBuilderSample.setValue("1");
        String value1 = cachingBuilderSample.build();
        String value2 = cachingBuilderSample.build();
        cachingBuilderSample.setValue("2");
        String value3 = cachingBuilderSample.build();
        //10c66dae9-c056-464e-8117-4787914c3af8
        logger.log("value1: {}", value1);
        //10c66dae9-c056-464e-8117-4787914c3af8
        logger.log("value2: {}", value2);
        //2c7c2e230-50b0-4a0f-8530-151723297fb8
        logger.log("value3: {}", value3);
    }

    @Test
    public void testShell() {
        Shell shell = Shell.DEFAULT;
        shell.println("Hello", ",", "World", "!");
        shell.println(Arrays.asList("Hello", ",", "World", "!"));
        shell.println("123", EscapeChars.linefeed(), "456", EscapeChars.newline(), EscapeChars.reset());
        shell.println(
                SgrChars.foregroundRed("red"),
                SgrChars.backgroundCyan(" "),
                SgrChars.foregroundGreen("green")
        );
        shell.println(
                SgrChars.withParam("bright red", SgrParam.FOREGROUND_BRIGHT_RED),
                SgrChars.backgroundCyan(" "),
                SgrChars.withParam("bright green", SgrParam.FOREGROUND_BRIGHT_GREEN)
        );
        shell.println(
                SgrChars.withParam("color 8", SgrParam.foregroundColor(8)),
                SgrChars.backgroundCyan(" "),
                SgrChars.withParam("rgb(100, 100, 50)", SgrParam.foregroundColor(100, 100, 50))
        );
        shell.println(ControlChars.beep());
        shell.println("123", ControlChars.backspaces(), "456", ControlChars.beep());
    }

    @Test
    public void testAbout() {
        String verString = "1.2.3-beta.2.3+123";
        SemVer semVer = SemVer.parse(verString);
        About about = About.of(
                "name",
                semVer.normalString(),
                Collections.singletonList(Author.of("name", "author@mail.com", null)),
                "123@123.com",
                "url",
                Collections.singletonList("licence"),
                Collections.singletonList(About.of(
                        "poweredBy",
                        null,
                        Collections.emptyList(),
                        null,
                        null,
                        Collections.emptyList(),
                        Collections.emptyList(),
                        null
                )),
                "© 2021 SrcLab"
        );
        //name
        //Version: 1.2.3
        //Author: name(author@mail.com)
        //Mail: 123@123.com
        //Url: url
        //Licence: licence
        //Powered by: poweredBy
        //© 2021 SrcLab
        logger.log("About: {}", about);
    }
}
