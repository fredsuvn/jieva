//package xyz.srclab.common.proxy;
//
//import xyz.srclab.common.reflect.MethodBody;
//import xyz.srclab.common.tuple.Pair;
//
//import java.lang.reflect.Method;
//import java.util.Arrays;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.function.Predicate;
//
//public class ClassProxyBuilder<T> {
//
//    public static <T> ClassProxyBuilder<T> newBuilder(Class<T> type) {
//        return new ClassProxyBuilder<>(type);
//    }
//
//    private final Class<T> type;
//    private final List<Pair<Predicate<Method>, MethodBody<?>>> predicatePairs = new LinkedList<>();
//
//    public ClassProxyBuilder(Class<T> type) {
//        this.type = type;
//    }
//
//    public ClassProxyBuilder<T> proxyMethod(
//            String methodName, Class<?>[] parameterTypes, MethodBody<?> methodBody) {
//        return proxyMethod(
//                method -> method.getName().equals(methodName)
//                        && Arrays.equals(method.getParameterTypes(), parameterTypes),
//                methodBody
//        );
//    }
//
//    public ClassProxyBuilder<T> proxyMethod(
//            Predicate<Method> methodPredicate, MethodBody<?> methodBody) {
//        predicatePairs.add(Pair.of(methodPredicate, methodBody));
//        return this;
//    }
//
//    public ClassProxy<T> build() {
//        ClassConstructor<T> classConstructor = buildConstructor();
//        return new ClassProxy<T>() {
//            @Override
//            public T newInstance() {
//                return classConstructor.newInstance();
//            }
//
//            @Override
//            public T newInstance(Class<?>[] parameterTypes, Object[] arguments) {
//                return classConstructor.newInstance(parameterTypes, arguments);
//            }
//        };
//    }
//
//    private ClassConstructor<T> buildConstructor() {
//        ClassBuilder<T> classBuilder = ClassBuilder.newBuilder(type);
//        for (Pair<Predicate<Method>, MethodBody<?>> predicatePair : predicatePairs) {
//
//        }
//    }
//
//    static class Aa{
//        protected void d() {
//
//        }
//    }
//
//    static class Bb extends Aa{
//    }
//
//    static class Cc extends Bb{
//        protected void d() {
//            super.d();
//        }
//    }
//}
