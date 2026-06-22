// package space.sunqian.fs.base.function;
//
// import java.util.function.Supplier;
//
// /**
//  * Represents a supplier of results.
//  * <p>
//  * This is the sub-interface and checked exception version of {@link Supplier} whose functional method is
//  * {@link #getChecked()}. The default implementation of {@link #get()} is call {@link #getChecked()} and wrap any
//  * exception thrown during the execution by {@link FunctionalException}.
//  *
//  * @param <T> the type of results supplied by this supplier
//  */
// @FunctionalInterface
// public interface CheckedSupplier<T> extends Supplier<T> {
//
//     /**
//      * Gets a result. This is the checked version of {@link #get()}.
//      *
//      * @return a result
//      * @throws Exception any exception that may be thrown by the execution
//      */
//     T getChecked() throws Exception;
//
//     @Override
//     default T get() {
//         try {
//             return getChecked();
//         } catch (Exception e) {
//             throw new FunctionalException(e);
//         }
//     }
// }
