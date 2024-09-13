// package test.proxy;
//
// import xyz.fslabo.common.proxy.ProxiedInvoker;
// import xyz.fslabo.common.proxy.ProxyInvoker;
//
// import java.lang.reflect.Method;
//
// public class TestA extends TestS {
//
//     private final ProxiedInvoker[] proxiedInvokers;
//     private final ProxyInvoker[] invokers;
//     private final Method[] methods;
//
//     public TestA(ProxyInvoker[] invokers, Method[] methods) {
//         this.proxiedInvokers = new ProxiedInvoker[3];
//         for (int i = 0; i < proxiedInvokers.length; i++) {
//             proxiedInvokers[i] = new ProxiedImpl(i);
//         }
//         this.invokers = invokers;
//         this.methods = methods;
//     }
//
//     public void m1(Object a, Object b) {
//         try {
//             invokers[0].invoke(this, methods[0], proxiedInvokers[0], a, b);
//         } catch (Throwable e) {
//             throw new RuntimeException(e);
//         }
//     }
//
//     public int m2() {
//         try {
//             return (Integer) invokers[1].invoke(this, methods[1], proxiedInvokers[1]);
//         } catch (Throwable e) {
//             throw new RuntimeException(e);
//         }
//     }
//
//     public String m3(int a) {
//         try {
//             return (String) invokers[2].invoke(this, methods[2], proxiedInvokers[2], a);
//         } catch (Throwable e) {
//             throw new RuntimeException(e);
//         }
//     }
//
//     public Object callSuper(int i, Object a, Object[] args) {
//         switch (i) {
//             case 0:
//                 super.m1(args[0], args[1]);
//                 return null;
//             case 1:
//                 return super.m2();
//             case 2:
//                 return super.m3((Integer) args[0]);
//         }
//         return null;
//     }
//
//     private class ProxiedImpl implements ProxiedInvoker {
//         private final int i;
//
//         private ProxiedImpl(int i) {
//             this.i = i;
//         }
//
//         public Object invoke(Object a, Object[] args) {
//             return callSuper(i, a, args);
//         }
//     }
// }
