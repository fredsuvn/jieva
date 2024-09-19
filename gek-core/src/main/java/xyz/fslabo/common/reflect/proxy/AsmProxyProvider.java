package xyz.fslabo.common.reflect.proxy;

import org.objectweb.asm.*;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.base.JieString;
import xyz.fslabo.common.base.Tuple;
import xyz.fslabo.common.coll.JieColl;
import xyz.fslabo.common.reflect.JieJvm;
import xyz.fslabo.common.reflect.JieReflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * <a href="https://asm.ow2.io/">ASM</a> implementation for {@link ProxyProvider}. The runtime environment must have
 * asm classed such as {@code org.objectweb.asm.ClassWriter}.
 * <p>
 * Note this provider doesn't support custom class loader, and the proxied class must have a public constructor with
 * empty parameters.
 *
 * @author fredsuvn
 */
public class AsmProxyProvider implements ProxyProvider, Opcodes {

    private static final AtomicLong counter = new AtomicLong();

    private static final String PROXY_INVOKER_INTERNAL_NAME = JieJvm.getInternalName(ProxyInvoker.class);
    private static final String PROXY_INVOKER_ARRAY_DESCRIPTOR = JieJvm.getDescriptor(ProxyInvoker[].class);
    private static final String METHOD_ARRAY_DESCRIPTOR = JieJvm.getDescriptor(Method[].class);
    private static final String METHOD_ARRAY_INTERNAL = JieJvm.getInternalName(Method[].class);
    private static final String OBJECT_INTERNAL_NAME = JieJvm.getInternalName(Object.class);
    private static final String PROXY_INVOKER_SIMPLE_NAME = ProxyInvoker.class.getSimpleName();
    private static final String CALL_SUPER_METHOD_DESCRIPTOR =
        "(I" + JieJvm.getDescriptor(Object.class) + JieJvm.getDescriptor(Object[].class) + JieJvm.getDescriptor($X.class) + ")"
            + JieJvm.getDescriptor(Object.class);
    private static final String CALL_SUPER_METHOD_NAME = "callSuper";
    private static final String CALL_VIRTUAL_METHOD_NAME = "callVirtual";

    @Override
    public <T> T newProxyInstance(
        @Nullable ClassLoader loader,
        Iterable<Class<?>> proxied,
        MethodProxyHandler handler
    ) {
        if (JieColl.isEmpty(proxied)) {
            throw new ProxyException("No super class or interface to proxy.");
        }
        Iterator<Class<?>> classIterator = proxied.iterator();
        Class<?> firstClass = classIterator.next();
        String superInternalName;
        List<String> interfaceInternalNames = new LinkedList<>();
        if (firstClass.isInterface()) {
            superInternalName = OBJECT_INTERNAL_NAME;
            interfaceInternalNames.add(JieJvm.getInternalName(firstClass));
        } else {
            superInternalName = JieJvm.getInternalName(firstClass);
        }
        while (classIterator.hasNext()) {
            Class<?> cls = classIterator.next();
            if (!cls.isInterface()) {
                throw new ProxyException("Only one super class:"
                    + JieColl.stream(proxied).map(Class::getName).collect(Collectors.joining(", "))
                    + "."
                );
            }
            interfaceInternalNames.add(JieJvm.getInternalName(cls));
        }
        long count = counter.getAndIncrement();
        String proxyClassName = getClass().getName() + "$Proxy$" + count;
        String proxyInternalName = JieString.replace(proxyClassName, ".", "/");
        String proxyDescriptor = "L" + proxyInternalName + ";";
        // String invokerName = proxyClassName + "$" + PROXY_INVOKER_SIMPLE_NAME;
        String invokerInternalName = proxyInternalName + "$" + PROXY_INVOKER_SIMPLE_NAME;
        Map<String, Method> proxiedMethods = new LinkedHashMap<>();
        for (Class<?> upper : proxied) {
            Method[] methods = upper.getMethods();
            for (Method method : methods) {
                if (Modifier.isFinal(method.getModifiers()) || Modifier.isStatic(method.getModifiers())) {
                    continue;
                }
                if (!handler.proxy(method)) {
                    continue;
                }
                String methodInfo = method.getName() + ":" + JieJvm.getDescriptor(method);
                if (proxiedMethods.containsKey(methodInfo)) {
                    continue;
                }
                proxiedMethods.put(methodInfo, method);
            }
        }
        List<Method> methodList = new ArrayList<>(proxiedMethods.values());
        // Generates proxy class
        ProxyGenerator proxyGenerator = new ProxyGenerator(
            proxyInternalName,
            generateSignature(proxied),
            superInternalName,
            interfaceInternalNames,
            invokerInternalName,
            methodList
        );
        byte[] proxyBytecode = proxyGenerator.generateBytecode();
        Class<?> proxyClass = JieJvm.loadBytecode(proxyBytecode);
        // Generates invoker class
        InvokerGenerator invokerGenerator = new InvokerGenerator(
            proxyInternalName,
            proxyDescriptor,
            invokerInternalName
        );
        byte[] invokerBytecode = invokerGenerator.generateBytecode();
        Class<?> invokerClass = JieJvm.loadBytecode(invokerBytecode);
        Constructor<?> constructor = JieReflect.getConstructor(proxyClass, Jie.array(MethodProxyHandler.class, Method[].class));
        try {
            return Jie.as(
                constructor.newInstance(handler, methodList.toArray(new Method[0]))
            );
        } catch (Exception e) {
            throw new ProxyException(e);
        }
    }

    private String generateSignature(Iterable<Class<?>> uppers) {
        StringBuilder sb = new StringBuilder();
        for (Class<?> upper : uppers) {
            sb.append(JieJvm.getSignature(upper));
        }
        return sb.toString();
    }

    private static final Map<Class<?>, String> VALUE_OF_DESCRIPTORS = Jie.map(
        boolean.class, Tuple.of(Opcodes.ILOAD,"(Z)Ljava/lang/Boolean;"),
        byte.class, Tuple.of(Opcodes.ILOAD,"(B)Ljava/lang/Byte;"),
        short.class, Tuple.of(Opcodes.ILOAD,"(S)Ljava/lang/Short;"),
        char.class, Tuple.of(Opcodes.ILOAD,"(C)Ljava/lang/Character;"),
        int.class, Tuple.of(Opcodes.ILOAD,"(I)Ljava/lang/Integer;"),
        long.class, Tuple.of(Opcodes.ILOAD,"(J)Ljava/lang/Long;"),
        float.class, Tuple.of(Opcodes.ILOAD,"(F)Ljava/lang/Float;"),
        double.class, Tuple.of(Opcodes.ILOAD,"(D)Ljava/lang/Double;")
    );

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
        // if (Objects.equals(type, double.class))
        {
            visitor.visitVarInsn(Opcodes.DLOAD, i);
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
            // return;
        }
    }

    private static void visitObjectCast(MethodVisitor visitor, Class<?> type, boolean needReturn) {
        if (type.isPrimitive()) {

        }


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
        // if (Objects.equals(type, double.class))
        {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Double");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
            if (needReturn) {
                visitor.visitInsn(Opcodes.DRETURN);
            }
            // return;
        }
    }

    private static void returnCastObject(MethodVisitor visitor, Class<?> type) {
        if (!type.isPrimitive()) {
            visitor.visitInsn(Opcodes.ARETURN);
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
        } else {
            visitor.visitInsn(ACONST_NULL);
        }
        visitor.visitInsn(Opcodes.ARETURN);
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
        }
        if (i <= Byte.MAX_VALUE) {
            visitor.visitIntInsn(Opcodes.BIPUSH, i);
        } else {
            visitor.visitIntInsn(Opcodes.SIPUSH, i);
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

    private static final class ProxyGenerator implements Opcodes {

        private static final String HANDLER_DESCRIPTOR = JieJvm.getDescriptor(MethodProxyHandler.class);
        private static final String HANDLER_INTERNAL = JieJvm.getInternalName(MethodProxyHandler.class);
        private static final String INIT_DESCRIPTOR =
            "(" + HANDLER_DESCRIPTOR + METHOD_ARRAY_DESCRIPTOR + ")V";

        private static final String HANDLER_METHOD_DESCRIPTOR = JieJvm.getDescriptor(JieReflect.getMethod(
            MethodProxyHandler.class, "invoke", Jie.array(Object.class, Method.class, Object[].class, ProxyInvoker.class))
        );

        private final String proxyInternalName;
        private final String proxySignature;
        private final String superInternalName;
        private final List<String> superInternalNames;
        private final String invokerInternalName;
        private final List<Method> methods;

        private ProxyGenerator(
            String proxyInternalName,
            String proxySignature,
            String superInternalName,
            List<String> superInternalNames,
            String invokerInternalName,
            List<Method> methods
        ) {
            this.proxyInternalName = proxyInternalName;
            this.proxySignature = proxySignature;
            this.superInternalName = superInternalName;
            this.superInternalNames = superInternalNames;
            this.invokerInternalName = invokerInternalName;
            this.methods = methods;
        }

        public byte[] generateBytecode() {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            // Declaring
            cw.visit(V1_8, ACC_PUBLIC | ACC_SUPER, proxyInternalName, proxySignature, superInternalName, superInternalNames.isEmpty() ? null : superInternalNames.toArray(new String[0]));
            cw.visitInnerClass(invokerInternalName, proxyInternalName, PROXY_INVOKER_SIMPLE_NAME, ACC_STATIC | ACC_SYNTHETIC);

            // Generates fields
            FieldVisitor fieldVisitor;
            {
                // private final MethodProxyHandler handler
                fieldVisitor = cw.visitField(ACC_PRIVATE, "handler", HANDLER_DESCRIPTOR, null, null);
                fieldVisitor.visitEnd();
            }
            {
                // private Method[] methods
                fieldVisitor = cw.visitField(ACC_PRIVATE, "methods", METHOD_ARRAY_DESCRIPTOR, null, null);
                fieldVisitor.visitEnd();
            }
            {
                // private ProxyInvoker[] invokers
                fieldVisitor = cw.visitField(ACC_PRIVATE, "invokers", PROXY_INVOKER_ARRAY_DESCRIPTOR, null, null);
                fieldVisitor.visitEnd();
            }

            // Generates constructor: (MethodProxyHandler handler, Method[] methods)
            MethodVisitor constructor;
            {
                constructor = cw.visitMethod(ACC_PUBLIC, "<init>", INIT_DESCRIPTOR, null, null);
                // super();
                constructor.visitVarInsn(ALOAD, 0);
                constructor.visitMethodInsn(INVOKESPECIAL, superInternalName, "<init>", "()V", false);
                // this.handler = handler;
                constructor.visitVarInsn(ALOAD, 0);
                constructor.visitVarInsn(ALOAD, 1);
                constructor.visitFieldInsn(PUTFIELD, proxyInternalName, "handler", HANDLER_DESCRIPTOR);
                // this.methods = methods;
                constructor.visitVarInsn(ALOAD, 0);
                constructor.visitVarInsn(ALOAD, 2);
                constructor.visitFieldInsn(PUTFIELD, proxyInternalName, "methods", METHOD_ARRAY_DESCRIPTOR);
                // this.invokers = new ProxyInvoker[length];
                constructor.visitVarInsn(ALOAD, 0);
                constructor.visitVarInsn(ALOAD, 2);
                constructor.visitInsn(ARRAYLENGTH);
                constructor.visitTypeInsn(ANEWARRAY, PROXY_INVOKER_INTERNAL_NAME);
                constructor.visitFieldInsn(PUTFIELD, proxyInternalName, "invokers", PROXY_INVOKER_ARRAY_DESCRIPTOR);
                // int i = 0
                constructor.visitInsn(ICONST_0);
                constructor.visitVarInsn(ISTORE, 3);
                // for-i
                Label forLabel = new Label();
                constructor.visitLabel(forLabel);
                constructor.visitFrame(F_FULL, 4, new Object[]{proxyInternalName, HANDLER_INTERNAL, METHOD_ARRAY_INTERNAL, INTEGER}, 0, new Object[]{});
                constructor.visitVarInsn(ILOAD, 3);
                constructor.visitVarInsn(ALOAD, 0);
                constructor.visitFieldInsn(GETFIELD, proxyInternalName, "invokers", PROXY_INVOKER_ARRAY_DESCRIPTOR);
                constructor.visitInsn(ARRAYLENGTH);
                Label breakLabel = new Label();
                // if(i >= invokers.length) break;
                constructor.visitJumpInsn(IF_ICMPGE, breakLabel);
                // invokers[i] = new InvokerImpl(i);
                constructor.visitVarInsn(ALOAD, 0);
                constructor.visitFieldInsn(GETFIELD, proxyInternalName, "invokers", PROXY_INVOKER_ARRAY_DESCRIPTOR);
                constructor.visitVarInsn(ILOAD, 3);
                constructor.visitTypeInsn(NEW, invokerInternalName);
                constructor.visitInsn(DUP);
                constructor.visitVarInsn(ALOAD, 0);
                constructor.visitVarInsn(ILOAD, 3);
                constructor.visitMethodInsn(INVOKESPECIAL, invokerInternalName, "<init>", "(L" + proxyInternalName + ";I)V", false);
                constructor.visitInsn(AASTORE);
                constructor.visitIincInsn(3, 1);
                constructor.visitJumpInsn(GOTO, forLabel);
                constructor.visitLabel(breakLabel);
                constructor.visitFrame(F_CHOP, 1, null, 0, null);
                constructor.visitInsn(RETURN);
                // constructor.visitMaxs(6, 4);
                constructor.visitMaxs(0, 0);
                constructor.visitEnd();
            }

            // Generates override methods
            int methodCount = 0;
            for (Method method : methods) {
                MethodVisitor override = cw.visitMethod(ACC_PUBLIC, method.getName(), JieJvm.getDescriptor(method), null, null);
                Parameter[] parameters = method.getParameters();
                int extraLocalIndex = countExtraLocalIndex(parameters);
                // method = this.methods[j]
                override.visitVarInsn(ALOAD, 0);
                override.visitFieldInsn(GETFIELD, proxyInternalName, "methods", METHOD_ARRAY_DESCRIPTOR);
                visitPushNumber(override, methodCount);
                override.visitInsn(AALOAD);
                override.visitVarInsn(ASTORE, extraLocalIndex);
                // Object args = new Object[]{};
                visitPushNumber(override, parameters.length);
                override.visitTypeInsn(ANEWARRAY, OBJECT_INTERNAL_NAME);
                int paramIndex = 1;
                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    override.visitInsn(DUP);
                    visitPushNumber(override, i);
                    visitLoadParamAsObject(override, parameter.getType(), paramIndex);
                    override.visitInsn(AASTORE);
                    if (Objects.equals(parameter.getType(), long.class) || Objects.equals(parameter.getType(), double.class)) {
                        paramIndex += 2;
                    } else {
                        paramIndex++;
                    }
                }
                override.visitVarInsn(ASTORE, extraLocalIndex + 1);
                // invoker = this.invokers[i];
                override.visitVarInsn(ALOAD, 0);
                override.visitFieldInsn(GETFIELD, proxyInternalName, "invokers", PROXY_INVOKER_ARRAY_DESCRIPTOR);
                visitPushNumber(override, methodCount);
                override.visitInsn(AALOAD);
                override.visitVarInsn(ASTORE, extraLocalIndex + 2);
                // handler
                override.visitVarInsn(ALOAD, 0);
                override.visitFieldInsn(GETFIELD, proxyInternalName, "handler", HANDLER_DESCRIPTOR);
                // this
                override.visitVarInsn(ALOAD, 0);
                // method
                override.visitVarInsn(ALOAD, extraLocalIndex);
                // args
                override.visitVarInsn(ALOAD, extraLocalIndex + 1);
                // invoker
                override.visitVarInsn(ALOAD, extraLocalIndex + 2);
                // handler.invoke(this, method, args, invoker)
                override.visitMethodInsn(INVOKEINTERFACE, HANDLER_INTERNAL, "invoke", HANDLER_METHOD_DESCRIPTOR, true);
                if (Objects.equals(method.getReturnType(), void.class)) {
                    override.visitInsn(RETURN);
                } else {
                    visitObjectCast(override, method.getReturnType(), true);
                }
                // override.visitMaxs(5, extraLocalIndex + 3);
                override.visitMaxs(0, 0);
                override.visitEnd();
                methodCount++;
            }

            // Generate callSuper(i, inst, args, null)
            {
                Label[] caseLabels = methods.stream().map(it -> new Label()).toArray(Label[]::new);
                MethodVisitor callSuper = cw.visitMethod(ACC_PUBLIC, CALL_SUPER_METHOD_NAME, CALL_SUPER_METHOD_DESCRIPTOR, null, null);
                callSuper.visitVarInsn(ILOAD, 1);
                Label switchLabel = new Label();
                callSuper.visitTableSwitchInsn(0, methods.size() - 1, switchLabel, caseLabels);
                int i = 0;
                for (Method method : methods) {
                    callSuper.visitLabel(caseLabels[i]);
                    callSuper.visitFrame(F_SAME, 0, null, 0, null);
                    callSuper.visitVarInsn(ALOAD, 0);
                    // callSuper.visitTypeInsn(CHECKCAST, JieJvm.getInternalName(method.getDeclaringClass()));
                    // callSuper.visitTypeInsn(Opcodes.CHECKCAST, JieJvm.getInternalName(method.getDeclaringClass()));
                    Class<?>[] params = method.getParameterTypes();
                    for (int j = 0; j < params.length; j++) {
                        callSuper.visitVarInsn(ALOAD, 3);
                        visitPushNumber(callSuper, j);
                        callSuper.visitInsn(AALOAD);
                        visitObjectCast(callSuper, params[j], false);
                    }
                    String methodOwnerInternalName = JieJvm.getInternalName(method.getDeclaringClass());
                    boolean hasOwner = Objects.equals(methodOwnerInternalName, superInternalName) || superInternalNames.contains(methodOwnerInternalName);
                    if (hasOwner) {
                        callSuper.visitMethodInsn(INVOKESPECIAL, JieJvm.getInternalName(method.getDeclaringClass()), method.getName(), JieJvm.getDescriptor(method), method.getDeclaringClass().isInterface());
                    } else {
                        callSuper.visitMethodInsn(INVOKESPECIAL, superInternalName, method.getName(), JieJvm.getDescriptor(method), false);
                    }
                    returnCastObject(callSuper, method.getReturnType());
                    i++;
                }
                callSuper.visitLabel(switchLabel);
                callSuper.visitFrame(F_SAME, 0, null, 0, null);
                callSuper.visitInsn(ACONST_NULL);
                // stackCount.increment();
                callSuper.visitInsn(ARETURN);
                // callSuper.visitMaxs(0, 5);
                callSuper.visitMaxs(0, 0);
                callSuper.visitEnd();
            }

            // Generate callVirtual(i, inst, args, null)
            {
                Label[] caseLabels = methods.stream().map(it -> new Label()).toArray(Label[]::new);
                MethodVisitor callVirtual = cw.visitMethod(ACC_PUBLIC, CALL_VIRTUAL_METHOD_NAME, CALL_SUPER_METHOD_DESCRIPTOR, null, null);
                callVirtual.visitVarInsn(ILOAD, 1);
                Label switchLabel = new Label();
                callVirtual.visitTableSwitchInsn(0, methods.size() - 1, switchLabel, caseLabels);
                int i = 0;
                for (Method method : methods) {
                    callVirtual.visitLabel(caseLabels[i]);
                    callVirtual.visitFrame(F_SAME, 0, null, 0, null);
                    callVirtual.visitVarInsn(ALOAD, 2);
                    callVirtual.visitTypeInsn(CHECKCAST, JieJvm.getInternalName(method.getDeclaringClass()));
                    Class<?>[] params = method.getParameterTypes();
                    for (int j = 0; j < params.length; j++) {
                        callVirtual.visitVarInsn(ALOAD, 3);
                        visitPushNumber(callVirtual, j);
                        callVirtual.visitInsn(AALOAD);
                        visitObjectCast(callVirtual, params[j], false);
                    }
                    if (method.getDeclaringClass().isInterface()) {
                        callVirtual.visitMethodInsn(INVOKEINTERFACE, JieJvm.getInternalName(method.getDeclaringClass()), method.getName(), JieJvm.getDescriptor(method), true);
                    } else {
                        callVirtual.visitMethodInsn(INVOKEVIRTUAL, JieJvm.getInternalName(method.getDeclaringClass()), method.getName(), JieJvm.getDescriptor(method), false);
                    }
                    returnCastObject(callVirtual, method.getReturnType());
                    i++;
                }
                callVirtual.visitLabel(switchLabel);
                callVirtual.visitFrame(F_SAME, 0, null, 0, null);
                callVirtual.visitInsn(ACONST_NULL);
                callVirtual.visitInsn(ARETURN);
                // callVirtual.visitMaxs(2 + 2 * maxParameterCount, 5);
                callVirtual.visitMaxs(0, 0);
                callVirtual.visitEnd();
            }

            return cw.toByteArray();
        }
    }

    private static final class InvokerGenerator implements Opcodes {

        private static final String INVOKE_METHOD_DESCRIPTOR = JieJvm.getDescriptor(
            JieReflect.getMethod(ProxyInvoker.class, "invoke", Jie.array(Object.class, Object[].class))
        );
        private static final String INVOKE_SUPER_METHOD_DESCRIPTOR = JieJvm.getDescriptor(
            JieReflect.getMethod(ProxyInvoker.class, "invokeSuper", Jie.array(Object[].class))
        );

        private final String proxyInternalName;
        private final String proxyDescriptor;
        private final String invokerInternalName;

        private InvokerGenerator(
            String proxyInternalName,
            String proxyDescriptor,
            String invokerInternalName
        ) {
            this.proxyInternalName = proxyInternalName;
            this.proxyDescriptor = proxyDescriptor;
            this.invokerInternalName = invokerInternalName;
        }

        public byte[] generateBytecode() {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            // Declaring: Inner class: X implements ProxyInvoker
            cw.visit(V1_8, ACC_PUBLIC, invokerInternalName, null, OBJECT_INTERNAL_NAME, new String[]{PROXY_INVOKER_INTERNAL_NAME});
            cw.visitInnerClass(invokerInternalName, proxyInternalName, PROXY_INVOKER_SIMPLE_NAME, ACC_STATIC | ACC_SYNTHETIC);

            // Generates fields:
            // private final int i;
            FieldVisitor fv1 = cw.visitField(ACC_PRIVATE | ACC_FINAL, "i", "I", null, null);
            fv1.visitEnd();
            // Out.this
            FieldVisitor fv2 = cw.visitField(ACC_FINAL | ACC_SYNTHETIC, "this$0", proxyDescriptor, null, null);
            fv2.visitEnd();

            // Generates constructor: (int i)
            MethodVisitor cmv = cw.visitMethod(ACC_SYNTHETIC, "<init>", "(" + proxyDescriptor + "I)V", null, null);
            // this.this$0 = out;
            cmv.visitVarInsn(ALOAD, 0);
            cmv.visitVarInsn(ALOAD, 1);
            cmv.visitFieldInsn(PUTFIELD, invokerInternalName, "this$0", proxyDescriptor);
            // super();
            cmv.visitVarInsn(ALOAD, 0);
            cmv.visitMethodInsn(INVOKESPECIAL, OBJECT_INTERNAL_NAME, "<init>", "()V", false);
            // this.i = i;
            cmv.visitVarInsn(ALOAD, 0);
            cmv.visitVarInsn(ILOAD, 2);
            cmv.visitFieldInsn(PUTFIELD, invokerInternalName, "i", "I");
            cmv.visitInsn(RETURN);
            // cmv.visitMaxs(2, 3);
            cmv.visitMaxs(0, 0);
            cmv.visitEnd();

            // Generates methods:
            // invoke(Object inst, Object[] args): return proxy.callSuper(i, inst, args, null);
            MethodVisitor invoke = cw.visitMethod(ACC_PUBLIC, "invoke", INVOKE_METHOD_DESCRIPTOR, null, null);
            invoke.visitVarInsn(ALOAD, 0);
            invoke.visitFieldInsn(GETFIELD, invokerInternalName, "this$0", proxyDescriptor);
            invoke.visitVarInsn(ALOAD, 0);
            invoke.visitFieldInsn(GETFIELD, invokerInternalName, "i", "I");
            invoke.visitVarInsn(ALOAD, 1);
            invoke.visitVarInsn(ALOAD, 2);
            invoke.visitInsn(ACONST_NULL);
            invoke.visitMethodInsn(INVOKEVIRTUAL, proxyInternalName, CALL_VIRTUAL_METHOD_NAME, CALL_SUPER_METHOD_DESCRIPTOR, false);
            invoke.visitInsn(ARETURN);
            // invoke.visitMaxs(5, 3);
            invoke.visitMaxs(0, 0);
            invoke.visitEnd();
            // invokeSuper(Object[] args): return proxy.callSuper(i, proxy, args, null);
            MethodVisitor invokeSuper = cw.visitMethod(ACC_PUBLIC, "invokeSuper", INVOKE_SUPER_METHOD_DESCRIPTOR, null, null);
            invokeSuper.visitVarInsn(ALOAD, 0);
            invokeSuper.visitFieldInsn(GETFIELD, invokerInternalName, "this$0", proxyDescriptor);
            invokeSuper.visitInsn(DUP);
            invokeSuper.visitVarInsn(ASTORE, 2);
            invokeSuper.visitVarInsn(ALOAD, 0);
            invokeSuper.visitFieldInsn(GETFIELD, invokerInternalName, "i", "I");
            invokeSuper.visitVarInsn(ALOAD, 2);
            invokeSuper.visitVarInsn(ALOAD, 1);
            invokeSuper.visitInsn(ACONST_NULL);
            invokeSuper.visitMethodInsn(INVOKEVIRTUAL, proxyInternalName, CALL_SUPER_METHOD_NAME, CALL_SUPER_METHOD_DESCRIPTOR, false);
            invokeSuper.visitInsn(ARETURN);
            // invokeSuper.visitMaxs(5, 3);
            invokeSuper.visitMaxs(0, 0);
            invokeSuper.visitEnd();

            return cw.toByteArray();
        }
    }

    private static final class $X {}
}
