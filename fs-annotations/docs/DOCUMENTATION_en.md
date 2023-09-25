# <span class="image">![Boat Annotations](../../logo.svg)</span> `boat-annotations`: Boat Annotations — Annotations of [Boat](../../README.md)

<span id="author" class="author">Sun Qian</span>
<span id="email" class="email"><fredsuvn@163.com></span>

Boat Annotations provides a set of annotations to make codes clear and
clean:

-   **DefaultNonNull**/**DefaultNullable**: It tells that all variables,
    fields, parameters, and other use of type are non-null/nullable by
    default in annotated scope, usually used in package-info.java;

-   **NotNull**/**Nullable**: It tells that annotated variable, field,
    parameter, and other use of type is non-null/nullable;

-   **JavaBean**: It tells that annotated type is a java-bean, which all
    properties are nullable by default;

-   **Acceptable**/**Accepted**: It tells that annotated parameter only
    accepts specified types;

-   **Rejectable**/**Rejected**: It tells that annotated parameter will
    reject specified types;

-   **Written**: It tells that the parameter may be written;

-   **Immutable**: It tells that annotated variable, field, parameter,
    and other use of type is immutable and thread-safe;

-   **ThreadSafe**: It tells that annotated variable, field, parameter,
    and other use of type is thread-safe;

-   **ThreadSafeIf**: It tells that annotated variable, field,
    parameter, and other use of type is thread-safe if specified
    conditions were met;

Java Examples

    package sample.java.xyz.srclab.annotations;

    import org.testng.Assert;
    import org.testng.annotations.Test;
    import xyz.srclab.annotations.AcceptedType;
    import xyz.srclab.annotations.JavaBean;
    import xyz.srclab.annotations.NonNull;
    import xyz.srclab.annotations.OutParam;

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
    import xyz.srclab.annotations.AcceptedTypes
    import xyz.srclab.annotations.AcceptedType
    import xyz.srclab.annotations.OutParam

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
