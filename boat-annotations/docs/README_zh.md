# <span class="image">![Boat Annotations](../../logo.svg)</span> `boat-annotations`: Boat Annotations — [Boat](../../README.md) 注解库

<span id="author" class="author">Sun Qian</span>  
<span id="email" class="email"><fredsuvn@163.com></span>  

Boat Annotations提供了许多可以让代码整洁又干净的注解:

-   **DefaultNonNull**/**DefaultNullable**: 它说明注解范围内所有的变量,
    属性, 参数和类型使用默认都是non-null/nullable的,
    通常用在package-info.java中;

-   **NotNull**/**Nullable**: 它说明被注解的变量, 属性,
    参数和类型使用是non-null/nullable的;

-   **JavaBean**: 它说明被注解的类型是一个javabean,
    所有的属性默认都是nullable的;

-   **Acceptable**/**Accepted**: 它说明参数只能接受指定的几个类型;

-   **Rejectable**/**Rejected**: 它说明参数不接受指定的几个类型;

-   **Written**: 它说明参数可能被进行写操作;

-   **Immutable**: 它说明被注解的变量, 属性,
    参数和类型使用是不可变和线程安全的;

-   **ThreadSafe**: 它说明被注解的变量, 属性,
    参数和类型使用是线程安全的;

-   **ThreadSafeIf**: 它说明被注解的变量, 属性,
    参数和类型使用在满足指定条件的情况下是线程安全的;

.Java Examples

    package sample.java.xyz.srclab.annotations;

    import org.testng.Assert;
    import org.testng.annotations.Test;
    import xyz.srclab.annotations.Accepted;
    import xyz.srclab.annotations.JavaBean;
    import xyz.srclab.annotations.NonNull;
    import xyz.srclab.annotations.Written;

    public class AnnotationSample {

        @Test
        public void testAnnotations() {
            TestBean testBean = new TestBean();
            Assert.assertEquals(testBean.getP2().substring(1), "2");
            Assert.expectThrows(NullPointerException.class, () -> testBean.getP1().substring(1));

            StringBuilder buffer = new StringBuilder();
            writeBuffer(buffer, "123");
            Assert.assertEquals(buffer.toString(), "123");
        }

        private void writeBuffer(
            @Written StringBuilder buffer,
            @Accepted(String.class) @Accepted(StringBuffer.class) CharSequence readOnly
        ) {
            buffer.append(readOnly);
        }

        @JavaBean
        public static class TestBean {

            private String p1;
            @NonNull
            private String p2 = "p2";

            public String getP1() {
                return p1;
            }

            public void setP1(String p1) {
                this.p1 = p1;
            }

            @NonNull
            public String getP2() {
                return p2;
            }

            public void setP2(@NonNull String p2) {
                this.p2 = p2;
            }
        }
    }

Kotlin Examples

    package sample.kotlin.xyz.srclab.annotations

    import org.testng.Assert
    import org.testng.annotations.Test
    import xyz.srclab.annotations.Acceptable
    import xyz.srclab.annotations.Accepted
    import xyz.srclab.annotations.Written

    class AnnotationSample {

        @Test
        fun testAnnotations() {
            val buffer = StringBuilder()
            buffer.writeBuffer("123")
            Assert.assertEquals(buffer.toString(), "123")
        }

        private fun @receiver:Written StringBuilder.writeBuffer(
            @Acceptable(
                Accepted(String::class),
                Accepted(StringBuffer::class),
            )
            readOnly: String
        ) {
            this.append(readOnly)
        }
    }
