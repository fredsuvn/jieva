# Boat: SrcLab的基础Java/kotlin库

## Variables

* boat-version: 1.0.0

## 修订

|Date|Revision|Author|Content|
|---|---|---|---|
|日期|修订版|作者|内容
|2020-12-10|0.0.0|Sun Qian fredsuvn@163.com|New
|2020-12-10|{boat-version}|Sun Qian fredsuvn@163.com|重构

## 简介

Boat是一组Java/Kotlin核心库集合（JDK 1.8+）, 主要由Kotlin编写, 广泛应用于SrcLab里的项目, 当然, 也可以在其他项目中使用.

Boat包括:

* *boat-annotations*: 核心注释, 如@Nullable, @NotNull, @DefaultNullable, @DefaultNotNull;
* *boat-core*: 基础工具和接口, 包括base, bean, bus, cache, collect, convert, exception, invoke, jvm, proxy, reflect, run, state
  and test;
* *boat-serialize*: 序列化工具, 包括json序列化;
* *boat-codec*: 编码功能, 支持Hex, Base64, AES, RSA, SM2 and more other algorithms;
* *boat-id*: 一个轻量级id生成框架;
* *boat-test*: 辅助引入测试库.

如果你需要引入以上全部, 只需要:

* *boat-all*: 引入以上所有模块.

## 获取

### Gradle

```groovy
implementation("xyz.srclab.common:boat-all:{boat-version}")
```

### Maven

```xml

<dependency>
    <groupId>xyz.srclab.common</groupId>
    <artifactId>boat-all</artifactId>
    <version>{boat-version}</version>
</dependency>
```

### 源代码

https://github.com/srclab-projects/boat

## 使用

- [Boat Annotation](#usage-annotations)
- [Boat Core](#usage-core)
    * [Base](#usage-core-base)
    * [Bean](#usage-core-bean)
    * [Bus](#usage-core-bus)
    * [Cache](#usage-core-cache)
    * [Collect](#usage-core-collect)
    * [Convert](#usage-core-convert)
    * [Exception](#usage-core-exception)
    * [Invoke](#usage-core-invoke)
    * [Jvm](#usage-core-jvm)
    * [Proxy](#usage-core-proxy)
    * [Reflect](#usage-core-reflect)
    * [Run](#usage-core-run)
    * [State](#usage-core-state)
    * [Test](#usage-core-test)
- [Boat Serialize](#usage-serialize)
- [Boat Codec](#usage-codec)
- [Boat Id](#usage-id)

### <a id="usage-annotations"/>BoatAnnotations (boat-annotations)

适当的注释可以让代码清晰整洁

* *DefaultNotNull*/*DefaultNullable*: 指定被注释的对象默认不为空或可以为空, 通常用在package-info.java里. 这些注释继承自jsr305的Nonnull,
  IDE比如IDEA可以识别他们;
* *NonNull*/*Nullable*: 指定被注释的对象不为空或可以为空. 这些注释继承自jsr305的Nonnull, IDE比如IDEA可以识别他们;
* *OutParam*/*OutReturn*: 指定参数可以被修改并返回;
* *Immutable*: 指定被注释的对象是不可变并且线程安全的;
* *ThreadSafe*: 指定被注释的对象是线程安全的;
* *ThreadSafeDependOn*: 指定被注释的对象本身是线程安全的, 但是其依赖了第三方对象, 最终是否线程安全需要看被依赖的对象;
* *PossibleTypes*: 指定实际类型在PossibleTypes所指定的类型范围内.

### <a id="usage-core"/>Boat Core (boat-core)

#### <a id="usage-core-base"/>Base

Base包提供基础工具包括:

* 快捷对象: Current, Default, Environment;
* 基础静态工具类: Anys, Bools, Chars, Nums, Dates, Checks, Requires, Loaders, Sorts;
* 语法增强工具: Let, Ref, Lazy;
* 基础对象工具: Counter, Format, NamingCase, Shell, SpecParser;
* 辅助类基类: SimpleAccessor, CachingProductBuilder.

##### Java Examples

```java
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
        Version version = Version.parse(verString);
        About about = About.of(
                "name",
                "url",
                version,
                Licence.of("lName", "lUrl"),
                PoweredBy.of("pName", "pUrl", "pMail")
        );
        //name 1.2.3-beta.2.3+123, release on 2021-02-07T14:49:36.787+08:00[Asia/Shanghai]
        //url
        //Under the lName licence
        //Powered by pName
        logger.log("About: {}", about);
    }
}
```

##### Kotlin Examples

```kotlin
class BaseSampleKt {

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
        logger.log(Default.charset)
        //Locale.getDefault();
        logger.log(Default.locale)
    }

    @Test
    fun testEnvironment() {
        logger.log(Environment.getProperty(Environment.KEY_OS_ARCH))
        logger.log(Environment.availableProcessors)
        logger.log(Environment.osVersion)
        logger.log(Environment.isOsWindows)
    }

    @Test
    fun testFormat() {
        val byFast = "1, 2, {}".fastFormat(3)
        val byMessage = "1, 2, {0}".messageFormat(3)
        val byPrintf = "1, 2, %d".printfFormat(3)
        //1, 2, 3
        logger.log("byFast: {}", byFast)
        logger.log("byMessage: {}", byMessage)
        logger.log("byPrintf: {}", byPrintf)
    }

    @Test
    fun testNamingCase() {
        val upperCamel = "UpperCamel"
        val lowerCamel = NamingCase.UPPER_CAMEL.convertTo(upperCamel, NamingCase.LOWER_CAMEL)
        //upperCamel
        logger.log("lowerCamel: {}", lowerCamel)
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
    fun testUtils() {

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
    }

    @Test
    fun testCachingBuilder() {

        class CachingBuilderSample : CachingProductBuilder<String>() {
            private var value = "null"
            fun setValue(value: String) {
                this.value = value
                commitChange()
            }

            override fun buildNew(): String {
                return value + UUID.randomUUID().toString()
            }
        }

        val cachingBuilderSample = CachingBuilderSample()
        cachingBuilderSample.setValue("1")
        val value1 = cachingBuilderSample.build()
        val value2 = cachingBuilderSample.build()
        cachingBuilderSample.setValue("2")
        val value3 = cachingBuilderSample.build()
        //10c66dae9-c056-464e-8117-4787914c3af8
        logger.log("value1: {}", value1)
        //10c66dae9-c056-464e-8117-4787914c3af8
        logger.log("value2: {}", value2)
        //2c7c2e230-50b0-4a0f-8530-151723297fb8
        logger.log("value3: {}", value3)
    }

    @Test
    fun testShell() {
        val shell = Shell.DEFAULT
        shell.println("Hello", ",", "World", "!")
        shell.println(Arrays.asList("Hello", ",", "World", "!"))
        shell.println("123", ControlChars.linefeed, "456", EscapeChars.newline, EscapeChars.reset)
        shell.println(
            SgrChars.foregroundRed("red"),
            SgrChars.backgroundCyan(" "),
            SgrChars.foregroundGreen("green")
        )
        shell.println(
            SgrChars.withParam("bright red", SgrParam.FOREGROUND_BRIGHT_RED),
            SgrChars.backgroundCyan(" "),
            SgrChars.withParam("bright green", SgrParam.FOREGROUND_BRIGHT_GREEN)
        )
        shell.println(
            SgrChars.withParam("color 8", SgrParam.foregroundColor(8)),
            SgrChars.backgroundCyan(" "),
            SgrChars.withParam("rgb(100, 100, 50)", SgrParam.foregroundColor(100, 100, 50))
        )
        shell.println(ControlChars.beep)
        shell.println("123", ControlChars.backspaces, "456", ControlChars.beep)
    }

    @Test
    fun testAbout() {
        val verString = "1.2.3-beta.2.3+123"
        val version: Version = verString.parseToVersion()
        val about = About.of(
            "name",
            "url",
            version,
            Licence.of("lName", "lUrl"),
            PoweredBy.of("pName", "pUrl", "pMail")
        )
        //name 1.2.3-beta.2.3+123, release on 2021-02-07T14:49:36.787+08:00[Asia/Shanghai]
        //url
        //Under the lName licence
        //Powered by pName
        logger.log("About: {}", about)
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}
```

#### <a id="usage-core-bean"/>Bean

Bean包提供强大的bean操作能力:

* Beans: bean操作工具类;
* BeanResolver: bean解析接口, Beans使用其默认实现;

##### Java Examples

```java
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
```

##### Kotlin Examples

```kotlin
class BeanSampleKt {

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
```

#### <a id="usage-core-bus"/>Bus

Bus包提供EventBus.

##### Java Examples

```java
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
```

##### Kotlin Examples

```kotlin
class EventBusSampleKt {

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
```

#### <a id="usage-core-cache"/>Cache

Boat提供一个Cache接口和若干实现:

* FastCache
* CaffeineCache
* GuavaCache
* MapCache
* ThreadLocalCache

##### Java Examples

```java
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
```

##### Kotlin Examples

```kotlin
class CacheSampleKt {

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
```

#### <a id="usage-core-collect"/>Collect

Collect包提供工具类Collects和ArrayCollects, 提供Ops接口来实现链式调用.

##### Java Examples

```java
public class CollectSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testArray() {
        String[] strings = ArrayCollects.newArray("1", "2", "3");
        ArrayCollects.asList(strings).set(0, "111");
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
        int sum = listOps.addAll(ArrayCollects.newArray("4", "5", "6"))
                .removeFirst()
                .map(it -> it + "0")
                .map(Nums::toInt)
                .reduce(Integer::sum);
        //200
        logger.log("sum: {}", sum);
    }
}
```

#### <a id="usage-core-convert"/>Convert

Convert包提供类型转换功能:

* Converts: 转换工具类;
* Converter: 转换接口, Converts使用其默认实现.

##### Java Examples

```java
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
```

##### Kotlin Examples

```kotlin
class ConvertSampleKt {

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
```

#### <a id="usage-core-exception"/>Exception

Exception包提供StatusException和ExceptionStatus, 继承自State (参考[State](#usage-core-state)), 还有一个ShouldNotException.

##### Java Examples

```java
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
```

##### Kotlin Examples

```kotlin
class ExceptionSampleKt {

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
```

#### <a id="usage-core-invoke"/>Invoke

Invoke包提供Invoker接口去调用方法和函数.

##### Java Examples

```java
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
```

##### Kotlin Examples

```kotlin
class InvokeSampleKt {

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
```

#### <a id="usage-core-jvm"/>Jvm

Jvm包提供Jvms工具类.

##### Java Examples

```java
public class JvmSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testJvms() {
        String jvmDescriptor = Jvms.jvmDescriptor(int.class);
        //I
        logger.log("jvmDescriptor: {}", jvmDescriptor);
    }
}
```

##### Kotlin Examples

```kotlin
class JvmSampleKt {

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
```

#### <a id="usage-core-proxy"/>Proxy

Proxy提供类代理功能, 底层使用spring-cglib, cglib或者JDK proxy.

##### Java Examples

```java
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
```

##### Kotlin Examples

```kotlin
class ProxySampleKt {

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
```

#### <a id="usage-core-reflect"/>Reflect

Reflect包提供相关工具类:

* Reflects: 提供反射操作;
* Types: 用来创建泛型类型;
* TypeRef: 用来获取类型引用.

##### Java Examples

```java
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
```

##### Kotlin Examples

```kotlin
class ReflectSampleKt {

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
```

#### <a id="usage-core-run"/>Run

Run包提供Runner和ScheduledRunner接口来运行一段代码, 可以在一个线程或者协程里.

##### Java Examples

```java
public class RunSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testRunner() {
        Runner runner = Runner.SYNC_RUNNER;
        IntRef intRef = IntRef.of(0);
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
        ScheduledRunner runner = ScheduledRunner.SINGLE_THREAD_RUNNER;
        IntRef intRef = IntRef.of(0);
        ScheduledRunning<?> running = runner.scheduleWithFixedDelay(Duration.ZERO, Duration.ofMillis(1000), () -> {
            intRef.set(intRef.get() + 100);
            return null;
        });
        Current.sleep(2500);
        running.cancel(false);
        //300
        logger.log("int: {}", intRef.get());
    }
}
```

##### Kotlin Examples

```kotlin
class RunSampleKt {

    @Test
    fun testRunner() {
        val runner: Runner = Runner.SYNC_RUNNER
        val intRef = of(0)
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
        val runner = ScheduledRunner.SINGLE_THREAD_RUNNER
        val intRef = of(0)
        val running: ScheduledRunning<*> = runner.scheduleWithFixedDelay<Any?>(Duration.ZERO, Duration.ofMillis(1000)) {
            intRef.set(intRef.get() + 100)
            null
        }
        sleep(2500)
        running.cancel(false)
        //300
        logger.log("int: {}", intRef.get())
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}
```

#### <a id="usage-core-state"/>State

State包提供State接口来复制定制状态概念的类型.

##### Java Examples

```java
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
        private final String description;

        public MyState(int code, String description) {
            this.code = code;
            this.description = description;
        }

        @Override
        public Integer code() {
            return code;
        }

        @Nullable
        @Override
        public String description() {
            return description;
        }

        @NotNull
        @Override
        public MyState withNewDescription(@Nullable String newDescription) {
            return new MyState(code, newDescription);
        }

        @NotNull
        @Override
        public MyState withMoreDescription(@Nullable String moreDescription) {
            return new MyState(code, State.moreDescription(description, moreDescription));
        }
    }
}
```

##### Kotlin Examples

```kotlin
class StateSampleKt {

    @Test
    fun testState() {
        val myState = MyState(1, "description")
        val newState = myState.withMoreDescription("cause")
        //description[cause]
        logger.log(newState.description)
    }

    class MyState(override val code: Int, override val description: String?) :
        State<Int, String, MyState> {

        override fun withNewDescription(newDescription: String?): MyState {
            return MyState(code, newDescription)
        }

        override fun withMoreDescription(moreDescription: String?): MyState {
            return MyState(code, description.stateMoreDescription(moreDescription))
        }
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}
```

#### <a id="usage-core-test"/>Test

Test包提供简单的测试工具:

* Tester: 一个用来启动测试任务的接口;
* TestLogger: 简单的测试日志;
* TestTask and TestListener: 测试任务和监听器;
* Tests: 测试工具类.

##### Java Examples

```java
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
```

##### Kotlin Examples

```kotlin
class TestSampleKt {

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
```

### <a id="usage-serialize"/>Boat Serialize (boat-serialize)

Boat serialize (需要引入boat-serialize)提供通用序列化接口. 在当前版本, boat-serialize提供JsonSerializer.

#### Java Examples

```java
public class SerializeSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testJsonSerialize() {
        Json json = JsonSerials.toJson("{\"p1\":\"p1 value\",\"p2\":\"p2 value\"}");
        Map<String, String> map = json.toJavaObject(new TypeRef<Map<String, String>>() {});
        //{p1=p1 value, p2=p2 value}
        logger.log(map);
    }
}
```

#### Kotlin Examples

```kotlin
class SerializeSampleKt {

    @Test
    fun testJsonSerialize() {
        val json = "{\"p1\":\"p1 value\",\"p2\":\"p2 value\"}".toJson()
        val map: Map<String, String> = json.toJavaObject(object : TypeRef<Map<String, String>>() {})
        //{p1=p1 value, p2=p2 value}
        logger.log(map)
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}
```

### <a id="usage-codec"/>Boat Codec (boat-codec)

Boat codec (需要引入boat-codec)提供Codec, CodecKeys, AesKeys 以及 其他接口来实现编码功能, 支持hex, base64, AES, RSA, SM2以及更多算法.

#### Java Examples

```java
public class CodecSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testCodec() {
        String password = "hei, xiongdi, womenhaojiubujiannizainali";
        String messageBase64 = "aGVpLCBwZW5neW91LCBydWd1b3poZW5kZXNoaW5pcWluZ2Rhemhhb2h1";
        SecretKey secretKey = AesKeys.newKey(password);

        //Use static
        String message = Codec.decodeBase64String(messageBase64);
        byte[] encrypt = Codec.aesCipher().encrypt(secretKey, message);
        String decrypt = Codec.aesCipher().decryptToString(secretKey, encrypt);
        //hei, pengyou, ruguozhendeshiniqingdazhaohu
        logger.log("decrypt: {}", decrypt);

        //Use chain
        encrypt = Codec.forData(messageBase64).decodeBase64().encryptAes(secretKey).doFinal();
        decrypt = Codec.forData(encrypt).decryptAes(secretKey).doFinalToString();
        //hei, pengyou, ruguozhendeshiniqingdazhaohu
        logger.log("decrypt: {}", decrypt);
    }
}
```

#### Kotlin Examples

```kotlin
class CodecSampleKt {

    @Test
    fun testCodec() {
        val password = "hei, xiongdi, womenhaojiubujiannizainali"
        val messageBase64 = "aGVpLCBwZW5neW91LCBydWd1b3poZW5kZXNoaW5pcWluZ2Rhemhhb2h1"
        val secretKey = password.toAesKey()

        //Use static
        val message: String = messageBase64.decodeBase64String()
        var encrypt = Codec.aesCipher().encrypt(secretKey, message)
        var decrypt = Codec.aesCipher().decryptToString(secretKey, encrypt)
        //hei, pengyou, ruguozhendeshiniqingdazhaohu
        logger.log("decrypt: {}", decrypt)

        //Use chain
        encrypt = Codec.forData(messageBase64).decodeBase64().encryptAes(secretKey).doFinal()
        decrypt = Codec.forData(encrypt).decryptAes(secretKey).doFinalToString()
        //hei, pengyou, ruguozhendeshiniqingdazhaohu
        logger.log("decrypt: {}", decrypt)
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}
```

### <a id="usage-id"/>Boat Id (boat-id)

Boat id (需要引入boat-id)是一个轻量级的id生成框架. 提供IdFactory接口来构造任意类型的id, 以及StringIdSpec 来快速构造String类型的id.

#### Java Examples

```java
public class IdSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testId() {
        String spec = "seq-{TimeCount,yyyyMMddHHmmssSSS,1023,%17s%04d}-{Constant,tail}";
        StringIdSpec stringIdSpec = new StringIdSpec(spec);
        //seq-202102071449568890000-tail
        for (int i = 0; i < 10; i++) {
            logger.log(stringIdSpec.newId());
        }
    }
}
```

#### Kotlin Examples

```kotlin
class IdSampleKt {

    @Test
    fun testId() {
        val spec = "seq-{TimeCount,yyyyMMddHHmmssSSS,1023,%17s%04d}-{Constant,tail}"
        val stringIdSpec = StringIdSpec(spec)
        //seq-202102071449568890000-tail
        for (i in 0..9) {
            logger.log(stringIdSpec.newId())
        }
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}
```

## 贡献和联系方式

* <fredsuvn@163.com>
* https://github.com/srclab-projects/boat
* QQ群: 1037555759

## License

[Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0.html)