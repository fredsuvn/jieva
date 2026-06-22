// package space.sunqian.fs.base.function;
//
// import java.util.function.Predicate;
//
// /**
//  * Represents a predicate (boolean-valued function) of one argument.
//  * <p>
//  * This is the sub-interface and checked exception version of {@link Predicate} whose functional method is
//  * {@link #testChecked(Object)}. The default implementation of {@link #test(Object)} is call
//  * {@link #testChecked(Object)} and wrap any exception thrown during the execution by {@link FunctionalException}.
//  *
//  * @param <T> the type of the input to the predicate
//  */
// @FunctionalInterface
// public interface CheckedPredicate<T> extends Predicate<T> {
//
//     /**
//      * Evaluates this predicate on the given argument. This is the checked version of {@link #test(Object)}.
//      *
//      * @param t the predicate argument
//      * @return the predicate result
//      * @throws Exception any exception that may be thrown by the execution
//      */
//     boolean testChecked(T t) throws Exception;
//
//     @Override
//     default boolean test(T t) {
//         try {
//             return testChecked(t);
//         } catch (Exception e) {
//             throw new FunctionalException(e);
//         }
//     }
// }
