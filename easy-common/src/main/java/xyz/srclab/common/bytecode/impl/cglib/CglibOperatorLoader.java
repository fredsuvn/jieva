package xyz.srclab.common.bytecode.impl.cglib;

import xyz.srclab.common.bytecode.impl.cglib.original.OriginalCglibOperator;
import xyz.srclab.common.bytecode.impl.cglib.spring.SpringCglibOperator;

class CglibOperatorLoader {

    private static final CglibOperator INSTANCE;

    static {
        Package cglibPackage = Package.getPackage("org.springframework.cglib.proxy");
        INSTANCE = cglibPackage == null ? new OriginalCglibOperator() : new SpringCglibOperator();
    }

    static CglibOperator getInstance() {
        return INSTANCE;
    }
}
