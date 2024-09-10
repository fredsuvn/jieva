package xyz.fslabo.common.proxy;

import org.objectweb.asm.*;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.coll.JieColl;
import xyz.fslabo.common.reflect.JieJvm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

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
    private static final String INIT_DESCRIPTOR = "(" + PROXY_INVOKER_ARRAY_DESCRIPTOR + METHOD_ARRAY_DESCRIPTOR + ")V";
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
        Function<Method, @Nullable ProxyInvoker> invokerSupplier
    ) {
        if (JieColl.isEmpty(uppers)) {
            throw new ProxyException("No super class or interface to proxy.");
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

    private byte[] generateBytecode(
        String newInternalName, Iterable<Class<?>> uppers, Map<ProxyInvoker, List<Method>> invokerMap, String newProxiedInvokerInternalName, String proxiedSimpleInternalName) {
        ClassWriter cw = new ClassWriter(0);
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
        cw.visit(
            Opcodes.V1_8,
            Opcodes.ACC_PUBLIC | ACC_SUPER,
            newInternalName,
            generateSignature(uppers),
            superInternalName,
            interfaceInternalNames == null ? null : interfaceInternalNames.toArray(new String[0])
        );
        cw.visitInnerClass(newInternalName + "$1", null, null, ACC_STATIC | ACC_SYNTHETIC);
        cw.visitInnerClass(newProxiedInvokerInternalName, newInternalName, proxiedSimpleInternalName, ACC_PRIVATE);
        generateProxyMethods(cw, newInternalName, superInternalName, invokerMap, newProxiedInvokerInternalName);
        return cw.toByteArray();
    }

    private String generateSignature(Iterable<Class<?>> uppers) {
        StringBuilder sb = new StringBuilder();
        for (Class<?> upper : uppers) {
            sb.append(JieJvm.getSignature(upper));
        }
        return sb.toString();
    }

    private void generateProxyMethods(
        ClassWriter cw, String newInternalName, String superInternalName, Map<ProxyInvoker, List<Method>> invokerMap, String proxiedInvokerInternalName) {
        // Generates fields
        // private ProxyInvoker[] invokers
        FieldVisitor invokersVisitor = cw.visitField(
            Opcodes.ACC_PRIVATE, "invokers", PROXY_INVOKER_ARRAY_DESCRIPTOR, null, null);
        invokersVisitor.visitEnd();
        // private Method[] methods
        FieldVisitor methodsVisitor = cw.visitField(
            Opcodes.ACC_PRIVATE, "methods", METHOD_ARRAY_DESCRIPTOR, null, null);
        methodsVisitor.visitEnd();
        // private ProxiedInvoker[] proxiedInvokers
        FieldVisitor proxiedInvokersVisitor = cw.visitField(
            Opcodes.ACC_PRIVATE, "proxiedInvokers", PROXIED_INVOKER_ARRAY_DESCRIPTOR, null, null);
        proxiedInvokersVisitor.visitEnd();

        // Generates constructor: (ProxyInvoker[] invokers, Method[] methods)
        MethodVisitor constructor = cw.visitMethod(
            Opcodes.ACC_PUBLIC, "<init>", INIT_DESCRIPTOR, null, null);
        constructor.visitVarInsn(Opcodes.ALOAD, 0);
        constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, superInternalName, "<init>", "()V", false);
        constructor.visitVarInsn(Opcodes.ALOAD, 0);
        constructor.visitVarInsn(Opcodes.ALOAD, 1);
        constructor.visitFieldInsn(Opcodes.PUTFIELD, newInternalName, "invokers", PROXY_INVOKER_ARRAY_DESCRIPTOR);
        constructor.visitVarInsn(Opcodes.ALOAD, 0);
        constructor.visitVarInsn(Opcodes.ALOAD, 2);
        constructor.visitFieldInsn(Opcodes.PUTFIELD, newInternalName, "methods", METHOD_ARRAY_DESCRIPTOR);
        // visitPushNumber(proxyMethod, paramsCount);
        // proxyMethod.visitTypeInsn(Opcodes.ANEWARRAY, OBJECT_INTERNAL_NAME);
        // constructor.visitVarInsn(Opcodes.ALOAD, 0);
        // constructor.visitVarInsn(Opcodes.ALOAD, 2);
        // constructor.visitFieldInsn(Opcodes.PUTFIELD, newInternalName, "methods", METHOD_ARRAY_DESCRIPTOR);
        // this.proxiedInvokers = new ProxiedInvoker[10];
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitIntInsn(BIPUSH, 10);
        constructor.visitTypeInsn(ANEWARRAY, PROXIED_INVOKER_INTERNAL_NAME);
        constructor.visitFieldInsn(PUTFIELD, newInternalName, "proxiedInvokers", PROXIED_INVOKER_ARRAY_DESCRIPTOR);
        // int i = 0
        constructor.visitInsn(ICONST_0);
        constructor.visitVarInsn(ISTORE, 3);
        Label forLabel = new Label();
        constructor.visitLabel(forLabel);
        constructor.visitFrame(Opcodes.F_FULL, 4, new Object[]{newInternalName, PROXY_INVOKER_ARRAY_INTERNAL, METHOD_ARRAY_INTERNAL, Opcodes.INTEGER}, 0, new Object[]{});
        constructor.visitVarInsn(ILOAD, 3);
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitFieldInsn(GETFIELD, newInternalName, "proxiedInvokers", PROXIED_INVOKER_ARRAY_DESCRIPTOR);
        constructor.visitInsn(ARRAYLENGTH);
        Label breakLabel = new Label();
        constructor.visitJumpInsn(IF_ICMPGE, breakLabel);
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitFieldInsn(GETFIELD, newInternalName, "proxiedInvokers", PROXIED_INVOKER_ARRAY_DESCRIPTOR);
        constructor.visitVarInsn(ILOAD, 3);
        constructor.visitTypeInsn(NEW, proxiedInvokerInternalName);
        constructor.visitInsn(DUP);
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitVarInsn(ILOAD, 3);
        constructor.visitInsn(ACONST_NULL);
        constructor.visitMethodInsn(INVOKESPECIAL, proxiedInvokerInternalName, "<init>", "(L" + newInternalName + ";IL" + newInternalName + "$1;)V", false);
        constructor.visitInsn(AASTORE);
        constructor.visitIincInsn(3, 1);
        constructor.visitJumpInsn(GOTO, forLabel);
        constructor.visitLabel(breakLabel);
        constructor.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
        constructor.visitInsn(Opcodes.RETURN);
        constructor.visitMaxs(7, 4);
        constructor.visitEnd();

        // Generates methods
        int[] invokerCount = {0};
        int[] methodCount = {0};
        invokerMap.forEach((invoker, methods) -> {
            for (Method method : methods) {
                int paramsCount = method.getParameterCount();
                Class<?>[] parameters = method.getParameterTypes();
                // invoker = this.invokers[i];
                MethodVisitor proxyMethod = cw.visitMethod(
                    Opcodes.ACC_PUBLIC, method.getName(), JieJvm.getDescriptor(method), null, null);
                proxyMethod.visitVarInsn(Opcodes.ALOAD, 0);
                proxyMethod.visitFieldInsn(Opcodes.GETFIELD, newInternalName, "invokers", PROXY_INVOKER_ARRAY_DESCRIPTOR);
                proxyMethod.visitIntInsn(Opcodes.BIPUSH, invokerCount[0]);
                proxyMethod.visitInsn(Opcodes.AALOAD);
                proxyMethod.visitVarInsn(Opcodes.ASTORE, paramsCount + 1);
                // method = this.methods[j]
                proxyMethod.visitVarInsn(Opcodes.ALOAD, 0);
                proxyMethod.visitFieldInsn(Opcodes.GETFIELD, newInternalName, "methods", METHOD_ARRAY_DESCRIPTOR);
                proxyMethod.visitIntInsn(Opcodes.BIPUSH, methodCount[0]);
                proxyMethod.visitInsn(Opcodes.AALOAD);
                proxyMethod.visitVarInsn(Opcodes.ASTORE, paramsCount + 2);
                // Object args = new Object[]{};
                visitPushNumber(proxyMethod, paramsCount);
                proxyMethod.visitTypeInsn(Opcodes.ANEWARRAY, OBJECT_INTERNAL_NAME);
                for (int i = 0; i < paramsCount; i++) {
                    proxyMethod.visitInsn(Opcodes.DUP);
                    visitPushNumber(proxyMethod, i);
                    // proxyMethod.visitVarInsn(Opcodes.ALOAD, i + 1);
                    visitLoad(proxyMethod, parameters[i], i + 1);
                    proxyMethod.visitInsn(Opcodes.AASTORE);
                }
                proxyMethod.visitVarInsn(Opcodes.ASTORE, paramsCount + 3);
                // invoker.invoke(this, method, null, args);
                proxyMethod.visitVarInsn(Opcodes.ALOAD, paramsCount + 1);
                proxyMethod.visitVarInsn(Opcodes.ALOAD, 0);// this
                proxyMethod.visitVarInsn(Opcodes.ALOAD, paramsCount + 2);
                proxyMethod.visitInsn(Opcodes.ACONST_NULL);// null
                proxyMethod.visitVarInsn(Opcodes.ALOAD, paramsCount + 3);
                proxyMethod.visitMethodInsn(
                    Opcodes.INVOKEINTERFACE, PROXY_INVOKER_INTERNAL_NAME, "invoke", INVOKE_METHOD_DESCRIPTOR, true);
                // proxyMethod.visitTypeInsn(Opcodes.CHECKCAST, JieJvm.getInternalName(method.getReturnType()));
                // proxyMethod.visitInsn(Opcodes.ARETURN);
                visitReturn(proxyMethod, method.getReturnType());
                proxyMethod.visitMaxs(5, paramsCount + 3 + 1);
                proxyMethod.visitEnd();
                methodCount[0]++;
            }
            invokerCount[0]++;
        });

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
            Class<?>[] params = method.getParameterTypes();
            if (maxSize < params.length) {
                maxSize = params.length;
            }
            for (int j = 0; j < params.length; j++) {
                callSuper.visitVarInsn(ALOAD, 3);
                visitLoadArray(callSuper, params[j], j);
            }
            callSuper.visitMethodInsn(INVOKESPECIAL, superInternalName, method.getName(), JieJvm.getDescriptor(method), method.getDeclaringClass().isInterface());
            visitReturn(callSuper, method.getReturnType());
            // callSuper.visitInsn(RETURN);
            i++;
        }
        callSuper.visitLabel(switchLabel);
        callSuper.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        visitReturn(callSuper, Object.class);
        // callSuper.visitInsn(RETURN);
        callSuper.visitMaxs(maxSize + 1, maxSize + 2);
        callSuper.visitEnd();
    }

    private byte[] generateProxiedInvoker(
        String proxiedInternalName, String proxiedSimpleInternalName,
        String proxyInternalName, String proxyDescriptor
    ) {
        ClassWriter cw = new ClassWriter(0);
        // Inner class: X implements ProxiedInvoker
        cw.visit(V1_8, ACC_SUPER, proxiedInternalName, null, OBJECT_INTERNAL_NAME, new String[]{PROXIED_INVOKER_INTERNAL_NAME});
        cw.visitInnerClass(proxiedInternalName, proxyInternalName, proxiedSimpleInternalName, ACC_PRIVATE);
        cw.visitInnerClass(proxyInternalName + "$1", null, null, ACC_STATIC | ACC_SYNTHETIC);

        // Generates fields:
        // private final int i;
        FieldVisitor fv1 = cw.visitField(ACC_PRIVATE | ACC_FINAL, "i", "I", null, null);
        fv1.visitEnd();
        // Out.this
        FieldVisitor fv2 = cw.visitField(ACC_FINAL | ACC_SYNTHETIC, "this$0", proxyDescriptor, null, null);
        fv2.visitEnd();

        // Generates constructor: (int i)
        MethodVisitor cmv = cw.visitMethod(ACC_SYNTHETIC, "<init>", "(" + proxyDescriptor + "IL" + proxyInternalName + "$1;)V", null, null);
        cmv.visitVarInsn(ALOAD, 0);
        cmv.visitVarInsn(ALOAD, 1);
        cmv.visitFieldInsn(PUTFIELD, proxiedInternalName, "this$0", proxyDescriptor);
        cmv.visitVarInsn(ALOAD, 0);
        cmv.visitMethodInsn(INVOKESPECIAL, OBJECT_INTERNAL_NAME, "<init>", "()V", false);
        cmv.visitVarInsn(ALOAD, 0);
        cmv.visitVarInsn(ILOAD, 2);
        cmv.visitFieldInsn(PUTFIELD, proxiedInternalName, "i", "I");
        cmv.visitInsn(RETURN);
        cmv.visitMaxs(2, 4);
        cmv.visitEnd();

        // Generates methods:
        // invoke: return callSuper(i, inst, args);
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | ACC_VARARGS, "invoke", INVOKER_SUPER_METHOD_DESCRIPTOR, null, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, proxiedInternalName, "this$0", proxyDescriptor);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, proxiedInternalName, "i", "I");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(
            INVOKEVIRTUAL, proxyInternalName, "callSuper",
            "(I" + JieJvm.getDescriptor(Object.class) + JieJvm.getDescriptor(Object[].class) + ")" + JieJvm.getDescriptor(Object.class),
            false);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(4, 3);
        mv.visitEnd();

        return cw.toByteArray();
    }

    private void visitLoad(MethodVisitor visitor, Class<?> type, int i) {
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

    private void visitLoadArray(MethodVisitor visitor, Class<?> type, int i) {
        visitor.visitIntInsn(Opcodes.BIPUSH, i);
        if (!type.isPrimitive()) {
            visitor.visitInsn(Opcodes.AALOAD);
            return;
        }
        if (Objects.equals(type, boolean.class)) {
            visitor.visitInsn(Opcodes.IALOAD);
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
            return;
        }
        if (Objects.equals(type, byte.class)) {
            visitor.visitInsn(Opcodes.IALOAD);
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
            return;
        }
        if (Objects.equals(type, short.class)) {
            visitor.visitInsn(Opcodes.IALOAD);
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
            return;
        }
        if (Objects.equals(type, char.class)) {
            visitor.visitInsn(Opcodes.IALOAD);
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
            return;
        }
        if (Objects.equals(type, int.class)) {
            visitor.visitInsn(Opcodes.IALOAD);
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            return;
        }
        if (Objects.equals(type, long.class)) {
            visitor.visitInsn(Opcodes.LALOAD);
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
            return;
        }
        if (Objects.equals(type, float.class)) {
            visitor.visitInsn(Opcodes.FALOAD);
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
            return;
        }
        if (Objects.equals(type, double.class)) {
            visitor.visitInsn(Opcodes.DALOAD);
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
            return;
        }
    }

    private void visitReturn(MethodVisitor visitor, Class<?> type) {
        if (!type.isPrimitive()) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, JieJvm.getInternalName(type));
            visitor.visitInsn(Opcodes.ARETURN);
            return;
        }
        if (Objects.equals(type, boolean.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Boolean");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
            visitor.visitInsn(Opcodes.IRETURN);
            return;
        }
        if (Objects.equals(type, byte.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Byte");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
            visitor.visitInsn(Opcodes.IRETURN);
            return;
        }
        if (Objects.equals(type, short.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Short");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
            visitor.visitInsn(Opcodes.IRETURN);
            return;
        }
        if (Objects.equals(type, char.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Character");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
            visitor.visitInsn(Opcodes.IRETURN);
            return;
        }
        if (Objects.equals(type, int.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Integer");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            visitor.visitInsn(Opcodes.IRETURN);
            return;
        }
        if (Objects.equals(type, long.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Long");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
            visitor.visitInsn(Opcodes.IRETURN);
            return;
        }
        if (Objects.equals(type, float.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Float");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
            visitor.visitInsn(Opcodes.IRETURN);
            return;
        }
        if (Objects.equals(type, double.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Double");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
            visitor.visitInsn(Opcodes.IRETURN);
            return;
        }
    }

    private void visitPushNumber(MethodVisitor visitor, int i) {
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
        }
        // -127-128
        visitor.visitIntInsn(Opcodes.BIPUSH, i);
    }

    private static final class AsmClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] bytecode) {
            return defineClass(name, bytecode, 0, bytecode.length);
        }
    }
}
