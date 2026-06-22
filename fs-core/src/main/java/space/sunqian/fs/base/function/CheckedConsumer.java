// package space.sunqian.fs.base.function;
//
// import java.util.function.Consumer;
//
// /**
//  * Represents an operation that accepts a single input argument and returns no result.
//  * <p>
//  * This is the sub-interface and checked exception version of {@link Consumer} whose functional method is
//  * {@link #acceptChecked(Object)}. The default implementation of {@link #accept(Object)} is call
//  * {@link #acceptChecked(Object)} and wrap any exception thrown during the execution by {@link FunctionalException}.
//  *
//  * @param <T> the type of the input to the operation
//  */
// @FunctionalInterface
// public interface CheckedConsumer<T> extends Consumer<T> {
//
//     /**
//      * Performs this operation on the given argument. This is the checked version of {@link #accept(Object)}.
//      *
//      * @param t the input argument
//      * @throws Exception any exception that may be thrown by the execution
//      */
//     void acceptChecked(T t) throws Exception;
//
//     @Override
//     default void accept(T t) {
//         try {
//             acceptChecked(t);
//         } catch (Exception e) {
//             throw new FunctionalException(e);
//         }
//     }
// }
