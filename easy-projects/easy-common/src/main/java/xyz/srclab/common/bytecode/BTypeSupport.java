package xyz.srclab.common.bytecode;

/**
 * @author sunqian
 */
final class BTypeSupport {

    private static final BType[] EMPTY_B_TYPE_ARRAY = new BType[0];

    private static final class BTypeImpl implements BType {

        private final String className;
        private final BTypeVariable[] typeVariables;
        private final BType[] inheritances;

        private BTypeImpl(String className, BTypeVariable[] typeVariables, BType[] inheritances) {
            this.className = className;
            this.typeVariables = typeVariables;
            this.inheritances = inheritances;
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
            return ByteCodeHelper.getTypeSignature(typeVariables, inheritances);
        }
    }

    private static final class BArrayTypeImpl implements BArrayType {

        private final BType componentType;

        private BArrayTypeImpl(BType componentType) {
            this.componentType = componentType;
        }

        @Override
        public String getInternalName() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getDescriptor() {
            return "[" + componentType.getDescriptor();
        }

        @Override
        public String getSignature() {
            return "[" + componentType.getSignature();
        }
    }

}
