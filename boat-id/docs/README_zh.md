# <span class="image">![Boat Id](../../logo.svg)</span> `boat-id`: Boat Id — [Boat](../../README.md) ID生成库

<span id="author" class="author">Sun Qian</span>  
<span id="email" class="email"><fredsuvn@163.com></span>  

目录

-   [简介](#_简介)
-   [Usage](#_usage)
    -   [IdSpec](#_idspec)
    -   [IdGenerator](#_idgenerator)
-   [Samples](#_samples)

## 简介

Boat Id 是一个简单的 [Boat](../../README.md) ID生成库. 它提供一套接口如
`IdGenerator` 和 `IdSpec` 来生成新id.

## Usage

### IdSpec

要创建一套id生成规则, 我们可以使用 `IdSpec`:

Java Examples

    class Example{
        @Test
        public void test() {
            String spec = "seq-{Snowflake, 20, 41, 10, 12}-tail";
            IdSpec stringIdSpec = new IdSpec(spec);
            String id = stringIdSpec.newId();
        }
    }

Kotlin Examples

    class Example {
        @Test
        fun test() {
            val spec = "seq-{Snowflake, 20, 41, 10, 12}-tail"
            val stringIdSpec = IdSpec(spec)
            val id = stringIdSpec.newId()
        }
    }

结果可能是: `seq-06803239610792857600-tail`

`IdSpec` 由字面量和 `IdComponent`们组成, `IdComponent`是被 `{` and `}`
包裹的申明, 格式是 `{type[: name][, arg]+}`
(一个逗号后面可以跟一个空格或者不跟, 因为空格都会被忽略).

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon"><div class="title">
Note
</div></td>
<td class="content"><code>IdSpec</code> 的语法规则来自 <code>boat-core</code> 的 <code>CharsTemplate</code>.</td>
</tr>
</tbody>
</table>

`IdSpec` 有1个内置 `IdComponent`:

-   `Snowflake`

更多关于 `IdSpec` 的细节请参阅它的javadoc.

### IdGenerator

`IdGenerator` 是 boat-id 的核心接口, `IdSpec` 是它的子类.
我们可以创建一个 `IdGenerator` 来定制生成规则.

Java Examples

    public class MyIdComponent implements IdComponent<String> {

        public static final String TYPE = "My";

        private String value;

        @NotNull
        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public void init(@NotNull List<?> args) {
            this.value = String.valueOf(args.get(0));
        }

        @Override
        public String newValue(@NotNull IdContext context) {
            return value;
        }
    }

Kotlin Examples

    class MyIdComponent : IdComponent<String?> {

        private var value: String? = null
        override val type: String = TYPE

        override fun init(args: List<Any>) {
            value = args[0].toString()
        }

        override fun newValue(context: IdContext): String? {
            return value
        }

        companion object {
            const val TYPE = "My"
        }
    }

然后, 加入 `MyIdComponent`:

Java Examples

    class Example{
        @Test
        public void test() {
            IdSpec stringIdSpec = new IdSpec(spec, type -> {
                if (type.equals(MyIdComponent.TYPE)) {
                    return new MyIdComponent();
                }
                return IdSpec.DEFAULT_COMPONENT_SUPPLIER.get(type);
            });
        }
    }

Kotlin Examples

    class Example {
        @Test
        fun test() {
            val stringIdSpec = IdSpec(spec, object : ComponentSupplier {
                override fun get(type: String): IdComponent<*> {
                    if (type == MyIdComponent.TYPE) {
                        return MyIdComponent()
                    }
                    return IdSpec.DEFAULT_COMPONENT_SUPPLIER.get(type)
                }
            })
        }
    }

## Samples

Java Examples

    package sample.java.xyz.srclab.id;

    import org.testng.Assert;
    import org.testng.annotations.Test;
    import xyz.srclab.common.id.IdSpec;
    import xyz.srclab.common.test.TestLogger;

    public class IdSample {

        private static final TestLogger logger = TestLogger.DEFAULT;

        @Test
        public void testId() {
            //seq-06803239610792857600-tail
            String spec = "seq-{Snowflake, 20, 41, 10, 12}-tail";
            IdSpec stringIdSpec = new IdSpec(spec);
            for (int i = 0; i < 10; i++) {
                logger.log(stringIdSpec.newId());
            }

            //seq{}-06803240106559590400-tail
            String spec2 = "seq\\{}-{Snowflake, 20, 41, 10, 12}-tail";
            IdSpec stringIdSpec2 = new IdSpec(spec2);
            for (int i = 0; i < 10; i++) {
                logger.log(stringIdSpec2.newId());
            }

            String spec3 = "seq\\{\\}-{Snowflake, 20, 41, 10, 12";
            Assert.expectThrows(IllegalArgumentException.class, () -> new IdSpec(spec3));
            //new StringIdSpec(spec3);
        }

        @Test
        public void testCustomId() {
            String spec = "seq-{Snowflake, 20, 41, 10, 12}-{My, 88888}";
            IdSpec stringIdSpec = new IdSpec(spec, type -> {
                if (type.equals(MyIdComponent.TYPE)) {
                    return new MyIdComponent();
                }
                return IdSpec.DEFAULT_COMPONENT_SUPPLIER.get(type);
            });
            //seq-06803242693339123712-88888
            for (int i = 0; i < 10; i++) {
                logger.log(stringIdSpec.newId());
            }
        }
    }

    package sample.java.xyz.srclab.id;

    import org.jetbrains.annotations.NotNull;
    import xyz.srclab.common.id.IdComponent;
    import xyz.srclab.common.id.IdContext;

    import java.util.List;

    /**
     * @author sunqian
     */
    public class MyIdComponent implements IdComponent<String> {

        public static final String TYPE = "My";

        private String value;

        @NotNull
        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public void init(@NotNull List<?> args) {
            this.value = String.valueOf(args.get(0));
        }

        @Override
        public String newValue(@NotNull IdContext context) {
            return value;
        }
    }

Kotlin Examples

    package sample.kotlin.xyz.srclab.id

    import org.testng.Assert
    import org.testng.annotations.Test
    import xyz.srclab.common.id.IdComponent
    import xyz.srclab.common.id.IdContext
    import xyz.srclab.common.id.IdSpec
    import xyz.srclab.common.id.IdSpec.ComponentSupplier
    import xyz.srclab.common.test.TestLogger

    class IdSample {
        private val logger = TestLogger.DEFAULT

        @Test
        fun testId() {
            //seq-06803239610792857600-tail
            val spec = "seq-{Snowflake, 20, 41, 10, 12}-tail"
            val stringIdSpec = IdSpec(spec)
            for (i in 0..9) {
                logger.log(stringIdSpec.newId())
            }

            //seq{}-06803240106559590400-tail
            val spec2 = "seq\\{}-{Snowflake, 20, 41, 10, 12}-tail"
            val stringIdSpec2 = IdSpec(spec2)
            for (i in 0..9) {
                logger.log(stringIdSpec2.newId())
            }
            val spec3 = "seq\\{\\}-{Snowflake, 20, 41, 10, 12"
            Assert.expectThrows(
                IllegalArgumentException::class.java
            ) { IdSpec(spec3) }
            //new StringIdSpec(spec3);
        }

        @Test
        fun testCustomId() {
            val spec = "seq-{Snowflake, 20, 41, 10, 12}-{My, 88888}"
            val stringIdSpec = IdSpec(spec, object : ComponentSupplier {
                override fun get(type: String): IdComponent<*> {
                    if (type == MyIdComponent.TYPE) {
                        return MyIdComponent()
                    }
                    return IdSpec.DEFAULT_COMPONENT_SUPPLIER.get(type)
                }
            })
            //seq-06803242693339123712-88888
            for (i in 0..9) {
                logger.log(stringIdSpec.newId())
            }
        }
    }

    class MyIdComponent : IdComponent<String?> {

        private var value: String? = null
        override val type: String = TYPE

        override fun init(args: List<Any>) {
            value = args[0].toString()
        }

        override fun newValue(context: IdContext): String? {
            return value
        }

        companion object {
            const val TYPE = "My"
        }
    }
