//package xyz.srclab.common.collection;
//
//import xyz.srclab.annotation.Nullable;
//import xyz.srclab.common.base.Require;
//
//import java.util.function.BiFunction;
//import java.util.function.BinaryOperator;
//import java.util.function.Function;
//import java.util.function.Predicate;
//
///**
// * List operations.
// *
// * @author sunqian
// */
//public interface ListOps<E> extends BaseIterableOps<E, ListOps<E>> {
//
//    ListOps<E> filter(Predicate<@Nullable ? super E> predicate);
//
//    <R> ListOps<R> map(Function<@Nullable ? super E, @Nullable ? extends R> mapper);
//
//    @Nullable
//    default E reduce(BinaryOperator<@Nullable E> accumulator) {
//        return reduce(null, accumulator);
//    }
//
//    @Nullable
//    public E reduce(@Nullable E identity, BinaryOperator<@Nullable E> accumulator);
//
//    @Nullable
//    public <R> R reduce(
//            @Nullable R identity,
//            BiFunction<@Nullable R, @Nullable ? super E, @Nullable R> accumulator,
//            BinaryOperator<@Nullable R> combiner
//    );
//
//    default E reduceNonNull(BinaryOperator<@Nullable E> accumulator) {
//        return Require.nonNull(reduce(accumulator));
//    }
//
//    default E reduceNonNull(@Nullable E identity, BinaryOperator<@Nullable E> accumulator) {
//        return Require.nonNull(reduce(identity, accumulator));
//    }
//
//    default <R> R reduceNonNull(
//            @Nullable R identity,
//            BiFunction<@Nullable R, @Nullable ? super E, @Nullable R> accumulator,
//            BinaryOperator<@Nullable R> combiner
//    ) {
//        return Require.nonNull(reduce(identity, accumulator, combiner));
//    }
//
//    <R> ListOps<R> flat(Function<@Nullable ? super E, ? extends Iterable<@Nullable ? extends R>> mapper);
//}
