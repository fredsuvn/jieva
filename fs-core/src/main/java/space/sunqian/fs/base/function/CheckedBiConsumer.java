// package space.sunqian.fs.base.function;
//
// import java.util.function.BiConsumer;
//
// /**
//  * Represents an operation that accepts two input arguments and returns no result.
//  * <p>
//  * This is the sub-interface and checked exception version of {@link BiConsumer} whose functional method is
//  * {@link #acceptChecked(Object, Object)}. The default implementation of {@link #accept(Object, Object)} is call
//  * {@link #acceptChecked(Object, Object)} and wrap any exception thrown during the execution by
//  * {@link FunctionalException}.
//  *
//  * @param <T> the type of the first input to the operation
//  * @param <U> the type of the second input to the operation
//  */
// @FunctionalInterface
// public interface CheckedBiConsumer<T, U> extends BiConsumer<T, U> {
//
//     /**
//      * Performs this operation on the given arguments. This is the checked version of {@link #accept(Object, Object)}.
//      *
//      * @param t the first input argument
//      * @param u the second input argument
//      * @throws Exception any exception that may be thrown by the execution
//      */
//     void acceptChecked(T t, U u) throws Exception;
//
//     @Override
//     default void accept(T t, U u) {
//         try {
//             acceptChecked(t, u);
//         } catch (Exception e) {
//             throw new FunctionalException(e);
//         }
//     }
// }
