package xyz.srclab.common.bytecode.provider.invoke.asm.spring;

import xyz.srclab.common.bytecode.provider.invoke.asm.AbstractAsmInvokerProvider;

/**
 * @author sunqian
 */
public class AsmInvokerProvider extends AbstractAsmInvokerProvider {

    public AsmInvokerProvider() {
        super(new AsmInvokerGeneratorImpl());
    }
}
