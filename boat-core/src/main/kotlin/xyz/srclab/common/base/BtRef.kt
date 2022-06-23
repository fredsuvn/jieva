//@file:JvmName("BtRef")
//
//package xyz.srclab.common.base
//
//import xyz.srclab.annotations.ForJava
//import xyz.srclab.common.base.Ref.Companion.ref
//import java.util.*
//import java.util.function.Consumer
//import java.util.function.Function
//import java.util.function.Supplier
//
///**
// * Returns a [Ref] of which value from the [Optional].
// */
//fun <T : Any> Optional<T>.toRef(): Ref<T> {
//    return this.orElse(null).ref()
//}
//
///**
// * Presents a reference holds a value.
// *
// * Using `Ref` can implement chain operation in Java:
// *
// * ```
// * Date dateValue = Ref.of(intValue)
// *     .map(Utils::intToString)
// *     .accept(Utils::printString)
// *     .map(Utils::stringToDate)
// *     .get();
// * ```
// *
// * It is equivalent to:
// *
// * ```
// * String stringValue = Utils.intToString(intValue);
// * Utils.printString(stringValue);
// * Date dateValue = Utils.stringToDate(stringValue);
// * ```
// *
// * It also provides mutable operation for final variable:
// *
// * ```
// * Ref<String> ref = Ref.of("a");
// * List<String> list = Arrays.asList("b", "c", "d");
// * list.forEach(i -> ref.set(ref.get() + "-" + i));
// * Utils.printString(ref.get());//a-b-c-d
// * ```
// *
// * This interface is similar to [Optional] in chain operations, the differences are:
// *
// * * [Ref] is mutable but [Optional] is immutable;
// * * [Ref] always returns itself (although generic type may be changed) but [Optional] returns a new object;
// */
//@ForJava
//interface Ref<T : Any> : RefGetOps<T>, RefSetOps<T> {
//
//    /**
//     * Sets current value to result of [func], returns this as typed.
//     */
//    fun <R : Any> map(func: Function<in T?, R?>): Ref<R> {
//        val thisObj = this.asType<Ref<R>>()
//        thisObj.set(func.apply(getOrNull()))
//        return thisObj
//    }
//
//    /**
//     * Gets held value to [consumer], returns this.
//     */
//    fun <R : Any> accept(consumer: Consumer<in T?>) = apply {
//        consumer.accept(getOrNull())
//    }
//
//    /**
//     * Returns a new [Ref] holds the value of this ref.
//     */
//    fun copy(): Ref<T> {
//        return getOrNull().ref()
//    }
//
//    /**
//     * Returns an [Optional] of held value:
//     *
//     * ```
//     * return Optional.ofNullable(orNull());
//     * ```
//     */
//    fun toOptional(): Optional<T> {
//        return Optional.ofNullable(getOrNull())
//    }
//
//    companion object {
//
//        /**
//         * Returns a [Ref] holds the value.
//         */
//        @JvmName("of")
//        @JvmStatic
//        fun <T : Any> T?.ref(): Ref<T> {
//            return RefImpl(this)
//        }
//
//        private class RefImpl<T : Any>(private var value: T?) : Ref<T> {
//
//            override fun getOrNull(): T? {
//                return value
//            }
//
//            override fun set(value: T?) {
//                this.value = value
//            }
//        }
//    }
//}
//
///**
// * `Get` operation for [Ref].
// */
//interface RefGetOps<T : Any> {
//
//    /**
//     * Returns true if held value is not null, else false.
//     */
//    val isPresent: Boolean
//        get() {
//            return getOrNull() !== null
//        }
//
//    /**
//     * Return the value, or throw [NoSuchElementException] if it is null.
//     */
//    @Throws(NoSuchElementException::class)
//    fun get(): T {
//        return getOrNull() ?: throw NoSuchElementException()
//    }
//
//    /**
//     * Return the value, may be null.
//     */
//    fun getOrNull(): T?
//
//    /**
//     * Return the value, or default value if it is null.
//     */
//    fun getOrDefault(value: T): T {
//        return getOrNull() ?: value
//    }
//
//    /**
//     * Return the value, or else value if it is null.
//     */
//    fun getOrElse(value: Supplier<T>): T {
//        return getOrNull() ?: value.get()
//    }
//}
//
///**
// * `Set` operation for [Ref].
// */
//interface RefSetOps<T : Any> {
//
//    /**
//     * Sets the value.
//     */
//    fun set(value: T?)
//}
//
///**
// * Boolean version of [Ref].
// */
//@ForJava
//interface BooleanRef {
//
//    fun get(): Boolean
//
//    fun set(value: Boolean): BooleanRef
//
//    fun copy(): BooleanRef {
//        return get().intRef()
//    }
//
//    fun toOptional(): OptionalInt {
//        return OptionalInt.of(get().toInt())
//    }
//
//    fun toRef(): Ref<Boolean> {
//        return get().ref()
//    }
//
//    companion object {
//
//        @JvmName("of")
//        @JvmStatic
//        fun Boolean.intRef(): BooleanRef {
//            return BooleanRefImpl(this)
//        }
//
//        private class BooleanRefImpl(private var value: Boolean) : BooleanRef {
//            override fun get(): Boolean = value
//
//            override fun set(value: Boolean) = apply {
//                this.value = value
//            }
//        }
//    }
//}
//
///**
// * Byte version of [Ref].
// */
//@ForJava
//interface ByteRef {
//
//    fun get(): Byte
//
//    fun set(value: Byte): ByteRef
//
//    fun copy(): ByteRef {
//        return get().intRef()
//    }
//
//    fun toOptional(): OptionalInt {
//        return OptionalInt.of(get().toUnsignedInt())
//    }
//
//    fun toRef(): Ref<Byte> {
//        return get().ref()
//    }
//
//    companion object {
//
//        @JvmName("of")
//        @JvmStatic
//        fun Byte.intRef(): ByteRef {
//            return ByteRefImpl(this)
//        }
//
//        private class ByteRefImpl(private var value: Byte) : ByteRef {
//            override fun get(): Byte = value
//
//            override fun set(value: Byte) = apply {
//                this.value = value
//            }
//        }
//    }
//}
//
///**
// * Short version of [Ref].
// */
//@ForJava
//interface ShortRef {
//
//    fun get(): Short
//
//    fun set(value: Short): ShortRef
//
//    fun copy(): ShortRef {
//        return get().intRef()
//    }
//
//    fun toOptional(): OptionalInt {
//        return OptionalInt.of(get().toUnsignedInt())
//    }
//
//    fun toRef(): Ref<Short> {
//        return get().ref()
//    }
//
//    companion object {
//
//        @JvmName("of")
//        @JvmStatic
//        fun Short.intRef(): ShortRef {
//            return ShortRefImpl(this)
//        }
//
//        private class ShortRefImpl(private var value: Short) : ShortRef {
//            override fun get(): Short = value
//
//            override fun set(value: Short) = apply {
//                this.value = value
//            }
//        }
//    }
//}
//
///**
// * Char version of [Ref].
// */
//@ForJava
//interface CharRef {
//
//    fun get(): Char
//
//    fun set(value: Char): CharRef
//
//    fun copy(): CharRef {
//        return get().intRef()
//    }
//
//    fun toOptional(): OptionalInt {
//        return OptionalInt.of(get().code)
//    }
//
//    fun toRef(): Ref<Char> {
//        return get().ref()
//    }
//
//    companion object {
//
//        @JvmName("of")
//        @JvmStatic
//        fun Char.intRef(): CharRef {
//            return CharRefImpl(this)
//        }
//
//        private class CharRefImpl(private var value: Char) : CharRef {
//            override fun get(): Char = value
//
//            override fun set(value: Char) = apply {
//                this.value = value
//            }
//        }
//    }
//}
//
///**
// * Int version of [Ref].
// */
//@ForJava
//interface IntRef {
//
//    fun get(): Int
//
//    fun set(value: Int): IntRef
//
//    fun copy(): IntRef {
//        return get().intRef()
//    }
//
//    fun toOptional(): OptionalInt {
//        return OptionalInt.of(get())
//    }
//
//    fun toRef(): Ref<Int> {
//        return get().ref()
//    }
//
//    companion object {
//
//        @JvmName("of")
//        @JvmStatic
//        fun Int.intRef(): IntRef {
//            return IntRefImpl(this)
//        }
//
//        private class IntRefImpl(private var value: Int) : IntRef {
//            override fun get(): Int = value
//
//            override fun set(value: Int) = apply {
//                this.value = value
//            }
//        }
//    }
//}
//
///**
// * Long version of [Ref].
// */
//@ForJava
//interface LongRef {
//
//    fun get(): Long
//
//    fun set(value: Long): LongRef
//
//    fun copy(): LongRef {
//        return get().intRef()
//    }
//
//    fun toOptional(): OptionalLong {
//        return OptionalLong.of(get())
//    }
//
//    fun toRef(): Ref<Long> {
//        return get().ref()
//    }
//
//    companion object {
//
//        @JvmName("of")
//        @JvmStatic
//        fun Long.intRef(): LongRef {
//            return LongRefImpl(this)
//        }
//
//        private class LongRefImpl(private var value: Long) : LongRef {
//            override fun get(): Long = value
//
//            override fun set(value: Long) = apply {
//                this.value = value
//            }
//        }
//    }
//}
//
///**
// * Float version of [Ref].
// */
//@ForJava
//interface FloatRef {
//
//    fun get(): Float
//
//    fun set(value: Float): FloatRef
//
//    fun copy(): FloatRef {
//        return get().intRef()
//    }
//
//    fun toOptional(): OptionalDouble {
//        return OptionalDouble.of(get().toDouble())
//    }
//
//    fun toRef(): Ref<Float> {
//        return get().ref()
//    }
//
//    companion object {
//
//        @JvmName("of")
//        @JvmStatic
//        fun Float.intRef(): FloatRef {
//            return FloatRefImpl(this)
//        }
//
//        private class FloatRefImpl(private var value: Float) : FloatRef {
//            override fun get(): Float = value
//
//            override fun set(value: Float) = apply {
//                this.value = value
//            }
//        }
//    }
//}
//
///**
// * Double version of [Ref].
// */
//@ForJava
//interface DoubleRef {
//
//    fun get(): Double
//
//    fun set(value: Double): DoubleRef
//
//    fun copy(): DoubleRef {
//        return get().intRef()
//    }
//
//    fun toOptional(): OptionalDouble {
//        return OptionalDouble.of(get())
//    }
//
//    fun toRef(): Ref<Double> {
//        return get().ref()
//    }
//
//    companion object {
//
//        @JvmName("of")
//        @JvmStatic
//        fun Double.intRef(): DoubleRef {
//            return DoubleRefImpl(this)
//        }
//
//        private class DoubleRefImpl(private var value: Double) : DoubleRef {
//            override fun get(): Double = value
//
//            override fun set(value: Double) = apply {
//                this.value = value
//            }
//        }
//    }
//}