# <span class="image">![Boat Core](../../logo.svg)</span> `boat-core`: Boat Core — Core Lib of [Boat](../../README.md)

<span id="author" class="author">Sun Qian</span>  
<span id="email" class="email"><fredsuvn@163.com></span>  

Table of Contents

-   [Introduction](#_introduction)
-   [Usage](#_usage)
    -   [Lang](#_lang)
    -   [Bean](#_bean)
    -   [Convert](#_convert)
    -   [Cache](#_cache)
    -   [Run](#_run)
    -   [Bus](#_bus)
    -   [Invoke](#_invoke)
    -   [Proxy](#_proxy)
    -   [State](#_state)
    -   [Exception](#_exception)
    -   [Collect](#_collect)
    -   [Reflect](#_reflect)
    -   [IO](#_io)
    -   [Jvm](#_jvm)
    -   [Utils](#_utils)
    -   [Test](#_test)

## Introduction

Boat core provides core interfaces, functions and utilities for
[Boat](../../README.md), including:

-   [Lang](#_lang): Base package, a serials of core and base interfaces
    and components;

-   [Bean](#_bean): Bean package, supports fast and convenient operation
    for `bean`;

-   [Convert](#_convert): Convert package, supports various types
    conversion each other;

-   [Cache](#_cache): Cache package, provides core cache interfaces and
    built-in implementations;

-   [Run](#_run): Package about run and thread, provides `Runner`
    interfaces and implementations;

-   [Bus](#_bus): Bus package, provides `EventBus` interfaces;

-   [Invoke](#_invoke): Provides `Invoker` interfaces and
    implementations;

-   [Proxy](#_proxy): Provides `ProxyClass` to proxy a class in runtime
    dynamically, by `reflection`, `spring-core` or `cglib` ways;

-   [State](#_state): Provides `State` interfaces represents a state or
    status;

-   [Exception](#_exception): About exceptions;

-   [Collect](#_collect): Collection extension package, supports
    `chain operation`, `multi-value-map`, various utilities, etc.;

-   [Reflect](#_reflect): Reflection extension package, provides many
    convenient reflection operation;

-   [IO](#_io): Input/Output interfaces and utilities package;

-   [Jvm](#_jvm): Provides JVM underlying operations;

-   [Utils](#_utils): Other convenient tools;

-   [Test](#_test): Provides interfaces and utilities for testing;

## Usage

### Lang

Lang package provides a serials of core and base interfaces and
components:

-   Global system objects: `Current`, `Defaults`, `Environment`;

-   Lang syntax enhancement (mainly for Java): `Let`, `Ref`, `Lazy`,
    `LazyString`;

-   String/CharSequence functions: `CharsFormat`, `CharsTemplate`,
    `NamingCase`;

-   Special character support: `CtlChars`, `EscChars`, `CsiChars`,
    `SgrChars`;

-   Access interfaces: `Accessor`, `Getter`, `Setter`,
    `GenericAccessor`, `GenericGetter`, `GenericSetter`;

-   Helper interfaces and utilities: SpecParser, CachingProductBuilder,
    Processing;

-   Common utilities: Anys, Bools, Chars, Nums, Dates, Randoms,
    Compares, Checks, Requires, Enums, Loaders.

Java Examples

    package sample.java.xyz.srclab.core.lang;

    import org.jetbrains.annotations.NotNull;
    import org.testng.Assert;
    import org.testng.annotations.Test;
    import xyz.srclab.common.lang.*;
    import xyz.srclab.common.test.TestLogger;
    import xyz.srclab.common.utils.Counter;

    import java.math.BigDecimal;
    import java.time.LocalDateTime;
    import java.util.*;
    import java.util.stream.IntStream;

    public class LangSample {

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
            Ref<String> ref = Ref.with("1");
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
            logger.log(Defaults.charset());
            //Locale.getDefault();
            logger.log(Defaults.locale());
        }

        @Test
        public void testEnvironment() {
            logger.log(Environment.getProperty(Environment.OS_ARCH_KEY));
            logger.log(Environment.availableProcessors());
            logger.log(Environment.osVersion());
            logger.log(Environment.isOsWindows());
        }

        @Test
        public void testCharsFormat() {
            String byFast = CharsFormat.fastFormat("1, 2, {}", 3);
            String byMessage = CharsFormat.messageFormat("1, 2, {0}", 3);
            String byPrintf = CharsFormat.printfFormat("1, 2, %d", 3);
            //1, 2, 3
            logger.log("byFast: {}", byFast);
            logger.log("byMessage: {}", byMessage);
            logger.log("byPrintf: {}", byPrintf);
        }

        @Test
        public void testCharsTemplate() {
            Map<Object, Object> args = new HashMap<>();
            args.put("name", "Dog");
            args.put("name}", "DogX");
            args.put(1, "Cat");
            args.put(2, "Bird");
            CharsTemplate template1 = CharsTemplate.resolve(
                "This is a {name}, that is a {}", "{", "}");
            //This is a Dog, that is a Cat
            logger.log(template1.process(args));
            CharsTemplate template2 = CharsTemplate.resolve(
                "This is a } {name}, that is a {}}", "{", "}");
            //This is a } Dog, that is a Cat}
            logger.log(template2.process(args));
            CharsTemplate template3 = CharsTemplate.resolve(
                "This is a } \\{{name\\}} ({name}), that is a {}\\\\\\{\\", "{", "}", "\\");
            //This is a } {DogX (Dog), that is a Bird\{\
            logger.log(template3.process(args));
        }

        @Test
        public void testNamingCase() {
            String upperCamel = "UpperCamel";
            String lowerCamel = NamingCase.UPPER_CAMEL.convertTo(upperCamel, NamingCase.LOWER_CAMEL);
            //upperCamel
            logger.log("lowerCamel: {}", lowerCamel);
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
        public void testLazyString() {
            Counter counter = Counter.startsAt(0);
            LazyString<Integer> lazyString = LazyString.of(Lazy.of(counter::getAndIncrementInt));
            //0
            logger.log("lazyToString: {}", lazyString);
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
        public void testBaseTypes() {

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

            //Enums examples:
            TestEnum t1 = Enums.value(TestEnum.class, "T1");
            //t1: T1
            logger.log("t1: {}", t1);
            TestEnum t2 = Enums.valueIgnoreCase(TestEnum.class, "t2");
            //t2: T2
            logger.log("t2: {}", t2);
        }

        @Test
        public void testRandom() {
            //[10, 20]
            for (int j = 0; j < 10; j++) {
                logger.log("random[10, 20]: {}", Randoms.between(new Random(), 10, 21));
            }

            RandomSupplier<?> randomSupplier = RandomSupplier.newBuilder()
                .mayBe(20, "A")
                .mayBe(20, "B")
                .mayBe(60, "C")
                .build();
            int countA = 0;
            int countB = 0;
            int countC = 0;
            for (int i = 0; i < 1000; i++) {
                Object result = randomSupplier.get();
                if (result.equals("A")) {
                    countA++;
                } else if (result.equals("B")) {
                    countB++;
                } else if (result.equals("C")) {
                    countC++;
                }
            }
            int total = countA + countB + countC;
            Assert.assertEquals(total, 1000);
            //countA: 189, countB: 190, countC: 621, total: 1000
            logger.log("countA: {}, countB: {}, countC: {}, total: {}", countA, countB, countC, total);
        }

        @Test
        public void testCachingProductBuilder() {

            class CachingBuilderSample extends CachingProductBuilder<String> {

                private String value = "null";
                private long counter = 0L;

                public void setValue(String value) {
                    this.value = value;
                    this.commitModification();
                }

                @NotNull
                @Override
                protected String buildNew() {
                    return value + counter++;
                }
            }

            CachingBuilderSample cachingBuilderSample = new CachingBuilderSample();
            cachingBuilderSample.setValue("1");
            String value1 = cachingBuilderSample.build();
            String value2 = cachingBuilderSample.build();
            cachingBuilderSample.setValue("2");
            String value3 = cachingBuilderSample.build();
            //10
            logger.log("value1: {}", value1);
            //10
            logger.log("value2: {}", value2);
            //21
            logger.log("value3: {}", value3);
        }

        @Test
        public void testProcess() {
            if (Environment.isOsUnix()) {
                testProcessing("echo", "ECHO_CONTENT");
            }
            if (Environment.isOsWindows()) {
                testProcessing("cmd.exe", "/c", "echo " + "ECHO_CONTENT");
            }
        }

        private void testProcessing(String... command) {
            Processing processing = Processing.newProcessing(command);
            processing.waitForTermination();
            String output = processing.outputString();
            //ECHO_CONTENT
            logger.log(output);
        }

        @Test
        public void testShell() {
            logger.log("Hello, world!");
            logger.log("123{}456{}{}", EscChars.linefeed(), EscChars.newline(), EscChars.reset());
            logger.log("{}{}{}",
                SgrChars.foregroundRed("red"),
                SgrChars.backgroundCyan(" "),
                SgrChars.foregroundGreen("green")
            );
            logger.log("{}{}{}",
                SgrChars.withParam("bright red", SgrParam.FOREGROUND_BRIGHT_RED),
                SgrChars.backgroundCyan(" "),
                SgrChars.withParam("bright green", SgrParam.FOREGROUND_BRIGHT_GREEN)
            );
            logger.log("{}{}{}",
                SgrChars.withParam("color 8", SgrParam.foregroundColor(8)),
                SgrChars.backgroundCyan(" "),
                SgrChars.withParam("rgb(100, 100, 50)", SgrParam.foregroundColor(100, 100, 50))
            );
            logger.log(CtlChars.beep());
            logger.log("123\010456\007");
            logger.log("123{}456{}", CtlChars.backspaces(), CtlChars.beep());
        }

        public enum TestEnum {
            T1,
            T2
        }
    }

Kotlin Examples

    package sample.kotlin.xyz.srclab.core.lang

    import org.testng.Assert
    import org.testng.annotations.Test
    import xyz.srclab.common.lang.*
    import xyz.srclab.common.lang.CharsFormat.Companion.fastFormat
    import xyz.srclab.common.lang.CharsFormat.Companion.messageFormat
    import xyz.srclab.common.lang.CharsFormat.Companion.printfFormat
    import xyz.srclab.common.lang.CharsTemplate.Companion.resolveTemplate
    import xyz.srclab.common.lang.LazyString.Companion.toLazyString
    import xyz.srclab.common.lang.Processing.Companion.newProcessing
    import xyz.srclab.common.lang.SpecParser.Companion.parseFirstClassNameToInstance
    import xyz.srclab.common.test.TestLogger
    import xyz.srclab.common.utils.Counter.Companion.counterStarts
    import java.math.BigDecimal
    import java.util.*
    import kotlin.text.toBigDecimal
    import kotlin.text.toBoolean

    class BaseSample {

        @Test
        fun testCurrent() {
            Current.set("1", "2")
            //2
            logger.log(Current.get<Any>("1"))
            //System.currentTimeMillis();
            logger.log(Current.millis)
        }

        @Test
        fun testDefault() {
            //UTF-8
            logger.log(Defaults.charset)
            //Locale.getDefault();
            logger.log(Defaults.locale)
        }

        @Test
        fun testEnvironment() {
            logger.log(Environment.getProperty(Environment.OS_ARCH_KEY))
            logger.log(Environment.availableProcessors)
            logger.log(Environment.osVersion)
            logger.log(Environment.isOsWindows)
        }

        @Test
        fun testCharsFormat() {
            val byFast = "1, 2, {}".fastFormat(3)
            val byMessage = "1, 2, {0}".messageFormat(3)
            val byPrintf = "1, 2, %d".printfFormat(3)
            //1, 2, 3
            logger.log("byFast: {}", byFast)
            logger.log("byMessage: {}", byMessage)
            logger.log("byPrintf: {}", byPrintf)
        }

        @Test
        fun testCharsTemplate() {
            val args: MutableMap<Any, Any?> = HashMap()
            args["name"] = "Dog"
            args["name}"] = "DogX"
            args[1] = "Cat"
            args[2] = "Bird"
            val template1 = "This is a {name}, that is a {}".resolveTemplate("{", "}")
            //This is a Dog, that is a Cat
            logger.log(template1.process(args))
            val template2 = "This is a } {name}, that is a {}}".resolveTemplate("{", "}")
            //This is a } Dog, that is a Cat}
            logger.log(template2.process(args))
            val template3 = "This is a } \\{{name\\}} ({name}), that is a {}\\\\\\{\\".resolveTemplate("{", "}", "\\")
            //This is a } {DogX (Dog), that is a Bird\{\
            logger.log(template3.process(args))
        }

        @Test
        fun testNamingCase() {
            val upperCamel = "UpperCamel"
            val lowerCamel = NamingCase.UPPER_CAMEL.convertTo(upperCamel, NamingCase.LOWER_CAMEL)
            //upperCamel
            logger.log("lowerCamel: {}", lowerCamel)
        }

        @Test
        fun testLazyString() {
            val counter = 0.counterStarts()
            val lazyToString = lazyOf { counter.getAndIncrementInt() }.toLazyString()
            //0
            logger.log("lazyToString: {}", lazyToString)
        }

        @Test
        fun testLoaders() {
            val cls = "[[[Ljava.lang.String;".loadClass<Array<Array<Array<String>>>>()
            //class [[[Ljava.lang.String;
            logger.log("cls: {}", cls)
        }

        @Test
        fun testSpecParser() {
            val s = "java.lang.String".parseFirstClassNameToInstance<String>()
            //an empty String
            logger.log("s: {}", s)
        }

        @Test
        fun testBaseTypes() {

            //Anys examples:
            val lists = arrayOf<List<*>>().asAny<Array<List<String>>>()
            val hash = Arrays.asList("", 1).anyOrArrayHash()
            val equals = Arrays.asList("", 1).anyOrArrayEquals(Arrays.asList("", 1))

            //Chars examples:
            val bytes = "message10086".toByteArray()
            val toChars = bytes.toChars()
            val toBytes = toChars.toBytes()
            //message10086
            logger.log("toChars: {}", toChars)
            //[109, 101, 115, 115, 97, 103, 101, 49, 48, 48, 56, 54]
            logger.log("toBytes: {}", toBytes)

            //Nums examples:
            val n = "110".toBigDecimal()
            val i = BigDecimal("2333").toInt()
            //110
            logger.log("n: {}", n)
            //2333
            logger.log("i: {}", i)

            //Bools examples:
            val b = "true".toBoolean()
            //true
            logger.log("b: {}", b)

            //Dates examples:
            val timestamp = timestamp()
            val localDateTime = "2011-12-03T10:15:30".toLocalDateTime()
            //20210207144816045
            logger.log("timestamp: {}", timestamp)
            //2011-12-03T10:15:30
            logger.log("localDateTime: {}", localDateTime)

            //Compares example:
            //99
            logger.log("inBounds: {}", 100.inBounds(0, 99))

            //Checks examples:
            try {
                checkArgument(1 == 2, "1 != 2")
            } catch (e: IllegalArgumentException) {
                //java.lang.IllegalArgumentException: 1 != 2
                logger.log("e: {}", e)
            }

            //Requires examples:
            try {
                val notNull = null.notNull<Any>("null")
            } catch (e: NullPointerException) {
                //java.lang.NullPointerException: null
                logger.log("e: {}", e)
            }

            //Enums examples:
            val t1: TestEnum = TestEnum::class.java.valueOfEnum("T1")
            //t1: T1
            logger.log("t1: {}", t1)
            val t2: TestEnum = TestEnum::class.java.valueOfEnumIgnoreCase("t2")
            //t2: T2
            logger.log("t2: {}", t2)
        }


        @Test
        fun testRandom() {
            //[10, 20]
            for (j in 0..9) {
                logger.log("random[10, 20]: {}", Random().between(10, 21))
            }

            val randomSupplier = RandomSupplier.newBuilder<Any>()
                .mayBe(20, "A")
                .mayBe(20, "B")
                .mayBe(60, "C")
                .build()
            var countA = 0
            var countB = 0
            var countC = 0
            for (i in 0..999) {
                val result = randomSupplier.get()
                if (result == "A") {
                    countA++
                } else if (result == "B") {
                    countB++
                } else if (result == "C") {
                    countC++
                }
            }
            val total = countA + countB + countC
            Assert.assertEquals(total, 1000)
            //countA: 189, countB: 190, countC: 621, total: 1000
            logger.log("countA: {}, countB: {}, countC: {}, total: {}", countA, countB, countC, total)
        }

        @Test
        fun testCachingProductBuilder() {

            class CachingBuilderSample : CachingProductBuilder<String>() {

                private var value = "null"
                private var counter = 0L

                fun setValue(value: String) {
                    this.value = value
                    commitModification()
                }

                override fun buildNew(): String {
                    return "$value${counter++}"
                }
            }

            val cachingBuilderSample = CachingBuilderSample()
            cachingBuilderSample.setValue("1")
            val value1 = cachingBuilderSample.build()
            val value2 = cachingBuilderSample.build()
            cachingBuilderSample.setValue("2")
            val value3 = cachingBuilderSample.build()
            //10
            logger.log("value1: {}", value1)
            //10
            logger.log("value2: {}", value2)
            //21
            logger.log("value3: {}", value3)
        }

        @Test
        fun testProcess() {
            if (Environment.isOsUnix) {
                testProcessing("echo", "ECHO_CONTENT")
            }
            if (Environment.isOsWindows) {
                testProcessing("cmd.exe", "/c", "echo " + "ECHO_CONTENT")
            }
        }

        private fun testProcessing(vararg command: String) {
            val processing = newProcessing(*command)
            processing.waitForTermination()
            val output = processing.outputString()
            //ECHO_CONTENT
            logger.log(output)
        }

        @Test
        fun testShell() {
            logger.log("Hello, world!")
            logger.log("123{}456{}{}", EscChars.linefeed, EscChars.newline, EscChars.reset)
            logger.log(
                "{}{}{}",
                SgrChars.foregroundRed("red"),
                SgrChars.backgroundCyan(" "),
                SgrChars.foregroundGreen("green")
            )
            logger.log(
                "{}{}{}",
                SgrChars.withParam("bright red", SgrParam.FOREGROUND_BRIGHT_RED),
                SgrChars.backgroundCyan(" "),
                SgrChars.withParam("bright green", SgrParam.FOREGROUND_BRIGHT_GREEN)
            )
            logger.log(
                "{}{}{}",
                SgrChars.withParam("color 8", SgrParam.foregroundColor(8)),
                SgrChars.backgroundCyan(" "),
                SgrChars.withParam("rgb(100, 100, 50)", SgrParam.foregroundColor(100, 100, 50))
            )
            logger.log(CtlChars.beep)
            //logger.log("123\010456\007");
            logger.log("123{}456{}", CtlChars.backspaces, CtlChars.beep)
        }

        companion object {
            private val logger = TestLogger.DEFAULT
        }
    }

    enum class TestEnum {
        T1, T2
    }

### Bean

Bean package supports fast and convenient operation for `bean`:

-   `Beans`: Default utilities for bean operation;

-   `BeanResolver`: Core Interface to resolve `bean`, `Beans` use its
    default implementation;

-   `BeanResolveHandler`: Handler to process resolving for
    `BeanResolver`;

-   `BeanMap`: A type of `Map` implementation that associated with a
    `bean`, means make a `bean` as a `Map`;

-   `BeanType`: To describe struct of a type of `bean` like `Class`;

-   `PropertyType`: To describe a property of `bean` like `Field`;

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon"><div class="title">
Tip
</div></td>
<td class="content"><code>Beans</code> is more than 20 times faster than <code>Apache BeanUtils</code> in copy-properties function.</td>
</tr>
</tbody>
</table>

Java Examples

    package sample.java.xyz.srclab.core.bean;

    import org.testng.annotations.Test;
    import xyz.srclab.common.bean.Beans;
    import xyz.srclab.common.test.TestLogger;

    public class BeanSample {

        private static final TestLogger logger = TestLogger.DEFAULT;

        @Test
        public void testBean() {
            A a = new A();
            a.setP1("1");
            a.setP2("2");
            B b = Beans.copyProperties(a, new B());
            int b1 = b.getP1();
            int b2 = b.getP2();
            //1
            logger.log("b1: {}", b1);
            //2
            logger.log("b1: {}", b2);
        }

        public static class A {
            private String p1;
            private String p2;

            public String getP1() {
                return p1;
            }

            public void setP1(String p1) {
                this.p1 = p1;
            }

            public String getP2() {
                return p2;
            }

            public void setP2(String p2) {
                this.p2 = p2;
            }
        }

        public static class B {
            private int p1;
            private int p2;

            public int getP1() {
                return p1;
            }

            public void setP1(int p1) {
                this.p1 = p1;
            }

            public int getP2() {
                return p2;
            }

            public void setP2(int p2) {
                this.p2 = p2;
            }
        }
    }

Kotlin Examples

    package sample.kotlin.xyz.srclab.core.bean

    import org.testng.annotations.Test
    import xyz.srclab.common.bean.copyProperties
    import xyz.srclab.common.test.TestLogger

    class BeanSample {

        @Test
        fun testBean() {
            val a = A()
            a.p1 = "1"
            a.p2 = "2"
            val b = a.copyProperties(B())
            val b1 = b.p1
            val b2 = b.p2
            //1
            logger.log("b1: {}", b1)
            //2
            logger.log("b1: {}", b2)
        }

        class A {
            var p1: String? = null
            var p2: String? = null
        }

        class B {
            var p1 = 0
            var p2 = 0
        }

        companion object {
            private val logger = TestLogger.DEFAULT
        }
    }

### Convert

Convert package supports various types conversion each other:

-   `Converts`: Default utilities for type conversion operation;

-   `Converter`: Core interfaces for type conversion, `Converts` use its
    default implementation;

-   `ConvertHandler`: Handler to process converting for `Converter`;

-   `FastConverter`: A fast type converter which must specify the target
    type;

Java Examples

    package sample.java.xyz.srclab.core.convert;

    import org.jetbrains.annotations.NotNull;
    import org.testng.annotations.Test;
    import xyz.srclab.common.convert.Converts;
    import xyz.srclab.common.convert.FastConvertHandler;
    import xyz.srclab.common.convert.FastConverter;
    import xyz.srclab.common.test.TestLogger;

    import java.util.Arrays;

    public class ConvertSample {

        private static final TestLogger logger = TestLogger.DEFAULT;

        @Test
        public void testConvert() {
            String s = Converts.convert(123, String.class);
            //123
            logger.log("s: {}", s);

            A a = new A();
            a.setP1("1");
            a.setP2("2");
            B b = Converts.convert(a, B.class);
            //1
            logger.log("b1: {}", b.getP1());
            //2
            logger.log("b1: {}", b.getP2());

            FastConverter fastConverter = FastConverter.newFastConverter(
                Arrays.asList(new IntToStringConvertHandler(), new NumberToStringConvertHandler()));
            //I123
            logger.log(fastConverter.convert(123, String.class));
            //N123
            logger.log(fastConverter.convert(123L, String.class));
        }

        public static class A {
            private String p1;
            private String p2;

            public String getP1() {
                return p1;
            }

            public void setP1(String p1) {
                this.p1 = p1;
            }

            public String getP2() {
                return p2;
            }

            public void setP2(String p2) {
                this.p2 = p2;
            }
        }

        public static class B {
            private int p1;
            private int p2;

            public int getP1() {
                return p1;
            }

            public void setP1(int p1) {
                this.p1 = p1;
            }

            public int getP2() {
                return p2;
            }

            public void setP2(int p2) {
                this.p2 = p2;
            }
        }

        private static class IntToStringConvertHandler implements FastConvertHandler<Integer, String> {

            @NotNull
            @Override
            public Class<?> fromType() {
                return Integer.class;
            }

            @NotNull
            @Override
            public Class<?> toType() {
                return String.class;
            }

            @NotNull
            @Override
            public String convert(@NotNull Integer from) {
                return "I" + from;
            }
        }

        private static class NumberToStringConvertHandler implements FastConvertHandler<Number, String> {

            @NotNull
            @Override
            public Class<?> fromType() {
                return Number.class;
            }

            @NotNull
            @Override
            public Class<?> toType() {
                return String.class;
            }

            @NotNull
            @Override
            public String convert(@NotNull Number from) {
                return "N" + from;
            }
        }
    }

Kotlin Examples

    package sample.kotlin.xyz.srclab.core.convert

    import org.testng.annotations.Test
    import xyz.srclab.common.convert.FastConvertHandler
    import xyz.srclab.common.convert.FastConverter
    import xyz.srclab.common.convert.convert
    import xyz.srclab.common.test.TestLogger

    class ConvertSample {

        @Test
        fun testConvert() {
            val s = 123.convert(String::class.java)
            //123
            logger.log("s: {}", s)
            val a = A()
            a.p1 = "1"
            a.p2 = "2"
            val b = a.convert(
                B::class.java
            )
            //1
            logger.log("b1: {}", b.p1)
            //2
            logger.log("b1: {}", b.p2)

            val fastConverter =
                FastConverter.newFastConverter(listOf(IntToStringConvertHandler, NumberToStringConvertHandler))
            //I123
            logger.log(fastConverter.convert(123, String::class.java))
            //N123
            logger.log(fastConverter.convert(123L, String::class.java))
        }


        companion object {
            private val logger = TestLogger.DEFAULT
        }
    }

    class A {
        var p1: String? = null
        var p2: String? = null
    }

    class B {
        var p1 = 0
        var p2 = 0
    }

    private object IntToStringConvertHandler : FastConvertHandler<Int, String> {
        override val fromType: Class<*> = Int::class.java
        override val toType: Class<*> = String::class.java
        override fun convert(from: Int): String {
            return "I$from"
        }
    }

    private object NumberToStringConvertHandler : FastConvertHandler<Number, String> {
        override val fromType: Class<*> = Number::class.java
        override val toType: Class<*> = String::class.java
        override fun convert(from: Number): String {
            return "N$from"
        }
    }

### Cache

Cache package provides core cache interfaces and built-in
implementations:

-   `Cache`: Core Cache interface;

-   `FastCache`: Implementation by `WeakHashMap` and `ThreadLocal`;

-   `CaffeineCache`: Implementation by `Caffeine`;

-   `GuavaCache`: Implementation by `Guava`;

-   `MapCache`: Implementation by `Map`, means make `Map` as `Cache`;

Java Examples

    package sample.java.xyz.srclab.core.cache;

    import org.testng.annotations.Test;
    import xyz.srclab.common.cache.Cache;
    import xyz.srclab.common.test.TestLogger;

    public class CacheSample {

        private static final TestLogger logger = TestLogger.DEFAULT;

        @Test
        public void testCache() {
            Cache<String, String> cache = Cache.newFastCache();
            cache.getOrLoad("1", k -> k);
            //1
            logger.log("1: {}", cache.get("1"));
            //null
            logger.log("2: {}", cache.getOrNull("2"));
        }
    }

Kotlin Examples

    package sample.kotlin.xyz.srclab.core.cache

    import org.testng.annotations.Test
    import xyz.srclab.common.cache.Cache
    import xyz.srclab.common.test.TestLogger

    class CacheSample {

        @Test
        fun testCache() {
            val cache = Cache.newFastCache<String, String>()
            cache.getOrLoad("1") { k: String -> k }
            //1
            logger.log("1: {}", cache.get("1"))
            //null
            logger.log("2: {}", cache.getOrNull("2"))
        }

        companion object {
            private val logger = TestLogger.DEFAULT
        }
    }

### Run

Run package is about run and thread, provides `Runner` interfaces and
implementations:

-   `Runner`: Core interface to run threads;

-   `Running`: Core interface represents a running thread;

-   `Scheduler`: Core interface to schedule tasks;

-   `Scheduling`: Core interface represents a scheduling task;

Java Examples

    package sample.java.xyz.srclab.core.run;

    import org.testng.annotations.Test;
    import xyz.srclab.common.lang.Current;
    import xyz.srclab.common.lang.IntRef;
    import xyz.srclab.common.run.Runner;
    import xyz.srclab.common.run.Running;
    import xyz.srclab.common.run.Scheduler;
    import xyz.srclab.common.run.Scheduling;
    import xyz.srclab.common.test.TestLogger;

    import java.time.Duration;

    public class RunSample {

        private static final TestLogger logger = TestLogger.DEFAULT;

        @Test
        public void testRunner() {
            Runner runner = Runner.SYNC_RUNNER;
            IntRef intRef = IntRef.with(0);
            Running<?> running = runner.run(() -> {
                intRef.set(666);
                return null;
            });
            running.get();
            //666
            logger.log("int: {}", intRef.get());
        }

        @Test
        public void testScheduledRunner() {
            Scheduler scheduler = Scheduler.DEFAULT_THREAD_SCHEDULER;
            IntRef intRef = IntRef.with(0);
            Scheduling<?> scheduling = scheduler.scheduleFixedDelay(Duration.ZERO, Duration.ofMillis(1000), () -> {
                intRef.set(intRef.get() + 100);
                return null;
            });
            Current.sleep(2500);
            scheduling.cancel(false);
            //300
            logger.log("int: {}", intRef.get());
        }
    }

Kotlin Examples

    package sample.kotlin.xyz.srclab.core.run

    import org.testng.annotations.Test
    import xyz.srclab.common.lang.Current.sleep
    import xyz.srclab.common.lang.IntRef.Companion.withRef
    import xyz.srclab.common.run.Runner
    import xyz.srclab.common.run.Running
    import xyz.srclab.common.run.Scheduler
    import xyz.srclab.common.run.Scheduling
    import xyz.srclab.common.test.TestLogger
    import java.time.Duration

    class RunSample {

        @Test
        fun testRunner() {
            val runner: Runner = Runner.SYNC_RUNNER
            val intRef = 0.withRef()
            val running: Running<*> = runner.run<Any?> {
                intRef.set(666)
                null
            }
            running.get()
            //666
            logger.log("int: {}", intRef.get())
        }

        @Test
        fun testScheduledRunner() {
            val scheduler = Scheduler.DEFAULT_THREAD_SCHEDULER
            val intRef = 0.withRef()
            val scheduling: Scheduling<*> = scheduler.scheduleFixedDelay<Any?>(Duration.ZERO, Duration.ofMillis(1000)) {
                intRef.set(intRef.get() + 100)
                null
            }
            sleep(2500)
            scheduling.cancel(false)
            //300
            logger.log("int: {}", intRef.get())
        }

        companion object {
            private val logger = TestLogger.DEFAULT
        }
    }

### Bus

Bus package provides `EventBus` interfaces:

-   `EventBus`: Event bus interface and implementations;

Java Examples

    package sample.java.xyz.srclab.core.bus;

    import org.jetbrains.annotations.NotNull;
    import org.testng.annotations.Test;
    import xyz.srclab.common.bus.EventBus;
    import xyz.srclab.common.bus.EventHandler;
    import xyz.srclab.common.bus.EventHandlerNotFoundException;
    import xyz.srclab.common.test.TestLogger;

    import java.util.Arrays;

    public class EventBusSample {

        private static final TestLogger logger = TestLogger.DEFAULT;

        @Test
        public void testEventBus() {
            EventBus eventBus = EventBus.newEventBus(Arrays.asList(
                new EventHandler<Object>() {
                    @NotNull
                    @Override
                    public Object eventType() {
                        return String.class;
                    }

                    @Override
                    public void handle(@NotNull Object event) {
                        logger.log(event);
                    }
                },
                new EventHandler<Object>() {
                    @NotNull
                    @Override
                    public Object eventType() {
                        return Integer.class;
                    }

                    @Override
                    public void handle(@NotNull Object event) {
                        logger.log(event);
                    }
                }
            ));
            //1
            eventBus.emit(1);
            //2
            eventBus.emit("2");
            //No output
            eventBus.emit(new Object());
            try {
                eventBus.emitOrThrow(new Object());
            } catch (EventHandlerNotFoundException e) {
                //xyz.srclab.common.bus.EventHandlerNotFoundException: class java.lang.Object
                logger.log(e);
            }
        }
    }

Kotlin Examples

    package sample.kotlin.xyz.srclab.core.bus

    import org.testng.annotations.Test
    import xyz.srclab.common.bus.EventBus
    import xyz.srclab.common.bus.EventHandler
    import xyz.srclab.common.bus.EventHandlerNotFoundException
    import xyz.srclab.common.test.TestLogger

    class EventBusSample {

        @Test
        fun testEventBus() {
            val eventBus = EventBus.newEventBus(
                listOf(
                    object : EventHandler<Any> {

                        override val eventType: Any
                            get() {
                                return String::class.java
                            }

                        override fun handle(event: Any) {
                            logger.log(event)
                        }
                    },
                    object : EventHandler<Any> {

                        override val eventType: Any
                            get() {
                                return Int::class.java
                            }

                        override fun handle(event: Any) {
                            logger.log(event)
                        }
                    }
                ))
            //1
            eventBus.emit(1)
            //2
            eventBus.emit("2")
            //No output
            eventBus.emit(Any())
            try {
                eventBus.emitOrThrow(Any())
            } catch (e: EventHandlerNotFoundException) {
                //xyz.srclab.common.bus.EventHandlerNotFoundException: class java.lang.Object
                logger.log(e)
            }
        }

        companion object {
            private val logger = TestLogger.DEFAULT
        }
    }

### Invoke

Invoke package Provides `Invoker` interfaces and implementations:

-   `Invoker`: Core invoke interface;

Java Examples

    package sample.java.xyz.srclab.core.invoke;

    import org.testng.annotations.Test;
    import xyz.srclab.common.invoke.Invoker;
    import xyz.srclab.common.test.TestLogger;

    public class InvokeSample {

        private static final TestLogger logger = TestLogger.DEFAULT;

        @Test
        public void testInvoke() throws Exception {
            Invoker invoker = Invoker.forMethod(String.class.getMethod("getBytes"));
            byte[] bytes = invoker.invoke("10086");
            //[49, 48, 48, 56, 54]
            logger.log("bytes: {}", bytes);
        }
    }

Kotlin Examples

    package sample.kotlin.xyz.srclab.core.invoke

    import org.testng.annotations.Test
    import xyz.srclab.common.invoke.Invoker
    import xyz.srclab.common.test.TestLogger

    class InvokeSample {

        @Test
        fun testInvoke() {
            val invoker: Invoker = Invoker.forMethod(String::class.java, "getBytes")
            val bytes = invoker.invoke<ByteArray>("10086")
            //[49, 48, 48, 56, 54]
            logger.log("bytes: {}", bytes)
        }

        companion object {
            private val logger = TestLogger.DEFAULT
        }
    }

### Proxy

Proxy package provides `ProxyClass` to proxy a class in runtime
dynamically, by `reflection`, `spring-core` or `cglib` ways:

-   `ProxyClass`: Core interface to proxy target `Class`;

-   `ProxyMethod`: Core interface represents proxy method body;

-   `ProxyClassFactory`: Factory to create `ProxyClass`, built-in
    `Reflection`, `spring-core` and `cglib` implementation;

Java Examples

    package sample.java.xyz.srclab.core.proxy;

    import org.jetbrains.annotations.NotNull;
    import org.jetbrains.annotations.Nullable;
    import org.testng.annotations.Test;
    import xyz.srclab.common.proxy.ProxyClass;
    import xyz.srclab.common.proxy.ProxyMethod;
    import xyz.srclab.common.proxy.SuperInvoker;
    import xyz.srclab.common.test.TestLogger;

    import java.lang.reflect.Method;
    import java.util.Arrays;

    public class ProxySample {

        private static final TestLogger logger = TestLogger.DEFAULT;

        @Test
        public void testProxy() {
            ProxyClass<Object> proxyClass = ProxyClass.newProxyClass(
                Object.class,
                Arrays.asList(
                    new ProxyMethod<Object>() {
                        @NotNull
                        @Override
                        public String name() {
                            return "toString";
                        }

                        @NotNull
                        @Override
                        public Class<?>[] parameterTypes() {
                            return new Class[0];
                        }

                        @Nullable
                        @Override
                        public Object invoke(
                            Object proxied,
                            @NotNull Method proxiedMethod,
                            @Nullable Object[] args, @NotNull SuperInvoker superInvoker
                        ) {
                            return "Proxy[super: " + superInvoker.invoke(args) + "]";
                        }
                    }
                )
            );
            String s = proxyClass.newInstance().toString();
            //Proxy[super: net.sf.cglib.empty.Object$$EnhancerByCGLIB$$4926690c@256f38d9]
            logger.log("s: {}", s);
        }
    }

Kotlin Examples

    package sample.kotlin.xyz.srclab.core.proxy

    import org.testng.annotations.Test
    import xyz.srclab.common.proxy.ProxyClass.Companion.newProxyClass
    import xyz.srclab.common.proxy.ProxyMethod
    import xyz.srclab.common.proxy.SuperInvoker
    import xyz.srclab.common.test.TestLogger
    import java.lang.reflect.Method

    class ProxySample {

        @Test
        fun testProxy() {
            val proxyClass = newProxyClass(
                Any::class.java,
                listOf(
                    object : ProxyMethod<Any> {
                        override val name: String
                            get() {
                                return "toString"
                            }

                        override val parameterTypes: Array<Class<*>>
                            get() {
                                return emptyArray()
                            }

                        override fun invoke(
                            proxied: Any,
                            proxiedMethod: Method,
                            args: Array<out Any?>?, superInvoker: SuperInvoker
                        ): Any? {
                            return "Proxy[super: " + superInvoker.invoke(args) + "]"
                        }
                    }
                )
            )
            val s = proxyClass.newInstance().toString()
            //Proxy[super: net.sf.cglib.empty.Object$$EnhancerByCGLIB$$4926690c@256f38d9]
            logger.log("s: {}", s)
        }

        companion object {
            private val logger = TestLogger.DEFAULT
        }
    }

### State

State package provides `State` interfaces represents a state or status:

-   `State`: Core interface represents a state or status;

-   `StringState`: `State` for `String` type;

Java Examples

    package sample.java.xyz.srclab.core.state;

    import org.jetbrains.annotations.NotNull;
    import org.jetbrains.annotations.Nullable;
    import org.testng.annotations.Test;
    import xyz.srclab.annotations.Immutable;
    import xyz.srclab.common.state.State;
    import xyz.srclab.common.state.StringState;
    import xyz.srclab.common.test.TestLogger;

    import java.util.List;

    public class StateSample {

        private static final TestLogger logger = TestLogger.DEFAULT;

        @Test
        public void testState() {
            MyState myState = new MyState(1, "description");
            MyState newState = myState.withMoreDescription("cause");
            //description[cause]
            logger.log(newState.description());
        }

        public static class MyState implements State<Integer, String, MyState> {

            private final int code;
            private final List<String> descriptions;

            public MyState(int code, @Nullable String description) {
                this.code = code;
                this.descriptions = StringState.newDescriptions(description);
            }

            public MyState(int code, @Immutable List<String> descriptions) {
                this.code = code;
                this.descriptions = descriptions;
            }

            @Override
            public Integer code() {
                return code;
            }

            @Nullable
            @Override
            public String description() {
                return StringState.joinDescriptions(descriptions);
            }

            @NotNull
            @Override
            public List<String> descriptions() {
                return descriptions;
            }

            @NotNull
            @Override
            public MyState withNewDescription(@Nullable String newDescription) {
                return new MyState(code, StringState.newDescriptions(newDescription));
            }

            @NotNull
            @Override
            public MyState withMoreDescription(String moreDescription) {
                return new MyState(code, StringState.moreDescriptions(descriptions(), moreDescription));
            }
        }
    }

Kotlin Examples

    package sample.kotlin.xyz.srclab.core.state

    import org.testng.annotations.Test
    import xyz.srclab.common.state.State
    import xyz.srclab.common.state.StringState
    import xyz.srclab.common.state.StringState.Companion.joinStateDescriptions
    import xyz.srclab.common.state.StringState.Companion.moreDescriptions
    import xyz.srclab.common.test.TestLogger

    class StateSample {

        @Test
        fun testState() {
            val myState = MyState(1, "description")
            val newState = myState.withMoreDescription("cause")
            //description[cause]
            logger.log(newState.description)
        }

        class MyState(
            override val code: Int, override val descriptions: List<String>
        ) : State<Int, String, MyState> {

            constructor(code: Int, description: String?) : this(code, StringState.newDescriptions(description))

            override val description: String? = descriptions.joinStateDescriptions()

            override fun withNewDescription(newDescription: String?): MyState {
                return MyState(code, StringState.newDescriptions(newDescription))
            }

            override fun withMoreDescription(moreDescription: String): MyState {
                return MyState(code, descriptions.moreDescriptions(moreDescription))
            }
        }

        companion object {
            private val logger = TestLogger.DEFAULT
        }
    }

### Exception

Exception package provides `Exception` implementation and utilities:

-   `ExceptionStatus`: Interface extends [State](#_state), used for
    `Exception`;

-   `StatusException`: Exception implements `ExceptionStatus`;

-   `ImpossibleException`: The exception represents an impossible thrown
    case but actually thrown;

Java Examples

    package sample.java.xyz.srclab.core.exception;

    import org.testng.annotations.Test;
    import xyz.srclab.common.exception.ExceptionStatus;
    import xyz.srclab.common.exception.StatusException;
    import xyz.srclab.common.test.TestLogger;

    public class ExceptionSample {

        private static final TestLogger logger = TestLogger.DEFAULT;

        @Test
        public void testStatusException() {
            SampleException sampleException = new SampleException();
            //000001-Unknown Error[for sample]
            logger.log("Status: {}", sampleException.withMoreDescription("for sample"));
        }

        public static class SampleException extends StatusException {

            public SampleException() {
                super(ExceptionStatus.UNKNOWN);
            }
        }
    }

Kotlin Examples

    package sample.kotlin.xyz.srclab.core.exception

    import org.testng.annotations.Test
    import xyz.srclab.common.exception.ExceptionStatus
    import xyz.srclab.common.exception.StatusException
    import xyz.srclab.common.test.TestLogger

    class ExceptionSample {

        @Test
        fun testStatusException() {
            val sampleException = SampleException()
            //000001-Unknown Error[for sample]
            logger.log("Status: {}", sampleException.withMoreDescription("for sample"))
        }

        class SampleException : StatusException(ExceptionStatus.UNKNOWN)

        companion object {
            private val logger = TestLogger.DEFAULT
        }
    }

### Collect

Collect package extends collection function, supports `chain operation`,
`multi-value-map`, various utilities, etc.:

-   `Collects`: Default utilities for Collection;

-   `IterableOps`, `ListOps`, `SetOps`, `MapOps`, `SequenceOps`: Ops
    interfaces, provide chain operation, mainly for Java;

-   `IterableType`, `MapType`: Meta type interfaces for generic
    Collection types;

-   `MultiMaps`: `MultiMaps` provides multi-values `Map` such as
    `SetMap`, `MutableSetMap`, `ListMap` and `MutableListMap`;

Java Examples

    package sample.java.xyz.srclab.core.collect;

    import org.testng.annotations.Test;
    import xyz.srclab.common.collect.*;
    import xyz.srclab.common.lang.Nums;
    import xyz.srclab.common.test.TestLogger;

    import java.util.*;

    public class CollectSample {

        private static final TestLogger logger = TestLogger.DEFAULT;

        @Test
        public void testArray() {
            String[] strings = Collects.newArray("1", "2", "3");
            Collects.asList(strings).set(0, "111");
            //111
            logger.log("string[0]: {}", strings[0]);
        }

        @Test
        public void testCollect() {
            List<String> list = new ArrayList<>();
            list.add("1");
            list.add("2");
            list.add("3");
            ListOps<String> listOps = ListOps.opsFor(list);
            int sum = listOps.addAll(Collects.newArray("4", "5", "6"))
                .removeFirst()
                .map(it -> it + "0")
                .map(Nums::toInt)
                .reduce(Integer::sum);
            //200
            logger.log("sum: {}", sum);
        }

        @Test
        public void testMultiMap() {
            SetMap<String, String> setMap = SetMap.newSetMap(
                Collects.newMap(
                    new LinkedHashMap<>(),
                    "s", Collects.newCollection(new LinkedHashSet<>(), "1", "2", "3")
                )
            );
            //setMap: {s=[1, 2, 3]}
            logger.log("setMap: {}", setMap);

            MutableSetMap<String, String> mutableSetMap = MutableSetMap.newMutableSetMap(
                Collects.newMap(
                    new LinkedHashMap<>(),
                    "s", Collects.newCollection(new LinkedHashSet<>(), "1", "2", "3")
                )
            );
            mutableSetMap.add("s", "9");
            mutableSetMap.addAll("s", Collects.newCollection(new LinkedHashSet<>(), "11", "12", "13"));
            //mutableSetMap: {s=[1, 2, 3, 9, 11, 12, 13]}
            logger.log("mutableSetMap: {}", mutableSetMap);

            ListMap<String, String> listMap = ListMap.newListMap(
                Collects.newMap(
                    new LinkedHashMap<>(),
                    "s", Collects.newCollection(new LinkedList<>(), "1", "2", "3")
                )
            );
            //listMap: {s=[1, 2, 3]}
            logger.log("listMap: {}", listMap);

            MutableListMap<String, String> mutableListMap = MutableListMap.newMutableListMap(
                Collects.newMap(
                    new LinkedHashMap<>(),
                    "s", Collects.newCollection(new LinkedList<>(), "1", "2", "3")
                )
            );
            mutableListMap.add("s", "9");
            mutableListMap.addAll("s", Collects.newCollection(new LinkedList<>(), "11", "12", "13"));
            //mutableListMap: {s=[1, 2, 3, 9, 11, 12, 13]}
            logger.log("mutableListMap: {}", mutableListMap);
        }
    }

Kotlin Examples

    package sample.kotlin.xyz.srclab.core.collect

    import org.testng.annotations.Test
    import xyz.srclab.common.collect.ListMap.Companion.toListMap
    import xyz.srclab.common.collect.MutableListMap.Companion.toMutableListMap
    import xyz.srclab.common.collect.MutableSetMap.Companion.toMutableSetMap
    import xyz.srclab.common.collect.SetMap.Companion.toSetMap
    import xyz.srclab.common.collect.addElements
    import xyz.srclab.common.test.TestLogger
    import java.util.*

    class CollectSample {

        @Test
        fun testMultiMap() {
            val setMap = mapOf("s" to setOf("1", "2", "3")).toSetMap()
            //setMap: {s=[1, 2, 3]}
            logger.log("setMap: {}", setMap)

            val mutableSetMap = mutableMapOf("s" to mutableSetOf("1", "2", "3")).toMutableSetMap()
            mutableSetMap.add("s", "9")
            mutableSetMap.addAll("s", LinkedHashSet<String>().addElements("11", "12", "13"))
            //mutableSetMap: {s=[1, 2, 3, 9, 11, 12, 13]}
            logger.log("mutableSetMap: {}", mutableSetMap)

            val listMap = mapOf("s" to listOf("1", "2", "3")).toListMap()
            //listMap: {s=[1, 2, 3]}
            logger.log("listMap: {}", listMap)

            val mutableListMap = mutableMapOf("s" to mutableListOf("1", "2", "3")).toMutableListMap()
            mutableListMap.add("s", "9")
            mutableListMap.addAll("s", LinkedList<String>().addElements("11", "12", "13"))
            //mutableListMap: {s=[1, 2, 3, 9, 11, 12, 13]}
            logger.log("mutableListMap: {}", mutableListMap)
        }

        companion object {
            private val logger = TestLogger.DEFAULT
        }
    }

### Reflect

Reflect package extends reflection function, provides many convenient
reflection operation:

-   `Reflects`: Provides reflect operations;

-   `Types`: To build generic types;

-   `TypeRef`: Help to code a type reference;

Java Examples

    package sample.java.xyz.srclab.core.reflect;

    import org.testng.annotations.Test;
    import xyz.srclab.common.reflect.Reflects;
    import xyz.srclab.common.reflect.Types;
    import xyz.srclab.common.test.TestLogger;

    import java.lang.reflect.GenericArrayType;
    import java.lang.reflect.Method;
    import java.lang.reflect.ParameterizedType;
    import java.util.List;

    public class ReflectSample {

        private static final TestLogger logger = TestLogger.DEFAULT;

        @Test
        public void testReflects() {
            Method method = Reflects.method(Object.class, "toString");
            String s = Reflects.invoke(method, new Object());
            //java.lang.Object@97c879e
            logger.log("s: {}", s);
        }

        @Test
        public void testTypes() {
            ParameterizedType type = Types.parameterizedType(List.class, String.class);
            GenericArrayType arrayType = Types.genericArrayType(type);
            //java.util.List<java.lang.String>[]
            logger.log("arrayType: {}", arrayType);
        }
    }

Kotlin Examples

    package sample.kotlin.xyz.srclab.core.reflect

    import org.testng.annotations.Test
    import xyz.srclab.common.reflect.genericArrayType
    import xyz.srclab.common.reflect.invoke
    import xyz.srclab.common.reflect.method
    import xyz.srclab.common.reflect.parameterizedType
    import xyz.srclab.common.test.TestLogger

    class ReflectSample {

        @Test
        fun testReflects() {
            val method = Any::class.java.method("toString")
            val s = method.invoke<String>(Any())
            //java.lang.Object@97c879e
            logger.log("s: {}", s)
        }

        @Test
        fun testTypes() {
            val type = parameterizedType(MutableList::class.java, String::class.java)
            val arrayType = type.genericArrayType()
            //java.util.List<java.lang.String>[]
            logger.log("arrayType: {}", arrayType)
        }

        companion object {
            private val logger = TestLogger.DEFAULT
        }
    }

### IO

IO package provides Input/Output interfaces and utilities package:

-   `IOStreams`: Provides operations for `Input`/`Output`,
    `Reader`/`Writer`;

Java Examples

    package sample.java.xyz.srclab.core.io;

    import org.testng.Assert;
    import org.testng.annotations.Test;
    import xyz.srclab.common.io.IOStreams;
    import xyz.srclab.common.test.TestLogger;

    import java.io.*;
    import java.util.Arrays;
    import java.util.List;

    /**
     * @author sunqian
     */
    public class IOSample {

        private static final TestLogger logger = TestLogger.DEFAULT;

        @Test
        public void testStream() throws Exception {
            String text = "123456\r\n234567\r\n";
            InputStream input = new ByteArrayInputStream(text.getBytes());
            String inputString = IOStreams.readString(input);
            input.reset();
            logger.log("inputString: {}", inputString);
            Assert.assertEquals(inputString, text);
            byte[] bytes = IOStreams.readBytes(input);
            input.reset();
            Assert.assertEquals(bytes, text.getBytes());
            List<String> inputStrings = IOStreams.readLines(input);
            input.reset();
            Assert.assertEquals(inputStrings, Arrays.asList("123456", "234567"));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            IOStreams.readTo(input, output);
            input.reset();
            Assert.assertEquals(output.toByteArray(), bytes);
        }

        @Test
        public void testReader() throws Exception {
            String text = "123456\r\n234567\r\n";
            InputStream input = new ByteArrayInputStream(text.getBytes());
            Reader reader = IOStreams.toReader(input);
            String readString = IOStreams.readString(reader);
            input.reset();
            logger.log("readString: {}", readString);
            Assert.assertEquals(readString, text);
            char[] chars = IOStreams.readString(reader).toCharArray();
            input.reset();
            Assert.assertEquals(chars, text.toCharArray());
            List<String> readStrings = IOStreams.readLines(reader);
            input.reset();
            Assert.assertEquals(readStrings, Arrays.asList("123456", "234567"));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Writer writer = IOStreams.toWriter(output);
            IOStreams.readTo(reader, writer);
            input.reset();
            writer.flush();
            Assert.assertEquals(output.toByteArray(), text.getBytes());
        }
    }

Kotlin Examples

    package sample.kotlin.xyz.srclab.core.io

    import org.testng.Assert
    import org.testng.annotations.Test
    import xyz.srclab.common.io.*
    import xyz.srclab.common.test.TestLogger
    import java.io.ByteArrayInputStream
    import java.io.ByteArrayOutputStream
    import java.io.InputStream
    import java.util.*

    /**
     * @author sunqian
     */
    class IOSample {

        @Test
        @Throws(Exception::class)
        fun testStream() {
            val text = "123456\r\n234567\r\n"
            val input: InputStream = ByteArrayInputStream(text.toByteArray())
            val inputString = input.readString()
            input.reset()
            logger.log("inputString: {}", inputString)
            Assert.assertEquals(inputString, text)
            val bytes = input.readBytes()
            input.reset()
            Assert.assertEquals(bytes, text.toByteArray())
            val inputStrings: List<String?> = input.readLines()
            input.reset()
            Assert.assertEquals(inputStrings, Arrays.asList("123456", "234567"))
            val output = ByteArrayOutputStream()
            input.readTo(output)
            input.reset()
            Assert.assertEquals(output.toByteArray(), bytes)
        }

        @Test
        @Throws(Exception::class)
        fun testReader() {
            val text = "123456\r\n234567\r\n"
            val input: InputStream = ByteArrayInputStream(text.toByteArray())
            val reader = input.toReader()
            val readString = reader.readString()
            input.reset()
            logger.log("readString: {}", readString)
            Assert.assertEquals(readString, text)
            val chars = reader.readString().toCharArray()
            input.reset()
            Assert.assertEquals(chars, text.toCharArray())
            val readStrings: List<String?> = reader.readLines()
            input.reset()
            Assert.assertEquals(readStrings, Arrays.asList("123456", "234567"))
            val output = ByteArrayOutputStream()
            val writer = output.toWriter()
            reader.readTo(writer)
            input.reset()
            writer.flush()
            Assert.assertEquals(output.toByteArray(), text.toByteArray())
        }

        companion object {
            private val logger = TestLogger.DEFAULT
        }
    }

### Jvm

Jvm provides jvm underlying operations:

-   `Jvms`: Provides JVM underlying operations;

Java Examples

    package sample.java.xyz.srclab.core.jvm;

    import org.testng.annotations.Test;
    import xyz.srclab.common.jvm.Jvms;
    import xyz.srclab.common.test.TestLogger;

    public class JvmSample {

        private static final TestLogger logger = TestLogger.DEFAULT;

        @Test
        public void testJvms() {
            String jvmDescriptor = Jvms.jvmDescriptor(int.class);
            //I
            logger.log("jvmDescriptor: {}", jvmDescriptor);
        }
    }

Kotlin Examples

    package sample.kotlin.xyz.srclab.core.jvm

    import org.testng.annotations.Test
    import xyz.srclab.common.jvm.jvmDescriptor
    import xyz.srclab.common.test.TestLogger

    class JvmSample {

        @Test
        fun testJvms() {
            val jvmDescriptor = Int::class.javaPrimitiveType!!.jvmDescriptor
            //I
            logger.log("jvmDescriptor: {}", jvmDescriptor)
        }

        companion object {
            private val logger = TestLogger.DEFAULT
        }
    }

### Utils

Utils package provides Other convenient tools:

-   `Counter`: Simple counter for int/long, may atomic;

-   `About`: Product info such as `About`, `Author`, `SemVer`;

Java Examples

    package sample.java.xyz.srclab.core.utils;

    import org.testng.annotations.Test;
    import xyz.srclab.common.test.TestLogger;
    import xyz.srclab.common.utils.About;
    import xyz.srclab.common.utils.Author;
    import xyz.srclab.common.utils.Counter;
    import xyz.srclab.common.utils.SemVer;

    import java.util.Collections;

    public class UtilsSample {

        private static final TestLogger logger = TestLogger.DEFAULT;

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

Kotlin Examples

    package sample.kotlin.xyz.srclab.core.utils

    import org.testng.annotations.Test
    import xyz.srclab.common.test.TestLogger
    import xyz.srclab.common.utils.About
    import xyz.srclab.common.utils.Author
    import xyz.srclab.common.utils.Counter.Companion.counterStarts
    import xyz.srclab.common.utils.SemVer
    import xyz.srclab.common.utils.SemVer.Companion.parseSemVer

    class BaseSample {

        @Test
        fun testAbout() {
            val verString = "1.2.3-beta.2.3+123"
            val semVer: SemVer = verString.parseSemVer()
            val about = About.of(
                "name",
                semVer.normalString,
                listOf(Author.of("name", "author@mail.com", null)),
                "123@123.com",
                "url",
                listOf("licence"),
                listOf(
                    About.of(
                        "poweredBy",
                        null,
                        emptyList(),
                        null,
                        null,
                        emptyList(),
                        emptyList(),
                        null
                    )
                ),
                "© 2021 SrcLab"
            )
            //name
            //Version: 1.2.3
            //Author: name(author@mail.com)
            //Mail: 123@123.com
            //Url: url
            //Licence: licence
            //Powered by: poweredBy
            //© 2021 SrcLab
            logger.log("About: {}", about)
        }

        @Test
        fun testCounter() {
            val counter = 100.counterStarts()
            counter.getAndIncrementInt()
            counter.reset()
            val atomicCounter = 100.counterStarts(true)
            atomicCounter.incrementAndGetInt()
            atomicCounter.reset()
        }

        companion object {
            private val logger = TestLogger.DEFAULT
        }
    }

### Test

Test package provides interfaces and utilities for testing:

-   `Tester`: Core interface to run test tasks;

-   `TestTask`, `TestListener`: Cor interface of test task and listener;

-   `TestLogger`: Simple logger for testing;

-   `Tests`: Default utilities for testing;

Java Examples

    package sample.java.xyz.srclab.core.test;

    import org.testng.annotations.Test;
    import xyz.srclab.common.test.TestLogger;
    import xyz.srclab.common.test.TestTask;
    import xyz.srclab.common.test.Tests;

    import java.util.Arrays;

    public class TestSample {

        private static final TestLogger logger = TestLogger.DEFAULT;

        @Test
        public void testTests() {
            Tests.testTasks(Arrays.asList(
                TestTask.newTask(() -> {
                    logger.log("Run test task!");
                })
            ));
        }
    }

Kotlin Examples

    package sample.kotlin.xyz.srclab.core.test

    import org.testng.annotations.Test
    import xyz.srclab.common.test.TestLogger
    import xyz.srclab.common.test.TestTask
    import xyz.srclab.common.test.testTasks

    class TestSample {

        @Test
        fun testTests() {
            testTasks(
                listOf(
                    TestTask.newTask { logger.log("Run test task!") }
                )
            )
        }

        companion object {
            private val logger = TestLogger.DEFAULT
        }
    }
