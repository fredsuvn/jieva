// package space.sunqian.fs.base.function;
//
// import java.util.function.Function;
//
// /**
//  * Represents a function that accepts one argument and produces a result.
//  * <p>
//  * This is the sub-interface and checked exception version of {@link Function} whose functional method is
//  * {@link #applyChecked(Object)}. The default implementation of {@link #apply(Object)} is call
//  * {@link #applyChecked(Object)} and wrap any exception thrown during the execution by {@link FunctionalException}.
//  *
//  * @param <T> the type of the input to the function
//  * @param <R> the type of the result of the function
//  */
// @FunctionalInterface
// public interface CheckedFunction<T, R> extends Function<T, R> {
//
//     /**
//      * Applies this function to the given argument. This is the checked version of {@link #apply(Object)}.
//      *
//      * @param t the function argument
//      * @return the function result
//      * @throws Exception any exception that may be thrown by the execution
//      */
//     R applyChecked(T t) throws Exception;
//
//     @Override
//     default R apply(T t) {
//         try {
//             return applyChecked(t);
//         } catch (Exception e) {
//             throw new FunctionalException(e);
//         }
//     }
// }
