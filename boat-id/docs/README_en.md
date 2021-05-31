# <span class="image">![Boat Id](../../logo.svg)</span> `boat-id`: Boat Id — Id Generation Lib of [Boat](../../README.md)

<span id="author" class="author">Sun Qian</span>  
<span id="email" class="email"><fredsuvn@163.com></span>  

Table of Contents

-   [Introduction](#_introduction)
-   [Usage](#_usage)
    -   [IdSpec](#_idspec)
    -   [IdGenerator](#_idgenerator)
-   [Samples](#_samples)

## Introduction

Boat Id is a simple id generation lib for [Boat](../../README.md). It
provides a set of interface such as `IdGenerator` and `IdSpec` to
generate new id.

## Usage

### IdSpec

To create an id generation rule, we can use `IdSpec`:

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

The result may be: `seq-06803239610792857600-tail`

`IdSpec` consists of literals and `IdComponent`'s, `IdComponent`s are
wrapped between `{` and `}`, format is `{type[: name][, arg]+}` (a comma
follows a space or not, note space will be trimmed).

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
<td class="content"><code>IdSpec</code>'s parameter syntax is come from <code>CharsTemplate</code> of <code>boat-core</code>.</td>
</tr>
</tbody>
</table>

`IdSpec` has 1 built-in `IdComponent`:

-   `Snowflake`

For more detail about `IdSpec`, see its javadoc.

### IdGenerator

`IdGenerator` is core interface of boat-id, `IdSpec` is its subtype. We
can create an `IdGenerator` to custom generation rules.

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

Then, add the `MyIdComponent`:

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
            //seq0-06803239610792857600-tail
            String spec0 = "seq0-{Snowflake, 20, 41, 10, 12}-tail";
            IdSpec stringIdSpec0 = new IdSpec(spec0);
            for (int i = 0; i < 10; i++) {
                logger.log(stringIdSpec0.newId());
            }

            //seq1-00001826267315077279180346359808-tail
            String spec1 = "seq1-{Snowflake, 32, 55, 25, 25}-tail";
            IdSpec stringIdSpec1 = new IdSpec(spec1);
            for (int i = 0; i < 10; i++) {
                logger.log(stringIdSpec1.newId());
            }

            //seq2-29921563690270857976266765631488-tail
            String spec2 = "seq2-{Snowflake, 32, 63, 32, 32}-tail";
            IdSpec stringIdSpec2 = new IdSpec(spec2);
            for (int i = 0; i < 10; i++) {
                logger.log(stringIdSpec2.newId());
            }

            //seq3{}-06803240106559590400-tail
            String spec3 = "seq3\\{}-{Snowflake, 20, 41, 10, 12}-tail";
            IdSpec stringIdSpec3 = new IdSpec(spec3);
            for (int i = 0; i < 10; i++) {
                logger.log(stringIdSpec3.newId());
            }

            //seq4{}-06805124180752646144-tail
            String spec4 = "seq4\\{\\}-{Snowflake, 20, 41, 10, 12}-tail";
            IdSpec stringIdSpec4 = new IdSpec(spec4);
            for (int i = 0; i < 10; i++) {
                logger.log(stringIdSpec4.newId());
            }

            String spec5 = "seq5\\{\\}-{Snowflake, 20, 41, 10, 12";
            Assert.expectThrows(IllegalArgumentException.class, () -> new IdSpec(spec5));
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
            //seq0-06803239610792857600-tail
            val spec0 = "seq0-{Snowflake, 20, 41, 10, 12}-tail"
            val stringIdSpec0 = IdSpec(spec0)
            for (i in 0..9) {
                logger.log(stringIdSpec0.newId())
            }

            //seq1-00001826267315077279180346359808-tail
            val spec1 = "seq1-{Snowflake, 32, 55, 25, 25}-tail"
            val stringIdSpec1 = IdSpec(spec1)
            for (i in 0..9) {
                logger.log(stringIdSpec1.newId())
            }

            //seq2-29921563690270857976266765631488-tail
            val spec2 = "seq2-{Snowflake, 32, 63, 32, 32}-tail"
            val stringIdSpec2 = IdSpec(spec2)
            for (i in 0..9) {
                logger.log(stringIdSpec2.newId())
            }

            //seq3{}-06803240106559590400-tail
            val spec3 = "seq3\\{}-{Snowflake, 20, 41, 10, 12}-tail"
            val stringIdSpec3 = IdSpec(spec3)
            for (i in 0..9) {
                logger.log(stringIdSpec3.newId())
            }

            //seq4{}-06805124180752646144-tail
            val spec4 = "seq4\\{\\}-{Snowflake, 20, 41, 10, 12}-tail"
            val stringIdSpec4 = IdSpec(spec4)
            for (i in 0..9) {
                logger.log(stringIdSpec4.newId())
            }

            val spec5 = "seq5\\{\\}-{Snowflake, 20, 41, 10, 12"
            Assert.expectThrows(
                IllegalArgumentException::class.java
            ) { IdSpec(spec5) }
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
