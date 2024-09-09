package xyz.fslabo.common.proxy;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
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
public class AsmProxyProvider implements ProxyProvider {

    private static final AtomicLong counter = new AtomicLong();

    private static final String PROXY_INVOKER_INTERNAL_NAME = JieJvm.getInternalName(ProxyInvoker.class);
    private static final String PROXIED_INVOKER_INTERNAL_NAME = JieJvm.getDescriptor(ProxiedInvoker.class);
    private static final String PROXY_INVOKER_ARRAY_DESCRIPTOR = JieJvm.getDescriptor(ProxyInvoker[].class);
    private static final String METHOD_ARRAY_DESCRIPTOR = JieJvm.getDescriptor(Method[].class);
    private static final String PROXIED_INVOKER_ARRAY_DESCRIPTOR = JieJvm.getDescriptor(ProxiedInvoker[].class);

    private static final String PROXY_INVOKER_DESCRIPTOR = JieJvm.getDescriptor(ProxyInvoker.class);
    private static final String OBJECT_INTERNAL_NAME = JieJvm.getInternalName(Object.class);
    private static final String INIT_DESCRIPTOR = "(" + PROXY_INVOKER_ARRAY_DESCRIPTOR + METHOD_ARRAY_DESCRIPTOR + ")V";
    private static final String INVOKE_METHOD_DESCRIPTOR;

    static {
        try {
            INVOKE_METHOD_DESCRIPTOR = JieJvm.getDescriptor(ProxyInvoker.class.getMethod(
                "invoke", Object.class, Method.class, ProxiedInvoker.class, Object[].class));
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
        String newClassName = getClass().getName() + "$" + counter.getAndIncrement();
        String newInternalName = newClassName.replaceAll("\\.", "/");
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
        byte[] bytecode = generateBytecode(newInternalName, uppers, invokerMap);
        Class<?> cls = asmClassLoader.defineClass(newClassName, bytecode);
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
        String newInternalName, Iterable<Class<?>> uppers, Map<ProxyInvoker, List<Method>> invokerMap) {
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
            Opcodes.ACC_PUBLIC,
            newInternalName,
            generateSignature(uppers),
            superInternalName,
            interfaceInternalNames == null ? null : interfaceInternalNames.toArray(new String[0])
        );
        generateProxyMethods(cw, newInternalName, superInternalName, invokerMap);
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
        ClassWriter cw, String newInternalName, String superInternalName, Map<ProxyInvoker, List<Method>> invokerMap) {
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
        constructor.visitInsn(Opcodes.RETURN);
        constructor.visitMaxs(2, 3);
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
    }

    private byte[] generateProxiedInvokerBytecode(String newInternalName, Iterable<Class<?>> uppers) {
        ClassWriter cw = new ClassWriter(0);
        String superInternalName = JieJvm.getInternalName(Object.class);
        List<String> interfaceInternalNames = new LinkedList<>();
        interfaceInternalNames.add(PROXIED_INVOKER_INTERNAL_NAME);
        int i = 0;
        for (Class<?> upper : uppers) {
            if (i == 0) {
                if (!upper.isInterface()) {
                    superInternalName = JieJvm.getInternalName(upper);
                    continue;
                }
            }
            interfaceInternalNames.add(JieJvm.getInternalName(upper));
            i++;
        }
        cw.visit(
            Opcodes.V1_8,
            Opcodes.ACC_PUBLIC,
            newInternalName,
            generateSignature(uppers),
            superInternalName,
            interfaceInternalNames.toArray(new String[0])
        );

        // Generates constructor: (String internalName, String methodName, methodDescriptor)
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
        constructor.visitInsn(Opcodes.RETURN);
        constructor.visitMaxs(2, 3);
        constructor.visitEnd();

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
