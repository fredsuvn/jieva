package xyz.srclab.helper.code;

import xyz.srclab.helper.code.invoke.InvokerCodeGenerator;

public class Launcher {

    public static void main(String[] args) {
        CodeGenerator codeGenerator = new InvokerCodeGenerator();
        codeGenerator.generate("virtual", System.out);
        codeGenerator.generate("Static", System.out);
    }
}
