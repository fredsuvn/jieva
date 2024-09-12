// package xyz.fslabo.common.proxy;
//
// import xyz.fslabo.annotations.Nullable;
// import xyz.fslabo.common.coll.JieColl;
//
// import java.lang.reflect.InvocationHandler;
// import java.lang.reflect.Method;
// import java.lang.reflect.Modifier;
// import java.lang.reflect.Proxy;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.function.Function;
//
// /**
//  * JDK implementation for {@link ProxyProvider}, based on {@link Proxy}, only supports interface proxy.
//  *
//  * @author fredsuvn
//  */
// public class JdkProxyProvider implements ProxyProvider {
//     @Override
//     public <T> T newProxyInstance(
//         @Nullable ClassLoader loader,
//         Iterable<Class<?>> uppers,
//         Function<Method, @Nullable ProxyInvoker> invokerSupplier
//     ) {
//         Map<Method, ProxyInvoker> invokers = new HashMap<>();
//         for (Class<?> upper : uppers) {
//             Method[] methods = upper.getMethods();
//             for (Method method : methods) {
//                 if (Modifier.isFinal(method.getModifiers())) {
//                     ProxyInvoker invoker = invokerSupplier.apply(method);
//                     if (invoker != null) {
//                         invokers.put(method, invoker);
//                     }
//                 }
//             }
//         }
//         Object inst = Proxy.newProxyInstance(loader, JieColl.toArray(uppers, Class.class), new InvocationHandler() {
//             @Override
//             public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                 ProxyInvoker invoker = invokers.get(method);
//                 if (invoker == null) {
//                     throw new
//                 }
//                 return null;
//             }
//         });
//     }
// }
