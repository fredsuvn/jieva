package xyz.fslabo.common.proxy;

import org.objectweb.asm.*;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.coll.JieColl;
import xyz.fslabo.common.reflect.JieJvm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <a href="https://asm.ow2.io/">ASM</a> implementation for {@link ProxyProvider}. The runtime environment must have
 * asm classed such as {@code org.objectweb.asm.ClassWriter}.
 * <p>
 * Note this provider doesn't support custom class loader.
 *
 * @author fredsuvn
 */
public class AsmProxyProvider implements ProxyProvider, Opcodes {

    private static final AtomicLong counter = new AtomicLong();

    private static final String PROXY_INVOKER_INTERNAL_NAME = JieJvm.getInternalName(ProxyInvoker.class);
    private static final String PROXIED_INVOKER_INTERNAL_NAME = JieJvm.getInternalName(ProxiedInvoker.class);
    private static final String PROXY_INVOKER_ARRAY_DESCRIPTOR = JieJvm.getDescriptor(ProxyInvoker[].class);
    private static final String METHOD_ARRAY_DESCRIPTOR = JieJvm.getDescriptor(Method[].class);
    private static final String PROXY_INVOKER_ARRAY_INTERNAL = JieJvm.getInternalName(ProxyInvoker[].class);
    private static final String METHOD_ARRAY_INTERNAL = JieJvm.getInternalName(Method[].class);
    private static final String PROXIED_INVOKER_ARRAY_DESCRIPTOR = JieJvm.getDescriptor(ProxiedInvoker[].class);

    private static final String PROXY_INVOKER_DESCRIPTOR = JieJvm.getDescriptor(ProxyInvoker.class);
    private static final String OBJECT_INTERNAL_NAME = JieJvm.getInternalName(Object.class);
    private static final String INVOKE_METHOD_DESCRIPTOR;
    private static final String INVOKER_SUPER_METHOD_DESCRIPTOR;

    static {
        try {
            INVOKE_METHOD_DESCRIPTOR = JieJvm.getDescriptor(ProxyInvoker.class.getMethod(
                "invoke", Object.class, Method.class, ProxiedInvoker.class, Object[].class));
            INVOKER_SUPER_METHOD_DESCRIPTOR = JieJvm.getDescriptor(ProxiedInvoker.class.getMethod(
                "invoke", Object.class, Object[].class));
        } catch (NoSuchMethodException e) {
            throw new ProxyException(e);
        }
    }

    private static final AsmClassLoader asmClassLoader = new AsmClassLoader();

    @Override
    public <T> T newProxyInstance(
        @Nullable ClassLoader loader,
        Iterable<Class<?>> uppers,
        MethodProxyHandler handler
    ) {
        if (JieColl.isEmpty(uppers)) {
            throw new ProxyException("No super class or interface to proxy.");
        }
        String superInternalName = JieJvm.getInternalName(Object.class);
        List<String> interfaceInternalNames = null;
        int i = 0;
        for (Class<?> upper : uppers) {
            if (i == 0) {
                if (!upper.isInterface()) {
                    superInternalName = JieJvm.getInternalName(upper);
                    continue;
                }
            }
            if (interfaceInternalNames == null) {
                interfaceInternalNames = new LinkedList<>();
            }
            interfaceInternalNames.add(JieJvm.getInternalName(upper));
            i++;
        }

        long count = counter.getAndIncrement();
        String newProxyClassName = getClass().getName() + "$Proxy$" + count;
        String newProxyInternalName = newProxyClassName.replaceAll("\\.", "/");
        String newProxiedInvokerSimpleName = "ProxiedInvoker";
        String newProxiedInvokerName = newProxyClassName + "$" + newProxiedInvokerSimpleName;
        String newProxiedInvokerInternalName = newProxyInternalName + "$" + newProxiedInvokerSimpleName;
        Map<ProxyInvoker, List<Method>> invokerMap = new LinkedHashMap<>();
        for (Class<?> upper : uppers) {
            Method[] methods = upper.getMethods();
            for (Method method : methods) {
                if (Modifier.isFinal(method.getModifiers())) {
                    continue;
                }
                ProxyInvoker invoker = invokerSupplier.apply(method);
                if (invoker == null) {
                    continue;
                }
                List<Method> list = invokerMap.computeIfAbsent(invoker, k -> new LinkedList<>());
                list.add(method);
            }
        }

        byte[] bytecode = generateBytecode(newProxyInternalName, uppers, invokerMap, newProxiedInvokerInternalName, newProxiedInvokerSimpleName);
        Class<?> cls = asmClassLoader.defineClass(newProxyClassName, bytecode);

        // Generate proxied invoker class
        byte[] proxiedInvokerByteCode = generateProxiedInvoker(
            newProxiedInvokerInternalName,
            newProxiedInvokerSimpleName,
            newProxyInternalName,
            "L" + newProxyInternalName.replaceAll("\\.", "/") + ";"
        );
        Class<?> proxiedInvokerCls = asmClassLoader.defineClass(newProxiedInvokerName, proxiedInvokerByteCode);

        try {
            Constructor<?> constructor = cls.getConstructor(ProxyInvoker[].class, Method[].class);
            List<ProxyInvoker> invokers = new LinkedList<>();
            List<Method> methods = new LinkedList<>();
            invokerMap.forEach((invoker, methodList) -> {
                invokers.add(invoker);
                methods.addAll(methodList);
            });
            Object result = constructor.newInstance(
                invokers.toArray(new ProxyInvoker[0]),
                methods.toArray(new Method[0])
            );
            return Jie.as(result);
        } catch (Exception e) {
            throw new ProxyException(e);
        }
    }

    private static final String PROXY_INVOKER_SIMPLE_NAME = ProxyInvoker.class.getSimpleName();
    private static final String CALL_SUPER_METHOD_DESCRIPTOR =
        "(I" + JieJvm.getDescriptor(Object.class) + JieJvm.getDescriptor(Object[].class) + ")"
            + JieJvm.getDescriptor(Object.class);

    private String generateSignature(Iterable<Class<?>> uppers) {
        StringBuilder sb = new StringBuilder();
        for (Class<?> upper : uppers) {
            sb.append(JieJvm.getSignature(upper));
        }
        return sb.toString();
    }

    private static final class ProxyGenerator implements Opcodes {

        // String newInternalName, Iterable<Class<?>> uppers, Map<ProxyInvoker, List<Method>> invokerMap, String newProxiedInvokerInternalName, String proxiedSimpleInternalName

        private static final String HANDLER_DESCRIPTOR = JieJvm.getDescriptor(MethodProxyHandler.class);
        private static final String HANDLER_INTERNAL = JieJvm.getInternalName(MethodProxyHandler.class);
        private static final String INIT_DESCRIPTOR =
            "(" + HANDLER_DESCRIPTOR + METHOD_ARRAY_DESCRIPTOR + ")V";

        private static final String HANDLER_METHOD_DESCRIPTOR;

        static {
            try {
                HANDLER_METHOD_DESCRIPTOR = JieJvm.getDescriptor(
                    MethodProxyHandler.class.getMethod("invoke", Object.class, Method.class, Object[].class, ProxyInvoker.class));
            } catch (NoSuchMethodException e) {
                throw new ProxyException(e);
            }
        }

        private final String internalName;
        private final String signature;
        private final String descriptor;
        private final String superInternalName;
        private final List<String> interfaceInternalNames;
        private final String innerInternalName;
        private final List<Method> methods;

        private ProxyGenerator(
            String internalName,
            String signature,
            String descriptor,
            String superInternalName,
            List<String> interfaceInternalNames,
            String innerInternalName,
            List<Method> methods
        ) {
            this.internalName = internalName;
            this.signature = signature;
            this.descriptor = descriptor;
            this.superInternalName = superInternalName;
            this.interfaceInternalNames = interfaceInternalNames;
            this.innerInternalName = innerInternalName;
            this.methods = methods;
        }

        public byte[] generateBytecode() {
            ClassWriter cw = new ClassWriter(0);

            // Declaring
            cw.visit(
                Opcodes.V1_8,
                Opcodes.ACC_PUBLIC | ACC_SUPER,
                internalName,
                signature,
                superInternalName,
                interfaceInternalNames == null ? null : interfaceInternalNames.toArray(new String[0])
            );
            cw.visitInnerClass(innerInternalName, internalName, PROXY_INVOKER_SIMPLE_NAME, ACC_STATIC | ACC_SYNTHETIC);

            // Generates fields
            // private final MethodProxyHandler handler
            FieldVisitor handlerVisitor = cw.visitField(
                Opcodes.ACC_PRIVATE, "handler", HANDLER_DESCRIPTOR, null, null);
            handlerVisitor.visitEnd();
            // private Method[] methods
            FieldVisitor methodsVisitor = cw.visitField(
                Opcodes.ACC_PRIVATE, "methods", METHOD_ARRAY_DESCRIPTOR, null, null);
            methodsVisitor.visitEnd();
            // private ProxyInvoker[] invokers
            FieldVisitor invokersVisitor = cw.visitField(
                Opcodes.ACC_PRIVATE, "invokers", PROXY_INVOKER_ARRAY_DESCRIPTOR, null, null);
            invokersVisitor.visitEnd();

            // Generates constructor: (MethodProxyHandler handler, Method[] methods)
            MethodVisitor constructor = cw.visitMethod(
                Opcodes.ACC_PUBLIC, "<init>", INIT_DESCRIPTOR, null, null);
            // super();
            constructor.visitVarInsn(Opcodes.ALOAD, 0);
            constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, superInternalName, "<init>", "()V", false);
            // this.handler = handler;
            constructor.visitVarInsn(Opcodes.ALOAD, 0);
            constructor.visitVarInsn(Opcodes.ALOAD, 1);
            constructor.visitFieldInsn(Opcodes.PUTFIELD, internalName, "handler", HANDLER_DESCRIPTOR);
            // this.methods = methods;
            constructor.visitVarInsn(Opcodes.ALOAD, 0);
            constructor.visitVarInsn(Opcodes.ALOAD, 2);
            constructor.visitFieldInsn(Opcodes.PUTFIELD, internalName, "methods", METHOD_ARRAY_DESCRIPTOR);
            // this.invokers = new ProxyInvoker[length];
            constructor.visitVarInsn(ALOAD, 0);
            constructor.visitVarInsn(Opcodes.ALOAD, 2);
            constructor.visitInsn(ARRAYLENGTH);
            constructor.visitTypeInsn(ANEWARRAY, PROXY_INVOKER_INTERNAL_NAME);
            constructor.visitFieldInsn(PUTFIELD, internalName, "invokers", PROXY_INVOKER_ARRAY_DESCRIPTOR);
            // int i = 0
            constructor.visitInsn(ICONST_0);
            constructor.visitVarInsn(ISTORE, 3);
            // for-i
            Label forLabel = new Label();
            constructor.visitLabel(forLabel);
            constructor.visitFrame(Opcodes.F_FULL, 4, new Object[]{internalName, HANDLER_DESCRIPTOR, METHOD_ARRAY_INTERNAL, Opcodes.INTEGER}, 0, new Object[]{});
            constructor.visitVarInsn(ILOAD, 3);
            constructor.visitVarInsn(ALOAD, 0);
            constructor.visitFieldInsn(GETFIELD, internalName, "invokers", PROXY_INVOKER_ARRAY_DESCRIPTOR);
            constructor.visitInsn(ARRAYLENGTH);
            Label breakLabel = new Label();
            // if(i >= invokers.length) break;
            constructor.visitJumpInsn(IF_ICMPGE, breakLabel);
            constructor.visitVarInsn(ALOAD, 0);
            constructor.visitFieldInsn(GETFIELD, internalName, "invokers", PROXY_INVOKER_ARRAY_DESCRIPTOR);
            constructor.visitVarInsn(ILOAD, 3);
            constructor.visitTypeInsn(NEW, innerInternalName);
            constructor.visitInsn(DUP);
            constructor.visitVarInsn(ALOAD, 0);
            constructor.visitVarInsn(ILOAD, 3);
            constructor.visitMethodInsn(INVOKESPECIAL, innerInternalName, "<init>", "(L" + internalName + ";I)V", false);
            constructor.visitInsn(AASTORE);
            constructor.visitIincInsn(3, 1);
            constructor.visitJumpInsn(GOTO, forLabel);
            constructor.visitLabel(breakLabel);
            constructor.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
            constructor.visitInsn(Opcodes.RETURN);
            constructor.visitMaxs(7, 4);
            constructor.visitEnd();

            // Generates override methods
            int methodCount = 0;
            for (Method method : methods) {
                MethodVisitor proxyMethod = cw.visitMethod(
                    Opcodes.ACC_PUBLIC, method.getName(), JieJvm.getDescriptor(method), null, null);
                Parameter[] parameters = method.getParameters();
                int extraLocalIndex = countExtraLocalIndex(parameters);
                // method = this.methods[j]
                proxyMethod.visitVarInsn(Opcodes.ALOAD, 0);
                proxyMethod.visitFieldInsn(Opcodes.GETFIELD, internalName, "methods", METHOD_ARRAY_DESCRIPTOR);
                visitPushNumber(proxyMethod, methodCount);
                proxyMethod.visitInsn(Opcodes.AALOAD);
                proxyMethod.visitVarInsn(Opcodes.ASTORE, extraLocalIndex);
                // Object args = new Object[]{};
                visitPushNumber(proxyMethod, parameters.length);
                proxyMethod.visitTypeInsn(Opcodes.ANEWARRAY, OBJECT_INTERNAL_NAME);
                int paramIndex = 1;
                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    proxyMethod.visitInsn(Opcodes.DUP);
                    visitPushNumber(proxyMethod, i);
                    visitLoadParamAsObject(proxyMethod, parameter.getType(), paramIndex);
                    proxyMethod.visitInsn(Opcodes.AASTORE);
                    if (Objects.equals(parameter.getType(), long.class) || Objects.equals(parameter.getType(), double.class)) {
                        paramIndex += 2;
                    } else {
                        paramIndex++;
                    }
                }
                proxyMethod.visitVarInsn(Opcodes.ASTORE, extraLocalIndex + 1);
                // invoker = this.invokers[i];
                proxyMethod.visitVarInsn(Opcodes.ALOAD, 0);
                proxyMethod.visitFieldInsn(Opcodes.GETFIELD, internalName, "invokers", PROXY_INVOKER_ARRAY_DESCRIPTOR);
                visitPushNumber(proxyMethod, methodCount);
                proxyMethod.visitInsn(Opcodes.AALOAD);
                proxyMethod.visitVarInsn(Opcodes.ASTORE, extraLocalIndex + 2);
                // handler
                proxyMethod.visitVarInsn(Opcodes.ALOAD, 0);
                constructor.visitFieldInsn(Opcodes.GETFIELD, internalName, "handler", HANDLER_DESCRIPTOR);
                // this
                proxyMethod.visitVarInsn(Opcodes.ALOAD, 0);
                // method
                proxyMethod.visitVarInsn(Opcodes.ALOAD, extraLocalIndex);
                // args
                proxyMethod.visitVarInsn(ALOAD, extraLocalIndex + 1);
                // invoker
                proxyMethod.visitVarInsn(ALOAD, extraLocalIndex + 2);
                // handler.invoke(this, method, args, invoker)
                proxyMethod.visitMethodInsn(INVOKEINTERFACE, HANDLER_INTERNAL, "invoke", HANDLER_METHOD_DESCRIPTOR, true);
                if (Objects.equals(method.getReturnType(), void.class)) {
                    proxyMethod.visitInsn(Opcodes.RETURN);
                } else {
                    visitObjectCast(proxyMethod, method.getReturnType(), true);
                }
                proxyMethod.visitMaxs(5, extraLocalIndex + 3);
                proxyMethod.visitEnd();
                methodCount++;
            }

            // Generate callSuper
            List<Method> methods = new LinkedList<>();
            invokerMap.forEach((i, l) -> {
                methods.addAll(l);
            });
            Label[] caseLabels = methods.stream().map(it -> new Label()).toArray(Label[]::new);
            MethodVisitor callSuper = cw.visitMethod(ACC_PUBLIC, "callSuper",
                "(I" + JieJvm.getDescriptor(Object.class) + JieJvm.getDescriptor(Object[].class) + ")" + JieJvm.getDescriptor(Object.class), null, null);
            callSuper.visitVarInsn(ILOAD, 1);
            Label switchLabel = new Label();
            callSuper.visitTableSwitchInsn(0, methods.size() - 1, switchLabel, caseLabels);
            int i = 0;
            int maxSize = 0;
            for (Method method : methods) {
                callSuper.visitLabel(caseLabels[i]);
                callSuper.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                callSuper.visitVarInsn(ALOAD, 2);
                // System.out.println(JieJvm.getInternalName(method.getDeclaringClass()));
                callSuper.visitTypeInsn(Opcodes.CHECKCAST, JieJvm.getInternalName(method.getDeclaringClass()));
                Class<?>[] params = method.getParameterTypes();
                if (maxSize < params.length) {
                    maxSize = params.length;
                }
                for (int j = 0; j < params.length; j++) {
                    callSuper.visitVarInsn(ALOAD, 3);
                    visitPushNumber(callSuper, j);
                    callSuper.visitInsn(Opcodes.AALOAD);
                    visitObjectCast(callSuper, params[j], false);
                }
                callSuper.visitMethodInsn(INVOKEVIRTUAL, JieJvm.getInternalName(method.getDeclaringClass()), method.getName(), JieJvm.getDescriptor(method), method.getDeclaringClass().isInterface());
                if (method.getReturnType().equals(void.class)) {
                    callSuper.visitInsn(ACONST_NULL);
                    callSuper.visitInsn(ARETURN);
                } else {
                    visitCastObject(callSuper, method.getReturnType(), true);
                }
                i++;
            }
            callSuper.visitLabel(switchLabel);
            callSuper.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            callSuper.visitInsn(ACONST_NULL);
            callSuper.visitInsn(ARETURN);
            callSuper.visitMaxs(1 + 3 * maxSize, 4);
            callSuper.visitEnd();

            return cw.toByteArray();
        }


        private void generateProxyMethods(
            ClassWriter cw, String newInternalName, String superInternalName, Map<ProxyInvoker, List<Method>> invokerMap, String proxiedInvokerInternalName) {

        }

    }

    private static final class ProxyInvokerGenerator implements Opcodes {

        private static final String INVOKE_METHOD_DESCRIPTOR;
        private static final String INVOKE_SUPER_METHOD_DESCRIPTOR;

        static {
            try {
                INVOKE_METHOD_DESCRIPTOR = JieJvm.getDescriptor(
                    ProxyInvoker.class.getDeclaredMethod("invoke", Object.class, Object[].class)
                );
                INVOKE_SUPER_METHOD_DESCRIPTOR = JieJvm.getDescriptor(
                    ProxyInvoker.class.getDeclaredMethod("invokeSuper", Object[].class)
                );
            } catch (NoSuchMethodException e) {
                throw new ProxyException(e);
            }
        }

        private final String outInternalName;
        private final String outDescriptor;
        private final String internalName;
        private final String callSuperMethodName;

        private ProxyInvokerGenerator(
            String outInternalName, String outDescriptor, String internalName, String callSuperMethodName) {
            this.outInternalName = outInternalName;
            this.outDescriptor = outDescriptor;
            this.internalName = internalName;
            this.callSuperMethodName = callSuperMethodName;
        }

        public byte[] generateBytecode() {
            ClassWriter cw = new ClassWriter(0);

            // Declaring: Inner class: X implements ProxyInvoker
            cw.visit(V1_8, ACC_SUPER, internalName, null, OBJECT_INTERNAL_NAME, new String[]{PROXIED_INVOKER_INTERNAL_NAME});
            cw.visitInnerClass(internalName, outInternalName, PROXY_INVOKER_SIMPLE_NAME, ACC_STATIC | ACC_SYNTHETIC);

            // Generates fields:
            // private final int i;
            FieldVisitor fv1 = cw.visitField(ACC_PRIVATE | ACC_FINAL, "i", "I", null, null);
            fv1.visitEnd();
            // Out.this
            FieldVisitor fv2 = cw.visitField(ACC_FINAL | ACC_SYNTHETIC, "this$0", outDescriptor, null, null);
            fv2.visitEnd();

            // Generates constructor: (int i)
            MethodVisitor cmv = cw.visitMethod(ACC_SYNTHETIC, "<init>", "(" + outDescriptor + "I)V", null, null);
            // this.this$0 = out;
            cmv.visitVarInsn(ALOAD, 0);
            cmv.visitVarInsn(ALOAD, 1);
            cmv.visitFieldInsn(PUTFIELD, internalName, "this$0", outDescriptor);
            // super();
            cmv.visitVarInsn(ALOAD, 0);
            cmv.visitMethodInsn(INVOKESPECIAL, OBJECT_INTERNAL_NAME, "<init>", "()V", false);
            // this.i = i;
            cmv.visitVarInsn(ALOAD, 0);
            cmv.visitVarInsn(ILOAD, 2);
            cmv.visitFieldInsn(PUTFIELD, internalName, "i", "I");
            cmv.visitInsn(RETURN);
            cmv.visitMaxs(2, 3);
            cmv.visitEnd();

            // Generates methods:
            // invoke(Object inst, Object[] args): return callSuper(i, inst, args);
            MethodVisitor invoke = cw.visitMethod(ACC_PUBLIC, "invoke", INVOKE_METHOD_DESCRIPTOR, null, null);
            invoke.visitVarInsn(ALOAD, 0);
            invoke.visitFieldInsn(GETFIELD, internalName, "this$0", outDescriptor);
            invoke.visitVarInsn(ALOAD, 0);
            invoke.visitFieldInsn(GETFIELD, internalName, "i", "I");
            invoke.visitVarInsn(ALOAD, 1);
            invoke.visitVarInsn(ALOAD, 2);
            invoke.visitMethodInsn(INVOKEVIRTUAL, outInternalName, callSuperMethodName, CALL_SUPER_METHOD_DESCRIPTOR, false);
            invoke.visitInsn(ARETURN);
            invoke.visitMaxs(4, 3);
            invoke.visitEnd();
            // invokeSuper(Object[] args): return callSuper(i, this$0, args);
            MethodVisitor invokeSuper = cw.visitMethod(ACC_PUBLIC, "invokeSuper", INVOKE_SUPER_METHOD_DESCRIPTOR, null, null);
            invokeSuper.visitVarInsn(ALOAD, 0);
            invokeSuper.visitFieldInsn(GETFIELD, internalName, "this$0", outDescriptor);
            invokeSuper.visitInsn(DUP);
            invokeSuper.visitVarInsn(Opcodes.ASTORE, 2);
            invokeSuper.visitVarInsn(ALOAD, 0);
            invokeSuper.visitFieldInsn(GETFIELD, internalName, "i", "I");
            invokeSuper.visitVarInsn(ALOAD, 2);
            invokeSuper.visitVarInsn(ALOAD, 1);
            invokeSuper.visitMethodInsn(INVOKEVIRTUAL, outInternalName, callSuperMethodName, CALL_SUPER_METHOD_DESCRIPTOR, false);
            invokeSuper.visitInsn(ARETURN);
            invokeSuper.visitMaxs(4, 3);
            invokeSuper.visitEnd();

            return cw.toByteArray();
        }
    }

    private static void visitLoadParamAsObject(MethodVisitor visitor, Class<?> type, int i) {
        if (!type.isPrimitive()) {
            visitor.visitVarInsn(Opcodes.ALOAD, i);
            return;
        }
        if (Objects.equals(type, boolean.class)) {
            visitor.visitVarInsn(Opcodes.ILOAD, i);
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
            return;
        }
        if (Objects.equals(type, byte.class)) {
            visitor.visitVarInsn(Opcodes.ILOAD, i);
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
            return;
        }
        if (Objects.equals(type, short.class)) {
            visitor.visitVarInsn(Opcodes.ILOAD, i);
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
            return;
        }
        if (Objects.equals(type, char.class)) {
            visitor.visitVarInsn(Opcodes.ILOAD, i);
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
            return;
        }
        if (Objects.equals(type, int.class)) {
            visitor.visitVarInsn(Opcodes.ILOAD, i);
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            return;
        }
        if (Objects.equals(type, long.class)) {
            visitor.visitVarInsn(Opcodes.LLOAD, i);
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
            return;
        }
        if (Objects.equals(type, float.class)) {
            visitor.visitVarInsn(Opcodes.FLOAD, i);
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
            return;
        }
        if (Objects.equals(type, double.class)) {
            visitor.visitVarInsn(Opcodes.DLOAD, i);
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
            return;
        }
    }

    private static void visitObjectCast(MethodVisitor visitor, Class<?> type, boolean needReturn) {
        if (!type.isPrimitive()) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, JieJvm.getInternalName(type));
            if (needReturn) {
                visitor.visitInsn(Opcodes.ARETURN);
            }
            return;
        }
        if (Objects.equals(type, boolean.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Boolean");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
            if (needReturn) {
                visitor.visitInsn(Opcodes.IRETURN);
            }
            return;
        }
        if (Objects.equals(type, byte.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Byte");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
            if (needReturn) {
                visitor.visitInsn(Opcodes.IRETURN);
            }
            return;
        }
        if (Objects.equals(type, short.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Short");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
            if (needReturn) {
                visitor.visitInsn(Opcodes.IRETURN);
            }
            return;
        }
        if (Objects.equals(type, char.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Character");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
            if (needReturn) {
                visitor.visitInsn(Opcodes.IRETURN);
            }
            return;
        }
        if (Objects.equals(type, int.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Integer");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            if (needReturn) {
                visitor.visitInsn(Opcodes.IRETURN);
            }
            return;
        }
        if (Objects.equals(type, long.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Long");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
            if (needReturn) {
                visitor.visitInsn(Opcodes.LRETURN);
            }
            return;
        }
        if (Objects.equals(type, float.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Float");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
            if (needReturn) {
                visitor.visitInsn(Opcodes.FRETURN);
            }
            return;
        }
        if (Objects.equals(type, double.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Double");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
            if (needReturn) {
                visitor.visitInsn(Opcodes.DRETURN);
            }
            return;
        }
    }

    private static void visitCastObject(MethodVisitor visitor, Class<?> type, boolean needReturn) {
        if (!type.isPrimitive()) {
            if (needReturn) {
                visitor.visitInsn(Opcodes.ARETURN);
            }
            return;
        }
        if (Objects.equals(type, boolean.class)) {
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
        } else if (Objects.equals(type, byte.class)) {
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
        } else if (Objects.equals(type, short.class)) {
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
        } else if (Objects.equals(type, char.class)) {
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
        } else if (Objects.equals(type, int.class)) {
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        } else if (Objects.equals(type, long.class)) {
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
        } else if (Objects.equals(type, float.class)) {
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
        } else if (Objects.equals(type, double.class)) {
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
        }
        if (needReturn) {
            visitor.visitInsn(Opcodes.ARETURN);
        }
    }

    private static void visitPushNumber(MethodVisitor visitor, int i) {
        switch (i) {
            case 0:
                visitor.visitInsn(Opcodes.ICONST_0);
                return;
            case 1:
                visitor.visitInsn(Opcodes.ICONST_1);
                return;
            case 2:
                visitor.visitInsn(Opcodes.ICONST_2);
                return;
            case 3:
                visitor.visitInsn(Opcodes.ICONST_3);
                return;
            case 4:
                visitor.visitInsn(Opcodes.ICONST_4);
                return;
            case 5:
                visitor.visitInsn(Opcodes.ICONST_5);
                return;
            default:
                // -127-128
                visitor.visitIntInsn(Opcodes.BIPUSH, i);
        }
    }

    private static int countExtraLocalIndex(Parameter[] parameters) {
        int i = 1;
        for (Parameter parameter : parameters) {
            if (Objects.equals(parameter.getType(), long.class) || Objects.equals(parameter.getType(), double.class)) {
                i += 2;
            } else {
                i++;
            }
        }
        return i;
    }

    private static final class AsmClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] bytecode) {
            return defineClass(name, bytecode, 0, bytecode.length);
        }
    }
}
