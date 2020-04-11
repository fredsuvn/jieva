package xyz.srclab.helper.code.invoke;

import xyz.srclab.helper.code.CodeGenerator;

import java.io.PrintStream;

public class InvokerCodeGenerator implements CodeGenerator {

    private void generateVirtual(PrintStream printStream) {
        StringBuilder buf = new StringBuilder();
        buf.append("switch (args.length) {");
        buf.append("case 0: return methodHandle.invoke(object);");
        for (int i = 1; i <= 255; i++) {
            buf.append("case ").append(i).append(": ");
            buf.append("return methodHandle.invoke(object,");
            for (int j = 0; j < i; j++) {
                buf.append("args[").append(j).append("]");
                buf.append(",");
            }
            buf.replace(buf.length() - 1, buf.length(), ");");
        }
        buf.append("default: Object[] arguments = new Object[args.length + 1];" +
                "arguments[0] = object;" +
                "System.arraycopy(args, 0, arguments, 1, args.length);" +
                "return methodHandle.invokeWithArguments(arguments);");
        printStream.println(buf.toString());
    }

    private void generateStatic(PrintStream printStream) {
        StringBuilder buf = new StringBuilder();
        buf.append("switch (args.length) {");
        buf.append("case 0: return methodHandle.invoke();");
        for (int i = 1; i <= 255; i++) {
            buf.append("case ").append(i).append(": ");
            buf.append("return methodHandle.invoke(");
            for (int j = 0; j < i; j++) {
                buf.append("args[").append(j).append("]");
                buf.append(",");
            }
            buf.replace(buf.length() - 1, buf.length(), ");");
        }
        buf.append("default: return methodHandle.invokeWithArguments(args);");
        printStream.println(buf.toString());
    }
}
