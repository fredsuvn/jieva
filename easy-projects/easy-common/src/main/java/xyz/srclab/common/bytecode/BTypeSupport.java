package xyz.srclab.common.bytecode;

import org.apache.commons.lang3.ArrayUtils;
import xyz.srclab.common.util.string.StringHelper;

/**
 * @author sunqian
 */
final class BTypeSupport {

    private static final BType[] EMPTY_TYPE_ARRAY = new BType[0];

    private static final BType OBJECT_TYPE = BType.of(Object.class);

    private static final BType[] OBJECT_BOUNDS = {OBJECT_TYPE};

    static BType newBType(Class<?> cls) {
        return new BTypeImpl(cls);
    }

    static BType newBType(String className, BType[] genericTypes, boolean array) {
        return new BTypeImpl(className, genericTypes, array);
    }

    static BNewType newBNewBType(String className, BTypeVariable[] typeVariables, BType[] inheritances) {
        return new BNewTypeImpl(className, typeVariables, inheritances);
    }

    static BTypeVariable newBTypeVariable(String name, BType[] bounds, boolean isInterface) {
        return new BTypeVariableImpl(name, bounds, isInterface);
    }

    static BWildcardType newBWildcardType(BType[] bounds, boolean upper) {
        return new BWildcardTypeImpl(bounds, upper);
    }

    private static final class BTypeImpl implements BType {

        private final String className;
        private final BType[] genericTypes;
        private final boolean array;

        BTypeImpl(Class<?> cls) {
            this(cls.getName(), EMPTY_TYPE_ARRAY, false);
        }

        BTypeImpl(String className, BType[] genericTypes, boolean array) {
            this.className = className;
            this.genericTypes = genericTypes;
            this.array = array;
        }

        @Override
        public String getClassName() {
            return className;
        }

        @Override
        public String getInternalName() {
            return (array ? "[" : "") + ByteCodeHelper.getTypeInternalName(className);
        }

        @Override
        public String getDescriptor() {
            return (array ? "[" : "") + ByteCodeHelper.getTypeDescriptor(className);
        }

        @Override
        public String getSignature() {
            return (array ? "[" : "") + ByteCodeHelper.getTypeSignature(className, genericTypes);
        }
    }

    private static final class BNewTypeImpl implements BNewType {

        private final String className;
        private final BTypeVariable[] typeVariables;
        private final BType[] inheritances;

        BNewTypeImpl(String className, BTypeVariable[] typeVariables, BType[] inheritances) {
            this.className = className;
            this.typeVariables = typeVariables;
            this.inheritances = inheritances;
        }

        @Override
        public String getClassName() {
            return className;
        }

        @Override
        public String getInternalName() {
            return ByteCodeHelper.getTypeInternalName(className);
        }

        @Override
        public String getDescriptor() {
            return ByteCodeHelper.getTypeDescriptor(className);
        }

        @Override
        public String getSignature() {
            return ByteCodeHelper.getTypeDeclarationSignature(typeVariables, inheritances);
        }
    }

    private static final class BTypeVariableImpl implements BTypeVariable {

        private final String name;
        private final BType[] bounds;
        private final boolean isInterface;

        BTypeVariableImpl(String name, BType[] bounds, boolean isInterface) {
            this.name = name;
            this.bounds = ArrayUtils.isEmpty(bounds) ? OBJECT_BOUNDS : bounds;
            this.isInterface = isInterface;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDeclaration() {
            return name +
                    (isInterface ? ":" : "") +
                    StringHelper.join("", bounds, BType::getSignature);
        }

        @Override
        public String getClassName() {
            return bounds[0].getClassName();
        }

        @Override
        public String getInternalName() {
            return bounds[0].getInternalName();
        }

        @Override
        public String getDescriptor() {
            return bounds[0].getDescriptor();
        }

        @Override
        public String getSignature() {
            return "T" + name + ";";
        }
    }

    private static final class BWildcardTypeImpl implements BWildcardType {

        private static final BType[] EMPTY_BOUNDS = new BType[0];

        private final BType[] bounds;
        private final boolean upper;

        BWildcardTypeImpl(BType[] bounds, boolean upper) {
            this.bounds = ArrayUtils.isEmpty(bounds) ? OBJECT_BOUNDS : bounds;
            this.upper = upper;
        }

        @Override
        public BType[] getUpperBounds() {
            return upper ? bounds : EMPTY_BOUNDS;
        }

        @Override
        public BType[] getLowerBounds() {
            return !upper ? bounds : EMPTY_BOUNDS;
        }

        @Override
        public String getClassName() {
            return upper ? bounds[0].getClassName() : OBJECT_TYPE.getClassName();
        }

        @Override
        public String getInternalName() {
            return upper ? bounds[0].getClassName() : OBJECT_TYPE.getInternalName();
        }

        @Override
        public String getDescriptor() {
            return upper ? bounds[0].getClassName() : OBJECT_TYPE.getDescriptor();
        }

        @Override
        public String getSignature() {
            return upper ? bounds[0].getClassName() : OBJECT_TYPE.getSignature();
        }
    }
}
