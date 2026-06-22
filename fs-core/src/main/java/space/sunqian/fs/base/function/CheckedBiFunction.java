// package space.sunqian.fs.base.function;
//
// import java.util.function.BiFunction;
//
// /**
//  * Represents a function that accepts two arguments and produces a result.
//  * <p>
//  * This is the sub-interface and checked exception version of {@link BiFunction} whose functional method is
//  * {@link #applyChecked(Object, Object)}. The default implementation of {@link #apply(Object, Object)} is call
//  * {@link #applyChecked(Object, Object)} and wrap any exception thrown during the execution by
//  * {@link FunctionalException}.
//  *
//  * @param <T> the type of the first input to the function
//  * @param <U> the type of the second input to the function
//  * @param <R> the type of the result of the function
//  */
// @FunctionalInterface
// public interface CheckedBiFunction<T, U, R> extends BiFunction<T, U, R> {
//
//     /**
//      * Applies this function to the given arguments. This is the checked version of {@link #apply(Object, Object)}.
//      *
//      * @param t the first input argument
//      * @param u the second input argument
//      * @return the function result
//      * @throws Exception any exception that may be thrown by the execution
//      */
//     R applyChecked(T t, U u) throws Exception;
//
//     @Override
//     default R apply(T t, U u) {
//         try {
//             return applyChecked(t, u);
//         } catch (Exception e) {
//             throw new FunctionalException(e);
//         }
//     }
// }
