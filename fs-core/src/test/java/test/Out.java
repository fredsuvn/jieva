package test;

public class Out {
    public static void println(Object... args) {
        for (Object arg : args) {
            System.out.print(arg);
        }
        System.out.println();
    }
}
