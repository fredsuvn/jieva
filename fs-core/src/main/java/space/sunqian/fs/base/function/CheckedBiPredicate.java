// package space.sunqian.fs.base.function;
//
// import java.util.function.BiPredicate;
//
// /**
//  * Represents a predicate (boolean-valued function) of two arguments.
//  * <p>
//  * This is the sub-interface and checked exception version of {@link BiPredicate} whose functional method is
//  * {@link #testChecked(Object, Object)}. The default implementation of {@link #test(Object, Object)} is call
//  * {@link #testChecked(Object, Object)} and wrap any exception thrown during the execution by
//  * {@link FunctionalException}.
//  *
//  * @param <T> the type of the first input to the predicate
//  * @param <U> the type of the second input to the predicate
//  */
// @FunctionalInterface
// public interface CheckedBiPredicate<T, U> extends BiPredicate<T, U> {
//
//     /**
//      * Evaluates this predicate on the given arguments. This is the checked version of {@link #test(Object, Object)}.
//      *
//      * @param t the first input argument
//      * @param u the second input argument
//      * @return the predicate result
//      * @throws Exception any exception that may be thrown by the execution
//      */
//     boolean testChecked(T t, U u) throws Exception;
//
//     @Override
//     default boolean test(T t, U u) {
//         try {
//             return testChecked(t, u);
//         } catch (Exception e) {
//             throw new FunctionalException(e);
//         }
//     }
// }
